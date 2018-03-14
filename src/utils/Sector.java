package utils;

public enum Sector {
	Financial("Finance", 1), CapitalGoods("Capital Goods", 2), Healthcare("Health Care", 3), Energy("Energy",
			4), Technology("Technology", 5), ConsumerDurables("Consumer Durables", 6), ConsumerNonDurables(
					"Consumer Non-Durables", 7), ConsumerServices("Consumer Services", 8), PublicUtilities(
							"Public Utilities", 9), Transportation("Transportation", 10), Miscellaneous("Miscellaneous",
									11), BasicIndustries("Basic Industries", 12), Materials("materials",
											14), Industrials("industrials", 15), RealEstate("real estate",
													19), ConsumerDiscretionary("consumer discretionary",20),ConsumerStaples("consumer staples", 16), Utilities("utilities",
															18), TeleCommunication("telecomm", 17), NA("n/a", 13);

	String name_;
	int id_;

	private Sector(String name, int id) {
		name_ = name;
		id_ = id;
	}

	public int getID() {
		return id_;
	}

	public String getName() {
		return name_;
	}

}
