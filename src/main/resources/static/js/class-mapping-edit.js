document.addEventListener('DOMContentLoaded', function() {
    const cancelBtn = document.getElementById('cancel-mapping-btn');
    const cancelModal = document.getElementById('cancel-modal');
    const confirmCancelBtn = document.getElementById('confirm-cancel-btn');
    const keepMappingBtn = document.getElementById('keep-mapping-btn');
    const modalClose = cancelModal.querySelector('.modal-close');
    
    function openCancelModal() {
        cancelModal.classList.add('is-active');
        document.documentElement.classList.add('is-modal-active');
    }
    
    function closeCancelModal() {
        cancelModal.classList.remove('is-active');
        document.documentElement.classList.remove('is-modal-active');
    }
    
    async function discardMapping() {
        try {
            const response = await fetch('/api/sections/' + sectionId + '/class-mappings/' + mappingId, {
                method: 'DELETE'
            });
            
            if (!response.ok) {
                throw new Error('Failed to delete mapping');
            }
            
            closeCancelModal();
            window.location.href = '/sections/' + sectionId + '/class-mappings/list';
            
        } catch (error) {
            console.error('Failed to delete mapping:', error);
            alert('Failed to discard seating chart: ' + error.message);
        }
    }
    
    if (cancelBtn) {
        cancelBtn.addEventListener('click', openCancelModal);
    }
    
    if (confirmCancelBtn) {
        confirmCancelBtn.addEventListener('click', discardMapping);
    }
    
    if (keepMappingBtn) {
        keepMappingBtn.addEventListener('click', closeCancelModal);
    }
    
    if (modalClose) {
        modalClose.addEventListener('click', closeCancelModal);
    }
    
    document.querySelectorAll('.modal-background').forEach(bg => {
        bg.addEventListener('click', closeCancelModal);
    });
    
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape' && cancelModal.classList.contains('is-active')) {
            closeCancelModal();
        }
    });
});
