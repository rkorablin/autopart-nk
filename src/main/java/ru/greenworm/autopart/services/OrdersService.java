package ru.greenworm.autopart.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;

import ru.greenworm.autopart.dao.catalog.ProductDao;
import ru.greenworm.autopart.dao.order.OrderDao;
import ru.greenworm.autopart.dao.order.OrderItemDao;
import ru.greenworm.autopart.events.OrderEvent;
import ru.greenworm.autopart.exceptions.OrderException;
import ru.greenworm.autopart.model.order.Order;
import ru.greenworm.autopart.model.order.OrderItem;
import ru.greenworm.autopart.model.order.OrderItemStatus;
import ru.greenworm.autopart.model.order.OrderStatus;
import ru.greenworm.autopart.model.user.User;
import ru.greenworm.autopart.utils.SecurityUtils;

@Service
public class OrdersService implements ApplicationEventPublisherAware {

	private ApplicationEventPublisher publisher;

	@Autowired
	private ProductDao productDao;

	@Autowired
	private OrderDao orderDao;

	@Autowired
	private OrderItemDao orderItemDao;

	public void createOrder(Order order) {
		order.setDate(new Date());
		order.setStatus(OrderStatus.NEW);
		order.setUser(SecurityUtils.getCurrentUser());
		order.getItems().stream().forEach(item -> {
			productDao.refresh(item.getProduct());
			item.setPrice(item.getProduct().getPrice());
			item.setStatus(OrderItemStatus.NEW);
			item.setOrder(order);
		});
		orderDao.save(order);
		publisher.publishEvent(new OrderEvent(this, order));
	}

	public List<Order> getOrders(User user, int page, int perPage) {
		return orderDao.getList(user, (page - 1) * perPage, perPage);
	}

	public int getOrdersCount(User user) {
		return orderDao.getCount(user);
	}

	public Order getOrder(long id) {
		return orderDao.getById(id);
	}

	public Order setOrderItemQuantities(OrderItem item, int acceptedQuantity, int rejectedQuantity) {
		item.setAcceptedQuantity(acceptedQuantity);
		item.setRejectedQuantity(rejectedQuantity);
		orderItemDao.update(item);
		Order order = item.getOrder();
		orderDao.refresh(order);
		return order;
	}

	public Order setOrderItemAcceptedQuantity(OrderItem item, int quantity) {
		if (quantity < 0) {
			throw new OrderException("Нельзя установить количество меньше нуля");
		}
		if (quantity > item.getOrderedQuantity()) {
			throw new OrderException("Нельзя установить количество больше заказанного");
		}
		return setOrderItemQuantities(item, quantity, item.getOrderedQuantity() - quantity);
	}

	public Order setOrderItemRejectedQuantity(OrderItem item, int quantity) {
		if (quantity < 0) {
			throw new OrderException("Нельзя установить количество меньше нуля");
		}
		if (quantity > item.getOrderedQuantity()) {
			throw new OrderException("Нельзя установить количество больше заказанного");
		}
		return setOrderItemQuantities(item, item.getOrderedQuantity() - quantity, quantity);
	}

	public Order setOrderItemAcceptedQuantity(long itemId, int quantity) {
		OrderItem item = orderItemDao.getById(itemId);
		return setOrderItemAcceptedQuantity(item, quantity);
	}

	public Order setOrderItemRejectedQuantity(long itemId, int quantity) {
		OrderItem item = orderItemDao.getById(itemId);
		return setOrderItemRejectedQuantity(item, quantity);
	}

	public Order incOrderItemAcceptedQuantity(long itemId) {
		OrderItem item = orderItemDao.getById(itemId);
		return setOrderItemAcceptedQuantity(itemId, item.getAcceptedQuantity() + 1);
	}

	public Order decOrderItemAcceptedQuantity(long itemId) {
		OrderItem item = orderItemDao.getById(itemId);
		return setOrderItemAcceptedQuantity(itemId, item.getAcceptedQuantity() - 1);
	}

	public Order incOrderItemRejectedQuantity(long itemId) {
		OrderItem item = orderItemDao.getById(itemId);
		return setOrderItemRejectedQuantity(itemId, item.getRejectedQuantity() + 1);
	}

	public Order decOrderItemRejectedQuantity(long itemId) {
		OrderItem item = orderItemDao.getById(itemId);
		return setOrderItemRejectedQuantity(itemId, item.getRejectedQuantity() - 1);
	}

	public OrderItem setOrderItemStatus(long itemId, OrderItemStatus status) {
		OrderItem item = orderItemDao.getById(itemId);
		item.setStatus(status);
		orderItemDao.update(item);
		return item;
	}

	public Order setOrderStatus(long orderId, OrderStatus status) {
		Order order = orderDao.getById(orderId);
		order.setStatus(status);
		orderDao.update(order);
		return order;
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}

}
