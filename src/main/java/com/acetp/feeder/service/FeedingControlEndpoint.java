package com.acetp.feeder.service;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Endpoint(id = "feedingControl")
public class FeedingControlEndpoint {

    private final FeedingControlService feedingControlService;

    public FeedingControlEndpoint(FeedingControlService feedingControlService) {
        this.feedingControlService = feedingControlService;
    }

    @ReadOperation
    public Map<String, Object> status() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("feedingPaused", feedingControlService.isPaused());
        return response;
    }

    @WriteOperation
    public Map<String, Object> stopFeeding() {
        feedingControlService.pauseFeeding();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("feedingPaused", true);
        response.put("message", "Feeding mis en pause.");
        return response;
    }

    @WriteOperation
    public Map<String, Object> resumeFeeding() {
        feedingControlService.resumeFeeding();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("feedingPaused", false);
        response.put("message", "Feeding repris.");
        return response;
    }
}
