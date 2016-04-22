package Support;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

import Support.parser.GenBank;
import Support.parser.Sequence;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.DefaultFontMapper;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;

public class CodonPairCalculator{
	int[][] codonPairFrequency=new int[64][64];
	public float[][] codonPairTable;

	static final Font font10=new Font(FontFamily.HELVETICA,10);	//defaultly used for this analysis
	final Font font8=new Font(FontFamily.HELVETICA,8);	//for table content

	public static int[][] getCodonPairFrequency(ArrayList<Sequence> proteinList){
		int[][] summaryCodonPairFrequency=new int[64][64];
		for(int i=0;i<proteinList.size();i++){	//iterate over all the proteins
			add(summaryCodonPairFrequency,getCodonPairFrequency(proteinList.get(i)));
		}

		return summaryCodonPairFrequency;
	}
	public static int[][] getCodonPairFrequency(Sequence protein){
		if(protein.codonPairFrequency!=null)return protein.codonPairFrequency;

		String[] arrprotSeq=protein.getTranscript(protein.genBank.completeSequence);
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
		protein.codonPairFrequency=theCodonPairFrequency;
		return theCodonPairFrequency;
	}

	public float[][] getCodonPairTable(ArrayList<Sequence> proteinList){
		if(codonPairTable!=null){
			return codonPairTable;
		}

		int[][] theCodonPairFrequency;
		if(proteinList!=null){
			theCodonPairFrequency=getCodonPairFrequency(proteinList);
		}else{
			theCodonPairFrequency=codonPairFrequency;
			ArrayList<ArrayList<Integer>> grouping=new ArrayList<ArrayList<Integer>>();
			for(int i=0;i<21;i++){
				grouping.add(new ArrayList<Integer>());
			}
			grouping.get(0).add(0);
			grouping.get(0).add(1);
			grouping.get(1).add(2);
			grouping.get(1).add(3);
			grouping.get(1).add(4);
			grouping.get(1).add(5);
			grouping.get(1).add(6);
			grouping.get(1).add(7);
			grouping.get(2).add(8);
			grouping.get(2).add(9);
			grouping.get(2).add(10);
			grouping.get(3).add(11);
			grouping.get(4).add(12);
			grouping.get(4).add(13);
			grouping.get(4).add(14);
			grouping.get(4).add(15);
			grouping.get(5).add(16);
			grouping.get(5).add(17);
			grouping.get(5).add(18);
			grouping.get(5).add(19);
			grouping.get(6).add(20);
			grouping.get(6).add(21);
			grouping.get(6).add(22);
			grouping.get(6).add(23);
			grouping.get(7).add(24);
			grouping.get(7).add(25);
			grouping.get(7).add(26);
			grouping.get(7).add(27);
			grouping.get(8).add(28);
			grouping.get(8).add(29);
			grouping.get(8).add(30);
			grouping.get(8).add(31);
			grouping.get(9).add(32);
			grouping.get(9).add(33);
			grouping.get(10).add(34);
			grouping.get(10).add(35);
			grouping.get(11).add(36);
			grouping.get(11).add(37);
			grouping.get(12).add(38);
			grouping.get(12).add(39);
			grouping.get(13).add(40);
			grouping.get(13).add(41);
			grouping.get(14).add(42);
			grouping.get(14).add(43);
			grouping.get(15).add(44);
			grouping.get(15).add(45);
			grouping.get(16).add(46);
			grouping.get(16).add(47);
			grouping.get(17).add(48);
			grouping.get(17).add(49);
			grouping.get(10).add(50);
			grouping.get(18).add(51);
			grouping.get(19).add(52);
			grouping.get(19).add(53);
			grouping.get(19).add(54);
			grouping.get(19).add(55);
			grouping.get(5).add(56);
			grouping.get(5).add(57);
			grouping.get(19).add(58);
			grouping.get(19).add(59);
			grouping.get(20).add(60);
			grouping.get(20).add(61);
			grouping.get(20).add(62);
			grouping.get(20).add(63);
			int[][] highestFrequency=new int[21][21];
			//get the highest occurence of each group
			for(int i=0;i<21;i++){
				for(int j=0;j<21;j++){
					int highest=0;
					for(int k=0;k<grouping.get(i).size();k++){
						for(int m=0;m<grouping.get(j).size();m++){
							int coord1=grouping.get(i).get(k);//first codon coord
							int coord2=grouping.get(j).get(m);//second codon coord
							if(codonPairFrequency[coord1][coord2]>highest){
								highest=codonPairFrequency[coord1][coord2];
							}
						}
					}
					highestFrequency[i][j]=highest;
				}
			}
			//decode the highest to [64][64] format
			int[][] highestFrequency64=new int[64][64];
			for(int i=0;i<21;i++){
				for(int j=0;j<21;j++){
					for(int k=0;k<grouping.get(i).size();k++){
						for(int m=0;m<grouping.get(j).size();m++){
							int coord1=grouping.get(i).get(k);
							int coord2=grouping.get(j).get(m);
							highestFrequency64[coord1][coord2]=highestFrequency[i][j];
						}
					}
				}
			}
			//use the highest to normalize all pairs
			codonPairTable=new float[64][64];
			for(int i=0;i<64;i++){
				for(int j=0;j<64;j++){
					codonPairTable[i][j]=(float)theCodonPairFrequency[i][j]/highestFrequency64[i][j];
					if(Float.isNaN(codonPairTable[i][j])){	//zero divided by something
						codonPairTable[i][j]=(float)0;
					}
				}
			}
		}

		return codonPairTable;
	}

