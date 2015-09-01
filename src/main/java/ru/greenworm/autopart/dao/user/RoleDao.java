package ru.greenworm.autopart.dao.user;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import ru.greenworm.autopart.dao.AbstractDao;
import ru.greenworm.autopart.model.user.Role;
import ru.greenworm.autopart.model.user.User;

@Repository
public class RoleDao extends AbstractDao<Role> {

	public List<Role> getAll() {
		return findAll(Order.asc("position"));
	}

	public List<Role> getByUser(User user) {
		return user.getRoles().stream().sorted(Comparator.comparing(Role::getName)).collect(Collectors.toList());
	}

	public boolean exists(String name) {
		return exists(Restrictions.eq("name", name));
	}

	public Role getByName(String name) {
		return findFirst(Restrictions.eq("name", name));
	}

}
