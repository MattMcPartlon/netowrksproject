package utils;

public enum Exchange {
	NYSE("NYSE", 1);
	int id_;
	String name_;

	private Exchange(String name, int id) {
		name_ = name;
		id_ = id;
	}

	public static Exchange getExchange(String name) {
		name = name.toLowerCase();
		if (!FieldValues.TESTMODE) {
			throw new UnsupportedOperationException();
		} else {
			return Exchange.NYSE;
		}
	}

}
