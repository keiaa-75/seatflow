document.addEventListener('DOMContentLoaded', function() {
    const deleteBtn = document.getElementById('delete-mapping-btn');
    const deleteModal = document.getElementById('delete-modal-' + mappingId);
    const confirmDeleteBtn = deleteModal.querySelector('.confirm-delete-btn');
    const cancelDeleteBtn = deleteModal.querySelector('.cancel-delete-btn');
    const modalClose = deleteModal.querySelector('.modal-close');
    
    function openModal() {
        deleteModal.classList.add('is-active');
        document.documentElement.classList.add('is-modal-active');
    }
    
    function closeModal() {
        deleteModal.classList.remove('is-active');
        document.documentElement.classList.remove('is-modal-active');
    }
    
    async function deleteMapping() {
        try {
            const response = await fetch('/api/sections/' + sectionId + '/class-mappings/' + mappingId, {
                method: 'DELETE'
            });
            
            if (!response.ok) {
                throw new Error('Failed to delete mapping');
            }
            
            closeModal();
            window.location.href = '/sections/' + sectionId + '/class-mappings/list';
            
        } catch (error) {
            console.error('Failed to delete mapping:', error);
            alert('Failed to delete seating chart: ' + error.message);
        }
    }
    
    if (deleteBtn) {
        deleteBtn.addEventListener('click', openModal);
    }
    
    if (confirmDeleteBtn) {
        confirmDeleteBtn.addEventListener('click', deleteMapping);
    }
    
    if (cancelDeleteBtn) {
        cancelDeleteBtn.addEventListener('click', closeModal);
    }
    
    if (modalClose) {
        modalClose.addEventListener('click', closeModal);
    }
    
    document.querySelectorAll('.modal-background').forEach(bg => {
        bg.addEventListener('click', closeModal);
    });
    
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape' && deleteModal.classList.contains('is-active')) {
            closeModal();
        }
    });
});
