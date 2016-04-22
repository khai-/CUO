package Tools.CAI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import Support.WeightTable;

public class CAIInsert extends JDialog{
	Color bgColor=new Color(70,70,70);
	CAI cai;
	JDialog caiInsert=this;

	JLabel type;
	JComboBox comboBox;
	JTextPane textPane;
	JScrollPane textScroll;
	JButton previewButton;
	JButton saveButton;
	JButton OKButton;

	public CAIInsert(CAI cai){
		super(cai.CAI,"CAI Insert");
		setModal(true);
		this.cai=cai;
		initiate();
	}

	private void initiate(){
		createComponents();
		assignFunctions();
		layoutComponents();
		setupProperties();
	}

	private void createComponents(){
		type=new JLabel("Type:");
		type.setForeground(Color.WHITE);
		String[] theCombo={"Codon Number","Frequency per thousand","Raw Sequence","Weight Table"};
		comboBox=new JComboBox(theCombo);
		textPane=new JTextPane();
		textScroll=new JScrollPane(textPane,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		textScroll.setPreferredSize(new Dimension(10000,10000));
		previewButton=new JButton("Preview");
		saveButton=new JButton("Save");
		OKButton=new JButton("OK");
	}

	private void assignFunctions(){
		//previewButton
		previewButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				String typeNow=(String)comboBox.getSelectedItem();
				//check for validity
				boolean valid=true;
				if(typeNow.equals("Codon Number")){
					valid=checkValidity("Codon Number",textPane.getText());
				}else if(typeNow.equals("Frequency per thousand")){
					valid=checkValidity("Frequency per thousand",textPane.getText());
				}else if(typeNow.equals("Raw Sequence")){
					valid=checkValidity("Raw Sequence",textPane.getText());
				}else if(typeNow.equals("Weight Table")){
					valid=checkValidity("Weight Table",textPane.getText());
				}
				if(!valid){
					Toolkit.getDefaultToolkit().beep();
					return;
				}

				//calculate the codon frequency table
				int[] codonFrequency=new int[65];
				if(typeNow.equals("Codon Number")){
					codonFrequency=getCodonFrequencyNum(textPane.getText());
				}else if(typeNow.equals("Frequency per thousand")){
					codonFrequency=getCodonFrequencyFre(textPane.getText());
				}else if(typeNow.equals("Raw Sequence")){
					codonFrequency=getCodonFrequencyRaw(textPane.getText().toUpperCase());
				}else if(typeNow.equals("Weight Table")){
					//don't do anything
				}else{	//something is wrong
					return;
				}	
				codonFrequency[64]=0;	//ignore the error codons

				//calculate the weight table
				float[] weightTable=new float[64];
				if(typeNow.equals("Codon Number")||typeNow.equals("Frequency per thousand")||typeNow.equals("Raw Sequence")){
					weightTable=WeightTable.getWeight(codonFrequency);
				}else if(typeNow.equals("Weight Table")){	//just extract the data given
					BufferedReader reader=new BufferedReader(new StringReader(textPane.getText()));
					for(int i=0;i<64;i++){
						try{
							weightTable[i]=Float.parseFloat(reader.readLine());
						}catch(IOException ioe){
							ioe.printStackTrace();
							return;
						}
					}
				}else{	//something is wrong
					return;
				}

				//show the weight table
				JDialog previewDialog=new JDialog(caiInsert);
				previewDialog.setTitle("Preview Weight Table");
				DecimalFormat df=new DecimalFormat("0.###");
				df.setRoundingMode(RoundingMode.HALF_UP);
				for(int i=0;i<64;i++){
					weightTable[i]=Float.parseFloat(df.format(weightTable[i]));
				}
				JLabel previewTable1=new JLabel("<html><font color=WHITE>"+
"UUU "+weightTable[0]+"<br>UUC "+weightTable[1]+"<br>"+
"UUA "+weightTable[2]+"<br>UUG "+weightTable[3]+"<br>"+
"CUU "+weightTable[4]+"<br>CUC "+weightTable[5]+"<br>CUA "+weightTable[6]+"<br>CUG "+weightTable[7]+"<br>"+
"AUU "+weightTable[8]+"<br>AUC "+weightTable[9]+"<br>AUA "+weightTable[10]+"<br>"+
"AUG "+weightTable[11]+"<br>"+
"GUU "+weightTable[12]+"<br>GUC "+weightTable[13]+"<br>GUA "+weightTable[14]+"<br>GUG "+weightTable[15]+"<br>"+
"</font></html>");
				JLabel previewTable2=new JLabel("<html><font color=WHITE>"+
"UCU "+weightTable[16]+"<br>UCC "+weightTable[17]+"<br>UCA "+weightTable[18]+"<br>UCG "+weightTable[19]+"<br>"+
"CCU "+weightTable[20]+"<br>CCC "+weightTable[21]+"<br>CCA "+weightTable[22]+"<br>CCG "+weightTable[23]+"<br>"+
"ACU "+weightTable[24]+"<br>ACC "+weightTable[25]+"<br>ACA "+weightTable[26]+"<br>ACG "+weightTable[27]+"<br>"+
"GCU "+weightTable[28]+"<br>GCC "+weightTable[29]+"<br>GCA "+weightTable[30]+"<br>GCG "+weightTable[31]+"<br>"+
"</font></html>");
				JLabel previewTable3=new JLabel("<html><font color=WHITE>"+
"UAU "+weightTable[32]+"<br>UAC "+weightTable[33]+"<br>"+
"UAA "+weightTable[34]+"<br>UAG "+weightTable[35]+"<br>"+
"CAU "+weightTable[36]+"<br>CAC "+weightTable[37]+"<br>"+
"CAA "+weightTable[38]+"<br>CAG "+weightTable[39]+"<br>"+
"AAU "+weightTable[40]+"<br>AAC "+weightTable[41]+"<br>"+
"AAA "+weightTable[42]+"<br>AAG "+weightTable[43]+"<br>"+
"GAU "+weightTable[44]+"<br>GAC "+weightTable[45]+"<br>"+
"GAA "+weightTable[46]+"<br>GAG "+weightTable[47]+"<br>"+
"</font></html>");
				JLabel previewTable4=new JLabel("<html><font color=WHITE>"+
"UGU "+weightTable[48]+"<br>UGC "+weightTable[49]+"<br>"+
"UGA "+weightTable[50]+"<br>"+
"UGG "+weightTable[51]+"<br>"+
"CGU "+weightTable[52]+"<br>CGC "+weightTable[53]+"<br>CGA "+weightTable[54]+"<br>CGG "+weightTable[55]+"<br>"+
"AGU "+weightTable[56]+"<br>AGC "+weightTable[57]+"<br>"+
"AGA "+weightTable[58]+"<br>AGG "+weightTable[59]+"<br>"+
"GGU "+weightTable[60]+"<br>GGC "+weightTable[61]+"<br>GGA "+weightTable[62]+"<br>GGG "+weightTable[63]+"<br>"+
"</font></html>");
				JPanel previewPanel=new JPanel();
				previewPanel.setLayout(new BoxLayout(previewPanel,BoxLayout.X_AXIS));
				previewPanel.setBackground(bgColor);
				previewPanel.add(previewTable1);
				previewPanel.add(previewTable2);
				previewPanel.add(previewTable3);
				previewPanel.add(previewTable4);
				previewDialog.add(previewPanel);

				previewDialog.setModal(false);
				previewDialog.setResizable(false);
				previewDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				previewDialog.setSize(380,280);
				previewDialog.setLocationRelativeTo(null);
				previewDialog.setVisible(true);				
			}
		});

		//saveButton
		saveButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				String typeNow=(String)comboBox.getSelectedItem();
				//check for validity
				boolean valid=true;
				if(typeNow.equals("Codon Number")){
					valid=checkValidity("Codon Number",textPane.getText());
				}else if(typeNow.equals("Frequency per thousand")){
					valid=checkValidity("Frequency per thousand",textPane.getText());
				}else if(typeNow.equals("Raw Sequence")){
					valid=checkValidity("Raw Sequence",textPane.getText());
				}else if(typeNow.equals("Weight Table")){
					valid=checkValidity("Weight Table",textPane.getText());
				}
				if(!valid){
					Toolkit.getDefaultToolkit().beep();
					return;
				}

				//save manager
				String wtFileName;
				JFileChooser jfc=new JFileChooser("."+File.separator+"Construction");
				if(jfc.showSaveDialog(caiInsert)==JFileChooser.APPROVE_OPTION){
					wtFileName=jfc.getSelectedFile().getPath();
				}else{
					return;
				}

				//calculate the codon frequency table
				int[] codonFrequency=new int[65];
				if(typeNow.equals("Codon Number")){
					codonFrequency=getCodonFrequencyNum(textPane.getText());
				}else if(typeNow.equals("Frequency per thousand")){
					codonFrequency=getCodonFrequencyFre(textPane.getText());
				}else if(typeNow.equals("Raw Sequence")){
					codonFrequency=getCodonFrequencyRaw(textPane.getText().toUpperCase());
				}else if(typeNow.equals("Weight Table")){
					//don't do anything
				}else{	//something is wrong
					return;
				}	
				codonFrequency[64]=0;	//ignore the error codons

				//calculate the weight table
				float[] weightTable=new float[64];
				if(typeNow.equals("Codon Number")||typeNow.equals("Frequency per thousand")||typeNow.equals("Raw Sequence")){
					weightTable=WeightTable.getWeight(codonFrequency);
				}else if(typeNow.equals("Weight Table")){	//just extract the data given
					BufferedReader reader=new BufferedReader(new StringReader(textPane.getText()));
					for(int i=0;i<64;i++){
						try{
							weightTable[i]=Float.parseFloat(reader.readLine());
						}catch(IOException ioe){
							ioe.printStackTrace();
							return;
						}
					}
				}else{	//something is wrong
					return;
				}

				//save to file
				File wtFile=new File(wtFileName+".wt");
				try{
					BufferedWriter bw=new BufferedWriter(new FileWriter(wtFile));
					for(int i=0;i<64;i++){
						bw.write(Float.toString(weightTable[i])+"\n");
					}
					bw.close();
				}catch(IOException ioe){
					ioe.printStackTrace();
				}

				JOptionPane.showMessageDialog(caiInsert,"Save successful.");
			}
		});

		//OKButton
		OKButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				String typeNow=(String)comboBox.getSelectedItem();
				//check for validity
				boolean valid=true;
				if(comboBox.getSelectedItem().equals("Codon Number")){
					valid=checkValidity("Codon Number",textPane.getText());
				}else if(comboBox.getSelectedItem().equals("Frequency per thousand")){
					valid=checkValidity("Frequency per thousand",textPane.getText());
				}else if(comboBox.getSelectedItem().equals("Raw Sequence")){
					valid=checkValidity("Raw Sequence",textPane.getText());
				}else if(comboBox.getSelectedItem().equals("Weight Table")){
					valid=checkValidity("Weight Table",textPane.getText());
				}
				if(!valid){
					Toolkit.getDefaultToolkit().beep();
					return;
				}

				//calculate the codon frequency table
				int[] codonFrequency=new int[65];
				if(typeNow.equals("Codon Number")){
					codonFrequency=getCodonFrequencyNum(textPane.getText());
				}else if(typeNow.equals("Frequency per thousand")){
					codonFrequency=getCodonFrequencyFre(textPane.getText());
				}else if(typeNow.equals("Raw Sequence")){
					codonFrequency=getCodonFrequencyRaw(textPane.getText().toUpperCase());
				}else if(typeNow.equals("Weight Table")){
					//don't do anything
				}else{	//something is wrong
					return;
				}	
				codonFrequency[64]=0;	//ignore the error codons

				//calculate the weight table
				float[] weightTable=new float[64];
				if(typeNow.equals("Codon Number")||typeNow.equals("Frequency per thousand")||typeNow.equals("Raw Sequence")){
					weightTable=WeightTable.getWeight(codonFrequency);
				}else if(typeNow.equals("Weight Table")){	//just extract the data given
					BufferedReader reader=new BufferedReader(new StringReader(textPane.getText()));
					for(int i=0;i<64;i++){
						try{
							weightTable[i]=Float.parseFloat(reader.readLine());
						}catch(IOException ioe){
							ioe.printStackTrace();
							return;
						}
					}
				}else{	//something is wrong
					return;
				}

				//save to a temp .wt file
				File weightFile=new File("."+File.separator+"Construction"+File.separator+"temp.wt");	//a temporary .wt file
				try{
					BufferedWriter bw=new BufferedWriter(new FileWriter(weightFile));
					for(int i=0;i<64;i++){
						bw.write(Float.toString(weightTable[i])+"\n");
					}
					bw.close();
				}catch(IOException ioe){
					ioe.printStackTrace();
				}
				
				//update the importedWeight
				cai.importedWeight=weightTable;

				//update the CAI reference list
				final JPanel thePanel=new JPanel();
				thePanel.setLayout(new BoxLayout(thePanel,BoxLayout.Y_AXIS));
				JLabel theLabel=new JLabel("<html><u>Imported weight table:</u></html>");
				JLabel theLabel2=new JLabel("<html><div style=width:"+(cai.weightList.listWidth+20)+"px><font color=BLUE>"+weightFile.getName()+"</font></div></html>");	//label with info about the imported weight table
				thePanel.add(theLabel);
				thePanel.add(theLabel2);
				//replace the listmodel in weightList with imported weight table list
				DefaultListModel importModel=new DefaultListModel();
				importModel.addElement(thePanel);
				String[] codons={"UUU","UUC","UUA","UUG","CUU","CUC","CUA","CUG","AUU","AUC","AUA","AUG","GUU","GUC","GUA","GUG","UCU","UCC","UCA","UCG", "CCU","CCC","CCA","CCG","ACU","ACC","ACA","ACG","GCU","GCC","GCA","GCG","UAU","UAC","UAA","UAG","CAU","CAC","CAA","CAG", "AAU","AAC","AAA","AAG","GAU","GAC","GAA","GAG","UGU","UGC","UGA","UGG","CGU","CGC","CGA","CGG","AGU","AGC","AGA","AGG", "GGU","GGC","GGA","GGG"};
				for(int i=0;i<64;i++){	//list out the values of all weights
					JPanel weightValuePanel=new JPanel();
					weightValuePanel.setLayout(new BoxLayout(weightValuePanel,BoxLayout.X_AXIS));
					JLabel weightValueLabel=new JLabel("<html><font size=2>"+codons[i]+": "+weightTable[i]+"<br></font></html>");
					weightValuePanel.add(weightValueLabel);
					weightValuePanel.add(Box.createHorizontalGlue());
					importModel.addElement(weightValuePanel);
				}
				cai.weightList.needToRelist=false;	//disable the relist function
				cai.weightList.setModel(importModel);
				//replace importWeight with importCancelButton
				cai.analyzePanel.remove(1);
				cai.analyzePanel.add(cai.importCancelButton,1);
				cai.analyzePanel.validate();
				cai.analyzePanel.repaint();

				//delete the temp .wt file
				weightFile.delete();

				//close this dialog
				dispose();
			}
		});
	}

	public boolean checkValidity(String checkType,String toBeChecked){
		if(checkType.equals("Codon Number")||checkType.equals("Frequency per thousand")||checkType.equals("Weight Table")){
			//convert to ArrayList<int>
			ArrayList<String> check=new ArrayList<String>();
			BufferedReader reader=new BufferedReader(new StringReader(toBeChecked));
			String buffer;
			try{
				while((buffer=reader.readLine())!=null){
					check.add(buffer);
				}
			}catch(IOException ioe){
				ioe.printStackTrace();
			}

			//check if number of lines is 64
			if(check.size()!=64){
				JOptionPane.showMessageDialog(caiInsert,"Please insert 64 lines exactly.");
				return false;
			}

			//check if everything is number
			for(int i=0;i<check.size();i++){
				try{
					Float.parseFloat(check.get(i));
				}catch(NumberFormatException nfe){
					JOptionPane.showMessageDialog(caiInsert,"Please insert number only.");
					return false;
				}
			}

			return true;
		}else if(checkType.equals("Raw Sequence")){
			//convert all to upper case
			toBeChecked=toBeChecked.toUpperCase();

			//check if everything is only A,T,G or C
			for(int i=0;i<toBeChecked.length();i++){
				char checkChar=toBeChecked.charAt(i);
				if(checkChar!='A'&&checkChar!='T'&&checkChar!='G'&&checkChar!='C'){
					JOptionPane.showMessageDialog(caiInsert,"Please insert ATGC only.");
					return false;
				}
			}

			//check if the sequence can form exact number of triplet
			if(toBeChecked.length()%3!=0){
				JOptionPane.showMessageDialog(caiInsert,"Cannot form round number of triplet.");
				return false;
			}

			return true;
		}else{
			JOptionPane.showMessageDialog(caiInsert,"Invalid type.");
			return false;
		}
	}

	public int[] getCodonFrequencyRaw(String rawSeq){	//get the codon frequency[65] from raw sequence
		//count
		int[] codonFrequency=new int[65];
		int codonNum=rawSeq.length()/3;
		for(int k=0;k<codonNum;k++){
			String codon=rawSeq.substring(k*3,k*3+3);
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
			}
		}
		return codonFrequency;
	}
	public int[] getCodonFrequencyNum(String number){	//get codon frequency from codon number(same)
		BufferedReader reader=new BufferedReader(new StringReader(number));
		int[] codonNumber=new int[65];
		try{
			for(int i=0;i<64;i++){
				codonNumber[i]=Integer.parseInt(reader.readLine());
			}
		}catch(IOException ioe){
			ioe.printStackTrace();
		}catch(NumberFormatException nfe){
			Toolkit.getDefaultToolkit().beep();
		}

		return codonNumber;
	}
	public int[] getCodonFrequencyFre(String frequency){	//get codon frequency from codon frequency Note: some critical rounding approximation implied
		BufferedReader reader=new BufferedReader(new StringReader(frequency));
		int[] codonFrequency=new int[65];
		try{
			for(int i=0;i<64;i++){
				codonFrequency[i]=(int)(Float.parseFloat(reader.readLine())*1000);
			}
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
		return codonFrequency;
	}

	private void layoutComponents(){
		JPanel mainPanel=new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		mainPanel.setBackground(bgColor);
		//typePanel
		JPanel typePanel=new JPanel();
		typePanel.setOpaque(false);
		typePanel.setLayout(new BoxLayout(typePanel,BoxLayout.X_AXIS));
		typePanel.add(type);
		typePanel.add(comboBox);
		typePanel.add(Box.createHorizontalGlue());
		mainPanel.add(typePanel);
		mainPanel.add(Box.createRigidArea(new Dimension(0,5)));
		//textPanel
		JPanel textPanel=new JPanel();
		textPanel.setOpaque(false);
		textPanel.setLayout(new BoxLayout(textPanel,BoxLayout.X_AXIS));
		textPanel.add(textScroll);
		mainPanel.add(textPanel);
		mainPanel.add(Box.createRigidArea(new Dimension(0,5)));
		//buttonsPanel
		JPanel buttonsPanel=new JPanel();
		buttonsPanel.setOpaque(false);
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel,BoxLayout.X_AXIS));
		buttonsPanel.add(Box.createHorizontalGlue());
		buttonsPanel.add(previewButton);
		buttonsPanel.add(Box.createRigidArea(new Dimension(5,0)));
		buttonsPanel.add(saveButton);
		buttonsPanel.add(Box.createRigidArea(new Dimension(5,0)));
		buttonsPanel.add(OKButton);
		mainPanel.add(buttonsPanel);

		add(mainPanel);
	}

	private void setupProperties(){
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setSize(390,490);
		setLocationRelativeTo(null);
		setVisible(true);
	}
}
