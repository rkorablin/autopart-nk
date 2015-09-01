package ru.greenworm.autopart.model.odines;

import java.util.ArrayList;
import java.util.List;

public class OdinEsOffer {

	private String productExternalId;
	private int quantity;
	private List<OdinEsPrice> prices = new ArrayList<OdinEsPrice>();

	public String getProductExternalId() {
		return productExternalId;
	}

	public void setProductExternalId(String productExternalId) {
		this.productExternalId = productExternalId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public List<OdinEsPrice> getPrices() {
		return prices;
	}

	public void setPrices(List<OdinEsPrice> prices) {
		this.prices = prices;
	}

}
