package io.sequence;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import sequence.Element;
import sequence.Sequence;
import sequence.encoding.EncodingType;
import utils.Company;
import utils.StockDataObj;
import utils.Time;
import utils.TradingDay;
import utils.Utilities;

public class SandPReader extends SReader {
	Time start_, end_;
	int attribute_;
	
	

	public SandPReader(File toRead, Time start, Time end, int attribute) {
		super(toRead);
		start_ = start;
		end_ = end;
		attribute_ = attribute;
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Sequence> read() {
		int date = 2, symbol = 3, name = 4, cp = 6;
		HashMap<String, List<TradingDay>> series_ = new HashMap<>();
		String current = "";
		try {
			Scanner s = new Scanner(toRead_);
			s.nextLine();
			while (s.hasNextLine()) {
				current = s.nextLine();
				String[] line = current.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
				String dates = line[date];
				int year = Integer.parseInt(dates.substring(0, 4));
				int month = Integer.parseInt(dates.substring(4, 6));
				int day = Integer.parseInt(dates.substring(6, 8));
				Time t = new Time(year, month, day);
				String sym = line[symbol].trim();
				//if (!sym.contains(".")) {
					if (t.compareTo(start_) >= 0 && t.compareTo(end_) < 1) {
						TradingDay d = new TradingDay(t, line[cp]);

						if (!series_.containsKey(sym)) {
							series_.put(sym, new ArrayList<>());
						}
						series_.get(sym).add(d);
					}
			//	}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(current);
			e.printStackTrace();
			System.exit(0);

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
				boolean toAdd = true;
				for (TradingDay d : td) {
					if (d.failed()) {
						toAdd = false;
					}
					seq.add(new Element(d.get(attribute_) + "", pos,
							"value of " + sym + " on date: " + d.getTime().toString(), d.get(attribute_)));
					pos++;
				}
				//System.out.println(seq.length());
				if (toAdd && seq.length() == 355&&!sym.contains(".")) {
					sequences.add(seq);
				}else{
					System.out.println(sym+": "+seq.length());
				}
			}

		}
		System.out.println("num sequences: " + sequences.size());
		

		return sequences;

	}

}
