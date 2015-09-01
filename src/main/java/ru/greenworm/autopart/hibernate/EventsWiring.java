package ru.greenworm.autopart.hibernate;

import javax.annotation.PostConstruct;

import org.hibernate.SessionFactory;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventsWiring {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private ProductPostLoadListener productPostLoadListener;
	
	@PostConstruct
	public void registerListeners() {
		final EventListenerRegistry registry = ((SessionFactoryImpl) sessionFactory).getServiceRegistry().getService(EventListenerRegistry.class);
		registry.getEventListenerGroup(EventType.POST_LOAD).appendListener(productPostLoadListener);
	}
}