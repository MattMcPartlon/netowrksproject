package stats;

import java.util.HashSet;
import java.util.List;

import alignment.Alignment;
import sequence.Element;
import sequence.Sequence;

public abstract class AlignmentStats {
	
	
	

	public double pointwiseSimilarity(Alignment a1, Alignment a2) {
		Sequence s1, s2, t1, t2;
		s1 = a1.getS1();
		t1 = a1.getS2();
		s2 = a2.getS1();
		t2 = a2.getS2();
		if (!(s1.getID().equals(s2.getID())) && t1.getID().equals(t2.getID())) {
			throw new IllegalArgumentException(
					"can't compare alignments of difference sequences, got this s1 ID: " + s1.getID() + " and s2 ID: "
							+ s2.getID() + " and this t1 ID: " + t1.getID() + " and  t2 ID: " + t2.getID());
		}
		// compare each entry
		double N = Math.min(a1.length(), a2.length());
		double numSame = 0;
		for (int i = 1; i <= N; i++) {
			numSame += matches(a1.pairAt(i), a2.pairAt(i));

		}
		return numSame / N;
	}

	/**
	 * RETURNS THE NUMBER OF PAIRS THAT A AND THIS HAVE IN COMMON.
	 * 
	 * @param a
	 * @return
	 */

	public double pairwiseSimilarity(Alignment a1, Alignment a2) {
		Sequence s1 = a1.getS1(), s2 = a2.getS1(), t1 = a1.getS2(), t2 = a2.getS2();
		if (!(s1.getID().equals(s2.getID())) && t1.getID().equals(t2.getID())) {
			throw new IllegalArgumentException(
					"can't compare alignments of difference sequences, got this s1 ID: " + s1.getID() + " and s2 ID: "
							+ s2.getID() + " and this t1 ID: " + t1.getID() + " and  t2 ID: " + t2.getID());
		}

		HashSet<String> pairs1 = getPairs(a1);
		HashSet<String> pairs2 = getPairs(a2);

		// check if the same pairs are aligned
		double numSame = 0.0;
		for (String pair : pairs1) {
			if (pairs2.contains(pair)) {
				numSame += 1.0;
			}
		}

		return numSame;
	}

	private HashSet<String> getPairs(Alignment a) {
		HashSet<String> pairs = new HashSet<String>();
		int i = a.getStartIndexS1(), j = a.getStartIndexS2();
		// we add all pairs i and j that are aligned from original
		// sequences in this sequence
		Sequence s1, t1;
		s1 = a.getS1();
		t1 = a.getS2();

		for (int idx = 1; idx <= a.length(); idx++) {
			int s1idx = -1, s2idx = -1;
			if (!s1.get(idx).equals(Element.gap)) {
				s1idx = i;
				i++;
			} else {
				s1idx = 0;
			}
			if (!t1.get(idx).equals(Element.gap)) {
				s2idx = j;
				j++;
			} else {
				s2idx = 0;
			}
			pairs.add("" + s1idx + "," + s2idx);
		}
		return pairs;
	}

	/**
	 * returns 1 if the pairs of elements are the same
	 */
	public double matches(Element[] pair1, Element[] pair2) {
		return (pair1[0].equals(pair2[0]) && pair1[1].equals(pair2[1])) ? 1 : 0;
	}

}
