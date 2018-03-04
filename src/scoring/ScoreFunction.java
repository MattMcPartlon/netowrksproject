package scoring;

import java.awt.geom.Ellipse2D;

import javax.rmi.CORBA.Util;

import alignment.Aligner;
import alignment.Alignment;
import alignment.AlignmentType;
import sequence.Element;
import utils.Utilities;

public abstract class ScoreFunction {

	protected double op_, ep_; // gap open and gap extend penalty values

	public ScoreFunction(double op, double ep) {
		op_ = op;
		ep_ = ep;
	}

	public abstract double getScore(Alignment a);

	public double getRawScore(Alignment a) {
		if (a.getAlignmentType() == AlignmentType.NWGlobal || a.getAlignmentType() == AlignmentType.GlobalCorr) {
			return getRawScore1(a);
		} else {
			return getRawScore2(a);
		}
	}

	private double getRawScore2(Alignment a) {
		double score = 0;

		boolean gapOpen_ = false;
		int startPos = Utilities.MAX_LAG;
		int endPos = a.length()-Utilities.MAX_LAG;
		// don't penalize for starting gaps or ending gaps
		for (int i = 1; i <= Utilities.MAX_LAG; i++) {
			Element[] pair = a.pairAt(i);
			if (pair[1] != Element.gap && pair[0] != Element.gap) {
				startPos = i;
				break;
			}
		}

		for (int i = a.length(); i >= a.length()-Utilities.MAX_LAG; i--) {
			Element[] pair = a.pairAt(i);
			if (pair[1] != Element.gap && pair[0] != Element.gap) {
				endPos = i;
				break;
			}
		}
		
	
		startPos++;
		endPos--;

		for (int i = startPos; i <= endPos; i++) {

			Element[] pair = a.pairAt(i);
			if (pair[1] == Element.gap || pair[0] == Element.gap) {
				if (gapOpen_) {
					score -= ep_;
				} else {
					gapOpen_ = true;
					score -= op_;
				}
			} else {
				gapOpen_ = false;
			}
			if (!gapOpen_) {
				score += getScore(a.pairAt(i));
			}
		}
		return score;
	}

	private double getRawScore1(Alignment a) {
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
				gapOpen_ = false;
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
	
	public abstract ScoreFunction clone();

}
