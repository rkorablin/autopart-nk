package ru.greenworm.autopart.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "settings")
public class Setting extends LongIdentifiable {

	public static final String PER_PAGE = "view.per-page";
	public static final String DEFAULT_PRICE = "catalog.default-price";
	public static final String NEW_PARTS_CATEGORY = "catelog.new-parts-category";
	public static final String TEMP_SUBDIRECTORY = "path.temp-subdirectory";
	public static final String EMAIL_FROM_LOGIN = "email.from.login";
	public static final String EMAIL_FROM_PASSWORD = "email.from.password";
	public static final String EMAIL_TO = "email.to";
	public static final String CATEGORY_DEFAULT_IMAGE = "catalog.category-default-image";
	public static final String SEARCH_MIN_LENGTH = "search.min-length";
	public static final String IMAGES_UPLOAD_URL = "images.upload-url";
	public static final String IMAGES_SERVER = "images.server";
	public static final String SKIP_CATEGORIES = "catalog.skip-categories";
	
	@Column(length = 10000, unique = true, nullable = false)
	private String key;

	@Column(length = 10000, name = "val", nullable = false)
	private String value;

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
