package alignment;

import scoring.ScoreFunction;
import sequence.encoding.CorrelationWindowEncoder;
import sequence.encoding.Encoder;
import sequence.encoding.EncodingType;
import utils.Time;
import utils.Utilities;

public class AlignmentParameters {

	Aligner a_;
	ScoreFunction f_;
	Encoder e_;
	Time start_, end_;

	public AlignmentParameters(ScoreFunction f, Encoder e, Aligner a, Time start, Time end) {
		f_ = f;
		a_ = a;
		e_ = e;

	}
	
	public Time getStart(){
		return start_;
	}
	
	public Time getEnd(){
		return end_;
	}
	
	public Encoder getEncoder(){
		return e_;
	}
	
	public EncodingType getEncodingType(){
		return e_.getEncodingType();
	}
	
	public AlignmentType getAlignmentType(){
		return a_.getAlignmentType();
	}
	

	public Aligner getAligner() {
		return a_;
	}
	
	public ScoreFunction getScoreFunction(){
		return f_;
	}
	
	public int hashCode(){
		String s=f_.getExtendPenalty()+""+f_.getOpenPenalty()+""+e_.getDescription()+a_.toString();
		return s.hashCode();
	}

	public String toString() {
		return a_.toString() + "\n" + f_.toString() + "\n" + e_.toString();
	}
	
	public double getO(){
		return f_.getOpenPenalty();
	}
	
	public double getE(){
		return f_.getExtendPenalty();
	}
	
	public int getW(){
		if(e_ instanceof CorrelationWindowEncoder){
			CorrelationWindowEncoder e= (CorrelationWindowEncoder)e_;
			return e.getWindow();
		}
		return 1;
	
	}
	
	public String getParamList() {
		int w=getW();
		return a_.toString()+","+f_.getExtendPenalty()+", "+f_.getOpenPenalty()+", "+w+","+Utilities.MAX_LAG;
	}

}
