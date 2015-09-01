package ru.greenworm.autopart.dto;

import java.util.LinkedList;
import java.util.List;

public class Paginator {

	public class Item {
		private int index;
		private boolean current;
		private String url;

		public Item(int index, boolean current, String url) {
			super();
			this.index = index;
			this.current = current;
			this.url = url;
		}

		public int getIndex() {
			return index;
		}

		public boolean isCurrent() {
			return current;
		}

		public String getUrl() {
			return url;
		}

	}

	private List<Item> items = new LinkedList<Item>();
	private int itemsCount;
	private int currentItemIndex;
	private String baseUrl;

	private String getUrl(String baseUrl, int page) {
		if (page == 1) {
			return baseUrl;
		} else {
			return baseUrl + (baseUrl.indexOf('?') != -1 ? "&" : "?") + "page=" + page;
		}
	}

	public Paginator(int count, int perPage, String baseUrl, int currentPage) {
		this.itemsCount = (int) Math.ceil((float) count / perPage);
		this.baseUrl = baseUrl;
		for (int index = 1; index <= this.itemsCount; index++) {
			this.items.add(new Item(index, index == currentPage, getUrl(baseUrl, index)));
		}
	}

	public List<Item> getItems() {
		return items;
	}
	
	public int getItemsCount() {
		return itemsCount;
	}
	
	public boolean isHasPrevious() {
		return currentItemIndex > 1;
	}
	
	public boolean isHasNext() {
		return currentItemIndex < itemsCount;
	}
	
	public String getPreviousUrl() {
		return isHasPrevious() ? getUrl(baseUrl, currentItemIndex - 1) : null;
	}

	public String getNextUrl() {
		return isHasNext() ? getUrl(baseUrl, currentItemIndex + 1) : null;
	}
	
}
