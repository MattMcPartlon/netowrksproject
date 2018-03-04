package stats;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import network.Edge;
import network.StockVertex;
import network.ThresholdNetwork;
import network.Vertex;
import network.WeightedEdge;

public class NetworkStatsIO {

	ThresholdNetwork net_;

	public NetworkStatsIO(ThresholdNetwork net) {
		net_ = net;
	}

	public void saveNetwork(File f) {
		List<StockVertex> V = net_.getVertices();
		String toPrint = "";
		for (StockVertex v : V) {
			toPrint += net_.getAdjacencyListAsString(v) + ",";
		}
		try {
			PrintWriter pw = new PrintWriter(f);
			pw.println(""+toPrint.substring(0,toPrint.length()-1)+"");
			pw.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void saveOffsets(File f) {
		List<StockVertex> V = net_.getVertices();
		double[][] offsets = new double[V.size()][V.size()];
		for (Vertex v : V) {
			Collection<Edge> incidentEdges = net_.getIncidentEdges(v);
			for (Edge e : incidentEdges) {
				double offset = ((WeightedEdge) e).getAvgGapOffset(v);
				Vertex u = e.getOpposite(v);
				offsets[v.getIndex()][u.getIndex()] = offset;
				
			}
		}
		try {
			PrintWriter pw = new PrintWriter(f);
			for (int i = 0; i < offsets.length - 1; i++) {
				String s = Arrays.toString(offsets[i]);
				pw.print("{" + s.substring(1, s.length() - 1) + "},");
			}
			String s = Arrays.toString(offsets[offsets.length - 1]);
			pw.println("{" + s.substring(1, s.length() - 1) + "}");
			pw.flush();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void saveGaps(File f) {
		List<StockVertex> V = net_.getVertices();
		double[][] offsets = new double[V.size()][V.size()];
		for (Vertex v : V) {
			Collection<Edge> incidentEdges = net_.getIncidentEdges(v);
			for (Edge e : incidentEdges) {
				double offset = ((WeightedEdge) e).getAvgGapOffset(v);
				Vertex u = e.getOpposite(v);
				offsets[v.getIndex()][u.getIndex()] = offset;
				
			}
		}
		try {
			PrintWriter pw = new PrintWriter(f);
			for (int i = 0; i < offsets.length - 1; i++) {
				String s = Arrays.toString(offsets[i]);
				pw.print("{" + s.substring(1, s.length() - 1) + "},");
			}
			String s = Arrays.toString(offsets[offsets.length - 1]);
			pw.println("{" + s.substring(1, s.length() - 1) + "}");
			pw.flush();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void saveSectorIDs(File f) {
		System.out.println();
		List<StockVertex> V = net_.getVertices();
		String toPrint = "{";
		for (int i = 0; i < V.size(); i++) {
			toPrint += ((StockVertex) (V.get(i))).getSectorID() + ",";
		}
		System.out.println(toPrint.substring(0, toPrint.length() - 1) + "};");
		try {
			PrintWriter pw = new PrintWriter(f);
			pw.println(toPrint.substring(0, toPrint.lastIndexOf(',')) + "}");
			pw.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void saveVertexDat(File f) {
		System.out.println();
		List<StockVertex> V = net_.getVertices();
		String toPrint = "{";
		for (int i = 0; i < V.size(); i++) {
			toPrint += ((StockVertex) (V.get(i))).getData().toString() + ",";
		}
		try {
			PrintWriter pw = new PrintWriter(f);
			pw.println(toPrint.substring(0, toPrint.lastIndexOf(',')) + "}");
			pw.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}

}
