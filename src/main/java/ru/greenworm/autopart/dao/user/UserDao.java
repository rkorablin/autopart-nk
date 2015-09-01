package ru.greenworm.autopart.dao.user;

import java.util.List;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import ru.greenworm.autopart.dao.AbstractDao;
import ru.greenworm.autopart.model.catalog.PriceType;
import ru.greenworm.autopart.model.user.User;
import ru.greenworm.autopart.model.user.UserStatus;
import ru.greenworm.autopart.utils.StringUtils;

@Repository
public class UserDao extends AbstractDao<User> {

	public User getByEmail(String username) {
		return findFirst(Restrictions.eq("email", username));
	}

	public boolean checkUniqueEmail(User user) {
		Conjunction conjunction = new Conjunction();
		conjunction.add(Restrictions.eq("email", user.getEmail()));
		conjunction.add(Restrictions.eq("status", UserStatus.ACTIVE));
		if (user.getId() != null) {
			conjunction.add(Restrictions.ne("id", user.getId()));
		}
		return !exists(conjunction);
	}

	public User getByCode(String code) {
		return findFirst(Restrictions.eq("code", code), Restrictions.eq("status", UserStatus.NEW));
	}

	private Criterion getSearchCriterion(String email, String name, PriceType priceType) {
		Conjunction conjunction = new Conjunction();
		conjunction.add(Restrictions.eq("status", UserStatus.ACTIVE));
		if (StringUtils.hasLength(email)) {
			conjunction.add(Restrictions.ilike("email", email, MatchMode.ANYWHERE));
		}
		if (StringUtils.hasLength(name)) {
			conjunction.add(Restrictions.or(Restrictions.ilike("firstName", name, MatchMode.ANYWHERE), Restrictions.ilike("secondName", name, MatchMode.ANYWHERE), Restrictions.ilike("patronymicName", name, MatchMode.ANYWHERE), Restrictions.ilike("organizationName", name, MatchMode.ANYWHERE)));
		}
		if (priceType != null) {
			conjunction.add(Restrictions.eq("priceType", priceType));
		}
		return conjunction;
	}

	public List<User> getList(String email, String name, PriceType priceType, int firstResult, int maxResults) {
		return find(firstResult, maxResults, getSearchCriterion(email, name, priceType));
	}

	public int getCount(String email, String name, PriceType priceType) {
		return count(getSearchCriterion(email, name, priceType));
	}

}
