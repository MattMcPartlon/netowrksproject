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
public abstract class NWAligner extends Aligner {

	
	protected ScoreFunction f_;
	protected Sequence s_, t_;
	protected double E, O; // O is penalty for opening a gap, E is penalty for
							// extending a gap
	private double[][][] a_, b_, c_;
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
		int m, n;
		m = s.length() + 1;
		n = t.length() + 1;
		a_ = new double[m][n][2];
		b_ = new double[m][n][2];
		c_ = new double[m][n][2];

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

	private void checkValidIndex(int row, int col) {
		if (row > a_.length || col > a_[0].length || row < 0 || col < 0)
			throw new IllegalArgumentException("row or column out of bounds, got row: " + row + ", col: " + col
					+ ", num rows: " + a_.length + ", num cols: " + a_[0].length);
	}

	private void checkValidLevel(int level) {
		if (!(level >= 0 && level <= 1)) {
			throw new IllegalArgumentException("level: " + level + " is not a valid level");
		}
	}

	private void setVal(int mat, int row, int col, double val, int level) {

		checkValidIndex(row, col);
		checkValidLevel(level);
		getMatrix(mat)[row][col][level] = val;

	}

	private double getVal(int mat, int row, int col, int level) {

		checkValidIndex(row, col);
		checkValidLevel(level);
		return getMatrix(mat)[row][col][level];

	}

	private double[][][] getMatrix(int mat) {
		checkMatrix(mat);
		if (mat == A) {
			return a_;
		} else if (mat == B) {
			return b_;
		} else {// mat==c
			return c_;
		}

	}

	protected void initializeFirstRowVal(int mat, int val) {
		if (t_.length() != getMatrix(mat).length - 1) {
			throw new IllegalArgumentException("logical error");
		}
		for (int i = 1; i <= t_.length(); i++) {
			setVal(mat, 0, i, val);
		}

	}
	


	protected void initializeFirstColVal(int mat, int val) {
		if (s_.length() != getMatrix(mat)[0].length - 1) {
			throw new IllegalArgumentException("logical error");
		}
		for (int i = 1; i <= s_.length(); i++) {
			setVal(mat, i, 0, val);
		}
	}

	protected void initializeFirstRowBackPointer(int mat, int fromMat) {
		for (int i = 1; i <= t_.length(); i++) {
			setBackPointer(mat, 0, i, fromMat);
		}

	}

	protected void initializeFirstColBackPointer(int mat, int fromMat) {
		for (int i = 1; i <= s_.length(); i++) {
			setBackPointer(mat, i, 0, fromMat);
		}

	}

	protected double getVal(int mat, int row, int col) {
		return getVal(mat, row, col, 0);
	}

	protected double getBackPointer(int mat, int row, int col) {
		return getVal(mat, row, col, 1);
	}

	protected void setVal(int mat, int row, int col, double val) {
		setVal(mat, row, col, val, 0);
	}

	protected void setBackPointer(int mat, int row, int col, int fromMat) {
		checkMatrix(fromMat);
		setVal(mat, row, col, fromMat, 1);
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

	@Override
	public Alignment getAlignment(Sequence s1, Sequence s2, ScoreFunction f) {
		init(s1, s2, f);
		align();
		return doTraceback();
	}

	/**
	 * Initialize DP tables based on type of sequence alignment (global, local
	 * semi-global, e.g).
	 */
	public abstract void initializeA();

	public abstract void initializeB();

	public abstract void initializeC();

	/**
	 * 
	 * @param i
	 * @param j
	 * @return the matrix in which the value stored at A(i,j) was derived
	 *         from.(A,B,C, or NONE)
	 */
	protected abstract int updateA(int i, int j);

	/**
	 * 
	 * @param i
	 * @param j
	 * 
	 */

	/**
	 * This method is meant to update the (i,j)^th entry of the table b_, i.e.
	 * b(i,j) is best alignment s.t. gap is aligned to t[j].
	 * 
	 * @param i
	 * @param j
	 *            * @return the matrix in which the value stored at B(i,j) was
	 *            derived from.(A,B,C, or NONE).
	 */
	protected int updateB(int i, int j) {
		double a, b, c;
		a = getVal(A, i, j - 1) - (O);// starting a gap in s

		b = getVal(B, i, j - 1) - E; // extending a gap in s
		c = getVal(C, i, j - 1) - (O); // stopping a gap in t, and starting
										// one in s

		double val = max(a, b, c);
		setVal(B, i, j, val);
		return (val == a) ? A : (val == b) ? B : C;
	}

	/**
	 * This method is meant to update the (i,j)^th entry of the table c_, i.e.
	 * c(i,j) is best alignment s.t. s[i] is aligned to gap
	 * 
	 * @param i
	 * @param j
	 *            * @return the matrix in which the value stored at C(i,j) was
	 *            derived from (A,B,C, or NONE).
	 */
	protected int updateC(int i, int j) {
		double a, b, c;
		a = getVal(A, i - 1, j) - (O); // starting a gap in t
		b = getVal(B, i - 1, j) - (O); // stop a gap in s and start one in t
		c = getVal(C, i - 1, j) - E; // continue to extend gap in t
		double val = max(a, b, c);
		setVal(C, i, j, val);

		return (val == a) ? A : (val == b) ? B : C;
	}

	/**
	 * fills (i,j)^th entry of DP tables a_, b_, and c_
	 * 
	 * @param i
	 * @param j
	 */
	private void updateDPTables(int i, int j) {
		int fromA = updateA(i, j);
		int fromB = updateB(i, j);
		int fromC = updateC(i, j);
		setBackPointer(A, i, j, fromA);
		setBackPointer(B, i, j, fromB);
		setBackPointer(C, i, j, fromC);
	}

	protected double max(double a, double b, double c) {
		return Math.max(Math.max(a, b), c);
	}

	public void align() {
		for (int i = 1; i < a_.length; i++) {
			for (int j = 1; j < a_[i].length; j++) {
				updateDPTables(i, j);
			}
		}
	}

	/**
	 * return the alignment type being implemented
	 */
	public abstract int getType();

	public abstract Alignment doTraceback();

}
