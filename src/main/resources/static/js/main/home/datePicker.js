document.addEventListener('DOMContentLoaded', function () {


    var totalTimeSeries = (function () {
        var json = null;
        $.ajax({
            'async': false,
            'global': false,
            'url': "http://localhost:8084/expenditure/stats/" + getTodaysDate(),
            'dataType': "json",
            'success': function (data) {
                json = data;
            }
        });
        return json.totalTimeSeries;
    })();

    $('#date_picker').datepicker({
        format: "yyyy-mm-dd",
        beforeShowDay: function (date) {
            var d = date;
            var curr_date = d.getDate();
            var curr_month = d.getMonth() + 1; //Months are zero based
            var curr_year = d.getFullYear();

            if (curr_date < 10) {
                curr_date = '0' + curr_date
            }

            if (curr_month < 10) {
                curr_month = '0' + curr_month
            }

            var formattedDate = curr_year + "-" + curr_month + "-" + curr_date


            if ($.inArray(formattedDate, Object.keys(totalTimeSeries)) != -1) {
                return {
                    classes: 'highlight',
                    tooltip: totalTimeSeries[formattedDate]
                };
            }
            return;
        }
    }).datepicker("setDate", 'now');


    getTopTagsAssignedToMonth($('#date_picker').datepicker('getFormattedDate'))

    $('#date_picker').on('changeDate', function () {
        getTopTagsAssignedToMonth($('#date_picker').datepicker('getFormattedDate'));
        console.log($('#date_picker').datepicker('getFormattedDate'))
    });
})


function getTodaysDate() {
    var today = new Date();
    var dd = today.getDate();
    var mm = today.getMonth() + 1;
    var yyyy = today.getFullYear();

    if (dd < 10) {
        dd = '0' + dd
    }

    if (mm < 10) {
        mm = '0' + mm
    }

    return yyyy + '-' + mm + '-' + dd;

}


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
//

function getThisVsPreviousPercentage(dateExpendituresJson) {
    var thisMonth = dateExpendituresJson.monthTotal;
    var previousMonth = dateExpendituresJson.previousMonthTotal;

    if (previousMonth == 0) {
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

function fillInStatistics(dateExpendituresJson) {
    $('#this_month_total').html(dateExpendituresJson.monthTotal)
    $('#previous_month_total').html(dateExpendituresJson.previousMonthTotal)
    $('#this_vs_previous_total').html(dateExpendituresJson.monthTotal - dateExpendituresJson.previousMonthTotal)
    $('#this_vs_previous_percentage').html(getThisVsPreviousPercentage(dateExpendituresJson))
    $('#this_year_total').html(dateExpendituresJson.yearTotal)
    $('#this_year_monthly_average').html(getThisYearMonthlyAverage(dateExpendituresJson))
}