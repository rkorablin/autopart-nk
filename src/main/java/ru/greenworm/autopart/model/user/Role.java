package ru.greenworm.autopart.model.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import ru.greenworm.autopart.model.LongIdentifiable;

@Entity
@Table(name = "roles")
public class Role extends LongIdentifiable {

	@Column(nullable = false, unique = true, length = 100)
	private String name;

	@Column(nullable = false, unique = true)
	int position;

	public Role() {
		
	}
	
	public Role(String name, int position) {
		super();
		this.name = name;
		this.position = position;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

}
