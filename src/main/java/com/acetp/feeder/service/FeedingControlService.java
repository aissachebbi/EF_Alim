package com.acetp.feeder.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class FeedingControlService {

    private final AtomicBoolean paused = new AtomicBoolean(false);

    public void pauseFeeding() {
        paused.set(true);
    }

    public void resumeFeeding() {
        paused.set(false);
    }

    public boolean isPaused() {
        return paused.get();
    }
}
