package ru.greenworm.autopart.events;

import org.springframework.context.ApplicationEvent;

import ru.greenworm.autopart.model.request.Request;

public class RequestEvent extends ApplicationEvent {

	private Request request;

	public RequestEvent(Object source, Request request) {
		super(source);
		this.request = request;
	}

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

}
