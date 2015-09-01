package ru.greenworm.autopart.services;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.greenworm.autopart.dao.catalog.CategoryDao;
import ru.greenworm.autopart.dao.catalog.CrossCodeDao;
import ru.greenworm.autopart.dao.catalog.PriceDao;
import ru.greenworm.autopart.dao.catalog.PriceTypeDao;
import ru.greenworm.autopart.dao.catalog.ProductDao;
import ru.greenworm.autopart.exceptions.AutopartException;
import ru.greenworm.autopart.model.Setting;
import ru.greenworm.autopart.model.catalog.Category;
import ru.greenworm.autopart.model.catalog.CrossCode;
import ru.greenworm.autopart.model.catalog.Price;
import ru.greenworm.autopart.model.catalog.PriceType;
import ru.greenworm.autopart.model.catalog.Product;
import ru.greenworm.autopart.model.odines.OdinEsGroup;
import ru.greenworm.autopart.model.odines.OdinEsOffer;
import ru.greenworm.autopart.model.odines.OdinEsPriceType;
import ru.greenworm.autopart.model.odines.OdinEsProduct;
import ru.greenworm.autopart.utils.StringUtils;

@Service
public class CatalogService {

	private static final Logger logger = LoggerFactory.getLogger(CatalogService.class);

	@Autowired
	private CategoryDao categoryDao;

	@Autowired
	private ProductDao productDao;

	@Autowired
	private PriceTypeDao priceTypeDao;

	@Autowired
	private PriceDao priceDao;

	@Autowired
	private CrossCodeDao crossCodeDao;

	@Autowired
	private SettingsService settingsService;

	@Autowired
	private SearchService searchService;

	public List<Category> getCategories(Category parent) {
		if (parent != null) {
			return parent.getChilds();
		} else {
			return categoryDao.getRoots();
		}
	}

	public List<Category> getParents(Category category, Category topParent) {
		LinkedList<Category> parents = new LinkedList<Category>();
		while (true) {
			category = category.getParent();
			if (category == null) {
				break;
			} else {
				parents.addFirst(category);
				if (category.equals(topParent)) {
					break;
				}
			}
		}
		return parents;
	}

	public List<Category> getParents(Product product, List<Category> topParents) {
		LinkedList<Category> parents = new LinkedList<Category>();
		Category category = product.getCategory();
		while (category != null && !topParents.contains(category)) {
			parents.addFirst(category);
			category = category.getParent();
		}
		return parents;
	}

	public List<Product> getProducts(Category category) {
		return category.getProducts();
	}

	public Category getCategory(long id) {
		return categoryDao.getById(id);
	}

	public void saveOdinEsGroup(OdinEsGroup odinEsGroup) {
		logger.info("saving category : " + odinEsGroup.getExternalId() + " : " + odinEsGroup.getName() + (StringUtils.hasLength(odinEsGroup.getParentExternalId()) ? " (parent : " + odinEsGroup.getParentExternalId() + ")" : ""));
		List<String> skipExternalIds = Stream.of(settingsService.getValue(Setting.SKIP_CATEGORIES).split(",")).collect(Collectors.toList());
		Category parent = categoryDao.getByExternalId(odinEsGroup.getParentExternalId());
		if (skipExternalIds.contains(odinEsGroup.getExternalId())) {
			logger.info("skip by external id");
		} else if (parent == null && StringUtils.hasLength(odinEsGroup.getParentExternalId())) {
			logger.info("skip by parent");
		} else {
			Category category = categoryDao.getByExternalId(odinEsGroup.getExternalId());
			if (category == null) {
				category = new Category();
				category.setExternalId(odinEsGroup.getExternalId());
			}
			category.setName(odinEsGroup.getName());
			category.setParent(parent);
			category.setProductsTotalCount(-1);
			categoryDao.saveOrUpdate(category);
			logger.info("done");
		}
	}

	public void saveOdinEsProduct(OdinEsProduct odinEsProduct) {
		logger.info("saving product : " + odinEsProduct.getExternalId() + " : " + odinEsProduct.getName() + " (group " + odinEsProduct.getGroupExternalId() + ", code " + odinEsProduct.getCode() + ")");

		Category category = categoryDao.getByExternalId(odinEsProduct.getGroupExternalId());
		if (category == null) {
			logger.info("skip by category");
		} else {
			Product product = productDao.getByExternalId(odinEsProduct.getExternalId());
			if (product == null) {
				product = new Product();
				product.setExternalId(odinEsProduct.getExternalId());
			}
			product.setCategory(categoryDao.getByExternalId(odinEsProduct.getGroupExternalId()));
			product.setCode(odinEsProduct.getCode());
			product.setCleanCode(searchService.clearCode(odinEsProduct.getCode()));
			product.setName(odinEsProduct.getName());
			if (StringUtils.hasLength(odinEsProduct.getDescription())) {
				int newStringIndex = odinEsProduct.getDescription().indexOf("\n");
				product.setDescription(newStringIndex > -1 ? odinEsProduct.getDescription().substring(newStringIndex + 1) : "");
				saveCrossCodes(odinEsProduct.getCode(), newStringIndex > -1 ? odinEsProduct.getDescription().substring(0, newStringIndex) : odinEsProduct.getDescription());
			}
			productDao.saveOrUpdate(product);
			logger.info("done");
		}
	}

