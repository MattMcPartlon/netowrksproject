package alignment;

import scoring.ScoreFunction;
import sequence.Sequence;

/**
 * Class meant to implement and evaluate pairwise alignment of sequences given
 * scoring function
 * 
 * @author matt
 *
 */
public abstract class CorrAligner extends Aligner {

	protected Alignment alignment_;
	protected ScoreFunction f_;
	protected Sequence s_, t_;
	protected double E, O; // O is penalty for opening a gap, E is penalty for
							// extending a gap
	protected double[][] a_;
	private double[][] b_;
	private double[][] c_;
	protected final int A = 1, B = 2, C = 3, NONE = 0;
	public boolean debug;
	// the type of alignment being done, to be returned by getType() method
	public static final int GLOBAL = 1, SEMI_GLOBAL = 2, LOCAL = 3;

	// a(i,j): best alignment of s[1..i] & t[1..j] that aligns s[i] with t[j]
	// b(i,j): best alignment of s[1..i] & t[1..j] that aligns gap with t[j]
	// c(i,j): best alignment of s[1..i] & t[1..j] that aligns s[i] with gap

	/**
	 * 
	 * used to produce ML alignment between sequences s and t given the score
	 * function f.
	 * 
	 * @param s
	 *            sequence to be aligned to t
	 * @param t
	 *            sequence to be aligned to s
	 * @param f
	 *            score function used to evaluate likelihood of alignment
	 */
	public void init(Sequence s, Sequence t, ScoreFunction f) {
		// initialize instance variables
		s_ = s;
		t_ = t;
		f_ = f;

		// initialize new matrices for instance variables a,b, and c
		int m;
		m = s.length() + 1;

		a_ = new double[m][2];
		b_ = new double[m][2];
		c_ = new double[m][2];

		// store these locally - requires less method calls
		E = f_.getExtendPenalty();
		O = f_.getOpenPenalty();

		// initializes tables
		initializeDPTables();
	}

	private void checkMatrix(int mat) {
		if (mat != A && mat != B && mat != C) {
			throw new IllegalArgumentException(
					"mat must correspond to matrix a, b or c (i.e mat in {1,2,3}, got: " + mat);
		}
	}

	private void checkValidIndex(int index) {
		if (index > a_.length || index < 0)
			throw new IllegalArgumentException("got index: " + index);
	}

	private void checkValidLevel(int level) {
		if (!(level >= 0 && level <= 1)) {
			throw new IllegalArgumentException("level: " + level + " is not a valid level");
		}
	}

	private void setVal(int mat, int index, double val, int level) {

		checkValidIndex(index);
		checkValidLevel(level);
		getMatrix(mat)[index][level] = val;

	}

	private double getVal(int mat, int index, int level) {

		checkValidIndex(index);
		checkValidLevel(level);
		return getMatrix(mat)[index][level];

	}

	private double[][] getMatrix(int mat) {
		checkMatrix(mat);
		if (mat == A) {
			return a_;
		} else if (mat == B) {
			return b_;
		} else {// mat==c
			return c_;
		}

	}

	protected double getVal(int mat, int pos) {
		return getVal(mat, pos, 0);
	}

	protected double getBackPointer(int mat, int pos) {
		return getVal(mat, pos, 1);
	}

	protected void setVal(int mat, int index, double val) {
		setVal(mat, index, val, 0);
	}

	protected void setBackPointer(int mat, int index, int fromMat) {
		checkMatrix(fromMat);
		setVal(mat, index, fromMat, 1);
	}

	/**
	 * initialize entries in DP tables a_, b_, and c_ depending on alignment
	 * scheme (i.e. global, semi global, or local)
	 */
	protected void initializeDPTables() {
		initializeA();
		initializeB();
		initializeC();
	}

	/**
	 * 
	 * @return the score achieved by ML alignment given alignment scheme (i.e
	 *         global, semi global, or local)
	 */

	public abstract double getAlignmentScore();

	/**
	 * @return ML alignment given alignment scheme (i.e global, semi global, or
	 *         local)
	 */

	public Alignment getAlignment(Sequence s, Sequence t, ScoreFunction f) {
		init(s, t, f);
		align();
		return doTraceback();
	}

	/**
	 * Initialize DP tables based on type of sequence alignment (global, local
	 * semi-global, e.g).
	 */
	public void initializeMats() {
		initializeA();
		initializeB();
		initializeC();
	}

	public void initializeA() {
		a_[0][0] = 0;
		a_[0][1] = 0;
	}

	public void initializeB() {
		b_[0][0] = 0;
		b_[0][1] = 0;
	}

	public void initializeC() {
		c_[0][0] = 0;
		c_[0][1] = 0;
	}

	/**
	 * 
	 * @param i
	 * @param j
	 * @return the matrix in which the value stored at A(i,j) was derived
	 *         from.(A,B,C, or NONE)
	 */
	protected int updateA(int i) {
		double a, b, c;
		double score = f_.getSubScore(s_.get(i), t_.get(i));
		a = getVal(A, i - 1) + score;
		b = getVal(B, i - 1) + score;
		c = getVal(C, i - 1) + score;

		double val = max(a, b, c);
		setVal(A, i, val);
		return (val == a) ? A : (val == b) ? B : C;

	}

	/**
	 * This method is meant to update the i^th entry of the table b_, i.e. b(i)
	 * is best alignment s.t. gap -s[i] is aligned to t[i].
	 * 
	 * @param i
	 * @param j
	 *            * @return the matrix in which the value stored at B(i,j) was
	 *            derived from.(A,B,C, or NONE).
	 */
	protected int updateB(int i) {
		double a, b, c;
		double score = f_.getSubScore(s_.get(i).negate(), t_.get(i));
		a = getVal(A, i - 1) - (O) + score;
		b = getVal(B, i - 1) - (E) + score;
		c = getVal(C, i - 1) - (O) + score;

		double val = max(a, b, c);
		setVal(B, i, val);
		return (val == a) ? A : (val == b) ? B : C;
	}

	/**
	 * This method is meant to update the i^th entry of the table c_, i.e. c(i)
	 * is best alignment s.t. s[i] is aligned to -t[i]
	 * 
	 * @param i
	 * 
	 *            * @return the matrix in which the value stored at C(i,j) was
	 *            derived from (A,B,C, or NONE).
	 */
	protected int updateC(int i) {
		double a, b, c;
		double score = f_.getSubScore(s_.get(i), t_.get(i).negate());
		a = getVal(A, i - 1) - (O) + score;
		b = getVal(B, i - 1) - (O) + score;
		c = getVal(C, i - 1) - E + score;
		double val = max(a, b, c);
		setVal(C, i, val);

		return (val == a) ? A : (val == b) ? B : C;
	}

	/**
	 * fills (i,j)^th entry of DP tables a_, b_, and c_
	 * 
	 * @param i
	 * @param j
	 */
	private void updateDPTables(int i) {
		int fromA = updateA(i);
		int fromB = updateB(i);
		int fromC = updateC(i);
		setBackPointer(A, i, fromA);
		setBackPointer(B, i, fromB);
		setBackPointer(C, i, fromC);
	}

	protected double max(double a, double b, double c) {
		return Math.max(Math.max(a, b), c);
	}

	public void align() {
		for (int i = 1; i < a_.length; i++) {
			updateDPTables(i);
		}
	}

	/**
	 * return the alignment type being implemented
	 */
	public abstract int getType();

	public abstract Alignment doTraceback();

}
