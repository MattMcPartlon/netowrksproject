package scoring;

import alignment.Alignment;
import sequence.Element;
import utils.Utilities;

public class CorrelationScoreFunction extends ScoreFunction {

	public CorrelationScoreFunction(double op, double ep) {
		super(op, ep);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double getSubScore(Element e1, Element e2) {
		if (e1 == Element.gap || e2 == Element.gap) {
			throw new IllegalArgumentException("can't get substitution score of gap element!");
		}
		return e1.getVal() * e2.getVal();
	}

	@Override
	public double getScore(Alignment a) {
		// TODO Auto-generated method stub
		if (!a.scoreSetQ()) {
			double score = getRawScore(a) / (a.length() - 1.0);
			a.setScore(score);
		}
		return a.getScore();
	}

	/**
	 * Gets the substitution score of (-)e1 an e2
	 * 
	 * @param e1
	 * @param e2
	 * @return
	 */
	public double getSubScoreNeg1(Element e1, Element e2) {
		if (e1 == Element.gap || e2 == Element.gap) {
			throw new IllegalArgumentException("can't get substitution score of gap element!");
		}
		return (-1) * e1.getVal() * e2.getVal();
	}

	/**
	 * Gets the substitution score of e1 and (-)e2
	 * 
	 * @param e1
	 * @param e2
	 * @return
	 */
	public double getSubScoreNeg2(Element e1, Element e2) {
		if (e1 == Element.gap || e2 == Element.gap) {
			throw new IllegalArgumentException("can't get substitution score of gap element!");
		}
		return e1.getVal() * (-1) * e2.getVal();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "correlation score function, open penalty: " + op_ + " extend penalty: " + ep_;
	}

	@Override
	public ScoreFunction clone() {

		return new CorrelationScoreFunction(op_, ep_);
	}
}