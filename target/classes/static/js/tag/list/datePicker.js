document.addEventListener('DOMContentLoaded', function () {
    $('#date_picker').datepicker({
        viewMode: "months",
        minViewMode: "months",
        format: "yyyy-mm"
    }).datepicker("setDate", 'now');

    $('#date_picker').on('changeDate', function () {
        updateTagStatistics($('#date_picker').datepicker('getFormattedDate'));
    });
})

function updateTagStatistics(date) {
    $.ajax({
        'url': window.location.origin + "/tag/list/" + date + "-01",
        'dataType': "json",
        'success': function (jsonData) {

            var toDelete = document.getElementById("tag_records");

            if (toDelete != null) {
                removeEnlistedTags(toDelete)
            }
            appendReceivedTags(jsonData)
            updatePeriodsInHeaders(jsonData)
        }
    })
};

function removeEnlistedTags(toDelete) {
    while (toDelete.hasChildNodes()) {
        toDelete.removeChild(toDelete.lastChild);
    }
}

function appendReceivedTags(jsonData) {
    var currentMonthTagTotals = jsonData.currentMonthAllTagTotals;
    var previousMonthTagTotals = jsonData.previousMonthAllTagTotals;
    var currentYearAllTagTotals = jsonData.currentYearAllTagTotals;

    Object.keys(currentMonthTagTotals).forEach(function (key) {
        var listElement = $(`<tr>
                                <td>${currentMonthTagTotals[key].name}</td>
                                <td>${currentMonthTagTotals[key].total.toFixed(2)}</td>
                                <td>${(currentMonthTagTotals[key].total - previousMonthTagTotals[key].total).toFixed(2)}</td>
                                <td>${currentYearAllTagTotals[key].total.toFixed(2)}</td>
                                <td>
                                    <a href="${window.location.origin}/expenditure/list/${jsonData.date}/${currentMonthTagTotals[key].id}">${currentMonthTagTotals[key].count}</a>
                                </td>
                                <td>
                                    <a href="#modal_delete" data-toggle="modal" data-tag-id=${currentMonthTagTotals[key].id}>
                                        <span class="glyphicon glyphicon-remove"></span>
                                    </a>
                                    <a href= ${window.location.origin + "/tag/edit/" + currentMonthTagTotals[key].id}>
                                        <span class="glyphicon glyphicon-pencil"></span>
                                    </a>
                                </td>
                            </tr>`);
        $("#tag_records").append(listElement)
    })
}

function updatePeriodsInHeaders(jsonData) {
    $('#current_month_name').text(jsonData.currentMonthName);
    $('#previous_month_name').text("vs " + jsonData.previousMonthName);
    $('#current_year_name').text(jsonData.currentYearName);
}