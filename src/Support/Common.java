package Support;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import Support.parser.GenBank;
import Support.parser.Sequence;

public class Common{
	//colors for tables and graphics
	static Color pheColor=new Color(197,217,241);
	static Color serColor=Color.YELLOW;
	static Color tyrColor=new Color(0,112,192);
	static Color cysColor=new Color(247,150,70);
	static Color leuColor=new Color(234,241,221);
	static Color stopColor=Color.WHITE;
	static Color trpColor=new Color(255,0,255);
	static Color proColor=new Color(146,208,80);
	static Color hisColor=new Color(221,217,195);
	static Color glnColor=new Color(250,192,144);
	static Color argColor=new Color(216,216,216);
	static Color ileColor=new Color(242,221,220);
	static Color thrColor=new Color(0,176,80);
	static Color asnColor=new Color(178,161,199);
	static Color lysColor=new Color(148,139,84);
	static Color metColor=Color.RED;
	static Color valColor=new Color(255,192,0);
	static Color alaColor=new Color(0,176,240);
	static Color aspColor=new Color(102,255,51);
	static Color gluColor=new Color(0,255,255);
	static Color glyColor=new Color(182,221,232);

	public static String aaToSequence(String AA){	//generate coding sequence from given amino acids (the first aa among its sibling codons is chosen)
		String sequence="";
		for(int i=0;i<AA.length();i++){
			sequence+=aaToSequence(AA.charAt(i));
		}
		return sequence;
	}

	public static String aaToSequence(char AA){	//transcribe AA to sequence (take the first sibling codons)
		char aa=Character.toUpperCase(AA);
		if(aa=='A'){	//alanine
			return "GCT";
		}else if(aa=='V'){	//valine
			return "GTT";
		}else if(aa=='F'){	//phenylalanine
			return "TTT";
		}else if(aa=='I'){	//isoleucine
			return "ATT";
		}else if(aa=='L'){	//leucine
			return "TTA";
		}else if(aa=='G'){	//glycine
			return "GGT";
		}else if(aa=='P'){	//proline
			return "CCT";
		}else if(aa=='M'){	//methionine
			return "ATG";
		}else if(aa=='D'){	//aspartic acid
			return "GAT";
		}else if(aa=='E'){	//glutamic acid
			return "GAA";
		}else if(aa=='K'){	//lysine
			return "AAA";
		}else if(aa=='R'){	//arginine
			return "CGT";
		}else if(aa=='S'){	//serine
			return "AGT";
		}else if(aa=='T'){	//threonine
			return "ACT";
		}else if(aa=='Y'){	//tyrosine
			return "TAT";
		}else if(aa=='C'){	//cystine
			return "TGT";
		}else if(aa=='N'){	//asparagine
			return "AAT";
		}else if(aa=='Q'){	//glutamine
			return "CAA";
		}else if(aa=='H'){	//histidine
			return "CAT";
		}else if(aa=='W'){	//tryptophan
			return "TGG";
		}else if(aa=='*'){	//STOP
			return "TAA";
		}else{	//unknown
			return "XXX";
		}
	}

	public static char sequenceToAA(String codon){	//translate sequence to AA
		String theCodon=codon.toUpperCase();
		if(theCodon.equals("TTT")||theCodon.equals("TTC") || theCodon.equals("UUU")||theCodon.equals("UUC")){	//Phe
			return 'F';
		}else if(theCodon.equals("TTA")||theCodon.equals("TTG")||theCodon.equals("CTT")||theCodon.equals("CTC")||theCodon.equals("CTA")||theCodon.equals("CTG") || theCodon.equals("UUA")||theCodon.equals("UUG")||theCodon.equals("CUU")||theCodon.equals("CUC")||theCodon.equals("CUA")||theCodon.equals("CUG")){	//Leu
			return 'L';
		}else if(theCodon.equals("ATT")||theCodon.equals("ATC")||theCodon.equals("ATA") || theCodon.equals("AUU")||theCodon.equals("AUC")||theCodon.equals("AUA")){	//ile
			return 'I';
		}else if(theCodon.equals("ATG") || theCodon.equals("AUG")){	//Met
			return 'M';
		}else if(theCodon.equals("GTT")||theCodon.equals("GTC")||theCodon.equals("GTA")||theCodon.equals("GTG") || theCodon.equals("GUU")||theCodon.equals("GUC")||theCodon.equals("GUA")||theCodon.equals("GUG")){	//Val
			return 'V';
		}else if(theCodon.equals("TCT")||theCodon.equals("TCC")||theCodon.equals("TCA")||theCodon.equals("TCG")||theCodon.equals("AGT")||theCodon.equals("AGC") || theCodon.equals("UCU")||theCodon.equals("UCC")||theCodon.equals("UCA")||theCodon.equals("UCG")||theCodon.equals("AGU")){	//Ser
			return 'S';
		}else if(theCodon.equals("CCT")||theCodon.equals("CCC")||theCodon.equals("CCA")||theCodon.equals("CCG") || theCodon.equals("CCU")){	//Pro
			return 'P';
		}else if(theCodon.equals("ACT")||theCodon.equals("ACC")||theCodon.equals("ACA")||theCodon.equals("ACG") || theCodon.equals("ACU")){	//Thr
			return 'T';
		}else if(theCodon.equals("GCT")||theCodon.equals("GCC")||theCodon.equals("GCA")||theCodon.equals("GCG") || theCodon.equals("GCU")){	//Ala
			return 'A';
		}else if(theCodon.equals("TAT")||theCodon.equals("TAC") || theCodon.equals("UAU")||theCodon.equals("UAC")){	//Tyr
			return 'Y';
		}else if(theCodon.equals("TAA")||theCodon.equals("TAG")||theCodon.equals("TGA") || theCodon.equals("UAA")||theCodon.equals("UAG")||theCodon.equals("UGA")){	//STOP
			return '*';
		}else if(theCodon.equals("CAT")||theCodon.equals("CAC") || theCodon.equals("CAU")){	//His
			return 'H';
		}else if(theCodon.equals("CAA")||theCodon.equals("CAG")){	//Gln
			return 'Q';
		}else if(theCodon.equals("AAT")||theCodon.equals("AAC") || theCodon.equals("AAU")){	//Asn
			return 'N';
		}else if(theCodon.equals("AAA")||theCodon.equals("AAG")){	//Lys
			return 'K';
		}else if(theCodon.equals("GAT")||theCodon.equals("GAC") || theCodon.equals("GAU")){	//Asp
			return 'D';
		}else if(theCodon.equals("GAA")||theCodon.equals("GAG")){	//Glu
			return 'E';
		}else if(theCodon.equals("TGT")||theCodon.equals("TGC") || theCodon.equals("UGU")||theCodon.equals("UGC")){	//Cys
			return 'C';
		}else if(theCodon.equals("TGG") || theCodon.equals("UGG")){	//Trp
			return 'W';
		}else if(theCodon.equals("CGT")||theCodon.equals("CGC")||theCodon.equals("CGA")||theCodon.equals("CGG")||theCodon.equals("AGA")||theCodon.equals("AGG") || theCodon.equals("CGU")){	//Arg
			return 'R';
		}else if(theCodon.equals("GGT")||theCodon.equals("GGC")||theCodon.equals("GGA")||theCodon.equals("GGG") || theCodon.equals("GGU")){	//Gly
			return 'G';
		}else{	//unknown
			return 'X';
		}
	}

