package Support;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

import Support.parser.GenBank;
import Support.parser.Sequence;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

public class CodonFrequencyTable{
	GenBank genBank;
	ArrayList<Sequence> proteinList;
	int geneNum=0;
	int totalCodonNum=0;
	public int[] codonFrequency=new int[65];	//one extra for unidentified
	Paragraph title;
	Paragraph header;
	PdfPTable table;
	Paragraph footer;
	final Font font10=new Font(FontFamily.HELVETICA,10);	//defaultly used for this analysis
	final Font font8=new Font(FontFamily.HELVETICA,8);

	public CodonFrequencyTable(){}	//manual creation and loading

	public CodonFrequencyTable(GenBank genBank,Document pdf)throws DocumentException,NullPointerException{	//table from GenBank
		initialize(genBank);
		setTitle();
		setHeader();
		setTable();
		setFooter();
		show(pdf);
	}

	public CodonFrequencyTable(ArrayList<Sequence> sequences,Document pdf)throws DocumentException,NullPointerException{	//table from list of genes
		initialize(sequences);
		//no need to set title
		setHeader();
		setTable();
		setFooter();
		show(pdf);
	}

	public CodonFrequencyTable(Sequence sequence,Document pdf)throws DocumentException,NullPointerException{	//table from single gene
		this(new ArrayList<Sequence>(Arrays.asList(sequence)),pdf);	//this must be first statement,so be the clumsy inline
	}

	public void initialize(ArrayList<Sequence> sequences){
		proteinList=sequences;
		geneNum=proteinList.size();
	}

	public void initialize(GenBank genBank){	//initialize some commonly used variables
		this.genBank=genBank;

		proteinList=genBank.getSequencesByType("CDS");
		geneNum=proteinList.size();
	}

	public Paragraph setTitle(){
		title=new Paragraph("\n",font10);
		title.add(new Phrase(genBank.name+":",new Font(FontFamily.HELVETICA,10,Font.UNDERLINE,BaseColor.BLUE)));
		//type of analysis
		title.add(new Phrase("\nComplete genome analysis..."));
		return title;
	}

	public Paragraph setHeader(){
		header=new Paragraph("",font10);
		//seqLength and number of protein genes
		int seqLength=0;
		for(int i=0;i<proteinList.size();i++){
			for(int j=0;j<proteinList.get(i).interval.size();j++){
				int[] site=proteinList.get(i).interval.get(j);
				seqLength+=Math.abs(site[1]-site[0])+1;
			}
		}
		header.add(new Phrase("Sequence length: "+seqLength));
		header.add(new Phrase("      Number of protein genes: "+geneNum+"      "));
		//total number of codons
		for(int j=0;j<geneNum;j++){
			String[] geneSequence=proteinList.get(j).getSequence(proteinList.get(j).genBank.completeSequence);
			int totalLength=0;
			for(int k=0;k<geneSequence.length;k++){
				totalLength+=geneSequence[k].length();
			}
			totalCodonNum+=totalLength/3;
		}
		header.add(new Phrase("Total codons: "+totalCodonNum));
		return header;
	}

	public PdfPTable setTable(){
		//count codon frequency of every gene
		if(genBank!=null){
			codonFrequency=getCodonFrequency();
		}else{
			for(int i=0;i<proteinList.size();i++){
				for(int j=0;j<codonFrequency.length;j++){
					codonFrequency[j]+=proteinList.get(i).codonFrequency[j];
				}
			}
		}
		//tabulate codon frequency result
		PdfPTable codonTable=new PdfPTable(16);
		codonTable.setSpacingBefore(5f);
		codonTable.setKeepTogether(true);
		codonTable.setWidthPercentage(100f);
		PdfPCell keyCell=new PdfPCell(new Phrase("Amino acid | Codon | Number | Frequency per thousand",font8));
		keyCell.setColspan(16);
		codonTable.addCell(keyCell);
		//generate codon table's pattern
		//color
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
		//codon number
		int[] number=new int[64];
		for(int j=0;j<16;j++){
			for(int k=0;k<4;k++){
				number[j*4+k]=codonFrequency[j+k*16];
			}
		}
		//generate codon frequency table in pdf
		DecimalFormat df=new DecimalFormat("###0.0");
		df.setRoundingMode(RoundingMode.HALF_UP);
		for(int j=0;j<64;j++){ //show 64 codons, unidentified codon is shown separately
			if(aa[j]!=null){
				PdfPCell aaCell=new PdfPCell(new Phrase(aa[j].split("@")[0],font8));
				aaCell.setRowspan(Integer.parseInt(aa[j].split("@")[1]));
				aaCell.setBackgroundColor(color[j]);
				codonTable.addCell(aaCell);
			}
			PdfPCell codonCell=new PdfPCell(new Phrase(codon[j],font8));
			codonCell.setBackgroundColor(color[j]);
			codonTable.addCell(codonCell);
			PdfPCell numberCell=new PdfPCell(new Phrase(Integer.toString(number[j]),font8));
			numberCell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
			numberCell.setBackgroundColor(color[j]);
			codonTable.addCell(numberCell);
			PdfPCell frequencyCell=new PdfPCell(new Phrase(df.format(number[j]/(totalCodonNum/1000f)),font8));
			frequencyCell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
			frequencyCell.setBackgroundColor(color[j]);
			codonTable.addCell(frequencyCell);
		}
		table=codonTable;
		return table;
	}

