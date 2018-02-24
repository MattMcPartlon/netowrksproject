package sequence;

import java.util.ArrayList;
import java.util.List;

import io.sequence.StockPriceReader;
import sequence.encoding.EncodingType;
import utils.DataObj;
import utils.StockDataObj;

/**
 * Sequence - represented by a list of Elements All sequences should have some
 * 
 * @author matt
 *
 */

public class Sequence {

	List<Element> seq_;
	DataObj data_;

	public Sequence(DataObj obj) {

		data_ = obj.clone();
		seq_ = new ArrayList<>();
	}

	public Sequence(Sequence s) {
		this.data_ = s.getData().clone();
		this.seq_ = new ArrayList<>();
		seq_.addAll(s.seq_);

	}

	public double getVal(int pos) {
		return this.get(pos).getVal();
	}

	public DataObj getData() {
		return data_;
	}

	public Element get(int pos) {
		return seq_.get(pos - 1);
	}

	public int length() {
		return seq_.size();
	}

	public void removeLastElt() {
		seq_.remove(seq_.size() - 1);
	}

	public String getID() {
		return data_.getID();
	}

	/*
	 * adds element to tail of sequence
	 */
	public void add(Element elt) {
		seq_.add(elt);
	}

	public String toString() {
		return seq_.toString();
	}

	public Sequence deepCopy() {
		Sequence s = new Sequence(this.getData());
		s.seq_ = new ArrayList<>(this.seq_);
		return s;
	}

	public void reverseOrder() {
		if (seq_ == null) {
			throw new IllegalArgumentException("sequence is null!");
		}
		ArrayList<Element> temp = new ArrayList<>();
		for (int i = seq_.size() - 1; i >= 0; i--) {
			temp.add(seq_.get(i));
		}
		seq_ = temp;
	}

	public String asString() {
		String str = "";
		for (int i = 1; i <= this.length(); i++) {
			str += this.get(i) + " ";
		}
		return str;
	}

	public boolean isMalformed() {
		return this.length() < 1 || this.get(1) == Element.gap || this.get(this.length()) == Element.gap;

	}

	public Sequence clone() {
		return new Sequence(this);
	}

}
