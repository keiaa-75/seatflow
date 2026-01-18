document.addEventListener('DOMContentLoaded', function() {
    const layouts = {
        'NORMAL': { rows: 8, cols: 8, disabled: [] },
        'SMALL': { rows: 4, cols: 4, disabled: [] },
        'ROWS': { rows: 6, cols: 5, disabled: [] },
        'U_SHAPE': { rows: 6, cols: 5, disabled: generateUShapeDisabled(6, 5) },
        'GROUPS': { rows: 5, cols: 5, disabled: generateGroupsDisabled(5, 5) }
    };
    
    Object.keys(layouts).forEach(layoutType => {
        const preview = document.querySelector(`[data-layout="${layoutType}"]`);
        const layout = layouts[layoutType];
        
        preview.style.gridTemplateColumns = `repeat(${layout.cols}, 1fr)`;
        
        for (let r = 0; r < layout.rows; r++) {
            for (let c = 0; c < layout.cols; c++) {
                const seat = document.createElement('div');
                seat.className = 'preview-seat';
                
                if (layout.disabled.includes(`${r}-${c}`)) {
                    seat.classList.add('disabled');
                }
                
                preview.appendChild(seat);
            }
        }
    });
    
    // Add click handlers
    document.querySelectorAll('.layout-card').forEach(card => {
        card.addEventListener('click', function() {
            const layoutType = this.getAttribute('data-layout-type');
            const sectionId = this.getAttribute('data-section-id');
            selectLayout(layoutType, sectionId);
        });
    });
});

function generateUShapeDisabled(rows, cols) {
    const disabled = [];
    const centerCols = 2; // Two center columns
    const startCol = Math.floor((cols - centerCols) / 2);
    
    // Disable center columns except first row (front) and last two rows (back)
    for (let r = 1; r < rows - 2; r++) {
        for (let c = startCol; c < startCol + centerCols; c++) {
            disabled.push(`${r}-${c}`);
        }
    }
    
    return disabled;
}

function generateGroupsDisabled(rows, cols) {
    const disabled = [];
    for (let r = 0; r < rows; r++) {
        for (let c = 0; c < cols; c++) {
            if ((r + 1) % 3 === 0 || (c + 1) % 3 === 0) {
                disabled.push(`${r}-${c}`);
            }
        }
    }
    return disabled;
}

function selectLayout(layoutType, sectionId) {
    window.location.href = `/sections/${sectionId}/assignments/assign?layoutType=${layoutType}`;
}
