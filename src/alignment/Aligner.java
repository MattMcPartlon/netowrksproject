package alignment;

import scoring.ScoreFunction;
import sequence.Sequence;

public abstract class Aligner {

	public abstract Alignment getAlignment(Sequence s, Sequence t, ScoreFunction f);

	public abstract String toString();

	public abstract Aligner clone();

	public abstract AlignmentType getAlignmentType();

}
