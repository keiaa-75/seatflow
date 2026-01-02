package com.xinnsuu.seatflow.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "seat_assignments", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"layout_id", "rowNumber", "columnNumber"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"student", "academicStructure", "classroomLayout"})
public class SeatAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "section_id")
    private AcademicStructure academicStructure;

    @ManyToOne
    @JoinColumn(name = "layout_id")
    private ClassroomLayout classroomLayout;

    @NotBlank(message = "Assignment name is required")
    @Column(nullable = false)
    private String assignmentName;

    private String description;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @NotNull(message = "Row number is required")
    @Min(value = 1, message = "Row number must be 1 or greater")
    private int rowNumber;

    @NotNull(message = "Column number is required")
    @Min(value = 1, message = "Column number must be 1 or greater")
    private int columnNumber;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}