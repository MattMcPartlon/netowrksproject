package io.sequence;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.KeyStore.Entry.Attribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.rmi.CORBA.Util;

import sequence.Element;
import sequence.Sequence;
import sequence.encoding.EncodingType;
import utils.Company;
import utils.StockDataObj;
import utils.Time;
import utils.TradingDay;
import utils.Utilities;

public class NYSEDatReader extends SReader {
	public static int date = 0, symbol = 1, open = 2, close = 3, low = 4, high = 5, volume = 6;

	Time start_, end_;
	int attribute_;

	public NYSEDatReader(File toRead, Time start, Time end, int attribute) {
		super(toRead);
		// TODO Auto-generated constructor stub
		start_ = start;
		end_ = end;
		attribute_ = attribute;
	}

	@Override
	public List<Sequence> read() {
		int date = 0, symbol = 1, open = 2, close = 3, low = 4, high = 5, volume = 6;
		HashMap<String, List<TradingDay>> series_ = new HashMap<>();

		if (Utilities.VERBOSE) {
			System.out.println("reading NYSE data");
		}
		
		try {
			Scanner s = new Scanner(toRead_);
			// skip header
			s.nextLine();
			while (s.hasNextLine()) {
				String[] line = s.nextLine().split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
				String[] dates = line[date].split("-");

				Time t = new Time(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]), Integer.parseInt(dates[2]));
				if (t.compareTo(start_) >= 0 && t.compareTo(end_) < 1) {
					TradingDay d = new TradingDay(t, line[open], line[close], line[low], line[high], line[volume]);
					String sym = line[symbol].trim();
					if (!series_.containsKey(sym)) {
						series_.put(sym, new ArrayList<>());
					}
					series_.get(sym).add(d);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (Utilities.VERBOSE) {
			System.out.println("finished reading NYSE data");
		}
		List<Sequence> sequences = new ArrayList<>();

		for (String sym : series_.keySet()) {
			List<TradingDay> td = series_.get(sym);
			Collections.sort(td);
			Company c = Utilities.findCompany(sym);
			if (c != null) {
				Sequence seq = new Sequence(
						new StockDataObj(Utilities.findCompany(sym), start_, end_, EncodingType.RawValues));
				int pos = 1;
				for (TradingDay d : td) {
					seq.add(new Element(d.get(attribute_) + "", pos,
							"value of " + sym + " on date: " + d.getTime().toString(), d.get(attribute_)));
					pos++;
				}
				sequences.add(seq);
			}

		}
		return sequences;

	}

}