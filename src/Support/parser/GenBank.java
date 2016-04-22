package Support.parser;

import java.awt.FlowLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import Support.Common;

//a parser class for data from GenBank
public class GenBank implements Transferable{
	public ImageIcon geneIcon=new ImageIcon("Icons"+File.separator+"geneIcon.gif");
	public ImageIcon proteinIcon=new ImageIcon("Icons"+File.separator+"proteinIcon.gif");
	public ImageIcon tRNAIcon=new ImageIcon("Icons"+File.separator+"tRNAIcon.gif");
	public ImageIcon rRNAIcon=new ImageIcon("Icons"+File.separator+"rRNAIcon.gif");
	public ImageIcon miRNAIcon=new ImageIcon("Icons"+File.separator+"miRNAIcon.gif");
	int listWidth=190;
	public String dirName;

	boolean loaded=false;	//indicate whether this class is loaded with data
	public ArrayList<Sequence> sequences=new ArrayList<Sequence>();	//protein
	public DefaultListModel listModel=new DefaultListModel();

	public String fileName;
	public String completeSequence;
	public int seqLength;
	public String strandedness;
	public String moltype;
	public String topology;	//the type of the sequence
	public String note;	//extra parameter for storing info of this program
	public String updateDate;
	public String createDate;
	public String name;
	public String accession;
	public String taxonomy;

	public GenBank(){}	//manual creation

	public GenBank(Sequence sequence){	//create a genBank with given sequence only
		this.sequences.add(sequence);
		this.completeSequence=sequence.codingSequence;
	}

	public GenBank(ArrayList<Sequence> sequences){	//create a genBank with given sequences
		this.sequences=sequences;
	}

	public GenBank(String fileName){	//created with known fileName
		this.fileName=fileName.split("\\"+File.separator)[fileName.split("\\"+File.separator).length-1];
		this.dirName=fileName;
	}

	public GenBank(Mfa mfa){
		if(mfa.fileName!=null){
			this.fileName=mfa.fileName.split("\\"+File.separator)[mfa.fileName.split("\\"+File.separator).length-1];
			this.dirName=mfa.fileName;
		}
		if(mfa.name!=null)this.name=mfa.name;
		else this.name=mfa.fileName.substring(mfa.fileName.lastIndexOf(File.separator)+1,mfa.fileName.length()-4);
		this.completeSequence=mfa.getCompleteSequence();
		this.seqLength=completeSequence.length();
		this.strandedness="-";
		this.moltype="-";
		this.topology="Gene";
		this.updateDate="-";
		this.createDate="-";
		this.accession="-";
		this.taxonomy="-";
		//create the sequences from fnaList
		int posNow=0;	//position now in the completeSequence
		for(int i=0;i<mfa.fnaList.size();i++){
			Sequence theSeq=new Sequence();

			int[]site={posNow+1,posNow+mfa.fnaList.get(i).sequence.length()};
			theSeq.interval.add(site);
			posNow+=mfa.fnaList.get(i).sequence.length();

			theSeq.genBank=this;
			theSeq.type=mfa.fnaList.get(i).type;
			if(theSeq.type.equals("Protein"))theSeq.type="CDS";
			theSeq.gene=mfa.fnaList.get(i).name;

			this.sequences.add(theSeq);
		}
		this.loaded=true;
	}

	public GenBank(Faa faa){
		if(faa.fileName!=null){
			this.fileName=faa.fileName.split("\\"+File.separator)[faa.fileName.split("\\"+File.separator).length-1];
			this.dirName=faa.fileName;
		}
		this.name=faa.name;
		this.completeSequence=Common.aaToSequence(faa.AA);
		this.seqLength=faa.AA.length()*3;
		this.strandedness="-";
		this.moltype="-";
		this.topology=faa.type;
		this.updateDate="-";
		this.createDate="-";
		this.accession="-";
		this.taxonomy="-";
		//create the only sequence
		Sequence theSeq=new Sequence();
		int []site={1,completeSequence.length()};	//the entire thing
		theSeq.interval.add(site);
		theSeq.genBank=this;
		theSeq.type="CDS";
		theSeq.gene=name;
		theSeq.AA=faa.AA;
		this.sequences.add(theSeq);
		this.loaded=true;
	}

