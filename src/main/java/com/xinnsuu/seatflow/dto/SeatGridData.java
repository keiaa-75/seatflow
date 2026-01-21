package com.xinnsuu.seatflow.dto;

import java.util.List;
import java.util.Map;

import com.xinnsuu.seatflow.model.Student;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatGridData {
    private Long sectionId;
    private Long layoutId;
    private String layoutType;
    private int rows;
    private int columns;
    private List<String> disabledSeats;
    private List<Student> students;
    private Map<String, SeatData> seatAssignments;
    private List<SeatInfo> allSeats;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeatData {
        private String studentId;
        private String studentName;
        private String initials;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeatInfo {
        private String id;
        private int row;
        private int col;
        private String status;
        private String studentId;
        private String studentName;
        private String initials;
    }
}
