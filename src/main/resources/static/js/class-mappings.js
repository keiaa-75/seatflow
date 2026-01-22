document.addEventListener('DOMContentLoaded', function() {
    const mappingsList = document.getElementById('mappings-list');
    const emptyState = document.getElementById('empty-state');
    const loadingState = document.getElementById('loading-state');
    const deleteModal = document.getElementById('delete-modal');
    const confirmDeleteBtn = document.getElementById('confirm-delete-btn');
    const cancelDeleteBtn = document.getElementById('cancel-delete-btn');
    const modalClose = deleteModal.querySelector('.modal-close');
    
    let mappingsToDelete = null;
    
    async function loadMappings() {
        try {
            const response = await fetch('/api/sections/' + sectionId + '/class-mappings');
            if (!response.ok) {
                throw new Error('Failed to load mappings');
            }
            const mappings = await response.json();
            renderMappings(mappings);
        } catch (error) {
            console.error('Failed to load mappings:', error);
            loadingState.innerHTML = '<p class="has-text-danger">Failed to load seating charts</p>';
        }
    }
    
    function renderMappings(mappings) {
        loadingState.style.display = 'none';
        
        if (mappings.length === 0) {
            emptyState.style.display = 'block';
            return;
        }
        
        emptyState.style.display = 'none';
        
        mappings.forEach(mapping => {
            const card = document.createElement('div');
            card.className = 'column is-half';
            card.innerHTML = `
                <div class="box mapping-card" data-mapping-id="${mapping.id}">
                    <div class="level is-mobile mb-2">
                        <div class="level-left">
                            <h3 class="title is-5">${escapeHtml(mapping.name)}</h3>
                        </div>
                        <div class="level-right">
                            <span class="tag is-info is-light">${escapeHtml(mapping.layoutId)}</span>
                        </div>
                    </div>
                    <div class="content">
                        <p class="help">
                            <span id="mapping-count-${mapping.id}">${Object.keys(mapping.assignments || {}).length}</span> students assigned
                        </p>
                        <p class="help">
                            Created: ${formatDate(mapping.createdAt)}
                        </p>
                    </div>
                    <div class="field is-grouped mt-4">
                        <div class="control">
                            <a href="/sections/${sectionId}/class-mappings/${mapping.id}" class="button is-small">
                                <i class="fas fa-eye mr-1"></i>View
                            </a>
                        </div>
                        <div class="control">
                            <button class="button is-small is-danger is-outlined delete-mapping-btn" data-mapping-id="${mapping.id}" data-mapping-name="${escapeHtml(mapping.name)}">
                                <i class="fas fa-trash mr-1"></i>Delete
                            </button>
                        </div>
                    </div>
                </div>
            `;
            mappingsList.appendChild(card);
        });
        
        document.querySelectorAll('.delete-mapping-btn').forEach(btn => {
            btn.addEventListener('click', function() {
                mappingsToDelete = {
                    id: this.dataset.mappingId,
                    name: this.dataset.mappingName
                };
                document.getElementById('delete-mapping-name').textContent = mappingsToDelete.name;
                deleteModal.classList.add('is-active');
            });
        });
    }
    
    async function deleteMapping() {
        if (!mappingsToDelete) return;
        
        try {
            const response = await fetch('/api/sections/' + sectionId + '/class-mappings/' + mappingsToDelete.id, {
                method: 'DELETE'
            });
            
            if (!response.ok) {
                throw new Error('Failed to delete mapping');
            }
            
            deleteModal.classList.remove('is-active');
            
            // Remove from DOM
            const card = document.querySelector('.mapping-card[data-mapping-id="' + mappingsToDelete.id + '"]');
            if (card) {
                card.closest('.column').remove();
            }
            
            // Check if empty
            const remainingCards = document.querySelectorAll('.mapping-card');
            if (remainingCards.length === 0) {
                emptyState.style.display = 'block';
            }
            
        } catch (error) {
            console.error('Failed to delete mapping:', error);
            alert('Failed to delete seating chart: ' + error.message);
        }
    }
    
    function formatDate(dateString) {
        if (!dateString) return 'Unknown';
        const date = new Date(dateString);
        return date.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        });
    }
    
    function escapeHtml(text) {
        if (!text) return '';
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
    
    if (confirmDeleteBtn) {
        confirmDeleteBtn.addEventListener('click', deleteMapping);
    }
    
    if (cancelDeleteBtn) {
        cancelDeleteBtn.addEventListener('click', function() {
            deleteModal.classList.remove('is-active');
            mappingsToDelete = null;
        });
    }
    
    if (modalClose) {
        modalClose.addEventListener('click', function() {
            deleteModal.classList.remove('is-active');
            mappingsToDelete = null;
        });
    }
    
    document.querySelectorAll('.modal-background').forEach(bg => {
        bg.addEventListener('click', function() {
            deleteModal.classList.remove('is-active');
            mappingsToDelete = null;
        });
    });
    
    loadMappings();
});
