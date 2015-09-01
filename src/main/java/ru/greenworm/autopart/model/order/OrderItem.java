package ru.greenworm.autopart.model.order;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ru.greenworm.autopart.model.LongIdentifiable;
import ru.greenworm.autopart.model.catalog.Product;

@Entity
@Table(name = "order_items")
public class OrderItem extends LongIdentifiable {

	@JoinColumn(name = "product_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private Product product;

	@Column(nullable = false, columnDefinition = "numeric(19,2) default 0.00")
	private BigDecimal price;

	@Column(nullable = false)
	private OrderItemStatus status;

	@Column(name = "ordered_quantity", nullable = false)
	private int orderedQuantity;

	@Column(name = "accepted_quantity", nullable = false)
	private int acceptedQuantity;

	@Column(name = "rejected_quantity", nullable = false)
	private int rejectedQuantity;

	@JsonIgnore
	@JoinColumn(name = "order_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private Order order;

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public OrderItemStatus getStatus() {
		return status;
	}

	public void setStatus(OrderItemStatus status) {
		this.status = status;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public int getOrderedQuantity() {
		return orderedQuantity;
	}

	public void setOrderedQuantity(int orderedQuantity) {
		this.orderedQuantity = orderedQuantity;
	}

	public int getAcceptedQuantity() {
		return acceptedQuantity;
	}

	public void setAcceptedQuantity(int acceptedQuantity) {
		this.acceptedQuantity = acceptedQuantity;
	}

	public int getRejectedQuantity() {
		return rejectedQuantity;
	}

	public void setRejectedQuantity(int rejectedQuantity) {
		this.rejectedQuantity = rejectedQuantity;
	}

	public BigDecimal getOrderedCost() {
		return price.multiply(new BigDecimal(orderedQuantity));
	}

	public BigDecimal getAcceptedCost() {
		return price.multiply(new BigDecimal(acceptedQuantity));
	}

	public BigDecimal getRejectedCost() {
		return price.multiply(new BigDecimal(rejectedQuantity));
	}

}