	public Paragraph setFooter(){
		footer=new Paragraph("",font10);

		Phrase unidentifiedPara=new Phrase(12,codonFrequency[64]+" unidentified codons.");
		footer.add(unidentifiedPara);

		//count nucleotide frequency,include overlapped genes
		int A=0;
		int U=0;
		int G=0;
		int C=0;
		int X=0;
		for(int j=0;j<geneNum;j++){
			//concat all sites
			String[] geneTranscript=proteinList.get(j).getTranscript(proteinList.get(j).genBank.completeSequence);
			StringBuilder builder=new StringBuilder();
			for(int k=0;k<geneTranscript.length;k++){
				builder.append(geneTranscript[k]);
			}
			String combinedTrans=builder.toString();
			for(int k=0;k<combinedTrans.length();k++){
				if(combinedTrans.charAt(k)=='A'){
					A+=1;
				}else if(combinedTrans.charAt(k)=='T'){
					U+=1;
				}else if(combinedTrans.charAt(k)=='G'){
					G+=1;
				}else if(combinedTrans.charAt(k)=='C'){
					C+=1;
				}else{	//unidentified sequence
					X+=1;
				}
			}
		}
		footer.add(new Paragraph("\nNucleotide frequency:   A="+A+"   U="+U+"   G="+G+"   C="+C+"  X="+X,font10));
		//calculate GC percentage
		DecimalFormat GCformat=new DecimalFormat("###.##");
		GCformat.setRoundingMode(RoundingMode.HALF_UP);
		float totalGC=((G+C)/(float)(A+U+G+C+X))*100f;
		int firstGCNumber=0;
		for(int j=0;j<8;j++){	//retrieve relevant codon freq
			for(int k=0;k<4;k++){
				firstGCNumber+=codonFrequency[4+j*8+k];
			}
		}
		float firstGC=((firstGCNumber)/(float)totalCodonNum)*100f;
		int secondGCNumber=0;
		for(int j=0;j<2;j++){
			for(int k=0;k<16;k++){
				secondGCNumber+=codonFrequency[16+j*32+k];
			}
		}
		float secondGC=((secondGCNumber)/(float)totalCodonNum)*100f;
		int thirdGCNumber=0;
		for(int j=0;j<32;j++){
			thirdGCNumber+=codonFrequency[1+j*2];
		}
		float thirdGC=((thirdGCNumber)/(float)totalCodonNum)*100f;
		footer.add(new Paragraph("GC Content: "+GCformat.format(totalGC)+"%    1st letter GC: "+GCformat.format(firstGC)+"%    2nd letter GC: "+GCformat.format(secondGC)+"%    3rd letter GC: "+GCformat.format(thirdGC)+"%",font10));

		return footer;
	}

	public void show(Document pdf)throws DocumentException{
		if(title!=null){
			pdf.add(title);
		}
		pdf.add(header);
		pdf.add(table);
		pdf.add(footer);
	}

	public int[] getCodonFrequency(){
		return getCodonFrequency(proteinList,genBank.completeSequence);
	}

	public static int[] getCodonFrequency(ArrayList<Sequence> proteinList,String completeSequence){	//static method that returns collective codon frequency int[65]
		//count codon frequency of every gene
		int[] codonFrequency=new int[65];
		for(int j=0;j<proteinList.size();j++){
			proteinList.get(j).codonFrequency=proteinList.get(j).getCodonFrequency(completeSequence);
			for(int k=0;k<proteinList.get(j).codonFrequency.length;k++){	//add the codon frequency to total codon frequency
				codonFrequency[k]+=proteinList.get(j).codonFrequency[k];
			}
		}
		return codonFrequency;
	}

	public void addTable(CodonFrequencyTable table){	//add another table's data into this one(useful in creating summary table)
		if(this.proteinList==null){
			this.proteinList=new ArrayList<Sequence>();
		}
		this.proteinList.addAll(table.proteinList);
		this.geneNum+=table.geneNum;
	}

	public void export(String fileName){
		try{
			File cftFile=new File(fileName+".cft");
			BufferedWriter bw=new BufferedWriter(new FileWriter(cftFile));
			for(int i=0;i<codonFrequency.length;i++){
				bw.write(Integer.toString(codonFrequency[i])+"\n");
			}
			bw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static int[] read(File f){
		try{
			BufferedReader br=new BufferedReader(new FileReader(f));
			int[] cft=new int[64];
			for(int i=0;i<64;i++){
				cft[i]=Integer.parseInt(br.readLine());
			}		
			br.close();
			return cft;
		}catch(Exception e){
			return null;
		}
	}
}
