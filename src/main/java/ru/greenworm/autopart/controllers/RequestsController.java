package ru.greenworm.autopart.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ru.greenworm.autopart.model.request.Request;
import ru.greenworm.autopart.services.RequestsService;
import ru.greenworm.autopart.utils.JsonUtils;

@Controller
public class RequestsController {

	@Autowired
	private RequestsService requestsService;
	
	@RequestMapping(value = "/request", method = RequestMethod.GET)
	public String showRequestPage() {
		return "request";
	}

	@RequestMapping(value = "/create_request.json", method = RequestMethod.POST, headers = { "Content-type=application/json" })
	public @ResponseBody String createRequest(@RequestBody Request request) {
		requestsService.createRequest(request);
		return JsonUtils.getSuccessJson();
	}

}
