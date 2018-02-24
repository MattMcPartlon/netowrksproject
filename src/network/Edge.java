package network;

import utils.Pair;

public abstract class Edge {

	Pair<Vertex, Vertex> endpts_;

	public Edge(Vertex u, Vertex v) {
		checkValid(u, v);
		Pair<Vertex, Vertex> endpts = new Pair<>();
		endpts.setFirst(u);
		endpts.setSecond(v);
	}

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

}
