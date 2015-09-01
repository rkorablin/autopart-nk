package ru.greenworm.autopart.controllers;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import ru.greenworm.autopart.dto.Paginator;
import ru.greenworm.autopart.exceptions.AutopartException;
import ru.greenworm.autopart.model.EmailTemplate;
import ru.greenworm.autopart.model.Setting;
import ru.greenworm.autopart.model.catalog.Category;
import ru.greenworm.autopart.model.catalog.PriceType;
import ru.greenworm.autopart.model.order.Order;
import ru.greenworm.autopart.model.order.OrderItem;
import ru.greenworm.autopart.model.order.OrderItemStatus;
import ru.greenworm.autopart.model.order.OrderStatus;
import ru.greenworm.autopart.model.request.Request;
import ru.greenworm.autopart.model.request.RequestStatus;
import ru.greenworm.autopart.model.user.User;
import ru.greenworm.autopart.services.CatalogService;
import ru.greenworm.autopart.services.EmailService;
import ru.greenworm.autopart.services.OrdersService;
import ru.greenworm.autopart.services.RequestsService;
import ru.greenworm.autopart.services.SearchService;
import ru.greenworm.autopart.services.SettingsService;
import ru.greenworm.autopart.services.UserDetailsService;
import ru.greenworm.autopart.utils.JsonUtils;
import ru.greenworm.autopart.utils.UriUtils;

@Controller
public class AdminController {

	@Autowired
	private SettingsService settingsService;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private RequestsService requestsService;

	@Autowired
	private OrdersService ordersService;

	@Autowired
	private CatalogService catalogService;

	@Autowired
	private SearchService searchService;

	@Autowired
	private EmailService emailService;

	@RequestMapping("/admin")
	public String showUsersPage() {
		return "admin/index";
	}

