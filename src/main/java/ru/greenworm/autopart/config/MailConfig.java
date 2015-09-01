package ru.greenworm.autopart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;

import ru.greenworm.autopart.model.Setting;
import ru.greenworm.autopart.services.SettingsService;

@Configuration
public class MailConfig {

	@Autowired
	private SettingsService settingsService;
	
	@Bean
	public JavaMailSenderImpl javaMailService() {
		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
		javaMailSender.setHost("smtp.yandex.ru");
		javaMailSender.setUsername(settingsService.getValue(Setting.EMAIL_FROM_LOGIN));
		javaMailSender.setPassword(settingsService.getValue(Setting.EMAIL_FROM_PASSWORD));
		javaMailSender.setPort(465);
		javaMailSender.getJavaMailProperties().setProperty("mail.mime.charset", "UTF-8");
		javaMailSender.getJavaMailProperties().setProperty("mail.smtp.starttls.enable", "true");
		javaMailSender.getJavaMailProperties().setProperty("mail.smtp.ssl.enable", "true");
		return javaMailSender;
	}
	
	@Bean
	public MustacheFactory mustacheFactory() {
		MustacheFactory factory = new DefaultMustacheFactory();
		return factory;
	}
}
