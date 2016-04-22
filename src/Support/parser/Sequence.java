package Support.parser;

import java.util.ArrayList;

import Support.CodonPairCalculator;

public class Sequence{	//a class to present the gene
	public GenBank genBank;
	public String type;
	public ArrayList<int[]> interval=new ArrayList<int[]>();//list of intervals, can be one only as well
	public String gene;	//these information can be null, depends on the type of sequence
	public String note;
	public String product;
	public String geneID;
	public String codonRecognized;
	public int number;	//for the exon or intron order number
	public int[] codonFrequency;
	public int[][] codonPairFrequency;
	public String[] transcript;
	public double gCAI;
	public double CAI;
	public double CPI;
	public Boolean complementary=null;
	public int length=0;
	public String AA;	//String to store translated amino acid sequences
	public String codingSequence;	//string to store the codingSequence of the sequence

	public Sequence(){}

	public Sequence(Sequence original){	//copy another sequence details but not things like codonFrequency, CAI, gCAI, genBank, geneID, transcript
		if(original.type!=null)this.type=new String(original.type);
		this.interval=new ArrayList<int[]>();
		for(int i=0;i<original.interval.size();i++){
			int[] newarray=new int[2];
			newarray[0]=original.interval.get(i)[0];
			newarray[1]=original.interval.get(i)[1];
			this.interval.add(newarray);
		}
		if(original.gene!=null)this.gene=new String(original.gene);
		if(original.note!=null)this.note=new String(original.note);
		if(original.product!=null)this.product=new String(original.product);
		if(original.codonRecognized!=null)this.codonRecognized=new String(original.codonRecognized);
		if(original.number>0)this.number=original.number;
		if(original.complementary!=null)this.complementary=original.complementary.booleanValue();
		if(original.AA!=null)this.AA=original.AA;
	}

	public boolean isComplementary(){
		if(interval.get(0)[0]<interval.get(0)[1]){
			complementary=false;	//positive strand
		}else{
			complementary=true;	//negative strand
		}
		return complementary;
	}

	public String[] getSequence(String completeSequence){	//get sequence from the complete sequence
		String[] sequenceArray=new String[interval.size()];
		for(int i=0;i<sequenceArray.length;i++){
			int start=interval.get(i)[0];	//start site of the sequence
			int stop=interval.get(i)[1];	//stop site of the sequence
			if(stop<start){	//if it is on negative strand
				int temp=start;
				start=stop;
				stop=temp;
			}
			String sequence=completeSequence.substring(start-1,stop);	//start -1 because base 1 is included
			sequenceArray[i]=sequence;
		}

		return sequenceArray;
	}

	public String getTranscriptInLine(String completeSequence){	//getTranscript and then combine them into a string
		String theLine="";
		String[] theArray=getTranscript(completeSequence);
		for(int i=0;i<theArray.length;i++){
			theLine+=theArray[i];
		}
		return theLine;
	}
	public String[] getTranscript(String completeSequence){	//get transcribed sequence from DNA coding strand
		String[] sequenceArray=new String[interval.size()];
		for(int i=0;i<sequenceArray.length;i++){
			boolean positiveStrand=true;
			int start=interval.get(i)[0];	//start site of the sequence
			int stop=interval.get(i)[1];	//stop site of the sequence
			if(stop<start){	//if it is on negative strand
				positiveStrand=false;
				int temp=start;
				start=stop;
				stop=temp;
			}
			String sequence=completeSequence.substring(start-1,stop);	//start -1 because base 1 is included
			if(positiveStrand){	//the sequence is a positive strand no need to do much
				sequenceArray[i]=sequence;
			}else{
				//reverse the complementary intervals
				StringBuilder builder=new StringBuilder(sequence);
				sequence=builder.reverse().toString();
				//transcribe
				sequence=getCodingStrand(sequence);

				sequenceArray[i]=sequence;
			}
		}

		this.transcript=sequenceArray;
		return sequenceArray;
	}

	public String getCodingStrand(String sequence){	//from template strand
		StringBuilder builder=new StringBuilder(sequence);
		for(int i=0;i<builder.length();i++){
			if(builder.charAt(i)=='A'){
				builder.setCharAt(i,'T');
			}else if(builder.charAt(i)=='T'){
				builder.setCharAt(i,'A');
			}else if(builder.charAt(i)=='G'){
				builder.setCharAt(i,'C');
			}else if(builder.charAt(i)=='C'){
				builder.setCharAt(i,'G');
			}else{	//unidentified
				builder.setCharAt(i,'X');
			}
		}
		return builder.toString();
	}

