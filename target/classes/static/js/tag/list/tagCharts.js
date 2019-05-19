document.addEventListener('DOMContentLoaded', function () {
    $.ajax({
        'async': true,
        'url': window.location.origin + "/expenditure/test/" + getTodaysDate(),
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
            labels: Object.keys(jsonData.Jan),
            datasets: [
                {
                    label: 'Jan',
                    data: Object.values(jsonData.Jan),
                    backgroundColor: 'rgba(170,164,225,.4)'
                },
                {
                    label: 'Feb',
                    data: Object.values(jsonData.Feb),
                    backgroundColor: 'rgba(155,27,92,.4)'
                },
                {
                    label: 'Mar',
                    data: Object.values(jsonData.Mar),
                    backgroundColor: 'rgba(106,102,140,.4)'
                },
                {
                    label: 'Apr',
                    data: Object.values(jsonData.Apr),
                    backgroundColor: 'rgba(203,102,234,.4)'
                },
                {
                    label: 'May',
                    data: Object.values(jsonData.May),
                    backgroundColor: 'rgba(66,51,166,.4)'
                },
                {
                    label: 'Jun',
                    data: Object.values(jsonData.Jun),
                    backgroundColor: 'rgba(222,113,85,.4)'
                },
                {
                    label: 'Jul',
                    data: Object.values(jsonData.Jul),
                    backgroundColor: 'rgba(138,27,7,.4)'
                },
                {
                    label: 'Aug',
                    data: Object.values(jsonData.Aug),
                    backgroundColor: 'rgba(199,164,144,.4)'
                },
                {
                    label: 'Sep',
                    data: Object.values(jsonData.Sep),
                    backgroundColor: 'rgba(241,192,57,.4)'
                },
                {
                    label: 'Oct',
                    data: Object.values(jsonData.Oct),
                    backgroundColor: 'rgba(247,57,58,.4)'
                },
                {
                    label: 'Nov',
                    data: Object.values(jsonData.Nov),
                    backgroundColor: 'rgba(255,0,135,.4)'
                },
                {
                    label: 'Dec',
                    data: Object.values(jsonData.Dec),
                    backgroundColor: 'rgba(171,123,5,.4)'
                },
            ]
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
            },
            legend:{
                position:'right'
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

