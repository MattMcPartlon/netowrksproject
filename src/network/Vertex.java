package network;

import java.util.Collection;
import java.util.HashSet;

import utils.DataObj;

public abstract class Vertex {

	VertexDataObject data_;
	Collection<Edge> incidentEdges_;

	public Vertex(VertexDataObject data) {
		data_ = data;
		incidentEdges_ = new HashSet<Edge>();
	}

	public abstract String toString();

	public abstract boolean equals(Object o);

	public abstract int hashCode();

	public void addIncidentEdge(Edge e) {
		if ((!e.getU().equals(this) && !e.getV().equals(this))) {
			throw new IllegalArgumentException("edge: " + e.toString() + " does not have endpoint " + this.toString());
		}
		if (incidentEdges_.contains(e)) {
			throw new IllegalArgumentException("incident edges already contains edge: " + e.toString());
		}
		incidentEdges_.add(e);
	}

	public void removeIncidentEdge(Edge e) {
		if (!incidentEdges_.remove(e)) {
			throw new IllegalArgumentException("edge: " + e.toString() + " not incident to vertex: " + this.toString());
		}
	}

	public Collection<Edge> getIncidentEdges() {
		return incidentEdges_;
	}

	public boolean isAdjacent(Vertex v) {
		for (Edge e : incidentEdges_) {
			if (e.isEndpoint(v)) {
				return true;
			}
		}
		return false;
	}

	public Collection<Vertex> getNeighbors() {
		Collection<Vertex> neighbors = new HashSet<Vertex>();
		for (Edge e : incidentEdges_) {
			if (e.getU().equals(this)) {
				neighbors.add(e.getV());
			}
		}
		return neighbors;
	}
	
	public int getIndex(){
		return data_.getIndex();
	}

	public DataObj getData() {
		return data_;
	}

}
