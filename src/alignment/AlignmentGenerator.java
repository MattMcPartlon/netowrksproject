package alignment;

import java.io.File;
import java.security.KeyStore.LoadStoreParameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;

import io.sequence.SReader;
import io.sequence.SWriter;
import io.sequence.StockPriceReader;
import scoring.CorrelationScoreFunction;
import scoring.ScoreFunction;
import sequence.Sequence;
import sequence.encoding.CorrelationWindowEncoder;
import sequence.encoding.Encoder;
import sequence.encoding.SimpleCorrelationEncoder;
import stats.AlignmentStats;
import stats.CorrelationAlignmentStats;
import utils.Pair;
import utils.Utilities;

public class AlignmentGenerator {

	SReader sr_;
	Encoder e_;
	Aligner a_;
	ScoreFunction f_;
	AlignmentStats stats_;
	int seqLength_;

	public AlignmentGenerator(SReader s, Encoder e, Aligner a, ScoreFunction f, AlignmentStats stats) {
		a_ = a;
		e_ = e;
		sr_ = s;
		f_ = f;
		stats_ = stats;

	}

	/**
	 * get all alignments for sequences read in by sequence reader uses max
	 * number of threads to do this...
	 * 
	 * 
	 * @return
	 */
	public List<Alignment> getAlignments() {

		List<Sequence> encoded = loadSequences();

		boolean[] safeElts = new boolean[encoded.size()];
		List<Pair<Integer, Integer>> pairs = new ArrayList<>();

		for (int i = 0; i < encoded.size(); i++) {
			for (int j = i + 1; j < encoded.size(); j++) {
				Pair<Integer, Integer> p = new Pair<>();
				p.put(i, j);
				pairs.add(p);
			}
		}
		if (Utilities.VERBOSE) {
			System.out.println("num encoded seqs: " + encoded.size());
		}

		ThreadSafeAlignmentAdder adder = new ThreadSafeAlignmentAdder(pairs, safeElts, encoded);
		int maxThreads = Runtime.getRuntime().availableProcessors();
		CountDownLatch latch = new CountDownLatch(maxThreads);
		for (int i = 0; i < maxThreads; i++) {
			NotifyingThread t = new AlignmentGetter(adder, a_.clone(), f_.clone(), i, latch);
			t.start();
		}

		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} // Wait for countdown

		System.out.println("returned!!");

