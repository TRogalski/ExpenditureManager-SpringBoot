$('#modal_delete').on('show.bs.modal', function (e) {
    var expenditureId = $(e.relatedTarget).data('expenditure-id');
    $(e.currentTarget).find('input[id="id"]').val(expenditureId);

    $(e.currentTarget).find('input[id="redirectPage"]').val(window.location.pathname);
});
