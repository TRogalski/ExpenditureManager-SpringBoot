document.addEventListener('DOMContentLoaded', function () {

    $('#date_picker').datepicker({
        viewMode: "months",
        minViewMode: "months",
        format: "yyyy-mm"
    }).datepicker("setDate", 'now');

    getTopTagsAssignedToMonth($('#date_picker').datepicker('getFormattedDate') + "-01")

    $('#date_picker').on('changeDate', function () {
        getTopTagsAssignedToMonth($('#date_picker').datepicker('getFormattedDate') + "-01");
        console.log($('#date_picker').datepicker('getFormattedDate'))
    });
})

//display top tags

function getTopTagsAssignedToMonth(date) {
    fetch("http://localhost:8084/expenditure/stats/" + date).then(function (response) {
        return response.json();
    }).then(function (dateExpendituresJson) {

        var toDelete = document.getElementById("top_tags_records");

        if (toDelete != null) {
            removeEnlistedTags(toDelete)
        }
        console.log(dateExpendituresJson)
        appendReceivedTags(dateExpendituresJson)
        fillInStatistics(dateExpendituresJson)
    });

}

function removeEnlistedTags(toDelete) {

    while (toDelete.hasChildNodes()) {
        toDelete.removeChild(toDelete.lastChild);
    }
}

function appendReceivedTags(dateExpendituresJson) {


    var topTags = dateExpendituresJson.topTags;


    topTags.sort(function (a, b) {
        return b.monthTotal - a.monthTotal;
    });

    for (var i = 0; i < topTags.length; i++) {
        var listElement = $(`<tr>
                                <td>${topTags[i].name}</td>
                                <td>${topTags[i].monthTotal}</td>
                                <td>${(topTags[i].monthTotal / dateExpendituresJson.monthTotal * 100).toFixed(2)}</td>
                                <td>
                                    <a href="http://localhost:8084/expenditure/list/${dateExpendituresJson.date}/${topTags[i].id}">
                                        ${topTags[i].monthCount}
                                    </a>
                                </td>
                            </tr>`);
        $("#top_tags_records").append(listElement)
    }
}

// fill in the statistics

function getThisVsPreviousPercentage(dateExpendituresJson) {
    var thisMonth = dateExpendituresJson.monthTotal;
    var previousMonth = dateExpendituresJson.previousMonthTotal;

    if (thisMonth == null ||
        thisMonth == 0 ||
        previousMonth == null ||
        previousMonth == 0) {
        return "N/A"
    }
    return ((thisMonth - previousMonth) / previousMonth * 100).toFixed(2);
}

function getThisYearMonthlyAverage(dateExpendituresJson) {

    var avg = 0;
    var count = 0;

    for (var i = 0; i < dateExpendituresJson.timeSeries.length; i++) {
        if (dateExpendituresJson.timeSeries[i] > 0) {
            avg += dateExpendituresJson.timeSeries[i];
            count++;
        }
    }

    return (avg / count).toFixed(2);
}

function getCurrentVsPreviousTotal(dateExpendituresJson) {

    if (dateExpendituresJson.monthTotal == null ||
        dateExpendituresJson.monthTotal == 0 ||
        dateExpendituresJson.previousMonthTotal == null ||
        dateExpendituresJson.previousMonthTotal == 0) {
        return "N/A";
    }

    return dateExpendituresJson.monthTotal - dateExpendituresJson.previousMonthTotal;
}

function fillInStatistics(dateExpendituresJson) {
    $('#this_month_total').html(dateExpendituresJson.monthTotal==null?0:dateExpendituresJson.monthTotal)
    $('#previous_month_total').html(dateExpendituresJson.previousMonthTotal==null?0:dateExpendituresJson.previousMonthTotal)
    $('#this_vs_previous_total').html(getCurrentVsPreviousTotal(dateExpendituresJson))
    $('#this_vs_previous_percentage').html(getThisVsPreviousPercentage(dateExpendituresJson))
    $('#this_year_total').html(dateExpendituresJson.yearTotal)
    $('#this_year_monthly_average').html(getThisYearMonthlyAverage(dateExpendituresJson))
}

