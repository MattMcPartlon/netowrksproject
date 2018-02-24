package utils;

public class Pair<F, S> {
	F first_;
	S second_;

	public void put(F first, S second) {
		first_ = first;
		second_ = second;
	}

	public F getFirst() {
		return first_;
	}

	public S getSecond() {
		return second_;
	}

	public void setFirst(F first) {
		first_ = first;
	}

	public void setSecond(S second) {
		second_ = second;
	}

}
