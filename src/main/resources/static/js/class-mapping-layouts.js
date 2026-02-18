document.addEventListener('DOMContentLoaded', function() {
    const nameModal = document.getElementById('name-modal');
    const loadingModal = document.getElementById('loading-modal');
    const nameInput = document.getElementById('mapping-name-input');
    const confirmCreateBtn = document.getElementById('confirm-create-btn');
    const cancelCreateBtn = document.getElementById('cancel-create-btn');
    const modalClose = nameModal.querySelector('.modal-close');
    
    const layoutBtns = document.querySelectorAll('.layout-select-btn');
    const generateBtns = document.querySelectorAll('.generate-btn');
    const dropdownTriggers = document.querySelectorAll('.dropdown-trigger');
    
    let isAutoGenerate = false;
    let selectedPresetType = null;
    
    // Generate layout previews
    generateLayoutPreviews();
    
    // Setup dropdown toggles
    dropdownTriggers.forEach(trigger => {
        trigger.addEventListener('click', function(e) {
            e.stopPropagation();
            const dropdown = this.closest('.dropdown');
            dropdown.classList.toggle('is-active');
        });
    });
    
    // Close dropdowns when clicking outside
    document.addEventListener('click', function(e) {
        if (!e.target.closest('.dropdown')) {
            document.querySelectorAll('.dropdown.is-active').forEach(d => d.classList.remove('is-active'));
        }
    });
    
    function generateLayoutPreviews() {
        document.querySelectorAll('.layout-preview').forEach(preview => {
            const rows = parseInt(preview.dataset.rows);
            const cols = parseInt(preview.dataset.columns);
            const disabledSeatsStr = preview.dataset.disabledSeats;
            const disabledSeats = disabledSeatsStr ? disabledSeatsStr.split(',') : [];
            
            // Calculate preview size - limit max cells for performance
            const maxPreviewCells = 100;
            const totalCells = rows * cols;
            const scale = totalCells > maxPreviewCells ? Math.sqrt(maxPreviewCells / totalCells) : 1;
            
            const previewRows = Math.max(3, Math.floor(rows * scale));
            const previewCols = Math.max(3, Math.floor(cols * scale));
            
            preview.style.display = 'grid';
            preview.style.gridTemplateColumns = `repeat(${previewCols}, 1fr)`;
            preview.style.gap = '2px';
            preview.style.width = '100%';
            preview.style.maxWidth = '200px';
            preview.style.margin = '0 auto 1rem';
            preview.style.aspectRatio = `${cols}/${rows}`;
            
            for (let r = 1; r <= previewRows; r++) {
                for (let c = 1; c <= previewCols; c++) {
                    const seat = document.createElement('div');
                    seat.style.aspectRatio = '1';
                    seat.style.borderRadius = '2px';
                    
                    // Map preview coordinates to actual layout coordinates
                    const actualRow = Math.ceil((r / previewRows) * rows);
                    const actualCol = Math.ceil((c / previewCols) * cols);
                    const seatId = `${actualRow}-${actualCol}`;
                    
                    if (disabledSeats.includes(seatId)) {
                        seat.style.backgroundColor = 'transparent';
                    } else {
                        seat.style.backgroundColor = '#dbdbdb';
                    }
                    
                    preview.appendChild(seat);
                }
            }
        });
    }
    
    function openNameModal(layoutId, layoutName, autoGen = false, presetType = null) {
        selectedLayoutId = layoutId;
        selectedLayoutName = layoutName;
        isAutoGenerate = autoGen;
        selectedPresetType = presetType;
        
        // Update modal title based on action
        const modalTitle = nameModal.querySelector('.modal-card-title');
        if (autoGen) {
            modalTitle.textContent = `Name your ${presetType.toLowerCase()} seating chart`;
            nameInput.placeholder = `e.g., ${presetType} Seating, ${layoutName} - ${presetType}`;
        } else {
            modalTitle.textContent = 'Name your seating chart';
            nameInput.placeholder = 'e.g., Monday Seating, Week 3, Exam Layout';
        }
        
        nameModal.classList.add('is-active');
        nameInput.value = '';
        nameInput.focus();
    }
    
    function closeNameModal() {
        nameModal.classList.remove('is-active');
        selectedLayoutId = null;
        selectedLayoutName = null;
        isAutoGenerate = false;
        selectedPresetType = null;
    }
    
    function showLoading(message = 'Creating seating chart...') {
        loadingModal.querySelector('p').textContent = message;
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
        
        showLoading(isAutoGenerate ? 'Generating seating assignments...' : 'Creating seating chart...');
        
        try {
            let response;
            let result;
            
            if (isAutoGenerate && selectedPresetType) {
                // Call auto-generate endpoint
                response = await fetch('/api/sections/' + sectionId + '/class-mappings/generate', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        layoutId: selectedLayoutId,
                        presetType: selectedPresetType,
                        assignmentName: name
                    })
                });
            } else {
                // Call init endpoint for empty mapping
                response = await fetch('/api/sections/' + sectionId + '/class-mappings/init', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        name: name,
                        layoutId: selectedLayoutId
                    })
                });
            }
            
            if (!response.ok) {
                const error = await response.text();
                throw new Error(error || 'Failed to create mapping');
            }
            
            result = await response.json();
            closeNameModal();
            hideLoading();
            
            // Redirect to the view page with the new mapping ID
            window.location.href = '/sections/' + sectionId + '/class-mappings/' + result.mappingId;
            
        } catch (error) {
            console.error('Failed to create mapping:', error);
            hideLoading();
            alert('Failed to create seating chart: ' + error.message);
        }
    }
    
    layoutBtns.forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            openNameModal(this.dataset.layoutId, this.dataset.layoutName, false);
        });
    });
    
    generateBtns.forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            // Close the dropdown
            this.closest('.dropdown').classList.remove('is-active');
            openNameModal(this.dataset.layoutId, this.dataset.layoutName, true, this.dataset.preset);
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
