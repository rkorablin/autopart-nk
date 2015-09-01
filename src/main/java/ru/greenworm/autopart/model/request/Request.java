package ru.greenworm.autopart.model.request;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import ru.greenworm.autopart.model.LongIdentifiable;
import ru.greenworm.autopart.model.user.User;

@Entity
@Table(name = "requests")
public class Request extends LongIdentifiable {

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	@Column
	private RequestStatus status;

	@JoinColumn(name = "user_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private User user;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "request", cascade = CascadeType.ALL)
	@OrderBy("id")
	private List<RequestItem> items;

	@Column(length = 1000)
	private String comment;

	public Request() {

	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public RequestStatus getStatus() {
		return status;
	}

	public void setStatus(RequestStatus status) {
		this.status = status;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<RequestItem> getItems() {
		return items;
	}

	public void setItems(List<RequestItem> items) {
		this.items = items;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getItemsCount() {
		return items.size();
	}

}
