package network.utils;

import java.awt.font.NumericShaper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;

import org.omg.CosNaming.NamingContextExtPackage.AddressHelper;

import network.Edge;
import network.StockVertex;
import network.ThresholdNetwork;
import network.Vertex;

public class GraphAlgorithms {

	public double getClusteringCoefficient(ThresholdNetwork network) {
		double[] counts = countSubgraphs(network);
		return 3 * counts[3] / (counts[2]);
	}

	private double[] countSubgraphs(ThresholdNetwork network) {
		List<StockVertex> vertices = network.getVertices();
		double[] counts = new double[4];
		for (int i = 0; i < vertices.size(); i++) {
			Vertex x = vertices.get(i);
			for (int j = i + 1; j < vertices.size(); j++) {
				Vertex y = vertices.get(j);
				for (int k = 0; k < vertices.size(); k++) {
					Vertex z = vertices.get(k);
					int numEdges = 0;
					numEdges += network.areAdjacent(x, y) ? 1 : 0;
					numEdges += network.areAdjacent(x, z) ? 1 : 0;
					numEdges += network.areAdjacent(y, z) ? 1 : 0;
					counts[numEdges] += 1;
				}
			}
		}
		return counts;

	}

	public double[] getDegreeDistribution(ThresholdNetwork network) {
		List<StockVertex> vertices = network.getVertices();
		int n = network.getOrder();
		double[] dist = new double[n];
		for (int i = 0; i < n; i++) {
			dist[network.getDegree(vertices.get(i))] += 1;
		}
		for (int i = 0; i < n; i++) {
			dist[i] = dist[i] / (n + 0.0);
		}
		return dist;
	}

	/**
	public double[] getBetweennessCentrality(ThresholdNetwork network) {
		// g(v)=sum {s \new v \new t) (P_{s,t}(v) / P_{s,t})
		HashMap<Integer, HashMap<Integer, Path>> paths = computeShortestPaths(network);
		int n = network.getOrder();
		Double[] dist = new Double[n];

		for (int i = 0; i < n; i++) {
			if (paths.containsKey(i)) {
				HashMap<Integer, Path> temp = paths.get(i);
				for (int j = 0; j < n; j++) {
					if (temp.containsKey(j)) {
						Set<Vertex> interior = paths.get(i).get(j).getInterior();
						for (Vertex v : interior) {
							// this vertex appears in the interior of a shortest
							// u-v path.
							dist[v.getIndex()] += 1;
						}
					}
				}
			}

		}

	}
	*/

	public List<Path> getAllShortestPaths(Vertex u, ThresholdNetwork net) {
		// going to use dijkstra's algorithm... tried to modify floyd warshall,
		// but it got confusing pretty quickly.
		PriorityQueue<DijkstraVertex> verts = new PriorityQueue<>();
		HashMap<Integer, DijkstraVertex> vertMap = new HashMap<>();
		List<StockVertex> V = net.getVertices();
		for (int i = 0; i < V.size(); i++) {
			Vertex v = V.get(i);
			if (v.getIndex() != u.getIndex()) {
				DijkstraVertex vtx = new DijkstraVertex(v.getIndex());
				verts.add(vtx);
				vertMap.put(u.getIndex(), vtx);
			} else {
				DijkstraVertex vtx = new DijkstraVertex(v.getIndex());
				vtx.dist_ = 0.0;
				vertMap.put(u.getIndex(), vtx);
			}
		}

		while (verts.size() > 0) {
			DijkstraVertex currentV = verts.poll();
			Vertex current = V.get(currentV.v_);

			for (Edge e : net.getIncidentEdges(current)) {

				Vertex opp = e.getOpposite(current);
				DijkstraVertex temp = vertMap.get(opp.getIndex());
				Double dist = currentV.dist_ + e.getWeight();
				if (dist < temp.dist_) {
					temp.dist_ = dist;
					temp.setPrev(currentV);
				} else if (dist == temp.dist_) {
					temp.addPrev(currentV);
				}
				// update pq
				verts.remove(temp);
				verts.add(temp);
			}
		}
		// when we are done we can reconstruct the paths
		return null;
	}

	public HashMap<DijkstraVertex, ArrayList<Path>> reconstructShortestPaths(HashMap<Integer, DijkstraVertex> verts,
			DijkstraVertex source) {
		HashMap<DijkstraVertex, ArrayList<Path>> paths = new HashMap<>();
		PriorityQueue<DijkstraVertex> q = new PriorityQueue<>();

		for (Integer i : verts.keySet()) {
			q.add(verts.get(i));
		}
		while (!q.isEmpty()) {
			DijkstraVertex curr1 = q.poll();
			paths.put(curr1, new ArrayList<Path>());
			if (curr1.v_ == source.v_) {
				Path p = new Path(0.0);
				p.add(curr1);
				paths.get(curr1).add(p);

			} else if (curr1.prev_.size() > 0) {
				for (DijkstraVertex vtx : curr1.prev_) {
					if (paths.containsKey(vtx)) {
						for (Path p : paths.get(vtx)) {
							Path p2 = new Path(p);
							p2.add(curr1);
							paths.get(curr1).add(p2);
						}
					} else {
						ArrayList<Path> paths2 = getPaths(source, vtx);
						for (Path p : paths2) {
							Path p2 = new Path(p);
							p2.add(curr1);
							paths.get(curr1).add(p2);
						}
					}
				}
			}
		}

		return paths;
	}

