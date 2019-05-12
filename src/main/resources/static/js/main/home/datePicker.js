document.addEventListener('DOMContentLoaded', function () {
    $('#date_picker').datepicker({
        viewMode: "months",
        minViewMode: "months",
        format: "yyyy-mm"
    }).datepicker("setDate", 'now');

    getTopTagsAssignedToMonth($('#date_picker').datepicker('getFormattedDate'))

    $('#date_picker').on('changeDate', function () {
        getTopTagsAssignedToMonth($('#date_picker').datepicker('getFormattedDate'));
    });
})


function getTopTagsAssignedToMonth(date) {
    $.ajax({
        'url': "http://localhost:8084/expenditure/stats/" + date + "-01",
        'dataType': "json",
        'success': function (jsonData) {

            var toDelete = document.getElementById("top_tags_records");

            if (toDelete != null) {
                removeEnlistedTags(toDelete)
            }
            appendReceivedTags(jsonData)
            fillInStatistics(jsonData)
        }
    })
};


function removeEnlistedTags(toDelete) {
    while (toDelete.hasChildNodes()) {
        toDelete.removeChild(toDelete.lastChild);
    }
}


function appendReceivedTags(jsonData) {
    var topTags = jsonData.topTags;

    topTags.sort(function (a, b) {
        return b.monthTotal - a.monthTotal;
    });

    for (var i = 0; i < topTags.length; i++) {
        var listElement = $(`<tr>
                                <td>${topTags[i].name}</td>
                                <td>${topTags[i].monthTotal}</td>
                                <td>${(topTags[i].monthTotal / jsonData.monthTotal * 100).toFixed(2)}</td>
                                <td>
                                    <a href="http://localhost:8084/expenditure/list/${jsonData.date}/${topTags[i].id}">
                                        ${topTags[i].monthCount}
                                    </a>
                                </td>
                            </tr>`);
        $("#top_tags_records").append(listElement)
    }
}


function getThisVsPreviousPercentage(jsonData) {
    var thisMonth = jsonData.monthTotal;
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
    var avg = 0;
    var count = 0;

    for (var i = 0; i < jsonData.timeSeries.length; i++) {
        if (jsonData.timeSeries[i] > 0) {
            avg += jsonData.timeSeries[i];
            count++;
        }
    }
    return (avg / count).toFixed(2);
}


function getCurrentVsPreviousTotal(jsonData) {

    if (jsonData.monthTotal == null ||
        jsonData.monthTotal == 0 ||
        jsonData.previousMonthTotal == null ||
        jsonData.previousMonthTotal == 0) {
        return "N/A";
    }

    return jsonData.monthTotal - jsonData.previousMonthTotal;
}


function fillInStatistics(jsonData) {
    $('#this_month_total').html(jsonData.monthTotal == null ? 0 : formatNumber(jsonData.monthTotal))
    $('#previous_month_total').html(jsonData.previousMonthTotal == null ? 0 : formatNumber(jsonData.previousMonthTotal))
    $('#this_vs_previous_total').html(getCurrentVsPreviousTotal(jsonData))
    $('#this_vs_previous_percentage').html(getThisVsPreviousPercentage(jsonData))
    $('#this_year_total').html(formatNumber(jsonData.yearTotal))
    $('#this_year_monthly_average').html(formatNumber(getThisYearMonthlyAverage(jsonData)))
}


function formatNumber(num) {
    return num.toString().replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1,')
}