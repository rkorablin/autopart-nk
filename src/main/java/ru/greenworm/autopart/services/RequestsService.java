package ru.greenworm.autopart.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;

import ru.greenworm.autopart.dao.RequestDao;
import ru.greenworm.autopart.events.RequestEvent;
import ru.greenworm.autopart.model.request.Request;
import ru.greenworm.autopart.model.request.RequestStatus;
import ru.greenworm.autopart.utils.SecurityUtils;

@Service
public class RequestsService implements ApplicationEventPublisherAware {

	private ApplicationEventPublisher publisher;

	@Autowired
	private RequestDao requestDao;

	public Request createRequest(Request request) {
		request.setDate(new Date());
		request.setStatus(RequestStatus.NEW);
		request.setUser(SecurityUtils.getCurrentUser());
		request.getItems().stream().forEach(item -> item.setRequest(request));
		requestDao.save(request);
		publisher.publishEvent(new RequestEvent(this, request));
		return request;
	}

	public List<Request> getRequests(RequestStatus status, int page, int perPage) {
		return requestDao.getList(status, (page - 1) * perPage, perPage);
	}

	public Request setRequestStatus(long requestId, RequestStatus status) {
		Request request = requestDao.getById(requestId);
		request.setStatus(status);
		requestDao.update(request);
		return request;
	}

	public Request getRequest(long id) {
		return requestDao.getById(id);
	}

	public int getRequestsCount(RequestStatus status) {
		return requestDao.getCount(status);
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}

}
