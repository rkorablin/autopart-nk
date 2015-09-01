package ru.greenworm.autopart.dao.catalog;

import java.util.List;

import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import ru.greenworm.autopart.dao.AbstractDao;
import ru.greenworm.autopart.model.catalog.Product;

@Repository
public class ProductDao extends AbstractDao<Product> {

	public Product getByExternalId(String externalId) {
		return findFirst(Restrictions.eq("externalId", externalId));
	}

	public List<Product> getByCode(String code) {
		return find(Restrictions.ilike("cleanCode", code, MatchMode.ANYWHERE));
	}

	public List<Product> getByCross(String code) {
		return createSQLQuery("select * from products where clean_code <> :code and clean_code in (select cross_code from cross_codes where code ilike :code)").addEntity(Product.class).setString("code", "%" + code + "%").list();
	}

	public List<Product> getByText(String text) {
		return find(Order.asc("name"), Restrictions.or(Restrictions.ilike("name", text, MatchMode.ANYWHERE), Restrictions.ilike("description", text, MatchMode.ANYWHERE), Restrictions.ilike("categoriesString", text, MatchMode.ANYWHERE)));
	}

}
