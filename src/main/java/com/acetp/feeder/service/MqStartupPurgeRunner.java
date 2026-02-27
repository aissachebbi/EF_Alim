package com.acetp.feeder.service;

import com.acetp.feeder.config.FeederProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Profile("mqfeeder")
public class MqStartupPurgeRunner implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqStartupPurgeRunner.class);

    private final FeederProperties feederProperties;
    private final MqQueuePurgeService mqQueuePurgeService;
    private final ConfigurableApplicationContext applicationContext;

    public MqStartupPurgeRunner(
            FeederProperties feederProperties,
            MqQueuePurgeService mqQueuePurgeService,
            ConfigurableApplicationContext applicationContext
    ) {
        this.feederProperties = feederProperties;
        this.mqQueuePurgeService = mqQueuePurgeService;
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!feederProperties.getMq().isPurgeOnStartupEnabled()) {
            LOGGER.info("Purge MQ au démarrage désactivée (app.feeder.mq.purge-on-startup-enabled=false).");
            return;
        }

        LOGGER.info("Purge MQ au démarrage activée. Déclenchement de mqQueuePurge.purge().");
        Map<String, Object> result = mqQueuePurgeService.purgeQueue();
        LOGGER.info("Purge MQ au démarrage terminée: {}", result);

        if (feederProperties.getMaxMessagesPerRun() == 0) {
            LOGGER.info("Mode purge-only détecté (max-messages-per-run=0). Arrêt propre de l'application après purge.");
            applicationContext.close();
        }
    }
}
