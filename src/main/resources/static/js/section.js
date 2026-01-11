$(document).ready(function() {
    const $searchInput = $('#section-search');
    const $deleteModal = $('#delete-modal');

    if ($searchInput.length) {
        $searchInput.on('input', debounce(function() {
            const query = $(this).val().trim();
            if (query.length > 0) {
                performSearch(query);
            } else {
                showDefaultView();
            }
        }, 300));
    }

    $('#delete-section-btn').on('click', function() {
        $deleteModal.addClass('is-active');
    });

    $('.modal .delete, .modal-background, .modal-cancel').on('click', function() {
        $deleteModal.removeClass('is-active');
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
    const $container = $('#results-container');
    const $view = $('#sections-view');
    const $results = $('#sections-results');

    $view.addClass('is-hidden');
    $results.removeClass('is-hidden');

    if (sections.length === 0) {
        $container.html('<div class="box has-text-centered"><p class="has-text-grey">No sections found.</p></div>');
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
    $container.html(html);
}

function showDefaultView() {
    $('#sections-results').addClass('is-hidden');
    $('#sections-view').removeClass('is-hidden');
}

function escapeHtml(text) {
    return $('<div>').text(text).html();
}
