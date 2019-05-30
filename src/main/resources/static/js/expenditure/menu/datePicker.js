document.addEventListener('DOMContentLoaded', function () {

    $.ajax({
        'url': window.location.origin + "/expenditure/datepicker/" + getTodaysDate(),
        'dataType': "json",
        'success': function (jsonData) {
            $('#date_picker').datepicker({
                format: "yyyy-mm-dd",
                beforeShowDay: function (date) {
                    var d = date;
                    var curr_date = d.getDate();
                    var curr_month = d.getMonth() + 1;
                    var curr_year = d.getFullYear();

                    if (curr_date < 10) {
                        curr_date = '0' + curr_date
                    }

                    if (curr_month < 10) {
                        curr_month = '0' + curr_month
                    }

                    var formattedDate = curr_year + "-" + curr_month + "-" + curr_date

                    if ($.inArray(formattedDate, Object.keys(jsonData.monthlyTotalTimeSeries)) != -1) {
                        return {
                            classes: 'highlight',
                            tooltip: formatNumber(jsonData.monthlyTotalTimeSeries[formattedDate])
                        };
                    }
                    return;
                }
            }).datepicker("setDate", 'now');

            $('#date_picker').on('changeDate', function () {
                getExpendituresAssignedToDate($('#date_picker').datepicker('getFormattedDate'));
            });
        }
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


function getExpendituresAssignedToDate(date) {
    $.ajax({
        'url': window.location.origin + "/expenditure/date/" + date,
        'dataType': "json",
        'success': function (jsonData) {
            var toDelete = document.getElementById("expenditure_records");
            if (toDelete != null) {
                removeEnlistedExpenditures(toDelete)
            }

            appendReceivedElements(jsonData)
        }
    })
}


function removeEnlistedExpenditures(toDelete) {
    while (toDelete.hasChildNodes()) {
        toDelete.removeChild(toDelete.lastChild);
    }
}


function appendReceivedElements(jsonData) {
    for (var i = 0; i < jsonData.length; i++) {
        var tableRow = $(`<tr>
                            <td>${convertTagListToString(jsonData[i].tags)}</td>
                            <td>${jsonData[i].name}</td>
                            <td>${formatNumber(jsonData[i].amount.toFixed(2))}</td>
                            <td>${jsonData[i].description}</td>
                            <td>${jsonData[i].created}</td>
                            <td>
                                <a href="#modal_delete" data-toggle="modal" 
                                data-expenditure-id="${jsonData[i].id}">
                                    <span class="glyphicon glyphicon-remove"></span>
                                </a>
                                <a href="/expenditure/edit/${jsonData[i].id}">
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


function formatNumber(num) {
    return num.toString().replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1,')
}