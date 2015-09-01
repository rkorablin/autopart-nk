$(document).ready(function() {

	var activationInProgress = false;
	
	function activate() {
		if (!activationInProgress) {
			activationInProgress = true;
			waitingDialog.show('Активация профиля', {dialogSize: 'm', progressType: 'info'});
			var data = $("#activation-form").serialize();
			$.ajax({
				type : "post",
				dataType : "json",
				url : "/activate.json",
				data : data,
				success : function(response) {
					if (response.result == "error") {
						bootbox.alert(response.message);
						waitingDialog.hide();
						activationInProgress = false;
					} else {
						window.location = "/login?activated=true";
					}
				},
				error : function() {
					bootbox.alert("Ошибка связи с сервером, попробуйте позже или обратитесь к администратору сайта");
					waitingDialog.hide();
					activationInProgress = false;
				}
			});
		}
	}
	
	$("#activation-form").submit(function() {
		activate();
		return false;
	});
	
});