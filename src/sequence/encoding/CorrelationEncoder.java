package sequence.encoding;

import sequence.Sequence;

public abstract class CorrelationEncoder extends Encoder {

	protected double getMean(int start, int end, Sequence s) {
		checkValid(start, end, s);
		double sum = 0;
		for (int i = start; i <= end; i++) {
			sum += s.getVal(i);
		}
		return sum / (end - start + 1.0);
	}

	protected double getRawSum(int start, int end, Sequence s) {
		checkValid(start, end, s);
		double sum = 0;
		for (int i = start; i <= end; i++) {
			sum += s.getVal(i);
		}
		return sum;
	}

	protected double getVariance(int start, int end, Sequence s) {
		checkValid(start, end, s);
		if (start == end) {
			return Math.pow(s.getVal(start),2);
		}
		double mean = getMean(start, end, s);
		double sum = 0;
		for (int i = start; i <= end; i++) {
			sum += Math.pow((s.getVal(i) - mean), 2);
		}

		return sum / (end - start);
	}

	private void checkValid(int start, int end, Sequence s) {
		if (s == null) {
			throw new IllegalArgumentException("sequence is null!");
		}
		if (start > end || end > s.length() || start < 1) {
			throw new IllegalArgumentException(
					"got start: " + start + " end: " + end + " sequence length: " + s.length());
		}
	}

	protected String getLabel(double val) {
		return val > 0 ? "U" : "D";
	}

}
