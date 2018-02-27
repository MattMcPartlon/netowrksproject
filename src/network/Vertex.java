package network;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

import utils.DataObj;
import utils.Utilities;

public abstract class Vertex {

	VertexDataObject data_;
	HashMap<Vertex, Edge> incidentEdges_;

	public Vertex(VertexDataObject data) {
		data_ = data;
		incidentEdges_ = new HashMap<>();
	}

	public abstract String toString();

	public abstract boolean equals(Object o);

	public abstract int hashCode();

	public void addIncidentEdge(Edge e) {
		if ((!e.getU().equals(this) && !e.getV().equals(this))) {
			throw new IllegalArgumentException("edge: " + e.toString() + " does not have endpoint " + this.toString());
		}
		Vertex other = e.getU().equals(this) ? e.getV() : e.getU();
		if (incidentEdges_.containsKey(other)) {
			throw new IllegalArgumentException("incident edges already contains edge: " + e.toString());
		}
		incidentEdges_.put(other, e);
	}

	public void removeIncidentEdge(Edge e) {
		Vertex other = e.getU().equals(this) ? e.getV() : e.getU();
		if (!(incidentEdges_.remove(other, e))) {
			throw new IllegalArgumentException("edge: " + e.toString() + " not incident to vertex: " + this.toString());
		}
	}

	public Collection<Edge> getIncidentEdges() {
		HashSet<Edge> edges = new HashSet<>();
		for (Vertex key : incidentEdges_.keySet()) {
			edges.add(incidentEdges_.get(key));
		}
		return edges;
	}

	public boolean isAdjacent(Vertex v) {
		return incidentEdges_.containsKey(v);

	}

	public Collection<Vertex> getNeighbors() {
		return incidentEdges_.keySet();
	}

	public int getIndex() {
		return data_.getIndex();
	}

	public DataObj getData() {
		return data_;
	}
	

	


	public String getAdjacencyListAsString(int numVerts) {
		double[] adj = new double[numVerts];
		for (Vertex v : incidentEdges_.keySet()) {
			adj[v.getIndex()] = incidentEdges_.get(v).getWeight();
		}
		return Utilities.mathematicaFormattedArray(adj);

	}

}
