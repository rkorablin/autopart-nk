package ru.greenworm.autopart.dao.catalog;

import java.util.List;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import ru.greenworm.autopart.dao.AbstractDao;
import ru.greenworm.autopart.model.catalog.Category;

@Repository
public class CategoryDao extends AbstractDao<Category> {

	public Category getByExternalId(String externalId) {
		return findFirst(Restrictions.eq("externalId", externalId));
	}

	public List<Category> getRoots() {
		return find(Order.asc("name"), Restrictions.isNull("parent"));
	}
	
}
