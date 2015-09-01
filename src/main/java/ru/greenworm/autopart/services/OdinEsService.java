package ru.greenworm.autopart.services;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ru.greenworm.autopart.model.Setting;
import ru.greenworm.autopart.model.odines.OdinEsGroup;
import ru.greenworm.autopart.model.odines.OdinEsOffer;
import ru.greenworm.autopart.model.odines.OdinEsPrice;
import ru.greenworm.autopart.model.odines.OdinEsPriceType;
import ru.greenworm.autopart.model.odines.OdinEsProduct;
import ru.greenworm.autopart.utils.StringUtils;
import ru.greenworm.autopart.utils.XmlUtils;

@Service
public class OdinEsService {

	@Autowired
	private CatalogService catalogService;

	@Autowired
	private SettingsService settingsService;
	
	public void processImport(String filename) throws Exception {
		Path filePath = Paths.get(System.getProperty("java.io.tmpdir"), settingsService.getValue(Setting.TEMP_SUBDIRECTORY), filename);
		switch (filename) {
		case "import.xml":
			processImportXml(filePath.toFile());
			break;
		case "offers.xml":
			processOffersXml(filePath.toFile());
			break;
		default:
			break;
		}
	}

	private void processImportXmlGroups(Element groupsElement, String parentExternalId) {
		XmlUtils.stream(groupsElement.getChildNodes()).forEach(group -> {
			OdinEsGroup odinEsGroup = new OdinEsGroup();
			odinEsGroup.setExternalId(XmlUtils.firstChildText(group, "Ид"));
			odinEsGroup.setName(XmlUtils.firstChildText(group, "Наименование"));
			odinEsGroup.setParentExternalId(parentExternalId);
			catalogService.saveOdinEsGroup(odinEsGroup);
			XmlUtils.firstChild(group, "Группы").ifPresent(childs -> processImportXmlGroups(childs, odinEsGroup.getExternalId()));
		});
	}

	private void processImportXmlProducts(Element products) {
		XmlUtils.stream(products.getChildNodes()).forEach(product -> {
			
			OdinEsProduct odinEsProduct = new OdinEsProduct();
			odinEsProduct.setExternalId(XmlUtils.firstChildText(product, "Ид"));
			odinEsProduct.setName(XmlUtils.firstChildText(product, "Наименование"));
			Element group = XmlUtils.firstChild(product, "Группы").get();
			odinEsProduct.setGroupExternalId(XmlUtils.firstChildText(group, "Ид"));
			odinEsProduct.setCode(XmlUtils.firstChildText(product, "Артикул"));
			odinEsProduct.setDescription(XmlUtils.firstChildText(product, "Описание"));
			catalogService.saveOdinEsProduct(odinEsProduct);
		});
		
		catalogService.setProductsTotalCounts();
		catalogService.setProductsCategoriesStrings();
		
	}

	private void processImportXml(File file) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(file);
		Element root = document.getDocumentElement();
		Element classifier = XmlUtils.firstChild(root, "Классификатор").get();
		Element groupsRoot = XmlUtils.firstChild(classifier, "Группы").get();
		processImportXmlGroups(groupsRoot, null);

		Element catalog = XmlUtils.firstChild(root, "Каталог").get();
		Element products = XmlUtils.firstChild(catalog, "Товары").get();
		processImportXmlProducts(products);
	}

	private void processOffersXml(File file) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(file);
		Element root = document.getDocumentElement();
		Element offersPackage = XmlUtils.firstChild(root, "ПакетПредложений").get();
		
		Element pricesTypes = XmlUtils.firstChild(offersPackage, "ТипыЦен").get();
		XmlUtils.stream(pricesTypes.getChildNodes()).forEach(priceType -> {
			OdinEsPriceType odinEsPriceType = new OdinEsPriceType();
			odinEsPriceType.setExternalId(XmlUtils.firstChildText(priceType, "Ид"));
			odinEsPriceType.setName(XmlUtils.firstChildText(priceType, "Наименование"));
			catalogService.saveOdinEsPriceType(odinEsPriceType);
		});
		
		Element offers = XmlUtils.firstChild(offersPackage, "Предложения").get();
		XmlUtils.stream(offers.getChildNodes()).forEach(offer -> {
			OdinEsOffer odinEsOffer = new OdinEsOffer();
			odinEsOffer.setProductExternalId(XmlUtils.firstChildText(offer, "Ид"));
			odinEsOffer.setQuantity((int)StringUtils.parseFloat(XmlUtils.firstChildText(offer, "Количество"), 0));
			XmlUtils.firstChild(offer, "Цены").ifPresent(prices -> {
				XmlUtils.stream(prices.getChildNodes()).forEach(price -> {
					OdinEsPrice odinEsPrice = new OdinEsPrice();
					odinEsPrice.setPriceTypeExternalId(XmlUtils.firstChildText(price, "ИдТипаЦены"));
					odinEsPrice.setPriceForUnit(new BigDecimal(XmlUtils.firstChildText(price, "ЦенаЗаЕдиницу")));
					odinEsOffer.getPrices().add(odinEsPrice);
				});				
			});
			catalogService.saveOdinEsOffer(odinEsOffer);
		});
	}
	
	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

}
