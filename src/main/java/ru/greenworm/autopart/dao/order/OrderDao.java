package ru.greenworm.autopart.dao.order;

import java.util.List;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import ru.greenworm.autopart.dao.AbstractDao;
import ru.greenworm.autopart.model.order.Order;
import ru.greenworm.autopart.model.user.User;

@Repository
public class OrderDao extends AbstractDao<Order> {

	private Conjunction getConjunction(User user) {
		Conjunction conjunction = new Conjunction();
		if (user != null) {
			conjunction.add(Restrictions.eq("user", user));
		}
		return conjunction;
	}

	public List<Order> getList(User user, int firstResult, int maxResults) {

		return find(org.hibernate.criterion.Order.desc("date"), firstResult, maxResults, getConjunction(user));
	}

	public int getCount(User user) {
		return count(getConjunction(user));
	}
}
