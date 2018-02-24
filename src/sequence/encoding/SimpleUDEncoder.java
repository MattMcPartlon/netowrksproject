package sequence.encoding;

import sequence.Element;
import sequence.Sequence;
import utils.StockDataObj;

public class SimpleUDEncoder extends Encoder {

	public Sequence encode(Sequence s) {
		Sequence seq = new Sequence(new StockDataObj((StockDataObj) s.getData(), getEncodingType()));

		for (int j = 1; j <= s.length(); j++) {

			String str = s.get(j).getVal() > 0 ? "U" : "D";
			double val = s.get(j).getVal() > 0 ? 1.0 : -1.0;

			Element e = new Element(str, j, "", val);

			seq.add(e);

		}
		return seq;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Simple UD Encoder";
	}

	@Override
	public EncodingType getEncodingType() {
		return EncodingType.SimpleUDEncoder;
	}

}
