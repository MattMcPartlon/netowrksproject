package network;

public class WeightedEdge extends Edge {

	double weight_;

	public WeightedEdge(Vertex u, Vertex v, double weight) {
		super(u, v);
		
		weight_ = weight;
		// TODO Auto-generated constructor stub
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

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return this.toString().hashCode();
	}

}
