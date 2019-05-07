document.addEventListener('DOMContentLoaded', function () {
    $('#date_picker').datepicker({
        format:  "yyyy-mm-dd",
    }).datepicker("setDate", 'now');

    console.log($('#date_picker').datepicker('getFormattedDate'))
    getTopTagsAssignedToMonth($('#date_picker').datepicker('getFormattedDate'))

    $('#date_picker').on('changeDate', function () {
        getTopTagsAssignedToMonth($('#date_picker').datepicker('getFormattedDate'));
    });
})

//display top tags

function getTopTagsAssignedToMonth(date) {
    fetch("http://localhost:8084/expenditure/stats/" + date).then(function (response) {
        return response.json();
    }).then(function (dateExpendituresJson) {

        console.log(dateExpendituresJson)
        var toDelete = document.getElementById("top_tags_records");

        if (toDelete != null) {
            removeEnlistedTags(toDelete)
        }

        appendReceivedTags(dateExpendituresJson)
    });

}

function removeEnlistedTags(toDelete) {

    while (toDelete.hasChildNodes()) {
        toDelete.removeChild(toDelete.lastChild);
    }
}

function appendReceivedTags(dateExpendituresJson) {
    for (var i = 0; i < dateExpendituresJson.topTags.length; i++) {
        var listElement = $(`<tr>
                                <td>${dateExpendituresJson.topTags[i].name}</td>
                                <td>${dateExpendituresJson.topTags[i].monthTotal}</td>
                                <td>${(dateExpendituresJson.topTags[i].monthTotal / dateExpendituresJson.monthTotal * 100).toFixed(2)}</td>
                                <td>
                                    <a href="http://localhost:8084/expenditure/list/${dateExpendituresJson.date}/${dateExpendituresJson.topTags[i].id}">
                                        ${dateExpendituresJson.topTags[i].monthCount}
                                    </a>
                                </td>
                            </tr>`);
        $("#top_tags_records").append(listElement)
    }
}