package com.xinnsuu.seatflow.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xinnsuu.seatflow.dto.LayoutPreset;
import com.xinnsuu.seatflow.model.ClassroomLayout;
import com.xinnsuu.seatflow.service.ClassroomLayoutService;

@Service
public class LayoutPresetLoader {
    private static final Logger logger = LoggerFactory.getLogger(LayoutPresetLoader.class);
    
    @Value("${seatflow.layouts.directory-path:./layouts}")
    private String layoutsDirectoryPath;
    
    @Autowired
    private ClassroomLayoutService classroomLayoutService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    public List<LayoutPreset> loadLayoutPresets() {
        List<LayoutPreset> presets = new ArrayList<>();
        Path layoutsDir = Paths.get(layoutsDirectoryPath);
        
        if (!Files.exists(layoutsDir)) {
            logger.warn("Layouts directory does not exist: {}", layoutsDirectoryPath);
            return presets;
        }
        
        try (Stream<Path> files = Files.list(layoutsDir)) {
            files.filter(path -> path.toString().endsWith(".json"))
                 .forEach(path -> {
                     try {
                         LayoutPreset preset = objectMapper.readValue(path.toFile(), LayoutPreset.class);
                         presets.add(preset);
                         logger.debug("Loaded layout preset from file: {}", path.getFileName());
                     } catch (IOException e) {
                         logger.error("Failed to parse layout preset file: {}", path, e);
                     }
                 });
        } catch (IOException e) {
            logger.error("Failed to list layout preset files in directory: {}", layoutsDirectoryPath, e);
        }
        
        logger.info("Loaded {} layout presets from directory: {}", presets.size(), layoutsDirectoryPath);
        return presets;
    }
    
    public void loadPresetsIntoDatabase() {
        List<LayoutPreset> presets = loadLayoutPresets();
        
        for (LayoutPreset preset : presets) {
            try {
                // Upsert preset using service
                classroomLayoutService.upsertPresetLayout(
                    preset.getName(), 
                    preset.getDisplayName(), 
                    preset.getRows(), 
                    preset.getColumns()
                );
                logger.debug("Upserted layout preset: {}", preset.getName());
            } catch (Exception e) {
                logger.error("Failed to load preset into database: {}", preset.getName(), e);
            }
        }
        
        logger.info("Successfully processed {} layout presets into database", presets.size());
    }
    
    
}