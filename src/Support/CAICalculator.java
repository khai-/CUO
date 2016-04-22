package Support;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
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

public class CAICalculator{
	static Font font10=new Font(FontFamily.HELVETICA,10);

	public CAICalculator(){}

	public static double getCAI(float[] weight,int[] codonFrequency){	//return CAI of a gene: gCAI=exp(all codon's weight in ln plus together/length) note: the weight can be gWeight to get gCAI instead of CAI
		double power=0;	//to store the power value of exponential
		int length=0;	//count the number of codons(note:skip met,try and STOP codons)
		for(int i=0;i<64;i++){	//iterate through every codon
			//skip methionine,tryptophan,STOP and X unidentified codons
			if(i==11||i==34||i==35||i==50||i==51){
				continue;
			}

			length+=codonFrequency[i];
			boolean wasZero=false;
			if(weight[i]==0||Double.isNaN(weight[i])){	//weight value must not be zero or NaN, this will mess up the result
				weight[i]=(float)0.01;
				wasZero=true;
			}
			if(codonFrequency[i]!=0){
				power+=Math.log(weight[i])*codonFrequency[i];
			}

			//make weight value back to zero to avoid confusion
			if(wasZero)weight[i]=0;
		}

		return Math.exp(power/length);
	}

	public static Paragraph getCAI(float[] weight,GenBank genBank){	//return CAI results list from a genBank
		ArrayList<Sequence> proteinList=genBank.getSequencesByType("CDS");
		Paragraph caiPara=new Paragraph("",font10);
		//calculate CAI of every proteins
		for(int i=0;i<proteinList.size();i++){
			proteinList.get(i).CAI=getCAI(weight,proteinList.get(i).getCodonFrequency(proteinList.get(i).genBank.completeSequence));
		}
		//sort CAI list from low CAI to high CAI
		Collections.sort(proteinList,new Comparator<Sequence>(){
			public int compare(Sequence sequenceOne,Sequence sequenceTwo){
				if(sequenceOne.CAI==sequenceTwo.CAI){
					return 0;
				}else if(sequenceOne.CAI>sequenceTwo.CAI){
					return 1;
				}else{
					return -1;
				}
			}
		});
		//bake sorted CAI list into a dotted list
		caiPara.add(new Phrase(genBank.name+"\n",new Font(FontFamily.HELVETICA,10,Font.UNDERLINE,BaseColor.BLUE))); //header
		DecimalFormat df=new DecimalFormat("0.000");
		df.setRoundingMode(RoundingMode.HALF_UP);
		Chunk dottedTab=new Chunk(new DottedLineSeparator(),400,true);
		for(int i=0;i<proteinList.size();i++){	//the dotted list
			Sequence protein=proteinList.get(i);
			if(protein.product!=null) caiPara.add(new Chunk(protein.gene+"("+protein.product+")"));
			else caiPara.add(new Chunk(protein.gene));
			caiPara.add(new Chunk(dottedTab));
			caiPara.add(new Chunk("(CAI="+df.format(protein.CAI)+")\n"));
		}

		//summary CAI
		int[] summaryCodonFrequency=new int[65];
		for(int i=0;i<proteinList.size();i++){
			for(int j=0;j<65;j++){
				summaryCodonFrequency[j]+=proteinList.get(i).getCodonFrequency(proteinList.get(i).genBank.completeSequence)[j];
			}
		}
		double summaryCAI=getCAI(weight,summaryCodonFrequency);
		caiPara.add(new Phrase("\nCodon Adaptation Index = "+df.format(summaryCAI)+"\n"));

		//set some properties of the paragraph
		caiPara.setSpacingBefore(10f);
		caiPara.setSpacingAfter(10f);

		return caiPara;
	}

