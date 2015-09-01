$(document).ready(function() {

	function showItem(item) {
		var $tr = $($.Mustache.render('request-item-template', item));
		$tr.hide();
		$("table.request-table tbody tr.add").before($tr);
		$tr.fadeIn();
	}
	
	function loadRequest() {
		$("table.request-table tbody tr.request-item").remove();
		var request = $.localStorage.get("request");
		if (request && request.items && request.items.length > 0) {
			$.each(request.items, function(index, item) {
				showItem(item);	
			});
		} else {
			showItem();
			showItem();
			showItem();
			showItem();
			showItem();
		}
		$("textarea[name='comment']").val(request ? request.comment : "");
	}
	
	function getItems() {
		var items = [];
		$.each($("table.request-table tbody tr.request-item"), function(index, tr) {
			var $tr = $(tr);
			var item = {};
			var code = $tr.find("input[name='code']").val();
			var name = $tr.find("input[name='name']").val();
			var vin = $tr.find("input[name='vin']").val();
			if (code != "" || name != "" || vin != "") {
				item.code = code;
				item.name = name;
				item.vin = vin;
				items.push(item);
			}
		});
		return items;
	}
	
	function getComment() {
		return $("textarea[name='comment']").val();
	}
	
	function saveRequest() {
		var request = {};
		request.items = getItems();
		request.comment = getComment();
		$.localStorage.set("request", request);
	}
	
	function clearRequest() {
		$.localStorage.remove("request");
		loadRequest();
	}
	
	var requestInProgress = false;
	
	function createRequest() {
		
		if (!requestInProgress) {
			requestInProgress = true;
			var data = {};
			var $csrf = $("#csrf");
			data.comment = $("textarea[name='comment']").val();
			data.items = getItems();
			waitingDialog.show('Создание запроса', {dialogSize: 'sm', progressType: 'info'});
			$.ajax({
				type : "post",
				dataType : "json",
				contentType : "application/json",
				data : JSON.stringify(data),
				url : "/create_request.json?" + $csrf.attr("name") + "=" + $csrf.val(),
				success : function() {
					clearRequest();
					$.notify({
						message: 'Заявка успешно сохранена, наши сотрудники свяжутся с Вами в ближайшее время' 
					},{
						type: 'success'
					});
				},
				error : function() {
					$.notify({
						message: 'Ошибка связи с сервером, попробуйте позже или обратитесь к администратору сайта' 
					},{
						type: 'danger'
					});
				},
				complete : function() {
					requestInProgress = false;
					waitingDialog.hide();
				}			
			});
		}
	}
	
	$("table.request-table").on("keyup", "input", function() {
		saveRequest();
	});
	
	$("textarea[name='comment']").on("keyup", function() {
		saveRequest();
	});
	
	$("table.request-table tbody tr.add a").click(function() {
		showItem();
		return false;
	});
	
	$("table.request-table tbody").on("click", "a.delete", function() {
		var $tr = $(this).closest("tr");
		$tr.fadeOut(function() {
			$tr.remove();
			saveRequest();
		});
		return false;
	});
	
	$("#send-button").click(function() {
		createRequest();
		return false;
	});
	
	$.Mustache.addFromDom('request-item-template');
	loadRequest();
	
});