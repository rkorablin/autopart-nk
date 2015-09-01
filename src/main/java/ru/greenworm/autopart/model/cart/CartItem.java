package ru.greenworm.autopart.model.cart;

import java.math.BigDecimal;

import ru.greenworm.autopart.model.catalog.Product;

public class CartItem {

	private Product product;
	private int quantity;

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getPrice() {
		return getProduct().getPrice();
	}
	
	public BigDecimal getCost() {
		return getProduct().getPrice().multiply(new BigDecimal(quantity));
	}

}
