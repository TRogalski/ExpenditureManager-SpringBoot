document.addEventListener('DOMContentLoaded', function () {
    $('#date_picker').datepicker({
        viewMode: "months",
        minViewMode: "months",
        format: "yyyy-mm"
    }).datepicker("setDate", 'now');
})