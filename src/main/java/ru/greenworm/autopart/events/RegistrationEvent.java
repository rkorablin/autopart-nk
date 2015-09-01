package ru.greenworm.autopart.events;

import org.springframework.context.ApplicationEvent;

import ru.greenworm.autopart.model.user.User;

public class RegistrationEvent extends ApplicationEvent {

	private User user;

	public RegistrationEvent(Object source, User user) {
		super(source);
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
