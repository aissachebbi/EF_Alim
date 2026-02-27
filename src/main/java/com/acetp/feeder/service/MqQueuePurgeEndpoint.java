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

    public MqQueuePurgeEndpoint(MqQueuePurgeService mqQueuePurgeService) {
        this.mqQueuePurgeService = mqQueuePurgeService;
    }

    @WriteOperation
    public Map<String, Object> purge() {
        return mqQueuePurgeService.purgeQueue();
    }
}
