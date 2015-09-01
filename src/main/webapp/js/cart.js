Cart = {
	
		getEmpty : function() {
			var cart = {};
			cart.items = [];
			cart.cost = 0.00;
			return cart;
		},
		
		load : function() {
			var cart = $.localStorage.get("cart");
			if (!cart) {
				cart = Cart.getEmpty();
			}
			return cart;
		},
		
		show : function(cart) {
			$("#header-cart-link span").html("Корзина (" + cart.items.length + ")");
		},
		
		showTable : function(cart) {
			
			var $table = $("#cart-table");
			if ($table.length > 0) {
				var ids = [];
				$.each(cart.items, function(index, item) {
					if (!item.product) {
						ids.push(item.productId);
					}
				});
				if (ids.length > 0) {
					$.ajax({
						type : "get",
						dataType : "json",
						url : "/catalog/products.json",
						data : {
							id : ids
						},
						success : function(products) {
							$.each(products, function(i, product) {
								$.each(cart.items, function(j, item) {
									if (item.productId == product.id) {
										item.product = product;
									}
								});
							});
							Cart.save(cart);
							Cart.renderTable($table, cart);
						},
						error : function() {
							
						}
					});
				} else {
					Cart.renderTable($table, cart);
				}
			}
		},
		
		renderTable : function($table, cart) {
			$table.find("tbody").find("tr:not(.total)").attr("data-exists", "false")
			var $total = $table.find("tbody").find("tr.total")
			cart.cost = 0.00;
			if (cart.items.length > 0) {
				$.each(cart.items, function(index, item) {
					item.cost = item.product.price * item.quantity;
					cart.cost += item.cost;
					item.cost = item.cost.toFixed(2);					

					var $tr = $table.find("tr[data-product-id='" + item.product.id + "']");
					if ($tr.length > 0) {
//						$tr.replaceWith($newTr);
						$tr.find(".td-quantity > input").val(item.quantity > 0 ? item.quantity : "");
						$tr.find(".td-price").html(item.product.price);
						$tr.find(".td-cost").html(item.cost);
						$tr.attr("data-exists", "true");
					} else {
						var newTr = Mustache.render($("#cart-item-template").html(), item);
						var $newTr = $(newTr);
						$newTr.attr("data-exists", "true");
						$total.before($newTr);
					}
				});
				cart.cost = cart.cost.toFixed(2);
				$total.find(".td-cost").html(cart.cost);
			} else {
				$total.before($("#empty-cart-item-template").html());
				$total.find(".td-cost").html("0.00");
			}
			
			var $trsForDeletion = $table.find("tr[data-exists='false']"); 
			$trsForDeletion.fadeOut(function() {
				$trsForDeletion.remove();
			});
			
		},
		
		save : function(cart) {
			$.localStorage.set("cart", cart);
		},
		
		clear : function() {
			var cart = Cart.getEmpty();
			Cart.save(cart);
			Cart.show(cart);
			Cart.showTable(cart);
		},
		
		addItem : function(productId) {
			var cart = Cart.load();
			var found = false;
			$.each(cart.items, function(index, item) {
				if (item.productId == productId) {
					item.quantity = item.quantity + 1; 
					found = true;
				}
			});
			if (!found) {
				var item = {};
				item.productId = productId;
				item.quantity = 1;
				cart.items.push(item);
			}
			Cart.save(cart);
			Cart.show(cart);
			$.notify("Товар добавлен в корзину");
			return cart;
		},
		
		deleteItem : function(productId) {
			var cart = Cart.load();
			cart.items = $.grep(cart.items, function(item, index) {
				return item.productId != productId;
			});
			Cart.save(cart);
			Cart.show(cart);
			Cart.showTable(cart);
			$.notify("Товар удален из корзины");
			return cart;
		},
		
		setItemQuantity : function(productId, quantity) {
			var cart = Cart.load();
			$.each(cart.items, function(index, item) {
				if (item.productId == productId) {
					item.quantity = quantity;
				}
			});
			Cart.save(cart);
			Cart.show(cart);
			Cart.showTable(cart);
			return cart;
		},

		incItemQuantity : function(productId) {
			var cart = Cart.load();
			$.each(cart.items, function(index, item) {
				if (item.productId == productId) {
					item.quantity = item.quantity + 1;
				}
			});
			Cart.save(cart);
			Cart.show(cart);
			Cart.showTable(cart);
			return cart;			
		},
		
		decItemQuantity : function(productId) {
			var cart = Cart.load();
			$.each(cart.items, function(index, item) {
				if (item.productId == productId) {
					if (item.quantity > 1) {
						item.quantity = item.quantity - 1;
						Cart.save(cart);
						Cart.show(cart);
						Cart.showTable(cart);
						return cart;
					} else {
						$.notify({
							message: 'Нельзя установить количество меньше 1' 
						},{
							type: 'danger'
						});
					}
				}
			});
		},
		
		createOrder : function() {
			var order = {};
			var cart = Cart.load();
			var $csrf = $("#csrf");
			var $form = $("#order-data");
			
			order.comment = $form.find("[name='comment']").val();
			order.receivingMethod = $form.find("[name='receivingMethod']").val();
			
			if (order.receivingMethod == "DELIVERY") {
				order.address = {};
				order.address.region = $form.find("[name='address.region']").val();
				order.address.city = $form.find("[name='address.city']").val();
				order.address.street = $form.find("[name='address.street']").val();
				order.address.building = $form.find("[name='address.building']").val();
				order.address.room = $form.find("[name='address.room']").val();
				order.address.zipcode = $form.find("[name='address.zipcode']").val();
			}
			
			order.items = [];
			$.each(cart.items, function(index, item) {
				order.items.push({
					product : {
						id : item.product.id
					},
					orderedQuantity : item.quantity
				});
			});
			waitingDialog.show('Создание заказа', {dialogSize: 'sm', progressType: 'info'});
			$.ajax({
				type : "post",
				dataType : "json",
				contentType : "application/json",
				url : "/create_order.json?" + $csrf.attr("name") + "=" + $csrf.val(),
				data : JSON.stringify(order),
				success : function() {
					$.notify({
						message: 'Заказ создан' 
					},{
						type: 'success'
					});
					Cart.clear();
					window.location = "/orders";
				},
				error : function() {
					$.notify({
						message: 'Ошибка создания заказа' 
					},{
						type: 'danger'
					});
				},
				complete : function() {
					waitingDialog.hide();
				}
			});
		}
		
};

