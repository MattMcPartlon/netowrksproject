package utils;

import sequence.encoding.EncodingType;

public class StockDataObj extends DataObj {

	Sector sector_;
	Exchange exchange_;
	Group group_;
	String startTime_, endTime_, company_;
	EncodingType eTy_;

	public StockDataObj(String company, String exchange, String start, String end, EncodingType ty) {
		company_ = company;
		exchange_ = Exchange.getExchange(exchange);
		group_ = Group.getGroup(company);
		sector_ = Sector.getSector(company);
		startTime_ = start;
		endTime_ = end;
		eTy_ = ty;

	}

	public StockDataObj(StockDataObj obj, EncodingType ty) {
		company_ = obj.company_;
		exchange_ = obj.exchange_;
		group_ = obj.group_;
		sector_ = obj.sector_;
		startTime_ = obj.startTime_;
		endTime_ = obj.endTime_;
		eTy_ = ty;
	}

	public StockDataObj(String company, Exchange exchange, String start, String end) {
		company_ = company;
		exchange_ = exchange;
		group_ = Group.getGroup(company);
		sector_ = Sector.getSector(company);
		startTime_ = start;
		endTime_ = end;

	}

	@Override
	public String getID() {
		// TODO Auto-generated method stub
		return company_;
	}

	@Override
	public DataObj clone() {
		return new StockDataObj(company_, exchange_, startTime_, endTime_);
	}

	public String getStartTime() {
		return startTime_;
	}

	public String getEndTime() {
		return endTime_;
	}

	public String getCompany() {
		return company_;
	}

	public EncodingType getEncodingType() {
		return eTy_;
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if (o instanceof StockDataObj) {
			StockDataObj other = (StockDataObj) o;
			boolean w, x, y, z;
			w = this.getEncodingType() == other.getEncodingType();
			x = this.getCompany().equals(other.getCompany());
			y = this.getStartTime().equals(other.getStartTime());
			z = this.getEndTime().equals(other.getEndTime());
			return w && x && y && z;
		}
		return false;
	}

}