		return adder.getAlignments();

	}

	public void printAlignments(List<Alignment> als) {
		if (Utilities.VERBOSE) {
			System.out.println("printing alignments: ");
		}
		int idx = 1;
		for (Alignment al : als) {

			if (Utilities.TESTMODE) {
				if (idx > Utilities.MAX_ALIGNMENTS) {
					break;
				}
			}
			if (Utilities.TESTMODE) {
				double score = f_.getScore(al);
				if (score < 0) {
					System.out.println("Alignment " + idx + ":");

					idx++;

					al.printAlignment();
					System.out.println("temp score: " + al.tempScore_);
					System.out.println("score: " + f_.getScore(al));
				}
			} else {
				double score = f_.getScore(al);
				System.out.println("Alignment " + idx + ":");

				idx++;

				al.printAlignment();
				System.out.println("temp score: " + al.tempScore_);
				System.out.println("score: " + f_.getScore(al));
			}

		}
	}

	/**
	 * loads and encodes sequences into encoded list
	 */
	private List<Sequence> loadSequences() {
		List<Sequence> encoded = new ArrayList<>();
		List<Sequence> rawSeqs = sr_.read();
		if (Utilities.Randomized) {
			Collections.shuffle(rawSeqs);
		}
		int count = 0;

		for (Sequence toEncode : rawSeqs) {
			if (Utilities.TESTMODE) {
				if (Utilities.MAX_SEQS < count) {
					break;
				}
			}
			if (Utilities.VERBOSE) {
				System.out.println("getting " + e_.getDescription() + " - encoding for: " + toEncode.getID());
				System.out.println("unencoded: " + toEncode.toString());
				System.out.println();
			}
			Sequence enc = e_.encode(toEncode);
			encoded.add(enc);
			if (Utilities.VERBOSE) {
				System.out.println(e_.getDescription() + " encoding for: " + toEncode.getID() + " O= "
						+ f_.getOpenPenalty() + ", E=" + f_.getExtendPenalty());
				System.out.println("encoded: " + enc.toString());
				System.out.println();
			}
			count++;
		}

		return encoded;
	}

	public abstract class NotifyingThread extends Thread {
		private final Set<ThreadCompleteListener> listeners = new CopyOnWriteArraySet<ThreadCompleteListener>();

		public final void addListener(final ThreadCompleteListener listener) {
			listeners.add(listener);
		}

		public final void removeListener(final ThreadCompleteListener listener) {
			listeners.remove(listener);
		}

		private final void notifyListeners() {
			for (ThreadCompleteListener listener : listeners) {
				listener.notifyOfThreadComplete(this);
			}
		}

		@Override
		public final void run() {
			try {
				doRun();
			} finally {
				notifyListeners();
			}
		}

		public abstract void doRun();
	}

	public class AlignmentGetter extends NotifyingThread {
		ThreadSafeAlignmentAdder a_;
		Aligner al_;
		ScoreFunction f_;
		int idx_;
		CountDownLatch l_;

		public AlignmentGetter(ThreadSafeAlignmentAdder a, Aligner al, ScoreFunction f, int idx, CountDownLatch l) {
			a_ = a;
			al_ = al;
			f_ = f;
			idx_ = idx;
			l_ = l;
		}

		public String toString() {

			return "" + idx_;
		}

		@Override
		public void doRun() {
			Pair<Integer, Integer> job = null;
			while (a_.getNumJobs() > 0) {
				job = a_.take(job);
				if (job != null) {
					Pair<Sequence, Sequence> seqs = a_.getSequences(job);
					Alignment alignment = al_.getAlignment(seqs.getFirst(), seqs.getSecond(), f_);
					if (Utilities.VERBOSE) {
						if (Math.random() < 0.001) {
							System.out
									.println("thread: " + idx_ + " getting alignments for " + seqs.getFirst().getID());
							System.out.println("num left: " + a_.getNumJobs());
						}
					}
					if (alignment.length() > 1) {
						a_.addAlignment(alignment);
					}
				}

			}
			l_.countDown();

		}

	}

	public class ThreadSafeAlignmentAdder {

		private List<Pair<Integer, Integer>> list_;
		private List<Alignment> alignments_;
		private List<Sequence> toAlign_;
		private boolean[] safeElts_;
		private int capacity;

		public ThreadSafeAlignmentAdder(List<Pair<Integer, Integer>> list, boolean[] safeElts, List<Sequence> toAlign) {
			list_ = list;
			toAlign_ = toAlign;
			safeElts_ = safeElts;
			alignments_ = new ArrayList<>();
			shuffle();
		}

		public void shuffle() {
			Collections.shuffle(list_);
		}

		public int getNumJobs() {
			return list_.size();
		}

		public synchronized void add(Pair<Integer, Integer> element) {
			list_.add(element);
		}

		public synchronized Pair<Integer, Integer> take(Pair<Integer, Integer> lastTook) {
			if (lastTook != null) {
				safeElts_[lastTook.getFirst()] = false;
				safeElts_[lastTook.getSecond()] = false;
			}
			Pair<Integer, Integer> toTake = null;
			int numToCheck = Math.min(list_.size(), 10);
			for (int i = 1; i <= numToCheck; i++) {
				Pair<Integer, Integer> p = list_.get(list_.size() - i);
				if (!safeElts_[p.getFirst()] && !safeElts_[p.getFirst()]) {
					safeElts_[p.getFirst()] = true;
					safeElts_[p.getSecond()] = true;
					toTake = p;
					list_.remove(list_.size() - i);
					break;
				}
			}
			return toTake;

		}

		public List<Alignment> getAlignments() {
			return alignments_;
		}

		public synchronized void addAlignment(Alignment a) {
			alignments_.add(a);
		}

		public Pair<Sequence, Sequence> getSequences(Pair<Integer, Integer> toGet) {
			Pair<Sequence, Sequence> p = new Pair<>();
			p.put(toAlign_.get(toGet.getFirst()), toAlign_.get(toGet.getSecond()));
			return p;
		}
	}

}