	public static String getComplementarySequence(String na){
		String result="";
		for(int i=0;i<na.length();i++){
			if(na.charAt(i)=='A'||na.charAt(i)=='a')result+="T";
			else if(na.charAt(i)=='T'||na.charAt(i)=='t')result+="A";
			else if(na.charAt(i)=='G'||na.charAt(i)=='g')result+="C";
			else if(na.charAt(i)=='C'||na.charAt(i)=='c')result+="G";
			else if(na.charAt(i)=='U'||na.charAt(i)=='u')result+="A";
			else result+="X";	//unknow sequence
		}
		return result;
	}

	public static ArrayList<Sequence> makeSequence(GenBank genBank){	//method for converting genBank into sequences that can be graphitized in operation panel
		ArrayList<Sequence> seqList=new ArrayList<Sequence>();

		//make sequences from sequences of genBank
		for(int i=0;i<genBank.sequences.size();i++){
			Sequence originalSeq=genBank.sequences.get(i);

			//skip excessive features
			if(originalSeq.type.equals("intron")||originalSeq.type.equals("exon")||originalSeq.type.equals("misc_feature")||originalSeq.type.equals("misc_difference")||originalSeq.type.equals("old_sequence")){
				continue;
			}

			for(int j=0;j<originalSeq.interval.size();j++){	//create a sequence for every interval
				Sequence copySeq=new Sequence(originalSeq);
				copySeq.interval=new ArrayList<int[]>();//reset the interval to this one only
				int[] newInt={originalSeq.interval.get(j)[0],originalSeq.interval.get(j)[1]};
				copySeq.interval.add(newInt);
				int start=originalSeq.interval.get(j)[0];
				int stop=originalSeq.interval.get(j)[1];
				int temp=-1;
				if(copySeq.isComplementary()){
					temp=start;
					start=stop;
					stop=temp;
				}
				copySeq.codingSequence=genBank.completeSequence.substring(start-1,stop);	//manually set the codingSequence of copySeq
				copySeq.AA=null;

				seqList.add(copySeq);
			}
		}

		//sort the seqList by position
		Collections.sort(seqList,new Comparator<Sequence>(){
			public int compare(Sequence sequenceOne,Sequence sequenceTwo){
				int startOne=sequenceOne.interval.get(0)[0];
				int stopOne=sequenceOne.interval.get(0)[1];
				if(sequenceOne.isComplementary()){
					int temp=startOne;
					startOne=stopOne;
					stopOne=temp;
				}
				int startTwo=sequenceTwo.interval.get(0)[0];
				int stopTwo=sequenceTwo.interval.get(0)[1];
				if(sequenceTwo.isComplementary()){
					int temp=startTwo;
					startTwo=stopTwo;
					stopTwo=temp;
				}

				if(startOne==startTwo){
					return 0;
				}else if(startOne>startTwo){
					return 1;
				}else{
					return -1;
				}
			}
		});

		//create the introns and slit them in
		for(int i=0;i<seqList.size();i++){
			Sequence theSeq=seqList.get(i);
			int start=theSeq.interval.get(0)[0];
			int stop=theSeq.interval.get(0)[1];
			if(theSeq.isComplementary()){
				int temp=start;
				start=stop;
				stop=temp;
			}
			int nextStart=-1;
			if(i!=seqList.size()-1){	//if it is not the last sequence
				nextStart=seqList.get(i+1).interval.get(0)[0];
				if(seqList.get(i+1).isComplementary()){
					nextStart=seqList.get(i+1).interval.get(0)[1];
				}
			}else{	//if it is the last sequence
				if(seqList.get(i).interval.get(0)[1]<genBank.completeSequence.length()&&seqList.get(i).interval.get(0)[0]<genBank.completeSequence.length()){	//if there is still one more intron
					nextStart=genBank.completeSequence.length()+1;
				}
			}
			if((nextStart-stop)==1){	//avoid repetition
				continue;
			}

			//if this is the first sequence in the genome
			if(i==0){
				//the first intron
				int distance=start-1;
				if(distance>0){
					Sequence newSeq=new Sequence(theSeq);
					newSeq.interval=new ArrayList<int[]>();	//reset interval
					int[] newInt={1,distance};
					newSeq.interval.add(newInt);
					newSeq.codingSequence=genBank.completeSequence.substring(newInt[0]-1,newInt[1]);
					newSeq.type="intron";	//this is an intron
					newSeq.gene=null;
					newSeq.AA=null;
					seqList.add(0,newSeq);
				}

				//the second intron
				int somethingBehind=genBank.seqLength-stop;
				if(somethingBehind>0){
					Sequence theNewSeq=new Sequence(theSeq);
					theNewSeq.interval=new ArrayList<int[]>();	//reset interval
					int[] theNewInt={stop+1,nextStart-1};
					theNewSeq.interval.add(theNewInt);
					theNewSeq.codingSequence=genBank.completeSequence.substring(theNewInt[0]-1,theNewInt[1]);
					theNewSeq.type="intron";	//this is an intron
					theNewSeq.gene=null;
					theNewSeq.AA=null;
					if(distance>0){
						seqList.add(2,theNewSeq);
					}else{
						seqList.add(1,theNewSeq);
					}
				}

				continue;
			}

			if(nextStart!=-1){	//if there is still more intron
				//for the other sequences, including the last one
				Sequence newSeq=new Sequence(theSeq);
				newSeq.interval=new ArrayList<int[]>();	//reset interval
				int[] newInt={stop+1,nextStart-1};
				newSeq.interval.add(newInt);
				newSeq.codingSequence=genBank.completeSequence.substring(newInt[0]-1,newInt[1]);
				newSeq.type="intron";	//this is an intron
				newSeq.gene=null;
				newSeq.AA=null;
				seqList.add(i+1,newSeq);//the intron to next in the list
			}
		}

		return seqList;
	}
	public static ArrayList<Sequence> makeSequence(Sequence originalSeq){	//convert sequence to graphic friendly sequence
		ArrayList<Sequence> seqList=new ArrayList<Sequence>();
		for(int j=0;j<originalSeq.interval.size();j++){	//create a sequence for every interval
			Sequence copySeq=new Sequence(originalSeq);
			copySeq.interval=new ArrayList<int[]>();//reset the interval to this one only
			int[] newInt={originalSeq.interval.get(j)[0],originalSeq.interval.get(j)[1]};
			copySeq.interval.add(newInt);
			copySeq.codingSequence=originalSeq.genBank.completeSequence.substring(originalSeq.interval.get(j)[0]-1,originalSeq.interval.get(j)[1]);	//manually set the codingSequence of copySeq
			copySeq.AA=null;

			seqList.add(copySeq);
		}

		return seqList;
	}

