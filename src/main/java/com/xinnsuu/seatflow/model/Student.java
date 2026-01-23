package com.xinnsuu.seatflow.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
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
	@OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
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
    
    public String getDisplayName() {
        StringBuilder displayName = new StringBuilder();
        
        // Last Name Suffix
        if (lastName != null && !lastName.trim().isEmpty()) {
            displayName.append(lastName.trim());
            if (suffix != null && !suffix.trim().isEmpty()) {
                displayName.append(" ").append(suffix.trim());
            }
        }
        
        // First Name
        if (firstName != null && !firstName.trim().isEmpty()) {
            if (displayName.length() > 0) {
                displayName.append(", ");
            }
            displayName.append(firstName.trim());
        }
        
        // Middle Initial
        if (middleName != null && !middleName.trim().isEmpty()) {
            displayName.append(" ").append(middleName.trim().charAt(0)).append(".");
        }
        
        return displayName.toString().trim();
    }
}