package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.channels.NonWritableChannelException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import alignment.Aligner;
import alignment.Alignment;
import alignment.AlignmentGenerator;
import alignment.AlignmentParameters;
import alignment.NWGlobalAligner;
import alignment.NWSemiGlobalAligner;
import alignment.TrivialAligner;
import io.alignment.AlignmentReader;
import io.alignment.AlignmentWriter;
import io.sequence.AReader;
import io.sequence.NYSEDatReader;
import io.sequence.SReader;
import io.sequence.SandPReader;
import io.sequence.StockPriceReader;
import network.ThresholdNetwork;
import scoring.CorrelationScoreFunction;
import scoring.ScoreFunction;
import sequence.encoding.CorrelationWindowEncoder;
import sequence.encoding.DailyPriceChangeEncoder;
import sequence.encoding.Encoder;
import stats.AlignmentStatsIO;
import stats.CorrelationAlignmentStats;
import stats.NetworkStatsIO;
import utils.Company;
import utils.Pair;
import utils.Time;
import utils.Utilities;

public class Main {

	Time t1, t2;
	File priceFile;
	double[] open, extend;
	int[] window;
	

	public void init() {
		Utilities.init();
		String fileString = "C:/Users/matt/Desktop/StockDat/s_and_p_data_2006_2015.csv";
		priceFile = new File(fileString);
		int startMonth = 1, startYear = 2007, startDay = 1, endYear = 2008, endDay = 1, endMonth = 6;
		t1 = new Time(startYear, startMonth, startDay);
		t2 = new Time(endYear, endMonth, endDay);
		window = new int[] { 20, 50 };
		open = new double[] {0.5,1.0,2.0,4.0,8.0};
		extend = new double[]  {1.0,2.0,4.0};
	

	}

	public void doMain1() {
		init();
		double O, E;
		int w;

		for (int k = 0; k < window.length; k++) {
			for (int i = 0; i < open.length; i++) {
				for (int j = 0; j < extend.length; j++) {
					System.out.println("k: " + k);
					System.out.println("i: " + i);
					System.out.println("j: " + j);
					O = open[i];
					E = extend[j];
					w = window[k];
					String suffix = getSuffix(O, E, w);
					SReader sr = new SandPReader(priceFile, t1, t2, Utilities.close);
					

					ScoreFunction f = new CorrelationScoreFunction(O, E);
					Aligner a = new NWSemiGlobalAligner();
					Encoder e = new CorrelationWindowEncoder(w);
					AlignmentGenerator g = new AlignmentGenerator(sr, e, a, f, new CorrelationAlignmentStats());
					List<Alignment> alignments = g.getAlignments();
					
					System.out.println("num alignments: "+alignments.size());
					
					
					AlignmentStatsIO alignmentStats = new AlignmentStatsIO(alignments, f);
					AlignmentWriter aW = new AlignmentWriter();
					g.printAlignments(alignments);

					alignmentStats.saveAlignmentStats(new File("AlignmentStats_" + suffix));
					ThresholdNetwork network = new ThresholdNetwork(0);
					network.build(alignments, f);
					NetworkStatsIO netStats = new NetworkStatsIO(network);
					netStats.saveNetwork(new File("network_" + suffix));
					netStats.saveOffsets(new File("offsets_" + suffix));
					netStats.saveSectorIDs(new File("sectors_" + suffix));
					netStats.saveVertexDat(new File("vertexData_" + suffix));
					netStats.saveGaps(new File("gapMatrix_" + suffix));
					aW.writeAlignments(alignments, new File("alignments_" + suffix),
							"alignments written for s and p 500");
				}
			}
		}

	}

	public String getSuffix(double O, double E, int w) {
		return "recession_time_CorrelationWindow_Encoder_NWSemiGlobal_Powers_of_2_New_Data_S_AND_P_" + Utilities.MAX_SEQS + "lag_" + Utilities.MAX_LAG
				+ "_NWSemiGlobalAligner_CorrelationScoreFunction_" + t1.toString() + "_to_" + t2.toString() + "O_" + O
				+ "_E_" + E + "W_" + w;

	}

	public void doMain2() {
		init();
		double O, E;
		int w;
		List<AlignmentParameters> paramList= new ArrayList<>();
		for (int k = 0; k < window.length; k++) {
			for (int i = 0; i < open.length; i++) {
				for (int j = 0; j < extend.length; j++) {
					System.out.println("i: " + i);
					System.out.println("j: " + j);
					O = open[i];
					E = extend[j];
					w = window[k];
					String suffix = getSuffix(O, E, w);

					ScoreFunction f = new CorrelationScoreFunction(O, E);
					Aligner a = new NWSemiGlobalAligner();
					Encoder e = new CorrelationWindowEncoder(w);
					AlignmentParameters params = new AlignmentParameters(f, e, a, t1, t2);
					paramList.add(params);
				}
			}
		}

		// for all of the alignments, we want to figure out som similarity
		// measures between different parameter values
		try {
			
			
			PrintWriter pw= new PrintWriter(new FileOutputStream(new File("alignment_similarities_recession_time")),true);
			CorrelationAlignmentStats stats=new CorrelationAlignmentStats();
			for(int i=0;i<paramList.size();i++){
				AlignmentParameters pi=paramList.get(i);
				String suffixi=getSuffix(pi.getO(), pi.getE(), pi.getW());
				AlignmentReader readeri= new AlignmentReader();
				HashMap<Company, HashMap<Company, Alignment>> alignmentsi = readeri
						.readAlignments(new File("alignments_" + suffixi), pi);
				
				int numAls=0;
				for(Company key: alignmentsi.keySet()){
					numAls+=alignmentsi.get(key).keySet().size();
				}
				System.out.println("num alignments produced: "+numAls);
				
				for(int j=i+1;j<paramList.size();j++){
					System.out.println("getting scores for i: "+i+", j: "+j);
					AlignmentParameters pj=paramList.get(j);
					String suffixj=getSuffix(pj.getO(), pj.getE(), pj.getW());
					AlignmentReader readerj= new AlignmentReader();
					System.gc();
					HashMap<Company, HashMap<Company, Alignment>> alignmentsj = readerj
							.readAlignments(new File("alignments_" + suffixj), pj);
					
					double[] arr1=stats.getAvgPairwiseSimilarity(alignmentsi, alignmentsj);
					double[] arr2=stats.getAvgPointwiseSimilarity(alignmentsi, alignmentsj);
					String toPrint="{{"+pi.getParamList()+"},{"+pj.getParamList()+"},";
					toPrint+=Utilities.mathematicaFormattedArray(arr1)+","+Utilities.mathematicaFormattedArray(arr2)+"}";
					pw.println(toPrint);
				}
				pw.flush();
				
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}



	public static void main(String[] args) {

		Main main = new Main();
		main.doMain1();
		main.doMain2();
	}

}
