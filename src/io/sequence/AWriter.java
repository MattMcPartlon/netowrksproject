package io.sequence;

import java.io.File;

import alignment.Alignment;

public abstract class AWriter {
	File f_;
	public AWriter(File f){
		f_=f;
	}

	public abstract void write(Alignment a, boolean toAppend);

}
