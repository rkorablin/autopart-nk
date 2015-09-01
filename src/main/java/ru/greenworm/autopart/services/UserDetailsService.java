package ru.greenworm.autopart.services;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ru.greenworm.autopart.dao.catalog.PriceDao;
import ru.greenworm.autopart.dao.catalog.PriceTypeDao;
import ru.greenworm.autopart.dao.user.RoleDao;
import ru.greenworm.autopart.dao.user.UserDao;
import ru.greenworm.autopart.events.ActivationEvent;
import ru.greenworm.autopart.events.RegistrationEvent;
import ru.greenworm.autopart.exceptions.UserException;
import ru.greenworm.autopart.model.Address;
import ru.greenworm.autopart.model.catalog.Price;
import ru.greenworm.autopart.model.catalog.PriceType;
import ru.greenworm.autopart.model.catalog.Product;
import ru.greenworm.autopart.model.user.Role;
import ru.greenworm.autopart.model.user.User;
import ru.greenworm.autopart.model.user.UserStatus;
import ru.greenworm.autopart.model.user.UserType;
import ru.greenworm.autopart.utils.RandomUtils;
import ru.greenworm.autopart.utils.StringUtils;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService, ApplicationEventPublisherAware {

	private ApplicationEventPublisher applicationEventPublisher;

	@Autowired
	private UserDao userDao;

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private PriceTypeDao priceTypeDao;

	@Autowired
	private PriceDao priceDao;

	@Autowired
	private CatalogService catalogService;

	@Autowired
	private SettingsService settingService;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userDao.getByEmail(username);
		if (user == null) {
			throw new UsernameNotFoundException("Пользователь с e-mail'ом " + username + " не найден");
		}
		user.setAuthorities(roleDao.getByUser(user).stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName())).collect(Collectors.toList()));
		return user;
	}

	@Transactional(rollbackOn = Exception.class)
	public User register(User user) throws UserException {
		checkFields(user);
		if (!userDao.checkUniqueEmail(user)) {
			throw new UserException("Пользователь с таким e-mail'ом уже зарегистрирован");
		}
		if (StringUtils.isEmpty(user.getPlainPassword())) {
			throw new UserException("Пароль не задан");
		}
		if (!user.getPlainPassword().equals(user.getPlainPasswordConfirm())) {
			throw new UserException("Пароль и подтверждение не совпадают");
		}
		user.setPasswordHash(passwordEncoder.encode(user.getPlainPassword()));
		user.setStatus(UserStatus.NEW);
		user.setCode(RandomUtils.nextString(20));
		user.setRoles(Arrays.asList(roleDao.getByName("USER")));
		user.setPriceType(catalogService.getDefaultPriceType());
		userDao.save(user);
		applicationEventPublisher.publishEvent(new RegistrationEvent(this, user));
		return user;
	}

	@Transactional(rollbackOn = Exception.class)
	public User activate(String code) throws UserException {
		User user = userDao.getByCode(code);
		if (user == null) {
			throw new UserException("Неверный код активации");
		}
		if (!userDao.checkUniqueEmail(user)) {
			throw new UserException("Пользователь с таким e-mail'ом уже зарегистрирован");
		}
		user.setCode(null);
		user.setStatus(UserStatus.ACTIVE);
		userDao.update(user);
		applicationEventPublisher.publishEvent(new ActivationEvent(this, user));
		return user;
	}

	public User edit(User requestUser) throws UserException {
		User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		BeanUtils.copyProperties(requestUser, currentUser, "id", "email", "passwordHash", "status", "roles");
		checkFields(currentUser);
		if (StringUtils.hasLength(requestUser.getPlainPassword())) {
			if (!requestUser.getPlainPassword().equals(requestUser.getPlainPasswordConfirm())) {
				throw new UserException("Пароль и подтверждение не совпадают");
			}
			currentUser.setPasswordHash(passwordEncoder.encode(requestUser.getPlainPassword()));
		}
		userDao.update(currentUser);
		return currentUser;
	}

	public User editOther(User requestUser, long[] roleIds, long priceTypeId) throws UserException {
		User persistUser = userDao.getById(requestUser.getId());
		List<Role> roles = roleDao.getByIds(roleIds);
		PriceType priceType = priceTypeDao.getById(priceTypeId);
		BeanUtils.copyProperties(requestUser, persistUser, "id", "passwordHash", "status");
		checkFields(persistUser);
		if (StringUtils.hasLength(requestUser.getPlainPassword())) {
			if (!requestUser.getPlainPassword().equals(requestUser.getPlainPasswordConfirm())) {
				throw new UserException("Пароль и подтверждение не совпадают");
			}
			persistUser.setPasswordHash(passwordEncoder.encode(requestUser.getPlainPassword()));
		}
		persistUser.setRoles(roles);
		persistUser.setPriceType(priceType);
		userDao.update(persistUser);
		return persistUser;
	}

	public User getUser(long id) {
		return userDao.getById(id);
	}

	public List<User> getUsers(String email, String name, PriceType priceType, int page, int perPage) {
		return userDao.getList(email, name, priceType, (page - 1) * perPage, perPage);
	}

	public int getUsersCount(String email, String name, PriceType priceType) {
		return userDao.getCount(email, name, priceType);
	}

	public int getUsersTotalCount() {
		return userDao.getCount(null, null, null);
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}

	private void checkFields(User user) {
		if (StringUtils.isAnyEmpty(user.getFirstName(), user.getSecondName()) || (user.getType() == UserType.ORGANIZATION_AGENT && StringUtils.isEmpty(user.getOrganizationName()))) {
			throw new UserException("Заполнены не все обязательные поля");
		}
	}

	private void createRoleIfNoExists(String name, int position) {
		if (!roleDao.exists(name)) {
			roleDao.save(new Role(name, position));
		}
	}

	public List<Role> getUserRoles(User user) {
		return roleDao.getByUser(user);
	}

	public List<Role> getAllRoles() {
		return roleDao.getAll();
	}

	public BigDecimal getUserPrice(Product product, User user) {
		PriceType priceType = user != null && user.getPriceType() != null ? user.getPriceType() : catalogService.getDefaultPriceType();
		Price price = priceDao.get(product, priceType);
		return price != null ? price.getValue() : BigDecimal.ZERO;
	}
}
