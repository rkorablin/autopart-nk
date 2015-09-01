package ru.greenworm.autopart.services;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import ru.greenworm.autopart.dao.EmailTemplateDao;
import ru.greenworm.autopart.events.ActivationEvent;
import ru.greenworm.autopart.events.OrderEvent;
import ru.greenworm.autopart.events.RegistrationEvent;
import ru.greenworm.autopart.events.RequestEvent;
import ru.greenworm.autopart.model.EmailTemplate;
import ru.greenworm.autopart.model.Setting;
import ru.greenworm.autopart.model.order.Order;
import ru.greenworm.autopart.model.request.Request;
import ru.greenworm.autopart.model.user.User;

import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

@Service
public class EmailService implements ApplicationListener<ApplicationEvent> {

	@Autowired
	private JavaMailSenderImpl javaMailSender;

	@Autowired
	private SettingsService settingsService;

	@Autowired
	private MustacheFactory mustacheFactory;

	@Autowired
	private EmailTemplateDao emailTemplateDao;

	private String executeMustache(String name, String template, Map<String, Object> scope) {
		Mustache mustache = mustacheFactory.compile(new StringReader(template), name);
		StringWriter writer = new StringWriter();
		mustache.execute(writer, scope);
		writer.flush();
		return writer.toString();
	}

	private void send(String to, String subject, String text) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("noreply@autopart-nk.ru");
		message.setSubject(subject);
		message.setTo(to);
		message.setText(text);
		javaMailSender.getSession().setDebug(true);
		javaMailSender.send(message);
	}

	private void send(String to, EmailTemplate template, Map<String, Object> scope) {
		String subject = executeMustache(template.getMnemo() + ".subject", template.getSubject(), scope);
		String text = executeMustache(template.getMnemo() + ".text", template.getHtml(), scope);
		send(to, subject, text);
	}

	private void onRegistration(User user) {
		Map<String, Object> scope = new HashMap<String, Object>();
		scope.put("user", user);
		send(user.getEmail(), emailTemplateDao.getByMnemo(EmailTemplate.REGISTRATION), scope);
	}

	private void onActivation(User user) {
		Map<String, Object> scope = new HashMap<String, Object>();
		scope.put("user", user);
		send(user.getEmail(), emailTemplateDao.getByMnemo(EmailTemplate.ACTIVATION), scope);
	}

	private void onOrder(Order order) {
		Map<String, Object> scope = new HashMap<String, Object>();
		scope.put("order", order);
		send(settingsService.getValue(Setting.EMAIL_TO), emailTemplateDao.getByMnemo(EmailTemplate.ORDER), scope);
	}

	private void onRequest(Request request) {
		Map<String, Object> scope = new HashMap<String, Object>();
		scope.put("request", request);
		send(settingsService.getValue(Setting.EMAIL_TO), emailTemplateDao.getByMnemo(EmailTemplate.REQUEST), scope);
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof RegistrationEvent) {
			onRegistration(((RegistrationEvent) event).getUser());
		} else if (event instanceof ActivationEvent) {
			onActivation(((ActivationEvent) event).getUser());
		} else if (event instanceof OrderEvent) {
			onOrder(((OrderEvent) event).getOrder());
		} else if (event instanceof RequestEvent) {
			onRequest(((RequestEvent) event).getRequest());
		}
	}

	public List<EmailTemplate> getTemplates() {
		return emailTemplateDao.getAll();
	}

	public EmailTemplate getTemplate(long id) {
		return emailTemplateDao.getById(id);
	}

	public void editTemplate(EmailTemplate template) {
		emailTemplateDao.update(template);
	}

}
