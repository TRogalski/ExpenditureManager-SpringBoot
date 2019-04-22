//change hidden value on click

$('#date_picker').on('changeDate', function () {
    $('#date_picker_input').val(
        $('#date_picker').datepicker('getFormattedDate')
    );

});

$('#date_picker').datepicker({
    format: 'yyyy-mm-dd',
}).datepicker("setDate", 'now');

