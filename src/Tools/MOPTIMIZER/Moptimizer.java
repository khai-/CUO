package Tools.MOPTIMIZER;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.text.BadLocationException;

import Support.CAICalculator;
import Support.CodonPairCalculator;
import Support.Common;
import Support.Grapher;
import Support.SelectGeneDialog;
import Support.graphics.AminoAcid;
import Support.graphics.Codon;
import Support.graphics.Indicators.CodonPairIndicator;
import Support.graphics.Indicators.WeightMapIndicator;
import Support.parser.Fna;
import Support.parser.GenBank;
import Support.parser.Sequence;
import Tools.TestDigest.TestDigest;

public class Moptimizer extends JDialog{
	JDialog MOPTIMIZER=this;
	Color bgColor=new Color(70,70,70);
	Color themeColor=new Color(76,117,35);
	Color blueBg=new Color(0,62,132);
	ArrayList<GenBank> library;
	ArrayList<GenBank> toolbox;
	ArrayList<GenBank> favourite;

	Sequence target;
	float[] weightTable=new float[64];
	float minimumWeight=(float)0.5;	//default 0.5
	boolean hasWeightTable=false;
	int[] codonFrequencyTable=new int[64];
	boolean hasCodonFrequencyTable=false;
	float[][] codonPairTable=new float[64][64];
	float minimumCodonPair=(float)0.5;	//default 0.5
	boolean hasCodonPairTable=false;

	//target sequence details
	JLabel targetNameLabel;
	JLabel targetName;
	JLabel targetLengthLabel;
	JLabel targetLength;
	JLabel targetAALengthLabel;
	JLabel targetAALength;

	JLabel targetCAILabel;
	JLabel targetCAI;
	JButton showCAIDataButton;
	ImageIcon showDataIcon_up=new ImageIcon("Icons/showData_up.gif");
	ImageIcon showDataIcon_down=new ImageIcon("Icons/showData_down.gif");
	JButton showCAIGraphButton;
	JButton showCPIGraphButton;
	ImageIcon showGraphIcon_up=new ImageIcon("Icons/showGraph_up.gif");
	ImageIcon showGraphIcon_down=new ImageIcon("Icons/showGraph_down.gif");
	JLabel targetCodonPairLabel;
	JLabel targetCPI;
	JButton showCPIDataButton;
	JLabel targetmRNAStructureLabel;
	JLabel targetmRNAStructure;

	JButton selectTargetButton;
	JButton loadWeightTableButton;
	ConfigureDialog configureDialog;
	JButton loadCodonFrequencyButton;
	FrequencyDialog loadCodonFrequencyDialog;
	JButton loadCodonPairButton;
	CodonPairDialog loadCodonPairDialog;

	//toolbar
	JButton generateButton;
	JButton saveButton;
	JButton snapshotButton;
	ImageIcon snapshot_up=new ImageIcon("Icons/snapshot_up.gif");
	ImageIcon snapshot_down=new ImageIcon("Icons/snapshot_down.gif");
	JButton detectSitesButton;
	TestDigest testDigest;

	//control frame
	JPanel controlPanel;
	JScrollPane controlScroll;
	JPanel aminoAcidPanel;	//panel to place amino acid graphics
	JPanel transcriptPanel;	//panel to place transcript graphics
	JPanel weightMapPanel;	//panel to show weight map
	JPanel codonPairPanel;	//panel to show codon pair map

	//view frame
	JLabel viewLabel;
	JToggleButton weightMapButton;
	JToggleButton codonPairButton;
	JTextField goToField;
	JButton goToButton;

	public Moptimizer (JFrame ownerframe,ArrayList<GenBank> library,ArrayList<GenBank> toolbox,ArrayList<GenBank> favourite){
		super(ownerframe,false);
		this.library=library;
		this.toolbox=toolbox;
		this.favourite=favourite;
		initiate();
	}

	private void initiate(){
		createComponents();
		assignFunctions();
		layoutComponents();
		setupProperties();
	}

