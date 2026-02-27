package com.acetp.feeder.config;

import com.acetp.feeder.service.MqMessagePublisher;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class MqModeValidationConfig {

    private final FeederProperties feederProperties;
    private final ObjectProvider<MqMessagePublisher> mqMessagePublisher;
    private final Environment environment;

    public MqModeValidationConfig(
            FeederProperties feederProperties,
            ObjectProvider<MqMessagePublisher> mqMessagePublisher,
            Environment environment
    ) {
        this.feederProperties = feederProperties;
        this.mqMessagePublisher = mqMessagePublisher;
        this.environment = environment;
    }

    @PostConstruct
    void validateMqMode() {
        if (!feederProperties.getMq().isEnabled()) {
            return;
        }

        if (mqMessagePublisher.getIfAvailable() != null) {
            return;
        }

        throw new IllegalStateException(
                "Mode MQ actif mais MqMessagePublisher indisponible. "
                        + "Activez le profil 'mqfeeder' (ou 'mqpurge') et vérifiez la configuration ibm.mq.*.");
    }
}
