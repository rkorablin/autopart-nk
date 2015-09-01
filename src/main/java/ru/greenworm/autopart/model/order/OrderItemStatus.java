package ru.greenworm.autopart.model.order;

public enum OrderItemStatus {

	NEW {

		@Override
		public String toString() {
			return "Новый";
		}

	},

	PROCESSING {

		@Override
		public String toString() {
			return "В обработке";
		}

	},

	FINISHED {

		@Override
		public String toString() {
			return "Завершен";
		}

	}

}
