package io.alignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import alignment.Alignment;
import sequence.Element;
import sequence.Sequence;
import utils.Utilities;

public class AlignmentWriter {
	List<Alignment> alignments_;

	public void writeAlignments(List<Alignment> alignments, File f, String header) {
		alignments_ = alignments;

		if (Utilities.VERBOSE) {
			System.out.println("writing alignments...");
		}
		int maxThreads = Utilities.MaxThreads;
		CountDownLatch latch = new CountDownLatch(maxThreads);
		String pathname = f.getAbsolutePath();
		int numToWrite = alignments_.size() / maxThreads;
		for (int i = 0; i < maxThreads - 1; i++) {
			Thread t = new Thread(new SyncWriter(new File(pathname + "" + i), i * numToWrite, (i + 1) * numToWrite,
					latch, header, i));
			t.start();
		}
		Thread t = new Thread(new SyncWriter(new File(pathname + "" + (maxThreads - 1)), (maxThreads - 1) * numToWrite,
				alignments.size(), latch, header, maxThreads - 1));
		t.start();

		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} // Wait for countdown

		if (Utilities.VERBOSE) {
			System.out.println("wrote alignments");
		}

	}

	public synchronized Alignment getAlignment(int pos) {
		return alignments_.get(pos);
	}

	private String formatElement(Element e) {
		double val = e.getVal();
		String name = e.getName();
		return name + "~" + val;
	}

	private String getFormatted(Alignment a) {
		Sequence s1 = a.getS1();
		Sequence s2 = a.getS2();
		String str1 = s1.getID() + ",";
		String str2 = s2.getID() + ",";
		if (s1.length() != s2.length()) {
			throw new IllegalArgumentException("Sequences in alignment have different lengths, got s1 length: "
					+ s1.length() + " and s2 length: " + s2.length());
		}
		for (int i = 1; i < s1.length(); i++) {
			str1 += formatElement(s1.get(i)) + ",";
			str2 += formatElement(s2.get(i)) + ",";
		}
		str1 += formatElement(s1.get(s1.length()));
		str2 += formatElement(s2.get(s2.length()));
		return str1 + "\n" + str2;

	}

	public class SyncWriter implements Runnable {
		File toWrite_;
		int start_, end_;
		String header_;
		CountDownLatch latch_;
		int idx_;

		public SyncWriter(File toWrite, int start, int end, CountDownLatch latch, String header, int idx) {
			toWrite_ = toWrite;
			start_ = start;
			end_ = end;
			latch_ = latch;
			header_ = header;
			idx_ = idx;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			PrintWriter pw;
			try {
				pw = new PrintWriter(toWrite_);
				pw.println(header_);
				for (int i = start_; i < end_; i++) {
					if (Math.random() < .15) {
						pw.println(getFormatted(getAlignment(i)));
						pw.println("END");
						if (Math.random() < .01) {
							System.out.println(
									"thread " + idx_ + " writing file: " + i + ", " + (end_ - i) + " left to write");
						}
					}
				}
				pw.flush();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			latch_.countDown();

		}

	}

}