	public int[] getCodonFrequency(String completeSequence){	//get int[65] codon frequency of this sequence
		if(this.codonFrequency!=null)return this.codonFrequency;
		countCodonFrequency(completeSequence);
		return codonFrequency;
	}

	public void countCodonFrequency(String completeSequence){
		String[] geneTranscript=getTranscript(completeSequence);
		StringBuilder builder=new StringBuilder();
		for(int k=0;k<geneTranscript.length;k++){	//concat all sites
			builder.append(geneTranscript[k]);
		}
		String combinedTrans=builder.toString();
		//count
		int[] codonFrequency=new int[65];
		int codonNum=combinedTrans.length()/3;
		for(int k=0;k<codonNum;k++){
			String codon=combinedTrans.substring(k*3,k*3+3);
			if(codon.equals("TTT")){	//UUU
				codonFrequency[0]+=1;
			}else if(codon.equals("TTC")){	//UUC
				codonFrequency[1]+=1;
			}else if(codon.equals("TTA")){	//UUA
				codonFrequency[2]+=1;
			}else if(codon.equals("TTG")){	//UUG
				codonFrequency[3]+=1;
			}else if(codon.equals("CTT")){	//CUU
				codonFrequency[4]+=1;
			}else if(codon.equals("CTC")){	//CUC
				codonFrequency[5]+=1;
			}else if(codon.equals("CTA")){	//CUA
				codonFrequency[6]+=1;
			}else if(codon.equals("CTG")){	//CUG
				codonFrequency[7]+=1;
			}else if(codon.equals("ATT")){	//AUU
				codonFrequency[8]+=1;
			}else if(codon.equals("ATC")){	//AUC
				codonFrequency[9]+=1;
			}else if(codon.equals("ATA")){	//AUA
				codonFrequency[10]+=1;
			}else if(codon.equals("ATG")){	//AUG
				codonFrequency[11]+=1;
			}else if(codon.equals("GTT")){	//GUU
				codonFrequency[12]+=1;
			}else if(codon.equals("GTC")){	//GUC
				codonFrequency[13]+=1;
			}else if(codon.equals("GTA")){	//GUA
				codonFrequency[14]+=1;
			}else if(codon.equals("GTG")){	//GUG
				codonFrequency[15]+=1;
			}else if(codon.equals("TCT")){	//UCU
				codonFrequency[16]+=1;
			}else if(codon.equals("TCC")){	//UCC
				codonFrequency[17]+=1;
			}else if(codon.equals("TCA")){	//UCA
				codonFrequency[18]+=1;
			}else if(codon.equals("TCG")){	//UCG
				codonFrequency[19]+=1;
			}else if(codon.equals("CCT")){	//CCU
				codonFrequency[20]+=1;
			}else if(codon.equals("CCC")){	//CCC
				codonFrequency[21]+=1;
			}else if(codon.equals("CCA")){	//CCA
				codonFrequency[22]+=1;
			}else if(codon.equals("CCG")){	//CCG
				codonFrequency[23]+=1;
			}else if(codon.equals("ACT")){	//ACU
				codonFrequency[24]+=1;
			}else if(codon.equals("ACC")){	//ACC
				codonFrequency[25]+=1;
			}else if(codon.equals("ACA")){	//ACA
				codonFrequency[26]+=1;
			}else if(codon.equals("ACG")){	//ACG
				codonFrequency[27]+=1;
			}else if(codon.equals("GCT")){	//GCU
				codonFrequency[28]+=1;
			}else if(codon.equals("GCC")){	//GCC
				codonFrequency[29]+=1;
			}else if(codon.equals("GCA")){	//GCA
				codonFrequency[30]+=1;
			}else if(codon.equals("GCG")){	//GCG
				codonFrequency[31]+=1;
			}else if(codon.equals("TAT")){	//UAU
				codonFrequency[32]+=1;
			}else if(codon.equals("TAC")){	//UAC
				codonFrequency[33]+=1;
			}else if(codon.equals("TAA")){	//UAA
				codonFrequency[34]+=1;
			}else if(codon.equals("TAG")){	//UAG
				codonFrequency[35]+=1;
			}else if(codon.equals("CAT")){	//CAU
				codonFrequency[36]+=1;
			}else if(codon.equals("CAC")){	//CAC
				codonFrequency[37]+=1;
			}else if(codon.equals("CAA")){	//CAA
				codonFrequency[38]+=1;
			}else if(codon.equals("CAG")){	//CAG
				codonFrequency[39]+=1;
			}else if(codon.equals("AAT")){	//AAU
				codonFrequency[40]+=1;
			}else if(codon.equals("AAC")){	//AAC
				codonFrequency[41]+=1;
			}else if(codon.equals("AAA")){	//AAA
				codonFrequency[42]+=1;
			}else if(codon.equals("AAG")){	//AAG
				codonFrequency[43]+=1;
			}else if(codon.equals("GAT")){	//GAU
				codonFrequency[44]+=1;
			}else if(codon.equals("GAC")){	//GAC
				codonFrequency[45]+=1;
			}else if(codon.equals("GAA")){	//GAA
				codonFrequency[46]+=1;
			}else if(codon.equals("GAG")){	//GAG
				codonFrequency[47]+=1;
			}else if(codon.equals("TGT")){	//UGU
				codonFrequency[48]+=1;
			}else if(codon.equals("TGC")){	//UGC
				codonFrequency[49]+=1;
			}else if(codon.equals("TGA")){	//UGA
				codonFrequency[50]+=1;
			}else if(codon.equals("TGG")){	//UGG
				codonFrequency[51]+=1;
			}else if(codon.equals("CGT")){	//CGU
				codonFrequency[52]+=1;
			}else if(codon.equals("CGC")){	//CGC
				codonFrequency[53]+=1;
			}else if(codon.equals("CGA")){	//CGA
				codonFrequency[54]+=1;
			}else if(codon.equals("CGG")){	//CGG
				codonFrequency[55]+=1;
			}else if(codon.equals("AGT")){	//AGU
				codonFrequency[56]+=1;
			}else if(codon.equals("AGC")){	//AGC
				codonFrequency[57]+=1;
			}else if(codon.equals("AGA")){	//AGA
				codonFrequency[58]+=1;
			}else if(codon.equals("AGG")){	//AGG
				codonFrequency[59]+=1;
			}else if(codon.equals("GGT")){	//GGU
				codonFrequency[60]+=1;
			}else if(codon.equals("GGC")){	//GGC
				codonFrequency[61]+=1;
			}else if(codon.equals("GGA")){	//GGA
				codonFrequency[62]+=1;
			}else if(codon.equals("GGG")){	//GGG
				codonFrequency[63]+=1;
			}else{	//unidentified
				codonFrequency[64]+=1;
			}
		}

		this.codonFrequency=codonFrequency;
	}