	public static Color getAAColor(String theCodon){
		if(theCodon.equals("TTT")||theCodon.equals("TTC") || theCodon.equals("UUU")||theCodon.equals("UUC")){	//Phe
			return pheColor;
		}else if(theCodon.equals("TTA")||theCodon.equals("TTG")||theCodon.equals("CTT")||theCodon.equals("CTC")||theCodon.equals("CTA")||theCodon.equals("CTG") || theCodon.equals("UUA")||theCodon.equals("UUG")||theCodon.equals("CUU")||theCodon.equals("CUC")||theCodon.equals("CUA")||theCodon.equals("CUG")){	//Leu
			return leuColor;
		}else if(theCodon.equals("ATT")||theCodon.equals("ATC")||theCodon.equals("ATA") || theCodon.equals("AUU")||theCodon.equals("AUC")||theCodon.equals("AUA")){	//ile
			return ileColor;
		}else if(theCodon.equals("ATG") || theCodon.equals("AUG")){	//Met
			return metColor;
		}else if(theCodon.equals("GTT")||theCodon.equals("GTC")||theCodon.equals("GTA")||theCodon.equals("GTG") || theCodon.equals("GUU")||theCodon.equals("GUC")||theCodon.equals("GUA")||theCodon.equals("GUG")){	//Val
			return valColor;
		}else if(theCodon.equals("TCT")||theCodon.equals("TCC")||theCodon.equals("TCA")||theCodon.equals("TCG")||theCodon.equals("AGT")||theCodon.equals("AGC") || theCodon.equals("UCU")||theCodon.equals("UCC")||theCodon.equals("UCA")||theCodon.equals("UCG")||theCodon.equals("AGU")){	//Ser
			return serColor;
		}else if(theCodon.equals("CCT")||theCodon.equals("CCC")||theCodon.equals("CCA")||theCodon.equals("CCG") || theCodon.equals("CCU")){	//Pro
			return proColor;
		}else if(theCodon.equals("ACT")||theCodon.equals("ACC")||theCodon.equals("ACA")||theCodon.equals("ACG") || theCodon.equals("ACU")){	//Thr
			return thrColor;
		}else if(theCodon.equals("GCT")||theCodon.equals("GCC")||theCodon.equals("GCA")||theCodon.equals("GCG") || theCodon.equals("GCU")){	//Ala
			return alaColor;
		}else if(theCodon.equals("TAT")||theCodon.equals("TAC") || theCodon.equals("UAU")||theCodon.equals("UAC")){	//Tyr
			return tyrColor;
		}else if(theCodon.equals("TAA")||theCodon.equals("TAG")||theCodon.equals("TGA") || theCodon.equals("UAA")||theCodon.equals("UAG")||theCodon.equals("UGA")){	//STOP
			return stopColor;
		}else if(theCodon.equals("CAT")||theCodon.equals("CAC") || theCodon.equals("CAU")){	//His
			return hisColor;
		}else if(theCodon.equals("CAA")||theCodon.equals("CAG")){	//Gln
			return glnColor;
		}else if(theCodon.equals("AAT")||theCodon.equals("AAC") || theCodon.equals("AAU")){	//Asn
			return asnColor;
		}else if(theCodon.equals("AAA")||theCodon.equals("AAG")){	//Lys
			return lysColor;
		}else if(theCodon.equals("GAT")||theCodon.equals("GAC") || theCodon.equals("GAU")){	//Asp
			return aspColor;
		}else if(theCodon.equals("GAA")||theCodon.equals("GAG")){	//Glu
			return gluColor;
		}else if(theCodon.equals("TGT")||theCodon.equals("TGC") || theCodon.equals("UGU")||theCodon.equals("UGC")){	//Cys
			return cysColor;
		}else if(theCodon.equals("TGG") || theCodon.equals("UGG")){	//Trp
			return trpColor;
		}else if(theCodon.equals("CGT")||theCodon.equals("CGC")||theCodon.equals("CGA")||theCodon.equals("CGG")||theCodon.equals("AGA")||theCodon.equals("AGG") || theCodon.equals("CGU")){	//Arg
			return argColor;
		}else if(theCodon.equals("GGT")||theCodon.equals("GGC")||theCodon.equals("GGA")||theCodon.equals("GGG") || theCodon.equals("GGU")){	//Gly
			return glyColor;
		}else{	//unrecognized
			return Color.BLACK;
		}
	}

