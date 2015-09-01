package ru.greenworm.autopart.model.catalog;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import ru.greenworm.autopart.model.LongIdentifiable;

@Entity
@Table(name = "cross_codes", uniqueConstraints = { @UniqueConstraint(columnNames = { "code", "cross_code" }) })
public class CrossCode extends LongIdentifiable {

	@Column(length = 100, nullable = false)
	private String code;

	@Column(name = "cross_code", length = 100, nullable = false)
	private String cross;

	public CrossCode() {

	}

	public CrossCode(String code, String cross) {
		this.code = code;
		this.cross = cross;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCross() {
		return cross;
	}

	public void setCross(String cross) {
		this.cross = cross;
	}

}
