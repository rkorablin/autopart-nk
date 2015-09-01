package ru.greenworm.autopart.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.greenworm.autopart.model.Setting;
import ru.greenworm.autopart.services.SearchService;
import ru.greenworm.autopart.services.SettingsService;

@Controller
public class SearchController {

	@Autowired
	private SearchService searchService;
	
	@Autowired
	private SettingsService settingsService;
	
	@RequestMapping("/search")
	public String showSearchPage(@RequestParam("query") String query, Model model) {
		if (searchService.checkQueryLength(query)) {
			model.addAttribute("lengthAccepted", true);
			model.addAttribute("byCode", searchService.getProductsByCode(query));
			model.addAttribute("byCross", searchService.getProductsByCross(query));
			model.addAttribute("byText", searchService.getProductsByText(query));
			searchService.saveQuery(query);
		} else {
			model.addAttribute("lengthAccepted", false);
			model.addAttribute("minLength", settingsService.getAsInteger(Setting.SEARCH_MIN_LENGTH));
		}
		model.addAttribute("query", query);
		return "search";
	}
	
}
