package com.xinnsuu.seatflow.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xinnsuu.seatflow.model.ClassroomLayout;
import com.xinnsuu.seatflow.repository.ClassroomLayoutRepository;

@Service
public class ClassroomLayoutServiceImpl implements ClassroomLayoutService {

    @Autowired
    private ClassroomLayoutRepository classroomLayoutRepository;

    @Override
    public List<ClassroomLayout> getAllLayouts() {
        return classroomLayoutRepository.findAll();
    }

    @Override
    public Optional<ClassroomLayout> getLayoutById(Long id) {
        return classroomLayoutRepository.findById(id);
    }

    @Override
    public ClassroomLayout createLayout(ClassroomLayout classroomLayout) {
        return classroomLayoutRepository.save(classroomLayout);
    }

    @Override
    public ClassroomLayout updateLayout(Long id, ClassroomLayout updatedLayout) {
        Optional<ClassroomLayout> existingLayoutOpt = classroomLayoutRepository.findById(id);

        if (existingLayoutOpt.isPresent()) {
            ClassroomLayout existingLayout = existingLayoutOpt.get();
            
            // Don't update presetId for existing layouts (protect presets)
            existingLayout.setName(updatedLayout.getName());
            existingLayout.setRows(updatedLayout.getRows());
            existingLayout.setColumns(updatedLayout.getColumns());
            
            return classroomLayoutRepository.save(existingLayout);
        } else {
            throw new RuntimeException("Classroom Layout with ID " + id + " not found");
        }
    }
    
    public ClassroomLayout upsertPresetLayout(String presetId, String displayName, int rows, int columns) {
        Optional<ClassroomLayout> existingLayout = classroomLayoutRepository.findByPresetId(presetId);
        
        ClassroomLayout layout;
        if (existingLayout.isPresent()) {
            layout = existingLayout.get();
        } else {
            layout = new ClassroomLayout();
            layout.setPresetId(presetId);
        }
        
        layout.setName(displayName);
        layout.setRows(rows);
        layout.setColumns(columns);
        layout.setLayoutType(com.xinnsuu.seatflow.model.ClassroomLayout.LayoutType.NORMAL);
        
        return classroomLayoutRepository.save(layout);
    }

    @Override
    public void deleteLayout(Long id) {
        if (!classroomLayoutRepository.existsById(id)) {
            throw new RuntimeException("Classroom Layout with ID " + id + " not found");
        }
        classroomLayoutRepository.deleteById(id);
    }
}