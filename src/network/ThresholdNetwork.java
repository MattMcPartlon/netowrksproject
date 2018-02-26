package network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import alignment.Alignment;
import scoring.ScoreFunction;
import utils.DataObj;
import utils.Pair;
import utils.StockDataObj;

public class ThresholdNetwork {

	double threshold_;
	List<StockVertex> V_;
	Collection<Edge> E_;

	public void build(Collection<Alignment> alignments, ScoreFunction f) {
		HashMap<String, DataObj> verts = new HashMap<>();
		HashMap<String, HashMap<String, Double>> edges = new HashMap<>();

		for (Alignment a : alignments) {
			double score = f.getScore(a);
			if (score > threshold_) {
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

				// add to edges
				edges.get(d1.getID()).put(d2.getID(), score);
				edges.get(d2.getID()).put(d1.getID(), score);
			}

		}
		// add all vertices
		int index = 1;

		for (String key : verts.keySet()) {
			DataObj d = verts.get(key);
			VertexDataObject vdo = new VertexDataObject((StockDataObj) d, index);
			index++;
			V_.add(new StockVertex(vdo));
		}
		// add edges
		Collections.sort(V_);
		for (int i = 0; i < V_.size(); i++) {
			StockVertex u = V_.get(i);
			for (int j = i + 1; j < V_.size(); j++) {
				StockVertex v = V_.get(j);
				double score = edges.get(v.getData().getID()).get(u.getData().getID());
				Edge e = new WeightedEdge(u, v, score);
				addEdge(e);
			}
		}

	}
	
	private void addEdge(Edge e){
		if(E_.contains(e)){
			throw new IllegalArgumentException("edge "+e.toString()+" already present");
		}else{
			Vertex u=e.getU();
			Vertex v=e.getV();
			E_.add(e);
			u.addIncidentEdge(e);
			v.addIncidentEdge(e);
		}
	}
	
	public void printNetwork(){
		for (StockVertex v: V_) {
			
		}
	}

}