	public static String getAAShortForm(String theCodon){
		if(theCodon.equals("TTT")||theCodon.equals("TTC") || theCodon.equals("UUU")||theCodon.equals("UUC")){	//Phe
			return "Phe";
		}else if(theCodon.equals("TTA")||theCodon.equals("TTG")||theCodon.equals("CTT")||theCodon.equals("CTC")||theCodon.equals("CTA")||theCodon.equals("CTG") || theCodon.equals("UUA")||theCodon.equals("UUG")||theCodon.equals("CUU")||theCodon.equals("CUC")||theCodon.equals("CUA")||theCodon.equals("CUG")){	//Leu
			return "Leu";
		}else if(theCodon.equals("ATT")||theCodon.equals("ATC")||theCodon.equals("ATA") || theCodon.equals("AUU")||theCodon.equals("AUC")||theCodon.equals("AUA")){	//ile
			return "ile";
		}else if(theCodon.equals("ATG") || theCodon.equals("AUG")){	//Met
			return "Met";
		}else if(theCodon.equals("GTT")||theCodon.equals("GTC")||theCodon.equals("GTA")||theCodon.equals("GTG") || theCodon.equals("GUU")||theCodon.equals("GUC")||theCodon.equals("GUA")||theCodon.equals("GUG")){	//Val
			return "Val";
		}else if(theCodon.equals("TCT")||theCodon.equals("TCC")||theCodon.equals("TCA")||theCodon.equals("TCG")||theCodon.equals("AGT")||theCodon.equals("AGC") || theCodon.equals("UCU")||theCodon.equals("UCC")||theCodon.equals("UCA")||theCodon.equals("UCG")||theCodon.equals("AGU")){	//Ser
			return "Ser";
		}else if(theCodon.equals("CCT")||theCodon.equals("CCC")||theCodon.equals("CCA")||theCodon.equals("CCG") || theCodon.equals("CCU")){	//Pro
			return "Pro";
		}else if(theCodon.equals("ACT")||theCodon.equals("ACC")||theCodon.equals("ACA")||theCodon.equals("ACG") || theCodon.equals("ACU")){	//Thr
			return "Thr";
		}else if(theCodon.equals("GCT")||theCodon.equals("GCC")||theCodon.equals("GCA")||theCodon.equals("GCG") || theCodon.equals("GCU")){	//Ala
			return "Ala";
		}else if(theCodon.equals("TAT")||theCodon.equals("TAC") || theCodon.equals("UAU")||theCodon.equals("UAC")){	//Tyr
			return "Tyr";
		}else if(theCodon.equals("TAA")||theCodon.equals("TAG")||theCodon.equals("TGA") || theCodon.equals("UAA")||theCodon.equals("UAG")||theCodon.equals("UGA")){	//STOP
			return "STOP";
		}else if(theCodon.equals("CAT")||theCodon.equals("CAC") || theCodon.equals("CAU")){	//His
			return "His";
		}else if(theCodon.equals("CAA")||theCodon.equals("CAG")){	//Gln
			return "Gln";
		}else if(theCodon.equals("AAT")||theCodon.equals("AAC") || theCodon.equals("AAU")){	//Asn
			return "Asn";
		}else if(theCodon.equals("AAA")||theCodon.equals("AAG")){	//Lys
			return "Lys";
		}else if(theCodon.equals("GAT")||theCodon.equals("GAC") || theCodon.equals("GAU")){	//Asp
			return "Asp";
		}else if(theCodon.equals("GAA")||theCodon.equals("GAG")){	//Glu
			return "Glu";
		}else if(theCodon.equals("TGT")||theCodon.equals("TGC") || theCodon.equals("UGU")||theCodon.equals("UGC")){	//Cys
			return "Cys";
		}else if(theCodon.equals("TGG") || theCodon.equals("UGG")){	//Trp
			return "Trp";
		}else if(theCodon.equals("CGT")||theCodon.equals("CGC")||theCodon.equals("CGA")||theCodon.equals("CGG")||theCodon.equals("AGA")||theCodon.equals("AGG") || theCodon.equals("CGU")){	//Arg
			return "Arg";
		}else if(theCodon.equals("GGT")||theCodon.equals("GGC")||theCodon.equals("GGA")||theCodon.equals("GGG") || theCodon.equals("GGU")){	//Gly
			return "Gly";
		}else{	//unrecognized
			return "???";
		}
	}

	public static int getCodonCoordinate(String theCodon){
		if(theCodon.equals("UUU")||theCodon.equals("TTT")){
			return 0;
		}else if(theCodon.equals("UUC")||theCodon.equals("TTC")){
			return 1;
		}else if(theCodon.equals("UUA")||theCodon.equals("TTA")){
			return 2;
		}else if(theCodon.equals("UUG")||theCodon.equals("TTG")){
			return 3;
		}else if(theCodon.equals("CUU")||theCodon.equals("CTT")){
			return 4;
		}else if(theCodon.equals("CUC")||theCodon.equals("CTC")){
			return 5;
		}else if(theCodon.equals("CUA")||theCodon.equals("CTA")){
			return 6;
		}else if(theCodon.equals("CUG")||theCodon.equals("CTG")){
			return 7;
		}else if(theCodon.equals("AUU")||theCodon.equals("ATT")){
			return 8;
		}else if(theCodon.equals("AUC")||theCodon.equals("ATC")){
			return 9;
		}else if(theCodon.equals("AUA")||theCodon.equals("ATA")){
			return 10;
		}else if(theCodon.equals("AUG")||theCodon.equals("ATG")){
			return 11;
		}else if(theCodon.equals("GUU")||theCodon.equals("GTT")){
			return 12;
		}else if(theCodon.equals("GUC")||theCodon.equals("GTC")){
			return 13;
		}else if(theCodon.equals("GUA")||theCodon.equals("GTA")){
			return 14;
		}else if(theCodon.equals("GUG")||theCodon.equals("GTG")){
			return 15;
		}else if(theCodon.equals("UCU")||theCodon.equals("TCT")){
			return 16;
		}else if(theCodon.equals("UCC")||theCodon.equals("TCC")){
			return 17;
		}else if(theCodon.equals("UCA")||theCodon.equals("TCA")){
			return 18;
		}else if(theCodon.equals("UCG")||theCodon.equals("TCG")){
			return 19;
		}else if(theCodon.equals("CCU")||theCodon.equals("CCT")){
			return 20;
		}else if(theCodon.equals("CCC")){
			return 21;
		}else if(theCodon.equals("CCA")){
			return 22;
		}else if(theCodon.equals("CCG")){
			return 23;
		}else if(theCodon.equals("ACU")||theCodon.equals("ACT")){
			return 24;
		}else if(theCodon.equals("ACC")){
			return 25;
		}else if(theCodon.equals("ACA")){
			return 26;
		}else if(theCodon.equals("ACG")){
			return 27;
		}else if(theCodon.equals("GCU")||theCodon.equals("GCT")){
			return 28;
		}else if(theCodon.equals("GCC")){
			return 29;
		}else if(theCodon.equals("GCA")){
			return 30;
		}else if(theCodon.equals("GCG")){
			return 31;
		}else if(theCodon.equals("UAU")||theCodon.equals("TAT")){
			return 32;
		}else if(theCodon.equals("UAC")||theCodon.equals("TAC")){
			return 33;
		}else if(theCodon.equals("UAA")||theCodon.equals("TAA")){
			return 34;
		}else if(theCodon.equals("UAG")||theCodon.equals("TAG")){
			return 35;
		}else if(theCodon.equals("CAU")||theCodon.equals("CAT")){
			return 36;
		}else if(theCodon.equals("CAC")){
			return 37;
		}else if(theCodon.equals("CAA")){
			return 38;
		}else if(theCodon.equals("CAG")){
			return 39;
		}else if(theCodon.equals("AAU")||theCodon.equals("AAT")){
			return 40;
		}else if(theCodon.equals("AAC")){
			return 41;
		}else if(theCodon.equals("AAA")){
			return 42;
		}else if(theCodon.equals("AAG")){
			return 43;
		}else if(theCodon.equals("GAU")||theCodon.equals("GAT")){
			return 44;
		}else if(theCodon.equals("GAC")){
			return 45;
		}else if(theCodon.equals("GAA")){
			return 46;
		}else if(theCodon.equals("GAG")){
			return 47;
		}else if(theCodon.equals("UGU")||theCodon.equals("TGT")){
			return 48;
		}else if(theCodon.equals("UGC")||theCodon.equals("TGC")){
			return 49;
		}else if(theCodon.equals("UGA")||theCodon.equals("TGA")){
			return 50;
		}else if(theCodon.equals("UGG")||theCodon.equals("TGG")){
			return 51;
		}else if(theCodon.equals("CGU")||theCodon.equals("CGT")){
			return 52;
		}else if(theCodon.equals("CGC")){
			return 53;
		}else if(theCodon.equals("CGA")){
			return 54;
		}else if(theCodon.equals("CGG")){
			return 55;
		}else if(theCodon.equals("AGU")||theCodon.equals("AGT")){
			return 56;
		}else if(theCodon.equals("AGC")){
			return 57;
		}else if(theCodon.equals("AGA")){
			return 58;
		}else if(theCodon.equals("AGG")){
			return 59;
		}else if(theCodon.equals("GGU")||theCodon.equals("GGT")){
			return 60;
		}else if(theCodon.equals("GGC")){
			return 61;
		}else if(theCodon.equals("GGA")){
			return 62;
		}else if(theCodon.equals("GGG")){
			return 63;
		}else{	//not recognized
			return -1;
		}
	}

