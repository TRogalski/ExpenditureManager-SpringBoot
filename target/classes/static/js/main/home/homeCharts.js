document.addEventListener("DOMContentLoaded", function () {

    updateCharts($('#date_picker').datepicker('getFormattedDate')+"-01")

    $('#date_picker').on('changeDate', function () {
        updateCharts($('#date_picker').datepicker('getFormattedDate')+"-01");
    });

})

function updateCharts(date) {

    var ctx = document.getElementById('myChart').getContext('2d');
    var ctx2 = document.getElementById('myChart2').getContext('2d');

    fetch("http://localhost:8084/expenditure/stats/" + date).then(function (response) {
        return response.json();
    }).then(function (expenditureStatisticsJson) {
        var chart = new Chart(ctx, {
            // The type of chart we want to create
            type: 'bar',
            data: {
                labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
                datasets: [{
                    label: "Total",
                    backgroundColor: 'rgb(175,188,201)',
                    borderColor: 'rgb(255, 99, 132)',
                    data: expenditureStatisticsJson.timeSeries
                }]
            },
            options: {
                // maintainAspectRatio: true,
                // responsive: true,
                events: ['click'],
                legend: {
                    display: false
                },
                title: {
                    display: true,
                    text: "Expenditures as of " + expenditureStatisticsJson.currentYear,
                },
                fontsize: 8
            }
        });

        var chart2 = new Chart(ctx2, {
            // The type of chart we want to create
            type: 'line',
            data: {
                labels: Object.keys(expenditureStatisticsJson.currentMonthTotalsTimeSeries),
                datasets: [{
                    data: Object.values(expenditureStatisticsJson.currentMonthTotalsTimeSeries),
                    fill: false,
                    borderColor: "rgb(71, 102, 153)",
                }],
            },

            options: {
                // events: ['hover'],
                legend: {
                    display: false
                },
                title: {
                    display: true,
                    text: "Daily expenditures as of " + expenditureStatisticsJson.date,
                }
            }
        });


    });

}

