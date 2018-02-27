package sequence.encoding;

import java.util.Arrays;

import org.omg.IOP.ENCODING_CDR_ENCAPS;

import sequence.Element;
import sequence.Sequence;
import utils.StockDataObj;
import utils.Utilities;

public class CorrelationWindowEncoder extends CorrelationEncoder {

	int window_; // number of prices to be averaged in each calculation

	public CorrelationWindowEncoder(int window) {
		window_ = window;
	}

	protected double[] getMovingAverages(Sequence s) {
		double[] movingAvgs = new double[s.length() - window_+1 ];

		for (int i = 1; i < movingAvgs.length; i++) {
			movingAvgs[i] = getMean(i , i + window_-1, s);
		}
		if (Utilities.VERBOSE) {
			System.out.println("moving averages for " + s.getID() + ": " + Arrays.toString(movingAvgs));
			System.out.println();
		}
		return movingAvgs;

	}

	protected double[] getMovingVars(Sequence s) {

		double[] movingVars = new double[s.length() - window_ + 1];
		for (int i = 1; i < movingVars.length; i++) {
			movingVars[i] = getVariance(i , i + window_-1, s);
		}
		if (Utilities.VERBOSE) {
			System.out.println("moving vars for " + s.getID() + ": " + Arrays.toString(movingVars));
			System.out.println();
		}
		return movingVars;

	}

	@Override
	public Sequence encode(Sequence toEncode) {
		double[] movingVars = getMovingVars(toEncode);
		double[] movingAvgs = getMovingAverages(toEncode);
		Sequence encoded = new Sequence(new StockDataObj((StockDataObj) toEncode.getData(), this.getEncodingType()));
		for (int i = 1; i < movingAvgs.length; i++) {
			double val = (toEncode.getVal(i + window_) - movingAvgs[i]) / (1+Math.pow(movingVars[i], 0.5));
			double transformed=Math.signum(val)*(Math.log(Math.abs(val)+1));
			encoded.add(new Element(getLabel(transformed), i + 1, "correlation window encoded value", transformed));
		}
		return encoded;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "correlation window encoder, window=" + window_;
	}

	@Override
	public EncodingType getEncodingType() {
		// TODO Auto-generated method stub
		return EncodingType.CorrelationWindowEncoder;
	}

}
