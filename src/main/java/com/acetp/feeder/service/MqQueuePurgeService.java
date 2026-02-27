package com.acetp.feeder.service;

import com.acetp.feeder.config.FeederProperties;
import jakarta.jms.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Profile("mqfeeder")
public class MqQueuePurgeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqQueuePurgeService.class);

    private final FeederProperties feederProperties;
    private final JmsTemplate jmsTemplate;

    public MqQueuePurgeService(FeederProperties feederProperties, JmsTemplate jmsTemplate) {
        this.feederProperties = feederProperties;
        this.jmsTemplate = jmsTemplate;
    }

    public Map<String, Object> purgeQueue() {
        String queueName = feederProperties.getMq().getQueueName();
        if (!StringUtils.hasText(queueName)) {
            throw new IllegalStateException("Impossible de purger: app.feeder.mq.queue-name est vide.");
        }

        LOGGER.info("Début purge MQ: queue={}, receiveTimeoutMs={}", queueName, 100L);

        long previousTimeout = jmsTemplate.getReceiveTimeout();
        jmsTemplate.setReceiveTimeout(100L);
        int purged = 0;

        try {
            while (true) {
                Message received = jmsTemplate.receive(queueName);
                if (received == null) {
                    break;
                }
                purged++;
            }
        } finally {
            jmsTemplate.setReceiveTimeout(previousTimeout);
        }

        LOGGER.info("Fin purge MQ: queue={}, messagesPurgés={}", queueName, purged);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("queue", queueName);
        result.put("purgedMessages", purged);
        return result;
    }
}
