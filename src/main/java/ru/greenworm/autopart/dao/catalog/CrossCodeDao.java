package ru.greenworm.autopart.dao.catalog;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import ru.greenworm.autopart.dao.AbstractDao;
import ru.greenworm.autopart.model.catalog.CrossCode;

@Repository
public class CrossCodeDao extends AbstractDao<CrossCode> {

	public void deleteByCode(String code) {
		createQuery("delete from CrossCode where code = :code").setString("code", code).executeUpdate();
	}

	public void deleteAll() {
		createQuery("delete from CrossCode").executeUpdate();
	}

	public boolean exists(String code, String cross) {
		return exists(Restrictions.eq("code", code), Restrictions.eq("cross", cross));
	}

}
