package com.acetp.feeder.service;

import com.acetp.feeder.config.FeederProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PollerScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(PollerScheduler.class);

    private final MessageFeederService messageFeederService;
    private final FeederProperties feederProperties;

    public PollerScheduler(MessageFeederService messageFeederService, FeederProperties feederProperties) {
        this.messageFeederService = messageFeederService;
        this.feederProperties = feederProperties;
    }

    @Scheduled(fixedDelayString = "#{@feederProperties.pollIntervalMs}")
    public void poll() {
        if (feederProperties.getMaxMessagesPerRun() <= 0) {
            LOGGER.info("Poll ignoré: app.feeder.max-messages-per-run <= 0 (mode purge-only)." );
            return;
        }
        messageFeederService.executeOneRun();
    }

    public long getConfiguredPollIntervalMs() {
        return feederProperties.getPollIntervalMs();
    }
}
