package com.acetp.feeder.service;

import com.acetp.feeder.config.FeederProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Profile("mqfeeder | mqpurge")
public class MqMessagePublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqMessagePublisher.class);

    private final FeederProperties feederProperties;
    private final JmsTemplate jmsTemplate;

    public MqMessagePublisher(FeederProperties feederProperties, JmsTemplate jmsTemplate) {
        this.feederProperties = feederProperties;
        this.jmsTemplate = jmsTemplate;
    }

    public void publish(String payload) {
        String queueName = feederProperties.getMq().getQueueName();
        if (!StringUtils.hasText(queueName)) {
            throw new IllegalStateException("app.feeder.mq.queue-name est obligatoire en mode MQ.");
        }

        jmsTemplate.convertAndSend(queueName, payload);
        LOGGER.debug("Message publié vers MQ queue={}", queueName);
    }
}
