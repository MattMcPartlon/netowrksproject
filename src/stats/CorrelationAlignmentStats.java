package stats;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import alignment.Alignment;
import scoring.ScoreFunction;
import sequence.Element;

public class CorrelationAlignmentStats extends AlignmentStats {

	public double[] getGapOpenPositionFreqs(Collection<Alignment> alignments, int start, int end) {
		double[] freqs = new double[(end - start) + 5];
		boolean gapOpen = false;
		int s1Pos = 1, s2Pos = 1;

		for (Alignment a : alignments) {
			s1Pos = 1;
			s2Pos = 1;
			try {
				for (int i = 1; i <= a.length(); i++) {
					Element[] pair = a.pairAt(i);
					if (pair[0] == Element.gap) {
						if (!gapOpen) {
							gapOpen = true;
							freqs[(s1Pos + s2Pos) / 2] += 1;
						}
						s2Pos++;

					} else if (pair[1] == Element.gap) {
						if (!gapOpen) {
							gapOpen = true;
							freqs[(s1Pos + s2Pos) / 2] += 1;
						}
						s1Pos++;
					} else {
						gapOpen = false;
						s1Pos++;
						s2Pos++;
					}
				}
			} catch (Exception e) {
				System.out.println("s1 idx: " + s1Pos + " s2 idx: " + s2Pos);
				a.printAlignment();
				System.out.println("alignment length: " + a.length());
				System.out.println("original sequence lengths " + a.getOrigS1Length() + ", " + a.getOrigS2Length());
				throw new ArrayIndexOutOfBoundsException(e.getMessage());
			}

		}
		return freqs;
	}

	/**
	 * keys correspond to number of gaps, values correspond to the number of
	 * sequences with that number of gaps
	 * 
	 * @param alignments
	 * @return
	 */
	public HashMap<Integer, Integer> getGapFrequencies(Collection<Alignment> alignments) {
		HashMap<Integer, Integer> gapFreqs = new HashMap<>();

		for (Alignment a : alignments) {
			int numGaps = 0;
			for (int i = 1; i <= a.length(); i++) {
				Element[] pair = a.pairAt(i);
				if (pair[0] == Element.gap || pair[1] == Element.gap) {
					numGaps++;
				}
			}
			if (!gapFreqs.containsKey(numGaps)) {
				gapFreqs.put(numGaps, 0);
			}
			// increment number of sequences with this many gaps
			int freq = gapFreqs.get(numGaps);
			gapFreqs.put(numGaps, freq + 1);
		}
		return gapFreqs;
	}

	/**
	 * 
	 */
	public HashMap<Double, Integer> getOffsetDistribution(Collection<Alignment> alignments, double granularity) {

		HashMap<Double, Integer> freqs = new HashMap<>();
		double mult = 1.0 / granularity;

		for (Alignment a : alignments) {
			int s1Pos = 1, s2Pos = 1;
			double sum = 0;
			for (int i = 1; i <= a.length(); i++) {
				Element[] pair = a.pairAt(i);
				if (pair[0] == Element.gap) {
					s2Pos++;
				} else if (pair[1] == Element.gap) {
					s1Pos++;
				} else {
					s1Pos++;
					s2Pos++;
				}
				sum += s1Pos - s2Pos;
			}

			double avg = (0.0) + Math.floor((sum / (a.length() + 0.0)) * mult);
			double key = avg * granularity;
			if (!freqs.containsKey(key)) {
				freqs.put(key, 0);
			}
			// increment number of sequences with this many gaps
			int freq = freqs.get(key);
			freqs.put(key, freq + 1);
		}
		return freqs;
	}

	public HashMap<Double, Integer> getScoreDistribution(Collection<Alignment> alignments, double granularity,
			ScoreFunction f) {
		HashMap<Double, Integer> freqs = new HashMap<>();

		double mult = 1.0 / granularity;
		for (Alignment a : alignments) {

			double score = f.getScore(a);
			double avg = (0.0) + Math.floor(score * mult);
			double key = avg * granularity;
			if (!freqs.containsKey(key)) {
				freqs.put(key, 0);
			}
			// increment number of sequences with this many gaps
			int freq = freqs.get(key);
			freqs.put(key, freq + 1);
		}
		return freqs;
	}

}