	public static double getCPI(float[][] codonPairTable,int[][] codonPairFrequency){	//return CPI of a gene
		if(codonPairTable==null)return -1;

		double power=0;	//to store the power value of exponential
		int length=0;	//count the number of codon pairs
		for(int i=0;i<64;i++){	//iterate through every codon pair
			//skip STOP:NNN,STOP:STOP and NNN:STOP
			if(i==34||i==35||i==50)continue;
			for(int j=0;j<64;j++){
				//skip STOP:NNN,STOP:STOP and NNN:STOP
				if(j==34||j==35||j==50)continue;

				length+=codonPairFrequency[i][j];
				boolean wasZero=false;
				if(codonPairTable[i][j]==0||Double.isNaN(codonPairTable[i][j])){	//weight value must not be zero or NaN, this will mess up the result
					codonPairTable[i][j]=(float)0.01;
					wasZero=true;
				}
				if(codonPairFrequency[i][j]!=0){
					power+=Math.log(codonPairTable[i][j])*codonPairFrequency[i][j];
				}

				//put the zero CP weight back to zero to avoid confusion
				if(wasZero)codonPairTable[i][j]=0;
			}
		}

		return Math.exp(power/length);
	}

	public void add(int[][] theCodonPairFrequency){	//add the codonPairFrequency table into the one in this
		add(codonPairFrequency,theCodonPairFrequency);
	}
	public static void add(int[][] summaryCodonPairFrequency,int[][] theCodonPairFrequency){	//add the codonPairFrequency into the summary one
		for(int i=0;i<64;i++){
			for(int j=0;j<64;j++){
				summaryCodonPairFrequency[i][j]+=theCodonPairFrequency[i][j];
			}
		}
	}

