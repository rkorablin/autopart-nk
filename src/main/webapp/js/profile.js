$(document).ready(function() {

	var editProfileInProgress = false;
	
	function editProfile() {
		if (!editProfileInProgress) {
			editProfileInProgress = true;
			waitingDialog.show('Сохранение изменений', {dialogSize: 'sm', progressType: 'info'});
			var data = $("#edit-profile-form").serialize();
			$.ajax({
				type : "post",
				dataType : "json",
				url : "/edit_profile.json",
				data : data,
				success : function(response) {
					if (response.result == "error") {
						bootbox.alert(response.message);
					} else {
						bootbox.alert("Изменения успешно сохранены");
					}
				},
				error : function() {
					bootbox.alert("Ошибка связи с сервером, попробуйте позже или обратитесь к администратору сайта");
				},
				complete : function() {
					waitingDialog.hide();
					editProfileInProgress = false;
				} 
			});
		}
	}
	
	$("input[name=type]").change(function() {
		var $block = $("#organization-name-block");
		if ($("input[name=type]:checked").val() == "PRIVATE_PERSON") {
			$block.find("input").val("");
			$block.slideUp();
		} else {
			$block.slideDown();
		}
	});
	
	$("#edit-profile-form").submit(function() {
		editProfile();
		return false;
	});
	
});