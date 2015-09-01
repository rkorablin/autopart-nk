package ru.greenworm.autopart.controllers;

import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.greenworm.autopart.model.catalog.Category;
import ru.greenworm.autopart.model.order.Order;
import ru.greenworm.autopart.services.CatalogService;

@Controller
public class IndexController {

	@Autowired
	private CatalogService catalogService;

	@RequestMapping("/")
	public String showIndexPage(Model model, HttpServletRequest request) {
		Category newPartsCategory = catalogService.getNewPartsCategory();
		model.addAttribute("categories", newPartsCategory != null ? newPartsCategory.getChilds() : Collections.emptyList());
		model.addAttribute("productsTotalCount", newPartsCategory != null ? newPartsCategory.getProductsTotalCount() : 0);
		return "index";
	}

}
