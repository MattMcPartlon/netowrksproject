package sequence.encoding;

import sequence.Element;
import sequence.Sequence;
import utils.StockDataObj;

public class SimpleCorrelationEncoder extends CorrelationEncoder {

	public Sequence encode(Sequence s) {
		Sequence seq = new Sequence(new StockDataObj((StockDataObj) s.getData(), getEncodingType()));

		for (int j = 1; j <= s.length(); j++) {
			
			double mean = getMean(1, s.length(), seq);
			double sd = Math.pow(getVariance(1, s.length(), seq), .5);
			double val = (s.getVal(j) - mean) / sd;

			Element e = new Element(getLabel(val), j, "simple correlation encoded elt", val);

			seq.add(e);

		}
		return seq;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "simple correlation encoder";
	}

	@Override
	public EncodingType getEncodingType() {
		// TODO Auto-generated method stub
		return EncodingType.SimpleCorrelationEncoder;
	}
}
