package alignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;

import javax.rmi.CORBA.Util;

import scoring.ScoreFunction;
import sequence.Element;
import sequence.Sequence;
import utils.Utilities;

public class NWSemiGlobalAligner extends NWAligner {

	@Override
	public double getAlignmentScore() {
		double best = Integer.MIN_VALUE;
		int m, n;
		m = s_.length();
		n = t_.length();

		// check over bottom row
		for (int i = 0; (n - i) >= (n - Utilities.MAX_LAG); i++) {
			best = getVal(A, m, n - i) > best ? getVal(A, m, n - i) : best;
		}
		for (int i = 0; (m - i) >= (m - Utilities.MAX_LAG); i++) {
			best = getVal(A, m - i, n) > best ? getVal(A, m - i, n) : best;
		}

		return best;
	}

	private int[] getBestIndex() {
		double best = Integer.MIN_VALUE;
		double val=0;
		int m, n;
		m = s_.length();
		n = t_.length();
		
		
		int[] bestIndex = new int[] { m, n };

		// find best entry such that s[m] aligned with t[n-i] (i.e. rest of t
		// aligned to gap)
		for (int i = 0; (n - i) >= (n - Utilities.MAX_LAG); i++) {
			val = getVal(A, m, n - i);
			
			if (val > best) {
				best = val;
				bestIndex = new int[] { m, n - i };
			}
		}
		// find best entry such that s[m-i] aligned with t[n] (i.e. rest of s
		// aligned to gap)
		for (int i = 0; (m - i) >= (m - Utilities.MAX_LAG); i++) {
			val = getVal(A, m - i, n);
			
			if (val > best) {
				best = val;
				bestIndex = new int[] { m - i, n };
			}
		}

		return bestIndex;
	}

	public Alignment doTraceback() {
		Sequence aS, aT;

		aS = new Sequence(s_.getData());
		aT = new Sequence(t_.getData());
		// figure out which entry of A we are starting from
		int[] index = getBestIndex();
		int i = index[0], j = index[1], m = s_.length(), n = t_.length(), current = A;
		// if i<m then t[n] is aligned to s[i] and s[i+1]...s[m] aligned to gap
		double tempScore = getVal(A, i, j);
		if (tempScore == 0.0) {
			System.out.println("i: " + i + ", j: " + j);
		}
		for (int k = m; k > i; k--) {
			aS.add(s_.get(k));
			aT.add(Element.gap);
		}

		// if j<n then s[m] is aligned to t[j] and t[j+1]...t[n] aligned to gap
		for (int k = n; k > j; k--) {
			aS.add(Element.gap);
			aT.add(t_.get(k));
		}

		for (; i > 0 || j > 0;) {
			int from = (int) getBackPointer(current, i, j);
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
				System.out.println("ERROR IN SEMI-GLOBAL ALIGN");
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
		// should be done, and aS and aT are sequences to align

		aT.reverseOrder();
		aS.reverseOrder();

		Alignment a = new Alignment(aS, aT, s_, t_, AlignmentType.NWSemiGlobal);
		a.setTempScore(tempScore);

		if (Utilities.TESTMODE) {
			try {

				if (tempScore == 0.0) {
					PrintWriter pw = new PrintWriter(new File("temp.txt"));
					for (int k = 0; k <= n; k++) {
						for (int l = 0; l < n; l++) {
							pw.print(getVal(A, k, l) + " ");
						}
						pw.println();
					}
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return a;

	}

	/**
	 * Bad programming style, but use exactly the same code as global align for
	 * update. Should have accounted for this in planning stage, but luckily
	 * this isn't meant to test software developing skills :)
	 */

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

		// initializeFirstRowVal(B, 0);
		for (int i = 1; i <= t_.length(); i++) {
			if (i <= Utilities.MAX_LAG) {
				setVal(B, 0, i, 0);
			} else {
				setVal(B, 0, i, Integer.MIN_VALUE);
			}
		}
		initializeFirstRowBackPointer(B, B);
	}

	@Override
	public void initializeC() {
		setVal(C, 0, 0, 0);
		for (int i = 1; i <= t_.length(); i++) {
			if (i <= Utilities.MAX_LAG) {
				setVal(C, i, 0, 0);
			} else {
				setVal(C, i, 0, Integer.MIN_VALUE);
			}
		}

		initializeFirstColBackPointer(C, C);
		initializeFirstRowVal(C, Integer.MIN_VALUE);
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

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return SEMI_GLOBAL;
	}

	@Override
	public String toString() {
		return "semi global NW aligner";
	}

	@Override
	public Aligner clone() {
		// TODO Auto-generated method stub
		return new NWSemiGlobalAligner();
	}

}
