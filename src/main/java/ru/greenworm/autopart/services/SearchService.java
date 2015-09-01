package ru.greenworm.autopart.services;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.greenworm.autopart.dao.QueryDao;
import ru.greenworm.autopart.dao.catalog.ProductDao;
import ru.greenworm.autopart.model.Query;
import ru.greenworm.autopart.model.Setting;
import ru.greenworm.autopart.model.catalog.Product;

@Service
public class SearchService {

	@Autowired
	private ProductDao productDao;

	@Autowired
	private SettingsService settingsService;

	@Autowired
	private QueryDao queryDao;

	public boolean checkQueryLength(String query) {
		return query != null && query.length() >= settingsService.getAsInteger(Setting.SEARCH_MIN_LENGTH);
	}

	public String clearCode(String code) {
		return code != null ? code.replaceAll("-", "").replaceAll(" ", "").replaceAll("\\.", "").replaceAll("\\\\", "").replaceAll("/", "") : null;
	}
	
	public List<Product> getProductsByCode(String code) {
		return productDao.getByCode(clearCode(code));
	}

	public List<Product> getProductsByCross(String code) {
		return productDao.getByCross(clearCode(code));
	}

	public List<Product> getProductsByText(String text) {
		return productDao.getByText(text);
	}

	public void saveQuery(String text) {
		Query query = new Query();
		query.setText(text);
		query.setDate(new Date());
		queryDao.save(query);
	}

	public Map<String, Integer> getQueries() {
		return queryDao.getMap();
	}

	public Map<String, Long> getQueries(Date from, Date to, int page, int perPage) {
		return queryDao.getMap(from, to, (page-1) * perPage, perPage);
	}
	
	public Integer getQueriesDistinctCount(Date from, Date to) {
		return queryDao.getDistinctCount(from, to);
	}

}
