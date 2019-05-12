document.addEventListener('DOMContentLoaded', function () {
    $.ajax({
        'async': true,
        'url': "http://localhost:8084/expenditure/stats/" + getTodaysDate(),
        'dataType': "json",
        'success': function (jsonData) {
            createTagChart(jsonData);
        }
    })
})


function createTagChart(jsonData) {
    var ctx = document.getElementById('tagChart').getContext('2d');

    var myRadarChart = new Chart(ctx, {
        type: 'radar',
        data: {
            labels: Object.keys(jsonData.tagTotals),
            datasets: [{
                label: 'Current month',
                data: Object.values(jsonData.tagTotals),
                backgroundColor: 'rgba(54, 162, 235,.4)'
            }, {
                label: 'Previous month',
                data: Object.values(jsonData.previousTagTotals),
                backgroundColor: 'rgba(255, 99, 132,.4)'
            }]
        },
        options: {
            tooltips: {
                callbacks: {
                    label: function (tooltipItem, data) {
                        var datasetLabel = data.datasets[tooltipItem.datasetIndex].label || 'Other';
                        var label = tooltipItem.yLabel;
                        return datasetLabel + ': ' + label;
                    }
                }
            }
        }
    });
}


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

