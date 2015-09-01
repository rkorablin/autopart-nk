Orders = {
	
		ajaxInProgress : false,
		
		rollbackTable : function(itemId) {
			var $table = $(".admin-order-table");
			var $tr = $table.find("tr[data-item-id=" + itemId + "]");
			var $acceptedQuantityInput = $tr.find(".accepted-quantity > input"); 
			$acceptedQuantityInput.val($acceptedQuantityInput.attr("data-prev-value"));
			var $rejectedQuantityInput = $tr.find(".rejected-quantity > input"); 
			$rejectedQuantityInput.val($rejectedQuantityInput.attr("data-prev-value"));
		},
		
		refreshTable : function(order, itemId) {
			
			var $table = $(".admin-order-table");
			var $tr = $table.find("tr[data-item-id=" + itemId + "]");
			var $total = $table.find("tr.total");
			var item = $.grep(order.items, function(item, index) {
				return item.id == itemId;
			})[0];
			
			$tr.find(".accepted-quantity > input").val(item.acceptedQuantity).attr("data-prev-value", item.acceptedQuantity);
			$tr.find(".rejected-quantity > input").val(item.rejectedQuantity).attr("data-prev-value", item.rejectedQuantity);
			
			$tr.find(".accepted-cost").html(item.acceptedCost.toFixed(2));
			$tr.find(".rejected-cost").html(item.rejectedCost.toFixed(2));
			
			$total.find(".accepted-cost").html(order.acceptedCost.toFixed(2));
			$total.find(".rejected-cost").html(order.rejectedCost.toFixed(2));
			
		},
		
		doAjax : function(params) {
			if (!Orders.ajaxInProgress) {
				Orders.ajaxInProgress = true;
				waitingDialog.show('Сохранение изменений', {dialogSize: 'sm', progressType: 'info'});
				var $csrf = $("#csrf");
				var csrfName = $csrf.attr("name");
				var csrfValue = $csrf.val();
				params.data[csrfName] = csrfValue;
				$.ajax({
					type : "post",
					dataType : "json",
					data : params.data,
					url : params.url,
					success : function(response) {
						if (params.success) {
							params.success(response);
						}
					},
					error : function() {
						if (params.error) {
							params.error();
						}
					},
					complete : function() {
						Orders.ajaxInProgress = false;
						waitingDialog.hide();
					}
				});
			}
		},
		
		doOrderItemQuantityAjax : function(params) {
			
			params.success = function(response) {
				if (response.result == "error") {
					$.notify({
						message: response.message 
					},{
						type: "danger"
					});
					Orders.rollbackTable(params.data.itemId);
				} else {
					Orders.refreshTable(response, params.data.itemId);
				}
			};
			Orders.doAjax(params);
			
		},
		
		setOrderItemAcceptedQuantity : function(itemId, quantity) {
			
			Orders.doOrderItemQuantityAjax({
				data : {
					itemId : itemId,
					quantity : quantity
				}, 
				url : "/admin/set_order_item_accepted_quantity"
			});
		},
		
		setOrderItemRejectedQuantity : function(itemId, quantity) {
			
			Orders.doOrderItemQuantityAjax({
				data :{
					itemId : itemId,
					quantity : quantity
				},
				url: "/admin/set_order_item_rejected_quantity"
			});
		},
		
		incOrderItemAcceptedQuantity : function(itemId) {
			
			Orders.doOrderItemQuantityAjax({
				data : {
					itemId : itemId
				},
				url : "/admin/inc_order_item_accepted_quantity"
			});
			
		},
		
		decOrderItemAcceptedQuantity : function(itemId) {
			
			Orders.doOrderItemQuantityAjax({
				data : {
					itemId : itemId
				},
				url :"/admin/dec_order_item_accepted_quantity"
			});
			
		},
		
		incOrderItemRejectedQuantity : function(itemId) {
			
			Orders.doOrderItemQuantityAjax({
				data : {
					itemId : itemId
				},
				url : "/admin/inc_order_item_rejected_quantity"
			});
			
		},
		
		decOrderItemRejectedQuantity : function(itemId) {
			
			Orders.doOrderItemQuantityAjax({
				data : {
					itemId : itemId
				},
				url : "/admin/dec_order_item_rejected_quantity"
			});
			
		},
		
		setOrderItemStatus : function(itemId, status) {
			
			Orders.doAjax({
				data : {
					itemId : itemId,
					status : status
				},
				url : "/admin/set_order_item_status",
				success : function(item) {
					$.notify({
						message: 'Статус позиции заказа успешно изменен' 
					},{
						type: 'success'
					});
				}
			});
			
		},
		
		setOrderStatus : function(orderId, status) {
			
			Orders.doAjax({
				data : {
					orderId : orderId,
					status : status
				},
				url : "/admin/set_order_status",
				success : function(item) {
					$.notify({
						message: 'Статус заказа успешно изменен' 
					},{
						type: 'success'
					});
				}
			});
			
		}
		
};

$(document).ready(function() {
	
	$(".admin-order-table").on("click", "[data-action='inc-order-item-accepted-quantity']", function() {
		Orders.incOrderItemAcceptedQuantity($(this).attr("data-item-id"));
		return false;
	});

	$(".admin-order-table").on("click", "[data-action='dec-order-item-accepted-quantity']", function() {
		Orders.decOrderItemAcceptedQuantity($(this).attr("data-item-id"));
		return false;
	});
	
	$(".admin-order-table").on("click", "[data-action='inc-order-item-rejected-quantity']", function() {
		Orders.incOrderItemRejectedQuantity($(this).attr("data-item-id"));
		return false;
	});

	$(".admin-order-table").on("click", "[data-action='dec-order-item-rejected-quantity']", function() {
		Orders.decOrderItemRejectedQuantity($(this).attr("data-item-id"));
		return false;
	});
	
	$(".admin-order-table").find("[data-action='set-order-item-accepted-quantity'], [data-action='set-order-item-rejected-quantity']").keydown(function(e) {
		if ($.inArray(e.keyCode, [46, 8, 9, 27, 13, 110, 190]) !== -1 || (e.keyCode == 65 && ( e.ctrlKey === true || e.metaKey === true ) ) || (e.keyCode >= 35 && e.keyCode <= 40)) {
			return;
	     }
        if ((e.shiftKey || (e.keyCode < 48 || e.keyCode > 57)) && (e.keyCode < 96 || e.keyCode > 105)) {
            e.preventDefault();
        }
	}).keyup(function(e) {
		var quantity = parseInt($(this).val());
		if (isNaN(quantity)) {
			quantity = 0;
		}
		var itemId = $(this).attr("data-item-id");
		var action = $(this).attr("data-action");
		if (action == "set-order-item-accepted-quantity") {
			Orders.setOrderItemAcceptedQuantity(itemId, quantity);
		} else if (action == "set-order-item-rejected-quantity") {
			Orders.setOrderItemRejectedQuantity(itemId, quantity);
		}	
	}).blur(function() {
		var $this = $(this);
		if ($this.val() == "") {
			$this.val(0);
		}
	});
	
	$("[data-action='set-order-item-status']").change(function() {
		var $this = $(this);
		Orders.setOrderItemStatus($this.attr("data-item-id"), $this.val());
	});
	
	$("[data-action='set-order-status']").change(function() {
		var $this = $(this);
		Orders.setOrderStatus($this.attr("data-order-id"), $this.val());
	});
	
});