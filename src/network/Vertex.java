package network;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

import utils.DataObj;
import utils.Utilities;

public abstract class Vertex {

	VertexDataObject data_;

	public Vertex(VertexDataObject data) {
		data_ = data;

	}

	public abstract String toString();

	public abstract boolean equals(Object o);

	public abstract int hashCode();

	public int getIndex() {
		return data_.getIndex();
	}

	public DataObj getData() {
		return data_;
	}



}
