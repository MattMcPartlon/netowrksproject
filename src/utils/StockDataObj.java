package utils;

import sequence.encoding.EncodingType;

public class StockDataObj extends DataObj {

	Company company_;
	Time startTime_, endTime_;
	EncodingType eTy_;

	public StockDataObj(Company company, Time start, Time end, EncodingType ty) {
		company_ = company;
		startTime_ = start;
		endTime_ = end;
		eTy_ = ty;

	}

	public StockDataObj(StockDataObj obj, EncodingType ty) {
		company_ = obj.company_;
		startTime_ = obj.startTime_;
		endTime_ = obj.endTime_;
		eTy_ = ty;
	}



	@Override
	public String getID() {
		// TODO Auto-generated method stub
		return company_.getName();
	}

	@Override
	public DataObj clone() {
		return new StockDataObj(company_, startTime_, endTime_,eTy_);
	}

	public Time getStartTime() {
		return startTime_;
	}

	public Time getEndTime() {
		return endTime_;
	}

	public Company getCompany() {
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
