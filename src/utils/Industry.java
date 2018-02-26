package utils;

public class Industry {

	String name_;

	public Industry(String name) {
		name = name.toLowerCase().trim();
		name_ = name;
	}

	public String getName() {
		return name_;
	}

	public boolean equals(Object obj) {
		if (obj instanceof Industry) {
			Industry o = (Industry) obj;
			return o.getName().equals(this.getName());
		}
		return false;
	}

	public String toString() {
		return toString();
	}

}
