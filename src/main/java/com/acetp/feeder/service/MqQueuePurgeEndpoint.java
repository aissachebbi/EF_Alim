package com.acetp.feeder.service;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Profile("mqfeeder")
@Endpoint(id = "mqQueuePurge")
public class MqQueuePurgeEndpoint {

    private final MqQueuePurgeService mqQueuePurgeService;
    private final FeedingControlService feedingControlService;

    public MqQueuePurgeEndpoint(
            MqQueuePurgeService mqQueuePurgeService,
            FeedingControlService feedingControlService
    ) {
        this.mqQueuePurgeService = mqQueuePurgeService;
        this.feedingControlService = feedingControlService;
    }

    @WriteOperation
    public Map<String, Object> purge() {
        Map<String, Object> result = mqQueuePurgeService.purgeQueue();
        feedingControlService.pauseFeeding();
        result.put("feedingPaused", true);
        result.put("resumeOperation", "feedingControl.resumeFeeding");
        return result;
    }
}
