package ru.greenworm.autopart.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ru.greenworm.autopart.exceptions.UserException;
import ru.greenworm.autopart.model.user.User;
import ru.greenworm.autopart.services.UserDetailsService;
import ru.greenworm.autopart.utils.JsonUtils;

@Controller
public class UserDetailsController {

	@Autowired
	private UserDetailsService userDetaildsService;

	@RequestMapping(value = "/registration", method = RequestMethod.GET)
	public String showRegistrationPage() {
		return "registration";
	}

	@RequestMapping(value = "/register.json", method = RequestMethod.POST)
	public @ResponseBody String register(User user) {
		userDetaildsService.register(user);
		return JsonUtils.getSuccessJson();
	}

	@RequestMapping(value = "/activation", method = RequestMethod.GET)
	public String showActivationPage() {
		return "activation";
	}

	@RequestMapping(value = "/activate.json", method = RequestMethod.POST)
	public @ResponseBody String activate(@RequestParam("code") String code) {
		userDetaildsService.activate(code);
		return JsonUtils.getSuccessJson();
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String showLoginPage(HttpServletRequest request, Model model) {
		model.addAttribute("activated", ServletRequestUtils.getBooleanParameter(request, "activated", false));
		return "login";
	}

	@RequestMapping(value = "/profile", method = RequestMethod.GET)
	public String showProfilePage(HttpServletRequest request, Model model) {
		model.addAttribute("user", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		return "profile";
	}

	@RequestMapping(value = "/edit_profile.json", method = RequestMethod.POST)
	public @ResponseBody String editProfile(User user) {
		userDetaildsService.edit(user);
		return JsonUtils.getSuccessJson();
	}

	@RequestMapping("/profile")
	public String showProfilePage(Model model) {
		return "profile";
	}
	
	@ExceptionHandler(UserException.class)
	public @ResponseBody String processException(UserException exception) {
		return JsonUtils.getErrorJson(exception.getMessage());
	}

}