	public static Paragraph getCPI(float[][] codonPairTable,GenBank genBank){	//return CPI results list from a genBank
		ArrayList<Sequence> proteinList=genBank.getSequencesByType("CDS");
		Paragraph cpiPara=new Paragraph("",font10);
		//calculate CPI of every proteins
		for(int i=0;i<proteinList.size();i++){
			proteinList.get(i).CPI=getCPI(codonPairTable,proteinList.get(i).getCodonPairFrequency());	//no presenting codon pair frequency table of target sequences
		}
		//sort CPI list from low CPI to high CPI
		Collections.sort(proteinList,new Comparator<Sequence>(){
			public int compare(Sequence sequenceOne,Sequence sequenceTwo){
				if(sequenceOne.CPI==sequenceTwo.CPI){
					return 0;
				}else if(sequenceOne.CPI>sequenceTwo.CPI){
					return 1;
				}else{
					return -1;
				}
			}
		});
		//bake sorted CPI list into a dotted list
		cpiPara.add(new Phrase(genBank.name+"\n",new Font(FontFamily.HELVETICA,10,Font.UNDERLINE,BaseColor.BLUE))); //header
		DecimalFormat df=new DecimalFormat("0.000");
		df.setRoundingMode(RoundingMode.HALF_UP);
		Chunk dottedTab=new Chunk(new DottedLineSeparator(),400,true);
		for(int i=0;i<proteinList.size();i++){	//the dotted list
			Sequence protein=proteinList.get(i);
			if(protein.product!=null) cpiPara.add(new Chunk(protein.gene+"("+protein.product+")"));
			else cpiPara.add(new Chunk(protein.gene));
			cpiPara.add(new Chunk(dottedTab));
			cpiPara.add(new Chunk("(CPI="+df.format(protein.CPI)+")\n"));
		}

		//summary CPI
		int[][] summaryCodonPairFrequency=new int[64][64];
		for(int i=0;i<proteinList.size();i++){
			add(summaryCodonPairFrequency,proteinList.get(i).getCodonPairFrequency());
		}
		double summaryCPI=getCPI(codonPairTable,summaryCodonPairFrequency);
		cpiPara.add(new Phrase("\nCodon Pair Index = "+df.format(summaryCPI)+"\n"));

		//set some properties of the paragraph
		cpiPara.setSpacingBefore(10f);
		cpiPara.setSpacingAfter(10f);

		return cpiPara;
	}

	public static Paragraph getCPI(float[][] codonPairTable,Sequence protein,PdfWriter writer)throws BadElementException{	//return CPI result and codon pair weight distribution graph from single protein
		if(codonPairTable==null)return null;

		Paragraph cpiPara=new Paragraph("",font10);

		//get weight of every codons
		String[] sequenceArray=protein.getTranscript(protein.genBank.completeSequence);
		String codons="";
		for(int i=0;i<sequenceArray.length;i++){	//attenuate all the codons
			codons+=sequenceArray[i];
		}
		int codonPairNum=codons.length()/3-1;	//total codon pair numbers
		XYSeries series=new XYSeries("Codon Pair Weight Distribution");
		for(int i=0;i<codonPairNum;i++){
			String firstCodon=codons.substring(i*3,i*3+3);
			int firstCodonCoord=Common.getCodonCoordinate(firstCodon);
			String secondCodon=codons.substring((i+1)*3,(i+1)*3+3);
			int secondCodonCoord=Common.getCodonCoordinate(secondCodon);
			series.add(i+1d,codonPairTable[firstCodonCoord][secondCodonCoord]);
		}
		XYSeriesCollection dataset=new XYSeriesCollection();
		dataset.addSeries(series);

		//make graph of codon pair weight vs sequence
		JFreeChart chart=ChartFactory.createScatterPlot(null,"Codon","Weight",dataset,PlotOrientation.VERTICAL,false,true,false);
		chart.setBackgroundPaint(Color.WHITE);
		XYPlot plot=chart.getXYPlot();
		plot.setBackgroundPaint(Color.WHITE);
		//customize the range axis
		NumberAxis rangeAxis=(NumberAxis)plot.getRangeAxis();
		rangeAxis.setNumberFormatOverride(new DecimalFormat("0.##"));
		//customize the shape of points
		XYLineAndShapeRenderer renderer=(XYLineAndShapeRenderer)plot.getRenderer();
		renderer.setSeriesShape(0,ShapeUtilities.createDiamond(1f));
		//draw chart into graphic
		int width=464;
		int height=250;
		PdfContentByte contentByte=writer.getDirectContent();
		PdfTemplate template=contentByte.createTemplate(width,height);
		Graphics2D g2d=template.createGraphics(width,height,new DefaultFontMapper());
		Rectangle2D r2d=new Rectangle2D.Double(0,0,width,height);
		chart.draw(g2d,r2d,null);
		g2d.dispose();
		Image graphImage=Image.getInstance(template);
		//insert into a table cell to layout properly
		PdfPTable graphTable=new PdfPTable(1);
		graphTable.setSpacingBefore(5f);
		PdfPCell graphCell=new PdfPCell(graphImage);
		graphCell.setBorder(0);
		graphTable.addCell(graphCell);
		graphTable.setWidthPercentage(100f);

		//create codon pair weight distribution diagram
		PdfPTable distributionDiagram=createWeightDistributionDiagram(codonPairTable,codons);

		//calculate CPI of the gene
		double summaryCPI=getCPI(codonPairTable,protein.getCodonPairFrequency());

		//cook the paragraph
		if(protein.product==null)cpiPara.add(new Phrase(protein.gene,new Font(FontFamily.HELVETICA,10,Font.UNDERLINE,BaseColor.BLUE)));	//header
		else cpiPara.add(new Phrase(protein.gene+"("+protein.product+")",new Font(FontFamily.HELVETICA,10,Font.UNDERLINE,BaseColor.BLUE)));
		cpiPara.add(graphTable);	//the graph
		cpiPara.add(distributionDiagram);	//the weight distribution diagram
		DecimalFormat df=new DecimalFormat("0.000");
		df.setRoundingMode(RoundingMode.HALF_UP);
		cpiPara.add(new Phrase("Codon Pair Index = "+df.format(summaryCPI)));

		//set some properties of the paragraph
		cpiPara.setSpacingBefore(10f);
		cpiPara.setSpacingAfter(10f);

		return cpiPara;
	}

