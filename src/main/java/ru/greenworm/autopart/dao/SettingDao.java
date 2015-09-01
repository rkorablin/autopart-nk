package ru.greenworm.autopart.dao;

import java.util.List;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import ru.greenworm.autopart.model.Setting;

@Repository
public class SettingDao extends AbstractDao<Setting> {

	public Setting getSetting(String key) {
		return findFirst(Restrictions.eq("key", key));
	}

	public List<Setting> getAll() {
		return findAll(Order.asc("key"));
	}

}
