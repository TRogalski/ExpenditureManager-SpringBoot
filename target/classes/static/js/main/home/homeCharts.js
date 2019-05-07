document.addEventListener("DOMContentLoaded",function(){

    updateCharts($('#date_picker').datepicker('getFormattedDate'))

    $('#date_picker').on('changeDate', function () {
        updateCharts($('#date_picker').datepicker('getFormattedDate'));
    });

})

function updateCharts(date){

    var ctx = document.getElementById('myChart').getContext('2d');

    fetch("http://localhost:8084/expenditure/stats/" + date).then(function (response) {
        return response.json();
    }).then(function (expenditureStatisticsJson) {

        var chart = new Chart(ctx, {
            // The type of chart we want to create
            type: 'bar',

            // The data for our dataset
            data: {
                labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul','Aug','Sep','Oct','Nov','Dec'],
                datasets: [{
                    label:"Total",
                    backgroundColor: 'rgb(255, 99, 132)',
                    borderColor: 'rgb(255, 99, 132)',
                    data: expenditureStatisticsJson.timeSeries
                }]
            },

            // Configuration options go here
            options: {
                maintainAspectRatio: true,
                responsive: true,
                events: ['click'],
                legend:{
                    display:false
                },
                title: {
                    display: true,
                    text: "Expenditures as of " + expenditureStatisticsJson.currentYear,

                },
                fontsize:8

            }
        });

    });


}