	public static String getCodonByCoordinate(int coord){	//'T' style NOT 'U' style
		if(coord==0){
			return "TTT";
		}else if(coord==1){
			return "TTC";
		}else if(coord==2){
			return "TTA";
		}else if(coord==3){
			return "TTG";
		}else if(coord==4){
			return "CTT";
		}else if(coord==5){
			return "CTC";
		}else if(coord==6){
			return "CTA";
		}else if(coord==7){
			return "CTG";
		}else if(coord==8){
			return "ATT";
		}else if(coord==9){
			return "ATC";
		}else if(coord==10){
			return "ATA";
		}else if(coord==11){
			return "ATG";
		}else if(coord==12){
			return "GTT";
		}else if(coord==13){
			return "GTC";
		}else if(coord==14){
			return "GTA";
		}else if(coord==15){
			return "GTG";
		}else if(coord==16){
			return "TCT";
		}else if(coord==17){
			return "TCC";
		}else if(coord==18){
			return "TCA";
		}else if(coord==19){
			return "TCG";
		}else if(coord==20){
			return "CCT";
		}else if(coord==21){
			return "CCC";
		}else if(coord==22){
			return "CCA";
		}else if(coord==23){
			return "CCG";
		}else if(coord==24){
			return "ACT";
		}else if(coord==25){
			return "ACC";
		}else if(coord==26){
			return "ACA";
		}else if(coord==27){
			return "ACG";
		}else if(coord==28){
			return "GCT";
		}else if(coord==29){
			return "GCC";
		}else if(coord==30){
			return "GCA";
		}else if(coord==31){
			return "GCG";
		}else if(coord==32){
			return "TAT";
		}else if(coord==33){
			return "TAC";
		}else if(coord==34){
			return "TAA";
		}else if(coord==35){
			return "TAG";
		}else if(coord==36){
			return "CAT";
		}else if(coord==37){
			return "CAC";
		}else if(coord==38){
			return "CAA";
		}else if(coord==39){
			return "CAG";
		}else if(coord==40){
			return "AAT";
		}else if(coord==41){
			return "AAC";
		}else if(coord==42){
			return "AAA";
		}else if(coord==43){
			return "AAG";
		}else if(coord==44){
			return "GAT";
		}else if(coord==45){
			return "GAC";
		}else if(coord==46){
			return "GAA";
		}else if(coord==47){
			return "GAG";
		}else if(coord==48){
			return "TGT";
		}else if(coord==49){
			return "TGC";
		}else if(coord==50){
			return "TGA";
		}else if(coord==51){
			return "TGG";
		}else if(coord==52){
			return "CGT";
		}else if(coord==53){
			return "CGC";
		}else if(coord==54){
			return "CGA";
		}else if(coord==55){
			return "CGG";
		}else if(coord==56){
			return "AGT";
		}else if(coord==57){
			return "AGC";
		}else if(coord==58){
			return "AGA";
		}else if(coord==59){
			return "AGG";
		}else if(coord==60){
			return "GGT";
		}else if(coord==61){
			return "GGC";
		}else if(coord==62){
			return "GGA";
		}else if(coord==63){
			return "GGG";
		}else{	//not recognized
			return null;
		}
	}

