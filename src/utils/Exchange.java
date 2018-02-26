package utils;

public enum Exchange {
	NYSE("NYSE", 1), AMEX("AMEX",2),NASDAQ("NASDAQ",3);
	int id_;
	String name_;

	private Exchange(String name, int id) {
		name_ = name;
		id_ = id;
	}

	
	
	public String getName(){
		return name_;
	}

}
