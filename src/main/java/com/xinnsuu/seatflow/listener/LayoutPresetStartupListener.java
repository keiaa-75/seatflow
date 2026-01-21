package com.xinnsuu.seatflow.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.xinnsuu.seatflow.service.LayoutPresetLoader;

@Component
public class LayoutPresetStartupListener {
    private static final Logger logger = LoggerFactory.getLogger(LayoutPresetStartupListener.class);
    
    @Autowired
    private LayoutPresetLoader layoutPresetLoader;
    
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        logger.info("Loading layout presets from JSON files...");
        layoutPresetLoader.loadPresetsIntoDatabase();
        logger.info("Layout preset loading completed");
    }
}