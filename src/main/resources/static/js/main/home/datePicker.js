document.addEventListener('DOMContentLoaded', function () {
    $('#date_picker').datepicker({
        viewMode: "months",
        minViewMode: "months",
        format: "yyyy-mm"
    }).datepicker("setDate", 'now');

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

            appendReceivedTags(jsonData)
            fillInStatistics(jsonData)
        }
    })
};


function appendReceivedTags(jsonData) {
    var tagTotals = jsonData.currentMonthTagTotals;

//    tagTotals.sort(function (a, b) {
//        return b.total - a.total;
//    });

    for (var i = 0; i < tagTotals.length; i++) {
        var listElement = $(`<tr>
                                <td>${tagTotals[i].name}</td>
                                <td>${formatNumber(tagTotals[i].total)}</td>
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

function markInfoBoxBasedOnMonthOverMonthChange(jsonData){

    $('#expenditure_change_info_card').removeClass('info-card info-card-success info-card-failure');

    if((jsonData.currentMonthTotal-jsonData.previousMonthTotal)>0){
        $('#expenditure_change_info_card').addClass('info-card-failure');
    } else if((jsonData.currentMonthTotal-jsonData.previousMonthTotal)<0){
        $('#expenditure_change_info_card').addClass('info-card-success');
    } else {
        $('#expenditure_change_info_card').addClass('info-card');
    }
}

function fillInStatistics(jsonData) {
    $('#this_month_total').html(jsonData.currentMonthTotal == null ? 0 : formatNumber(jsonData.currentMonthTotal))
    $('#previous_month_total').html(jsonData.previousMonthTotal == null ? 0 : formatNumber(jsonData.previousMonthTotal))
    $('#this_vs_previous_total').html(jsonData.currentVsPreviousMonthNominalChange)
    $('#this_vs_previous_percentage').html(jsonData.currentVsPreviousMonthPercentageChange)
    $('#this_year_total').html(formatNumber(jsonData.currentYearTotal))
    $('#this_year_monthly_average').html(formatNumber(getThisYearMonthlyAverage(jsonData)))
    $('#this_month_name').html(jsonData.currentMonthName)
    $('#this_vs_previous_month_names').html('Compared to '+jsonData.previousMonthName)
    $('#this_year_name').html(jsonData.currentYearName)
    markInfoBoxBasedOnMonthOverMonthChange(jsonData);
}


function formatNumber(num) {
    return isNaN(num)?"N/A":num.toString().replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1,')
}