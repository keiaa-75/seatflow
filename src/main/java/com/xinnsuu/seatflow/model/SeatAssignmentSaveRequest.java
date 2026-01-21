package com.xinnsuu.seatflow.model;

import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeatAssignmentSaveRequest {

    @NotBlank(message = "Assignment name is required")
    private String assignmentName;

    private String description;

    @NotBlank(message = "Layout type is required")
    private String layoutType;

    private Map<String, String> assignments;
}
