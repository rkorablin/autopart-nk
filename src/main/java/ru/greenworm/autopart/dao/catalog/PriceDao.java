package ru.greenworm.autopart.dao.catalog;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import ru.greenworm.autopart.dao.AbstractDao;
import ru.greenworm.autopart.model.catalog.Price;
import ru.greenworm.autopart.model.catalog.PriceType;
import ru.greenworm.autopart.model.catalog.Product;
import ru.greenworm.autopart.model.user.User;

@Repository
public class PriceDao extends AbstractDao<Price> {

	public Price get(Product product, PriceType priceType) {
		return findFirst(Restrictions.eq("product", product), Restrictions.eq("priceType", priceType));
	}	

}