	public void countCodonPairFrequency(){	//count/recount codon pair frequency
		String[] arrprotSeq=getTranscript(genBank.completeSequence);
		String protSeq="";
		for(int j=0;j<arrprotSeq.length;j++){
			protSeq+=arrprotSeq[j];
		}
		int[][] theCodonPairFrequency=new int[64][64];
		for(int j=0;j<protSeq.length();j+=3){	//for every pair
			if(j+3>protSeq.length()-1){
				break;	//reach the end
			}

			String thePair=protSeq.substring(j,j+6);
			String first=thePair.substring(0,3);
			int firstInt=0;
			if(first.equals("TTT")){
				firstInt=0;
			}else if(first.equals("TTC")){
				firstInt=1;
			}else if(first.equals("TTA")){
				firstInt=2;
			}else if(first.equals("TTG")){
				firstInt=3;
			}else if(first.equals("CTT")){
				firstInt=4;
			}else if(first.equals("CTC")){
				firstInt=5;
			}else if(first.equals("CTA")){
				firstInt=6;
			}else if(first.equals("CTG")){
				firstInt=7;
			}else if(first.equals("ATT")){
				firstInt=8;
			}else if(first.equals("ATC")){
				firstInt=9;
			}else if(first.equals("ATA")){
				firstInt=10;
			}else if(first.equals("ATG")){
				firstInt=11;
			}else if(first.equals("GTT")){
				firstInt=12;
			}else if(first.equals("GTC")){
				firstInt=13;
			}else if(first.equals("GTA")){
				firstInt=14;
			}else if(first.equals("GTG")){
				firstInt=15;
			}else if(first.equals("TCT")){
				firstInt=16;
			}else if(first.equals("TCC")){
				firstInt=17;
			}else if(first.equals("TCA")){
				firstInt=18;
			}else if(first.equals("TCG")){
				firstInt=19;
			}else if(first.equals("CCT")){
				firstInt=20;
			}else if(first.equals("CCC")){
				firstInt=21;
			}else if(first.equals("CCA")){
				firstInt=22;
			}else if(first.equals("CCG")){
				firstInt=23;
			}else if(first.equals("ACT")){
				firstInt=24;
			}else if(first.equals("ACC")){
				firstInt=25;
			}else if(first.equals("ACA")){
				firstInt=26;
			}else if(first.equals("ACG")){
				firstInt=27;
			}else if(first.equals("GCT")){
				firstInt=28;
			}else if(first.equals("GCC")){
				firstInt=29;
			}else if(first.equals("GCA")){
				firstInt=30;
			}else if(first.equals("GCG")){
				firstInt=31;
			}else if(first.equals("TAT")){
				firstInt=32;
			}else if(first.equals("TAC")){
				firstInt=33;
			}else if(first.equals("TAA")){
				firstInt=34;
			}else if(first.equals("TAG")){
				firstInt=35;
			}else if(first.equals("CAT")){
				firstInt=36;
			}else if(first.equals("CAC")){
				firstInt=37;
			}else if(first.equals("CAA")){
				firstInt=38;
			}else if(first.equals("CAG")){
				firstInt=39;
			}else if(first.equals("AAT")){
				firstInt=40;
			}else if(first.equals("AAC")){
				firstInt=41;
			}else if(first.equals("AAA")){
				firstInt=42;
			}else if(first.equals("AAG")){
				firstInt=43;
			}else if(first.equals("GAT")){
				firstInt=44;
			}else if(first.equals("GAC")){
				firstInt=45;
			}else if(first.equals("GAA")){
				firstInt=46;
			}else if(first.equals("GAG")){
				firstInt=47;
			}else if(first.equals("TGT")){
				firstInt=48;
			}else if(first.equals("TGC")){
				firstInt=49;
			}else if(first.equals("TGA")){
				firstInt=50;
			}else if(first.equals("TGG")){
				firstInt=51;
			}else if(first.equals("CGT")){
				firstInt=52;
			}else if(first.equals("CGC")){
				firstInt=53;
			}else if(first.equals("CGA")){
				firstInt=54;
			}else if(first.equals("CGG")){
				firstInt=55;
			}else if(first.equals("AGT")){
				firstInt=56;
			}else if(first.equals("AGC")){
				firstInt=57;
			}else if(first.equals("AGA")){
				firstInt=58;
			}else if(first.equals("AGG")){
				firstInt=59;
			}else if(first.equals("GGT")){
				firstInt=60;
			}else if(first.equals("GGC")){
				firstInt=61;
			}else if(first.equals("GGA")){
				firstInt=62;
			}else if(first.equals("GGG")){
				firstInt=63;
			}
			String second=thePair.substring(3,6);
			int secondInt=0;
			if(second.equals("UUU")){
				secondInt=0;
			}else if(second.equals("TTC")){
				secondInt=1;
			}else if(second.equals("TTA")){
				secondInt=2;
			}else if(second.equals("TTG")){
				secondInt=3;
			}else if(second.equals("CTT")){
				secondInt=4;
			}else if(second.equals("CTC")){
				secondInt=5;
			}else if(second.equals("CTA")){
				secondInt=6;
			}else if(second.equals("CTG")){
				secondInt=7;
			}else if(second.equals("ATT")){
				secondInt=8;
			}else if(second.equals("ATC")){
				secondInt=9;
			}else if(second.equals("ATA")){
				secondInt=10;
			}else if(second.equals("ATG")){
				secondInt=11;
			}else if(second.equals("GTT")){
				secondInt=12;
			}else if(second.equals("GTC")){
				secondInt=13;
			}else if(second.equals("GTA")){
				secondInt=14;
			}else if(second.equals("GTG")){
				secondInt=15;
			}else if(second.equals("TCT")){
				secondInt=16;
			}else if(second.equals("TCC")){
				secondInt=17;
			}else if(second.equals("TCA")){
				secondInt=18;
			}else if(second.equals("TCG")){
				secondInt=19;
			}else if(second.equals("CCT")){
				secondInt=20;
			}else if(second.equals("CCC")){
				secondInt=21;
			}else if(second.equals("CCA")){
				secondInt=22;
			}else if(second.equals("CCG")){
				secondInt=23;
			}else if(second.equals("ACT")){
				secondInt=24;
			}else if(second.equals("ACC")){
				secondInt=25;
			}else if(second.equals("ACA")){
				secondInt=26;
			}else if(second.equals("ACG")){
				secondInt=27;
			}else if(second.equals("GCT")){
				secondInt=28;
			}else if(second.equals("GCC")){
				secondInt=29;
			}else if(second.equals("GCA")){
				secondInt=30;
			}else if(second.equals("GCG")){
				secondInt=31;
			}else if(second.equals("TAT")){
				secondInt=32;
			}else if(second.equals("TAC")){
				secondInt=33;
			}else if(second.equals("TAA")){
				secondInt=34;
			}else if(second.equals("TAG")){
				secondInt=35;
			}else if(second.equals("CAT")){
				secondInt=36;
			}else if(second.equals("CAC")){
				secondInt=37;
			}else if(second.equals("CAA")){
				secondInt=38;
			}else if(second.equals("CAG")){
				secondInt=39;
			}else if(second.equals("AAT")){
				secondInt=40;
			}else if(second.equals("AAC")){
				secondInt=41;
			}else if(second.equals("AAA")){
				secondInt=42;
			}else if(second.equals("AAG")){
				secondInt=43;
			}else if(second.equals("GAT")){
				secondInt=44;
			}else if(second.equals("GAC")){
				secondInt=45;
			}else if(second.equals("GAA")){
				secondInt=46;
			}else if(second.equals("GAG")){
				secondInt=47;
			}else if(second.equals("TGT")){
				secondInt=48;
			}else if(second.equals("TGC")){
				secondInt=49;
			}else if(second.equals("TGA")){
				secondInt=50;
			}else if(second.equals("TGG")){
				secondInt=51;
			}else if(second.equals("CGT")){
				secondInt=52;
			}else if(second.equals("CGC")){
				secondInt=53;
			}else if(second.equals("CGA")){
				secondInt=54;
			}else if(second.equals("CGG")){
				secondInt=55;
			}else if(second.equals("AGT")){
				secondInt=56;
			}else if(second.equals("AGC")){
				secondInt=57;
			}else if(second.equals("AGA")){
				secondInt=58;
			}else if(second.equals("AGG")){
				secondInt=59;
			}else if(second.equals("GGT")){
				secondInt=60;
			}else if(second.equals("GGC")){
				secondInt=61;
			}else if(second.equals("GGA")){
				secondInt=62;
			}else if(second.equals("GGG")){
				secondInt=63;
			}

			theCodonPairFrequency[firstInt][secondInt]+=1;
		}
		codonPairFrequency=theCodonPairFrequency;
	}

