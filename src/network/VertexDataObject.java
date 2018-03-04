package network;

import utils.Company;
import utils.DataObj;
import utils.StockDataObj;

public  class VertexDataObject extends DataObj {

	int index_;
	StockDataObj dat_;

	public VertexDataObject(StockDataObj obj, int index) {
		index_ = index;
		dat_ = (StockDataObj) obj.clone();
	}

	@Override
	public String getID() {

		return dat_.getID();
	}

	public int getSectorID() {
		return dat_.getCompany().getSectorID();
	}
	
	public Company getCompany(){
		return dat_.getCompany();
	}

	public void setIndex(int index) {
		index_ = index;
	}

	@Override
	public DataObj clone() {

		return new VertexDataObject(dat_, index_);
	}

	public int getIndex() {
		return index_;
	}

	public DataObj getData() {
		return dat_;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof VertexDataObject) {
			VertexDataObject o = (VertexDataObject) obj;
			return o.getData().equals(this.getData()) && this.getIndex() == o.getIndex();
		}
		return false;
	}

	public int hashCode() {
		return this.dat_.getID().hashCode();

	}

	@Override
	public String toString() {
		return "company: "+getCompany().toString()+"index: "+index_;
	}
}
