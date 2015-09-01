package ru.greenworm.autopart.model.order;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ru.greenworm.autopart.model.Address;
import ru.greenworm.autopart.model.LongIdentifiable;
import ru.greenworm.autopart.model.user.User;

@Entity
@Table(name = "orders")
public class Order extends LongIdentifiable {

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	@Column(nullable = false)
	private OrderStatus status;

	@OneToMany(cascade = CascadeType.ALL)
	@OrderBy("id")
	private List<OrderItem> items;

	@JsonIgnore
	@JoinColumn(name = "user_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private User user;

	@Column(name = "receiving_method")
	private ReceivingMethod receivingMethod;

	@Embedded
	private Address address;

	@Column(length = 1000)
	private String comment;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public List<OrderItem> getItems() {
		return items;
	}

	public void setItems(List<OrderItem> items) {
		this.items = items;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public ReceivingMethod getReceivingMethod() {
		return receivingMethod;
	}

	public void setReceivingMethod(ReceivingMethod receivingMethod) {
		this.receivingMethod = receivingMethod;
	}

	private BigDecimal getCost(Function<OrderItem, BigDecimal> itemCostGetter) {
		return items.stream().map(itemCostGetter).reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
	}

	public BigDecimal getOrderedCost() {
		return getCost(OrderItem::getOrderedCost);
	}

	public BigDecimal getAcceptedCost() {
		return getCost(OrderItem::getAcceptedCost);
	}

	public BigDecimal getRejectedCost() {
		return getCost(OrderItem::getRejectedCost);
	}

	public int getItemsCount() {
		return items.size();
	}

}
