package Tools.OPTIMIZER.Filters;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Support.parser.GenBank;
import Support.parser.Sequence;
import Tools.OPTIMIZER.Optimizer;
import Tools.OPTIMIZER.ResultTab;

public class CodonUsageOptimizer extends Filter{
	Color pheColor=new Color(197,217,241);
	Color serColor=Color.YELLOW;
	Color tyrColor=new Color(0,112,192);
	Color cysColor=new Color(247,150,70);
	Color leuColor=new Color(234,241,221);
	Color stopColor=Color.WHITE;
	Color trpColor=new Color(255,0,255);
	Color proColor=new Color(146,208,80);
	Color hisColor=new Color(221,217,195);
	Color glnColor=new Color(250,192,144);
	Color argColor=new Color(216,216,216);
	Color ileColor=new Color(242,221,220);
	Color thrColor=new Color(0,176,80);
	Color asnColor=new Color(178,161,199);
	Color lysColor=new Color(148,139,84);
	Color metColor=Color.RED;
	Color valColor=new Color(255,192,0);
	Color alaColor=new Color(0,176,240);
	Color aspColor=new Color(102,255,51);
	Color gluColor=new Color(0,255,255);
	Color glyColor=new Color(182,221,232);

	public float[] weightTable=new float[64];
	float minimumWeight;
	public ArrayList<String[]> possibleList=new ArrayList<String[]>();	//all possible codons variations
	public int[] currentRoute;	//used in conjuction with routeMap to track current combination

	final JDialog configureDialog;
	JButton loadWeightTable;
	JTextField minimumWeightText;
	ArrayList<JTextField> textfield;
	JButton configureOKButton;
	JButton configureCancelButton;

