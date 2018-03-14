package portfolio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import io.sequence.NYSEDatReader;
import sequence.Element;
import sequence.Sequence;
import sequence.encoding.EncodingType;
import utils.Company;
import utils.StockDataObj;
import utils.Time;
import utils.TradingDay;
import utils.Utilities;

public class PortfolioBuilder {
	String filePath = "C:/Users/matt/Desktop/StockDat/prices-split-adjusted.csv";

	public double[] getReturns(String[] companies, Time start, Time end, double[] weights) {
		if (weights == null) {
			weights = new double[companies.length];
			for (int i = 0; i < weights.length; i++) {
				weights[i] = 1;
			}
		}
		HashSet<Company> compSet = new HashSet<>();
		for (int i = 0; i < companies.length; i++) {
			compSet.add(new Company(companies[i].trim()));
		}

		List<Sequence> seqs = this.readCompanies(new File(filePath), compSet, start, end, Utilities.close);
		for (int i = 0; i < seqs.size(); i++) {
			if (seqs.get(i).length() < seqs.get(0).length()) {
				seqs.remove(i);
			}
		}

		double[] returns = new double[seqs.get(0).length()];
		double buyPrice=0;
		Double quantities[] = new Double[seqs.size()];
		double[] initialPrices=new double[seqs.size()];

		for (int j = 0; j < seqs.size(); j++) {
			String comp=seqs.get(j).getData().getID();
			int idx=indexOf(companies,comp);
			quantities[idx] = (100.0 / seqs.get(idx).get(1).getVal())*weights[idx];
			double purchased=seqs.get(idx).get(1).getVal()*weights[idx];
			buyPrice+=Math.abs(purchased);
			initialPrices[idx]=seqs.get(idx).get(1).getVal();
			
		}

		for (int i = 1; i <= seqs.get(0).length(); i++) {
			double value = 0;
			for (int j = 0; j < seqs.size(); j++) {
				String comp=seqs.get(j).getData().getID();
				int idx=indexOf(companies,comp);
				value += (seqs.get(idx).get(i).getVal()-initialPrices[idx])*quantities[idx];
				
			}
			
			returns[i - 1] = (buyPrice+value) / buyPrice;
		}
		System.out.println(Utilities.mathematicaFormattedArray(returns));
	
		return returns;

	}
	
	private int indexOf(String[] arr, String toFind){
		for (int i = 0; i < arr.length; i++) {
			if(arr[i].trim().toUpperCase().contains(toFind.trim().toUpperCase())){
				return i;
			}
		}
		return -1;
	}

	public double[] getSandPReturns(Time start, Time end) {
		NYSEDatReader reader = new NYSEDatReader(new File(filePath), start, end, Utilities.close);
		List<Sequence> seqs = reader.read();
		double[] returns = new double[seqs.get(0).length()];

		for (int i = 0; i < seqs.size(); i++) {
			if (seqs.get(i).length() < seqs.get(0).length()) {
				seqs.remove(i);
			}
		}

		double buyPrice = 100.0 * seqs.size();
		Double quantities[] = new Double[seqs.size()];

		for (int j = 0; j < seqs.size(); j++) {

			quantities[j] = 100.0 / seqs.get(j).get(1).getVal();
		}

		for (int i = 1; i <= seqs.get(0).length(); i++) {
			double value = 0;
			for (int j = 0; j < seqs.size(); j++) {

				value += quantities[j] * seqs.get(j).get(i).getVal();

			}
			returns[i - 1] = value / buyPrice;
		}
		System.out.println(Utilities.mathematicaFormattedArray(returns));
		return returns;

	}

	public List<Sequence> readCompanies(File toRead, Collection<Company> companies, Time start, Time end,
			int attribute) {
		int date = 0, symbol = 1, open = 2, close = 3, low = 4, high = 5, volume = 6;
		HashMap<String, List<TradingDay>> series_ = new HashMap<>();

		if (Utilities.VERBOSE) {
			System.out.println("reading NYSE data");
		}

		try {
			Scanner s = new Scanner(toRead);
			// skip header
			s.nextLine();
			while (s.hasNextLine()) {
				String[] line = s.nextLine().split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
				String sym = line[symbol].trim().toUpperCase();
				if (companies.contains(new Company(sym))) {
					String[] dates = line[date].split("-");

					Time t = new Time(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]),
							Integer.parseInt(dates[2]));
					if (t.compareTo(start) >= 0 && t.compareTo(end) < 1) {
						TradingDay d = new TradingDay(t, line[open], line[close], line[low], line[high], line[volume]);

						if (!series_.containsKey(sym)) {
							series_.put(sym, new ArrayList<>());
						}
						series_.get(sym).add(d);
					}
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

			Sequence seq = new Sequence(new StockDataObj(new Company(sym), start, end, EncodingType.RawValues));
			int pos = 1;

			for (TradingDay d : td) {
				seq.add(new Element(d.get(attribute) + "", pos,
						"value of " + sym + " on date: " + d.getTime().toString(), d.get(attribute)));
				pos++;
			}
			sequences.add(seq);

		}
		return sequences;

	}

	public void writeAllReturns(Time start, Time end, int attribute) {
		int date = 0, symbol = 1, open = 2, close = 3, low = 4, high = 5, volume = 6;
		HashMap<String, List<TradingDay>> series_ = new HashMap<>();

		if (Utilities.VERBOSE) {
			System.out.println("reading NYSE data");
		}

		try {
			Scanner s = new Scanner(new File(filePath));
			// skip header
			s.nextLine();
			while (s.hasNextLine()) {
				String[] line = s.nextLine().split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
				String sym = line[symbol].trim().toUpperCase();

				String[] dates = line[date].split("-");

				Time t = new Time(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]), Integer.parseInt(dates[2]));
				if (t.compareTo(start) >= 0 && t.compareTo(end) < 1) {
					TradingDay d = new TradingDay(t, line[open], line[close], line[low], line[high], line[volume]);

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

		try {
			PrintWriter pw = new PrintWriter(new File("all_returns"));
			for (String sym : series_.keySet()) {
				List<TradingDay> td = series_.get(sym);
				Collections.sort(td);

				String toPrint = "{{" + sym + "},";
				for (TradingDay d : td) {
					double val = d.get(attribute);
					String day = d.getTime().toList();
					toPrint += "{{" + val + "}," + day + "},";

				}
				toPrint = toPrint.substring(0, toPrint.length() - 1);
				toPrint += "}";
				pw.println(toPrint);

			}
			pw.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		// Utilities.init();
		PortfolioBuilder b = new PortfolioBuilder();
		double[] weights = new double[] {0.0560918, 0.0781832, 0.0477162, 0.0483396, 0.0669497, 0.0434393, 
				0.0613567, 0.0428632, 0.0539882, 0.0306643, 0.0557866, 0.0566748, 
				0.0456425, 0.0379357, 0.0688688, 0.0570465, 0.044157, 0.0741687, 
				0.0301273};
		
		String[][] companies = new String[][]{{"K", "PPG", "ADI", "MAC", "VAR", "PVH", "GPS", "CME", "XL", "MMC"}};

		Time start = new Time(2013, 1, 1);
		Time end = new Time(2014, 1, 1);
		List<double[]> returns= new ArrayList<>();
		for (int i = 0; i < companies.length; i++) {
			returns.add(b.getReturns(companies[i], start, end,null));
		}
		try {
			PrintWriter pw= new PrintWriter(new File("randomPortfolio.txt"));
			for (double[] dat:returns) {
				pw.println(Utilities.mathematicaFormattedArray(dat));
			}
			pw.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// b.getSandPReturns(start, end);

	}

}
