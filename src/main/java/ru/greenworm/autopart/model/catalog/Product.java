package ru.greenworm.autopart.model.catalog;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import ru.greenworm.autopart.model.LongIdentifiable;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "products")
public class Product extends LongIdentifiable {

	@Column(nullable = false)
	private String name;

	@Column(length = 1000)
	private String description;

	@Column
	private String code;

	@Column(name = "clean_code")
	private String cleanCode;

	@Column(nullable = false)
	private int quantity;

	@Column(name = "external_id", nullable = false)
	private String externalId;

	@JoinColumn(name = "category_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private Category category;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "product")
	@OrderBy("id")
	private List<Price> prices;

	@Column(name = "categories_string", nullable = true, length = 1000)
	private String categoriesString;

	private BigDecimal price = BigDecimal.ZERO;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCleanCode() {
		return cleanCode;
	}

	public void setCleanCode(String cleanCode) {
		this.cleanCode = cleanCode;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public List<Price> getPrices() {
		return prices;
	}

	public void setPrices(List<Price> prices) {
		this.prices = prices;
	}

	public String getCategoriesString() {
		return categoriesString;
	}

	public void setCategoriesString(String categoriesString) {
		this.categoriesString = categoriesString;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

}