	private void saveCrossCodes(String code, String crossCodesString) {
		if (StringUtils.hasLength(code)) {
			logger.info("saving cross codes");
			Stream.of(crossCodesString.split(", ")).filter(StringUtils::hasLength).forEach(cross -> {

				String cleanCode = searchService.clearCode(code);
				String cleanCross = searchService.clearCode(cross);

				if (!crossCodeDao.exists(cleanCode, cleanCross)) {
					crossCodeDao.save(new CrossCode(cleanCode, cleanCross));
				}
				if (!crossCodeDao.exists(cleanCross, cleanCode)) {
					crossCodeDao.save(new CrossCode(cleanCross, cleanCode));
				}
			});
		}
	}

	public void saveOdinEsPriceType(OdinEsPriceType odinEsPriceType) {
		logger.info("saving price type : " + odinEsPriceType.getExternalId() + " : " + odinEsPriceType.getName());
		PriceType priceType = priceTypeDao.getByExternalId(odinEsPriceType.getExternalId());
		if (priceType == null) {
			priceType = new PriceType();
			priceType.setExternalId(odinEsPriceType.getExternalId());
		}
		priceType.setName(odinEsPriceType.getName());
		priceTypeDao.saveOrUpdate(priceType);
		logger.info("done");
	}

	public void saveOdinEsOffer(OdinEsOffer odinEsOffer) {
		logger.info("saving offer : " + odinEsOffer.getProductExternalId() + "(" + odinEsOffer.getQuantity() + ")");
		Product product = productDao.getByExternalId(odinEsOffer.getProductExternalId());
		if (product == null) {
			logger.info("skip by product");
		} else {
			product.setQuantity(odinEsOffer.getQuantity());
			productDao.update(product);
			odinEsOffer.getPrices().stream().forEach(odinEsPrice -> {
				logger.info("saving price : " + odinEsPrice.getPriceTypeExternalId() + " : " + odinEsPrice.getPriceForUnit());
				PriceType priceType = priceTypeDao.getByExternalId(odinEsPrice.getPriceTypeExternalId());
				if (priceType == null) {
					throw new IllegalStateException("Price type with external id " + odinEsOffer.getProductExternalId() + " is not exists");
				}
				Price price = priceDao.get(product, priceType);
				if (price == null) {
					price = new Price();
					price.setPriceType(priceType);
					price.setProduct(product);
				}
				price.setValue(odinEsPrice.getPriceForUnit());
				priceDao.saveOrUpdate(price);
			});
			logger.info("done");
		}
	}

	public List<Product> getProducts(long[] ids) {
		return productDao.getByIds(ids);
	}

	public Category getCategoryByExternalId(String externalId) {
		return categoryDao.getByExternalId(externalId);
	}

	public Category getNewPartsCategory() {
		return getCategoryByExternalId(settingsService.getValue(Setting.NEW_PARTS_CATEGORY));
	}

	public void setProductsTotalCounts() {
		List<Category> roots = categoryDao.getRoots();
		calculateProductsTotalCounts(roots);
	}

	private void calculateProductsTotalCounts(List<Category> categories) {
		categories.stream().forEach(category -> {
			if (category.getChildsCount() > 0) {
				calculateProductsTotalCounts(category.getChilds());
			}
			category.setProductsTotalCount(category.getProductsCount() + category.getChilds().stream().mapToInt(Category::getProductsTotalCount).sum());
			categoryDao.update(category);
		});
	}

	public List<PriceType> getPriceTypes() {
		return priceTypeDao.getAll();
	}

	public PriceType getPriceType(long id) {
		return priceTypeDao.getById(id);
	}

	public PriceType getDefaultPriceType() {
		return priceTypeDao.getByExternalId(settingsService.getValue(Setting.DEFAULT_PRICE));
	}

	public void setProductsCategoriesStrings() {
		productDao.findAll().stream().forEach(product -> {
			product.setCategoriesString(getParents(product, categoryDao.getRoots()).stream().map(Category::getName).collect(Collectors.joining(" / ")));
			productDao.update(product);
		});
	}

	public void uploadCategoryImage(long id, byte[] image) {
		try {
			HttpClient client = HttpClientBuilder.create().build();
			HttpPost post = new HttpPost(settingsService.getValue(Setting.IMAGES_SERVER) + settingsService.getValue(Setting.IMAGES_UPLOAD_URL));
			post.setEntity(MultipartEntityBuilder.create().addBinaryBody("image", image, ContentType.MULTIPART_FORM_DATA, "image").build());
			HttpResponse response = client.execute(post);
			String responseString = EntityUtils.toString(response.getEntity());
			JSONObject responseJson = new JSONObject(responseString);
			String imageUrl = settingsService.getValue(Setting.IMAGES_SERVER) + responseJson.getString("image");
			Category category = categoryDao.getById(id);
			category.setImageUrl(imageUrl);
			categoryDao.update(category);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new AutopartException("Ошибка загрузки изображения");
		}
	}

}
