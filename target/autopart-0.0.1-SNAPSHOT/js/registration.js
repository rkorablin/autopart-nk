$(document).ready(function() {

	var registrationInProgress = false;
	
	function register() {
		if (!registrationInProgress) {
			registrationInProgress = true;
			waitingDialog.show('Регистрация пользователя', {dialogSize: 'm', progressType: 'info'});
			var data = $("#registration-form").serialize();
			$.ajax({
				type : "post",
				dataType : "json",
				url : "/register.json",
				data : data,
				success : function(response) {
					if (response.result == "error") {
						bootbox.alert(response.message);
						waitingDialog.hide();
						registrationInProgress = false;
					} else {
						window.location = "/activation";
					}
				},
				error : function() {
					bootbox.alert("Ошибка связи с сервером, попробуйте позже или обратитесь к администратору сайта");
					waitingDialog.hide();
					registrationInProgress = false;
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
	
	$("h1").click(function() {
		$("input[name=email]").val("rkorablin-nk@yandex.ru");
		$("input[name=plainPassword]").val("111");
		$("input[name=plainPasswordConfirm]").val("111");
		$("input[name=firstName]").val("Ростислав");
		$("input[name=secondName]").val("Кораблин");
		$("input[name=patronymicName]").val("Александрович");
		$("input[name='address.region']").val("Кемеровская область");
		$("input[name='address.city']").val("город Новокузнецк");
		$("input[name='address.street']").val("проспект Бардина");
		$("input[name='address.building']").val("25");
		$("input[name='address.room']").val("404а");
		$("input[name='address.zipcode']").val("654000");
	});
	
	$("#registration-form").submit(function() {
		register();
		return false;
	});
	
});