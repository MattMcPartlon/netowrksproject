package alignment;

import org.omg.CORBA.portable.ValueInputStream;

import sequence.Element;
import sequence.Sequence;
import utils.DataObj;
import utils.Pair;

public class Alignment {

	Sequence s1_, s2_, origS1_, origS2_;
	int startIndexs1_ = 1, startIndexs2_ = 1;
	int numGaps_ = -1;
	AlignmentType alignmentType_;
	double tempScore_, score_ = Integer.MIN_VALUE, avgOffset_ = Integer.MIN_VALUE;

	public Alignment() {
	}

	public Alignment(Sequence s1, Sequence s2, AlignmentType t) {
		s1_ = s1;
		s2_ = s2;
		setAligned(s1_, s2_);
		alignmentType_ = t;
	}

	public Alignment(Sequence s1, Sequence s2, Sequence os1, Sequence os2, AlignmentType t) {
		s1_ = s1;
		origS1_ = os1.deepCopy();
		s2_ = s2;
		origS2_ = os2.deepCopy();
		setAligned(s1_, s2_);
		alignmentType_ = t;
	}

	public boolean scoreSetQ() {
		return score_ != Integer.MIN_VALUE;
	}

	public boolean averageOffsetSetQ() {
		return avgOffset_ != Integer.MIN_VALUE;
	}

	public void setTempScore(double score) {
		tempScore_ = score;
	}

	public void setScore(double score) {

		score_ = score;
	}

	public double getScore() {
		if (score_ == Integer.MIN_VALUE) {
			throw new IllegalAccessError("average offset has not been computed for this alignment");
		}
		return score_;
	}

	public void setNumGaps(int numGaps) {
		numGaps_ = numGaps;
	}

	public void setAverageOffset(double avgOffset) {
		avgOffset_ = avgOffset;
	}

	public boolean numGapsSetQ() {
		return numGaps_ != -1;
	}

	public double getAverageOffset(Sequence s) {
		if (avgOffset_ == Integer.MIN_VALUE) {
			throw new IllegalAccessError("average offset has not been computed for this alignment");
		}
		if (s.equals(s1_)) {
			return avgOffset_;
		} else if (s.equals(s2_)) {
			return -1 * avgOffset_;
		} else {
			throw new IllegalArgumentException("sequence s: " + s.toString() + " is not part of this alignment");
		}

	}

	public AlignmentType getAlignmentType() {
		return alignmentType_;
	}

	public int getOrigS1Length() {
		return origS1_.length();
	}

	public int getOrigS2Length() {
		return origS2_.length();
	}

	public int getNumGaps() {
		if (!this.numGapsSetQ()) {
			throw new IllegalAccessError("num gaps has not been set");
		}
		return numGaps_;
	}

	public void SetOriginalSeqs(Sequence s, Sequence t) {
		origS1_ = s;
		origS2_ = t;
	}

	public void setStartIndices(int i, int j) {
		startIndexs1_ = i;
		startIndexs2_ = j;
	}

	public Pair<DataObj, DataObj> getData() {
		Pair<DataObj, DataObj> dat = new Pair<>();
		dat.put(s1_.getData(), s2_.getData());
		return dat;
	}

	public void swapOrder() {

		Sequence temp = s1_;
		// swap s1 and s2
		s1_ = s2_.deepCopy();
		s2_ = temp.deepCopy();

		// switch the start indices accordingly
		int temp2 = startIndexs1_;
		startIndexs1_ = startIndexs2_;
		startIndexs2_ = temp2;
		Sequence tempS = origS1_;
		origS1_ = origS2_;
		origS2_ = tempS;

	}

	public String getS1ID() {
		return s1_.getID();
	}

	public String getS2ID() {
		return s2_.getID();
	}

	public Sequence getS1() {
		return s1_.clone();
	}

	public Sequence getS2() {
		return s2_.clone();
	}

	/**
	 * creates an alignment object such that s1[i] is aligned with s2[i].
	 * 
	 * @param s1
	 *            sequence to be aligned
	 * @param s2
	 * @return alignment object where s1[i] aligned to s2[i] for i=1...length
	 * @throws IllegalArgumentException
	 *             if Sequences are not same length
	 */

	public boolean areValid(Sequence s, Sequence t) {
		return true;
	}

	public void setAligned(Sequence s1, Sequence s2) {
		if (s1 == null || s2 == null) {
			throw new IllegalArgumentException("s1 or s2 is null! got s1: " + s1 + " s2: " + s2);
		}
		if (s1.isMalformed() && s2.isMalformed()) {
			if (!areValid(s1, s2)) {
				System.out.println("s1: " + s1.asString());
				System.out.println("s2: " + s2.asString());
				throw new IllegalArgumentException("s1 or s2 is malformed! ");
			}
		}
		if (s1.length() != s2.length()) {
			throw new IllegalArgumentException("Can't create alignment of sequences with diff. lengths. got s1 length: "
					+ s1.length() + " and s2 length: " + s2.length());
		}

		s1_ = s1;
		s2_ = s2;
	}

	public int length() {

		return s1_.length();

	}

	public int getStartIndexS1() {
		return startIndexs1_;

	}

	public int getStartIndexS2() {
		return startIndexs2_;

	}

	/**
	 * gets the pair of elements at given position of alignment
	 * 
	 * @param pos
	 * @return array a s.t. a[0] is element corresponding to s1 at position pos
	 *         and a[1] is element corresponding to s2 at position pos.
	 */
	public Element[] pairAt(int pos) {
		return new Element[] { s1_.get(pos), s2_.get(pos) };
	}

	public void printAlignment() {
		String indent = "";
		int len1 = s1_.getID().length();
		int len2 = s2_.getID().length();

		for (int i = 0; i < Math.max(len1, len2) - Math.min(len1, len2); i++) {
			indent += " ";
		}
		if (len1 >= len2) {
			System.out.println(s1_.getID() + " : " + s1_.toString());
			System.out.println(s2_.getID() + indent + " : " + s2_.toString());
		} else {
			System.out.println(s1_.getID() + indent + " : " + s1_.toString());
			System.out.println(s2_.getID() + " : " + s2_.toString());
		}
	}
	

}
