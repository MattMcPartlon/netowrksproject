package utils;

public enum Group {
	DJIA("DJIA", 1);
	String name_;
	int id_;

	private Group(String name, int id) {
		name_ = name;
		id_ = id;
	}

	public static Group getGroup(String company) {
		if(!FieldValues.TESTMODE){
		throw new UnsupportedOperationException();
		}else{
			return Group.DJIA;
		}
	}

}
