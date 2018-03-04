package network;

import alignment.Alignment;
import sequence.Sequence;
import stats.CorrelationAlignmentStats;
import utils.StockDataObj;

public class WeightedEdge extends Edge {

	double weight_;
	Alignment a_;

	public WeightedEdge(Vertex u, Vertex v, double weight, Alignment a) {
		super(u, v);
		a_ = a;
		weight_ = weight;
		// TODO Auto-generated constructor stub
	}

	public double getAvgGapOffset(Vertex u) {
		if (!endpts_.contains(u)) {
			throw new IllegalArgumentException("u is not incidednt to this edge!");
		}
		VertexDataObject obj = (VertexDataObject) u.getData();
		Sequence s1 = a_.getS1();
		StockDataObj obj1 = (StockDataObj) s1.getData();
		double offset = 0.0;

		CorrelationAlignmentStats stats = new CorrelationAlignmentStats();
		
		if (obj.getCompany().equals(obj1.getCompany())) {
			offset = stats.getAverageOffset(a_, s1);
		} else {
			Sequence s2 = a_.getS2();
			StockDataObj obj2 = (StockDataObj) s2.getData();
			if (obj.getCompany().equals(obj2.getCompany())) {
				offset = stats.getAverageOffset(a_, s2);
			} else {
				System.out.println("obj1:"+obj1.getCompany().getSymbol());
				System.out.println("obj2:"+obj2.getCompany().getSymbol());
				System.out.println("obj:"+obj.getCompany().getSymbol());
				throw new IllegalArgumentException("error!");
			}
		}
		return offset;
	}

	public double getWeight() {

		return weight_;
	}

	@Override
	public boolean isWeighted() {
		return true;
	}

	@Override
	public boolean isDirected() {

		return false;
	}

	@Override
	public String toString() {
		return "(" + this.getU().getIndex() + "," + this.getV().getIndex() + ")";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof WeightedEdge) {
			WeightedEdge o = (WeightedEdge) obj;
			return this.toString().equals(o.toString()) && this.getWeight() == o.getWeight();
		}
		return false;
	}

	public Alignment getAlignment() {
		return a_;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return this.toString().hashCode();
	}

}
