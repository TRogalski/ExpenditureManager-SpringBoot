document.addEventListener("DOMContentLoaded", function () {
    $('#add_expenditure').on('click', function () {
        window.location.href = "/expenditure/add/" + $('#date_picker').datepicker('getFormattedDate');
    });
})
