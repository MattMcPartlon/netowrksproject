package main;

import java.io.File;
import java.util.List;

import alignment.Aligner;
import alignment.Alignment;
import alignment.AlignmentGenerator;
import alignment.NWGlobalAligner;
import alignment.NWSemiGlobalAligner;
import io.sequence.NYSEDatReader;
import io.sequence.SReader;
import io.sequence.StockPriceReader;
import network.ThresholdNetwork;
import scoring.CorrelationScoreFunction;
import scoring.ScoreFunction;
import sequence.encoding.CorrelationWindowEncoder;
import sequence.encoding.Encoder;
import stats.AlignmentStatsIO;
import stats.CorrelationAlignmentStats;
import stats.NetworkStatsIO;
import utils.Time;
import utils.Utilities;

public class Main {
	public static void main(String[] args) {
		Utilities.init();
		String fileString = "C:/Users/matt/Desktop/StockDat/prices-split-adjusted.csv";
		File file = new File(fileString);
		int startMonth = 1, startYear = 2012, startDay = 1, endYear = 2013, endDay = 1, endMonth = 1;
		double O = 5, E = 3;
		int w = 20;

		Time t1 = new Time(startYear, startMonth, startDay);
		Time t2 = new Time(endYear, endMonth, endDay);
		int[] window= new int[] { 20, 50, 1 };
		double[] open = new double[] { 1.0, 2.0, 3.0, 4.0, 5.0, 6.0 };
		double[] extend = new double[] { 1.0, 2.0, 3.0, 4.0, 5.0, 6.0 };

		for (int k = 0; k < window.length; k++) {
			for (int i = 0; i < open.length; i++) {
				for (int j = 0; j < extend.length; j++) {
					System.out.println("i: " + i);
					System.out.println("j: " + j);
					O = open[i];
					E = extend[j];
					w = window[k];
					String suffix = "S_AND_P_lag_" + Utilities.MAX_LAG
							+ "_NWSemiGlobalAligner_CorrelationScoreFunction_" + t1.toString() + "_to_" + t2.toString()
							+ "O_" + O + "_E_" + E + "W_" + w;
					SReader sr = new NYSEDatReader(file, t1, t2, Utilities.close);

					ScoreFunction f = new CorrelationScoreFunction(O, E);
					Aligner a = new NWSemiGlobalAligner();
					Encoder e = new CorrelationWindowEncoder(w);
					AlignmentGenerator g = new AlignmentGenerator(sr, e, a, f, new CorrelationAlignmentStats());
					List<Alignment> alignments = g.getAlignments();
					AlignmentStatsIO alignmentStats = new AlignmentStatsIO(alignments, f);
					// g.printAlignments(alignments);

					alignmentStats.saveAlignmentStats(new File("AlignmentStats_" + suffix));
					ThresholdNetwork network = new ThresholdNetwork(.1);
					network.build(alignments, f);
					NetworkStatsIO netStats = new NetworkStatsIO(network);
					netStats.saveNetwork(new File("network_" + suffix));
					netStats.saveOffsets(new File("offsets_" + suffix));
					netStats.saveSectorIDs(new File("sectors_" + suffix));
					netStats.saveVertexDat(new File("vertexData_" + suffix));
				}
			}
		}

	}

}
