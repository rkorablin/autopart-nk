package ru.greenworm.autopart.config;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import ru.greenworm.autopart.services.UserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private static final String LOGIN_PAGE_URL = "/login";

	@Autowired
	private UserDetailsService userDetailsService;

	@Bean
	public AuthenticationSuccessHandler authenticationSuccessHandler() {
		SavedRequestAwareAuthenticationSuccessHandler handler = new SavedRequestAwareAuthenticationSuccessHandler();
		handler.setAlwaysUseDefaultTargetUrl(false);
		handler.setDefaultTargetUrl("/");
		handler.setTargetUrlParameter("target_url");
		return handler;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(10, new SecureRandom());
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//@formatter:off
		http
//			.csrf().requireCsrfProtectionMatcher(new CsrfProtectionMatcher())
//		.and()
//			.httpBasic()
//		.and()
			.formLogin().loginProcessingUrl("/security/login").failureUrl("/login?error").loginPage(LOGIN_PAGE_URL).successHandler(authenticationSuccessHandler())
		.and()
			.logout().logoutSuccessUrl("/").logoutRequestMatcher(new AntPathRequestMatcher("/security/logout"))
		.and()
			.authorizeRequests()
				.antMatchers("/registration", "/register.json", "/activation", "/activate.json", "/login").anonymous()
				.antMatchers("/", "/request", "/cart", "/catalog/**", "/search").permitAll()
				.antMatchers("/admin/**").hasRole("ADMIN")
				.anyRequest().authenticated();

		//@formatter:on
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/css/**", "/js/**", "/images/**", "/fonts/**");
	}

	@Bean(name = "authenticationManager")
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
}