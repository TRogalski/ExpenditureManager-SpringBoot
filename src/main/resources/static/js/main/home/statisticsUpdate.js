function getExpenditureStatistics(date) {
    fetch("http://localhost:8084/expenditure/stats/" + date).then(function (response) {
        return response.json();
    }).then(function (expenditureStatisticsJson) {
        console.log(expenditureStatisticsJson.timeSeries);
    });
}