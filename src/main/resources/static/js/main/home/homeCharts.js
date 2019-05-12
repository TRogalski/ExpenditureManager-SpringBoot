document.addEventListener("DOMContentLoaded", function () {

    var todaysDate = $('#date_picker').datepicker('getFormattedDate');

    var jsonData = getJsonExpenditureStatistics(todaysDate);

    var expendituresMonthlyChart = createMonthlyExpendituresChart(jsonData);
    var expendituresDailyChart = createDailyExpendituresChart(jsonData);

    $('#date_picker').on('changeDate', function () {
        var datePickerDate = $('#date_picker').datepicker('getFormattedDate');
        updateDailyExpendituresChart(expendituresDailyChart, datePickerDate);
        updateMonthlyExpendituresChart(expendituresMonthlyChart, datePickerDate);
    });
})

function createMonthlyExpendituresChart(jsonData) {

    var ctx = document.getElementById('myChart').getContext('2d');

    return new Chart(ctx, {
        type: 'bar',
        data: {
            labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
            datasets: [{
                label: "Total",
                backgroundColor: 'rgb(175,188,201)',
                borderColor: 'rgb(255, 99, 132)',
                data: jsonData.timeSeries
            }]
        },
        options: {
            legend: {
                display: false
            },
            title: {
                display: true,
                text: "Expenditures as of " + jsonData.currentYear,
            },
            fontsize: 8
        }
    });
}


function createDailyExpendituresChart(jsonData) {
    var ctx2 = document.getElementById('myChart2').getContext('2d');

    return new Chart(ctx2, {
        type: 'line',
        data: {
            labels: Object.keys(jsonData.currentMonthTotalsTimeSeries),
            datasets: [{
                data: Object.values(jsonData.currentMonthTotalsTimeSeries),
                fill: false,
                borderColor: "rgb(71, 102, 153)",
            }],
        },
        options: {
            legend: {
                display: false
            },
            title: {
                display: true,
                text: "Daily expenditures as of " + jsonData.date,
            }
        }
    });
}

function getJsonExpenditureStatistics(date) {
    var json = null;
    $.ajax({
        'async': false,
        'global': false,
        'url': "http://localhost:8084/expenditure/stats/" + date + "-01",
        'dataType': "json",
        'success': function (data) {
            json = data;
        }
    });
    return json;
}

function updateMonthlyExpendituresChart(chart, date) {
    var jsonData = getJsonExpenditureStatistics(date);

    chart.data.datasets[0].data = jsonData.timeSeries;
    chart.options.title.text = "Expenditures as of " + jsonData.currentYear;
    chart.update();

}

function updateDailyExpendituresChart(chart, date) {
    var jsonData = getJsonExpenditureStatistics(date);

    chart.data.labels=Object.keys(jsonData.currentMonthTotalsTimeSeries);
    chart.data.datasets[0].data = Object.values(jsonData.currentMonthTotalsTimeSeries);
    chart.options.title.text = "Daily expenditures as of " + jsonData.date,
    chart.update();
}
