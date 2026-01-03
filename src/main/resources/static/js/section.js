document.addEventListener('DOMContentLoaded', function() {
    const messageHeaders = document.querySelectorAll('.message-header.clickable-row');
    messageHeaders.forEach(header => {
        header.addEventListener('click', function() {
            const targetId = this.getAttribute('data-toggle');
            const target = document.getElementById(targetId);
            const icon = this.querySelector('.icon i');
            if (target && icon) {
                target.classList.toggle('is-hidden');
                icon.classList.toggle('fa-chevron-down');
                icon.classList.toggle('fa-chevron-up');
            }
        });
    });

    const tabs = document.querySelectorAll('.tabs li');
    tabs.forEach(tab => {
        tab.addEventListener('click', function() {
            tabs.forEach(t => t.classList.remove('is-active'));
            this.classList.add('is-active');
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
    const modalCloseButtons = document.querySelectorAll('.modal .delete, .modal-background, .modal-cancel');

    if (deleteBtn && deleteModal) {
        deleteBtn.addEventListener('click', function() {
            deleteModal.classList.add('is-active');
        });
    }

    modalCloseButtons.forEach(button => {
        button.addEventListener('click', function() {
            deleteModal.classList.remove('is-active');
        });
    });

    document.querySelectorAll('.clickable-row').forEach(row => {
        row.addEventListener('click', function(e) {
            const sectionId = this.getAttribute('data-section-id');
            if (sectionId) {
                window.location.href = '/sections/edit/' + sectionId;
            }
        });
    });

    document.addEventListener('click', function(e) {
        const resultItem = e.target.closest('.section-result-item');
        if (resultItem) {
            const sectionId = resultItem.getAttribute('data-section-id');
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

async function performSearch(query) {
    try {
        const response = await fetch('/sections/search?q=' + encodeURIComponent(query) + '&fragment=true', {
            headers: { 'X-Requested-With': 'XMLHttpRequest' }
        });

        if (response.ok) {
            const html = await response.text();
            document.getElementById('sections-view').classList.add('is-hidden');
            const resultsContainer = document.getElementById('sections-results');
            resultsContainer.classList.remove('is-hidden');
            resultsContainer.innerHTML = html;
        }
    } catch (error) {
        console.error('Search error:', error);
    }
}

function showDefaultView() {
    document.getElementById('sections-results').classList.add('is-hidden');
    document.getElementById('sections-view').classList.remove('is-hidden');
}
