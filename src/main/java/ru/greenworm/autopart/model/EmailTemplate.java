package ru.greenworm.autopart.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "email_templates")
public class EmailTemplate extends LongIdentifiable {

	public static final String REGISTRATION = "registration";
	public static final String ACTIVATION = "activation";
	public static final String ORDER = "order";
	public static final String REQUEST = "request";

	@Column(length = 100, nullable = false)
	private String mnemo;

	@Column(length = 1000, nullable = false)
	private String subject;

	@Column(length = 10000, nullable = false)
	private String html;

	public String getMnemo() {
		return mnemo;
	}

	public void setMnemo(String mnemo) {
		this.mnemo = mnemo;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

}
