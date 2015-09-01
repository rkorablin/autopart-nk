package ru.greenworm.autopart.model.cart;

import java.math.BigDecimal;
import java.util.List;

public class Cart {

	private List<CartItem> items;
	private BigDecimal cost;

	public List<CartItem> getItems() {
		return items;
	}

	public void setItems(List<CartItem> items) {
		this.items = items;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

}
