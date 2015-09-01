package ru.greenworm.autopart.dao.catalog;

import java.util.List;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import ru.greenworm.autopart.dao.AbstractDao;
import ru.greenworm.autopart.model.catalog.PriceType;

@Repository
public class PriceTypeDao extends AbstractDao<PriceType> {

	public PriceType getByExternalId(String externalId) {
		return findFirst(Restrictions.eq("externalId", externalId));
	}

	public List<PriceType> getAll() {
		return findAll(Order.asc("name"));
	}

}
