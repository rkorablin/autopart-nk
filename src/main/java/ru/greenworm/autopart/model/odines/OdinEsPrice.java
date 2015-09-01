package ru.greenworm.autopart.model.odines;

import java.math.BigDecimal;

public class OdinEsPrice {

	private String priceTypeExternalId;
	private BigDecimal priceForUnit;

	public String getPriceTypeExternalId() {
		return priceTypeExternalId;
	}

	public void setPriceTypeExternalId(String priceTypeExternalId) {
		this.priceTypeExternalId = priceTypeExternalId;
	}

	public BigDecimal getPriceForUnit() {
		return priceForUnit;
	}

	public void setPriceForUnit(BigDecimal priceForUnit) {
		this.priceForUnit = priceForUnit;
	}

}
