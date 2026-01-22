document.addEventListener('DOMContentLoaded', function() {
    const saveBtn = document.getElementById('save-mapping-btn');
    const nameModal = document.getElementById('name-modal');
    const successModal = document.getElementById('success-modal');
    const nameInput = document.getElementById('mapping-name-input');
    const confirmSaveBtn = document.getElementById('confirm-save-btn');
    const cancelSaveBtn = document.getElementById('cancel-save-btn');
    const modalClose = nameModal.querySelector('.modal-close');
    const successModalClose = successModal.querySelector('.modal-close');
    
    const seatGrid = document.querySelector('.seat-grid');
    const sectionId = seatGrid ? seatGrid.dataset.sectionId : null;
    
    function getCurrentAssignments() {
        const assignments = {};
        const seats = document.querySelectorAll('.seat.occupied');
        seats.forEach(seat => {
            const seatId = seat.dataset.seatId;
            const studentId = seat.dataset.studentId;
            if (seatId && studentId) {
                assignments[seatId] = studentId;
            }
        });
        return assignments;
    }
    
    function updateAssignedCount() {
        const count = document.querySelectorAll('.seat.occupied').length;
        const countElement = document.getElementById('assigned-count');
        if (countElement) {
            countElement.textContent = count;
        }
    }
    
    function openNameModal() {
        nameModal.classList.add('is-active');
        nameInput.value = '';
        nameInput.focus();
    }
    
    function closeNameModal() {
        nameModal.classList.remove('is-active');
    }
    
    function openSuccessModal(mappingId, mappingName) {
        const viewLink = document.getElementById('view-mapping-link');
        viewLink.href = '/sections/' + sectionId + '/class-mappings/' + mappingId;
        document.getElementById('saved-mapping-name').textContent = mappingName;
        successModal.classList.add('is-active');
    }
    
    function closeSuccessModal() {
        successModal.classList.remove('is-active');
    }
    
    async function saveMapping(name) {
        const assignments = getCurrentAssignments();
        const layoutType = seatGrid ? seatGrid.dataset.layoutType : null;
        
        try {
            const response = await fetch('/api/sections/' + sectionId + '/class-mappings', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    name: name,
                    layoutId: layoutType,
                    assignments: assignments
                })
            });
            
            if (!response.ok) {
                const error = await response.text();
                throw new Error(error || 'Failed to save');
            }
            
            const mapping = await response.json();
            closeNameModal();
            openSuccessModal(mapping.id, mapping.name);
            
        } catch (error) {
            console.error('Failed to save mapping:', error);
            alert('Failed to save seating chart: ' + error.message);
        }
    }
    
    if (saveBtn) {
        saveBtn.addEventListener('click', function(e) {
            e.preventDefault();
            openNameModal();
        });
    }
    
    if (confirmSaveBtn) {
        confirmSaveBtn.addEventListener('click', function() {
            const name = nameInput.value.trim();
            if (!name) {
                nameInput.classList.add('is-danger');
                return;
            }
            nameInput.classList.remove('is-danger');
            saveMapping(name);
        });
    }
    
    if (cancelSaveBtn) {
        cancelSaveBtn.addEventListener('click', closeNameModal);
    }
    
    if (modalClose) {
        modalClose.addEventListener('click', closeNameModal);
    }
    
    if (successModalClose) {
        successModalClose.addEventListener('click', closeSuccessModal);
    }
    
    document.querySelectorAll('.modal-background').forEach(bg => {
        bg.addEventListener('click', function() {
            closeNameModal();
            closeSuccessModal();
        });
    });
    
    nameInput.addEventListener('keydown', function(e) {
        if (e.key === 'Enter') {
            confirmSaveBtn.click();
        }
        if (e.key === 'Escape') {
            closeNameModal();
        }
    });
    
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape' && successModal.classList.contains('is-active')) {
            closeSuccessModal();
        }
    });
    
    updateAssignedCount();
});
