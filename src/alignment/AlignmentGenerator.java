package alignment;

import java.io.File;
import java.security.KeyStore.LoadStoreParameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import io.sequence.SReader;
import io.sequence.SWriter;
import io.sequence.StockPriceReader;
import scoring.CorrelationScoreFunction;
import scoring.ScoreFunction;
import sequence.Sequence;
import sequence.encoding.CorrelationWindowEncoder;
import sequence.encoding.Encoder;
import sequence.encoding.SimpleCorrelationEncoder;
import stats.AlignmentStats;
import stats.CorrelationAlignmentStats;
import utils.FieldValues;

public class AlignmentGenerator {

	SReader sr_;
	Encoder e_;
	Aligner a_;
	ScoreFunction f_;
	AlignmentStats stats_;
	int seqLength_;

	public AlignmentGenerator(SReader s, Encoder e, Aligner a, ScoreFunction f, AlignmentStats stats) {
		a_ = a;
		e_ = e;
		sr_ = s;
		f_ = f;
		stats_ = stats;

	}

	private void printFormattedDI(String prefix,  String suffix, HashMap<Double, Integer> map) {

		List<Double> keyset = new ArrayList<>(map.keySet());
		Collections.sort(keyset);
		String str = "{";
		for (Double key : keyset) {
			str += "{" + key + "," + map.get(key) + "},";
		}
		str = str.substring(0, str.length() - 1);
		str += "}";
		System.out.println(prefix+""+str+""+suffix);
	}

	private void printFormattedII(String prefix, String suffix, HashMap<Integer, Integer> map) {

		List<Integer> keyset = new ArrayList<>(map.keySet());
		Collections.sort(keyset);
		String str = "{";
		for (Integer key : keyset) {
			str += "{" + key + "," + map.get(key) + "},";
		}
		str = str.substring(0, str.length() - 1);
		str += "}";
		System.out.println(prefix+""+str+""+suffix);
	}

	public void printStats(Collection<Alignment> alignments) {
		CorrelationAlignmentStats cStats = (CorrelationAlignmentStats) stats_;
		// print gap frequency distribution
		HashMap<Integer, Integer> gapFreqs = cStats.getGapFrequencies(alignments);
		//System.out.println("gap frequencies: ");
		printFormattedII("gapFreqs=",";",gapFreqs);

		// print offset distribution
		HashMap<Double, Integer> offsets = cStats.getOffsetDistribution(alignments, .25);
		//System.out.println("offset distribution: ");
		printFormattedDI("offsetDist=",";",offsets);

		// print score distribution
		HashMap<Double, Integer> scoreDist = cStats.getScoreDistribution(alignments, 0.05, f_);
		//System.out.println("score distribution: ");
		printFormattedDI("scoreDist=",";",scoreDist);

		// print gap open distribution
		double[] gapOpenFreqs = cStats.getGapOpenPositionFreqs(alignments, 0, seqLength_+5);
		String str= Arrays.toString(gapOpenFreqs);
		str=str.substring(1, str.length()-1);
		str="gapOpenDist= {"+str+"};";
		str.replace(']', '}');
		//System.out.println("gap open distribution: ");
		System.out.println(str);

	}

	/**
	 * get all alignments for sequences read in by sequence reader
	 * 
	 * @return
	 */
	public List<Alignment> getAlignments() {
		List<Alignment> alignments = new ArrayList<>();
		List<Sequence> encoded = loadSequences();
		seqLength_ = encoded.get(0).length();

		for (int i = 0; i < encoded.size(); i++) {
			Sequence s = encoded.get(i);
			if (FieldValues.VERBOSE) {
				System.out.println("getting alignments for " + s.getID());
			}
			for (int j = i + 1; j < encoded.size(); j++) {
				Sequence t = encoded.get(j);

				alignments.add(a_.getAlignment(s, t, f_));
			}
		}
		return alignments;

	}

	public void printAlignments(List<Alignment> als) {
		if (FieldValues.VERBOSE) {
			System.out.println("printing alignments: ");
		}
		int idx = 1;
		for (Alignment al : als) {
			if (FieldValues.VERBOSE) {
				System.out.println("Alignment " + idx + ":");
			}
			if (FieldValues.TESTMODE) {
				if (idx > FieldValues.MAX_ALIGNMENTS) {
					break;
				}
			}
			idx++;
			al.printAlignment();
			System.out.println("score: " + f_.getScore(al));
		}
	}

	/**
	 * loads and encodes sequences into encoded list
	 */
	private List<Sequence> loadSequences() {
		List<Sequence> encoded = new ArrayList<>();
		List<Sequence> rawSeqs = sr_.read();
		for (Sequence toEncode : rawSeqs) {
			if (FieldValues.VERBOSE) {
				System.out.println("getting " + e_.getDescription() + " - encoding for: " + toEncode.getID());
				System.out.println("unencoded: " + toEncode.toString());
				System.out.println();
			}
			Sequence enc = e_.encode(toEncode);
			encoded.add(enc);
			if (FieldValues.VERBOSE) {
				System.out.println(e_.getDescription() + " encoding for: " + toEncode.getID());
				System.out.println("encoded: " + enc.toString());
				System.out.println();
			}
		}
		return encoded;
	}

	public static void main(String[] args) {
		String fileString = "C:/Users/matt/Desktop/StockDat/djia4.seqs";
		File file = new File(fileString);
		SReader sr = new StockPriceReader(file);
		ScoreFunction f = new CorrelationScoreFunction(2, 3);
		Aligner a = new NWGlobalAligner();
		Encoder e = new CorrelationWindowEncoder(20);
		AlignmentGenerator g = new AlignmentGenerator(sr, e, a, f, new CorrelationAlignmentStats());
		List<Alignment> alignments= g.getAlignments();
		g.printAlignments(alignments);
		g.printStats(alignments);

	}

}
