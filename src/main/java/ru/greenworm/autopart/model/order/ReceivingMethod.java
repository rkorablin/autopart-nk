package ru.greenworm.autopart.model.order;

public enum ReceivingMethod {

	SELF {

		@Override
		public String toString() {
			return "Самовывоз";
		}

	},

	DELIVERY {

		@Override
		public String toString() {
			return "Доставка";
		}

	}

}
