package alignment;

import scoring.ScoreFunction;
import sequence.Sequence;

public class TrivialAligner extends Aligner {

	public TrivialAligner() {

	}

	@Override
	public Alignment getAlignment(Sequence s, Sequence t, ScoreFunction f) {
		return new Alignment(s, t, AlignmentType.Trivial);
	}

	@Override
	public String toString() {
		return "trivial alignment";
	}

	@Override
	public Aligner clone() {
		return new TrivialAligner();
	}

	@Override
	public AlignmentType getAlignmentType() {
		// TODO Auto-generated method stub
		return AlignmentType.Trivial;
	}

}
