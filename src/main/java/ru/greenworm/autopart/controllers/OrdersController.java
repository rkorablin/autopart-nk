package ru.greenworm.autopart.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ru.greenworm.autopart.dto.Paginator;
import ru.greenworm.autopart.model.Setting;
import ru.greenworm.autopart.model.order.Order;
import ru.greenworm.autopart.model.user.User;
import ru.greenworm.autopart.services.OrdersService;
import ru.greenworm.autopart.services.SettingsService;
import ru.greenworm.autopart.utils.JsonUtils;
import ru.greenworm.autopart.utils.SecurityUtils;

@Controller
public class OrdersController {

	@Autowired
	private OrdersService ordersService;

	@Autowired
	private SettingsService settingsService;

	@RequestMapping("/cart")
	public String showCartPage(Model model) {
		return "cart";
	}

	@RequestMapping(value = "/create_order.json", method = RequestMethod.POST, headers = { "Content-type=application/json" })
	public @ResponseBody String createRequest(@RequestBody Order order) {
		ordersService.createOrder(order);
		return JsonUtils.getSuccessJson();
	}

	@RequestMapping("/orders")
	public String showOrdersPage(Model model, @RequestParam(value = "page", defaultValue = "1") int page) {
		User user = SecurityUtils.getCurrentUser();
		int perPage = settingsService.getAsInteger(Setting.PER_PAGE);
		model.addAttribute("orders", ordersService.getOrders(user, page, perPage));
		model.addAttribute("paginator", new Paginator(ordersService.getOrdersCount(user), perPage, "/admin/orders", page));
		return "orders";
	}

	@RequestMapping("/order/{id}")
	public String showOrderPage(Model model, @PathVariable("id") long id) {
		Order order = ordersService.getOrder(id);
		model.addAttribute("order", order);
		model.addAttribute("items", order.getItems());
		return "order";
	}

}
