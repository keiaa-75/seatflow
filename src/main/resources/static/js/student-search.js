$(document).ready(function() {
    console.log('Student search script loaded');
    
    const $searchInput = $('#student-search');
    const $studentsView = $('#students-view');
    const $studentsResults = $('#students-results');
    const $studentsList = $('#students-list');
    const $resultsContainer = $('#results-container');
    
    // Extract sectionId from URL
    const pathParts = window.location.pathname.split('/');
    const sectionId = pathParts[2];
    
    console.log('Current URL:', window.location.pathname);
    console.log('Extracted sectionId:', sectionId);
    console.log('Search input found:', $searchInput.length > 0);
    console.log('Students view found:', $studentsView.length > 0);
    console.log('Results container found:', $studentsResults.length > 0);
    console.log('Results container content div found:', $resultsContainer.length > 0);

    // Initialize search functionality if all required elements exist
    if ($searchInput.length && $studentsView.length && $studentsResults.length && $resultsContainer.length) {
        console.log('All required elements found, setting up search functionality');
        
        // Add visual feedback for search on the control wrapper
        const $controlWrapper = $searchInput.closest('.control');
        
        // Set up search input event listener (merged with visual feedback)
        $searchInput.on('input', debounce(function() {
            const query = $(this).val().trim();
            console.log('Search input changed:', query);
            
            // Update loading state based on query presence
            if (query.length > 0) {
                $controlWrapper.addClass('is-loading');
                performStudentSearch(sectionId, query);
            } else {
                $controlWrapper.removeClass('is-loading');
                showDefaultStudentView();
            }
        }, 300));
    } else {
        console.error('Missing required elements for student search functionality');
        console.error('Search input:', $searchInput.length, 'Students view:', $studentsView.length, 
                     'Results container:', $studentsResults.length, 'Content div:', $resultsContainer.length);
    }
});

function debounce(func, wait) {
    let timeout;
    return function() {
        const context = this, args = arguments;
        clearTimeout(timeout);
        timeout = setTimeout(() => func.apply(context, args), wait);
    };
}

async function performStudentSearch(sectionId, query) {
    try {
        console.log('Performing student search for sectionId:', sectionId, 'query:', query);
        
        const response = await fetch('/sections/' + sectionId + '/students/api/search?q=' + encodeURIComponent(query), {
            headers: { 'Accept': 'application/json' }
        });

        console.log('Search API response status:', response.status);
        
        if (response.ok) {
            const students = await response.json();
            console.log('Search results:', students);
            renderStudentSearchResults(students);
        } else {
            console.error('Search API error:', response.status, response.statusText);
            const $searchInput = $('#student-search');
            const $controlWrapper = $searchInput.closest('.control');
            $controlWrapper.removeClass('is-loading');
            $('#results-container').html('<div class="has-text-centered has-text-danger py-5">Search error: ' + escapeHtml(response.status + ' ' + response.statusText) + '</div>');
        }
    } catch (error) {
        console.error('Student search error:', error);
        const $searchInput = $('#student-search');
        const $controlWrapper = $searchInput.closest('.control');
        $controlWrapper.removeClass('is-loading');
        $('#results-container').html('<div class="has-text-centered has-text-danger py-5">Network error: ' + escapeHtml(error.message) + '</div>');
    }
}

function renderStudentSearchResults(students) {
    const $container = $('#results-container');
    const $studentsView = $('#students-view');
    const $studentsResults = $('#students-results');
    const $searchInput = $('#student-search');

    console.log('Rendering search results:', students);

    // Remove loading indicator
    const $controlWrapper = $searchInput.closest('.control');
    $controlWrapper.removeClass('is-loading');

    // Show results container and hide main view
    $studentsView.addClass('is-hidden');
    $studentsResults.removeClass('is-hidden');

    if (students.length === 0) {
        console.log('No students found for search');
        $container.html('<div class="has-text-centered has-text-grey py-5">No students found matching your search.</div>');
        return;
    }

    console.log('Displaying ' + students.length + ' search results');

    let html = '';

    students.forEach(student => {
        html += '<a class="box mb-3 is-block student-item" href="/sections/' + encodeURIComponent(student.academicStructure.id) + '/students/edit/' + encodeURIComponent(student.studentId) + '" data-search-text="' + escapeHtml(student.displayName + ' ' + student.studentId) + '">';
        html += '<div>';
        html += '<span class="has-text-weight-medium">' + escapeHtml(student.displayName) + '</span>';
        html += '</div>';
        html += '<div>';
        html += '<span class="tag is-info is-light">' + escapeHtml(student.studentId) + '</span>';
        html += '</div>';
        html += '</a>';
    });

    $container.html(html);

    console.log('Search results rendered successfully');
}

function showDefaultStudentView() {
    const $searchInput = $('#student-search');
    const $controlWrapper = $searchInput.closest('.control');
    $controlWrapper.removeClass('is-loading');

    $('#students-results').addClass('is-hidden');
    $('#students-view').removeClass('is-hidden');
}

function escapeHtml(text) {
    return $('<div>').text(text).html();
}