package sequence.encoding;


import sequence.Sequence;

public abstract class Encoder {

	public abstract Sequence encode(Sequence toEncode);

	public abstract String getDescription();

	public abstract EncodingType getEncodingType();

}
