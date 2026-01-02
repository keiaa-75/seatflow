package com.xinnsuu.seatflow.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeatAssignmentDetailDTO {

    private Long id;
    private String assignmentName;
    private String description;
    private LocalDateTime createdAt;

    private String studentId;
    private String studentName;

    private Long layoutId;
    private String layoutName;

    private int rowNumber;
    private int columnNumber;
}