	public static int[] getAAGroupMembers(String theCodon){
		if(theCodon.equals("TTT")||theCodon.equals("TTC") || theCodon.equals("UUU")||theCodon.equals("UUC")){	//Phe
			return new int[]{0,1};
		}else if(theCodon.equals("TTA")||theCodon.equals("TTG")||theCodon.equals("CTT")||theCodon.equals("CTC")||theCodon.equals("CTA")||theCodon.equals("CTG") || theCodon.equals("UUA")||theCodon.equals("UUG")||theCodon.equals("CUU")||theCodon.equals("CUC")||theCodon.equals("CUA")||theCodon.equals("CUG")){	//Leu
			return new int[]{2,3,4,5,6,7};
		}else if(theCodon.equals("ATT")||theCodon.equals("ATC")||theCodon.equals("ATA") || theCodon.equals("AUU")||theCodon.equals("AUC")||theCodon.equals("AUA")){	//ile
			return new int[]{8,9,10};
		}else if(theCodon.equals("ATG") || theCodon.equals("AUG")){	//Met
			return new int[]{11};
		}else if(theCodon.equals("GTT")||theCodon.equals("GTC")||theCodon.equals("GTA")||theCodon.equals("GTG") || theCodon.equals("GUU")||theCodon.equals("GUC")||theCodon.equals("GUA")||theCodon.equals("GUG")){	//Val
			return new int[]{12,13,14,15};
		}else if(theCodon.equals("TCT")||theCodon.equals("TCC")||theCodon.equals("TCA")||theCodon.equals("TCG")||theCodon.equals("AGT")||theCodon.equals("AGC") || theCodon.equals("UCU")||theCodon.equals("UCC")||theCodon.equals("UCA")||theCodon.equals("UCG")||theCodon.equals("AGU")){	//Ser
			return new int[]{16,17,18,19,56,57};
		}else if(theCodon.equals("CCT")||theCodon.equals("CCC")||theCodon.equals("CCA")||theCodon.equals("CCG") || theCodon.equals("CCU")){	//Pro
			return new int[]{20,21,22,23};
		}else if(theCodon.equals("ACT")||theCodon.equals("ACC")||theCodon.equals("ACA")||theCodon.equals("ACG") || theCodon.equals("ACU")){	//Thr
			return new int[]{24,25,26,27};
		}else if(theCodon.equals("GCT")||theCodon.equals("GCC")||theCodon.equals("GCA")||theCodon.equals("GCG") || theCodon.equals("GCU")){	//Ala
			return new int[]{28,29,30,31};
		}else if(theCodon.equals("TAT")||theCodon.equals("TAC") || theCodon.equals("UAU")||theCodon.equals("UAC")){	//Tyr
			return new int[]{32,33};
		}else if(theCodon.equals("TAA")||theCodon.equals("TAG")||theCodon.equals("TGA") || theCodon.equals("UAA")||theCodon.equals("UAG")||theCodon.equals("UGA")){	//STOP
			return new int[]{34,35,50};
		}else if(theCodon.equals("CAT")||theCodon.equals("CAC") || theCodon.equals("CAU")){	//His
			return new int[]{36,37};
		}else if(theCodon.equals("CAA")||theCodon.equals("CAG")){	//Gln
			return new int[]{38,39};
		}else if(theCodon.equals("AAT")||theCodon.equals("AAC") || theCodon.equals("AAU")){	//Asn
			return new int[]{40,41};
		}else if(theCodon.equals("AAA")||theCodon.equals("AAG")){	//Lys
			return new int[]{42,43};
		}else if(theCodon.equals("GAT")||theCodon.equals("GAC") || theCodon.equals("GAU")){	//Asp
			return new int[]{44,45};
		}else if(theCodon.equals("GAA")||theCodon.equals("GAG")){	//Glu
			return new int[]{46,47};
		}else if(theCodon.equals("TGT")||theCodon.equals("TGC") || theCodon.equals("UGU")||theCodon.equals("UGC")){	//Cys
			return new int[]{48,49};
		}else if(theCodon.equals("TGG") || theCodon.equals("UGG")){	//Trp
			return new int[]{51};
		}else if(theCodon.equals("CGT")||theCodon.equals("CGC")||theCodon.equals("CGA")||theCodon.equals("CGG")||theCodon.equals("AGA")||theCodon.equals("AGG") || theCodon.equals("CGU")){	//Arg
			return new int[]{52,53,54,55,58,59};
		}else if(theCodon.equals("GGT")||theCodon.equals("GGC")||theCodon.equals("GGA")||theCodon.equals("GGG") || theCodon.equals("GGU")){	//Gly
			return new int[]{60,61,62,63};
		}else{
			return null;
		}
	}

	public static int[] getAAGroupMembersByNumber(int number){	//by 21 numbering system
		if(number==0){	//Phe
			return new int[]{0,1};
		}else if(number==1){	//Leu
			return new int[]{2,3,4,5,6,7};
		}else if(number==2){	//ile
			return new int[]{8,9,10};
		}else if(number==3){	//Met
			return new int[]{11};
		}else if(number==4){	//Val
			return new int[]{12,13,14,15};
		}else if(number==5){	//Ser
			return new int[]{16,17,18,19,56,57};
		}else if(number==6){	//Pro
			return new int[]{20,21,22,23};
		}else if(number==7){	//Thr
			return new int[]{24,25,26,27};
		}else if(number==8){	//Ala
			return new int[]{28,29,30,31};
		}else if(number==9){	//Tyr
			return new int[]{32,33};
		}else if(number==10){	//STOP
			return new int[]{34,35,50};
		}else if(number==11){	//His
			return new int[]{36,37};
		}else if(number==12){	//Gln
			return new int[]{38,39};
		}else if(number==13){	//Asn
			return new int[]{40,41};
		}else if(number==14){	//Lys
			return new int[]{42,43};
		}else if(number==15){	//Asp
			return new int[]{44,45};
		}else if(number==16){	//Glu
			return new int[]{46,47};
		}else if(number==17){	//Cys
			return new int[]{48,49};
		}else if(number==18){	//Trp
			return new int[]{51};
		}else if(number==19){	//Arg
			return new int[]{52,53,54,55,58,59};
		}else if(number==20){	//Gly
			return new int[]{60,61,62,63};
		}else{
			return null;
		}
	}

