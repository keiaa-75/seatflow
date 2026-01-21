package com.xinnsuu.seatflow.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "classroom_layouts")
@Data
public class ClassroomLayout {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Layout name is required")
	@Size(max = 100)
	private String name;

	@NotNull(message = "Layout type is required")
	@Enumerated(EnumType.STRING)
	private LayoutType layoutType;

	@NotNull(message = "Number of rows is required")
	@Min(value = 1, message = "Layout must have at least 1 row")
	private int rows;

	@NotNull(message = "Number of columns is required")
	@Min(value = 1, message = "Layout must have at least 1 column")
	private int columns;
	
	// Store preset identifier for JSON-loaded layouts
	private String presetId;

	public enum LayoutType {
		NORMAL("Normal", "Ten by ten", 10, 10),
		SMALL("Small", "Five by five", 5, 5),
		ROWS("Rows", "Traditional rows", 8, 6),
		U_SHAPE("U-Shape", "U formation", 8, 6),
		GROUPS("Groups", "Group tables", 6, 6);

		private final String displayName;
		private final String description;
		private final int defaultRows;
		private final int defaultColumns;

		LayoutType(String displayName, String description, int defaultRows, int defaultColumns) {
			this.displayName = displayName;
			this.description = description;
			this.defaultRows = defaultRows;
			this.defaultColumns = defaultColumns;
		}

		public String getDisplayName() { return displayName; }
		public String getDescription() { return description; }
		public int getDefaultRows() { return defaultRows; }
		public int getDefaultColumns() { return defaultColumns; }
	}
}