package com.xinnsuu.seatflow.model;

import com.xinnsuu.seatflow.model.enums.AssignmentPresetType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentGenerateRequest {
    @NotNull(message = "Layout ID is required")
    private Long layoutId;

    @NotNull(message = "Preset type is required")
    private AssignmentPresetType presetType;

    @NotBlank(message = "Assignment name is required")
    private String assignmentName;
}
