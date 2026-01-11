package com.xinnsuu.seatflow.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "seatAssignments")
public class Student {

	@Id
	@NotBlank(message = "Student ID is required")
	@Size(min = 12, max = 12, message = "Student ID must be exactly 12 characters")
	private String studentId;

	@NotBlank(message = "First name is required")
	@Size(max = 150)
	private String firstName;

	@Size(max = 150)
	private String middleName;

	@NotBlank(message = "Last name is required")
	@Size(max = 150)
	private String lastName;

	@Size(max = 10)
	private String suffix;

	@JsonIgnore
	@OneToMany(mappedBy = "student")
	private Set<SeatAssignment> seatAssignments;

    @ManyToOne
    @JoinColumn(name = "section_id")
    private AcademicStructure academicStructure;
    
    public String getFullName() {
        StringBuilder fullName = new StringBuilder();
        if (firstName != null && !firstName.trim().isEmpty()) {
            fullName.append(firstName.trim());
        }
        if (middleName != null && !middleName.trim().isEmpty()) {
            fullName.append(" ").append(middleName.trim());
        }
        if (lastName != null && !lastName.trim().isEmpty()) {
            fullName.append(" ").append(lastName.trim());
        }
        if (suffix != null && !suffix.trim().isEmpty()) {
            fullName.append(" ").append(suffix.trim());
        }
        return fullName.toString().trim();
    }
}