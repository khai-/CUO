package Support;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

public class WeightTable{
	public int[] codonFrequency;
	public float[] weight;
	public PdfPTable table;

	public WeightTable(){}	//manual creation and loading

	public WeightTable(int[] codonFrequency,Document pdf)throws DocumentException{
		this.codonFrequency=codonFrequency;
		weight=getWeight(codonFrequency);
		table=setTable(weight);
		pdf.add(table);
	}

	public WeightTable(float[] weight,Document pdf)throws DocumentException{
		this.weight=weight;
		table=setTable(weight);
		pdf.add(table);
	}

	public static float[] getWeight(ArrayList<int[]> codonFrequencyList){	//get weight value from a list of codonFrequency[]
		//merge all codon frequency into one
		int[] codonFrequency=new int[65];
		for(int i=0;i<codonFrequencyList.size();i++){
			for(int j=0;j<codonFrequencyList.get(i).length;j++){
				codonFrequency[j]+=codonFrequencyList.get(i)[j];
			}
		}

		return getWeight(codonFrequency);
	}

	public static float[] getWeight(int[] codonFrequency){	//return float[64] weight from given int[65] codonFrequency: weight=Xij/Ximax
		float[] weight=new float[64];
		int Xij;	//occurence of i codon of j amino acid
		int Ximax;	//highest codon occurence of j amino acid
		//Phenylalanine: 2 codons
		Ximax=Math.max(codonFrequency[0],codonFrequency[1]);
		for(int i=0;i<2;i++){	//[0] and [1]
			Xij=codonFrequency[i];
			weight[i]=(float)Xij/Ximax;
		}
		//Leucine: 2 codons + 4 codons
		Ximax=Math.max(codonFrequency[2],codonFrequency[3]);
		Ximax=Math.max(Ximax,codonFrequency[4]);
		Ximax=Math.max(Ximax,codonFrequency[5]);
		Ximax=Math.max(Ximax,codonFrequency[6]);
		Ximax=Math.max(Ximax,codonFrequency[7]);
		for(int i=2;i<8;i++){	//[2] [3] [4] [5] [6] and [7]
			Xij=codonFrequency[i];
			weight[i]=(float)Xij/Ximax;
		}
		//Isoleucine: 3 codons
		Ximax=Math.max(codonFrequency[8],codonFrequency[9]);
		Ximax=Math.max(Ximax,codonFrequency[10]);
		for(int i=8;i<11;i++){	//[8] [9] and [10]
			Xij=codonFrequency[i];
			weight[i]=(float)Xij/Ximax;
		}
		//Methionine is neglected because it only has absolute single codon: 1 codon
		weight[11]=1;
		//Valine: 4 codons
		Ximax=Math.max(codonFrequency[12],codonFrequency[13]);
		Ximax=Math.max(Ximax,codonFrequency[14]);
		Ximax=Math.max(Ximax,codonFrequency[15]);
		for(int i=12;i<16;i++){	//[12] [13] [14] and [15]
			Xij=codonFrequency[i];
			weight[i]=(float)Xij/Ximax;
		}
		//Serine: 4 codons + 2 codons
		Ximax=Math.max(codonFrequency[16],codonFrequency[17]);
		Ximax=Math.max(Ximax,codonFrequency[18]);
		Ximax=Math.max(Ximax,codonFrequency[19]);
		Ximax=Math.max(Ximax,codonFrequency[56]);
		Ximax=Math.max(Ximax,codonFrequency[57]);
		for(int i=16;i<20;i++){	//[16] [17] [18] and [19]
			Xij=codonFrequency[i];
			weight[i]=(float)Xij/Ximax;
		}
		for(int i=56;i<58;i++){	//[56] and [57]
			Xij=codonFrequency[i];
			weight[i]=(float)Xij/Ximax;
		}
		//Proline: 4 codons
		Ximax=Math.max(codonFrequency[20],codonFrequency[21]);
		Ximax=Math.max(Ximax,codonFrequency[22]);
		Ximax=Math.max(Ximax,codonFrequency[23]);
		for(int i=20;i<24;i++){	//[20] [21] [22] and [23]
			Xij=codonFrequency[i];
			weight[i]=(float)Xij/Ximax;
		}
		//Threonine: 4 codons
		Ximax=Math.max(codonFrequency[24],codonFrequency[25]);
		Ximax=Math.max(Ximax,codonFrequency[26]);
		Ximax=Math.max(Ximax,codonFrequency[27]);
		for(int i=24;i<28;i++){	//[24] [25] [26] and [27]
			Xij=codonFrequency[i];
			weight[i]=(float)Xij/Ximax;
		}
		//Alanine: 4 codons
		Ximax=Math.max(codonFrequency[28],codonFrequency[29]);
		Ximax=Math.max(Ximax,codonFrequency[30]);
		Ximax=Math.max(Ximax,codonFrequency[31]);
		for(int i=28;i<32;i++){	//[28] [29] [30] and [31]
			Xij=codonFrequency[i];
			weight[i]=(float)Xij/Ximax;
		}
		//Tyrosine: 2 codons
		Ximax=Math.max(codonFrequency[32],codonFrequency[33]);
		for(int i=32;i<34;i++){	//[32] and [33]
			Xij=codonFrequency[i];
			weight[i]=(float)Xij/Ximax;
		}
		//STOP codon is neglected because it is not translated: 3 codons
		weight[34]=1;
		weight[35]=1;
		weight[50]=1;
		//Histidine: 2 codons
		Ximax=Math.max(codonFrequency[36],codonFrequency[37]);
		for(int i=36;i<38;i++){	//[36] and [37]
			Xij=codonFrequency[i];
			weight[i]=(float)Xij/Ximax;
		}
		//Glutamine: 2 codons
		Ximax=Math.max(codonFrequency[38],codonFrequency[39]);
		for(int i=38;i<40;i++){	//[38] and [39]
			Xij=codonFrequency[i];
			weight[i]=(float)Xij/Ximax;
		}
		//Asparagine: 2 codons
		Ximax=Math.max(codonFrequency[40],codonFrequency[41]);
		for(int i=40;i<42;i++){	//[40] and [41]
			Xij=codonFrequency[i];
			weight[i]=(float)Xij/Ximax;
		}
		//Lysine: 2 codons
		Ximax=Math.max(codonFrequency[42],codonFrequency[43]);
		for(int i=42;i<44;i++){	//[42] and [43]
			Xij=codonFrequency[i];
			weight[i]=(float)Xij/Ximax;
		}
		//Aspartic Acid: 2 codons
		Ximax=Math.max(codonFrequency[44],codonFrequency[45]);
		for(int i=44;i<46;i++){	//[44] and [45]
			Xij=codonFrequency[i];
			weight[i]=(float)Xij/Ximax;
		}
		//Glutamic Acid: 2 codons
		Ximax=Math.max(codonFrequency[46],codonFrequency[47]);
		for(int i=46;i<48;i++){	//[46] and [47]
			Xij=codonFrequency[i];
			weight[i]=(float)Xij/Ximax;
		}
		//Cysteine: 2 codons
		Ximax=Math.max(codonFrequency[48],codonFrequency[49]);
		for(int i=48;i<50;i++){	//[48] and [49]
			Xij=codonFrequency[i];
			weight[i]=(float)Xij/Ximax;
		}
		//Tryptophan is neglected because it only has absolute single codon: 1 codon
		weight[51]=1;
		//Arginine: 4 codons + 2 codons
		Ximax=Math.max(codonFrequency[52],codonFrequency[53]);
		Ximax=Math.max(Ximax,codonFrequency[54]);
		Ximax=Math.max(Ximax,codonFrequency[55]);
		Ximax=Math.max(Ximax,codonFrequency[58]);
		Ximax=Math.max(Ximax,codonFrequency[59]);
		for(int i=52;i<56;i++){	//[52] [53] [54] and [55]
			Xij=codonFrequency[i];
			weight[i]=(float)Xij/Ximax;
		}
		for(int i=58;i<60;i++){	//[58] and [59]
			Xij=codonFrequency[i];
			weight[i]=(float)Xij/Ximax;
		}
		//Glycine: 4 codons
		Ximax=Math.max(codonFrequency[60],codonFrequency[61]);
		Ximax=Math.max(Ximax,codonFrequency[62]);
		Ximax=Math.max(Ximax,codonFrequency[63]);
		for(int i=60;i<64;i++){	//[60] [61] [62] and [63]
			Xij=codonFrequency[i];
			weight[i]=(float)Xij/Ximax;
		}

		for(int i=0;i<64;i++){	//eliminate NaN as 0 in the weight
			if(Double.isNaN(weight[i])){	//NaN means to entire batch of codon siblings never appeared
				weight[i]=0;
			}
		}
		return weight;
	}

