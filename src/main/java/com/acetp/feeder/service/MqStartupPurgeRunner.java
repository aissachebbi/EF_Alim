package com.acetp.feeder.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Profile("mqpurge")
public class MqStartupPurgeRunner implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqStartupPurgeRunner.class);

    private final MqQueuePurgeService mqQueuePurgeService;
    private final ConfigurableApplicationContext applicationContext;

    public MqStartupPurgeRunner(
            MqQueuePurgeService mqQueuePurgeService,
            ConfigurableApplicationContext applicationContext
    ) {
        this.mqQueuePurgeService = mqQueuePurgeService;
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(ApplicationArguments args) {
        LOGGER.info("Profil mqpurge actif: purge MQ au démarrage en cours.");
        Map<String, Object> result = mqQueuePurgeService.purgeQueue();
        LOGGER.info("Purge MQ terminée (profil mqpurge): {}", result);
        LOGGER.info("Arrêt propre de l'application après purge (profil mqpurge).");
        applicationContext.close();
    }
}