	//generate codon pair weight distribution diagram
	public static PdfPTable createWeightDistributionDiagram(float[][] codonPairTable,String codons){
		int codonPairNum=codons.length()/3-1;
		PdfPTable distributionDiagram=new PdfPTable(codonPairNum);

		//assimilate diagram
		int[][] weightCoordination=new int[codonPairNum][2];	//turn codon pairs into coordinates
		for(int i=0;i<codonPairNum;i++){
			weightCoordination[i][0]=Common.getCodonCoordinate(codons.substring(i*3,i*3+3));
			weightCoordination[i][1]=Common.getCodonCoordinate(codons.substring((i+1)*3,(i+1)*3+3));
		}
		//lower weight,darker the color;higher weight,lighter the color
		for(int i=0;i<codonPairNum;i++){
			PdfPCell theCell=new PdfPCell();
			theCell.setFixedHeight(10f);
			if(weightCoordination[i][0]!=-1&&weightCoordination[i][1]!=-1){
				int colorValue=(int)(codonPairTable[weightCoordination[i][0]][weightCoordination[i][1]]*255);
				theCell.setBackgroundColor(new BaseColor(colorValue,colorValue,colorValue));
			}else{	//unidentified codon
				theCell.setBackgroundColor(BaseColor.RED);
			}
			if(i!=0){	//if not first cell
				theCell.disableBorderSide(PdfPCell.LEFT);
			}
			if(i!=codonPairNum-1){	// if not last cell
				theCell.disableBorderSide(PdfPCell.RIGHT);
			}
			distributionDiagram.addCell(theCell);
		}

		//setup properties of distributionDiagram
		distributionDiagram.setWidthPercentage(75f);

		return distributionDiagram;
	}

