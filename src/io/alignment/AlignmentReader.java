package io.alignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

import alignment.Alignment;
import alignment.AlignmentParameters;
import alignment.AlignmentType;
import io.alignment.AlignmentWriter.SyncWriter;
import sequence.Element;
import sequence.Sequence;
import sequence.encoding.EncodingType;
import utils.Company;
import utils.DataObj;
import utils.Pair;
import utils.StockDataObj;
import utils.Time;
import utils.Utilities;

public class AlignmentReader {
	HashMap<Company, HashMap<Company, Alignment>> alignments_;
	Time start_, end_;
	EncodingType eTy_;
	AlignmentType alignmentType_;

	public HashMap<Company, HashMap<Company, Alignment>> readAlignments(File f, AlignmentParameters params) {
		alignments_ = new HashMap<>();

		start_ = params.getStart();
		end_ = params.getEnd();
		eTy_ = params.getEncodingType();
		alignmentType_ = params.getAlignmentType();

		if (Utilities.VERBOSE) {
			System.out.println("reading alignments...");
		}
		int maxThreads =Utilities.MaxThreads;
		CountDownLatch latch = new CountDownLatch(maxThreads);
		String pathname = f.getAbsolutePath();

		for (int i = 0; i < maxThreads; i++) {
			Thread t = new Thread(new AlignmentReaderWorker(new File(pathname + "" + i), latch));
			t.start();
		}

		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} // Wait for countdown

		if (Utilities.VERBOSE) {
			System.out.println("read alignments");
		}

		return alignments_;

	}

	public synchronized void addAlignment(Alignment a, Company c1, Company c2) {
		if (!alignments_.containsKey(c1)) {
			alignments_.put(c1, new HashMap<>());
		}
		// add alignments to map
		alignments_.get(c1).put(c2, a);
	}

	private void checkValidCompanies(Company c1, Company c2, String s1, String s2) {
		if (c1 == null || c2 == null) {
			if (c1 == null) {
				System.out.println("could not find company for c1: " + s1);
			}
			if (c2 == null) {
				System.out.println("could not find company for c2: " + s2);
			}
			throw new IllegalStateException("could not find company for sequence in this alignment");
		}
	}

	private void checkValidSeqs(String[] seq1, String[] seq2) {
		if (seq1.length != seq2.length) {
			System.out.println("seq1: " + seq1);
			System.out.println("seq2: " + seq2);
			throw new IllegalStateException("got seq1 length" + seq1.length + " and seq2 length: " + seq2.length);
		}
	}

	public class AlignmentReaderWorker implements Runnable {

		File toRead_;
		CountDownLatch latch_;

		public AlignmentReaderWorker(File toRead, CountDownLatch latch) {
			toRead_ = toRead;
			latch_ = latch;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String[] seq1 = null, seq2 = null, s1Elt = null, s2Elt = null;

			int currIndex = 0;
			try {
				Scanner s = new Scanner(toRead_);
				String header = s.nextLine();
				while (s.hasNextLine()) {

					seq1 = s.nextLine().split(",");
					seq2 = s.nextLine().split(",");

					// make sure sequences are as we expect
					checkValidSeqs(seq1, seq2);
					String s1ID = seq1[0];
					String s2ID = seq2[0];
					Company c1 = Utilities.findCompany(s1ID);
					Company c2 = Utilities.findCompany(s2ID);

					// make sure companies are as we expect
					checkValidCompanies(c1, c2, s1ID, s2ID);

					DataObj d1 = new StockDataObj(c1, start_, end_, eTy_);
					DataObj d2 = new StockDataObj(c2, start_, end_, eTy_);

					Sequence s1 = new Sequence(d1);
					Sequence s2 = new Sequence(d2);
					boolean caughtError = false;
					// add symbols to sequences
					for (int i = 1; i < seq1.length; i++) {
						currIndex = i;

						s1Elt = seq1[i].split("~");
						s2Elt = seq2[i].split("~");
						Element e1, e2;

						try {
							if (s1Elt[0].equalsIgnoreCase("G")) {
								e1 = Element.gap;
							} else {
								e1 = new Element(s1Elt[0], i, "elt read from alignment reader",
										Double.parseDouble(s1Elt[1]));
							}
							if (s2Elt[0].equalsIgnoreCase("G")) {
								e2 = Element.gap;
							} else {
								e2 = new Element(s2Elt[0], i, "elt read from alignment reader",
										Double.parseDouble(s2Elt[1]));
							}

							s1.add(e1);
							s2.add(e2);

						} catch (Exception e) {
							System.out.println("caught exception: " + e.getMessage());
							caughtError = true;

						}
						if (caughtError) {
							break;
						}

					}
					if (!caughtError) {
						Alignment a = new Alignment(s1, s2, alignmentType_);
						a.setAligned(s1, s2);
						addAlignment(a, c1, c2);
					}

					// another sanity check
					if (s.hasNextLine()) {
						String next = s.nextLine().trim();
						if (!next.equals("END")) {
							throw new IllegalStateException("expected to see 'END' but got: " + next);
						}
					} else {
						throw new IllegalStateException("should always have next line!!!");
					}

				}

			} catch (Exception e) {
				System.out.println("caught exception!");
				System.out.println(Arrays.toString(seq1));
				System.out.println(Arrays.toString(seq2));
				System.out.println("num elts to parse: " + seq1.length);
				System.out.println("current index:" + currIndex);
				System.out.println("seq1 i: " + seq1[currIndex]);
				System.out.println("seq2 i: " + seq2[currIndex]);
				System.out.println("s1elt: " + Arrays.toString(s1Elt));
				System.out.println("s2elt: " + Arrays.toString(s2Elt));
				e.printStackTrace();
			}
			latch_.countDown();

		}

	}

}
