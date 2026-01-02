package com.xinnsuu.seatflow.service;

import java.util.List;

import com.xinnsuu.seatflow.model.SeatAssignment;
import com.xinnsuu.seatflow.model.enums.AssignmentPresetType;

public interface AssignmentPresetService {
    List<SeatAssignment> generateAssignments(Long sectionId, Long layoutId, AssignmentPresetType presetType, String assignmentName);
}
