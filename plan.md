## **Implementation Plan**

### **Phase 1: Backend Foundation** 
Goal: Update models and core endpoints

1. Update SeatAssignment model
   - Add assignmentName field
   - Add description field (optional)
   - Add createdAt timestamp

2. Update SeatAssignmentController
   - Modify POST/PUT to accept new fields
   - Keep existing endpoints working

3. Add assignment detail DTO
   - Create SeatAssignmentDetailDTO with student name, layout info
   - Add endpoint: GET /api/sections/{sectionId}/assignments/details

4. Test with existing data
   - Run migrations
   - Verify API responses


### **Phase 2: Assignment Presets (Backend)**
Goal: Auto-assignment logic

1. Add preset service
   - Create AssignmentPresetService
   - Implement: alphabetical (A-Z), reverse (Z-A), random

2. Add preset endpoint
   - POST /api/sections/{sectionId}/assignments/generate
   - Body: { layoutId, presetType, assignmentName }
   - Returns: created assignments


### **Phase 3: Frontend Core Structure**
Goal: Basic navigation and section selection

1. Setup frontend framework (assuming you're using one?)
   - Configure routing
   - Setup API client/service layer

2. Implement wireframes 001-002
   - Section selection screen
   - Section action menu ("What to do")
   - Navigation between Home/Manage/About

3. API integration
   - Fetch sections: GET /api/sections
   - Display with grade level grouping



### **Phase 4: Seat Assignment Flow**
Goal: Create and view assignments

1. Implement wireframes 003-004
   - Layout selection screen
   - Seat assignment grid (interactive)
   - Manual seat clicking/assignment

2. Implement wireframe 006
   - Assignment list view
   - View assignment details

3. Connect to preset API
   - Dropdown for A-Z, Z-A, Random
   - Call generate endpoint

4. Add print styling
   - CSS @media print for clean output


### **Phase 5: Student Management**
Goal: CRUD for students

1. Implement wireframe 005
   - Student list with search
   - Add/Edit/Delete student forms
   - Client-side name filtering

2. Implement wireframe 007
   - CSV import UI
   - File upload with validation
   - Processing states and error handling


### **Phase 6: Section Management**
Goal: Section CRUD

1. Implement wireframe 008
   - Section edit form
   - Grade level and strand dropdowns
   - Save/Delete actions

2. Add section creation
   - "New Section" button from wireframe 001
   - Same form as edit


### **Phase 7: Polish & Testing**
Goal: UX refinements

1. Confirmation dialogs
   - Delete confirmations
   - Reset confirmations

2. Success/Error toasts
   - Task successful messages
   - Error handling

3. Loading states
   - Spinners for async operations

4. End-to-end testing
   - Test full workflows
   - Fix bugs
