document.addEventListener('DOMContentLoaded', function () {
    $('#date_picker').datepicker({
        viewMode: "months",
        minViewMode: "months",
        format: "yyyy-mm"
    }).datepicker("setDate", 'now');

//    getTopTagsAssignedToMonth($('#date_picker').datepicker('getFormattedDate'))

    $('#date_picker').on('changeDate', function () {
        getTopTagsAssignedToMonth($('#date_picker').datepicker('getFormattedDate'));
    });
})

function getTopTagsAssignedToMonth(date) {
    $.ajax({
        'url': window.location.origin + "/expenditure/dashboard/" + date + "-01",
        'dataType': "json",
        'success': function (jsonData) {

            $("#top_tags_records").empty()

//            removeEnlistedTags(toDelete)
            appendReceivedTags(jsonData)
            fillInStatistics(jsonData)
        }
    })
};


//function removeEnlistedTags(toDelete) {
//    while (toDelete.hasChildNodes()) {
//        toDelete.removeChild(toDelete.lastChild);
//    }
//}


function appendReceivedTags(jsonData) {
    var tagTotals = jsonData.currentMonthTagTotals;

//    tagTotals.sort(function (a, b) {
//        return b.total - a.total;
//    });

    for (var i = 0; i < tagTotals.length; i++) {
        var listElement = $(`<tr>
                                <td>${tagTotals[i].name}</td>
                                <td>${tagTotals[i].total}</td>
                                <td>${(tagTotals[i].total / jsonData.currentMonthTotal * 100).toFixed(2)}</td>
                                <td>
                                    <a href= "${window.location.origin}/expenditure/list/${jsonData.date}/${tagTotals[i].id}">
                                        ${tagTotals[i].count}
                                    </a>
                                </td>
                            </tr>`);
        $("#top_tags_records").append(listElement)
    }
}

function getThisVsPreviousPercentage(jsonData) {
    var thisMonth = jsonData.currentMonthTotal;
    var previousMonth = jsonData.previousMonthTotal;

    if (thisMonth == null ||
        thisMonth == 0 ||
        previousMonth == null ||
        previousMonth == 0) {
        return "N/A"
    }
    return ((thisMonth - previousMonth) / previousMonth * 100).toFixed(2);
}


function getThisYearMonthlyAverage(jsonData) {
    var average = 0;
    var count = 0;
    var monthlyTimeSeries = jsonData.currentYearMonthlyTimeSeries

    for (var i = 0; i < monthlyTimeSeries.length; i++) {
        if (monthlyTimeSeries[i] > 0) {
            average += monthlyTimeSeries[i];
            count++;
        }
    }
    return (average / count).toFixed(2);
}


function getCurrentVsPreviousTotal(jsonData) {

    if (jsonData.currentMonthTotal == null ||
        jsonData.currentMonthTotal == 0 ||
        jsonData.previousMonthTotal == null ||
        jsonData.previousMonthTotal == 0) {
        return "N/A";
    }

    return jsonData.currentMonthTotal - jsonData.previousMonthTotal;
}


function fillInStatistics(jsonData) {
    $('#this_month_total').html(jsonData.currentMonthTotal == null ? 0 : formatNumber(jsonData.currentMonthTotal))
    $('#previous_month_total').html(jsonData.previousMonthTotal == null ? 0 : formatNumber(jsonData.previousMonthTotal))
    $('#this_vs_previous_total').html(getCurrentVsPreviousTotal(jsonData))
    $('#this_vs_previous_percentage').html(getThisVsPreviousPercentage(jsonData))
    $('#this_year_total').html(formatNumber(jsonData.currentYearTotal))
    $('#this_year_monthly_average').html(formatNumber(getThisYearMonthlyAverage(jsonData)))
}


function formatNumber(num) {
    return num.toString().replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1,')
}