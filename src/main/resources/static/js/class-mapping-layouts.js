document.addEventListener('DOMContentLoaded', function() {
    const nameModal = document.getElementById('name-modal');
    const loadingModal = document.getElementById('loading-modal');
    const nameInput = document.getElementById('mapping-name-input');
    const confirmCreateBtn = document.getElementById('confirm-create-btn');
    const cancelCreateBtn = document.getElementById('cancel-create-btn');
    const modalClose = nameModal.querySelector('.modal-close');
    
    const layoutBtns = document.querySelectorAll('.layout-select-btn');
    
    function openNameModal(layoutId, layoutName) {
        selectedLayoutId = layoutId;
        selectedLayoutName = layoutName;
        nameModal.classList.add('is-active');
        nameInput.value = '';
        nameInput.focus();
    }
    
    function closeNameModal() {
        nameModal.classList.remove('is-active');
        selectedLayoutId = null;
        selectedLayoutName = null;
    }
    
    function showLoading() {
        loadingModal.classList.add('is-active');
    }
    
    function hideLoading() {
        loadingModal.classList.remove('is-active');
    }
    
    async function createMapping() {
        const name = nameInput.value.trim();
        if (!name) {
            nameInput.classList.add('is-danger');
            return;
        }
        nameInput.classList.remove('is-danger');
        
        showLoading();
        
        try {
            const response = await fetch('/api/sections/' + sectionId + '/class-mappings/init', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    name: name,
                    layoutId: selectedLayoutId
                })
            });
            
            if (!response.ok) {
                const error = await response.text();
                throw new Error(error || 'Failed to create mapping');
            }
            
            const result = await response.json();
            closeNameModal();
            hideLoading();
            
            // Redirect to the grid page with the new mapping ID
            window.location.href = '/sections/' + sectionId + '/class-mappings/' + result.mappingId + '/edit';
            
        } catch (error) {
            console.error('Failed to create mapping:', error);
            hideLoading();
            alert('Failed to create seating chart: ' + error.message);
        }
    }
    
    layoutBtns.forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            openNameModal(this.dataset.layoutId, this.dataset.layoutName);
        });
    });
    
    if (confirmCreateBtn) {
        confirmCreateBtn.addEventListener('click', createMapping);
    }
    
    if (cancelCreateBtn) {
        cancelCreateBtn.addEventListener('click', closeNameModal);
    }
    
    if (modalClose) {
        modalClose.addEventListener('click', closeNameModal);
    }
    
    document.querySelectorAll('.modal-background').forEach(bg => {
        bg.addEventListener('click', closeNameModal);
    });
    
    nameInput.addEventListener('keydown', function(e) {
        if (e.key === 'Enter') {
            createMapping();
        }
        if (e.key === 'Escape') {
            closeNameModal();
        }
    });
});