	public static Paragraph getCAI(float[] weight,Sequence protein,PdfWriter writer)throws BadElementException{	//return CAI result and weight distribution graph from single protein
		Paragraph caiPara=new Paragraph("",font10);

		//get weight of every codons
		String[] sequenceArray=protein.getTranscript(protein.genBank.completeSequence);
		String codons="";
		for(int i=0;i<sequenceArray.length;i++){	//attenuate all the codons
			codons+=sequenceArray[i];
		}
		int codonNum=codons.length()/3;
		XYSeries series=new XYSeries("Weight Distribution");
		for(int i=0;i<codonNum;i++){
			if(codons.substring(i*3,i*3+3).equals("TTT")){
				series.add(i+1d,weight[0]);
			}else if(codons.substring(i*3,i*3+3).equals("TTC")){
				series.add(i+1d,weight[1]);
			}else if(codons.substring(i*3,i*3+3).equals("TTA")){
				series.add(i+1d,weight[2]);
			}else if(codons.substring(i*3,i*3+3).equals("TTG")){
				series.add(i+1d,weight[3]);
			}else if(codons.substring(i*3,i*3+3).equals("CTT")){
				series.add(i+1d,weight[4]);
			}else if(codons.substring(i*3,i*3+3).equals("CTC")){
				series.add(i+1d,weight[5]);
			}else if(codons.substring(i*3,i*3+3).equals("CTA")){
				series.add(i+1d,weight[6]);
			}else if(codons.substring(i*3,i*3+3).equals("CTG")){
				series.add(i+1d,weight[7]);
			}else if(codons.substring(i*3,i*3+3).equals("ATT")){
				series.add(i+1d,weight[8]);
			}else if(codons.substring(i*3,i*3+3).equals("ATC")){
				series.add(i+1d,weight[9]);
			}else if(codons.substring(i*3,i*3+3).equals("ATA")){
				series.add(i+1d,weight[10]);
			}else if(codons.substring(i*3,i*3+3).equals("ATG")){
				series.add(i+1d,weight[11]);
			}else if(codons.substring(i*3,i*3+3).equals("GTT")){
				series.add(i+1d,weight[12]);
			}else if(codons.substring(i*3,i*3+3).equals("GTC")){
				series.add(i+1d,weight[13]);
			}else if(codons.substring(i*3,i*3+3).equals("GTA")){
				series.add(i+1d,weight[14]);
			}else if(codons.substring(i*3,i*3+3).equals("GTG")){
				series.add(i+1d,weight[15]);
			}else if(codons.substring(i*3,i*3+3).equals("TCT")){
				series.add(i+1d,weight[16]);
			}else if(codons.substring(i*3,i*3+3).equals("TCC")){
				series.add(i+1d,weight[17]);
			}else if(codons.substring(i*3,i*3+3).equals("TCA")){
				series.add(i+1d,weight[18]);
			}else if(codons.substring(i*3,i*3+3).equals("TCG")){
				series.add(i+1d,weight[19]);
			}else if(codons.substring(i*3,i*3+3).equals("CCT")){
				series.add(i+1d,weight[20]);
			}else if(codons.substring(i*3,i*3+3).equals("CCC")){
				series.add(i+1d,weight[21]);
			}else if(codons.substring(i*3,i*3+3).equals("CCA")){
				series.add(i+1d,weight[22]);
			}else if(codons.substring(i*3,i*3+3).equals("CCG")){
				series.add(i+1d,weight[23]);
			}else if(codons.substring(i*3,i*3+3).equals("ACT")){
				series.add(i+1d,weight[24]);
			}else if(codons.substring(i*3,i*3+3).equals("ACC")){
				series.add(i+1d,weight[25]);
			}else if(codons.substring(i*3,i*3+3).equals("ACA")){
				series.add(i+1d,weight[26]);
			}else if(codons.substring(i*3,i*3+3).equals("ACG")){
				series.add(i+1d,weight[27]);
			}else if(codons.substring(i*3,i*3+3).equals("GCT")){
				series.add(i+1d,weight[28]);
			}else if(codons.substring(i*3,i*3+3).equals("GCC")){
				series.add(i+1d,weight[29]);
			}else if(codons.substring(i*3,i*3+3).equals("GCA")){
				series.add(i+1d,weight[30]);
			}else if(codons.substring(i*3,i*3+3).equals("GCG")){
				series.add(i+1d,weight[31]);
			}else if(codons.substring(i*3,i*3+3).equals("TAT")){
				series.add(i+1d,weight[32]);
			}else if(codons.substring(i*3,i*3+3).equals("TAC")){
				series.add(i+1d,weight[33]);
			}else if(codons.substring(i*3,i*3+3).equals("TAA")){
				series.add(i+1d,weight[34]);
			}else if(codons.substring(i*3,i*3+3).equals("TAG")){
				series.add(i+1d,weight[35]);
			}else if(codons.substring(i*3,i*3+3).equals("CAT")){
				series.add(i+1d,weight[36]);
			}else if(codons.substring(i*3,i*3+3).equals("CAC")){
				series.add(i+1d,weight[37]);
			}else if(codons.substring(i*3,i*3+3).equals("CAA")){
				series.add(i+1d,weight[38]);
			}else if(codons.substring(i*3,i*3+3).equals("CAG")){
				series.add(i+1d,weight[39]);
			}else if(codons.substring(i*3,i*3+3).equals("AAT")){
				series.add(i+1d,weight[40]);
			}else if(codons.substring(i*3,i*3+3).equals("AAC")){
				series.add(i+1d,weight[41]);
			}else if(codons.substring(i*3,i*3+3).equals("AAA")){
				series.add(i+1d,weight[42]);
			}else if(codons.substring(i*3,i*3+3).equals("AAG")){
				series.add(i+1d,weight[43]);
			}else if(codons.substring(i*3,i*3+3).equals("GAT")){
				series.add(i+1d,weight[44]);
			}else if(codons.substring(i*3,i*3+3).equals("GAC")){
				series.add(i+1d,weight[45]);
			}else if(codons.substring(i*3,i*3+3).equals("GAA")){
				series.add(i+1d,weight[46]);
			}else if(codons.substring(i*3,i*3+3).equals("GAG")){
				series.add(i+1d,weight[47]);
			}else if(codons.substring(i*3,i*3+3).equals("TGT")){
				series.add(i+1d,weight[48]);
			}else if(codons.substring(i*3,i*3+3).equals("TGC")){
				series.add(i+1d,weight[49]);
			}else if(codons.substring(i*3,i*3+3).equals("TGA")){
				series.add(i+1d,weight[50]);
			}else if(codons.substring(i*3,i*3+3).equals("TGG")){
				series.add(i+1d,weight[51]);
			}else if(codons.substring(i*3,i*3+3).equals("CGT")){
				series.add(i+1d,weight[52]);
			}else if(codons.substring(i*3,i*3+3).equals("CGC")){
				series.add(i+1d,weight[53]);
			}else if(codons.substring(i*3,i*3+3).equals("CGA")){
				series.add(i+1d,weight[54]);
			}else if(codons.substring(i*3,i*3+3).equals("CGG")){
				series.add(i+1d,weight[55]);
			}else if(codons.substring(i*3,i*3+3).equals("AGT")){
				series.add(i+1d,weight[56]);
			}else if(codons.substring(i*3,i*3+3).equals("AGC")){
				series.add(i+1d,weight[57]);
			}else if(codons.substring(i*3,i*3+3).equals("AGA")){
				series.add(i+1d,weight[58]);
			}else if(codons.substring(i*3,i*3+3).equals("AGG")){
				series.add(i+1d,weight[59]);
			}else if(codons.substring(i*3,i*3+3).equals("GGT")){
				series.add(i+1d,weight[60]);
			}else if(codons.substring(i*3,i*3+3).equals("GGC")){
				series.add(i+1d,weight[61]);
			}else if(codons.substring(i*3,i*3+3).equals("GGA")){
				series.add(i+1d,weight[62]);
			}else if(codons.substring(i*3,i*3+3).equals("GGG")){
				series.add(i+1d,weight[63]);
			}else{	//unidentified codon
				series.add(i+1d,-0.1d);
			}
		}
		XYSeriesCollection dataset=new XYSeriesCollection();
		dataset.addSeries(series);

		//make graph of weight vs sequence
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

		//create weight distribution diagram
		PdfPTable distributionDiagram=createWeightDistributionDiagram(weight,codons);

		//calculate CAI of the gene
		double summaryCAI=getCAI(weight,protein.codonFrequency);

		//cook the paragraph
		if(protein.product==null)caiPara.add(new Phrase(protein.gene,new Font(FontFamily.HELVETICA,10,Font.UNDERLINE,BaseColor.BLUE)));	//header
		else caiPara.add(new Phrase(protein.gene+"("+protein.product+")",new Font(FontFamily.HELVETICA,10,Font.UNDERLINE,BaseColor.BLUE)));
		caiPara.add(graphTable);	//the graph
		caiPara.add(distributionDiagram);	//the weight distribution diagram
		DecimalFormat df=new DecimalFormat("0.000");
		df.setRoundingMode(RoundingMode.HALF_UP);
		caiPara.add(new Phrase("Codon Adaptation Index = "+df.format(summaryCAI)));

		//set some properties of the paragraph
		caiPara.setSpacingBefore(10f);
		caiPara.setSpacingAfter(10f);

		return caiPara;
	}

