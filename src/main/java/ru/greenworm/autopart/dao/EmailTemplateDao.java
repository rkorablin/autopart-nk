package ru.greenworm.autopart.dao;

import java.util.List;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import ru.greenworm.autopart.model.EmailTemplate;

@Repository
public class EmailTemplateDao extends AbstractDao<EmailTemplate> {

	public EmailTemplate getByMnemo(String mnemo) {
		return findFirst(Restrictions.eq("mnemo", mnemo));
	}

	public List<EmailTemplate> getAll() {
		return findAll(Order.asc("mnemo"));
	}

}
