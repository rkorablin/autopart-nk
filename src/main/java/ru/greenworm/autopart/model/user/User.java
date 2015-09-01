package ru.greenworm.autopart.model.user;

import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import ru.greenworm.autopart.model.Address;
import ru.greenworm.autopart.model.LongIdentifiable;
import ru.greenworm.autopart.model.catalog.PriceType;
import ru.greenworm.autopart.utils.StringUtils;

@Entity
@Table(name = "users")
public class User extends LongIdentifiable implements UserDetails {

	@Column(nullable = false)
	private String email;

	@Column(name = "password_hash")
	private String passwordHash;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "second_name")
	private String secondName;

	@Column(name = "patronymic_name")
	private String patronymicName;

	@Column(nullable = false)
	private UserType type;

	@Column(nullable = false)
	private UserStatus status;

	@Column
	private String code;

	@Embedded
	private Address address;

	@Column(name = "organization_name")
	private String organizationName;

	@JoinTable(name = "users_roles", joinColumns = { @JoinColumn(name = "user_id") }, inverseJoinColumns = { @JoinColumn(name = "role_id") })
	@ManyToMany(fetch = FetchType.LAZY)
	private List<Role> roles;

	@JoinColumn(name = "price_type_id", nullable = true)
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	private PriceType priceType;

	@Transient
	private String plainPassword;

	@Transient
	private String plainPasswordConfirm;

	@Transient
	private List<GrantedAuthority> authorities;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	public String getPatronymicName() {
		return patronymicName;
	}

	public void setPatronymicName(String patronymicName) {
		this.patronymicName = patronymicName;
	}

	public UserType getType() {
		return type;
	}

	public void setType(UserType type) {
		this.type = type;
	}

	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public PriceType getPriceType() {
		return priceType;
	}

	public void setPriceType(PriceType priceType) {
		this.priceType = priceType;
	}

	public String getPlainPassword() {
		return plainPassword;
	}

	public void setPlainPassword(String plainPassword) {
		this.plainPassword = plainPassword;
	}

	public String getPlainPasswordConfirm() {
		return plainPasswordConfirm;
	}

	public void setPlainPasswordConfirm(String plainPasswordConfirm) {
		this.plainPasswordConfirm = plainPasswordConfirm;
	}

	public void setAuthorities(List<GrantedAuthority> authorities) {
		this.authorities = authorities;
	}

	public String getFullName() {

		StringBuilder builder = new StringBuilder();
		if (StringUtils.hasLength(secondName)) {
			builder.append(secondName);
		}
		if (StringUtils.hasLength(firstName)) {
			if (builder.length() > 0) {
				builder.append(" ");
			}
			builder.append(firstName);
		}
		if (StringUtils.hasLength(patronymicName)) {
			if (builder.length() > 0) {
				builder.append(" ");
			}
			builder.append(patronymicName);
		}

		if (StringUtils.hasLength(organizationName)) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append(organizationName);
		}
		return builder.toString();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return passwordHash;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return status == UserStatus.ACTIVE;
	}

}
