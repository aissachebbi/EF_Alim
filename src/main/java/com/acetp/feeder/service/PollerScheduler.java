package com.acetp.feeder.service;

import com.acetp.feeder.config.FeederProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("!mqconsumer")
public class PollerScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(PollerScheduler.class);

    private final MessageFeederService messageFeederService;
    private final FeederProperties feederProperties;
    private final FeedingControlService feedingControlService;
    private boolean pausedMessageLogged = false;

    public PollerScheduler(
            MessageFeederService messageFeederService,
            FeederProperties feederProperties,
            FeedingControlService feedingControlService
    ) {
        this.messageFeederService = messageFeederService;
        this.feederProperties = feederProperties;
        this.feedingControlService = feedingControlService;
    }

    @Scheduled(fixedDelayString = "#{@feederProperties.pollIntervalMs}")
    public void poll() {
        if (feedingControlService.isPaused()) {
            if (!pausedMessageLogged) {
                LOGGER.info("Poll ignoré: feeding en pause (reprendre via endpoint feedingControl.resumeFeeding).");
                pausedMessageLogged = true;
            }
            return;
        }
        if (pausedMessageLogged) {
            pausedMessageLogged = false;
        }
        messageFeederService.executeOneRun();
    }

    public long getConfiguredPollIntervalMs() {
        return feederProperties.getPollIntervalMs();
    }
}
