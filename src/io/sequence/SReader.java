package io.sequence;

import java.io.File;
import java.util.List;
import sequence.Sequence;

public abstract class SReader {

	File toRead_;

	public SReader(File toRead) {
		toRead_ = toRead;
	}

	public void setReadFile(File f) {
		toRead_ = f;
	}

	public abstract List<Sequence> read();

}
