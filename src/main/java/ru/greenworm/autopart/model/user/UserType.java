package ru.greenworm.autopart.model.user;

public enum UserType {

	PRIVATE_PERSON {

		@Override
		public String toString() {
			return "Частное лицо";
		}

	},

	ORGANIZATION_AGENT {

		@Override
		public String toString() {
			return "Представитель юридического лица";
		}

	}

}
