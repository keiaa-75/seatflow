document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('section-search');
    if (searchInput) {
        searchInput.addEventListener('input', debounce(function() {
            const query = this.value.trim();
            if (query.length > 0) {
                performSearch(query);
            } else {
                showDefaultView();
            }
        }, 300));
    }

    const deleteModal = document.getElementById('delete-modal');
    const deleteBtn = document.getElementById('delete-section-btn');
    if (deleteBtn && deleteModal) {
        deleteBtn.addEventListener('click', function() {
            deleteModal.classList.add('is-active');
        });
    }

    document.querySelectorAll('.modal .delete, .modal-background, .modal-cancel').forEach(button => {
        if (deleteModal) {
            button.addEventListener('click', function() {
                deleteModal.classList.remove('is-active');
            });
        }
    });
});

function debounce(func, wait) {
    let timeout;
    return function() {
        const context = this, args = arguments;
        clearTimeout(timeout);
        timeout = setTimeout(() => func.apply(context, args), wait);
    };
}

async function performSearch(query) {
    try {
        const response = await fetch('/sections/api/search?q=' + encodeURIComponent(query), {
            headers: { 'Accept': 'application/json' }
        });

        if (response.ok) {
            const sections = await response.json();
            renderSearchResults(sections);
        }
    } catch (error) {
        console.error('Search error:', error);
    }
}

function renderSearchResults(sections) {
    const container = document.getElementById('results-container');
    const view = document.getElementById('sections-view');
    const results = document.getElementById('sections-results');

    view.classList.add('is-hidden');
    results.classList.remove('is-hidden');

    if (sections.length === 0) {
        container.innerHTML = '<div class="box has-text-centered"><p class="has-text-grey">No sections found.</p></div>';
        return;
    }

    let html = '<div class="box"><p class="has-text-centered has-text-weight-semibold mb-4">Search results</p>';
    sections.forEach(section => {
        const grade = section.gradeLevel && section.gradeLevel.displayValue ? section.gradeLevel.displayValue : '';
        const strand = section.strand ? section.strand.replace(/_/g, ' ') : '';
        html += '<a class="box mb-2 is-block" href="/sections/edit/' + section.id + '">';
        html += '<div class="is-flex is-justify-content-space-between is-align-items-center">';
        html += '<span class="has-text-weight-medium">' + escapeHtml(section.sectionName) + '</span>';
        html += '<span class="tag is-info is-light">' + escapeHtml(grade) + '</span>';
        html += '</div></a>';
    });
    html += '</div>';
    container.innerHTML = html;
}

function showDefaultView() {
    document.getElementById('sections-results').classList.add('is-hidden');
    document.getElementById('sections-view').classList.remove('is-hidden');
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}
