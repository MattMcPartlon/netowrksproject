package alignment;

import scoring.ScoreFunction;
import sequence.encoding.Encoder;

public class AlignmentParameters {

	Aligner a_;
	ScoreFunction f_;
	Encoder e_;

	public AlignmentParameters(ScoreFunction f, Encoder e, Aligner a) {
		f_ = f;
		a_ = a;
		e_ = e;

	}

	public Aligner getAligner() {
		return a_;
	}

	public String toString() {
		return a_.toString() + "\n" + f_.toString() + "\n" + e_.toString();
	}

}
