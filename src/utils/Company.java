package utils;

public class Company implements Comparable<Company> {
	Sector sector_;
	Exchange exchange_;
	String ticker_, name_;
	Industry industry_;
	double marketCap_;

	public Company(String ticker, String name, double marketCap, Sector sec, Industry ind, Exchange ex) {
		ticker_ = ticker.trim();
		name_ = name.trim();
		marketCap_ = marketCap;
		sector_ = sec;
		industry_ = ind;
		exchange_ = ex;
	}

	public Company(String symbol) {
		ticker_ = symbol;
	}
	
	public int getSectorID(){
		return sector_.getID();
	}

	public String getName() {
		return name_;
	}

	public String getSymbol() {
		return ticker_;
	}

	@Override
	public int compareTo(Company o) {
		return o.getSymbol().compareTo(this.getSymbol());
	}

}
