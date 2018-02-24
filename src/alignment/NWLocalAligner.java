package alignment;

import scoring.ScoreFunction;
import sequence.Element;
import sequence.Sequence;

public class NWLocalAligner extends NWAligner {

	@Override
	public double getAlignmentScore() {
		double best = Double.MIN_VALUE;
		int m, n;
		m = s_.length();
		n = t_.length();
		for (int i = 0; i <= m; i++) {
			for (int j = 0; j <= n; j++) {
				best = getVal(A, i, j) > best ? getVal(A, i, j) : best;
			}
		}
		return best;
	}

	private int[] getBestIndex() {
		double best = Double.MIN_VALUE;
		int m, n;
		m = s_.length();
		n = t_.length();
		int i1 = -1, j1 = -1;
		for (int i = 0; i <= m; i++) {
			for (int j = 0; j <= n; j++) {
				if (getVal(A, i, j) > best) {
					i1 = i;
					j1 = j;
					best = getVal(A, i, j);
				}
			}
		}
		return new int[] { i1, j1 };
	}

	public Alignment doTraceback() {
		Sequence aS, aT;
		aS = new Sequence(s_.getData());
		aT = new Sequence(t_.getData());
		// want to traceback through a,b, and c matrices determining whatever
		// the move we made at each step.

		int[] index = getBestIndex();
		int i = index[0], j = index[1];
		int current = A; // array in which we are coming from, A in local align
		int[] prev = new int[] { -1, -1 };

		for (; (i >= 0 && j >= 0) && (current != 0 && getVal(current, i, j) > 0);) {
			int from = (int) getBackPointer(current, i, j);
			prev[0] = i;
			prev[1] = j;
			// CASE
			switch (current) {

			case A:
				if (debug) {
					System.out.println("Case A");
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
				if (i > 0 && j > 0) {
					System.out.println("value: " + getVal(current, prev[0], prev[1]));
				}
			}
			current = from;
		}
		// should be done, and aS and aT are sequences to align

		aT.reverseOrder();
		aS.reverseOrder();

		Alignment a = new Alignment(aS, aT, s_, t_);
		a.startIndexs1_ = prev[0];
		a.startIndexs2_ = prev[1];
		return a;
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
		initializeFirstColVal(B, Integer.MIN_VALUE);
		initializeFirstRowBackPointer(B, B);
		initializeFirstRowVal(B, 0);
	}

	@Override
	public void initializeC() {
		setVal(C, 0, 0, 0);
		initializeFirstColVal(C, 0);
		initializeFirstColBackPointer(C, C);
		initializeFirstRowVal(C, Integer.MIN_VALUE);

	}

	@Override
	protected int updateA(int i, int j) {
		// get cost of aligning s[i] and t[j]
		double cost = f_.getSubScore(s_.get(i), t_.get(j));
		double a, b, c;
		a = getVal(A, i - 1, j - 1);
		b = getVal(B, i - 1, j - 1);
		c = getVal(C, i - 1, j - 1);
		// want to choose the best of a,b, c and 0 and add to cost
		double val = Math.max(max(a, b, c), 0);

		setVal(A, i, j, val + cost);
		return (val == a) ? A : (val == b) ? B : (val == cost) ? 0 : C;
	}

	@Override
	public int getType() {
		return LOCAL;
	}

	@Override
	public String toString() {
		return "local NW aligner";
	}

}
