package scoring;

import alignment.Aligner;
import alignment.Alignment;
import sequence.Element;

public abstract class ScoreFunction {

	protected double op_, ep_; // gap open and gap extend penalty values

	public ScoreFunction(double op, double ep) {
		op_ = op;
		ep_ = ep;
	}

	public abstract double getScore(Alignment a);

	public double getRawScore(Alignment a) {
		double score = 0;
		boolean gapOpen_ = false;
		for (int i = 1; i <= a.length(); i++) {

			Element[] pair = a.pairAt(i);
			if (pair[1] == Element.gap || pair[0] == Element.gap) {
				if (gapOpen_) {
					score -= ep_;
				} else {
					gapOpen_ = true;
					score -= op_;
				}
			} else {
				if (gapOpen_) {
					gapOpen_ = false;
				}
			}
			if (!gapOpen_) {
				score += getScore(a.pairAt(i));
			}
		}
		return score;
	}

	private double getScore(Element[] pair) {
		Element e1 = pair[0];
		Element e2 = pair[1];
		return getSubScore(e1, e2);
	}

	/**
	 * The cost of aligning element e1 with element e2
	 * 
	 * @param e1
	 * @param e2
	 * @return
	 */

	public abstract double getSubScore(Element e1, Element e2);

	/**
	 * gets the cost of opening a gap
	 * 
	 * @return
	 */
	public double getOpenPenalty() {
		return op_;
	}

	/**
	 * gets the cost of extending a gap which is already open
	 * 
	 * @return
	 */

	public double getExtendPenalty() {
		return ep_;
	}

	public abstract String toString();

}