	public static int[] getCodonGroupMembers(String theCodon){	//not useful at all
		if(theCodon.equals("TTT")||theCodon.equals("TTC")){	//Phe
			return new int[]{0,1};
		}else if(theCodon.equals("TTA")||theCodon.equals("TTG")){	//Leu1
			return new int[]{2,3};
		}else if(theCodon.equals("CTT")||theCodon.equals("CTC")||theCodon.equals("CTA")||theCodon.equals("CTG")){	//Leu2
			return new int[]{4,5,6,7};
		}else if(theCodon.equals("ATT")||theCodon.equals("ATC")||theCodon.equals("ATA")){	//ile
			return new int[]{8,9,10};
		}else if(theCodon.equals("AUG")){	//Met
			return new int[]{11};
		}else if(theCodon.equals("GTT")||theCodon.equals("GTC")||theCodon.equals("GTA")||theCodon.equals("GTG")){	//Val
			return new int[]{12,13,14,15};
		}else if(theCodon.equals("TCT")||theCodon.equals("TCC")||theCodon.equals("TCA")||theCodon.equals("TCG")){	//Ser1
			return new int[]{16,17,18,19};
		}else if(theCodon.equals("CCT")||theCodon.equals("CCC")||theCodon.equals("CCA")||theCodon.equals("CCG")){	//Pro
			return new int[]{20,21,22,23};
		}else if(theCodon.equals("ACT")||theCodon.equals("ACC")||theCodon.equals("ACA")||theCodon.equals("ACG")){	//Thr
			return new int[]{24,25,26,27};
		}else if(theCodon.equals("GCT")||theCodon.equals("GCC")||theCodon.equals("GCA")||theCodon.equals("GCG")){	//Ala
			return new int[]{28,29,30,31};
		}else if(theCodon.equals("TAT")||theCodon.equals("TAC")){	//Tyr
			return new int[]{32,33};
		}else if(theCodon.equals("TAA")||theCodon.equals("TAG")||theCodon.equals("TGA")){	//STOP
			return new int[]{34,35,50};
		}else if(theCodon.equals("CAT")||theCodon.equals("CAC")){	//His
			return new int[]{36,37};
		}else if(theCodon.equals("CAA")||theCodon.equals("CAG")){	//Gln
			return new int[]{38,39};
		}else if(theCodon.equals("AAT")||theCodon.equals("AAC")){	//Asn
			return new int[]{40,41};
		}else if(theCodon.equals("AAA")||theCodon.equals("AAG")){	//Lys
			return new int[]{42,43};
		}else if(theCodon.equals("GAT")||theCodon.equals("GAC")){	//Asp
			return new int[]{44,45};
		}else if(theCodon.equals("GAA")||theCodon.equals("GAG")){	//Glu
			return new int[]{46,47};
		}else if(theCodon.equals("TGT")||theCodon.equals("TGC")){	//Cys
			return new int[]{48,49};
		}else if(theCodon.equals("TGG")){	//Trp
			return new int[]{51};
		}else if(theCodon.equals("CGT")||theCodon.equals("CGC")||theCodon.equals("CGA")||theCodon.equals("CGG")){	//Arg1
			return new int[]{52,53,54,55};
		}else if(theCodon.equals("AGT")||theCodon.equals("AGC")){	//Ser2
			return new int[]{56,57};
		}else if(theCodon.equals("AGA")||theCodon.equals("AGG")){	//Arg2
			return new int[]{58,59};
		}else if(theCodon.equals("GGT")||theCodon.equals("GGC")||theCodon.equals("GGA")||theCodon.equals("GGG")){	//Gly
			return new int[]{60,61,62,63};
		}else{
			return null;
		}
	}

	public static String getAAByCoordinate(int number){	//64 aa numbering system
		return getAAShortForm(getCodonByCoordinate(number));
	}

	public static String getAAByNumber(int number){	//21 aa numbering system
		if(number==0){
			return "Phe";
		}else if(number==1){
			return "Leu";
		}else if(number==2){
			return "ile";
		}else if(number==3){
			return "Met";
		}else if(number==4){
			return "Val";
		}else if(number==5){
			return "Ser";
		}else if(number==6){
			return "Pro";
		}else if(number==7){
			return "Thr";
		}else if(number==8){
			return "Ala";
		}else if(number==9){
			return "Tyr";
		}else if(number==10){
			return "STOP";
		}else if(number==11){
			return "His";
		}else if(number==12){
			return "Gln";
		}else if(number==13){
			return "Asn";
		}else if(number==14){
			return "Lys";
		}else if(number==15){
			return "Asp";
		}else if(number==16){
			return "Glu";
		}else if(number==17){
			return "Cys";
		}else if(number==18){
			return "Trp";
		}else if(number==19){
			return "Arg";
		}else if(number==20){
			return "Gly";
		}else{
			return "???";
		}
	}

	public static String getOppositeDNAStrand(String theStrand){	//get the opposite DNA strand sequence of given strand
		String opposite="";
		for(int i=0;i<theStrand.length();i++){
			char theChar=theStrand.charAt(i);
			if(theChar=='A'){
				opposite+="T";
			}else if(theChar=='T'){
				opposite+="A";
			}else if(theChar=='G'){
				opposite+="C";
			}else if(theChar=='C'){
				opposite+="G";
			}else if(theChar=='N'){
				opposite+="N";
			}else if(theChar=='R'){
				opposite+="Y";
			}else if(theChar=='Y'){
				opposite+="R";
			}else if(theChar=='K'){
				opposite+="M";
			}else if(theChar=='M'){
				opposite+="K";
			}else if(theChar=='S'){
				opposite+="S";
			}else if(theChar=='W'){
				opposite+="W";
			}else if(theChar=='B'){
				opposite+="V";
			}else if(theChar=='D'){
				opposite+="H";
			}else if(theChar=='H'){
				opposite+="D";
			}else if(theChar=='V'){
				opposite+="B";
			}else{	//unidentified
				opposite+="X";
			}
		}
		return opposite;
	}

	public static GenBank getGenBank(String dirName,ArrayList<GenBank> library,ArrayList<GenBank> toolbox,ArrayList<GenBank> favourite){	//find the genbank out of library,toolbox and favourite
		GenBank genBank=null;
		if(dirName.contains("."+File.separator+"Library"+File.separator)){
			for(int j=0;j<library.size();j++){//seek up the genBank file in library
				if(dirName.equals(library.get(j).dirName)){
					genBank=library.get(j);
				}
			}
		}else if(dirName.contains("."+File.separator+"Favourite"+File.separator)){
//System.out.println("DIRNAME---------->"+dirName);
			for(int j=0;j<favourite.size();j++){//seek up the genBank file in favourite
//System.out.println(favourite.get(j).dirName);
				if(dirName.equals(favourite.get(j).dirName)){
					genBank=favourite.get(j);
				}
			}
		}else{	//dirName.contains("."+File.separator+"ToolBox")
			for(int j=0;j<toolbox.size();j++){//seek up the genBank file in toolbox
				if(dirName.equals(toolbox.get(j).dirName)){
					genBank=toolbox.get(j);
				}
			}
		}
		return genBank;
	}

	public static String replaceEach(final String[] cues,final String[] products,String input){	//stimultaneous replaceAll
		Map<String,String> replacements=new HashMap<String,String>(){{
			for(int i=0;i<cues.length;i++){
				put(cues[i],products[i]);
			}
		}};

		String regexp="";
		for(int i=0;i<cues.length;i++){
			if(i==0){
				regexp=cues[0];
			}else{
				regexp+="|"+cues[i];
			}
		}

		StringBuffer sb=new StringBuffer();
		Pattern p=Pattern.compile(regexp);
		Matcher m=p.matcher(input);

		while(m.find())m.appendReplacement(sb,replacements.get(m.group()));
		m.appendTail(sb);

		return sb.toString();
	}

