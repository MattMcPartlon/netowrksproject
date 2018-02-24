package sequence;

public class Element {

	private String name_, sign_;
	private final int pos_;
	private final String description_;
	private double val_;

	public Element(String name, int pos, String desc, double val) {
		name_ = name;
		pos_ = pos;
		description_ = desc;
		val_ = val;
		sign_ = "+";

	}

	public Element(String name, String sign, int pos, String desc, double val) {
		name_ = name;
		pos_ = pos;
		description_ = desc;
		val_ = val;
		sign_ = "+";

	}

	public void flipSign() {
		if (sign_.equals("+")) {
			sign_ = "-";
		} else {
			sign_ = "+";
		}
	}

	public Element(Element e, double val) {
		name_ = e.name_;
		pos_ = e.pos_;
		description_ = e.description_;
		val_ = val;
		sign_ = e.sign_;

	}

	public Element negate() {
		Element newElt = new Element(this, this.getVal() * (-1));
		newElt.flipSign();
		return newElt;
	}

	public static final Element gap = new Element("G", " ", -1, "gap of some length", 0);

	public String getName() {
		return name_;
	}

	public int getPos() {
		return pos_;
	}

	public String getDescription() {
		return description_;
	}

	public double getVal() {
		return val_;
	}

	public void setVal(double val) {
		val_ = val;
	}

	public String getFormattedVal() {
		int nearest = (int) Math.round(val_ * 1000);
		double rounded = (nearest + 0.0) / 1000;
		String toReturn = rounded > 0 ? "+" + rounded : "" + rounded;

		for (int i = 0; toReturn.length() < 6; i++) {
			toReturn += "0";
		}

		return toReturn;

	}

	public String toString() {
		return (char) 34 + sign_ + getName() + ", " + getFormattedVal() + " " + (char) 34;
	}

	public boolean equals(Element e) {
		return name_.equals(e.name_);
	}

}