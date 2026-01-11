$(document).ready(function() {
    const $aboutModal = $('#aboutModal');
    const $htmlElement = $('html');

    $('#aboutButton').on('click', function() {
        $aboutModal.addClass('is-active');
        $htmlElement.addClass('is-modal-active');
    });

    $('.modal .delete, .modal-background').on('click', function() {
        $aboutModal.removeClass('is-active');
        $htmlElement.removeClass('is-modal-active');
    });
});

function closeAboutModal() {
    const $aboutModal = $('#aboutModal');
    const $htmlElement = $('html');

    $aboutModal.removeClass('is-active');
    $htmlElement.removeClass('is-modal-active');
}
