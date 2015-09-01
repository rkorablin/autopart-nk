package ru.greenworm.autopart.model.request;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import ru.greenworm.autopart.model.LongIdentifiable;

@Entity
@Table(name = "request_items")
public class RequestItem extends LongIdentifiable {

	@Column(length = 100)
	private String code;

	@Column(length = 100)
	private String name;

	@Column(length = 100)
	private String vin;

	@JoinColumn(name = "request_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private Request request;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVin() {
		return vin;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

}
