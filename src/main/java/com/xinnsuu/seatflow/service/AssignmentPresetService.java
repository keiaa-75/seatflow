package com.xinnsuu.seatflow.service;

import java.util.Map;

import com.xinnsuu.seatflow.model.enums.AssignmentPresetType;

public interface AssignmentPresetService {
    Map<String, String> generateAssignments(Long sectionId, Long layoutId, AssignmentPresetType presetType);
}
