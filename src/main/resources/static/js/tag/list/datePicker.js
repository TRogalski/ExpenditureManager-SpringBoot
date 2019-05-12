document.addEventListener('DOMContentLoaded', function () {
    $('#date_picker').datepicker({
        viewMode: "years",
        minViewMode: "years",
        format: "yyyy"
    }).datepicker("setDate", 'now');
})