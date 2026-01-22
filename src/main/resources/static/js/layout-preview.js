document.addEventListener('DOMContentLoaded', function() {
    fetchLayouts();
});

async function fetchLayouts() {
    try {
        const response = await fetch('/api/layouts');
        const layouts = await response.json();
        
        layouts.forEach(layout => {
            const preview = document.querySelector(`[data-layout="${layout.presetId}"]`);
            if (preview) {
                generateLayoutPreview(preview, layout);
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
        
    } catch (error) {
        console.error('Failed to fetch layouts:', error);
    }
}

function generateLayoutPreview(preview, layout) {
    const cols = layout.columns;
    const rows = layout.rows;
    const disabledSeats = layout.disabledSeats || [];
    
    preview.style.gridTemplateColumns = `repeat(${cols}, 1fr)`;
    preview.innerHTML = ''; // Clear existing seats
    
    for (let r = 1; r <= rows; r++) {
        for (let c = 1; c <= cols; c++) {
            const seat = document.createElement('div');
            seat.className = 'preview-seat';
            
            if (disabledSeats.includes(`${r}-${c}`)) {
                seat.classList.add('disabled');
            }
            
            preview.appendChild(seat);
        }
    }
}

function generateUShapeDisabled(rows, cols) {
    const disabled = [];
    const centerCols = 2; // Two center columns
    const startCol = Math.floor((cols - centerCols) / 2);
    
    // Disable center columns except first row (front) and last two rows (back)
    for (let r = 2; r <= rows - 2; r++) {
        for (let c = startCol + 1; c <= startCol + centerCols; c++) {
            disabled.push(`${r}-${c}`);
        }
    }
    
    return disabled;
}

function generateGroupsDisabled(rows, cols) {
    const disabled = [];
    for (let r = 1; r <= rows; r++) {
        for (let c = 1; c <= cols; c++) {
            if (r % 3 === 0 || c % 3 === 0) {
                disabled.push(`${r}-${c}`);
            }
        }
    }
    return disabled;
}

function selectLayout(layoutType, sectionId) {
    window.location.href = `/sections/${sectionId}/assignments/assign?layoutType=${layoutType}`;
}
