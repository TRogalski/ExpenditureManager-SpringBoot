document.addEventListener('DOMContentLoaded', function () {

    $('#date_picker').datepicker({
        format: 'yyyy-mm-dd',
    }).datepicker("setDate", 'now');


    $('#date_picker').on('changeDate', function () {

        //change hidden value on click  - not needed
        // $('#date_picker_input').val(
        //     $('#date_picker').datepicker('getFormattedDate')
        // );

        getExpendituresAssignedToDate($('#date_picker').datepicker('getFormattedDate'));

        console.log("zmieniono date!");
    });


})


// var expenditure_records = document.getElementById("expenditure_records");
//
// expenditure_records.addEventListener('click', function () {
//     console.log("kliknieto mnie!")
// })


function getExpendituresAssignedToDate(date) {
    fetch("http://localhost:8084/expenditure/date/" + date).then(function (response) {
        return response.json();
    }).then(function (dateExpendituresJson) {
        console.log(JSON.stringify(dateExpendituresJson));
        appendReceivedElements(dateExpendituresJson)
    });

    removeEnlistedElements()
}

function removeEnlistedElements() {
    var toDelete = document.getElementById("expenditure_records");
    while (toDelete.hasChildNodes()) {
        toDelete.removeChild(toDelete.lastChild);
    }
}

function appendReceivedElements(dateExpendituresJson){
    for(var i=0;i<dateExpendituresJson.length;i++){
        console.log(dateExpendituresJson[i])
    }
}