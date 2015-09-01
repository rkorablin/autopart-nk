$(document).ready(function() {
	var $csrf = $("#csrf");
	$.each($(".admin-categories-table tr[data-category-id]"), function(index, tr) {
		var $tr = $(tr);
		var id = $tr.attr("data-category-id");
		var $input = $tr.find("input[name='file']");
		$input.fileupload({
	        url: "/admin/upload_category_image?" + $csrf.attr("name") + "=" + $csrf.val() + "&id=" + id,
	        add: function (e, data) {
	        	waitingDialog.show('Загрузка изображения', {dialogSize: 'sm', progressType: 'info'});
	            var jqXHR = data.submit()
	                .success(function (result, textStatus, jqXHR) {
						$.notify({
							message: 'Изображение загружено' 
						},{
							type: 'success'
						});
	                })
	                .error(function (jqXHR, textStatus, errorThrown) {
						$.notify({
							message: 'Ошибка загрузки изображения' 
						},{
							type: 'danger'
						});
	                })
	                .complete(function (result, textStatus, jqXHR) {
	                	waitingDialog.hide();
	                });
	        }
	    });
	});
});