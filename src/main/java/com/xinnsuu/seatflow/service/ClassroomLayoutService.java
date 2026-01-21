package com.xinnsuu.seatflow.service;

import java.util.List;
import java.util.Optional;

import com.xinnsuu.seatflow.model.ClassroomLayout;

public interface ClassroomLayoutService {
    List<ClassroomLayout> getAllLayouts();
    Optional<ClassroomLayout> getLayoutById(Long id);
    ClassroomLayout createLayout(ClassroomLayout classroomLayout);
    ClassroomLayout updateLayout(Long id, ClassroomLayout updatedLayout);
    void deleteLayout(Long id);
    ClassroomLayout upsertPresetLayout(String presetId, String displayName, int rows, int columns);
}