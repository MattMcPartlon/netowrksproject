package alignment;

import java.util.Arrays;

import scoring.ScoreFunction;
import sequence.Element;
import sequence.Sequence;

public class GlobalCorrAligner extends CorrAligner {

	@Override
	public double getAlignmentScore() {
		if (s_.length() != t_.length()) {
			throw new IllegalArgumentException();
		}
		double a, b, c;
		int m;
		m = s_.length();

		a = getVal(A, m);
		b = getVal(B, m);
		c = getVal(C, m);
		System.out.println("a: ");
		for (int i = 0; i < a_.length; i++) {
			System.out.print(getVal(A, i));
		}
		System.out.println();
		return max(a, b, c);

	}

	private int bestMat() {
		double best = getAlignmentScore();
		double a, b;
		int m;
		m = s_.length();

		a = getVal(A, m);
		b = getVal(B, m);

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
		int i;
		i = s_.length() + 1;

		int current = bestMat(); // array in which we are coming from initially

		for (; i > 1;) {
			i--;
			int from = (int) getBackPointer(current, i);
			// CASE
			switch (current) {
			case A:
				if (debug) {
					System.out.println("Case A");
					System.out.println("type: " + this.getType());
				}
				aS.add(s_.get(i));
				aT.add(t_.get(i));

				break;
			case B:

				if (debug) {
					System.out.println("Case B");
				}
				aS.add(s_.get(i).negate());
				aT.add(t_.get(i));

				break;
			case C:

				if (debug) {
					System.out.println("Case C");
				}
				aS.add(s_.get(i));
				aT.add(t_.get(i).negate());

				break;
			case NONE:
				if (debug) {
					System.out.println("Case NONE");
				}
				System.out.println("ERROR IN GLOBAL ALIGN");
				i = 0;

				break;

			default:
				break;
			}
			if (debug) {
				System.out.println("from: " + from);
				System.out.println("i: " + i);
				System.out.println("aligned: " + aS.get(Math.max(s_.length() - i, 1)) + ","
						+ aT.get(Math.max(t_.length() - i, 1)));
			}
			current = from;
		}
		if (i != 1) {
			throw new IllegalArgumentException("i: " + i);
		}
		// should be done, and aS and aT are sequences to align

		aT.reverseOrder();
		aS.reverseOrder();
		return new Alignment(aS, aT, s_, t_);

	}

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return GLOBAL;
	}

	@Override
	public String toString() {

		return "global correlation aligner";
	}

	
	

}