	public GenBank(Fna fna){
		if(fna.fileName!=null){
			this.fileName=fna.fileName.split("\\"+File.separator)[fna.fileName.split("\\"+File.separator).length-1];
			this.dirName=fna.fileName;
		}
		this.name=fna.name;
		this.completeSequence=fna.sequence;
		this.seqLength=fna.sequence.length();
		this.strandedness="-";
		this.moltype="-";
		this.topology=fna.type;
		if(fna.type.equals("Restriction")){
			this.note=fna.topCut+"|"+fna.bottomCut;
		}
		this.updateDate="-";
		this.createDate="-";
		this.accession="-";
		this.taxonomy="-";
		//create the only sequence
		Sequence theSeq=new Sequence();
		int []site={1,completeSequence.length()};	//the entire thing
		theSeq.interval.add(site);
		theSeq.genBank=this;
		theSeq.type="CDS";
		theSeq.gene=name;
		this.sequences.add(theSeq);
		this.loaded=true;
	}

	//Transferable overrides
	public Object getTransferData(DataFlavor flavor){
		if(isDataFlavorSupported(flavor)){
			return this;
		}
		return null;
	}
	public DataFlavor[] getTransferDataFlavors(){
		DataFlavor genBankFlavor=new DataFlavor(GenBank.class,"GenBank");
		return new DataFlavor[]{genBankFlavor};
	}
	public boolean isDataFlavorSupported(DataFlavor flavor){
		DataFlavor[] df=getTransferDataFlavors();
		for(int i=0;i<df.length;i++){
			if(df[i].equals(flavor)){
				return true;
			}
		}
		return false;
	}

	public static void getXML(String database,String UID,File genBankXML)throws MalformedURLException,IOException{	//download GenBank XML file from net and save it
		if(Thread.currentThread().isInterrupted())return;

		//open the url
		URL url=new URL("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db="+database+"&id="+UID+"&rettype=gb&retmode=xml");
		url.openConnection();
		InputStream reader=url.openStream();

		//create buffer
		FileOutputStream writer=new FileOutputStream(genBankXML);
		byte[] buffer=new byte[153600];
		int totalBytesRead=0;
		int bytesRead=0;

		//write to file (overwrite automatically)
		while((bytesRead=reader.read(buffer))>0){
			writer.write(buffer,0,bytesRead);
			buffer=new byte[153600];
			totalBytesRead+=bytesRead;
		}

		reader.close();
		writer.close();
	}

	public void parseXMLSummary(File genBankXML)throws IOException,ParserConfigurationException,SAXException{	//parse only small part that summarize this data
		//get the xml file by small fraction to reduce processing time
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);	//disable validation to ncbi server
		dbf.setFeature("http://xml.org/sax/features/namespaces",false);
		dbf.setFeature("http://xml.org/sax/features/validation",false);
		dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar",false);
		dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd",false);
		DocumentBuilder db=dbf.newDocumentBuilder();
		Document genBankDoc=db.parse(genBankXML);

