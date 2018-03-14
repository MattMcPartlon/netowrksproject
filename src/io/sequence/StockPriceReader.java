package io.sequence;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sequence.Element;
import sequence.Sequence;
import sequence.encoding.EncodingType;
import utils.Company;
import utils.DataObj;
import utils.StockDataObj;
import utils.Time;
import utils.Utilities;

public class StockPriceReader extends SReader {

	public StockPriceReader(File toRead) {
		super(toRead);
		// TODO Auto-generated constructor stub
	}

	public List<Sequence> read() {
		List<Sequence> sequences = new ArrayList<Sequence>();
		try {
			Scanner s = new Scanner(toRead_);
			while (s.hasNextLine()) {

				String line = s.nextLine();
				// finds all entries of form {...}
				Pattern regex = Pattern.compile("\\{+(.*?)\\}");
				Matcher regexMatcher = regex.matcher(line);

				if (regexMatcher.find()) {
					// company that the stock prices correspond to
					String[] first = regexMatcher.group(1).trim().replaceAll('"' + "", "").split(":");
					String company = first[1];
					String exchange = first[0];

					// date of first recorded price
					regexMatcher.find();
					String[] start = regexMatcher.group(1).split(",");
					// date of last recorded price
					regexMatcher.find();
					String[] end = regexMatcher.group(1).split(",");

					for (int i = 0; i < end.length; i++) {
						start[i] = start[i].trim();
						end[i] = end[i].trim();
					}
					Time startTime = new Time(Integer.parseInt(start[0]), Integer.parseInt(start[1]),
							Integer.parseInt(start[2]));
					Time endTime = new Time(Integer.parseInt(end[0]), Integer.parseInt(end[1]),
							Integer.parseInt(end[2]));

					Company c = Utilities.findCompany(company);
					
					DataObj dat = new StockDataObj(c, startTime, endTime, EncodingType.RawPrices);

					Sequence seq = new Sequence(dat);
					int pos = 1;
					while (regexMatcher.find()) {// Finds Matching Pattern in
													// String
						String price = regexMatcher.group(1);
						double val = Double.parseDouble(price);
						Element e = new Element("elt", pos, "price of " + company + " on date?" + " + " + pos, val);
						seq.add(e);
						pos++;

					}
					sequences.add(seq);
				}
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sequences;
	}
	
	
	

	public static void main(String[] args) {
		Utilities.init();
		String fileString = "C:/Users/matt/Desktop/StockDat/djia4.seqs";
		File file = new File(fileString);
		StockPriceReader r = new StockPriceReader(file);
		r.read();

	}
}
