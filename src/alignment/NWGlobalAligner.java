package alignment;

import scoring.ScoreFunction;
import sequence.Element;
import sequence.Sequence;

public class NWGlobalAligner extends NWAligner {

	@Override
	public double getAlignmentScore() {

		double a, b, c;
		int m, n;
		m = s_.length();
		n = t_.length();
		a = getVal(A, m, n);
		b = getVal(B, m, n);
		c = getVal(C, m, n);
		return max(a, b, c);
	}

	/**
	 * This method is meant to update the (i,j)^th entry of the table a_, i.e.
	 * a(i,j) is best alignment s.t. s[i] is aligned to t[j].
	 * 
	 * @param i
	 * @param j
	 * @returns matrix where this entry in A was taken from
	 */
	protected int updateA(int i, int j) {
		// get cost of aligning s[i] and t[j]
		double cost = f_.getSubScore(s_.get(i), t_.get(j));
		double a, b, c;
		a = getVal(A, i - 1, j - 1);
		b = getVal(B, i - 1, j - 1);
		c = getVal(C, i - 1, j - 1);
		// want to choose the best of a,b,and c and add to cost
		double val = max(a, b, c);
		setVal(A, i, j, cost + val);
		return (val == a) ? A : (val == b) ? B : C;

	}

	private int bestMat() {
		double best = getAlignmentScore();
		double a, b;
		int m, n;
		m = s_.length();
		n = t_.length();
		a = getVal(A, m, n);
		b = getVal(B, m, n);

		return (best == a) ? A : (best == b) ? B : C;
	}

	public Alignment doTraceback() {

		if (debug) {
			System.out.println("alignment score: " + this.getAlignmentScore());
		}

		Sequence aS, aT;
		aS = new Sequence(s_.getData());
		aT = new Sequence(t_.getData());
		// want to traceback through a,b, and c matrices determining
		// the move we made at each step.
		int i, j;
		i = s_.length();
		j = t_.length();

		int current = bestMat(); // array in which we are coming from initially

		for (; i > 0 || j > 0;) {
			int from = (int) getBackPointer(current, i, j);
			// CASE
			switch (current) {
			case A:
				if (debug) {
					System.out.println("Case A");
					System.out.println("type: " + this.getType());
				}
				aS.add(s_.get(i));
				aT.add(t_.get(j));
				i -= 1;
				j -= 1;
				break;
			case B:

				if (debug) {
					System.out.println("Case B");
				}
				aS.add(Element.gap);
				aT.add(t_.get(j));
				j -= 1;
				break;
			case C:

				if (debug) {
					System.out.println("Case C");
				}
				aS.add(s_.get(i));
				aT.add(Element.gap);
				i -= 1;
				break;
			case NONE:
				if (debug) {
					System.out.println("Case NONE");
				}
				System.out.println("ERROR IN GLOBAL ALIGN");
				i = 0;
				j = 0;
				break;

			default:
				break;
			}
			if (debug) {
				System.out.println("from: " + from);
				System.out.println("i: " + i + ", j: " + j);
				System.out.println("aligned: " + aS.get(Math.max(s_.length() - i, 1)) + ","
						+ aT.get(Math.max(t_.length() - j, 1)));
			}
			current = from;
		}
		if (i != 0 || j != 0) {
			throw new IllegalArgumentException("i: " + i + ", j: " + j);
		}
		// should be done, and aS and aT are sequences to align

		aT.reverseOrder();
		aS.reverseOrder();
		return new Alignment(aS, aT, s_, t_, AlignmentType.NWGlobal);

	}

	@Override
	public void initializeA() {
		setVal(A, 0, 0, 0);
		initializeFirstColVal(A, Integer.MIN_VALUE);
		initializeFirstRowVal(A, Integer.MIN_VALUE);
	}

	@Override
	public void initializeB() {
		setVal(B, 0, 0, 0);
		for (int i = 1; i <= s_.length(); i++) {
			setVal(B, i, 0, Integer.MIN_VALUE);
		}
		for (int j = 1; j <= t_.length(); j++) {
			setVal(B, 0, j, -(O + (E * (j - 1))));
			setBackPointer(B, 0, j, B);
		}
	}

	@Override
	public void initializeC() {
		setVal(C, 0, 0, 0);
		for (int i = 1; i <= s_.length(); i++) {
			setVal(C, i, 0, -(O + (E * (i - 1))));
			setBackPointer(C, i, 0, C);
		}
		for (int j = 1; j <= t_.length(); j++) {
			setVal(C, 0, j, Integer.MIN_VALUE);
		}
	}

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return GLOBAL;
	}

	@Override
	public String toString() {
		return "global NW aligner";
	}

	@Override
	public Aligner clone() {
		// TODO Auto-generated method stub
		return new NWGlobalAligner();
	}
	
	@Override
	public AlignmentType getAlignmentType() {
		// TODO Auto-generated method stub
		return AlignmentType.NWGlobal;
	}

}
