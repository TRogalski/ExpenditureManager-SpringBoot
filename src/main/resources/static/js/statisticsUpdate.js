function getTotalMonthExpenditures(date) {
    fetch("http://localhost:8084/expenditure/stats/" + date).then(function (response) {
        return response.json();
    }).then(function (dateExpendituresJson) {
        console.log(dateExpendituresJson);

    });}