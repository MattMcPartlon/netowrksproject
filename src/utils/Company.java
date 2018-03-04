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
		ticker_ = symbol.trim();
	}

	public int getSectorID() {
		return sector_.getID();
	}

	public String getName() {
		return name_;
	}

	public String getSymbol() {
		return ticker_;
	}
	
	public String toString(){
		return "ticker: "+this.ticker_+", sector: "+this.sector_;
	}

	@Override
	public int compareTo(Company o) {
		return o.getSymbol().compareTo(this.getSymbol());
	}

	public boolean equals(Object o) {
		if (o instanceof Company) {
			Company other = (Company) o;
			boolean b1=this.ticker_.toLowerCase().contains(other.getSymbol().toLowerCase());
			boolean b2=other.getSymbol().toLowerCase().contains(this.ticker_.toLowerCase());
			return b1||b2;
		}
		return false;
	}

}
