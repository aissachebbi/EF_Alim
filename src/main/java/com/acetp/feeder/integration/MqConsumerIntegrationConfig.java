package com.acetp.feeder.integration;

import com.acetp.feeder.config.FeederProperties;
import com.ibm.mq.jakarta.jms.MQConnectionFactory;
import com.ibm.msg.client.jakarta.wmq.WMQConstants;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;
import java.util.List;

/**
 * Robust Spring Integration flow for MQ consumption.
 * This configuration manually creates the MQConnectionFactory to avoid circular dependencies
 * and ensure proper authentication (MQRC_NOT_AUTHORIZED 2035).
 */
@Configuration
@Profile("mqconsumer")
public class MqConsumerIntegrationConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqConsumerIntegrationConfig.class);

    private final FeederProperties feederProperties;

    public MqConsumerIntegrationConfig(FeederProperties feederProperties) {
        this.feederProperties = feederProperties;
    }

    /**
     * Manual creation of the IBM MQ Connection Factory.
     * This avoids conflicts with Spring Boot's auto-configuration and circular dependencies.
     */
    @Bean(name = "mqTargetConnectionFactory")
    public ConnectionFactory mqTargetConnectionFactory() throws JMSException {
        MQConnectionFactory factory = new MQConnectionFactory();
        
        // Host and Port (e.g., "localhost(1414)")
        String connName = System.getenv().getOrDefault("IBM_MQ_CONN_NAME", "localhost(1414)");
        if (connName.contains("(") && connName.contains(")")) {
            String host = connName.substring(0, connName.indexOf("("));
            int port = Integer.parseInt(connName.substring(connName.indexOf("(") + 1, connName.indexOf(")")));
            factory.setHostName(host);
            factory.setPort(port);
        } else {
            factory.setHostName(connName);
            factory.setPort(1414);
        }

        factory.setQueueManager(System.getenv().getOrDefault("IBM_MQ_QUEUE_MANAGER", "QM1"));
        factory.setChannel(System.getenv().getOrDefault("IBM_MQ_CHANNEL", "CLIATP01.FRATP01T.T1"));
        
        // Transport type (Client = 1)
        factory.setTransportType(WMQConstants.WMQ_CM_CLIENT);
        
        // CCSID (default 819)
        int ccsid = Integer.parseInt(System.getenv().getOrDefault("IBM_MQ_CCSID", "819"));
        factory.setCCSID(ccsid);

        return factory;
    }

    /**
     * Adapter to force User/Password authentication.
     */
    @Bean
    public UserCredentialsConnectionFactoryAdapter connectionFactoryAdapter(ConnectionFactory mqTargetConnectionFactory) {
        UserCredentialsConnectionFactoryAdapter adapter = new UserCredentialsConnectionFactoryAdapter();
        adapter.setTargetConnectionFactory(mqTargetConnectionFactory);
        adapter.setUsername(System.getenv().getOrDefault("IBM_MQ_USER", "app"));
        adapter.setPassword(System.getenv().getOrDefault("IBM_MQ_PASSWORD", "QM1"));
        return adapter;
    }

    @Bean
    public JmsTransactionManager jmsTransactionManager(UserCredentialsConnectionFactoryAdapter connectionFactoryAdapter) {
        return new JmsTransactionManager(connectionFactoryAdapter);
    }

    @Bean
    public IntegrationFlow mqInboundFlow(
            UserCredentialsConnectionFactoryAdapter connectionFactoryAdapter,
            JmsTransactionManager jmsTransactionManager) {
        
        String queueName = feederProperties.getMq().getQueueName();

        return IntegrationFlow.from(Jms.messageDrivenChannelAdapter(connectionFactoryAdapter)
                        .destination(queueName)
                        .headerMapper(new org.springframework.integration.jms.DefaultJmsHeaderMapper()) // Mappe JMS -> Spring Message Headers
                        .extractPayload(true) // Conserve l'extraction automatique du payload mais les headers sont dans le Message enveloppe
                        .configureListenerContainer(c -> c
                                .transactionManager(jmsTransactionManager)
                                .sessionTransacted(true)
                                // GARANTIE D'ORDRE 1 : Un seul consommateur concurrent pour lire la file séquentiellement
                                .concurrentConsumers(1)))

                // --- PHASE D'AGRÉGATION ---
                // On utilise un processeur de groupe personnalisé pour agréger les OBJETS MESSAGE
                // entiers (Payload + Headers) dans une liste ordonnée.
                .aggregate(a -> a
                        .correlationStrategy(m -> "MQ_BATCH_GROUP")
                        .releaseStrategy(g -> g.size() >= 500)
                        .groupTimeout(200)
                        // GARANTIE D'ORDRE 2 : L'ordre d'arrivée dans le groupe est préservé.
                        // On renvoie une List (ordonnée) au lieu d'une Collection générique.
                        .outputProcessor(group -> new java.util.ArrayList<>(group.getMessages()))
                        .sendPartialResultOnExpiry(true)
                        .expireGroupsUponCompletion(true))

                // Le handler reçoit maintenant une List de messages ordonnés.
                .<java.util.List<org.springframework.messaging.Message<?>>>handle((batch, headers) -> {
                    LOGGER.info("📦 Lot de {} message(s) reçu dans l'ordre d'arrivée MQ.", batch.size());
                    
                    for (int i = 0; i < batch.size(); i++) {
                        org.springframework.messaging.Message<?> msg = batch.get(i);
                        String msgId = msg.getHeaders().get("jms_messageId", String.class);
                        Long timestamp = msg.getHeaders().get("jms_timestamp", Long.class);
                        
                        LOGGER.info("  ↳ Message [{}/{}] :", i + 1, batch.size());
                        LOGGER.info("    | ID MQ         : {}", msgId);
                        LOGGER.info("    | Horodatage JMS: {}", timestamp);
                        LOGGER.info("    | Payload       : {}", msg.getPayload());
                    }
                    return null;
                })
                .get();
    }
}