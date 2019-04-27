document.addEventListener('DOMContentLoaded', function () {
    $('#date_picker').datepicker({
        format: 'yyyy-mm-dd',
    }).datepicker("setDate", 'now');


    $('#date_picker').on('changeDate', function () {
        getExpendituresAssignedToDate($('#date_picker').datepicker('getFormattedDate'));
    });

    $('#add_expenditure').on('click', function () {
        window.location = "http://localhost:8084/expenditure/add/" + $('#date_picker').datepicker('getFormattedDate');
    })

})

//Show elements based on element clicked

function getExpendituresAssignedToDate(date) {
    fetch("http://localhost:8084/expenditure/date/" + date).then(function (response) {
        return response.json();
    }).then(function (dateExpendituresJson) {
        // console.log(JSON.stringify(dateExpendituresJson));

        var toDelete = document.getElementById("expenditure_records");

        if (toDelete != null) {
            removeEnlistedElements(toDelete)
        }

        appendReceivedElements(dateExpendituresJson)
    });

}

function removeEnlistedElements(toDelete) {

    while (toDelete.hasChildNodes()) {
        toDelete.removeChild(toDelete.lastChild);
    }
}


//JSON display functions

function appendReceivedElements(dateExpendituresJson) {
    for (var i = 0; i < dateExpendituresJson.length; i++) {
        var tableRow = $(`<tr>
                            <td>${convertTagListToString(dateExpendituresJson[i].tags)}</td>
                            <td>${dateExpendituresJson[i].name}</td>
                            <td>${dateExpendituresJson[i].amount.toFixed(2)}</td>
                            <td>${dateExpendituresJson[i].description}</td>
                            <td>${dateExpendituresJson[i].created}</td>
                            <td>
                                <a th:href="expenditure/remove/${dateExpendituresJson[i].id}">
                                    <span class="glyphicon glyphicon-remove"></span>
                                </a>
                            </td>
                       </tr>`);
        $("#expenditure_records").append(tableRow)
    }
}

function convertTagListToString(tagList) {

    var formattedString = "";

    for (var i = 0; i < tagList.length; i++) {
        if (i < tagList.length - 1) {
            formattedString += tagList[i].name + "; "
        } else if (tagList.length == 0) {
            return "No tags selected"
        } else {
            formattedString += tagList[i].name
        }
    }
    return formattedString;
}