	public int[][] getCodonPairFrequency(){	//get codon pair frequency of this sequence
		if(this.codonPairFrequency!=null)return this.codonPairFrequency;
		this.codonPairFrequency=CodonPairCalculator.getCodonPairFrequency(this);
		return this.codonPairFrequency;
	}

	public int getLength(){
		if(length!=0){
			return length;
		}else{
			length=0;
			for(int i=0;i<interval.size();i++){
				length+=Math.abs(interval.get(i)[1]-interval.get(i)[0])+1;
			}
			return length;
		}
	}

	public String getAA(){
		if(AA!=null)return AA;

		ArrayList<String> AAFragments=new ArrayList<String>();	//string list to store translated amino acid sequences
		String[] transcriptList=getTranscript(genBank.completeSequence);
		for(int i=0;i<transcriptList.length;i++){
			String thePart=transcriptList[i];
			String AAFragment="";	//string to store coded amino acid sequence of this part
			for(int j=0;j<thePart.length();j+=3){	//for every 3 sequences (1 codon)
				if((j+3)>thePart.length())break;
				String code=thePart.substring(j,j+3);
				if(code.equals("UUU")){
					AAFragment+="F";
				}else if(code.equals("UUC")){
					AAFragment+="F";
				}else if(code.equals("UUA")){
					AAFragment+="L";
				}else if(code.equals("UUG")){
					AAFragment+="L";
				}else if(code.equals("CUU")){
					AAFragment+="L";
				}else if(code.equals("CUC")){
					AAFragment+="L";
				}else if(code.equals("CUA")){
					AAFragment+="L";
				}else if(code.equals("CUG")){
					AAFragment+="L";
				}else if(code.equals("AUU")){
					AAFragment+="I";
				}else if(code.equals("AUC")){
					AAFragment+="I";
				}else if(code.equals("AUA")){
					AAFragment+="I";
				}else if(code.equals("AUG")){
					AAFragment+="M";
				}else if(code.equals("GUU")){
					AAFragment+="V";
				}else if(code.equals("GUC")){
					AAFragment+="V";
				}else if(code.equals("GUA")){
					AAFragment+="V";
				}else if(code.equals("GUG")){
					AAFragment+="V";
				}else if(code.equals("UCU")){
					AAFragment+="S";
				}else if(code.equals("UCC")){
					AAFragment+="S";
				}else if(code.equals("UCA")){
					AAFragment+="S";
				}else if(code.equals("UCG")){
					AAFragment+="S";
				}else if(code.equals("CCU")){
					AAFragment+="P";
				}else if(code.equals("CCC")){
					AAFragment+="P";
				}else if(code.equals("CCA")){
					AAFragment+="P";
				}else if(code.equals("CCG")){
					AAFragment+="P";
				}else if(code.equals("ACU")){
					AAFragment+="T";
				}else if(code.equals("ACC")){
					AAFragment+="T";
				}else if(code.equals("ACA")){
					AAFragment+="T";
				}else if(code.equals("ACG")){
					AAFragment+="T";
				}else if(code.equals("GCU")){
					AAFragment+="A";
				}else if(code.equals("GCC")){
					AAFragment+="A";
				}else if(code.equals("GCA")){
					AAFragment+="A";
				}else if(code.equals("GCG")){
					AAFragment+="A";
				}else if(code.equals("UAU")){
					AAFragment+="Y";
				}else if(code.equals("UAC")){
					AAFragment+="Y";
				}else if(code.equals("UAA")){
					AAFragment+="X";	//stop
				}else if(code.equals("UAG")){
					AAFragment+="X";
				}else if(code.equals("CAU")){
					AAFragment+="H";
				}else if(code.equals("CAC")){
					AAFragment+="H";
				}else if(code.equals("CAA")){
					AAFragment+="O";
				}else if(code.equals("CAG")){
					AAFragment+="O";
				}else if(code.equals("AAU")){
					AAFragment+="N";
				}else if(code.equals("AAC")){
					AAFragment+="N";
				}else if(code.equals("AAA")){
					AAFragment+="K";
				}else if(code.equals("AAG")){
					AAFragment+="K";
				}else if(code.equals("GAU")){
					AAFragment+="D";
				}else if(code.equals("GAC")){
					AAFragment+="D";
				}else if(code.equals("GAA")){
					AAFragment+="E";
				}else if(code.equals("GAG")){
					AAFragment+="E";
				}else if(code.equals("UGU")){
					AAFragment+="C";
				}else if(code.equals("UGC")){
					AAFragment+="C";
				}else if(code.equals("UGA")){
					AAFragment+="X";
				}else if(code.equals("UGG")){
					AAFragment+="W";
				}else if(code.equals("CGU")){
					AAFragment+="R";
				}else if(code.equals("CGC")){
					AAFragment+="R";
				}else if(code.equals("CGA")){
					AAFragment+="R";
				}else if(code.equals("CGG")){
					AAFragment+="R";
				}else if(code.equals("AGU")){
					AAFragment+="S";
				}else if(code.equals("AGC")){
					AAFragment+="S";
				}else if(code.equals("AGA")){
					AAFragment+="R";
				}else if(code.equals("AGG")){
					AAFragment+="R";
				}else if(code.equals("GGU")){
					AAFragment+="G";
				}else if(code.equals("GGC")){
					AAFragment+="G";
				}else if(code.equals("GGA")){
					AAFragment+="G";
				}else if(code.equals("GGG")){
					AAFragment+="G";
				}else{	//unidentified recognize as stop codon
					AAFragment+="X";
				}
			}
			AAFragments.add(AAFragment);
		}
		String result="";
		for(int z=0;z<AAFragments.size();z++){	//merge all together
			result+=AAFragments.get(z);
		}

		AA=result;
		return result;
	}
}
