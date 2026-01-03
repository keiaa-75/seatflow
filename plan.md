## **Implementation Plan: jQuery + Thymeleaf SPA**

### **Phase 1: SPA Foundation**
Goal: Setup pushState routing and fragment support

1. **Create main layout template**
   - `templates/main.html` with SPA content area
   - Modify existing templates to use main layout
   - Update `home.html` and `about.html` for fragment support

2. **Implement jQuery SPA router**
   - Create `static/js/spa-router.js` with pushState routing
   - Handle link interception and browser navigation
   - Add loading states for route transitions

3. **Modify HomeWebController**
   - Add fragment detection: `@RequestParam String fragment`
   - Return `fragments/home-content :: content` for AJAX requests
   - Keep full page returns for direct navigation

4. **Update bottom navigation**
   - Convert navigation links to SPA routing
   - Maintain active state management
   - Handle browser history correctly

### **Phase 2: Section Management**
Goal: Complete section CRUD with SPA

1. **Modify AcademicStructureWebController**
   - Add fragment detection to all methods
   - Return fragments for AJAX: `fragments/sections :: content`
   - Return forms: `fragments/forms/section-form :: form`

2. **Create section templates**
   - `fragments/pages/sections-content.html` - Section list
   - `fragments/forms/section-form.html` - Add/edit form
   - Include grade level and strand dropdowns

3. **Implement AJAX form handling**
   - Client-side validation before submission
   - Handle success/error responses
   - Update list content without page reload

4. **Add confirmation dialogs**
   - Delete confirmations with modal
   - Form change warnings

### **Phase 3: Student Management**
Goal: Complete student CRUD with CSV import

1. **Modify StudentWebController**
   - Add fragment detection to all methods
   - Return fragments for list and forms
   - Handle CSV import via AJAX

2. **Create student templates**
   - `fragments/pages/students-content.html` - Student list with search
   - `fragments/forms/student-form.html` - Add/edit form
   - `fragments/forms/csv-import.html` - CSV upload form

3. **Implement student features**
   - Client-side search/filter
   - AJAX file upload with progress
   - Inline error handling

4. **Add validation and notifications**
   - Client-side form validation
   - Success/error notifications
   - CSV import progress feedback

### **Phase 4: Assignments & Layouts**
Goal: Seat assignment generation and layout management

1. **Modify SeatAssignmentWebController**
   - Add fragment support
   - Handle assignment generation via AJAX
   - Return assignment grid fragments

2. **Modify ClassroomLayoutWebController**
   - Add fragment support
   - Layout CRUD operations via AJAX

3. **Create assignment templates**
   - `fragments/pages/assignments-content.html` - Assignment list
   - `fragments/pages/assignment-grid.html` - Interactive seat grid
   - `fragments/forms/assignment-form.html` - Create/generate form
   - `fragments/forms/layout-form.html` - Layout form

4. **Implement assignment features**
   - Interactive seat grid with jQuery
   - Assignment generation dropdown (A-Z, Z-A, Random)
   - Print styling for assignments

### **Phase 5: UX Polish & Error Handling**
Goal: Complete user experience

1. **Global notification system**
   - Success/error toast notifications
   - Auto-dismiss after 5 seconds
   - Stack multiple notifications

2. **Enhanced loading states**
   - Spinner overlays for AJAX operations
   - Progress bars for file uploads
   - Skeleton screens for content loading

3. **Advanced error handling**
   - Client-side validation
   - Server-side error display
   - Network error recovery
   - Form state preservation

4. **Accessibility and browser support**
   - Keyboard navigation
   - Screen reader support
   - Fallback for JavaScript disabled

### **Phase 6: Testing & Optimization**
Goal: Production readiness

1. **End-to-end testing**
   - Test all CRUD operations
   - Test browser navigation
   - Test form validation
   - Test error scenarios

2. **Performance optimization**
   - Minimize DOM manipulations
   - Optimize AJAX calls
   - Cache frequently used data

3. **Final polish**
   - Smooth transitions
   - Responsive design verification
   - Cross-browser compatibility
