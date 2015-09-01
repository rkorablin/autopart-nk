package ru.greenworm.autopart.dao;

import java.util.List;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import ru.greenworm.autopart.model.request.Request;
import ru.greenworm.autopart.model.request.RequestStatus;

@Repository
public class RequestDao extends AbstractDao<Request> {

	private Criterion getCriterion(RequestStatus status) {
		Conjunction conjunction = new Conjunction();
		if (status != null) {
			conjunction.add(Restrictions.eq("status", status));
		}
		return conjunction;
	}
	
	public List<Request> getList(RequestStatus status, int firstResult, int maxResults) {
		return find(Order.desc("date"), firstResult, maxResults, getCriterion(status));
	}

	public int getCount(RequestStatus status) {
		return count(getCriterion(status));
	}

}
