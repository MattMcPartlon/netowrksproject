package network;

import utils.DataObj;
import utils.StockDataObj;

public class VertexDataObject extends DataObj {

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
		if(obj instanceof VertexDataObject){
			VertexDataObject o= (VertexDataObject)obj;
			return o.getData().equals(this.getData())&&this.getIndex()==o.getIndex();
		}
		return false;
	}

}
