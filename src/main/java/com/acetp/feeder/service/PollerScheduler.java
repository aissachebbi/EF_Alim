package com.acetp.feeder.service;

import com.acetp.feeder.config.FeederProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PollerScheduler {

    private final MessageFeederService messageFeederService;
    private final FeederProperties feederProperties;

    public PollerScheduler(MessageFeederService messageFeederService, FeederProperties feederProperties) {
        this.messageFeederService = messageFeederService;
        this.feederProperties = feederProperties;
    }

    @Scheduled(fixedDelayString = "#{@feederProperties.pollIntervalMs}")
    public void poll() {
        messageFeederService.executeOneRun();
    }

    public long getConfiguredPollIntervalMs() {
        return feederProperties.getPollIntervalMs();
    }
}