	//generate iteration graph from given dataset
	public static Image createGraph(PdfWriter writer,XYSeriesCollection dataset,String xLabel,String yLabel)throws BadElementException,IOException{
		JFreeChart chart=ChartFactory.createXYLineChart(null,xLabel,yLabel,dataset,PlotOrientation.VERTICAL,true,true,false);
		chart.setBackgroundPaint(Color.WHITE);
		XYPlot plot=chart.getXYPlot();
		plot.setBackgroundPaint(Color.WHITE);

		//customize the range axis
		NumberAxis rangeAxis=(NumberAxis)plot.getRangeAxis();
		rangeAxis.setNumberFormatOverride(new DecimalFormat("0.##"));

		//draw chart into graphic
		int width=450;
		int height=300;
		PdfContentByte contentByte=writer.getDirectContent();
		PdfTemplate template=contentByte.createTemplate(width,height);
		Graphics2D g2d=template.createGraphics(width,height,new DefaultFontMapper());
		Rectangle2D r2d=new Rectangle2D.Double(0,0,width,height);
		chart.draw(g2d,r2d,null);
		g2d.dispose();
		Image image=Image.getInstance(template);

		return image;
	}

	//generate weight distribution diagram
	public static PdfPTable createWeightDistributionDiagram(float[] weight,String codons){
		int codonNum=codons.length()/3;
		PdfPTable distributionDiagram=new PdfPTable(codonNum);

		//assimilate diagram
		int[] weightCoordination=new int[codonNum];
		for(int i=0;i<codonNum;i++){
			if(codons.substring(i*3,i*3+3).equals("TTT")){
				weightCoordination[i]=0;
			}else if(codons.substring(i*3,i*3+3).equals("TTC")){
				weightCoordination[i]=1;
			}else if(codons.substring(i*3,i*3+3).equals("TTA")){
				weightCoordination[i]=2;
			}else if(codons.substring(i*3,i*3+3).equals("TTG")){
				weightCoordination[i]=3;
			}else if(codons.substring(i*3,i*3+3).equals("CTT")){
				weightCoordination[i]=4;
			}else if(codons.substring(i*3,i*3+3).equals("CTC")){
				weightCoordination[i]=5;
			}else if(codons.substring(i*3,i*3+3).equals("CTA")){
				weightCoordination[i]=6;
			}else if(codons.substring(i*3,i*3+3).equals("CTG")){
				weightCoordination[i]=7;
			}else if(codons.substring(i*3,i*3+3).equals("ATT")){
				weightCoordination[i]=8;
			}else if(codons.substring(i*3,i*3+3).equals("ATC")){
				weightCoordination[i]=9;
			}else if(codons.substring(i*3,i*3+3).equals("ATA")){
				weightCoordination[i]=10;
			}else if(codons.substring(i*3,i*3+3).equals("ATG")){
				weightCoordination[i]=11;
			}else if(codons.substring(i*3,i*3+3).equals("GTT")){
				weightCoordination[i]=12;
			}else if(codons.substring(i*3,i*3+3).equals("GTC")){
				weightCoordination[i]=13;
			}else if(codons.substring(i*3,i*3+3).equals("GTA")){
				weightCoordination[i]=14;
			}else if(codons.substring(i*3,i*3+3).equals("GTG")){
				weightCoordination[i]=15;
			}else if(codons.substring(i*3,i*3+3).equals("TCT")){
				weightCoordination[i]=16;
			}else if(codons.substring(i*3,i*3+3).equals("TCC")){
				weightCoordination[i]=17;
			}else if(codons.substring(i*3,i*3+3).equals("TCA")){
				weightCoordination[i]=18;
			}else if(codons.substring(i*3,i*3+3).equals("TCG")){
				weightCoordination[i]=19;
			}else if(codons.substring(i*3,i*3+3).equals("CCT")){
				weightCoordination[i]=20;
			}else if(codons.substring(i*3,i*3+3).equals("CCC")){
				weightCoordination[i]=21;
			}else if(codons.substring(i*3,i*3+3).equals("CCA")){
				weightCoordination[i]=22;
			}else if(codons.substring(i*3,i*3+3).equals("CCG")){
				weightCoordination[i]=23;
			}else if(codons.substring(i*3,i*3+3).equals("ACT")){
				weightCoordination[i]=24;
			}else if(codons.substring(i*3,i*3+3).equals("ACC")){
				weightCoordination[i]=25;
			}else if(codons.substring(i*3,i*3+3).equals("ACA")){
				weightCoordination[i]=26;
			}else if(codons.substring(i*3,i*3+3).equals("ACG")){
				weightCoordination[i]=27;
			}else if(codons.substring(i*3,i*3+3).equals("GCT")){
				weightCoordination[i]=28;
			}else if(codons.substring(i*3,i*3+3).equals("GCC")){
				weightCoordination[i]=29;
			}else if(codons.substring(i*3,i*3+3).equals("GCA")){
				weightCoordination[i]=30;
			}else if(codons.substring(i*3,i*3+3).equals("GCG")){
				weightCoordination[i]=31;
			}else if(codons.substring(i*3,i*3+3).equals("TAT")){
				weightCoordination[i]=32;
			}else if(codons.substring(i*3,i*3+3).equals("TAC")){
				weightCoordination[i]=33;
			}else if(codons.substring(i*3,i*3+3).equals("TAA")){
				weightCoordination[i]=34;
			}else if(codons.substring(i*3,i*3+3).equals("TAG")){
				weightCoordination[i]=35;
			}else if(codons.substring(i*3,i*3+3).equals("CAT")){
				weightCoordination[i]=36;
			}else if(codons.substring(i*3,i*3+3).equals("CAC")){
				weightCoordination[i]=37;
			}else if(codons.substring(i*3,i*3+3).equals("CAA")){
				weightCoordination[i]=38;
			}else if(codons.substring(i*3,i*3+3).equals("CAG")){
				weightCoordination[i]=39;
			}else if(codons.substring(i*3,i*3+3).equals("AAT")){
				weightCoordination[i]=40;
			}else if(codons.substring(i*3,i*3+3).equals("AAC")){
				weightCoordination[i]=41;
			}else if(codons.substring(i*3,i*3+3).equals("AAA")){
				weightCoordination[i]=42;
			}else if(codons.substring(i*3,i*3+3).equals("AAG")){
				weightCoordination[i]=43;
			}else if(codons.substring(i*3,i*3+3).equals("GAT")){
				weightCoordination[i]=44;
			}else if(codons.substring(i*3,i*3+3).equals("GAC")){
				weightCoordination[i]=45;
			}else if(codons.substring(i*3,i*3+3).equals("GAA")){
				weightCoordination[i]=46;
			}else if(codons.substring(i*3,i*3+3).equals("GAG")){
				weightCoordination[i]=47;
			}else if(codons.substring(i*3,i*3+3).equals("TGT")){
				weightCoordination[i]=48;
			}else if(codons.substring(i*3,i*3+3).equals("TGC")){
				weightCoordination[i]=49;
			}else if(codons.substring(i*3,i*3+3).equals("TGA")){
				weightCoordination[i]=50;
			}else if(codons.substring(i*3,i*3+3).equals("TGG")){
				weightCoordination[i]=51;
			}else if(codons.substring(i*3,i*3+3).equals("CGT")){
				weightCoordination[i]=52;
			}else if(codons.substring(i*3,i*3+3).equals("CGC")){
				weightCoordination[i]=53;
			}else if(codons.substring(i*3,i*3+3).equals("CGA")){
				weightCoordination[i]=54;
			}else if(codons.substring(i*3,i*3+3).equals("CGG")){
				weightCoordination[i]=55;
			}else if(codons.substring(i*3,i*3+3).equals("AGT")){
				weightCoordination[i]=56;
			}else if(codons.substring(i*3,i*3+3).equals("AGC")){
				weightCoordination[i]=57;
			}else if(codons.substring(i*3,i*3+3).equals("AGA")){
				weightCoordination[i]=58;
			}else if(codons.substring(i*3,i*3+3).equals("AGG")){
				weightCoordination[i]=59;
			}else if(codons.substring(i*3,i*3+3).equals("GGT")){
				weightCoordination[i]=60;
			}else if(codons.substring(i*3,i*3+3).equals("GGC")){
				weightCoordination[i]=61;
			}else if(codons.substring(i*3,i*3+3).equals("GGA")){
				weightCoordination[i]=62;
			}else if(codons.substring(i*3,i*3+3).equals("GGG")){
				weightCoordination[i]=63;
			}else{	//unidentified codon
				weightCoordination[i]=-1;	//will be assigned red color
			}
		}
		//lower weight,darker the color;higher weight,lighter the color
		for(int i=0;i<codonNum;i++){
			PdfPCell theCell=new PdfPCell();
			theCell.setFixedHeight(10f);
			if(weightCoordination[i]!=-1){
				int colorValue=(int)(weight[weightCoordination[i]]*255);
				theCell.setBackgroundColor(new BaseColor(colorValue,colorValue,colorValue));
			}else{	//unidentified codon
				theCell.setBackgroundColor(BaseColor.RED);
			}
			if(i!=0){	//first cell
				theCell.disableBorderSide(PdfPCell.LEFT);
			}
			if(i!=codonNum-1){	//last cell
				theCell.disableBorderSide(PdfPCell.RIGHT);
			}
			distributionDiagram.addCell(theCell);
		}

		//setup properties of distributionDiagram
		distributionDiagram.setWidthPercentage(75f);

		return distributionDiagram;
	}
}
