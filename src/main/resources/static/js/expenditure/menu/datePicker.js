document.addEventListener('DOMContentLoaded', function () {

    var totalTimeSeries = (function () {
        var json = null;
        $.ajax({
            'async': false,
            'global': false,
            'url': "http://localhost:8084/expenditure/stats/" + getTodaysDate(),
            'dataType': "json",
            'success': function (data) {
                json = data;
            }
        });
        return json.totalTimeSeries;
    })();

    $('#date_picker').datepicker({
        format: "yyyy-mm-dd",
        beforeShowDay: function (date) {
            var d = date;
            var curr_date = d.getDate();
            var curr_month = d.getMonth() + 1; //Months are zero based
            var curr_year = d.getFullYear();

            if (curr_date < 10) {
                curr_date = '0' + curr_date
            }

            if (curr_month < 10) {
                curr_month = '0' + curr_month
            }

            var formattedDate = curr_year + "-" + curr_month + "-" + curr_date


            if ($.inArray(formattedDate, Object.keys(totalTimeSeries)) != -1) {
                return {
                    classes: 'highlight',
                    tooltip: totalTimeSeries[formattedDate]
                };
            }
            return;
        }
    }).datepicker("setDate", 'now');

    $('#date_picker').on('changeDate', function () {
        getExpendituresAssignedToDate($('#date_picker').datepicker('getFormattedDate'));
    });

    $('#add_expenditure').on('click', function () {
        window.location.href = "/expenditure/add/" + $('#date_picker').datepicker('getFormattedDate');
    });
})

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


//Show elements based on element clicked

function getExpendituresAssignedToDate(date) {
    fetch("http://localhost:8084/expenditure/date/" + date).then(function (response) {
        return response.json();
    }).then(function (dateExpendituresJson) {
        // console.log(JSON.stringify(dateExpendituresJson));

        var toDelete = document.getElementById("expenditure_records");

        if (toDelete != null) {
            removeEnlistedExpenditures(toDelete)
        }

        appendReceivedElements(dateExpendituresJson)
    });
}

function removeEnlistedExpenditures(toDelete) {

    while (toDelete.hasChildNodes()) {
        toDelete.removeChild(toDelete.lastChild);
    }
}

//Display expenditures assigned to date
function appendReceivedElements(dateExpendituresJson) {

    for (var i = 0; i < dateExpendituresJson.length; i++) {
        var tableRow = $(`<tr>
                            <td>${convertTagListToString(dateExpendituresJson[i].tags)}</td>
                            <td>${dateExpendituresJson[i].name}</td>
                            <td>${dateExpendituresJson[i].amount.toFixed(2)}</td>
                            <td>${dateExpendituresJson[i].description}</td>
                            <td>${dateExpendituresJson[i].created}</td>
                            <td>
                                <a href="#modal_delete" data-toggle="modal" 
                                data-expenditure-id="${dateExpendituresJson[i].id}">
                                    <span class="glyphicon glyphicon-remove"></span>
                                </a>
                                <a href="/expenditure/edit/${dateExpendituresJson[i].id}">
                                    <span class="glyphicon glyphicon-pencil"></span>
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

