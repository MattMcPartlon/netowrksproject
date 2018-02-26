package main;

import java.io.File;
import java.util.List;

import alignment.Aligner;
import alignment.Alignment;
import alignment.AlignmentGenerator;
import alignment.NWGlobalAligner;
import io.sequence.NYSEDatReader;
import io.sequence.SReader;
import io.sequence.StockPriceReader;
import scoring.CorrelationScoreFunction;
import scoring.ScoreFunction;
import sequence.encoding.CorrelationWindowEncoder;
import sequence.encoding.Encoder;
import stats.CorrelationAlignmentStats;
import utils.Time;
import utils.Utilities;

public class Main {
	public static void main(String[] args) {
		Utilities.init();
		String fileString = "C:/Users/matt/Desktop/StockDat/prices-split-adjusted.csv";
		File file = new File(fileString);
		SReader sr = new NYSEDatReader(file, new Time(2012,1,1),new Time(2013,1,1),Utilities.close);
		double O=2, E=3;
		ScoreFunction f = new CorrelationScoreFunction(O,E);
		Aligner a = new NWGlobalAligner();
		Encoder e = new CorrelationWindowEncoder(2);
		AlignmentGenerator g = new AlignmentGenerator(sr, e, a, f, new CorrelationAlignmentStats());
		List<Alignment> alignments= g.getAlignments();
		g.printAlignments(alignments);
		g.printStats(alignments);
	}

}
