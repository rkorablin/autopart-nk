package ru.greenworm.autopart.model.catalog;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ru.greenworm.autopart.model.LongIdentifiable;

@Entity
@Table(name = "categories")
public class Category extends LongIdentifiable {

	@Column(nullable = false)
	private String name;

	@Column(name = "products_total_count", nullable = false)
	private int productsTotalCount;

	@Column(name = "external_id", nullable = false)
	private String externalId;

	@JoinColumn(name = "parent_id", nullable = true)
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	private Category parent;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
	@OrderBy("name")
	private List<Category> childs;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "category")
	@OrderBy("name")
	private List<Product> products;

	@Column(name = "image_url", length = 1000, nullable = true)
	private String imageUrl;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getChildsCount() {
		return childs.size();
	}

	public int getProductsCount() {
		return products.size();
	}

	public int getProductsTotalCount() {
		return productsTotalCount;
	}

	public void setProductsTotalCount(int productsTotalCount) {
		this.productsTotalCount = productsTotalCount;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public Category getParent() {
		return parent;
	}

	public void setParent(Category parent) {
		this.parent = parent;
	}

	public List<Category> getChilds() {
		return childs;
	}

	public void setChilds(List<Category> childs) {
		this.childs = childs;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getLink() {
		return "/catalog/category/" + getId();
	}

}