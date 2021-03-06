package stats;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import alignment.Alignment;
import scoring.ScoreFunction;
import sequence.Element;
import sequence.Sequence;

public class CorrelationAlignmentStats extends AlignmentStats {

	public double[] getGapOpenPositionFreqs(Collection<Alignment> alignments, int numGroups) {
		double[] freqs = new double[numGroups + 1];

		int s1Pos = 1, s2Pos = 1;

		for (Alignment a : alignments) {
			s1Pos = 1;
			s2Pos = 1;
			boolean gapOpen = false;
			try {
				for (int i = 1; i <= a.length(); i++) {
					Element[] pair = a.pairAt(i);
					if (pair[0] == Element.gap || pair[1] == Element.gap) {
						if (!gapOpen) {
							gapOpen = true;
							int group = (i * numGroups) / a.length();
							freqs[group] += 1;
						}
					} else {
						gapOpen = false;
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

	public int getNumGaps(Alignment a) {

		if (!a.numGapsSetQ()) {
			int numGaps = 0;
			for (int i = 1; i <= a.length(); i++) {
				Element[] pair = a.pairAt(i);
				if (pair[0] == Element.gap || pair[1] == Element.gap) {
					numGaps++;
				}
			}

			a.setNumGaps(numGaps);
		}
		return a.getNumGaps();
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
			
			int numGaps = this.getNumGaps(a);
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
	public double getAverageOffset(Alignment a, Sequence s1) {
		if (!a.averageOffsetSetQ()) {
			Sequence s2 = a.getS2();
			int s1idx = 0, s2idx = 1;

			int s1Pos = 1, s2Pos = 1;
			double sum = 0;
			for (int i = 1; i <= a.length(); i++) {
				Element[] pair = a.pairAt(i);
				if (pair[s1idx] == Element.gap) {
					s2Pos++;
				} else if (pair[s2idx] == Element.gap) {
					s1Pos++;
				} else {
					s1Pos++;
					s2Pos++;
				}
				sum += s1Pos - s2Pos;
			}
			double avgOffset = (sum + 0.0) / (a.length() + 0.0);
			a.setAverageOffset(avgOffset);
		}

		return a.getAverageOffset(s1);

	}

	/**
	 * 
	 */
	public HashMap<Double, Integer> getOffsetDistribution(Collection<Alignment> alignments, double granularity) {

		HashMap<Double, Integer> freqs = new HashMap<>();
		double mult = 1.0 / granularity;

		for (Alignment a : alignments) {
			double offset = 0;
			int rand = (int) Math.round(Math.random() * 2);

			if (rand == 1) {
				offset = this.getAverageOffset(a, a.getS1());
			} else {
				offset = this.getAverageOffset(a, a.getS2());
			}

			double avg = (0.0) + Math.floor(offset * mult);
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
		HashSet<Double> scores = new HashSet<>();
		double bestScore = 0.0;
		double sum=0;
		double mult = 1.0 / granularity;
		for (Alignment a : alignments) {

			double score = f.getScore(a);
			if (score > bestScore) {
				bestScore = score;
			}
			scores.add(score);

		}
		for (Double score : scores) {
			score = score / bestScore;
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
