package ru.greenworm.autopart.model.catalog;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import ru.greenworm.autopart.model.LongIdentifiable;

@Entity
@Table(name = "price_types")
public class PriceType extends LongIdentifiable {

	@Column(nullable = false)
	private String name;

	@Column(name = "external_id", nullable = false)
	private String externalId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

}
