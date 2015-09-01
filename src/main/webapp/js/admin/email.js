$(document).ready(function() {

	function editTemplate() {
		waitingDialog.show('Сохранение изменений', {dialogSize: 'sm', progressType: 'info'});
		$.ajax({
			type : "post",
			dataType : "json",
			url : "/admin/edit_email_template.json",
			data : $("#template-form").serialize(),
			success : function(response) {
				if (response.result == "error") {
					$.notify({
						message: response.error 
					},{
						type: 'danger'
					});
				} else {
					$.notify({
						message: 'Изменения успешно сохранены' 
					},{
						type: 'success'
					});
				}
			},
			error : function() {
				$.notify({
					message: 'Ошибка связи с сервером, попробуйте позже или обратитесь к администратору сайта' 
				},{
					type: 'danger'
				});
			},
			complete : function() {
				waitingDialog.hide();
			} 
		});
	}
	
	$("#template-form").submit(function() {
		editTemplate();
		return false;
	});
	
});