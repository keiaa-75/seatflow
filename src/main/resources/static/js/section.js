document.addEventListener('DOMContentLoaded', function() {
    const detailsElements = document.querySelectorAll('details.message');
    detailsElements.forEach(details => {
        const summary = details.querySelector('summary');
        const icon = summary.querySelector('.fa-chevron-down, .fa-chevron-up');
        if (summary && icon) {
            details.addEventListener('toggle', function() {
                if (this.open) {
                    icon.classList.remove('fa-chevron-down');
                    icon.classList.add('fa-chevron-up');
                } else {
                    icon.classList.remove('fa-chevron-up');
                    icon.classList.add('fa-chevron-down');
                }
            });
        }
    });

    const tabs = document.querySelectorAll('.tabs li');
    tabs.forEach(tab => {
        tab.addEventListener('click', function() {
            tabs.forEach(t => t.classList.remove('is-active'));
            this.classList.add('is-active');
            const grade = this.getAttribute('data-grade');
            filterByGrade(grade);
        });
    });

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
        button.addEventListener('click', function() {
            deleteModal.classList.remove('is-active');
        });
    });

    document.addEventListener('click', function(e) {
        const sectionItem = e.target.closest('.section-item');
        if (sectionItem) {
            const sectionId = sectionItem.getAttribute('data-section-id');
            if (sectionId) {
                window.location.href = '/sections/edit/' + sectionId;
            }
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

function filterByGrade(grade) {
    document.querySelectorAll('.message-body .mb-3').forEach(gradeBlock => {
        if (grade === 'all') {
            gradeBlock.classList.remove('is-hidden');
        } else {
            const blockGrade = gradeBlock.getAttribute('data-grade');
            if (blockGrade === grade) {
                gradeBlock.classList.remove('is-hidden');
            } else {
                gradeBlock.classList.add('is-hidden');
            }
        }
    });
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
        html += '<div class="box mb-2 clickable-row section-item" data-section-id="' + section.id + '">';
        html += '<div class="is-flex is-justify-content-space-between is-align-items-center">';
        html += '<span class="has-text-weight-medium">' + escapeHtml(section.sectionName) + '</span>';
        html += '<span class="tag is-info is-light">' + escapeHtml(grade) + '</span>';
        html += '</div></div>';
    });
    html += '</div>';
    container.innerHTML = html;

    document.querySelectorAll('#results-container .clickable-row').forEach(row => {
        row.addEventListener('click', function() {
            const sectionId = this.getAttribute('data-section-id');
            if (sectionId) {
                window.location.href = '/sections/edit/' + sectionId;
            }
        });
    });
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
