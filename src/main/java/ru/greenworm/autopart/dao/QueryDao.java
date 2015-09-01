package ru.greenworm.autopart.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;

import ru.greenworm.autopart.model.Query;

@Repository
public class QueryDao extends AbstractDao<Query> {

	public Map<String, Integer> getMap() {
		List<Object[]> list = createSQLQuery("select text t, count(*) c from queries group by t order by c desc").addScalar("t", StringType.INSTANCE).addScalar("c", IntegerType.INSTANCE).list();
		Map<String, Integer> map = new HashMap<String, Integer>();
		list.stream().forEach(objects -> {
			map.put((String) objects[0], (Integer) objects[1]);
		});
		return map;
	}

	public Map<String, Long> getMap(Date from, Date to, int firstResult, int maxResults) {
		List<Object[]> list = getCriteria().add(Restrictions.ge("date", from)).add(Restrictions.lt("date", to)).setProjection(Projections.projectionList().add(Projections.groupProperty("text")).add(Projections.count("id"), "count")).addOrder(Order.desc("count")).setFirstResult(firstResult).setMaxResults(maxResults).list();
		Map<String, Long> map = new LinkedHashMap<String, Long>();
		list.stream().forEach(objects -> {
			map.put((String) objects[0], (Long) objects[1]);
		});
		return map;
	}

	public int getDistinctCount(Date from, Date to) {
		return ((Long) getCriteria().setProjection(Projections.countDistinct("text")).add(Restrictions.ge("date", from)).add(Restrictions.lt("date", to)).uniqueResult()).intValue();
	}
	
}
