package sequence.encoding;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

import sequence.Element;
import sequence.Sequence;
import utils.StockDataObj;

public class DailyPriceChangeEncoder extends CorrelationEncoder {

	double granularity_ = 0.1;
	double[] zScores_ = new double[41];

	public DailyPriceChangeEncoder() {
		loadZscores();
	}

	public Sequence encode(Sequence s) {

		System.out.println(Arrays.toString(zScores_));
		Sequence seq = new Sequence(new StockDataObj((StockDataObj) s.getData(), getEncodingType()));
		double sd = Math.pow(getVariance(1, s.length(), s), .5);

		for (int j = 1; j <= s.length() - 1; j++) {

			double val = (s.getVal(j + 1) - s.getVal(j)) / sd;

			Element e = new Element(getLabel(val), j, "simple correlation encoded elt", this.getVal(val));

			seq.add(e);

		}
		return seq;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "daily price change encoder";
	}

	private void loadZscores() {
		File f = new File("D:/Recovery/Eclipse/eclipse/readme/workspace/NetworksFinalProject/zscoretable.txt");
		int idx = 0;
		try {
			Scanner s = new Scanner(f);

			while (s.hasNextLine()) {
				String[] line = s.nextLine().trim().split(" ");
				if (line.length > 1) {
					double val = Double.parseDouble(line[1]);
					zScores_[idx] = val;
					idx++;
				}

			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (idx != 40) {
			throw new IllegalStateException();
		}
		zScores_[40] = zScores_[39];

	}

	private double getVal(double sd) {

		int idx = (int) Math.min(Math.abs(Math.floor(sd / granularity_)), 40);
		double score = Math.signum(sd) * (1 / (1 - zScores_[idx]));
		return score;

	}

	@Override
	public EncodingType getEncodingType() {
		// TODO Auto-generated method stub
		return EncodingType.DailyPriceChangeEncoder;
	}
}
