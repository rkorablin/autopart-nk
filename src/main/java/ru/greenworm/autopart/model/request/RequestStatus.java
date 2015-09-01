package ru.greenworm.autopart.model.request;

public enum RequestStatus {

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
