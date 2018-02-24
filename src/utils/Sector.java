package utils;

public enum Sector {
	Financial("Financial",1);
	String name_;
	int id_;
	
	private Sector(String name, int id){
		name_=name;
		id_=id;
	}
	
	public static Sector getSector(String company){
		if(!FieldValues.TESTMODE){
			throw new UnsupportedOperationException();
			}else{
				return Financial;
			}
	}

}
