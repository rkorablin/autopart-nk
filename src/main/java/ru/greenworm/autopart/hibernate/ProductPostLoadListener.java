package ru.greenworm.autopart.hibernate;

import org.hibernate.event.spi.PostLoadEvent;
import org.hibernate.event.spi.PostLoadEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.greenworm.autopart.model.catalog.Product;
import ru.greenworm.autopart.model.user.User;
import ru.greenworm.autopart.services.UserDetailsService;
import ru.greenworm.autopart.utils.SecurityUtils;

@Component
public class ProductPostLoadListener implements PostLoadEventListener {

	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	public void onPostLoad(PostLoadEvent event) {
		if (event.getEntity() instanceof Product) {
			Product product = (Product) event.getEntity();
			User user = SecurityUtils.getCurrentUser();
			product.setPrice(userDetailsService.getUserPrice(product, user));
		}
	}

}