	public static float[] getGWeight(float[] weight,ArrayList<int[]> codonFrequencyList){	//get gWeight[64] for gCAI calculation from give weight[64]: gWeight=weight*(Si/S)
		float[] gWeight=new float[64];
		int Si;	//number of genes in S that contain at least one occurence of codon
		int S=codonFrequencyList.size();	//number of genes
		for(int i=0;i<weight.length;i++){
			//get Si
			Si=0;
			for(int j=0;j<codonFrequencyList.size();j++){	//iterate through every genes
				if(codonFrequencyList.get(j)[i]>0){	//there is that codon present in the gene
					Si+=1;
				}
			}

			//calculate gWeight
			gWeight[i]=weight[i]*((float)Si/S);
		}

		return gWeight;
	}

	public static PdfPTable setTable(float[] weight){	//get a pdf table representation of the given weight
		//the font used
		final Font font8=new Font(FontFamily.HELVETICA,8);
		//initiate table
		PdfPTable weightTable=new PdfPTable(12);
		weightTable.setSpacingBefore(5f);
		weightTable.setKeepTogether(true);
		weightTable.setWidthPercentage(75f);
		//description cell
		PdfPCell keyCell=new PdfPCell(new Phrase("Amino acid | Codon | Weight",font8));
		keyCell.setColspan(12);
		weightTable.addCell(keyCell);
		//generate color used for each amino acid
		BaseColor pheColor=new BaseColor(197,217,241);
		BaseColor serColor=BaseColor.YELLOW;
		BaseColor tyrColor=new BaseColor(0,112,192);
		BaseColor cysColor=new BaseColor(247,150,70);
		BaseColor leuColor=new BaseColor(234,241,221);
		BaseColor stopColor=BaseColor.WHITE;
		BaseColor trpColor=new BaseColor(255,0,255);
		BaseColor proColor=new BaseColor(146,208,80);
		BaseColor hisColor=new BaseColor(221,217,195);
		BaseColor glnColor=new BaseColor(250,192,144);
		BaseColor argColor=new BaseColor(216,216,216);
		BaseColor ileColor=new BaseColor(242,221,220);
		BaseColor thrColor=new BaseColor(0,176,80);
		BaseColor asnColor=new BaseColor(178,161,199);
		BaseColor lysColor=new BaseColor(148,139,84);
		BaseColor metColor=BaseColor.RED;
		BaseColor valColor=new BaseColor(255,192,0);
		BaseColor alaColor=new BaseColor(0,176,240);
		BaseColor aspColor=new BaseColor(102,255,51);
		BaseColor gluColor=new BaseColor(0,255,255);
		BaseColor glyColor=new BaseColor(182,221,232);
		BaseColor[] color={pheColor,serColor,tyrColor,cysColor,pheColor,serColor,tyrColor,cysColor,leuColor,serColor,stopColor,stopColor,leuColor, serColor,stopColor,trpColor,leuColor,proColor,hisColor,argColor,leuColor,proColor,hisColor,argColor,leuColor,proColor, glnColor,argColor,leuColor,proColor,glnColor,argColor,ileColor,thrColor,asnColor,serColor,ileColor,thrColor,asnColor, serColor,ileColor,thrColor,lysColor,argColor,metColor,thrColor,lysColor,argColor,valColor,alaColor,aspColor,glyColor, valColor,alaColor,aspColor,glyColor,valColor,alaColor,gluColor,glyColor,valColor,alaColor,gluColor,glyColor};
		//amino acid
		String[] aa={"Phe@2","Ser@4","Tyr@2","Cys@2",null,null,null,null,"Leu@2",null,"STOP@2","STOP@1",null,null,null,"Trp@1","Leu@4","Pro@4", "His@2","Arg@4",null,null,null,null,null,null,"Gln@2",null,null,null,null,null,"ile@3","Thr@4","Asn@2","Ser@2",null,null, null,null,null,null,"Lys@2","Arg@2","Met@1",null,null,null,"Val@4","Ala@4","Asp@2","Gly@4",null,null,null,null,null,null, "Glu@2",null,null,null,null,null};
		//codon cell
		String[] codon={"UUU","UCU","UAU","UGU","UUC","UCC","UAC","UGC","UUA","UCA","UAA","UGA","UUG","UCG","UAG","UGG","CUU","CCU","CAU","CGU", "CUC","CCC","CAC","CGC","CUA","CCA","CAA","CGA","CUG","CCG","CAG","CGG","AUU","ACU","AAU","AGU","AUC","ACC","AAC","AGC", "AUA","ACA","AAA","AGA","AUG","ACG","AAG","AGG","GUU","GCU","GAU","GGU","GUC","GCC","GAC","GGC","GUA","GCA","GAA","GGA", "GUG","GCG","GAG","GGG"};
		//weight value coordination(adjust from vertical array arrangement to horizontal cell adding pattern)
		float[] number=new float[64];
		for(int j=0;j<16;j++){
			for(int k=0;k<4;k++){
				number[j*4+k]=weight[j+k*16];
				if(Double.isNaN(number[j*4+k])){//NaN means to entire batch of codon siblings never appeared
					number[j*4+k]=0;
				}
			}
		}
		//generate codon frequency table in pdf
		DecimalFormat df=new DecimalFormat("0.###");
		df.setRoundingMode(RoundingMode.HALF_UP);
		for(int j=0;j<64;j++){ //show 64 codons weights
			if(aa[j]!=null){
				PdfPCell aaCell=new PdfPCell(new Phrase(aa[j].split("@")[0],font8));
				aaCell.setRowspan(Integer.parseInt(aa[j].split("@")[1]));
				aaCell.setBackgroundColor(color[j]);
				weightTable.addCell(aaCell);
			}
			PdfPCell codonCell=new PdfPCell(new Phrase(codon[j],font8));
			codonCell.setBackgroundColor(color[j]);
			weightTable.addCell(codonCell);
			if(j==44||j==10||j==14||j==11||j==15){	//ignore these weights as they are not included in calculation
				PdfPCell weightCell=new PdfPCell(new Phrase("---",font8));
				weightCell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
				weightCell.setBackgroundColor(color[j]);
				weightTable.addCell(weightCell);
			}else{
				PdfPCell weightCell=new PdfPCell(new Phrase(df.format(number[j]),font8));
				weightCell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
				weightCell.setBackgroundColor(color[j]);
				weightTable.addCell(weightCell);
			}
		}

		return weightTable;
	}

	//read *.wt to importedWeight[64]
	public static float[] read(File weightFile){
		try{
			BufferedReader br=new BufferedReader(new FileReader(weightFile));
			float[] importedWeight=new float[64];
			for(int i=0;i<64;i++){
				importedWeight[i]=Float.parseFloat(br.readLine());
			}	
			br.close();
			return importedWeight;
		}catch(Exception e){
			return null;
		}
	}
}