	public CodonUsageOptimizer(final Optimizer optimizer,int processNumber){
		super(optimizer,processNumber);
		filterName="Codon Usage Optimizer";
		filterLabel.setText("Codon Usage Optimizer");
		filterDelete.setVisible(false);	//cancel delete filter function in CodonUsageOptimizer

		//configureDialog
		configureDialog=new JDialog(optimizer.OPTIMIZER);
		loadWeightTable=new JButton("Load Weight Table");
		minimumWeightText=new JTextField("0.5");	//default is 0.5
		minimumWeightText.setPreferredSize(new Dimension(50,20));
		minimumWeightText.setMaximumSize(new Dimension(50,20));
		textfield=new ArrayList<JTextField>();
		configureOKButton=new JButton("OK");
		configureCancelButton=new JButton("Cancel");
		for(int i=0;i<64;i++){
			textfield.add(new JTextField());
		}
		loadWeightTable.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				JFileChooser jfc=new JFileChooser("."+File.separator+"Construction");
				jfc.setAcceptAllFileFilterUsed(false);
				jfc.setFileFilter(new javax.swing.filechooser.FileFilter(){
					public boolean accept(File f){
						if(f.isDirectory()){
							return true;
						}
						String extension=f.getName().split("\\.")[f.getName().split("\\.").length-1];
						if(extension.equals("wt")){
							return true;
						}
						return false;
					}
					public String getDescription(){
						return "Weight Table file (*.wt)";
					}
				});
				if(jfc.showOpenDialog(configureDialog)==JFileChooser.APPROVE_OPTION){
					float[] temp=new float[64];
					//read *.wt to importedWeight[64]
					File weightFile=jfc.getSelectedFile();
					try{
						BufferedReader br=new BufferedReader(new FileReader(weightFile));
						temp=weightTable;
						weightTable=new float[64];
						for(int i=0;i<64;i++){
							weightTable[i]=Float.parseFloat(br.readLine());
						}
					
						br.close();
					}catch(Exception e){
						JOptionPane.showMessageDialog(configureDialog,e.toString(),"Error",JOptionPane.ERROR_MESSAGE);
						weightTable=temp;
						return;
					}
					//update configure dialog table
					DecimalFormat df=new DecimalFormat("0.#####");
					df.setRoundingMode(RoundingMode.HALF_UP);
					for(int i=0;i<64;i++){
						textfield.get(i).setText(""+df.format(weightTable[i]));
					}
				}else{
					return;
				}
			}
		});
		configureOKButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				//extract the weight table
				for(int i=0;i<64;i++){
					try{
						weightTable[i]=Float.parseFloat(textfield.get(i).getText());
					}catch(NumberFormatException nfe){
						JOptionPane.showMessageDialog(configureDialog,"Insert number or decimal only.","Error",JOptionPane.PLAIN_MESSAGE);
						return;
					}
				}

				//extract the minimum weight
				try{
					minimumWeight=Float.parseFloat(minimumWeightText.getText());
				}catch(NumberFormatException nfe){
					JOptionPane.showMessageDialog(configureDialog,"Insert number or decimal only.","Error",JOptionPane.PLAIN_MESSAGE);
					return;
				}

				isConfigured=true;
				optimizer.updateStatus();
				configureDialog.setVisible(false);
			}
		});
		configureCancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				configureDialog.setVisible(false);
			}
		});
		JPanel mainPanel=new JPanel();
		mainPanel.setBackground(bgColor);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
		JPanel buttonPanel=new JPanel();
		buttonPanel.setOpaque(false);
		buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.X_AXIS));
		buttonPanel.add(loadWeightTable);
		buttonPanel.add(Box.createHorizontalGlue());
		JLabel minimumWeightLabel=new JLabel("Minimum weight:");
		minimumWeightLabel.setForeground(Color.WHITE);
		buttonPanel.add(minimumWeightLabel);
		buttonPanel.add(minimumWeightText);
		mainPanel.add(buttonPanel);
		mainPanel.add(Box.createRigidArea(new Dimension(0,5)));
		JPanel tablePanel=new JPanel();
		tablePanel.setOpaque(false);
		tablePanel.setLayout(new BoxLayout(tablePanel,BoxLayout.X_AXIS));
		JPanel leftPanel=new JPanel();	//left
		leftPanel.setOpaque(false);
		leftPanel.setLayout(new BoxLayout(leftPanel,BoxLayout.Y_AXIS));
		String[] leftCodons={"UUU","UUC","UUA","UUG","CUU","CUC","CUA","CUG","AUU","AUC","AUA","AUG","GUU","GUC","GUA","GUG"};
		Color[] leftColor={pheColor,pheColor,leuColor,leuColor,leuColor, leuColor,leuColor,leuColor,ileColor,ileColor,ileColor,metColor,valColor,valColor,valColor,valColor};
		int[] leftBorderStyle={1,3,1,3,1,2,2,3,1,2,3,0,1,2,2,3};	//0 for O 1 for TT 2 for | | 3 for |_|
		for(int i=0;i<16;i++){
			JPanel codonPanel=new JPanel();
			codonPanel.setLayout(new BoxLayout(codonPanel,BoxLayout.X_AXIS));
			if(leftBorderStyle[i]==0){
				codonPanel.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.BLACK));
			}else if(leftBorderStyle[i]==1){
				codonPanel.setBorder(BorderFactory.createMatteBorder(1,1,0,1,Color.BLACK));
			}else if(leftBorderStyle[i]==2){
				codonPanel.setBorder(BorderFactory.createMatteBorder(0,1,0,1,Color.BLACK));
			}else if(leftBorderStyle[i]==3){
				codonPanel.setBorder(BorderFactory.createMatteBorder(0,1,1,1,Color.BLACK));
			}
			codonPanel.setBackground(leftColor[i]);
			codonPanel.add(new JLabel(leftCodons[i]));
			codonPanel.add(Box.createRigidArea(new Dimension(5,0)));
			codonPanel.add(textfield.get(i));
			leftPanel.add(codonPanel);
		}
		tablePanel.add(leftPanel);
		tablePanel.add(Box.createRigidArea(new Dimension(5,0)));
		JPanel midLeftPanel=new JPanel();	//mid left
		midLeftPanel.setOpaque(false);
		midLeftPanel.setLayout(new BoxLayout(midLeftPanel,BoxLayout.Y_AXIS));
		String[] midLeftCodons={"UCU","UCC","UCA","UCG","CCU","CCC","CCA","CCG","ACU","ACC","ACA","ACG","GCU","GCC","GCA","GCG"};
		Color[] midLeftColor={serColor,serColor,serColor,serColor,proColor,proColor,proColor,proColor,thrColor,thrColor,thrColor, thrColor,alaColor,alaColor,alaColor,alaColor};
		int[] midLeftBorderStyle={1,2,2,3,1,2,2,3,1,2,2,3,1,2,2,3};
		for(int i=0;i<16;i++){
			JPanel codonPanel=new JPanel();
			codonPanel.setLayout(new BoxLayout(codonPanel,BoxLayout.X_AXIS));
			if(midLeftBorderStyle[i]==0){
				codonPanel.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.BLACK));
			}else if(midLeftBorderStyle[i]==1){
				codonPanel.setBorder(BorderFactory.createMatteBorder(1,1,0,1,Color.BLACK));
			}else if(midLeftBorderStyle[i]==2){
				codonPanel.setBorder(BorderFactory.createMatteBorder(0,1,0,1,Color.BLACK));
			}else if(midLeftBorderStyle[i]==3){
				codonPanel.setBorder(BorderFactory.createMatteBorder(0,1,1,1,Color.BLACK));
			}
			codonPanel.setBackground(midLeftColor[i]);
			codonPanel.add(new JLabel(midLeftCodons[i]));
			codonPanel.add(Box.createRigidArea(new Dimension(5,0)));
			codonPanel.add(textfield.get(16+i));
			midLeftPanel.add(codonPanel);
		}
		tablePanel.add(midLeftPanel);
		tablePanel.add(Box.createRigidArea(new Dimension(5,0)));
		JPanel midRightPanel=new JPanel();	//mid right
		midRightPanel.setOpaque(false);
		midRightPanel.setLayout(new BoxLayout(midRightPanel,BoxLayout.Y_AXIS));
		String[] midRightCodons={"UAU","UAC","UAA","UAG","CAU","CAC","CAA","CAG","AAU","AAC","AAA","AAG","GAU","GAC","GAA","GAG"};
		Color[] midRightColor={tyrColor,tyrColor,stopColor,stopColor, hisColor,hisColor,glnColor,glnColor,asnColor,asnColor,lysColor,lysColor,aspColor,aspColor,gluColor,gluColor};
		int[] midRightBorderStyle={1,3,1,3,1,3,1,3,1,3,1,3,1,3,1,3};
		for(int i=0;i<16;i++){
			JPanel codonPanel=new JPanel();
			codonPanel.setLayout(new BoxLayout(codonPanel,BoxLayout.X_AXIS));
			if(midRightBorderStyle[i]==0){
				codonPanel.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.BLACK));
			}else if(midRightBorderStyle[i]==1){
				codonPanel.setBorder(BorderFactory.createMatteBorder(1,1,0,1,Color.BLACK));
			}else if(midRightBorderStyle[i]==2){
				codonPanel.setBorder(BorderFactory.createMatteBorder(0,1,0,1,Color.BLACK));
			}else if(midRightBorderStyle[i]==3){
				codonPanel.setBorder(BorderFactory.createMatteBorder(0,1,1,1,Color.BLACK));
			}
			codonPanel.setBackground(midRightColor[i]);
			codonPanel.add(new JLabel(midRightCodons[i]));
			codonPanel.add(Box.createRigidArea(new Dimension(5,0)));
			codonPanel.add(textfield.get(32+i));
			midRightPanel.add(codonPanel);
		}
		tablePanel.add(midRightPanel);
		tablePanel.add(Box.createRigidArea(new Dimension(5,0)));
		JPanel rightPanel=new JPanel();	//right
		rightPanel.setOpaque(false);
		rightPanel.setLayout(new BoxLayout(rightPanel,BoxLayout.Y_AXIS));
		String[] rightCodons={"UGU","UGC","UGA","UGG","CGU","CGC","CGA","CGG","AGU","AGC","AGA","AGG","GGU","GGC","GGA","GGG"};
		Color[] rightColor={cysColor,cysColor,stopColor,trpColor,argColor, argColor,argColor,argColor,serColor,serColor,argColor,argColor,glyColor,glyColor,glyColor,glyColor};
		int[] rightBorderStyle={1,3,0,0,1,2,2,3,1,3,1,3,1,2,2,3};
		for(int i=0;i<16;i++){
			JPanel codonPanel=new JPanel();
			codonPanel.setLayout(new BoxLayout(codonPanel,BoxLayout.X_AXIS));
			if(rightBorderStyle[i]==0){
				codonPanel.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.BLACK));
			}else if(rightBorderStyle[i]==1){
				codonPanel.setBorder(BorderFactory.createMatteBorder(1,1,0,1,Color.BLACK));
			}else if(rightBorderStyle[i]==2){
				codonPanel.setBorder(BorderFactory.createMatteBorder(0,1,0,1,Color.BLACK));
			}else if(rightBorderStyle[i]==3){
				codonPanel.setBorder(BorderFactory.createMatteBorder(0,1,1,1,Color.BLACK));
			}
			codonPanel.setBackground(rightColor[i]);
			codonPanel.add(new JLabel(rightCodons[i]));
			codonPanel.add(Box.createRigidArea(new Dimension(5,0)));
			codonPanel.add(textfield.get(48+i));
			rightPanel.add(codonPanel);
		}
		tablePanel.add(rightPanel);
		mainPanel.add(tablePanel);
		mainPanel.add(Box.createRigidArea(new Dimension(0,5)));
		JPanel confirmPanel=new JPanel();
		confirmPanel.setOpaque(false);
		confirmPanel.setLayout(new BoxLayout(confirmPanel,BoxLayout.X_AXIS));
		confirmPanel.add(Box.createHorizontalGlue());
		confirmPanel.add(configureOKButton);
		confirmPanel.add(Box.createRigidArea(new Dimension(5,0)));
		confirmPanel.add(configureCancelButton);
		mainPanel.add(confirmPanel);
		configureDialog.add(mainPanel);
		configureDialog.setTitle("CodonUsageOptimizer Configuration");
		configureDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		configureDialog.setSize(450,450);
		configureDialog.setResizable(false);
		configureDialog.setLocationRelativeTo(null);
		configureDialog.setVisible(false);

		//assignFuctions
		//filterConfigure
		filterConfigure.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				for(int i=0;i<64;i++){
					textfield.get(i).setText(""+weightTable[i]);
				}
				configureDialog.setVisible(true);
			}
		});
	}

	public boolean trigger(){
		if(optimizer.isHalfWay){
			optimizer.currentProcess=processNumber;
			return false;
		}
		isEngaged=true;

		//update currentRoute
		if(currentRoute==null){
			currentRoute=new int[optimizer.routeMap.length];
			for(int i=0;i<currentRoute.length;i++){
				currentRoute[i]=0;	//begin with 1
			}
		}else{
			for(int i=0;i<currentRoute.length;i++){
				int test=currentRoute[i];
				test++;
				if(test>optimizer.routeMap[i]){
					continue;
				}else{
					currentRoute[i]=test;
					break;
				}
			}
		}

		//generate the new resultInProcess
		GenBank resultGenBank=new GenBank();
		resultGenBank.completeSequence="";
		for(int i=0;i<possibleList.size();i++){	//generate the sequence according to currentRoute
			resultGenBank.completeSequence+=possibleList.get(i)[currentRoute[i]];
		}
		resultGenBank.name=optimizer.targetTitle.getText()+optimizer.currentNumber;
		resultGenBank.seqLength=resultGenBank.completeSequence.length();
		SimpleDateFormat sdf=new SimpleDateFormat("yyy/MM/dd");
		resultGenBank.updateDate=sdf.format(new Date());
		resultGenBank.createDate=sdf.format(new Date());
		Sequence newseq=new Sequence(optimizer.target);
		newseq.genBank=resultGenBank;
		resultGenBank.sequences.add(newseq);	//add the only protein sequence
		ResultTab newProcess=new ResultTab();
		newProcess.resultSequence=resultGenBank.sequences.get(0);
		optimizer.resultInProcess=newProcess;
		
		isEngaged=false;
		if(processNumber==optimizer.filterList.size()){
			optimizer.currentProcess=0;
		}else{
			optimizer.currentProcess++;
		}
		return true;	//must be
	}

	public static String getDescription(){
		return "Create variations of target sequence according to weight table given with minimum weight given as codon selection cut off point.";
	}

	//method to generate all possible variations of a codon based on minimumWeight decided
	public String[] generatePossibleCodons(String originalCodon){
		ArrayList<String> possibleCodons=new ArrayList<String>();
		if(originalCodon.equals("TTT")||originalCodon.equals("TTC")){	//Phe
			if(weightTable[0]>=minimumWeight){
				possibleCodons.add("TTT");
			}
			if(weightTable[1]>=minimumWeight){
				possibleCodons.add("TTC");
			}
			if(weightTable[0]<minimumWeight&&weightTable[1]<minimumWeight){
				possibleCodons.add("TTT");
				possibleCodons.add("TTC");	//both have equal chance, both added
			}
		}else if(originalCodon.equals("TTA")||originalCodon.equals("TTG")||originalCodon.equals("CTT")||originalCodon.equals("CTC")||originalCodon.equals("CTA")||originalCodon.equals("CTG")){	//Leu
			if(weightTable[2]>=minimumWeight){
				possibleCodons.add("TTA");
			}
			if(weightTable[3]>=minimumWeight){
				possibleCodons.add("TTG");
			}
			if(weightTable[4]>=minimumWeight){
				possibleCodons.add("CTT");
			}
			if(weightTable[5]>=minimumWeight){
				possibleCodons.add("CTC");
			}
			if(weightTable[6]>=minimumWeight){
				possibleCodons.add("CTA");
			}
			if(weightTable[7]>=minimumWeight){
				possibleCodons.add("CTG");
			}
			if(weightTable[2]<minimumWeight&&weightTable[3]<minimumWeight&&weightTable[4]<minimumWeight&&weightTable[5]<minimumWeight&&weightTable[6]<minimumWeight&&weightTable[7]<minimumWeight){
				possibleCodons.add("TTA");
				possibleCodons.add("TTG");
				possibleCodons.add("CTT");
				possibleCodons.add("CTC");
				possibleCodons.add("CTA");
				possibleCodons.add("CTG");
			}
		}else if(originalCodon.equals("ATT")||originalCodon.equals("ATC")||originalCodon.equals("ATA")){	//ile
			if(weightTable[8]>=minimumWeight){
				possibleCodons.add("ATT");
			}
			if(weightTable[9]>=minimumWeight){
				possibleCodons.add("ATC");
			}
			if(weightTable[10]>=minimumWeight){
				possibleCodons.add("ATA");
			}
			if(weightTable[8]<minimumWeight&&weightTable[9]<minimumWeight&&weightTable[10]<minimumWeight){
				possibleCodons.add("ATT");
				possibleCodons.add("ATC");
				possibleCodons.add("ATA");
			}
		}else if(originalCodon.equals("ATG")){	//Met
			possibleCodons.add("ATG");	//no other choices
		}else if(originalCodon.equals("GTT")||originalCodon.equals("GTC")||originalCodon.equals("GTA")||originalCodon.equals("GTG")){	//Val
			if(weightTable[12]>=minimumWeight){
				possibleCodons.add("GTT");
			}
			if(weightTable[13]>=minimumWeight){
				possibleCodons.add("GTC");
			}
			if(weightTable[14]>=minimumWeight){
				possibleCodons.add("GTA");
			}
			if(weightTable[15]>=minimumWeight){
				possibleCodons.add("GTG");
			}
			if(weightTable[12]<minimumWeight&&weightTable[13]<minimumWeight&&weightTable[14]<minimumWeight&&weightTable[15]<minimumWeight){
				possibleCodons.add("GTT");
				possibleCodons.add("GTC");
				possibleCodons.add("GTA");
				possibleCodons.add("GTG");
			}
		}else if(originalCodon.equals("TCT")||originalCodon.equals("TCC")||originalCodon.equals("TCA")||originalCodon.equals("TCG")||originalCodon.equals("AGT")||originalCodon.equals("AGC")){	//Ser
			if(weightTable[16]>=minimumWeight){
				possibleCodons.add("TCT");
			}
			if(weightTable[17]>=minimumWeight){
				possibleCodons.add("TCC");
			}
			if(weightTable[18]>=minimumWeight){
				possibleCodons.add("TCA");
			}
			if(weightTable[19]>=minimumWeight){
				possibleCodons.add("TCG");
			}
			if(weightTable[56]>=minimumWeight){
				possibleCodons.add("AGT");
			}
			if(weightTable[57]>=minimumWeight){
				possibleCodons.add("AGC");
			}
			if(weightTable[16]<minimumWeight&&weightTable[17]<minimumWeight&&weightTable[18]<minimumWeight&&weightTable[19]<minimumWeight&&weightTable[56]<minimumWeight&&weightTable[57]<minimumWeight){
				possibleCodons.add("TCT");
				possibleCodons.add("TCC");
				possibleCodons.add("TCA");
				possibleCodons.add("TCG");
				possibleCodons.add("AGT");
				possibleCodons.add("AGC");
			}
		}else if(originalCodon.equals("CCT")||originalCodon.equals("CCC")||originalCodon.equals("CCA")||originalCodon.equals("CCG")){	//Pro
			if(weightTable[20]>=minimumWeight){
				possibleCodons.add("CCT");
			}
			if(weightTable[21]>=minimumWeight){
				possibleCodons.add("CCC");
			}
			if(weightTable[22]>=minimumWeight){
				possibleCodons.add("CCA");
			}
			if(weightTable[23]>=minimumWeight){
				possibleCodons.add("CCG");
			}
			if(weightTable[20]<minimumWeight&&weightTable[21]<minimumWeight&&weightTable[22]<minimumWeight&&weightTable[23]<minimumWeight){
				possibleCodons.add("CCT");
				possibleCodons.add("CCC");
				possibleCodons.add("CCA");
				possibleCodons.add("CCG");
			}
		}else if(originalCodon.equals("ACT")||originalCodon.equals("ACC")||originalCodon.equals("ACA")||originalCodon.equals("ACG")){	//Thr
			if(weightTable[24]>=minimumWeight){
				possibleCodons.add("ACT");
			}
			if(weightTable[25]>=minimumWeight){
				possibleCodons.add("ACC");
			}
			if(weightTable[26]>=minimumWeight){
				possibleCodons.add("ACA");
			}
			if(weightTable[27]>=minimumWeight){
				possibleCodons.add("ACG");
			}
			if(weightTable[24]<minimumWeight&&weightTable[25]<minimumWeight&&weightTable[26]<minimumWeight&&weightTable[27]<minimumWeight){
				possibleCodons.add("ACT");
				possibleCodons.add("ACC");
				possibleCodons.add("ACA");
				possibleCodons.add("ACG");
			}
		}else if(originalCodon.equals("GCT")||originalCodon.equals("GCC")||originalCodon.equals("GCA")||originalCodon.equals("GCG")){	//Ala
			if(weightTable[28]>=minimumWeight){
				possibleCodons.add("GCT");
			}
			if(weightTable[29]>=minimumWeight){
				possibleCodons.add("GCC");
			}
			if(weightTable[30]>=minimumWeight){
				possibleCodons.add("GCA");
			}
			if(weightTable[31]>=minimumWeight){
				possibleCodons.add("GCG");
			}
			if(weightTable[28]<minimumWeight&&weightTable[29]<minimumWeight&&weightTable[30]<minimumWeight&&weightTable[31]<minimumWeight){
				possibleCodons.add("GCT");
				possibleCodons.add("GCC");
				possibleCodons.add("GCA");
				possibleCodons.add("GCG");
			}
		}else if(originalCodon.equals("TAT")||originalCodon.equals("TAC")){	//Tyr
			if(weightTable[32]>=minimumWeight){
				possibleCodons.add("TAT");
			}
			if(weightTable[33]>=minimumWeight){
				possibleCodons.add("TAC");
			}
			if(weightTable[32]<minimumWeight&&weightTable[33]<minimumWeight){
				possibleCodons.add("TAT");
				possibleCodons.add("TAC");
			}
		}else if(originalCodon.equals("TAA")||originalCodon.equals("TAG")){	//STOP
			if(weightTable[34]>=minimumWeight){
				possibleCodons.add("TAA");
			}
			if(weightTable[35]>=minimumWeight){
				possibleCodons.add("TAG");
			}
			if(weightTable[34]<minimumWeight&&weightTable[35]<minimumWeight){
				possibleCodons.add("TAA");
				possibleCodons.add("TAG");
			}
		}else if(originalCodon.equals("CAT")||originalCodon.equals("CAC")){	//His
			if(weightTable[36]>=minimumWeight){
				possibleCodons.add("CAT");
			}
			if(weightTable[37]>=minimumWeight){
				possibleCodons.add("CAC");
			}
			if(weightTable[36]<minimumWeight&&weightTable[37]<minimumWeight){
				possibleCodons.add("CAT");
				possibleCodons.add("CAC");
			}
		}else if(originalCodon.equals("CAA")||originalCodon.equals("CAG")){	//Gln
			if(weightTable[38]>=minimumWeight){
				possibleCodons.add("CAA");
			}
			if(weightTable[39]>=minimumWeight){
				possibleCodons.add("CAG");
			}
			if(weightTable[38]<minimumWeight&&weightTable[39]<minimumWeight){
				possibleCodons.add("CAA");
				possibleCodons.add("CAG");
			}
		}else if(originalCodon.equals("AAT")||originalCodon.equals("AAC")){	//Asn
			if(weightTable[40]>=minimumWeight){
				possibleCodons.add("AAT");
			}
			if(weightTable[41]>=minimumWeight){
				possibleCodons.add("AAC");
			}
			if(weightTable[40]<minimumWeight&&weightTable[41]<minimumWeight){
				possibleCodons.add("AAT");
				possibleCodons.add("AAC");
			}
		}else if(originalCodon.equals("AAA")||originalCodon.equals("AAG")){	//Lys
			if(weightTable[42]>=minimumWeight){
				possibleCodons.add("AAA");
			}
			if(weightTable[43]>=minimumWeight){
				possibleCodons.add("AAG");
			}
			if(weightTable[42]<minimumWeight&&weightTable[43]<minimumWeight){
				possibleCodons.add("AAA");
				possibleCodons.add("AAG");
			}
		}else if(originalCodon.equals("GAT")||originalCodon.equals("GAC")){	//Asp
			if(weightTable[44]>=minimumWeight){
				possibleCodons.add("GAT");
			}
			if(weightTable[45]>=minimumWeight){
				possibleCodons.add("GAC");
			}
			if(weightTable[44]<minimumWeight&&weightTable[45]<minimumWeight){
				possibleCodons.add("GAT");
				possibleCodons.add("GAC");
			}
		}else if(originalCodon.equals("GAA")||originalCodon.equals("GAG")){	//Glu
			if(weightTable[46]>=minimumWeight){
				possibleCodons.add("GAA");
			}
			if(weightTable[47]>=minimumWeight){
				possibleCodons.add("GAG");
			}
			if(weightTable[46]<minimumWeight&&weightTable[47]<minimumWeight){
				possibleCodons.add("GAA");
				possibleCodons.add("GCG");
			}
		}else if(originalCodon.equals("TGT")||originalCodon.equals("TGC")){	//Cys
			if(weightTable[48]>=minimumWeight){
				possibleCodons.add("TGU");
			}
			if(weightTable[49]>=minimumWeight){
				possibleCodons.add("TGC");
			}
			if(weightTable[48]<minimumWeight&&weightTable[49]<minimumWeight){
				possibleCodons.add("TGT");
				possibleCodons.add("TGC");
			}
		}else if(originalCodon.equals("TGA")){	//STOP
			possibleCodons.add("TGA");	//no choice
		}else if(originalCodon.equals("TGG")){	//Trp
			possibleCodons.add("TGG");	//no choice
		}else if(originalCodon.equals("CGT")||originalCodon.equals("CGC")||originalCodon.equals("CGA")||originalCodon.equals("CGG")||originalCodon.equals("AGA")||originalCodon.equals("AGG")){	//Arg
			if(weightTable[52]>=minimumWeight){
				possibleCodons.add("CGT");
			}
			if(weightTable[53]>=minimumWeight){
				possibleCodons.add("CGC");
			}
			if(weightTable[54]>=minimumWeight){
				possibleCodons.add("CGA");
			}
			if(weightTable[55]>=minimumWeight){
				possibleCodons.add("CGG");
			}
			if(weightTable[58]>=minimumWeight){
				possibleCodons.add("AGA");
			}
			if(weightTable[59]>=minimumWeight){
				possibleCodons.add("AGG");
			}
			if(weightTable[52]<minimumWeight&&weightTable[53]<minimumWeight&&weightTable[54]<minimumWeight&&weightTable[55]<minimumWeight&&weightTable[58]<minimumWeight&&weightTable[59]<minimumWeight){
				possibleCodons.add("CGT");
				possibleCodons.add("CGC");
				possibleCodons.add("CGA");
				possibleCodons.add("CGG");
				possibleCodons.add("AGA");
				possibleCodons.add("AGG");
			}
		}else if(originalCodon.equals("GGT")||originalCodon.equals("GGC")||originalCodon.equals("GGA")||originalCodon.equals("GGG")){	//Gly
			if(weightTable[60]>=minimumWeight){
				possibleCodons.add("GGT");
			}
			if(weightTable[61]>=minimumWeight){
				possibleCodons.add("GGC");
			}
			if(weightTable[62]>=minimumWeight){
				possibleCodons.add("GGA");
			}
			if(weightTable[63]>=minimumWeight){
				possibleCodons.add("GGG");
			}
			if(weightTable[60]<minimumWeight&&weightTable[61]<minimumWeight&&weightTable[62]<minimumWeight&&weightTable[63]<minimumWeight){
				possibleCodons.add("GGT");
				possibleCodons.add("GGC");
				possibleCodons.add("GGA");
				possibleCodons.add("GGG");
			}
		}else{	//unrecognized, is assumed as stop codon
			possibleCodons.add("TAA");
		}

		String[] variations=new String[possibleCodons.size()];
		for(int i=0;i<possibleCodons.size();i++){
			variations[i]=possibleCodons.get(i).toString();
		}
		return variations;
	}
}