	public ArrayList<Path> getPaths(DijkstraVertex source, DijkstraVertex sink) {
		ArrayList<Path> myPaths = new ArrayList<>();
		if (source.v_ == sink.v_) {
			Path p = new Path(0.0);
			p.add(source);
			myPaths.add(p);
		} else {
			for (DijkstraVertex v : sink.prev_) {
				List<Path> pv = getPaths(source, v);
				for (Path p : pv) {
					p.add(sink);
					myPaths.add(p);
				}
			}

		}
		return myPaths;
	}

	/**
	 * public HashMap<Integer, HashMap<Integer, Path>>
	 * computeShortestPaths(ThresholdNetwork network) { // floyd warshall
	 * algorithm int n = network.getOrder(); List<StockVertex> V =
	 * network.getVertices(); Double[][] dists = new Double[n][n];
	 * HashMap<Integer,HashMap<Integer,ArrayList<Vertex>>> next = new HashMap();
	 * // initialize dp table for (int i = 0; i < dists.length; i++){
	 * StockVertex u = V.get(i); next.put(u.getIndex(),new
	 * HashMap<Integer,ArrayList<Integer>>()); for (int j = 0; j < dists.length;
	 * j++) { StockVertex v = V.get(j); int vI = v.getIndex(), uI =
	 * u.getIndex(); next.get(uI).put(vI,new ArrayList<Vertex>()); if (uI == vI)
	 * { dists[uI][vI] = 0.0; } else if (network.areAdjacent(u, v)) {
	 * dists[uI][vI] = network.getEdgeWeight(u, v); next.get(uI).get(vI).add(v);
	 * } else { dists[uI][vI] = Double.MAX_VALUE; } } } // compute shortest
	 * paths for (int i = 0; i < dists.length; i++) { StockVertex u = V.get(i);
	 * for (int j = 0; j < dists.length; j++) { StockVertex v = V.get(j); for
	 * (int k = 0; k < dists.length; k++) { StockVertex w = V.get(k); int ui =
	 * u.getIndex(), vj = v.getIndex(), wk = w.getIndex(); if (dists[ui][vj] >
	 * dists[ui][wk] + dists[wk][vj]) { dists[ui][vj] = dists[ui][wk] +
	 * dists[wk][vj]; next.get(ui).put(vj, new ArrayList<>()); //w is on a
	 * shortest u-v path next.get(ui).get(vj).add(w); }else if(dists[ui][vj] ==
	 * dists[ui][wk] +
	 * dists[wk][vj]&&wk!=ui&&wk!=vj&&dists[i][j]!=Double.MAX_VALUE){
	 * next.get(ui).get(vj).add(w); } } } } // we now have all pairs shortest
	 * paths info. next step is to recover // paths. HashMap<Integer,
	 * HashMap<Integer, Path>> paths = new HashMap<>(); for (int i = 0; i <
	 * next.length; i++) { Vertex u = V.get(i); paths.put(u.getIndex(), new
	 * HashMap<>()); for (int j = 0; j < next.length; j++) {
	 * 
	 * Vertex v = V.get(j); Path p = getPath(u, v, next,
	 * dists[u.getIndex()][v.getIndex()]); if (p.length() >= 2) {
	 * paths.get(u.getIndex()).put(v.getIndex(), p); } } } return paths;
	 * 
	 * }
	 * 
	 * private Path getPath(Vertex u, Vertex v, Vertex[][] next, double weight)
	 * { Path p = new Path(weight); int numElts = 0; if
	 * (next[u.getIndex()][v.getIndex()] != null) { p.add(u); while
	 * (u.getIndex() != v.getIndex() && numElts <= next.length + 1) { numElts++;
	 * v = next[u.getIndex()][v.getIndex()]; p.add(v); } } if (numElts >
	 * next.length) { throw new IllegalArgumentException(
	 * "error reconstructing paths!");
	 * 
	 * } return p; } /*
	 * 
	 * @author matt
	 *
	 */

	public class Path implements Comparable<Path> {
		List<DijkstraVertex> path_;
		Double weight_;

		public Path(Path p) {
			weight_ = p.weight_;
			path_ = new ArrayList<>(p.path_);
		}

		public Path(Double weight) {
			weight_ = weight;
			path_ = new ArrayList<>();
		}

		public Set<DijkstraVertex> getInterior() {
			Set<DijkstraVertex> interior = new HashSet<DijkstraVertex>();
			for (int i = 1; i < path_.size() - 1; i++) {
				interior.add(path_.get(i));
			}
			return interior;
		}

		public void setWeight(double weight) {
			weight_ = weight;
		}

		public boolean contains(DijkstraVertex v) {
			return path_.contains(v);
		}

		public DijkstraVertex get(int pos) {
			if (pos >= length()) {
				throw new IllegalArgumentException("pos>length!");
			}
			return path_.get(pos);
		}

		public int length() {
			return path_.size();
		}

		public void add(DijkstraVertex v) {
			path_.add(v);
		}

		@Override
		public int compareTo(Path o) {
			// TODO Auto-generated method stub
			return weight_.compareTo(o.getWeight());
		}

		public Double getWeight() {
			return weight_;
		}

	}

	public class DijkstraVertex implements Comparable<DijkstraVertex> {
		Integer v_;
		Double dist_ = Double.MAX_VALUE;
		boolean visited_ = false;
		ArrayList<DijkstraVertex> prev_;

		public DijkstraVertex(Integer v) {
			v_ = v;
			prev_ = new ArrayList<DijkstraVertex>();
		}

		public void addPrev(DijkstraVertex i) {
			prev_.add(i);
		}

		public void setPrev(DijkstraVertex i) {
			prev_ = new ArrayList<>();
			this.addPrev(i);
		}

		@Override
		public int compareTo(DijkstraVertex o) {
			return dist_.compareTo(o.dist_);
		}
	}
}
