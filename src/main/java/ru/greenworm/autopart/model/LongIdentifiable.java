package ru.greenworm.autopart.model;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.Hibernate;
import org.hibernate.annotations.GenericGenerator;

@MappedSuperclass
public class LongIdentifiable {

	@Id
	@GeneratedValue(generator = "per_table_sequence_generator")
	@GenericGenerator(name = "per_table_sequence_generator", strategy = "ru.greenworm.autopart.hibernate.PerTableSequenceGenerator")
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		return ((getId() == null) ? super.hashCode() : getId().hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof LongIdentifiable)) {
			return false;
		}
		if (obj == null || Hibernate.getClass(getClass()) != Hibernate.getClass(obj.getClass())) {
			return false;
		}
		LongIdentifiable other = (LongIdentifiable) obj;
		return getId() != null && other.getId() != null && getId().equals(other.getId());
	}

}
