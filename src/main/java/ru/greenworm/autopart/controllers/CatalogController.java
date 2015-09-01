package ru.greenworm.autopart.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ru.greenworm.autopart.model.catalog.Category;
import ru.greenworm.autopart.model.catalog.Product;
import ru.greenworm.autopart.services.CatalogService;

@Controller
@RequestMapping("/catalog")
public class CatalogController {

	@Autowired
	private CatalogService catalogService;

	@RequestMapping("/category/{id}")
	public String showCategoryPage(Model model, @PathVariable("id") long id) {
		Category category = catalogService.getCategory(id);
		model.addAttribute("category", category);
		model.addAttribute("childs", catalogService.getCategories(category));
		model.addAttribute("products", catalogService.getProducts(category));
		Category newPartsCategory = catalogService.getNewPartsCategory();
		model.addAttribute("parents", catalogService.getParents(category, newPartsCategory));
		return "category";
	}

	@RequestMapping("/products.json")
	public @ResponseBody List<Product> getProducts(@RequestParam("id[]") long[] ids) {
		return catalogService.getProducts(ids);
	}

}
