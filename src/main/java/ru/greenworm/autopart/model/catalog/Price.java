package ru.greenworm.autopart.model.catalog;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import ru.greenworm.autopart.model.LongIdentifiable;

@Entity
@Table(name = "prices")
public class Price extends LongIdentifiable {

	@JoinColumn(name = "product_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private Product product;

	@JoinColumn(name = "price_type_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private PriceType priceType;

	@Column(nullable = false, columnDefinition = "numeric(19,2) default 0.00")
	private BigDecimal value;

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public PriceType getPriceType() {
		return priceType;
	}

	public void setPriceType(PriceType priceType) {
		this.priceType = priceType;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

}
