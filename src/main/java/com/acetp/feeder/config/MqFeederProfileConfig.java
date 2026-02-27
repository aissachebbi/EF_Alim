package com.acetp.feeder.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("mqfeeder")
public class MqFeederProfileConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqFeederProfileConfig.class);

    private final FeederProperties feederProperties;

    public MqFeederProfileConfig(FeederProperties feederProperties) {
        this.feederProperties = feederProperties;
    }

    @PostConstruct
    void forceMqMode() {
        feederProperties.getMq().setEnabled(true);
        LOGGER.info("Profil 'mqfeeder' actif: app.feeder.mq.enabled forcé à true.");
    }
}