		Element rootEle=genBankDoc.getDocumentElement();
		//seqLength
		NodeList seqLengthNode=rootEle.getElementsByTagName("GBSeq_length");
		seqLength=Integer.parseInt(((Element)seqLengthNode.item(0)).getFirstChild().getNodeValue());
		//updateDate
		NodeList updateDateNode=rootEle.getElementsByTagName("GBSeq_update-date");
		updateDate=((Element)updateDateNode.item(0)).getFirstChild().getNodeValue();
		//createDate
		NodeList createDateNode=rootEle.getElementsByTagName("GBSeq_create-date");
		createDate=((Element)createDateNode.item(0)).getFirstChild().getNodeValue();
		//name
		NodeList nameNode=rootEle.getElementsByTagName("GBSeq_definition");
		name=((Element)nameNode.item(0)).getFirstChild().getNodeValue();
	}

	public void parseXML(File genBankXML)throws IOException,ParserConfigurationException,SAXException{	//parse the XML Document into class fields
		if(Thread.currentThread().isInterrupted())return;

		//get the xml file
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		dbf.setFeature("http://xml.org/sax/features/namespaces",false);
		dbf.setFeature("http://xml.org/sax/features/validation",false);
		dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar",false);
		dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd",false);
		DocumentBuilder db=dbf.newDocumentBuilder();
		Document genBankDoc=db.parse(genBankXML);

		Element rootEle=genBankDoc.getDocumentElement();
		//seqLength
		NodeList seqLengthNode=rootEle.getElementsByTagName("GBSeq_length");
		seqLength=Integer.parseInt(((Element)seqLengthNode.item(0)).getFirstChild().getNodeValue());
		//strandedness
		NodeList strandednessNode=rootEle.getElementsByTagName("GBSeq_strandedness");
		strandedness=((Element)strandednessNode.item(0)).getFirstChild().getNodeValue();
		//moltype
		NodeList moltypeNode=rootEle.getElementsByTagName("GBSeq_moltype");
		moltype=((Element)moltypeNode.item(0)).getFirstChild().getNodeValue();
		//topology
		NodeList topologyNode=rootEle.getElementsByTagName("GBSeq_topology");
		if(((Element)topologyNode.item(0)).getFirstChild().getNodeValue()!=null){
			topology=((Element)topologyNode.item(0)).getFirstChild().getNodeValue();
		}
		//updateDate
		NodeList updateDateNode=rootEle.getElementsByTagName("GBSeq_update-date");
		updateDate=((Element)updateDateNode.item(0)).getFirstChild().getNodeValue();
		//createDate
		NodeList createDateNode=rootEle.getElementsByTagName("GBSeq_create-date");
		createDate=((Element)createDateNode.item(0)).getFirstChild().getNodeValue();
		//name
		NodeList nameNode=rootEle.getElementsByTagName("GBSeq_definition");
		name=((Element)nameNode.item(0)).getFirstChild().getNodeValue();
		//accession
		NodeList accessionNode=rootEle.getElementsByTagName("GBSeq_accession-version");
		accession=((Element)accessionNode.item(0)).getFirstChild().getNodeValue();
		//taxonomy
		NodeList taxonomyNode=rootEle.getElementsByTagName("GBSeq_taxonomy");
		taxonomy=((Element)taxonomyNode.item(0)).getFirstChild().getNodeValue();
		//complete sequence
		try{
			NodeList completeSequenceNode=rootEle.getElementsByTagName("GBSeq_sequence");
			completeSequence=((Element)completeSequenceNode.item(0)).getFirstChild().getNodeValue();
			completeSequence=completeSequence.toUpperCase();
		}catch(NullPointerException npe){	//the information about the genome is not yet available
			return;
		}

		//retrieve gbfeature nodes from feature table node
		NodeList featureTableNode=rootEle.getElementsByTagName("GBSeq_feature-table");
		NodeList gbFeatureNode=((Element)featureTableNode.item(0)).getElementsByTagName("GBFeature");
		for(int i=0;i<gbFeatureNode.getLength();i++){
			Sequence sequence=new Sequence();
			//genBank
			sequence.genBank=this;
			//type
			NodeList typeNode=((Element)gbFeatureNode.item(i)).getElementsByTagName("GBFeature_key");
			String type=((Element)typeNode.item(0)).getFirstChild().getNodeValue();
			//filter out excessive types
			if(type.equals("source")||type.equals("gene")){
				continue;
			}
			sequence.type=type;
			//intervals
			NodeList featureIntervalNode=((Element)gbFeatureNode.item(i)).getElementsByTagName("GBFeature_intervals");
			NodeList intervalNode=((Element)featureIntervalNode.item(0)).getElementsByTagName("GBInterval");
			for(int j=0;j<intervalNode.getLength();j++){
				int[] interval=new int[2];
				NodeList fromNode=((Element)intervalNode.item(j)).getElementsByTagName("GBInterval_from");
				if(fromNode.item(0)==null){	//means this is an interval "point": misc_difference/old  sequence
					NodeList pointNode=((Element)intervalNode.item(j)).getElementsByTagName("GBInterval_point");
					int point=Integer.parseInt(((Element)pointNode.item(0)).getFirstChild().getNodeValue());
					interval[0]=interval[1]=point;
					sequence.interval.add(interval);
					continue;
				}
				int from=Integer.parseInt(((Element)fromNode.item(0)).getFirstChild().getNodeValue());
				interval[0]=from;
				NodeList toNode=((Element)intervalNode.item(j)).getElementsByTagName("GBInterval_to");
				int to=Integer.parseInt(((Element)toNode.item(0)).getFirstChild().getNodeValue());
				interval[1]=to;
				sequence.interval.add(interval);
			}

			//retrieving qualifiers
			NodeList qualsNode=((Element)gbFeatureNode.item(i)).getElementsByTagName("GBFeature_quals");
			if((Element)qualsNode.item(0)!=null){
				NodeList qualifierNode=((Element)qualsNode.item(0)).getElementsByTagName("GBQualifier");
				for(int j=0;j<qualifierNode.getLength();j++){
					String qualName;
					String qualValue;
					try{
						//retrieve qualifier's name
						NodeList qualNameNode=((Element)qualifierNode.item(j)).getElementsByTagName("GBQualifier_name");
						qualName=((Element)qualNameNode.item(0)).getFirstChild().getNodeValue();
						//retrieve qualifier's value
						NodeList qualValueNode=((Element)qualifierNode.item(j)).getElementsByTagName("GBQualifier_value");
						qualValue=((Element)qualValueNode.item(0)).getFirstChild().getNodeValue();
					}catch(NullPointerException npe){	//sometimes there are irresponsible data uploaders
						continue;	//ignore this qualifier
					}

					//put data into sequence
					if(qualName.equals("gene")){	//gene
						sequence.gene=qualValue;
					}else if(qualName.equals("note")){	//note
						sequence.note=qualValue;
					}else if(qualName.equals("product")){	//product
						sequence.product=qualValue;
					}else if(qualName.equals("db_xref")){	//geneID
						if(qualValue.contains("GeneID")){
						sequence.geneID=qualValue.split(":")[1];	//take only the number
						}
					}else if(qualName.equals("codon_recognized")){	//codonRecognized
						sequence.codonRecognized=qualValue;
					}else if(qualName.equals("number")){	//number for exon or intron sequence
						sequence.number=Integer.parseInt(qualValue);
					}
				}
			}

			sequences.add(sequence);	//add sequence to the sequences list
		}

		loaded=true;
	}

	public void generateListModel(){
		//sort sequences
		Collections.sort(sequences,new Comparator<Sequence>(){
			public int compare(Sequence listOne,Sequence listTwo){
				int result=listOne.interval.get(0)[0]-listTwo.interval.get(0)[0];
				if(result==0){
					return 0;
				}else if(result>0){
					return 1;
				}else{
					return -1;
				}
			}
		});

		//show sequences
		for(int i=0;i<sequences.size();i++){
			JPanel cellPanel=new JPanel(new FlowLayout(FlowLayout.LEFT));
			//icon
			String sequenceType=sequences.get(i).type;
			if(sequenceType.equals("source")||sequenceType.equals("gene")||sequenceType.equals("exon")||sequenceType.equals("intron")||sequenceType.equals("misc_difference")||sequenceType.equals("old_sequence")){	//skip these
				continue;
			}
			if(sequences.get(i).type.equals("CDS")){	//protein
				cellPanel.add(new JLabel(proteinIcon));
			}else if(sequences.get(i).type.equals("tRNA")){	//tRNA
				cellPanel.add(new JLabel(tRNAIcon));
			}else if(sequences.get(i).type.equals("rRNA")){	//rRNA
				cellPanel.add(new JLabel(rRNAIcon));
			}else if(sequences.get(i).type.equals("misc_RNA")){	//miRNA
				cellPanel.add(new JLabel(miRNAIcon));
			}else{	//other type
				cellPanel.add(new JLabel(geneIcon));
			}
			JLabel invisibleLabel=new JLabel(dirName); //dirName the genes are in
			invisibleLabel.setVisible(false);
			cellPanel.add(invisibleLabel);
			JLabel invisibleLabel2=new JLabel("gene");	//type
			invisibleLabel2.setVisible(false);
			cellPanel.add(invisibleLabel2);
			//start and stop sites
			String startAndStop="";
			int processedLength=0;
			for(int j=0;j<sequences.get(i).interval.size();j++){
				if(sequences.get(i).interval.get(j)[0]==sequences.get(i).interval.get(j)[1]){	//is a point interval
					startAndStop+=sequences.get(i).interval.get(j)[1]+"   ";
				}else{
					startAndStop+=sequences.get(i).interval.get(j)[0]+"~"+sequences.get(i).interval.get(j)[1]+" ";
				}
				processedLength+=Math.abs(sequences.get(i).interval.get(j)[0]-sequences.get(i).interval.get(j)[1])+1;
			}
			//name and product resolving(some sequence don't have gene name and product)
			String theName=sequences.get(i).gene;
			if(theName==null){
				theName=sequences.get(i).type;	//use type as name instead if there is no gene name
			}
			String theProduct="";
			if(sequences.get(i).product!=null){	//put product name only if there is one
				theProduct="("+sequences.get(i).product+")";
			}

			JLabel geneLabel=new JLabel("<html><div style=width:"+listWidth+"px><font size=2 color=BLUE><u>"+theName+theProduct+"</u></font><br><font size=2>"+startAndStop+" Length:"+processedLength+"</font></div></html>");
			cellPanel.add(geneLabel);
			listModel.addElement(cellPanel);
		}
	}

	//method to get specific type of sequence
	public ArrayList<Sequence> getSequencesByType(String type){	//type can be:CDS,tRNA,rRNA,misc_RNA,mobile_element
		if(Thread.currentThread().isInterrupted())return null;
		if(loaded==false){
			return null;
		}
		ArrayList<Sequence> result=new ArrayList<Sequence>();
		for(int i=0;i<sequences.size();i++){
			if(sequences.get(i).type.equals(type)){
				result.add(sequences.get(i));
			}
		}
		return result;
	}

	//method to get sequence by gene name and type. ***BUG:there are some genes having same name different length
	public Sequence getSequenceByGene(String gene,String type){	//gene name and type
		if(Thread.currentThread().isInterrupted())return null;
		if(loaded==false){
			return null;
		}
		for(int i=0;i<sequences.size();i++){
			if(sequences.get(i).type.equals(type)){
				if(sequences.get(i).gene.equals(gene)){
					return sequences.get(i);
				}
			}
		}
		return null;
	}

	//method to get sequence by start and stop site
	public Sequence getSequenceBySite(String type,int start,int stop){	//start and stop site in int form
		if(Thread.currentThread().isInterrupted())return null;
		if(loaded==false){
			return null;
		}
		if(type.equals("unknown")){	//for unknown types we do only start and stop site testing
			for(int i=0;i<sequences.size();i++){
				if(sequences.get(i).interval.get(0)[0]==start&&sequences.get(i).interval.get(0)[1]==stop){
					return sequences.get(i);
				}
			}
		}
		for(int i=0;i<sequences.size();i++){	//for known types we do type,start and stop site testing
			if(sequences.get(i).type.equals(type)){
				if(sequences.get(i).interval.get(0)[0]==start&&sequences.get(i).interval.get(0)[1]==stop){
					return sequences.get(i);
				}
			}
		}
		return null;
	}

	//get the sequence by location
	public Sequence getSequenceByLocation(int location){
		for(int i=0;i<sequences.size();i++){
			for(int j=0;j<sequences.get(i).interval.size();j++){
				int start=sequences.get(i).interval.get(j)[0];
				int stop=sequences.get(i).interval.get(j)[1];
				if(start>stop){
					int temp=0;
					stop=temp;
					stop=start;
					start=temp;
				}
				if((location-start)>=0&&(stop-location)>=0){
					return sequences.get(i);
				}
			}
		}
		return null;
	}

	//deep copy entire genBank
	public GenBank copy(){
		GenBank newgen=new GenBank(fileName);
		//copy the info
		if(completeSequence!=null){
			newgen.completeSequence=new String(completeSequence);
		}
		newgen.seqLength=seqLength;
		if(strandedness!=null){
			newgen.strandedness=new String(strandedness);
		}
		if(moltype!=null){
			newgen.moltype=new String(moltype);
		}
		if(topology!=null){
			newgen.topology=new String(topology);
		}
		if(updateDate!=null){
			newgen.updateDate=new String(updateDate);
		}
		if(createDate!=null){
			newgen.createDate=new String(createDate);
		}
		if(name!=null){
			newgen.name=new String(name);
		}
		if(accession!=null){
			newgen.accession=new String(accession);
		}
		if(taxonomy!=null){
			newgen.taxonomy=new String(taxonomy);
		}
		if(note!=null)newgen.note=new String(note);

		//deep copy the sequences
		for(int i=0;i<sequences.size();i++){
			Sequence newseq=new Sequence();
			newseq.genBank=newgen;
			newseq.type=new String(sequences.get(i).type);
			for(int j=0;j<sequences.get(i).interval.size();j++){	//the intervals
				int[] newinter=new int[sequences.get(i).interval.get(j).length];
				for(int k=0;k<sequences.get(i).interval.get(j).length;k++){
					newinter[k]=sequences.get(i).interval.get(j)[k];
				}
				newseq.interval.add(newinter);
			}
			if(sequences.get(i).gene!=null){
				newseq.gene=new String(sequences.get(i).gene);
			}
			if(sequences.get(i).note!=null){
				newseq.note=new String(sequences.get(i).note);
			}
			if(sequences.get(i).product!=null){
				newseq.product=new String(sequences.get(i).product);
			}
			if(sequences.get(i).geneID!=null){
				newseq.geneID=new String(sequences.get(i).geneID);
			}
			if(sequences.get(i).codonRecognized!=null){
				newseq.codonRecognized=new String(sequences.get(i).codonRecognized);
			}
			newseq.number=sequences.get(i).number;
			if(sequences.get(i).codonFrequency!=null){
				for(int j=0;j<sequences.get(i).codonFrequency.length;j++){	//codonFrequency
					try{
						newseq.codonFrequency[j]=sequences.get(i).codonFrequency[j];
					}catch(NullPointerException npe){
						continue;
					}
				}
			}
			if(sequences.get(i).transcript!=null){
				for(int j=0;j<sequences.get(i).transcript.length;j++){	//transcript
					try{
						newseq.transcript[j]=new String(sequences.get(i).transcript[j]);
					}catch(NullPointerException npe){
						continue;
					}
				}
			}
			newseq.gCAI=sequences.get(i).gCAI;
			newseq.CAI=sequences.get(i).CAI;

			newgen.sequences.add(newseq);
		}
		//no need to copy the listModel

		return newgen;
	}

	//deep copy genBank with only specific sequence
	public GenBank copy(ArrayList<int[]> copiedinterval){
		GenBank newgen=new GenBank(fileName);
		//copy the info
		 //completeSequence is not copied, it is generated later
		newgen.seqLength=seqLength;
		newgen.strandedness=new String(strandedness);
		newgen.moltype=new String(moltype);
		newgen.topology=new String(topology);
		newgen.updateDate=new String(updateDate);
		newgen.createDate=new String(createDate);
		newgen.name=new String(name);	//the new name
		newgen.accession=new String(accession);
		newgen.taxonomy=new String(taxonomy);
		if(note!=null)newgen.note=new String(note);

		//get the sequence
		Sequence theseq=null;
		for(int i=0;i<sequences.size();i++){
			if(copiedinterval.size()!=sequences.get(i).interval.size()){
				continue;
			}

			boolean same=true;
			for(int j=0;j<copiedinterval.size();j++){
				if(copiedinterval.get(j)[0]!=sequences.get(i).interval.get(j)[0]){
					same=false;
					break;
				}
				if(copiedinterval.get(j)[1]!=sequences.get(i).interval.get(j)[1]){
					same=false;
				}
			}
			if(same){
				theseq=sequences.get(i);
				break;
			}
		}

		//find the site on complete sequence and cut out the site
		if(!theseq.isComplementary()){	//positive strand
			newgen.completeSequence=completeSequence.substring(theseq.interval.get(0)[0]-1,theseq.interval.get(theseq.interval.size()-1)[1]);
		}else{	//negative strand
			newgen.completeSequence=completeSequence.substring(theseq.interval.get(theseq.interval.size()-1)[1],theseq.interval.get(0)[0]-1);
		}

		//make up the new single sequence
		Sequence newseq=new Sequence();
		 //deep copy the static info
		newseq.genBank=newgen;
		newseq.type=new String(theseq.type);
		if(theseq.gene!=null){
			newseq.gene=new String(theseq.gene);
		}
		if(theseq.note!=null){
			newseq.note=new String(theseq.note);
		}
		if(theseq.product!=null){
			newseq.product=new String(theseq.product);
		}
		if(theseq.geneID!=null){
			newseq.geneID=new String(theseq.geneID);
		}
		if(theseq.codonRecognized!=null){
			newseq.codonRecognized=new String(theseq.codonRecognized);
		}
		newseq.number=theseq.number;
		 //reconsidering the changing infos
		int reduction=0;
		if(!theseq.isComplementary()){	//positive strand
			reduction=theseq.interval.get(0)[0]-1;
		}else{	//negative strand
			reduction=theseq.interval.get(0)[1]-1;
		}
		for(int j=0;j<theseq.interval.size();j++){	//the intervals
			int[] reducedinter=new int[2];
			reducedinter[0]=theseq.interval.get(j)[0]-reduction;
			reducedinter[1]=theseq.interval.get(j)[1]-reduction;
			newseq.interval.add(reducedinter);
		}
		 //codonFrequency,transcript,gCAI,CAI are ignored
		newgen.sequences.add(newseq);

		return newgen;
	}
}
