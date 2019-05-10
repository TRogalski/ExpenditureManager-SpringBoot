$('#modal_delete').on('show.bs.modal', function (e) {
    var tagId = $(e.relatedTarget).data('tag-id');
    $(e.currentTarget).find('input[id="id"]').val(tagId);

    $(e.currentTarget).find('input[id="redirectPage"]').val(window.location.pathname);
});
