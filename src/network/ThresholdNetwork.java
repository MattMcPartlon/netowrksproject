package network;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import alignment.Alignment;
import scoring.ScoreFunction;
import utils.DataObj;
import utils.Pair;
import utils.StockDataObj;
import utils.Utilities;

public class ThresholdNetwork {

	double threshold_;
	List<StockVertex> V_;
	HashMap<Vertex, HashMap<Vertex, Edge>> E_;

	public ThresholdNetwork(double threshold) {
		V_ = new ArrayList<>();
		E_ = new HashMap<>();
		threshold_ = threshold;
	}

	public List<StockVertex> getVertices() {
		return V_;
	}

	public int getOrder() {
		return V_.size();
	}

	public int getSize() {
		return E_.size();
	}

	public double getEdgeWeight(Vertex u, Vertex v) {
		Edge e = E_.get(u).get(v);
		if (e == null) {
			return Double.MAX_VALUE;
		} else {
			return e.getWeight();
		}

	}

	public void build(Collection<Alignment> alignments, ScoreFunction f) {
		HashMap<String, DataObj> verts = new HashMap<>();
		HashMap<String, HashMap<String, Double>> edges = new HashMap<>();
		HashMap<String, HashMap<String, Alignment>> alignmentsMap = new HashMap<>();

		if (Utilities.TESTMODE) {
			System.out.println("building network!");
		}
		for (Alignment a : alignments) {
			double score = f.getScore(a);
			// if (score > threshold_) {
			DataObj d1 = a.getS1().getData();
			DataObj d2 = a.getS2().getData();
			verts.put(d1.getID(), d1);
			verts.put(d2.getID(), d2);
			if (!edges.containsKey(d1.getID())) {
				edges.put(d1.getID(), new HashMap<String, Double>());
			}
			if (!edges.containsKey(d2.getID())) {
				edges.put(d2.getID(), new HashMap<String, Double>());
			}
			if (!alignmentsMap.containsKey(d1.getID())) {
				alignmentsMap.put(d1.getID(), new HashMap<>());
			}
			if (!alignmentsMap.containsKey(d2.getID())) {
				alignmentsMap.put(d2.getID(), new HashMap<>());
			}
			alignmentsMap.get(d1.getID()).put(d2.getID(), a);
			alignmentsMap.get(d2.getID()).put(d1.getID(), a);

			// add to edges
			edges.get(d1.getID()).put(d2.getID(), score);
			edges.get(d2.getID()).put(d1.getID(), score);
			// }

		}
		if (Utilities.TESTMODE) {
			System.out.println("adding vertices to network");
		}
		// add all vertices
		int index = 0;
		System.out.println("num verts: " + verts.keySet().size());
		for (String key : verts.keySet()) {
			DataObj d = verts.get(key);
			VertexDataObject vdo = new VertexDataObject((StockDataObj) d, index);
			index++;
			StockVertex v = new StockVertex(vdo);
			V_.add(v);
			// initialize edge map
			E_.put(v, new HashMap<>());
		}
		// add edges
		if (Utilities.TESTMODE) {
			System.out.println("adding edges to network!");
		}
		Collections.sort(V_);
		for (int i = 0; i < V_.size(); i++) {
			StockVertex u = V_.get(i);
			for (int j = i + 1; j < V_.size(); j++) {
				StockVertex v = V_.get(j);
				if (u != null && v != null) {
					Alignment al = alignmentsMap.get(u.getData().getID()).get(v.getData().getID());
					double score = edges.get(v.getData().getID()).get(u.getData().getID());
					Edge e = new WeightedEdge(u, v, score, al);
					this.addEdge(e);
				}
			}
		}

	}

	private void addEdge(Edge e) {
		if (e == null) {
			throw new IllegalArgumentException("edge is null!");
		}
		Vertex u = e.getU();
		Vertex v = e.getV();
		if (E_.get(u).get(v) != null) {
			throw new IllegalArgumentException("an edge other than" + e.toString() + " already present");

		}

		E_.get(u).put(v, e);
		E_.get(v).put(u, e);

	}

	public int getDegree(Vertex v) {
		if (E_.get(v) == null) {
			return 0;
		} else {
			return E_.get(v).keySet().size();
		}
	}

	public Collection<Edge> getIncidentEdges(Vertex v) {
		HashSet<Edge> edges = new HashSet<>();
		if (E_.get(v) != null) {
			for (Vertex key : E_.get(v).keySet()) {
				edges.add(E_.get(v).get(key));
			}
		}
		return edges;
	}

	public boolean areAdjacent(Vertex u, Vertex v) {
		return E_.get(u).get(v) != null;
	}

	public Collection<Vertex> getNeighbors(Vertex v) {
		return E_.get(v).keySet();
	}

	public void removeEdge(Edge e) {
		Vertex u = e.getU();
		Vertex v = e.getV();

		if (E_.get(u).get(v) == null || (!E_.get(u).get(v).equals(e))) {
			throw new IllegalArgumentException("edge: " + e.toString() + "does not exist!");
		} else {
			E_.get(u).put(v, null);
			E_.get(v).put(u, null);
		}
	}

	public String getAdjacencyListAsString(Vertex v) {
		double[] adj = new double[V_.size()];
		for (Vertex u : E_.get(v).keySet()) {
			adj[u.getIndex()] = getEdgeWeight(u, v);
		}
		return Utilities.mathematicaFormattedArray(adj);
	}

	public Vertex getVertex(int index) {
		Vertex v = V_.get(index);
		if (v.getIndex() != index) {
			throw new IllegalArgumentException();
		}
		return v;
	}

	public void saveNetwork(File f) {

		String toPrint = "";
		for (StockVertex v : V_) {
			toPrint += this.getAdjacencyListAsString(v) + ", \n";
		}
		try {
			PrintWriter pw = new PrintWriter(f);
			pw.println(toPrint.substring(0, toPrint.lastIndexOf(',')));
			pw.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	//	System.out.println(toPrint.substring(0, toPrint.lastIndexOf(',')) + "};");
	}

	

	
}
