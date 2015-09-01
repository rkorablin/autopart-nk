package ru.greenworm.autopart.events;

import org.springframework.context.ApplicationEvent;

import ru.greenworm.autopart.model.order.Order;

public class OrderEvent extends ApplicationEvent {

	private Order order;

	public OrderEvent(Object source, Order order) {
		super(source);
		this.order = order;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

}
