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
		double[] movingAvgs = new double[s.length() - window_ + 1];

		for (int i = 1; i < movingAvgs.length; i++) {
			movingAvgs[i] = getMean(i, i + window_ - 1, s);
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
			movingVars[i] = getVariance(i, i + window_ - 1, s);
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
			double div=Math.sqrt(movingVars[i]);
			//control for strange cases
			if(div<.00001){
				div=1;
			}
			double val = (toEncode.getVal(i + window_) - movingAvgs[i]) /div;
			double transformed = Math.signum(val) * (Math.log(1 + Math.abs(val)));
			encoded.add(new Element(getLabel(transformed), i, "correlation window encoded value", transformed));
		}
		return encoded;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "correlation window encoder, window=" + window_;
	}

	public int getWindow() {
		return window_;
	}

	@Override
	public EncodingType getEncodingType() {
		// TODO Auto-generated method stub
		return EncodingType.CorrelationWindowEncoder;
	}

}
