package utils;

import alignment.Alignment;

public class DataObjT1{
		
		double count_, gep_, set_, gop_, avgSimilarity1_, avgSimilarity2_, scoreMatType_,scoreMatSuffix_, 
		avgLengthDiffABS_, avgAlignLengthDiff_, avgNumGaps_, alignType_, avgScoreOurs_, avgScoreRef_, avgScoreDif_, alignmentLengthRatio1_, alignmentLengthRatio2_;
	
		public DataObjT1(double gep,double gop, double scoreMatType, double scoreMatSuffix, double alignType, double set){
			gep_=gep;
			gop_=gop;
			scoreMatType_=scoreMatType;
			scoreMatSuffix_=scoreMatSuffix;
			alignType_=alignType;
			set_=set;
			
		}
		
		
		public void addToAverageScore(double similarity, double refLength, double alignLength, Alignment ours, double alignScoreOurs, double alignScoreRef, double s1Length, double s2Length){
			avgSimilarity1_+= (similarity/Math.max(alignLength, refLength));
			avgSimilarity2_+= (similarity/Math.min(alignLength, refLength));
			avgLengthDiffABS_+= (Math.abs(alignLength-refLength));
			avgAlignLengthDiff_+=(alignLength-refLength);
			avgNumGaps_+=(ours.getNumGaps());
			avgScoreOurs_+=alignScoreOurs;
			avgScoreRef_+=alignScoreRef;
			avgScoreDif_+=Math.abs(alignScoreRef-alignScoreOurs);
			alignmentLengthRatio1_+= (alignLength/Math.min(s1Length,s2Length));
			alignmentLengthRatio2_+= (alignLength/Math.max(s1Length,s2Length));
			count_+=1;
//			System.out.println("alignment ratio 1: "+alignLength/Math.min(s1Length,s2Length));
//			System.out.println("alignment length: "+alignLength);
//			System.out.println("s1length "+s1Length);
//			System.out.println("similarity: "+similarity);
//			System.out.println("total similarity so far: "+avgSimilarity1_);
//			System.out.println("*****************************");
//			
		}
		
		public void printInfo(){
	
			System.out.println("average Similarity: "+avgSimilarity1_);
		}

		public String getHeader(){
			String header="GEP,GOP,Score_Mat_Type,Score_Mat_Suffix,Align_Type,Set,";
			header+="Avg_Similarity_Min,";
			header+="Avg_Similarity_Max,";
			header+="Avg_Length_Diff_Abs,";
			header+="Avg_Length_Diff,";
			header+="Avg_Num_Gaps,";
			header+="Avg_Score_Ours,";
			header+="Avg_Score_Ref,";
			header+="Avg_Score_Diff_ABS,";
			header+="alignment_ratio_1,";
			header+="alignment_ratio_2,";
			header+="count,";
			header+="gop";
			return header;
		}

		@Override
		public String toString() {
			
			String s= gep_+","+gop_+","+scoreMatType_+","+scoreMatSuffix_+","+alignType_+","+set_+",";
			s+=avgSimilarity1_/count_;
			s+=",";
			s+=avgSimilarity2_/count_;
			s+=",";
			s+=avgLengthDiffABS_/count_;
			s+=",";
			s+=avgAlignLengthDiff_/count_;
			s+=",";
			s+=avgNumGaps_/count_;
			s+=",";
			s+=avgScoreOurs_/count_;
			s+=",";
			s+=avgScoreRef_/count_;
			s+=",";
			s+=avgScoreDif_/count_;
			s+=",";
			s+=alignmentLengthRatio1_/count_;
			s+=",";
			s+=alignmentLengthRatio2_/count_;
			s+=",";
			s+=count_;
			s+=",";
			s+=gop_;
			//add alignment ratio stuff
			return s;
			
		}
		
		
		
	}