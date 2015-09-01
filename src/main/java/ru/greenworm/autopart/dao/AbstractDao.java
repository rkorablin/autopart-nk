package ru.greenworm.autopart.dao;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import ru.greenworm.autopart.model.LongIdentifiable;

@Transactional
public abstract class AbstractDao<E> {

	@Autowired
	private SessionFactory sessionFactory;

	private Class<E> persistentClass;

	protected Class<E> getPersistentClass() {
		return (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	public Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	public SQLQuery createSQLQuery(String sql) {
		return getCurrentSession().createSQLQuery(sql);
	}

	public Query createQuery(String hql) {
		return getCurrentSession().createQuery(hql);
	}

	public AbstractDao() {
		this.persistentClass = getPersistentClass();
	}

	public E getById(Long id) {
		return (E) getCurrentSession().get(persistentClass, id);
	}

	public void delete(Long id) {
		getCurrentSession().delete(getById(id));
	}

	public E getByIdOrEmpty(Long id) {
		E entity = id != null ? (E) getCurrentSession().get(persistentClass, id) : null;
		if (entity == null) {
			try {
				return persistentClass.newInstance();
			} catch (InstantiationException exception) {
				exception.printStackTrace();
			} catch (IllegalAccessException exception) {
				exception.printStackTrace();
			}
		}
		return entity;
	}

	protected Criteria getCriteria() {
		return getCurrentSession().createCriteria(persistentClass);
	}

	public Criteria getCriteria(Order order) {
		return getCriteria().addOrder(order);
	}

	public Criteria getCriteria(Order order, int firstResult, int maxResults) {
		return getCriteria().addOrder(order).setFirstResult(firstResult).setMaxResults(maxResults);
	}

	public E findFirst(Criterion... criterions) {
		return findFirst(null, criterions);
	}

	public E findFirst(Order order, Criterion... criterions) {
		Criteria criteria = getCriteria();
		for (Criterion criterion : criterions) {
			criteria.add(criterion);
		}
		criteria.setMaxResults(1);
		if (order != null) {
			criteria.addOrder(order);
		}
		E entity = (E) criteria.uniqueResult();
		return entity;
	}

	public List<E> find(int firstResult, int maxResults, Criterion... criterions) {
		return find(null, firstResult, maxResults, criterions);
	}

	public List<E> find(Order order, int firstResult, int maxResults, Criterion... criterions) {
		Criteria criteria = getCriteria();
		for (Criterion criterion : criterions) {
			criteria.add(criterion);
		}
		if (order != null) {
			criteria.addOrder(order);
		}
		criteria.setFirstResult(firstResult);
		criteria.setMaxResults(maxResults);
		return criteria.list();
	}

	public List<E> find(Order order, Criterion... criterions) {
		return find(order, 0, Integer.MAX_VALUE, criterions);
	}

	public List<E> find(Criterion... criterions) {
		return find(null, criterions);
	}

	public Object aggregate(Projection projection, Criterion... criterions) {
		Criteria criteria = getCriteria();
		for (Criterion criterion : criterions) {
			criteria.add(criterion);
		}
		return criteria.setProjection(projection).uniqueResult();
	}

	public int count(Criterion... criterions) {
		Criteria criteria = getCriteria();
		for (Criterion criterion : criterions) {
			criteria.add(criterion);
		}
		return ((Long) criteria.setProjection(Projections.count("id")).uniqueResult()).intValue();
	}

	public boolean exists(Criterion... criterions) {
		return count(criterions) > 0;
	}

	public Long save(E entity) {
		return (Long) getCurrentSession().save(entity);
	}

	public void update(E entity) {
		getCurrentSession().update(entity);
	}

	public void saveOrUpdate(E entity) {
		getCurrentSession().saveOrUpdate(entity);
	}

	public void delete(E entity) {
		if (!getCurrentSession().contains(entity)) {
			entity = (E) getCurrentSession().merge(entity);
		}
		getCurrentSession().delete(entity);
	}

	public List<E> findAll() {
		return find();
	}

	public List<E> findAll(Order order) {
		return find(order);
	}

	public void refresh(E entity) {
		getCurrentSession().refresh(entity);
	}

	public List<Long> getIdsList(List<? extends LongIdentifiable> entities) {
		ArrayList<Long> ids = new ArrayList<Long>();
		for (LongIdentifiable entity : entities) {
			if (entity != null) {
				ids.add(entity.getId());
			}
		}
		return ids;
	}

	public List<E> getByIds(List<Long> ids) {
		return getByIds(null, ids);
	}

	public List<E> getByIds(Order order, List<Long> ids) {
		if (ids.isEmpty()) {
			return Collections.emptyList();
		} else {

			if (order != null) {
				return find(order, Restrictions.in("id", ids));
			} else {
				return find(Restrictions.in("id", ids));
			}
		}
	}

	public List<E> getByIds(long[] idsArray) {
		return getByIds(null, idsArray);
	}

	public List<E> getByIds(Order order, long[] idsArray) {
		List<Long> idsList = new ArrayList<Long>();
		for (long id : idsArray) {
			idsList.add(id);
		}
		return getByIds(order, idsList);
	}
}
