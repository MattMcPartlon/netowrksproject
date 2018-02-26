package utils;



public class TradingDay implements Comparable<TradingDay> {

		Time t_;
		double open_, close_, volume_, low_, high_;

		public TradingDay(Time t, String open, String close, String low, String high, String volume) {
			t_ = t;
			open_ = Double.parseDouble(open);
			close_ = Double.parseDouble(close);
			low_ = Double.parseDouble(low);
			high_ = Double.parseDouble(high);
			volume_ = Double.parseDouble(volume);
		}

		@Override
		public int compareTo(TradingDay o) {
			return this.t_.compareTo(o.t_);
		}

		public Time getTime() {
			return t_;
		}

		public double get(int attribute) {
			if (attribute == Utilities.close) {
				return close_;
			} else if (attribute == Utilities.high) {
				return high_;
			} else if (attribute == Utilities.open) {
				return open_;
			} else if (attribute == Utilities.low) {
				return low_;
			} else if (attribute == Utilities.volume) {
				return volume_;
			} else {
				throw new IllegalArgumentException("attribute value" + attribute
						+ " is not valid! See Utilities class for valid attribute values");
			}
		}

	}