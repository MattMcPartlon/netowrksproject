package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class Utilities {

	public static boolean DEBUG = false;
	public static boolean TESTMODE = true;
	public static boolean VERBOSE = true;
	public static int MAX_ALIGNMENTS = 450;
	public static int open = 2, close = 3, low = 4, high = 5, volume = 6;

	public static List<Company> companies_ = new ArrayList<>();
	public static Sector[] sectors_ = new Sector[] { Sector.CapitalGoods, Sector.ConsumerDurables,
			Sector.ConsumerNonDurables, Sector.ConsumerServices, Sector.Energy, Sector.Financial, Sector.Healthcare,
			Sector.PublicUtilities, Sector.Miscellaneous, Sector.Technology, Sector.Transportation,
			Sector.BasicIndustries, Sector.NA };

	public static Exchange[] exchanges_ = new Exchange[] { Exchange.NASDAQ, Exchange.NYSE, Exchange.AMEX };

	public static void init() {
		loadCompanyData();
	}

	public static void loadCompanyData() {
		String prefix = "C:/Users/matt/Desktop/StockDat/";
		File f1 = new File(prefix + "NASDAQCompanyData.csv");
		File f2 = new File(prefix + "NYSECompanyData.csv");
		File f3 = new File(prefix + "AMEXCompanyData.csv");
		File[] dataFiles = new File[] { f1, f2, f3 };
		String[] exchangeNames = new String[] { "NASDAQ", "NYSE", "AMEX" };
		for (int i = 0; i < dataFiles.length; i++) {
			loadCompanyData(dataFiles[i], exchangeNames[i]);
		}
		Collections.sort(companies_);
	}

	public static void loadCompanyData(File data, String exchange) {

		try {
			Scanner s = new Scanner(data);
			Exchange ex = findExchange(exchange);
			// skip header
			s.nextLine();
			while (s.hasNextLine()) {

				String[] line = s.nextLine().split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
				if (DEBUG) {
					System.out.println("parsing: " + Arrays.toString(line));
				}
				int symbol = 0, name = 1, marketCap = 3, sector = 6, industry = 7;
				double mc = Double.parseDouble(line[marketCap]);
				Sector sec = findSector(line[sector]);
				Industry ind = new Industry(line[industry]);
				String companyName = line[name].replace('"', ' ');
				Company c = new Company(line[symbol], companyName, mc, sec, ind, ex);
				companies_.add(c);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	public static Exchange findExchange(String exchange) {
		exchange = exchange.trim();
		for (int i = 0; i < exchanges_.length; i++) {
			if (exchanges_[i].getName().equalsIgnoreCase(exchange)) {
				return exchanges_[i];
			}
		}

		throw new IllegalArgumentException("can't find exchange: " + exchange);
	}

	public static Sector findSector(String sector) {
		sector = sector.trim();
		for (int i = 0; i < sectors_.length; i++) {
			if (sectors_[i].getName().equalsIgnoreCase(sector)) {
				return sectors_[i];
			}
		}
		throw new IllegalArgumentException("can't find sector: " + sector);
	}

	public static Company findCompany(String symbol) {
		int idx = Collections.binarySearch(companies_, new Company(symbol));
		if (idx < 0) {
			if (VERBOSE) {
				System.out.println("cannot find company: " + symbol);
			}
			return null;
		}
		return companies_.get(idx);
	}

}