	//get best codon from codon frequency table include/exclude itself
	public static String getBestCodon(String oriCodon,int[] codonFrequencyTable,boolean self){
		int oriCoord=getCodonCoordinate(oriCodon);
		int[] allCoord=getAAGroupMembers(oriCodon);
		int highestCoord=-1;
		for(int v=0;v<allCoord.length;v++){
			if(allCoord[v]==oriCoord&&!self){
				continue;
			}
			if(highestCoord==-1){
				highestCoord=allCoord[v];
				continue;
			}
			if(codonFrequencyTable[allCoord[v]]>codonFrequencyTable[highestCoord]){
				highestCoord= allCoord[v];
			}
		}
		return Common.getCodonByCoordinate(highestCoord);
	}

	//snapshot a JPanel
	public static void takeSnapshot(JPanel thePanel,File saveFile){
		BufferedImage bi=new BufferedImage(thePanel.getWidth(),thePanel.getHeight(),BufferedImage.TYPE_INT_RGB);
		thePanel.paint(bi.createGraphics());

		try{
			saveFile.createNewFile();
			ImageIO.write(bi,"jpeg",saveFile);
		}catch(Exception e){/*ignore*/}
	}

	//ping an address to check for connectivity(mainly CUO site)
	public static boolean ping(String address){
		try{
			HttpURLConnection connection=(HttpURLConnection)new URL(address).openConnection();
			connection.setRequestMethod("HEAD");
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(10000);
			int responseCode=connection.getResponseCode();
			connection.disconnect();
			if(responseCode!=200){	//not ok
				return false;
			}else{	//ok
				return true;
			}
		}catch(Exception e){
			return false;
		}
	}

	//download and save a TEMP file from a given URL
	public static File saveTempURL(String url){
		try{
			return saveTempURL(new URL(url));
		}catch(Exception e){return null;}
	}
	public static File saveTempURL(URL url){
		try{
			URL website=url;
			ReadableByteChannel rbc=Channels.newChannel(website.openStream());
			File theFile=File.createTempFile("CUO",null);
			FileOutputStream fos=new FileOutputStream(theFile);
			fos.getChannel().transferFrom(rbc,0,1<<24);
			rbc.close();
			fos.close();
			return theFile;
		}catch(Exception e){
			return null;
		}
	}

	//download and save a file from a given URL
	public static File saveURL(String url,String fileName){
		try{
			return saveURL(new URL(url),fileName);
		}catch(Exception e){return null;}
	}
	public static File saveURL(URL url,String fileName){
		try{
			URL website=url;
			ReadableByteChannel rbc=Channels.newChannel(website.openStream());
			File theFile=new File(fileName);
			FileOutputStream fos=new FileOutputStream(theFile);
			fos.getChannel().transferFrom(rbc,0,1<<24);
			rbc.close();
			fos.close();
			return theFile;
		}catch(Exception e){
			return null;
		}
	}

	public static boolean writeToFile(File file,String text){
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
			bw.write(text);
			bw.close();
			return true;	//success
		} catch (Exception e) {return false;}	//fail
	}

	public static void deleteFolder(File dir){
		if(dir.isDirectory()){
			String[] children=dir.list();
			for(int i=0;i<children.length;i++){
				deleteFolder(new File(dir,children[i]));
			}
		}
		dir.delete();
	}

	//restart the program, runBeforeStart can be null
	public static void restartApplication(Runnable runBeforeRestart) throws IOException {
		try {
			Runtime.getRuntime().exec("java -jar Launch.jar");
			System.exit(0);
		} catch (Exception e) {
			// something went wrong
			throw new IOException("Error while trying to restart the application", e);
		}
	}

	//reverse an int array
	public static void reverse(int[] b){
		int left=0;	//index of leftmost element
		int right=b.length-1;	//index of rightmost element

		while(left<right){
			//exchange the left and right elements
			int temp=b[left];
			b[left]=b[right];
			b[right]=temp;

			//move the bounds toward the center
			left++;
			right--;
		}
	}

	//disable ability to give name in JFileChooser
	public static void disableTextComponent(Container parent){
		Component[] c=parent.getComponents();
		for(int j=0;j<c.length;j++){
			//unpack component
			String s=c[j].getClass().getName();
			int dot=s.lastIndexOf(".");
			if(dot!=-1)s=s.substring(dot+1);

			if(s.equals("MetalFileChooserUI$3")){	//check if it is the one
				c[j].getParent().setVisible(false);	//hide
			}
			if(((Container)c[j]).getComponentCount()>0){
				disableTextComponent((Container)c[j]);
			}
		}
	}

	//add new genbank to library/toolbox/favourite
	public static void addToLibrary(ArrayList<GenBank> library,ArrayList<GenBank> toolbox,ArrayList<GenBank> favourite,GenBank newGen){
		//remove the overwritten genBank if there is any
		GenBank overwritten=Common.getGenBank(newGen.dirName,library,toolbox,favourite);
		if(overwritten!=null){
			if(!toolbox.remove(overwritten)){
				if(!library.remove(overwritten)){
					favourite.remove(overwritten);
				}
			}		
		}

		//add to list
		newGen.generateListModel();
		if(newGen.dirName.contains("."+File.separator+"Toolbox"+File.separator)){
			toolbox.add(newGen);
		}else if(newGen.dirName.contains("."+File.separator+"Library"+File.separator)){
			library.add(newGen);
		}else if(newGen.dirName.contains("."+File.separator+"Favourite"+File.separator)){
			favourite.add(newGen);
		}else{
			//ignore the outside files
		}
	}

	//check whether a string is nucleic acid sequence
	public static boolean isNA(String na){
		String test=na.trim().replaceAll("\\s","");	//remove all white space characters
		if(test.matches("[atugcATUGC]+")){
			return true;
		}
		return false;
	}

	//check whether a string is amino acid sequence
	public static boolean isAA(String aa){
		String test=aa.trim().replaceAll("\\s","");	//remove all white space characters
		if(test.matches("[gavfilpmdekrstycnqhwGAVFILPMDEKRSTYCNQHW\\*]+")){
			return true;
		}
		return false;
	}

	//invert the horizontal numbering of codon table to vertical numbering
	public static int horizontalToVerticalNumbering(int horizontalNum){
		//mathematics huh!
		//convert to coordinate
		int column=horizontalNum%4;
		int row=horizontalNum/4;
		//resolve from coordinate
		return row+column*16;
	}
	//invert vertical numbering to horizontal numbering (not useful at all)
	public static int verticalToHorizontalNumbering(int verticalNum){
		//convert to coordinate
		int column=verticalNum/16;
		int row=verticalNum%16;
		//resolve from coordinate
		return row*4+column;
	}
}