	@RequestMapping("/admin/users")
	public String showUsersPage(
// @formatter:off
			Model model, @RequestParam(value = "email", required = false) String email,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "price_type", defaultValue = "-1") long priceTypeId)
	// @formatter:on
	{
		model.addAttribute("usersTotalCount", userDetailsService.getUsersTotalCount());
		PriceType priceType = catalogService.getPriceType(priceTypeId);
		int usersCount = userDetailsService.getUsersCount(email, name, priceType);
		model.addAttribute("usersCount", usersCount);
		model.addAttribute("users", userDetailsService.getUsers(email, name, priceType, page, settingsService.getAsInteger(Setting.PER_PAGE)));
		model.addAttribute("name", name);
		model.addAttribute("email", email);
		model.addAttribute("paginator", new Paginator(usersCount, settingsService.getAsInteger(Setting.PER_PAGE), "/admin/users", page));
		model.addAttribute("filterPriceType", priceType);
		model.addAttribute("priceTypes", catalogService.getPriceTypes());
		return "admin/users";
	}

	@RequestMapping("/admin/user/{id}")
	public String showUserPage(Model model, @PathVariable("id") long id) {
		User user = userDetailsService.getUser(id);
		model.addAttribute("user", user);
		model.addAttribute("userRoles", userDetailsService.getUserRoles(user));
		model.addAttribute("allRoles", userDetailsService.getAllRoles());
		model.addAttribute("priceTypes", catalogService.getPriceTypes());
		return "admin/user";
	}

	@RequestMapping(value = "/admin/edit_profile.json", method = RequestMethod.POST)
	public @ResponseBody String editProfile(
//@formatter:off
			User user,
			@RequestParam(value = "role", required = false) long[] roleIds,
			@RequestParam(value = "priceTypeId", required = true) long priceTypeId) {
		//@formatter:on
		userDetailsService.editOther(user, roleIds, priceTypeId);
		return JsonUtils.getSuccessJson();
	}

	@RequestMapping("/admin/orders")
	public String showOrdersPage(Model model, @RequestParam(value = "page", defaultValue = "1") int page) {
		model.addAttribute("orders", ordersService.getOrders(null, page, settingsService.getAsInteger(Setting.PER_PAGE)));
		model.addAttribute("paginator", new Paginator(ordersService.getOrdersCount(null), settingsService.getAsInteger(Setting.PER_PAGE), "/admin/orders", page));
		return "admin/orders";
	}

	@RequestMapping("/admin/order/{id}")
	public String showOrderPage(Model model, @PathVariable("id") long id) {
		Order order = ordersService.getOrder(id);
		model.addAttribute("order", order);
		model.addAttribute("items", order.getItems());
		return "admin/order";
	}

	@RequestMapping("/admin/set_order_item_accepted_quantity")
	public @ResponseBody Order setOrderItemAcceptedQuantity(@RequestParam("itemId") long itemId, @RequestParam("quantity") int quantity) {
		return ordersService.setOrderItemAcceptedQuantity(itemId, quantity);
	}

	@RequestMapping("/admin/set_order_item_rejected_quantity")
	public @ResponseBody Order setOrderItemRejectedQuantity(@RequestParam("itemId") long itemId, @RequestParam("quantity") int quantity) {
		return ordersService.setOrderItemRejectedQuantity(itemId, quantity);
	}

	@RequestMapping("/admin/inc_order_item_accepted_quantity")
	public @ResponseBody Order incOrderItemAcceptedQuantity(@RequestParam("itemId") long itemId) {
		return ordersService.incOrderItemAcceptedQuantity(itemId);
	}

	@RequestMapping("/admin/dec_order_item_accepted_quantity")
	public @ResponseBody Order decOrderItemAcceptedQuantity(@RequestParam("itemId") long itemId) {
		return ordersService.decOrderItemAcceptedQuantity(itemId);
	}

	@RequestMapping("/admin/inc_order_item_rejected_quantity")
	public @ResponseBody Order incOrderItemRejectedQuantity(@RequestParam("itemId") long itemId) {
		return ordersService.incOrderItemRejectedQuantity(itemId);
	}

	@RequestMapping("/admin/dec_order_item_rejected_quantity")
	public @ResponseBody Order decOrderItemRejectedQuantity(@RequestParam("itemId") long itemId) {
		return ordersService.decOrderItemRejectedQuantity(itemId);
	}

	@RequestMapping("/admin/set_order_item_status")
	public @ResponseBody OrderItem setOrderItemStatus(@RequestParam("itemId") long itemId, @RequestParam("status") OrderItemStatus status) {
		return ordersService.setOrderItemStatus(itemId, status);
	}

	@RequestMapping("/admin/set_order_status")
	public @ResponseBody Order setOrderStatus(@RequestParam("orderId") long orderId, @RequestParam("status") OrderStatus status) {
		return ordersService.setOrderStatus(orderId, status);
	}

	@RequestMapping("/admin/requests")
	public String showRequestsPage(
// @formatter:off
		Model model,
		@RequestParam(value = "status", required = false) RequestStatus status,
		@RequestParam(value = "page", defaultValue = "1") int page
// @formatter:on
	) {
		model.addAttribute("requests", requestsService.getRequests(status, page, settingsService.getAsInteger(Setting.PER_PAGE)));
		model.addAttribute("paginator", new Paginator(requestsService.getRequestsCount(status), settingsService.getAsInteger(Setting.PER_PAGE), "/admin/requests", page));
		return "admin/requests";
	}

	@RequestMapping("/admin/request/{id}")
	public String showRequestPage(Model model, @PathVariable("id") long id) {
		Request request = requestsService.getRequest(id);
		model.addAttribute("request", request);
		model.addAttribute("items", request.getItems());
		return "admin/request";
	}

	@RequestMapping("/admin/queries")
	public String showQueriesPage(
//@formatter:off		
		Model model,
		@RequestParam(value = "from", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date from,
		@RequestParam(value = "to", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date to,
		@RequestParam(value = "page", defaultValue = "1") int page
	) {
//@formatter:on
		if (to == null) {
			to = DateUtils.ceiling(new Date(), Calendar.DAY_OF_MONTH);
		}
		if (from == null) {
			from = DateUtils.truncate(to, Calendar.MONTH);
		}
		
		String fromString = FastDateFormat.getInstance("dd.MM.yyyy").format(from);
		String toString = FastDateFormat.getInstance("dd.MM.yyyy").format(to);
		
		model.addAttribute("from", fromString);
		model.addAttribute("to", toString);
		model.addAttribute("queries", searchService.getQueries(from, to, page, settingsService.getAsInteger(Setting.PER_PAGE)));
		model.addAttribute("count", searchService.getQueriesDistinctCount(from, to));
		model.addAttribute("paginator", new Paginator(searchService.getQueriesDistinctCount(from, to), settingsService.getAsInteger(Setting.PER_PAGE), UriUtils.getBuilder("/admin/queries").addParameter("from", fromString).addParameter("to", toString).toString(), page));

		return "admin/queries";
	}

	@RequestMapping("/admin/categories")
	public String showCategoriesPage(Model model, @RequestParam(value = "parent_id", defaultValue = "-1") long parentId) {
		Category parent = catalogService.getCategory(parentId);
		model.addAttribute("parent", parent);
		model.addAttribute("categories", catalogService.getCategories(parent));
		model.addAttribute("parents", parent != null ? catalogService.getParents(parent, null) : null);
		return "admin/categories";
	}

	@RequestMapping("/admin/upload_category_image")
	public @ResponseBody String uploadCategoryImage(@RequestParam("id") long id, @RequestParam("file") MultipartFile image) throws IOException {
		catalogService.uploadCategoryImage(id, image.getBytes());
		return JsonUtils.getSuccessJson();
	}

	@RequestMapping("/admin/settings")
	public String showSettingsPage(Model model) {
		model.addAttribute("settings", settingsService.getAll());
		return "admin/settings";
	}

	@RequestMapping("/admin/set_setting_value")
	public @ResponseBody String setSettingValue(@RequestParam("name") String name, @RequestParam("value") String value) {
		settingsService.setValue(name, value);
		return JsonUtils.getSuccessJson();
	}

	@RequestMapping("/admin/emails")
	public String showEmailsPage(Model model) {
		model.addAttribute("templates", emailService.getTemplates());
		return "admin/emails";
	}

	@RequestMapping("/admin/email/{id}")
	public String showEmailsPage(Model model, @PathVariable("id") long id) {
		model.addAttribute("template", emailService.getTemplate(id));
		return "admin/email";
	}

	@RequestMapping("/admin/edit_email_template")
	public @ResponseBody String editEmailTemplate(EmailTemplate template) {
		emailService.editTemplate(template);
		return JsonUtils.getSuccessJson();
	}

	@ExceptionHandler({ AutopartException.class })
	public @ResponseBody String processException(AutopartException exception) {
		return JsonUtils.getErrorJson(exception.getMessage());
	}

}