	private void createComponents(){
		//target sequence details
		targetNameLabel=new JLabel("Target Name:");
		targetNameLabel.setForeground(Color.WHITE);
		targetName=new JLabel("-");
		targetName.setForeground(Color.WHITE);
		targetLengthLabel=new JLabel("Length:");
		targetLengthLabel.setForeground(Color.WHITE);
		targetLength=new JLabel("-");
		targetLength.setForeground(Color.WHITE);
		targetAALengthLabel=new JLabel("AA Length:");
		targetAALengthLabel.setForeground(Color.WHITE);
		targetAALength=new JLabel("-");
		targetAALength.setForeground(Color.WHITE);

		targetCAILabel=new JLabel("Codon Adaptation Index:");
		targetCAILabel.setForeground(Color.WHITE);
		targetCAI=new JLabel("-");
		targetCAI.setForeground(Color.WHITE);
		showCAIDataButton=new JButton(showDataIcon_up);
		showCAIDataButton.setContentAreaFilled(false);
		showCAIDataButton.setBorderPainted(false);
		showCAIDataButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		showCAIGraphButton=new JButton(showGraphIcon_up);
		showCAIGraphButton.setContentAreaFilled(false);
		showCAIGraphButton.setBorderPainted(false);
		showCAIGraphButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		targetCodonPairLabel=new JLabel("Codon Pair Index:");
		targetCodonPairLabel.setForeground(Color.WHITE);
		targetCPI=new JLabel("-");
		targetCPI.setForeground(Color.WHITE);
		showCPIDataButton=new JButton(showDataIcon_up);
		showCPIDataButton.setContentAreaFilled(false);
		showCPIDataButton.setBorderPainted(false);
		showCPIDataButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		showCPIGraphButton=new JButton(showGraphIcon_up);
		showCPIGraphButton.setContentAreaFilled(false);
		showCPIGraphButton.setBorderPainted(false);
		showCPIGraphButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		targetmRNAStructureLabel=new JLabel("mRNA Structure:");
		targetmRNAStructureLabel.setForeground(Color.WHITE);
		targetmRNAStructure=new JLabel("-");
		targetmRNAStructure.setForeground(Color.WHITE);

		Font theFont=new Font(null,Font.PLAIN,9);
		selectTargetButton=new JButton("<html><div width=120><center>Select Target</center></div></html>");
		selectTargetButton.setFont(theFont);
		selectTargetButton.setMaximumSize(new Dimension(150,50));
		loadWeightTableButton=new JButton("<html><div width=120><center>Load Weight Table</center></div></html>");
		loadWeightTableButton.setFont(theFont);
		loadWeightTableButton.setMaximumSize(new Dimension(150,50));
		configureDialog=new ConfigureDialog((Window)MOPTIMIZER,(Moptimizer)MOPTIMIZER);
		loadCodonFrequencyButton=new JButton("<html><div width=120><center>Load Codon Frequency</center></div></html>");
		loadCodonFrequencyButton.setFont(theFont);
		loadCodonFrequencyButton.setMaximumSize(new Dimension(150,50));
		loadCodonFrequencyDialog=new FrequencyDialog((Window)MOPTIMIZER,(Moptimizer)MOPTIMIZER);
		loadCodonPairButton=new JButton("<html><div width=120><center>Load Codon Pair Table</center></div></html>");
		loadCodonPairButton.setFont(theFont);
		loadCodonPairButton.setMaximumSize(new Dimension(150,50));
		loadCodonPairDialog=new CodonPairDialog((Window)MOPTIMIZER,(Moptimizer)MOPTIMIZER);

		//toolbar
		generateButton=new JButton("Generate");
		saveButton=new JButton("Save");
		snapshotButton=new JButton(snapshot_up);
		snapshotButton.setToolTipText("Snapshot");
		snapshotButton.setContentAreaFilled(false);
		snapshotButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		snapshotButton.setBorderPainted(false);
		detectSitesButton=new JButton("Detect sites");

		//control frame
		controlPanel=new JPanel();
		controlPanel.setBackground(Color.WHITE);
		controlPanel.setLayout(new BoxLayout(controlPanel,BoxLayout.Y_AXIS));
		controlScroll=new JScrollPane(controlPanel,JScrollPane.VERTICAL_SCROLLBAR_NEVER,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		controlScroll.getHorizontalScrollBar().setUnitIncrement(10);
		codonPairPanel=new JPanel();
		codonPairPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		codonPairPanel.setOpaque(false);
		codonPairPanel.setLayout(new BoxLayout(codonPairPanel,BoxLayout.Y_AXIS));
		weightMapPanel=new JPanel();
		weightMapPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		weightMapPanel.setOpaque(false);
		weightMapPanel.setLayout(new BoxLayout(weightMapPanel,BoxLayout.X_AXIS));
		aminoAcidPanel=new JPanel();
		aminoAcidPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		aminoAcidPanel.setOpaque(false);
		aminoAcidPanel.setLayout(new BoxLayout(aminoAcidPanel,BoxLayout.X_AXIS));
		transcriptPanel=new JPanel();
		transcriptPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		transcriptPanel.setOpaque(false);
		transcriptPanel.setLayout(new BoxLayout(transcriptPanel,BoxLayout.X_AXIS));

		//view frame
		viewLabel=new JLabel("View:");
		viewLabel.setForeground(Color.WHITE);
		weightMapButton=new JToggleButton("Weight Map");
		weightMapButton.setSelected(true);
		codonPairButton=new JToggleButton("Codon Pair");
		codonPairButton.setSelected(true);
		goToField=new JTextField();
		goToField.setMaximumSize(new Dimension(80,20));
		goToField.setPreferredSize(new Dimension(80,20));
		goToButton=new JButton("Go");
	}

	private void assignFunctions(){
		//showCAIDataButton
		showCAIDataButton.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				showCAIDataButton.setIcon(showDataIcon_down);
			}
			public void mouseReleased(MouseEvent me){
				if(target==null||weightTable==null){
					showCAIDataButton.setIcon(showDataIcon_up);
					return;
				}

				JDialog showDataDialog=new JDialog(MOPTIMIZER);
				JPanel thePanel=new JPanel();
				thePanel.setLayout(new BoxLayout(thePanel,BoxLayout.Y_AXIS));
				thePanel.setBackground(bgColor);
				JTextPane thePane=new JTextPane();

				//get CAI weight of every codons
				String[] sequenceArray=target.getTranscript(target.genBank.completeSequence);
				String codons="";
				for(int i=0;i<sequenceArray.length;i++){	//attenuate all the codons
					codons+=sequenceArray[i];
				}
				int codonNum=codons.length()/3;
				String codonWeights="";
				for(int i=0;i<codonNum;i++){
					String theCodon=codons.substring(i*3,i*3+3);
					int theCodonCoord=Common.getCodonCoordinate(theCodon);
					codonWeights+=weightTable[theCodonCoord]+"\n";
				}

				try{
					thePane.getStyledDocument().insertString(0,codonWeights,null);
					thePane.setCaretPosition(0);
				}catch(BadLocationException ble){}
				JScrollPane theScroll=new JScrollPane(thePane,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				thePanel.add(theScroll);
				showDataDialog.add(thePanel);
				showDataDialog.setSize(150,300);
				showDataDialog.setLocationRelativeTo(null);
				showDataDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				showDataDialog.setVisible(true);
				showCAIDataButton.setIcon(showDataIcon_up);
			}
		});

