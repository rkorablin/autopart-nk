package ru.greenworm.autopart.services;

import java.util.Date;

import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.greenworm.autopart.model.Setting;
import ru.greenworm.autopart.model.catalog.Category;
import ru.greenworm.autopart.model.order.OrderItemStatus;
import ru.greenworm.autopart.model.order.OrderStatus;
import ru.greenworm.autopart.model.order.ReceivingMethod;
import ru.greenworm.autopart.model.request.RequestStatus;
import ru.greenworm.autopart.utils.StringUtils;

@Service("utilsService")
public class UtilsService {

	@Autowired
	private SettingsService settingsService;

	public String getDeclension(long number, String string1, String string24, String string50) {
		return StringUtils.getDeclensionString(number, string1, string24, string50);
	}

	public String formatDateTime(Date date) {
		return FastDateFormat.getInstance("dd.MM.yyyy HH:mm").format(date);
	}

	public Class<?> getOrderStatusEnum() {
		return OrderStatus.class;
	}

	public Class<?> getOrderItemStatusEnum() {
		return OrderItemStatus.class;
	}

	public Class<?> getReceivingMethodEnum() {
		return ReceivingMethod.class;
	}

	public Class<?> getRequestStatusEnum() {
		return RequestStatus.class;
	}

	public String getImageUrl(Category category, String resize) {
		String url = StringUtils.hasLength(category.getImageUrl()) ? category.getImageUrl() : settingsService.getValue(Setting.CATEGORY_DEFAULT_IMAGE);
		int lastDotIndex = url.lastIndexOf(".");
		StringBuilder builder = new StringBuilder();
		builder.append(url.substring(0, lastDotIndex));
		builder.append("_");
		builder.append(resize);
		builder.append(url.substring(lastDotIndex));
		return builder.toString();
	}
}
