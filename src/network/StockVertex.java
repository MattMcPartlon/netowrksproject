package network;

public class StockVertex extends Vertex {

	public StockVertex(VertexDataObject data) {
		super(data);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return data_.getID();
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		StockVertex other;
		if (o instanceof StockVertex) {
			other = (StockVertex) o;
			return other.getData().equals(data_);
		}
		return false;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return data_.getIndex();
	}

}