		//showCAIGraphButton
		showCAIGraphButton.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				showCAIGraphButton.setIcon(showGraphIcon_down);
			}
			public void mouseReleased(MouseEvent me){
				if(target==null||weightTable==null){
					showCAIGraphButton.setIcon(showGraphIcon_up);
					return;
				}

				Grapher grapher=new Grapher(MOPTIMIZER);
				grapher.setTitle(target.gene+" [CAI weight distribution]");
				//get CAI weight of every codons
				String[] sequenceArray=target.getTranscript(target.genBank.completeSequence);
				String codons="";
				for(int i=0;i<sequenceArray.length;i++){	//attenuate all the codons
					codons+=sequenceArray[i];
				}
				int codonNum=codons.length()/3;
				float[] codonWeights=new float[codonNum];
				for(int i=0;i<codonNum;i++){
					String theCodon=codons.substring(i*3,i*3+3);
					int theCodonCoord=Common.getCodonCoordinate(theCodon);
					codonWeights[i]=weightTable[theCodonCoord];
				}

				grapher.setData(codonWeights);
				showCAIGraphButton.setIcon(showGraphIcon_up);
			}
		});

		//showCPIDataButton
		showCPIDataButton.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				showCPIDataButton.setIcon(showDataIcon_down);
			}
			public void mouseReleased(MouseEvent me){
				if(target==null||codonPairTable==null){
					showCPIDataButton.setIcon(showDataIcon_up);
					return;
				}

				JDialog showDataDialog=new JDialog(MOPTIMIZER);
				JPanel thePanel=new JPanel();
				thePanel.setLayout(new BoxLayout(thePanel,BoxLayout.Y_AXIS));
				thePanel.setBackground(bgColor);
				JTextPane thePane=new JTextPane();

				//get CPI weight of every codons
				String[] sequenceArray=target.getTranscript(target.genBank.completeSequence);
				String codons="";
				for(int i=0;i<sequenceArray.length;i++){	//attenuate all the codons
					codons+=sequenceArray[i];
				}
				int codonPairNum=codons.length()/3-1;	//total codon pair numbers
				String codonWeights="";
				for(int i=0;i<codonPairNum;i++){
					String firstCodon=codons.substring(i*3,i*3+3);
					int firstCodonCoord=Common.getCodonCoordinate(firstCodon);
					String secondCodon=codons.substring((i+1)*3,(i+1)*3+3);
					int secondCodonCoord=Common.getCodonCoordinate(secondCodon);
					codonWeights+=codonPairTable[firstCodonCoord][secondCodonCoord]+"\n";
				}

				try{
					thePane.getStyledDocument().insertString(0,codonWeights,null);
					thePane.setCaretPosition(0);
				}catch(BadLocationException ble){}
				JScrollPane theScroll=new JScrollPane(thePane,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				thePanel.add(theScroll);
				showDataDialog.add(thePanel);
				showDataDialog.setSize(150,300);
				showDataDialog.setLocationRelativeTo(null);
				showDataDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				showDataDialog.setVisible(true);
				showCPIDataButton.setIcon(showDataIcon_up);
			}
		});

		//showCPIGraphButton
		showCPIGraphButton.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				showCPIGraphButton.setIcon(showGraphIcon_down);
			}
			public void mouseReleased(MouseEvent me){
				if(target==null||codonPairTable==null){
					showCPIGraphButton.setIcon(showGraphIcon_up);
					return;
				}

				Grapher grapher=new Grapher(MOPTIMIZER);
				grapher.setTitle(target.gene+" [CPI weight distribution]");
				//get CPI weight of every codons
				String[] sequenceArray=target.getTranscript(target.genBank.completeSequence);
				String codons="";
				for(int i=0;i<sequenceArray.length;i++){	//attenuate all the codons
					codons+=sequenceArray[i];
				}
				int codonPairNum=codons.length()/3-1;	//total codon pair numbers
				float[] codonWeights=new float[codonPairNum];
				for(int i=0;i<codonPairNum;i++){
					String firstCodon=codons.substring(i*3,i*3+3);
					int firstCodonCoord=Common.getCodonCoordinate(firstCodon);
					String secondCodon=codons.substring((i+1)*3,(i+1)*3+3);
					int secondCodonCoord=Common.getCodonCoordinate(secondCodon);
					codonWeights[i]=codonPairTable[firstCodonCoord][secondCodonCoord];
				}
				grapher.setData(codonWeights);

				showCPIGraphButton.setIcon(showGraphIcon_up);
			}
		});

		//selectTargetButton
		selectTargetButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				final SelectGeneDialog theSGD=new SelectGeneDialog((Window)MOPTIMIZER,library,toolbox,favourite,"Add Target");
				theSGD.OKButton.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent ae){
						//identify the selected thing and copy selected gene/genome
						if(theSGD.viewer.getSelectedValue()==null){//assure something is chosen
							JOptionPane.showMessageDialog(theSGD.SGD,"Please choose a gene.","Error",JOptionPane.PLAIN_MESSAGE);
							return;
						}
						JPanel thePanel=(JPanel)theSGD.viewer.getSelectedValue();
						String type=((JLabel)thePanel.getComponent(2)).getText();
						GenBank passedGen=null;
						GenBank newgen;
						String name=((JLabel)thePanel.getComponent(1)).getText();
						if(type.equals("gene")){
							final ArrayList<int[]> sites=new ArrayList<int[]>();
							String theText=((JLabel)thePanel.getComponent(3)).getText();
							String[] temp=theText.split("<br>")[1].split("Length:")[0].replaceAll("\\<.*?>","").split(" ");
							for(int j=0;j<temp.length;j++){
								int[] site=new int[2];
								site[0]=Integer.parseInt(temp[j].split("~")[0]);
								site[1]=Integer.parseInt(temp[j].split("~")[1]);
								sites.add(site);
							}

							//get the genBank from library,toolbox and favourite list
							passedGen=Common.getGenBank(name,library,toolbox,favourite);

							newgen=passedGen.copy(sites);//copy only the gene
						}else if(type.equals("fasta")){
							passedGen=Common.getGenBank(name,library,toolbox,favourite);
							newgen=passedGen.copy();
						}else{
							JOptionPane.showMessageDialog(theSGD.SGD,"Please choose a gene.","Error",JOptionPane.PLAIN_MESSAGE);
							return;
						}

						target=newgen.sequences.get(0);
						if(target.getLength()%3>0){	//divisible by 3 test
							JOptionPane.showMessageDialog(theSGD.SGD,"The gene length is not divisble by 3, make sure it is gene only.","Error",JOptionPane.PLAIN_MESSAGE);
							return;
						}
						if(testDigest!=null){	//clearing the panel
							testDigest.genBanks.clear();
							testDigest.genBanks.add(target.genBank);
						}
						//targetDetails
						targetName.setText(target.gene);
						targetLength.setText(""+target.getLength());
						targetAALength.setText(""+target.getAA().length());
						targetCAI.setText("-");
						targetmRNAStructure.setText("-");
						targetCPI.setText("-");

						theSGD.SGD.dispose();
					}
				});
			}
		});

		//loadWeightTableButton
		loadWeightTableButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				configureDialog.setVisible(true);
			}
		});

		//loadCodonFrequencyButton
		loadCodonFrequencyButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				loadCodonFrequencyDialog.setVisible(true);
			}
		});

		//loadCodonPairButton
		loadCodonPairButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				loadCodonPairDialog.setVisible(true);
			}
		});

		//generateButton
		generateButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				//check validity
				if(target==null){
					JOptionPane.showMessageDialog(MOPTIMIZER,"Select a target sequence to optimize.","Select",JOptionPane.PLAIN_MESSAGE);
					return;
				}
				if(!hasWeightTable){
					JOptionPane.showMessageDialog(MOPTIMIZER,"Load weight table first.","Load",JOptionPane.PLAIN_MESSAGE);
					return;
				}
				if(!hasCodonFrequencyTable){
					JOptionPane.showMessageDialog(MOPTIMIZER,"Load codon frequency table first.","Load",JOptionPane.PLAIN_MESSAGE);
					return;
				}

				//createComponents
				final JDialog generateDialog=new JDialog((Window)MOPTIMIZER);
				generateDialog.setModal(true);
				String[] modes={"All the best","Original codons"};
				final JComboBox generateMode=new JComboBox(modes);
				generateMode.setAlignmentX(0);
				generateMode.setPreferredSize(new Dimension(210,25));
				generateMode.setMaximumSize(new Dimension(210,25));
				generateMode.setMinimumSize(new Dimension(210,25));
				final JCheckBox avoidCodonRepeats=new JCheckBox("Avoid codon repeats");
				avoidCodonRepeats.setAlignmentX(0);
				avoidCodonRepeats.setBackground(bgColor);
				avoidCodonRepeats.setForeground(Color.WHITE);
				if(!hasCodonFrequencyTable){
					avoidCodonRepeats.setEnabled(false);
				}
				final JCheckBox avoidBadCodonPairs=new JCheckBox("Avoid bad codon pairs");
				avoidBadCodonPairs.setAlignmentX(0);
				avoidBadCodonPairs.setBackground(bgColor);
				avoidBadCodonPairs.setForeground(Color.WHITE);
				if(!hasCodonPairTable){
					avoidBadCodonPairs.setEnabled(false);
				}
				JButton OKButton=new JButton("OK");
				//assignFunctions
				generateMode.addItemListener(new ItemListener(){
					public void itemStateChanged(ItemEvent ie){
						if(generateMode.getSelectedIndex()==0){	//all the best
							avoidCodonRepeats.setVisible(true);
							avoidBadCodonPairs.setVisible(true);
						}else if(generateMode.getSelectedIndex()==1){	//original
							avoidCodonRepeats.setVisible(false);
							avoidBadCodonPairs.setVisible(false);
						}
					}
				});
				OKButton.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent ae){
						//clear the controlPanel
						for(int i=0;i<controlPanel.getComponentCount();i++){
							if(i==0||i==controlPanel.getComponentCount()-1){	//skip the verticalglues
								continue;
							}
							((JPanel)controlPanel.getComponent(i)).removeAll();
						}

						target.genBank.completeSequence=target.getTranscriptInLine(target.genBank.completeSequence);	//get target sequence

						//original
						if(generateMode.getSelectedIndex()==1){
							//need to do nothing
						}
						//all the best
						else if(generateMode.getSelectedIndex()==0){
							if(avoidCodonRepeats.isSelected()&&(!avoidBadCodonPairs.isSelected())){	//first is chosen only
								String newOptimize="";
								for(int i=0;i<target.genBank.completeSequence.length();i+=3){
									String theCodon=target.genBank.completeSequence.substring(i,i+3);
									int[] allCoord=Common.getAAGroupMembers(theCodon);
									String newCodon="???";
									float totalChance=0;
									for(int j=0;j<allCoord.length;j++){
										if(weightTable[allCoord[j]]>=minimumWeight){
											totalChance+=weightTable[allCoord[j]];
										}
									}
									float chance=new Random().nextFloat()*totalChance;
									for(int j=0;j<allCoord.length;j++){
										if(weightTable[allCoord[j]]>=minimumWeight){
											chance-=weightTable[allCoord[j]];
											if(chance<=0){
												newCodon= Common.getCodonByCoordinate(allCoord[j]);
												break;
											}
										}
									}

									newOptimize+=newCodon;
								}
								target.genBank.completeSequence=newOptimize;
							}else if((!avoidCodonRepeats.isSelected())&&avoidBadCodonPairs.isSelected()){	//second is chosen only
								//all the best only
								String newOptimize="";	//store the new seq
								for(int i=0;i<target.genBank.completeSequence.length();i+=3){
									String theCodon=target.genBank.completeSequence.substring(i,i+3);
									int[] allCoord=Common.getAAGroupMembers(theCodon);
									int highestCoord=Common.getCodonCoordinate(theCodon);
									for(int j=0;j<allCoord.length;j++){
										if(codonFrequencyTable[allCoord[j]]>codonFrequencyTable[highestCoord]){
											highestCoord=allCoord[j];
										}
									}

									newOptimize+=Common.getCodonByCoordinate(highestCoord);
								}

								//filter out bad codon pairs
								String evenNewer="";	//store the codon pair optimized seq
								int passToNextLoop=-1;
								for(int i=0;i<newOptimize.length();i+=3){
									String theCodon;
									if(passToNextLoop==-1){
										theCodon=newOptimize.substring(i,i+3);
									}else{
										theCodon= Common.getCodonByCoordinate(passToNextLoop);
									}

									//skip the last one
									if(i+3==newOptimize.length()){//no pair
										evenNewer+=theCodon;
										break;
									}

									String thePair;
									if(passToNextLoop==-1){
										thePair=newOptimize.substring(i,i+6);
									}else{
										thePair=theCodon+newOptimize.substring(i+3,i+6);
									}

									int first=Common.getCodonCoordinate(thePair.substring(0,3));
									int second=Common.getCodonCoordinate(thePair.substring(3,6));

									//check if the codon pair value pass minimal
									if(codonPairTable[first][second]>minimumCodonPair){
										evenNewer+=theCodon;
										continue;
									}

									int[] firstGroup= Common.getAAGroupMembers(thePair.substring(0,3));
									int[] secondGroup= Common.getAAGroupMembers(thePair.substring(3,6));

									//fix the first codon change the second
									float highestCP=-1;
									for(int j=0;j<secondGroup.length;j++){
										float testedCP=codonPairTable[first][secondGroup[j]];
										if(testedCP>minimumCodonPair&& testedCP>highestCP){
											highestCP=testedCP;
											second=secondGroup[j];
										}
									}
									if(highestCP!=-1){	//found a solution
										evenNewer+=theCodon;
										passToNextLoop=second;
										continue;
									}

									//dynamic solution finding
									//else{
									//}

									//give up...(rely on manual optimizer)
									evenNewer+=theCodon;
System.out.println("Give Up!");

									passToNextLoop=-1;	//no passing
								}
								target.genBank.completeSequence=evenNewer;
							}else if(avoidCodonRepeats.isSelected()&&avoidBadCodonPairs.isSelected()){	//both are chosen
								//avoid codon repeats
								String newOptimize="";
								for(int i=0;i<target.genBank.completeSequence.length();i+=3){
									String theCodon=target.genBank.completeSequence.substring(i,i+3);
									int[] allCoord=Common.getAAGroupMembers(theCodon);
									String newCodon="???";
									float totalChance=0;
									for(int j=0;j<allCoord.length;j++){
										if(weightTable[allCoord[j]]>=minimumWeight){
											totalChance+=weightTable[allCoord[j]];
										}
									}
									float chance=new Random().nextFloat()*totalChance;
									for(int j=0;j<allCoord.length;j++){
										if(weightTable[allCoord[j]]>=minimumWeight){
											chance-=weightTable[allCoord[j]];
											if(chance<=0){
												newCodon= Common.getCodonByCoordinate(allCoord[j]);
												break;
											}
										}
									}

									newOptimize+=newCodon;
								}

								//filter out bad codon pairs
								String evenNewer="";	//store the codon pair optimized seq
								int passToNextLoop=-1;
								for(int i=0;i<newOptimize.length();i+=3){
									String theCodon;
									if(passToNextLoop==-1){
										theCodon=newOptimize.substring(i,i+3);
									}else{
										theCodon= Common.getCodonByCoordinate(passToNextLoop);
									}

									//skip the last one
									if(i+3==newOptimize.length()){//no pair
										evenNewer+=theCodon;
										break;
									}

									String thePair;
									if(passToNextLoop==-1){
										thePair=newOptimize.substring(i,i+6);
									}else{
										thePair=theCodon+newOptimize.substring(i+3,i+6);
									}

									int first=Common.getCodonCoordinate(thePair.substring(0,3));
									int second=Common.getCodonCoordinate(thePair.substring(3,6));

									//check if the codon pair value pass minimal
									if(codonPairTable[first][second]>minimumCodonPair){
										evenNewer+=theCodon;
										passToNextLoop=-1;
										continue;
									}

									int[] firstGroup= Common.getAAGroupMembers(thePair.substring(0,3));
									int[] secondGroup= Common.getAAGroupMembers(thePair.substring(3,6));

									//fix the first codon change the second
									float highestCP=-1;
									for(int j=0;j<secondGroup.length;j++){
										float testedCP=codonPairTable[first][secondGroup[j]];
										if(testedCP>minimumCodonPair&& testedCP>highestCP){
											highestCP=testedCP;
											second=secondGroup[j];
										}
									}
									if(highestCP!=-1){	//found a solution
										evenNewer+=theCodon;
										passToNextLoop=second;//passing
										continue;
									}

									//dynamic solution finding
									//else{
									//}

									//give up...(rely on manual optimizer)
									evenNewer+=theCodon;
System.out.println("Give Up!");

									passToNextLoop=-1;	//no passing
								}
								target.genBank.completeSequence=evenNewer;
							}else{	//none are chosen (all the best only)
								String newOptimize="";	//store the new seq
								for(int i=0;i<target.genBank.completeSequence.length();i+=3){
									String theCodon=target.genBank.completeSequence.substring(i,i+3);
									int[] allCoord=Common.getAAGroupMembers(theCodon);
									int highestCoord=Common.getCodonCoordinate(theCodon);
									for(int j=0;j<allCoord.length;j++){
										if(codonFrequencyTable[allCoord[j]]>codonFrequencyTable[highestCoord]){
											highestCoord=allCoord[j];
										}
									}

									newOptimize+=Common.getCodonByCoordinate(highestCoord);
								}
								target.genBank.completeSequence=newOptimize;
							}
						}

						for(int i=0;i<target.genBank.completeSequence.length();i+=3){
							String theCodon=target.genBank.completeSequence.substring(i,i+3);
							//generate transcript graphics
							transcriptPanel.add(new Codon(theCodon,i+1));

							//generate amino acid graphics
							aminoAcidPanel.add(new AminoAcid(theCodon,i/3+1));

							//generate weight map graphics
							weightMapPanel.add(new WeightMapIndicator(weightTable,minimumWeight,theCodon));
						}
						//generate codon pair map graphics
						JPanel topMap=new JPanel();	//top map
						topMap.setOpaque(false);
						topMap.setLayout(new BoxLayout(topMap,BoxLayout.X_AXIS));
						for(int i=0;i<target.genBank.completeSequence.length();i+=6){
							if(i+3>=target.genBank.completeSequence.length()){//meet the end
								break;
							}

							String thePair=target.genBank.completeSequence.substring(i,i+6);
							topMap.add(new CodonPairIndicator(codonPairTable,minimumCodonPair,thePair));
						}
						if(target.genBank.completeSequence.length()%2!=0){
							topMap.add(Box.createRigidArea(new Dimension(45,25)));
						}
						codonPairPanel.add(topMap);
						JPanel bottomMap=new JPanel();	//bottom map
						bottomMap.setOpaque(false);
						bottomMap.setLayout(new BoxLayout(bottomMap,BoxLayout.X_AXIS));
						bottomMap.add(Box.createRigidArea(new Dimension(45,25)));
						for(int i=3;i<target.genBank.completeSequence.length();i+=6){
							if(i+3>=target.genBank.completeSequence.length()){//meet the end
								break;
							}

							String thePair=target.genBank.completeSequence.substring(i,i+6);
							bottomMap.add(new CodonPairIndicator(codonPairTable,minimumCodonPair,thePair));
						}
						if(target.genBank.completeSequence.length()%2==0){
							bottomMap.add(Box.createRigidArea(new Dimension(45,25)));
						}
						codonPairPanel.add(bottomMap);

						controlPanel.validate();
						controlScroll.validate();
						generateDialog.dispose();

						//update CAI and CPI
						target.countCodonFrequency(target.genBank.completeSequence);
						updateTargetCAI();
						target.countCodonPairFrequency();
						updateTargetCPI();
					}
				});
				//layoutComponents
				JPanel mainPanel=new JPanel();
				mainPanel.setBackground(bgColor);
				mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,25));
				mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
				mainPanel.add(generateMode);
				mainPanel.add(Box.createRigidArea(new Dimension(0,5)));
				JPanel optionPanel=new JPanel();
				optionPanel.setOpaque(false);
				optionPanel.setLayout(new BoxLayout(optionPanel,BoxLayout.Y_AXIS));
				optionPanel.add(avoidCodonRepeats);
				optionPanel.add(avoidBadCodonPairs);
				mainPanel.add(optionPanel);
				mainPanel.add(Box.createVerticalGlue());
				JPanel okPanel=new JPanel();
				okPanel.setOpaque(false);
				okPanel.setLayout(new BoxLayout(okPanel,BoxLayout.X_AXIS));
				okPanel.add(Box.createHorizontalGlue());
				okPanel.add(OKButton);
				okPanel.add(Box.createHorizontalGlue());
				mainPanel.add(okPanel);
				generateDialog.add(mainPanel);
				//setupProperties
				generateDialog.setSize(250,140);
				generateDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				generateDialog.setUndecorated(true);
				generateDialog.setLocationRelativeTo(null);
				generateDialog.setVisible(true);
			}
		});

		//saveButton
		saveButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				//decide workingDirectory
				GenBank newGen=null;//for the library
				final String fileName;
				String dirName;
				JFileChooser jfc=new JFileChooser("."+File.separator+"Library");
				if(jfc.showSaveDialog(MOPTIMIZER)==JFileChooser.APPROVE_OPTION){
					fileName=jfc.getSelectedFile().getName();

					String[] temp1=jfc.getCurrentDirectory().getPath().split("CUO\\"+File.separator+"Library");
					String temp2="."+File.separator+"Library";
					if(temp1.length>1) temp2+=temp1[jfc.getCurrentDirectory().getPath().split("CUO\\"+File.separator+"Library").length-1]+File.separator;
					else temp2+=File.separator;
					dirName=temp2+fileName;
				}else return;

				Fna fna=new Fna();
				fna.fileName=dirName+".fna";
				fna.sequence=target.genBank.completeSequence;
				fna.name=fileName;
				fna.type="Gene";
				newGen=new GenBank(fna);
				try{
					fna.save();
				}catch(Exception e){
					JOptionPane.showMessageDialog(MOPTIMIZER,"Save failed.","Fail",JOptionPane.ERROR_MESSAGE);
					return;
				}

				//update library
				if(newGen!=null){
					newGen.generateListModel();
					library.add(newGen);
				}
			}
		});

		//detectSitesButton
		detectSitesButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				ArrayList<GenBank> genBanks=new ArrayList<GenBank>();
				if(target==null){
					JOptionPane.showMessageDialog(MOPTIMIZER,"Please select a target first.","Error",JOptionPane.PLAIN_MESSAGE);
					return;
				}
				genBanks.add(target.genBank);
				if(testDigest==null){
					testDigest=new TestDigest((Window)MOPTIMIZER,genBanks,library,toolbox,favourite);
					testDigest.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
					JButton autoEliminateButton=new JButton("<html>Auto Eliminate</html>");
					testDigest.execPanel.add(autoEliminateButton,0);
					testDigest.execPanel.add(Box.createRigidArea(new Dimension(0,5)),1);
					autoEliminateButton.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent ae){
							int[] selected=testDigest.resultTable.getSelectedRows();
							if(selected.length==0)return;
							testDigest.detectSitesButton.doClick();//pre-analyse
							for(int i=0;i<selected.length;i++){
								//get all first "in frame" position of the sites
								String[] posStrings=((String)testDigest.resultTable.getValueAt(selected[i],3)).split("\n");
								if(posStrings[0].equals("?"))continue;
								int[] positions=new int[posStrings.length];
								for(int j=0;j<positions.length;j++){
									positions[j]=Integer.parseInt(posStrings[j]);
								}
								int[] inFrame=new int[positions.length];
								for(int j=0;j<inFrame.length;j++){
									inFrame[j]=positions[j]%3;
									if(inFrame[j]==0)inFrame[j]=positions[j]+1;
									if(inFrame[j]==1)inFrame[j]=positions[j];
									if(inFrame[j]==2)inFrame[j]=positions[j]+2;
								}

								//get all possible patterns
								String theRes=testDigest.restrictions.get(selected[i]).completeSequence;
								String[] resPatterns={theRes,new StringBuffer(theRes).reverse().toString(),Common.getOppositeDNAStrand(theRes),new StringBuffer(Common.getOppositeDNAStrand(theRes)).reverse().toString()};	//4 different cutting patterns

								//detect for inverted repeat
								boolean goAhead=true;
								boolean changed=false;
								while(true){
									goAhead=true;
									changed=false;
									for(int n=0;n<resPatterns.length;n++){
										for(int m=0;m<resPatterns.length;m++){
											if(m==n)continue;//no check against itself
											if(resPatterns[n].equals(resPatterns[m])){
												String[] newPatterns=new String[resPatterns.length-1];
												for(int p=0;p<resPatterns.length;p++){
													if(p==m)continue;
													newPatterns[p]=resPatterns[p];
												}
												resPatterns=newPatterns;
												goAhead=false;//terminate this loop
												changed=true;//the array is changed,replay
												break;
											}
										}
										if(!goAhead)break;
									}
									if(!changed)break;
								}

								//dealing with wild card
								String[] resCards=new String[resPatterns.length];
								for(int r=0;r<resPatterns.length;r++){
									resCards[r]=resPatterns[r].replaceAll("N","[ATGC]").replaceAll("R","[AG]").replaceAll("Y","[CTU]").replaceAll("K","[GTU]").replaceAll("M","[AC]").replaceAll("S","[CG]").replaceAll("W","[ATU]").replaceAll("B","[CGTU]").replaceAll("D","[AGTU]").replaceAll("H","[ACTU]").replaceAll("V","[ACG]");
								}

								//replace the sites with new optimized sequences
								for(int j=0;j<testDigest.genBanks.size();j++){
									String temp=new String(testDigest.genBanks.get(j).completeSequence);	//for wild card backtrack

									for(int m=0;m<inFrame.length;m++){
										if(positions[m]-1+theRes.length()>temp.length())continue;

										//get the substring at there
										String check=temp.substring(positions[m]-1,positions[m]-1+theRes.length());

										for(int n=0;n<resCards.length;n++){
											if(check.matches(resCards[n])){
												//generate solution in frame and replace each codon with better one
												for(int h=(inFrame[m]-1);h<(positions[m]+resPatterns[n].length());h+=3){
													if((h+3)>=(positions[m]+resPatterns[n].length()))break;
													String oriCodon=testDigest.genBanks.get(j).completeSequence.substring(h,h+3);
													testDigest.genBanks.get(j).completeSequence=new StringBuilder(testDigest.genBanks.get(j).completeSequence).replace(h,h+3,Common.getBestCodon(oriCodon,codonFrequencyTable,false)).toString();
												}
											}
										}
									}

									//backtrack wild cards
									StringBuilder theBuilder=new StringBuilder(testDigest.genBanks.get(j).completeSequence);
									for(int k=0;k<theBuilder.length();k++){
										if(theBuilder.charAt(k)=='N'){
											theBuilder= theBuilder.replace(k,k+1,""+temp.charAt(k));
										}
									}

									testDigest.genBanks.get(j).completeSequence=theBuilder.toString();
								}
							}
							testDigest.detectSitesButton.doClick();	//rescan
							refreshControlPanel();
						}
					});
				}else{
					testDigest.setVisible(true);
					testDigest.requestFocus();
				}
			}
		});

		//snapshotButton
		snapshotButton.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				snapshotButton.setIcon(snapshot_down);
			}
			public void mouseReleased(MouseEvent me){
				snapshotButton.setIcon(snapshot_up);
				JFileChooser jfc=new JFileChooser("."+File.separator+"Construction");
				jfc.setFileFilter(new javax.swing.filechooser.FileFilter(){
					public boolean accept(File f){
						if(f.isDirectory()){
							return true;
						}
						String extension=f.getName().split("\\.")[f.getName().split("\\.").length-1];
						if(extension.equals("jpeg")){
							return true;
						}
						return false;
					}
					public String getDescription(){
						return "JPEG (*.jpeg)";
					}
				});
				if(jfc.showSaveDialog(MOPTIMIZER)==JFileChooser.APPROVE_OPTION){
					Common.takeSnapshot(controlPanel,jfc.getSelectedFile());
				}else{
					return;
				}
			}
		});

		//transcriptPanel
		transcriptPanel.addMouseListener(new MouseAdapter(){
			public void mouseReleased(MouseEvent me){
				Component theComp=transcriptPanel.getComponentAt(me.getX(),me.getY());
				if(theComp==null){
					return;
				}

				Codon theCodon;
				try{
					theCodon=(Codon)theComp;
				}catch(ClassCastException cce){return;}

				int i=-1;
				boolean exist=false;
				for(int j=0;j<transcriptPanel.getComponentCount();j++){
					if(transcriptPanel.getComponent(j)==theCodon){
						i=j;
						exist=true;
						break;
					}
				}
				if(!exist)return;

				int newCoord=-1;
				int coord=Common.getCodonCoordinate(((Codon)theComp).codon);
				int[] allCoord=Common.getAAGroupMembers(((Codon)theComp).codon);
				for(int j=0;j<allCoord.length;j++){
					if(allCoord[j]==coord){
						if(j!=allCoord.length-1){
							newCoord=allCoord[j+1];
						}else{
							newCoord=allCoord[0];
						}
						break;
					}
				}
				String newCodon=Common.getCodonByCoordinate(newCoord).replace('T','U');
				transcriptPanel.remove(i);
				transcriptPanel.add(new Codon(newCodon,i*3+1),i);

				//update target.genBank.completeSequence
				String frontFraction=target.genBank.completeSequence.substring(0,i*3);
				String backFraction=target.genBank.completeSequence.substring(i*3+3,target.genBank.completeSequence.length());
				target.genBank.completeSequence="";
				target.genBank.completeSequence+=frontFraction+newCodon.replace('U','T')+backFraction;

				//update control panel
				refreshControlPanel();

				//update CAI and CPI
				target.countCodonFrequency(target.genBank.completeSequence);
				updateTargetCAI();
				target.countCodonPairFrequency();
				updateTargetCPI();
			}
		});

		//viewFrame
		//weightMapButton
		weightMapButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				if(weightMapButton.isSelected()){
					weightMapPanel.setVisible(true);
				}else{
					weightMapPanel.setVisible(false);
				}
			}
		});
		//codonPairButton
		codonPairButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				if(codonPairButton.isSelected()){
					codonPairPanel.setVisible(true);
				}else{
					codonPairPanel.setVisible(false);
				}
			}
		});
		//goToField
		goToField.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				goToButton.doClick();
			}
		});
		//goToButton
		goToButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				//check whether any target was selected
				if(target==null){
					JOptionPane.showMessageDialog(MOPTIMIZER,"Please select a target sequence first.","Error",JOptionPane.ERROR_MESSAGE);
					return;
				}

				//check number format
				int goPos=-1;
				try{
					goPos=Integer.parseInt(goToField.getText());
				}catch(NumberFormatException nfe){
					JOptionPane.showMessageDialog(MOPTIMIZER,"Please enter position as number.","Error",JOptionPane.ERROR_MESSAGE);
					return;
				}

				//check for valid number
				if(goPos<=0||goPos>target.genBank.completeSequence.length()){
					JOptionPane.showMessageDialog(MOPTIMIZER,"Position out of bound.","Error",JOptionPane.ERROR_MESSAGE);
					return;
				}

				Component theNuc=((JPanel)transcriptPanel.getComponent((goPos-1)/3)).getComponent((goPos-1)%3+1);//find out the nucleotide
				Rectangle rc=theNuc.getBounds();
				rc.x+=theNuc.getParent().getX();
				rc.y+=theNuc.getParent().getY();

				controlPanel.scrollRectToVisible(rc);//scroll to view
			}
		});
	}

	//function to update control panel graphic
	public void refreshControlPanel(){
		//clear the controlPanel
		for(int j=0;j<controlPanel.getComponentCount();j++){
			if(j==0||j==controlPanel.getComponentCount()-1){	//skip the verticalglues
				continue;
			}
			((JPanel)controlPanel.getComponent(j)).removeAll();
		}

		//reform information panels
		for(int j=0;j<target.genBank.completeSequence.length();j+=3){
			String theCodon=target.genBank.completeSequence.substring(j,j+3);
			//generate transcript graphics
			transcriptPanel.add(new Codon(theCodon,j+1));

			//generate amino acid graphics
			aminoAcidPanel.add(new AminoAcid(theCodon,j/3+1));

			//generate weight map graphics
			weightMapPanel.add(new WeightMapIndicator(weightTable,minimumWeight,theCodon));
		}

		//generate codon pair map graphics
		JPanel topMap=new JPanel();	//top map
		topMap.setOpaque(false);
		topMap.setLayout(new BoxLayout(topMap,BoxLayout.X_AXIS));
		for(int j=0;j<target.genBank.completeSequence.length();j+=6){
			if(j+3>=target.genBank.completeSequence.length()){//meet the end
				break;
			}

			String thePair=target.genBank.completeSequence.substring(j,j+6);
			topMap.add(new CodonPairIndicator(codonPairTable,minimumCodonPair,thePair));
		}
		if(target.genBank.completeSequence.length()%2!=0){
			topMap.add(Box.createRigidArea(new Dimension(45,25)));
		}
		codonPairPanel.add(topMap);

		JPanel bottomMap=new JPanel();	//bottom map
		bottomMap.setOpaque(false);
		bottomMap.setLayout(new BoxLayout(bottomMap,BoxLayout.X_AXIS));
		bottomMap.add(Box.createRigidArea(new Dimension(45,25)));
		for(int j=3;j<target.genBank.completeSequence.length();j+=6){
			if(j+3>=target.genBank.completeSequence.length()){//meet the end
				break;
			}
			String thePair=target.genBank.completeSequence.substring(j,j+6);
			bottomMap.add(new CodonPairIndicator(codonPairTable,minimumCodonPair,thePair));
		}
		if(target.genBank.completeSequence.length()%2==0){
			bottomMap.add(Box.createRigidArea(new Dimension(45,25)));
		}
		codonPairPanel.add(bottomMap);

		controlPanel.validate();
		controlScroll.validate();
	}

	//update target CAI value
	public void updateTargetCAI(){
		ArrayList<Sequence> temp=new ArrayList<Sequence>();
		temp.add(target);
		double theCAI=CAICalculator.getCAI(weightTable,target.getCodonFrequency(target.genBank.completeSequence));
		DecimalFormat df=new DecimalFormat("0.000");
		df.setRoundingMode(RoundingMode.HALF_UP);
		targetCAI.setText(df.format(theCAI));
	}

	//update target CPI value
	public void updateTargetCPI(){
		if(codonPairTable==null)return;

		double theCPI=CodonPairCalculator.getCPI(codonPairTable,target.getCodonPairFrequency());
		DecimalFormat df=new DecimalFormat("0.000");
		df.setRoundingMode(RoundingMode.HALF_UP);
		targetCPI.setText(df.format(theCPI));
	}

	private void layoutComponents(){
		JPanel mainPanel=new JPanel();
		mainPanel.setBackground(bgColor);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.X_AXIS));

		JPanel leftPanel=new JPanel();
		leftPanel.setOpaque(false);
		leftPanel.setLayout(new BoxLayout(leftPanel,BoxLayout.Y_AXIS));

		//target details
		JPanel targetDetailsPanel=new JPanel();
		targetDetailsPanel.setOpaque(false);
		targetDetailsPanel.setLayout(new BoxLayout(targetDetailsPanel,BoxLayout.X_AXIS));
		JPanel targetDetailsLeft=new JPanel();
		targetDetailsLeft.setOpaque(false);
		targetDetailsLeft.setLayout(new BoxLayout(targetDetailsLeft,BoxLayout.Y_AXIS));
		targetDetailsLeft.add(targetNameLabel);
		targetDetailsLeft.add(targetName);
		targetDetailsLeft.add(targetLengthLabel);
		targetDetailsLeft.add(targetLength);
		targetDetailsLeft.add(targetAALengthLabel);
		targetDetailsLeft.add(targetAALength);
		targetDetailsPanel.add(targetDetailsLeft);
		JPanel targetDetailsMid=new JPanel();
		targetDetailsMid.setOpaque(false);
		targetDetailsMid.setLayout(new BoxLayout(targetDetailsMid,BoxLayout.Y_AXIS));
		targetDetailsMid.add(targetCAILabel);
		JPanel targetCAIPanel=new JPanel();
		targetCAIPanel.setAlignmentX(0f);
		targetCAIPanel.setOpaque(false);
		targetCAIPanel.setLayout(new BoxLayout(targetCAIPanel,BoxLayout.X_AXIS));
		targetCAIPanel.add(targetCAI);
		targetCAIPanel.add(Box.createRigidArea(new Dimension(5,0)));
		targetCAIPanel.add(showCAIDataButton);
		targetCAIPanel.add(showCAIGraphButton);
		targetDetailsMid.add(targetCAIPanel);
		targetDetailsMid.add(targetCodonPairLabel);
		JPanel targetCPIPanel=new JPanel();
		targetCPIPanel.setAlignmentX(0f);
		targetCPIPanel.setOpaque(false);
		targetCPIPanel.setLayout(new BoxLayout(targetCPIPanel,BoxLayout.X_AXIS));
		targetCPIPanel.add(targetCPI);
		targetCPIPanel.add(Box.createRigidArea(new Dimension(5,0)));
		targetCPIPanel.add(showCPIDataButton);
		targetCPIPanel.add(showCPIGraphButton);
		targetDetailsMid.add(targetCPIPanel);
		targetDetailsMid.add(targetmRNAStructureLabel);
		targetDetailsMid.add(targetmRNAStructure);
		targetDetailsPanel.add(Box.createRigidArea(new Dimension(10,0)));
		targetDetailsPanel.add(targetDetailsMid);
		JPanel targetDetailsRight=new JPanel();
		targetDetailsRight.setOpaque(false);
		targetDetailsRight.setLayout(new BoxLayout(targetDetailsRight,BoxLayout.Y_AXIS));
		targetDetailsRight.add(selectTargetButton);
		targetDetailsRight.add(Box.createRigidArea(new Dimension(0,5)));
		targetDetailsRight.add(loadWeightTableButton);
		targetDetailsRight.add(Box.createRigidArea(new Dimension(0,5)));
		targetDetailsRight.add(loadCodonFrequencyButton);
		targetDetailsRight.add(Box.createRigidArea(new Dimension(0,5)));
		targetDetailsRight.add(loadCodonPairButton);
		targetDetailsRight.add(Box.createRigidArea(new Dimension(0,5)));
		targetDetailsPanel.add(Box.createHorizontalGlue());
		targetDetailsPanel.add(targetDetailsRight);

		leftPanel.add(targetDetailsPanel);

		//toolbar
		JPanel toolbarPanel=new JPanel();
		toolbarPanel.setBackground(themeColor);
		toolbarPanel.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
		toolbarPanel.setLayout(new BoxLayout(toolbarPanel,BoxLayout.X_AXIS));
		toolbarPanel.add(generateButton);
		toolbarPanel.add(Box.createRigidArea(new Dimension(5,0)));
		toolbarPanel.add(saveButton);
		toolbarPanel.add(Box.createRigidArea(new Dimension(10,0)));
		toolbarPanel.add(snapshotButton);
		toolbarPanel.add(Box.createHorizontalGlue());
		toolbarPanel.add(detectSitesButton);
		leftPanel.add(toolbarPanel);

		//control frame
		controlPanel.add(Box.createVerticalGlue());
		controlPanel.add(codonPairPanel);
		controlPanel.add(weightMapPanel);
		controlPanel.add(aminoAcidPanel);
		controlPanel.add(transcriptPanel);
		controlPanel.add(Box.createVerticalGlue());
		leftPanel.add(controlScroll);
		leftPanel.add(Box.createRigidArea(new Dimension(0,5)));

		//view frame
		JPanel viewButtonsPanel=new JPanel();
		viewButtonsPanel.setBackground(blueBg);
		viewButtonsPanel.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
		viewButtonsPanel.setLayout(new BoxLayout(viewButtonsPanel,BoxLayout.X_AXIS));
		viewButtonsPanel.add(viewLabel);
		viewButtonsPanel.add(Box.createRigidArea(new Dimension(5,0)));
		viewButtonsPanel.add(weightMapButton);
		viewButtonsPanel.add(Box.createRigidArea(new Dimension(5,0)));
		viewButtonsPanel.add(codonPairButton);
		viewButtonsPanel.add(Box.createHorizontalGlue());
		viewButtonsPanel.add(goToField);
		viewButtonsPanel.add(goToButton);
		leftPanel.add(viewButtonsPanel);

		mainPanel.add(leftPanel);
		add(mainPanel);
	}

	private void setupProperties(){
		setTitle("Manual Optimizer");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setSize(700,500);
		setLocationRelativeTo(null);
		setVisible(true);
	}
}
