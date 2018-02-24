package io.sequence;

import java.io.File;
import java.util.List;
import alignment.Alignment;


public abstract class SWriter {
	
	File toWrite_;
	
	public SWriter(File toWrite){
		toWrite_=toWrite;
	}
	
	public abstract void write(List<Alignment> alignments);

}