$(document).ready(function() {
	var cart = Cart.load(); 
	Cart.show(cart);
	Cart.showTable(cart);
	
	$("[data-action='add-to-cart']").click(function() {
		var productId = $(this).attr("data-product-id");
		Cart.addItem(productId);
		return false;
	});

	if ($("#cart-table").length > 0) {
	
		$("#cart-table").on("click", "[data-action='delete-cart-item']", function() {
			var productId = $(this).attr("data-product-id");
			Cart.deleteItem(productId);
			return false;
		});

		$("#cart-table").on("click", "[data-action='inc-cart-item']", function() {
			var productId = $(this).attr("data-product-id");
			Cart.incItemQuantity(productId);
			return false;
		});
		
		$("#cart-table").on("click", "[data-action='dec-cart-item']", function() {
			var productId = $(this).attr("data-product-id");
			Cart.decItemQuantity(productId);
			return false;
		});

		$("#cart-table").on("keydown", "[data-action='set-cart-item-quantity']", function(e) {
			
			if ($.inArray(e.keyCode, [46, 8, 9, 27, 13, 110, 190]) !== -1 ||
				//Allow: Ctrl+A, Command+A
				(e.keyCode == 65 && ( e.ctrlKey === true || e.metaKey === true ) ) || 
				// Allow: home, end, left, right, down, up
				(e.keyCode >= 35 && e.keyCode <= 40)) {
				// let it happen, don't do anything
				return;
		     }
	        // Ensure that it is a number and stop the keypress
	        if ((e.shiftKey || (e.keyCode < 48 || e.keyCode > 57)) && (e.keyCode < 96 || e.keyCode > 105)) {
	            e.preventDefault();
	        }
		}).on("keyup", "[data-action='set-cart-item-quantity']", function(e) {
			var quantity = parseInt($(this).val());
			if (isNaN(quantity)) {
				quantity = 0;
			}
			var productId = $(this).attr("data-product-id");
			Cart.setItemQuantity(productId, quantity);
		}).on("blur", "[data-action='set-cart-item-quantity']", function() {
			var $this = $(this);
			if ($this.val() == "") {
				$this.val(0);
			}
		});
		
		$("select[name='receivingMethod']").change(function() {
			var method = $(this).val();
			var $deliveryDiv = $("#delivery-address");
			if (method == "SELF") {
				$deliveryDiv.find(":input").val("");
				$deliveryDiv.slideUp();
			} else if (method == "DELIVERY") {
				$deliveryDiv.slideDown();
			}
		});
		
		$("#create-order-button").click(function() {
			Cart.createOrder();
			return false;
		});
	
	}
	
});