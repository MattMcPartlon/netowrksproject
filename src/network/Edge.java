package network;

import utils.Pair;

public abstract class Edge implements Comparable<Edge> {

	Pair<Vertex, Vertex> endpts_;

	public Edge(Vertex u, Vertex v) {
		
		endpts_ = new Pair<>();
		endpts_.setFirst(u);
		endpts_.setSecond(v);
	}

	public abstract double getWeight();
	
	public Vertex getU() {
		return endpts_.getFirst();
	}

	public Vertex getV() {
		return endpts_.getSecond();
	}

	public boolean checkValid(Vertex u, Vertex v) {
		if (u == null || v == null || u.equals(v)) {
			throw new IllegalArgumentException(
					"can't add edge with endpoints u: " + u.toString() + ", v: " + v.toString());
		}
		return true;
	}

	public Pair<Vertex, Vertex> getEndpoints() {
		return endpts_;
	}

	public boolean isEndpoint(Vertex v) {
		return this.getU().equals(v) || this.getV().equals(v);
	}

	public abstract boolean isWeighted();

	public abstract boolean isDirected();

	public abstract String toString();

	public abstract boolean equals(Object o);

	public abstract int hashCode();

	public int compareTo(Edge e) {
		int max1 = Math.max(getU().getIndex(), getV().getIndex());
		int min1 = Math.min(getU().getIndex(), getV().getIndex());
		int max2 = Math.max(e.getU().getIndex(), e.getV().getIndex());
		int min2 = Math.min(e.getU().getIndex(), e.getV().getIndex());
		if (min1 < min2) {
			return -1;
		} else if (min1 > min2) {
			return 1;
		} else {
			if (max1 < max2) {
				return -1;
			} else if (max1 > max2) {
				return 1;
			} else {
				return 0;
			}
		}
	}

}
