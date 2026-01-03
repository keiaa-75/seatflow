## Navigation Flowchart Analysis

### Key Pages Identified

| Page | Purpose |
|------|---------|
| **Home** | Landing page with quick actions |
| **Manage** | Bottom nav → Section selection |
| **About** | Information page |

### Section Management Flow

```
Manage section page
├── New section
├── Edit section details → Save / Delete
└── Select student → (proceeds to student page)
```

### Student Management Flow

```
Select a section → Manage student page
├── New student
├── Edit student details → Save / Delete
└── Import student entry
    ├── Upload CSV
    ├── Server-side validation
    │   ├── Valid → Import
    │   └── Invalid → warning modal
    └── Confirm modal (delete/discard actions)
```

### Assignment Management Flow

```
Select assignment → New assignment page / Manage assignment page
├── Select layout / New layout
├── Assign seats
├── View assignment
├── Edit
├── Print → Print dialog
└── Delete → Confirm modal
```

### What This Means for Phase 2

Section management needs:
1. **Manage section page** - list view with CRUD actions
2. **New section** action
3. **Edit section details** form with Save
4. **Delete** with confirmation modal