	public void exportWeight(String fileName){
		try{
			File cptFile=new File(fileName+".cpt");
			BufferedWriter bw=new BufferedWriter(new FileWriter(cptFile));
			for(int i=0;i<64;i++){
				for(int j=0;j<64;j++){
					bw.write(Float.toString(codonPairTable[i][j])+"\n");
				}
			}
			bw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void exportFrequency(String fileName){
		try{
			File cptFile=new File(fileName+".cpf");
			BufferedWriter bw=new BufferedWriter(new FileWriter(cptFile));
			for(int i=0;i<64;i++){
				for(int j=0;j<64;j++){
					bw.write(Float.toString(codonPairFrequency[i][j])+"\n");
				}
			}
			bw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static float[][] read(File f){	//to read codon pair table
		try{
			BufferedReader br=new BufferedReader(new FileReader(f));
			float[][] cpt=new float[64][64];
			for(int i=0;i<64;i++){
				for(int j=0;j<64;j++){
					cpt[i][j]=Float.parseFloat(br.readLine());
				}
			}		
			br.close();
			return cpt;
		}catch(Exception e){
			return null;
		}
	}

	public static int[][] readCPF(File f){	//to read coodn pair frequency table
		try{
			BufferedReader br=new BufferedReader(new FileReader(f));
			int[][] cpf=new int[64][64];
			for(int i=0;i<64;i++){
				for(int j=0;j<64;j++){
					cpf[i][j]=Integer.parseInt(br.readLine());
				}
			}		
			br.close();
			return cpf;
		}catch(Exception e){
			return null;
		}
	}

	public void showTable(Document pdf)throws DocumentException{	//write codon pair frequency to pdf file
		if(codonPairTable==null)return;

		//legend
		PdfPTable legendTable=new PdfPTable(1);
		legendTable.setKeepTogether(true);
		legendTable.setWidthPercentage(40f);
		PdfPCell keyCell=new PdfPCell(new Phrase("First codon | Second codon | Number | Weight",font8));
		legendTable.addCell(keyCell);
		pdf.add(legendTable);

		DecimalFormat df=new DecimalFormat("0.###");
		df.setRoundingMode(RoundingMode.HALF_UP);

		PdfPCell fillingCell=new PdfPCell();
		fillingCell.setBorder(PdfPCell.NO_BORDER);

		//generate table (separate into 21 amino acids as first codon)
		for(int i=0;i<21;i++){	//for each AA pair, make one topic (first aa)
			for(int j=0;j<21;j++){	//(second aa)
				//topic
				String topic=Common.getAAByNumber(i)+":"+Common.getAAByNumber(j);
				pdf.add(new Paragraph(topic+"\n",font10));

				int[] firstCodons=Common.getAAGroupMembersByNumber(i);
				int[] secondCodons=Common.getAAGroupMembersByNumber(j);
				//master table for layout
				PdfPTable masterTable=new PdfPTable(5);
				masterTable.setWidthPercentage(100f);
				masterTable.setKeepTogether(true);
				masterTable.setWidths(new int[]{10,1,10,1,10});
				//table
				for(int k=0;k<firstCodons.length;k++){	//first codons
					PdfPTable theTable=new PdfPTable(4);	//column number
					theTable.setSpacingBefore(5);
					for(int m=0;m<secondCodons.length;m++){	//second codons
						//first cell
						PdfPCell firstCell=new PdfPCell(new Phrase(Common.getCodonByCoordinate(firstCodons[k]).replaceAll("T","U"),font8));
						theTable.addCell(firstCell);
						//second cell
						PdfPCell secondCell=new PdfPCell(new Phrase(Common.getCodonByCoordinate(secondCodons[m]).replaceAll("T","U"),font8));
						theTable.addCell(secondCell);
						//number cell
						PdfPCell numberCell=new PdfPCell(new Phrase(""+codonPairFrequency[firstCodons[k]][secondCodons[m]],font8));
						theTable.addCell(numberCell);
						//weight cell
						PdfPCell weightCell=new PdfPCell(new Phrase(df.format(codonPairTable[firstCodons[k]][secondCodons[m]]),font8));
						theTable.addCell(weightCell);
					}
					PdfPCell bigCell=new PdfPCell(theTable);
					bigCell.setBorder(PdfPCell.NO_BORDER);
					masterTable.addCell(bigCell);
					if(k==2||k==5){
						continue;
					}else{
						masterTable.addCell(fillingCell);
					}
				}
				//fill up the remain cells with fillingCell
				if(firstCodons.length<3){
					int count=firstCodons.length;
					count++;
					while(count<=3){
						masterTable.addCell(fillingCell);
						if(count!=3){
							masterTable.addCell(fillingCell);
						}
						count++;
					}
				}else if(firstCodons.length>3&&firstCodons.length<6){
					int count=firstCodons.length;
					count++;
					while(count<=6){
						masterTable.addCell(fillingCell);
						if(count!=6){
							masterTable.addCell(fillingCell);
						}
						count++;
					}
				}
				pdf.add(masterTable);
			}
		}
		pdf.add(new Paragraph("\n",font10));	
	}
}
