package utils;

public class Time implements Comparable<Time> {

	int year_, month_, day_;

	public Time(int year, int month, int day) {
		year_ = year;
		month_ = month;
		day_ = day;

	}

	public boolean equals(Object obj) {
		if (obj instanceof Time) {
			Time o = (Time) obj;
			return this.year_ == o.year_ && this.month_ == o.month_ && this.day_ == o.day_;
		}
		return false;
	}

	public String toString() {
		return year_ + "_" + month_ + "_" + day_;
	}

	@Override
	public int compareTo(Time o) {
		if (year_ < o.year_) {
			return -1;
		} else if (year_ > o.year_) {
			return 1;
		} else {
			if (month_ < o.month_) {
				return -1;
			} else if (month_ > o.month_) {
				return 1;
			} else {
				if (day_ < o.day_) {
					return -1;
				} else if (day_ > o.day_) {
					return 1;
				} else {
					return 0;
				}
			}

		}

	}
}