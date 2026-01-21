# Layout Presets Directory

This directory contains JSON files defining layout presets for the seat assignment system.

## File Format

Each JSON file must contain:
- `name`: Unique identifier for the layout (must match filename without .json)
- `displayName`: Human-readable name shown in UI
- `description`: Brief description of the layout
- `rows`: Number of rows in the seat grid
- `columns`: Number of columns in the seat grid
- `disabledSeats`: Array of seat positions that are disabled (format: "row-column")

## Example

```json
{
  "name": "NORMAL",
  "displayName": "Normal",
  "description": "Standard rectangular layout",
  "rows": 10,
  "columns": 10,
  "disabledSeats": []
}
```

## Notes

- Files must have .json extension
- Seat positions are zero-indexed (top-left is "0-0")
- The `name` field should match the filename (without .json extension)
- Invalid files will be skipped and logged