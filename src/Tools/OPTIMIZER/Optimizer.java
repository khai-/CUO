package Tools.OPTIMIZER;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import Support.CAICalculator;
import Support.SelectGeneDialog;
import Support.parser.GenBank;
import Support.parser.Sequence;
import Tools.OPTIMIZER.Filters.CodonUsageOptimizer;
import Tools.OPTIMIZER.Filters.Filter;

public class Optimizer{
	public final Optimizer optimizer=this;
	public final Window OPTIMIZER;
	ArrayList<GenBank> library;
	ArrayList<GenBank> toolbox;
	ArrayList<GenBank> favourite;
	Color bgColor=new Color(70,70,70);
	Color blueBg=new Color(0,62,132);
	Color themeColor=new Color(76,117,35);

	public ResultDialog resultDialog=new ResultDialog();
	public ResultTab resultInProcess;	//the sequence in process now
	public int currentProcess=0;	//the current process in progress (useful for pause and resume)
	public ArrayList<ResultTab> resultList;
	int possibleCombination;	//to store the number of possible combination
	public int currentNumber=1;		//to store currently operating sequence number
	public int[] routeMap;	//very large array to store the route map of all possible combinations

	boolean isRunning=false;	//the optimization is running
	public boolean isHalfWay=false;	//the process is previously paused

	public Sequence target;

	//button panel on the right
	JPanel buttonPanel;
	public JButton addFilterButton;
	public JButton addTargetButton;
	JPanel targetPropertiesPanel;	//target properties
	public JLabel targetTitle;
	JLabel targetSeqLength;
	JLabel targetGCContent;
	JLabel targetmRNASecondaryStructure;
	JButton viewTargetSeq;
	JButton addTargetGenome;	//
	JButton saveFilterButton;
	JButton loadFilterButton;
	public JButton optimizeButton;
	JButton pauseButton;
	JButton resultButton;

	//content panel
	JPanel contentPanel;
	JScrollPane contentScroll;

	//filters
	public ArrayList<Filter> filterList;
	CodonUsageOptimizer CUOFilter;

	//Log Panel
	JPanel logPanel;
	public JTextPane logPane;
	JScrollPane logScroll;
	JProgressBar progressBar;

	public Optimizer(JFrame ownerFrame,ArrayList<GenBank> library,ArrayList<GenBank> toolbox,ArrayList<GenBank> favourite){
		this.library=library;
		this.toolbox=toolbox;
		this.favourite=favourite;
		OPTIMIZER=new JDialog(ownerFrame,false);
		initiate();
	}

	private void initiate(){
		createComponents();
		assignFunctions();
		layoutComponents();
		setupProperties();
	}

	private void createComponents(){
		//button panel
		buttonPanel=new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.Y_AXIS));
		addFilterButton=new JButton("Add Filter");
		addFilterButton.setAlignmentX((float)0.5);
		addTargetButton=new JButton("Add Target");
		addTargetButton.setAlignmentX((float)0.5);
		targetPropertiesPanel=new JPanel();
		targetPropertiesPanel.setLayout(new BoxLayout(targetPropertiesPanel,BoxLayout.Y_AXIS));
		targetTitle=new JLabel("-");
		targetTitle.setAlignmentX((float)0.5);
		targetTitle.setForeground(Color.WHITE);
		targetSeqLength=new JLabel("-");
		targetSeqLength.setAlignmentX((float)0.5);
		targetSeqLength.setForeground(Color.WHITE);
		targetGCContent=new JLabel("-");
		targetGCContent.setAlignmentX((float)0.5);
		targetGCContent.setForeground(Color.WHITE);
		targetmRNASecondaryStructure=new JLabel("-");
		targetmRNASecondaryStructure.setAlignmentX((float)0.5);
		targetmRNASecondaryStructure.setForeground(Color.WHITE);
		viewTargetSeq=new JButton("View CDS");
		viewTargetSeq.setAlignmentX((float)0.5);
		addTargetGenome=new JButton("View Pattern");
		addTargetGenome.setAlignmentX((float)0.5);
		saveFilterButton=new JButton("Save Configurations");
		saveFilterButton.setAlignmentX((float)0.5);
		loadFilterButton=new JButton("Load Configurations");
		loadFilterButton.setAlignmentX((float)0.5);
		optimizeButton=new JButton("    Run    ");
		optimizeButton.setAlignmentX((float)0.5);
		pauseButton=new JButton("  Pause  ");
		pauseButton.setAlignmentX((float)0.5);
		resultButton=new JButton("      Results      ");
		resultButton.setAlignmentX((float)0.5);

		//content panel
		contentPanel=new JPanel();
		contentPanel.setOpaque(false);
		contentPanel.setLayout(new BoxLayout(contentPanel,BoxLayout.Y_AXIS));
		contentScroll=new JScrollPane(contentPanel,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		contentScroll.getViewport().setBackground(blueBg);
		contentScroll.getVerticalScrollBar().setUnitIncrement(10);

		//filters
		filterList=new ArrayList<Filter>();
		CUOFilter=new CodonUsageOptimizer(optimizer,0);
		filterList.add(CUOFilter);

		//log
		logPanel=new JPanel();
		logPanel.setLayout(new BoxLayout(logPanel,BoxLayout.Y_AXIS));
		logPanel.setBackground(bgColor);
		logPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,0));
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date now=new Date();
		logPane=new JTextPane();
		logPane.setText("Starting time: "+sdf.format(now));
		logScroll=new JScrollPane(logPane,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		logScroll.setPreferredSize(new Dimension(550,110));
		logScroll.setMaximumSize(new Dimension(1000000000,110));
		progressBar=new JProgressBar();
		progressBar.setString(currentNumber+"/"+possibleCombination);
		progressBar.setStringPainted(true);
	}

	private void assignFunctions(){
		//buttonPanel
		//addFilterButton
		addFilterButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				new AddFilterDialog(optimizer);
			}
		});

		//addTargetButton
		addTargetButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				final SelectGeneDialog theSGD=new SelectGeneDialog(OPTIMIZER,library,toolbox,favourite,"Add Target");
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
							for(int j=0;j<library.size();j++){	//library
								if(name.equals(library.get(j).dirName)){
									passedGen=library.get(j);
								}
							}
							//for(){	//toolbox
							//}
							//for(){	//favourite
							//}

							newgen=passedGen.copy(sites);//copy only the gene
						}else if(type.equals("fasta")){
							for(int j=0;j<library.size();j++){	//library
								if(name.equals(library.get(j).dirName)){
									passedGen=library.get(j);
								}
							}
							newgen=passedGen.copy();
						}else{
							JOptionPane.showMessageDialog(theSGD.SGD,"Please choose a gene.","Error",JOptionPane.PLAIN_MESSAGE);
							return;
						}

						target=newgen.sequences.get(0);
						//targetPropertiesPanel=
						targetTitle.setText(target.gene);
						targetSeqLength.setText(""+target.getLength());

						optimizeButton.setText("    Run    ");
						//clear the things
						currentProcess=0;
						currentNumber=1;
						resultInProcess=null;
						resultList=new ArrayList<ResultTab>();
						routeMap=null;
						((CodonUsageOptimizer)filterList.get(0)).possibleList=new ArrayList<String[]>();
						((CodonUsageOptimizer)filterList.get(0)).currentRoute=null;
						updateStatus();
						theSGD.SGD.dispose();
					}
				});
			}
		});

		//viewTargetSeq

		//addTargetGenome

		//saveFilterButton

		//loadFilterButton

		//optimizeButton
		optimizeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				//disable some buttons
				addTargetButton.setEnabled(false);
				for(int i=0;i<filterList.size();i++){
					filterList.get(i).filterConfigure.setEnabled(false);
				}
				optimizeButton.setEnabled(false);
				pauseButton.setEnabled(false);
				isRunning=true;

				new Thread(new Runnable(){public void run(){	//shoot out of AWT Thread
					if(!isHalfWay){
						pauseButton.setEnabled(true);

						//count original CAI value
						double oldCAI=CAICalculator.getCAI(((CodonUsageOptimizer)filterList.get(0)).weightTable,target.getCodonFrequency(target.genBank.completeSequence));
						resultDialog.oldCAI=oldCAI;
						log("CAI before optimization = "+oldCAI);

						//count possible combinations
						String combinedTranscript="";
						String[] transcriptArray=target.getTranscript(target.genBank.completeSequence);
						for(int i=0;i<transcriptArray.length;i++){
							combinedTranscript+=transcriptArray[i];
System.out.println(transcriptArray[i]);
						}
						for(int i=0;i<target.getLength();i+=3){
							String codonNow=combinedTranscript.substring(i,i+3);
							((CodonUsageOptimizer)filterList.get(0)).possibleList.add(((CodonUsageOptimizer)filterList.get(0)).generatePossibleCodons(codonNow));
						}
						possibleCombination=1;
						for(int i=0;i<((CodonUsageOptimizer)filterList.get(0)).possibleList.size();i++){
							possibleCombination*=((CodonUsageOptimizer)filterList.get(0)).possibleList.get(i).length;
						}
						progressBar.setMaximum(possibleCombination);
						log("Possible combination = "+possibleCombination);

						//generate routeMap
						routeMap=new int[((CodonUsageOptimizer)filterList.get(0)).possibleList.size()];
						for(int i=0;i<((CodonUsageOptimizer)filterList.get(0)).possibleList.size();i++){
							routeMap[i]=((CodonUsageOptimizer)filterList.get(0)).possibleList.get(i).length-1;
						}

						//create sequence then passthrough filters
						while(currentNumber<=possibleCombination){
							boolean pass=false;
							for(int i=0;i<filterList.size();i++){
								pass=filterList.get(i).trigger();
								if(!pass){
									break;	//throw away the result
								}
							}
							if(!pass){
								if(isHalfWay){
									break;
								}else{
									continue;
								}
							}

							log("Solution discovered at "+currentNumber+"th trial.");

							//generate analysis related to result
							//newCAI
							double newCAI=CAICalculator.getCAI(((CodonUsageOptimizer)filterList.get(0)).weightTable,resultInProcess.resultSequence.getCodonFrequency(resultInProcess.resultSequence.genBank.completeSequence));

							resultInProcess.newCAI=newCAI;
							log("CAI after optimization = "+newCAI);

							//store the result
							resultList.add(resultInProcess);

							currentNumber++;
							try{	//update the GUI at this spot
								SwingUtilities.invokeAndWait(new Runnable(){
									public void run(){
										progressBar.setValue(currentNumber);
										progressBar.setString(progressBar.getValue()+" / "+progressBar.getMaximum());
									}
								});
							}catch(Exception e){e.printStackTrace();}
						}
					}else{
						pauseButton.setEnabled(true);
						isHalfWay=false;

						//create sequence then passthrough filters
						while(currentNumber<=possibleCombination){
							boolean pass=false;
							for(int i=currentProcess;i<filterList.size();i++){
								pass=filterList.get(i).trigger();
								if(!pass){
									break;	//throw away the result
								}
							}

							if(!pass){
								if(isHalfWay){
									break;
								}else{
									continue;
								}
							}

							log("Solution discovered at "+currentNumber+"th trial.");

							//generate analysis related to result
							//newCAI
							double newCAI=CAICalculator.getCAI(((CodonUsageOptimizer)filterList.get(0)).weightTable,resultInProcess.resultSequence.getCodonFrequency(resultInProcess.resultSequence.genBank.completeSequence));
							resultInProcess.newCAI=newCAI;
							log("CAI after optimization = "+newCAI);
						
							//store the result
							resultList.add(resultInProcess);

							currentNumber++;
							try{	//update the GUI at this spot
								SwingUtilities.invokeAndWait(new Runnable(){
									public void run(){
										progressBar.setValue(currentNumber);
										progressBar.setString(progressBar.getValue()+" / "+progressBar.getMaximum());
									}
								});
							}catch(Exception e){e.printStackTrace();}
						}
					}

					//re-enable buttons
					addTargetButton.setEnabled(true);
					for(int i=0;i<filterList.size();i++){
						filterList.get(i).filterConfigure.setEnabled(true);
					}
					if(!isHalfWay)optimizeButton.setText("    Run    ");
					if(!isHalfWay)log("----------PROCESS COMPLETE----------");
					optimizeButton.setEnabled(true);
					pauseButton.setEnabled(false);
					isRunning=false;
				}}).start();
			}
		});

		//pauseButton
		pauseButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				isHalfWay=true;
				optimizeButton.setText("   Resume   ");

				//check any filter is still engaged
				OPTIMIZER.setCursor(new Cursor(Cursor.WAIT_CURSOR));
				boolean stillEngaged=true;
				while(stillEngaged){
					stillEngaged=false;
					for(int i=0;i<filterList.size();i++){
						if(filterList.get(i).isEngaged){
							stillEngaged=true;
							break;
						}
					}
				}

				pauseButton.setEnabled(false);
				optimizeButton.setEnabled(true);
			}
		});

		//resultButton
		resultButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				resultDialog.setVisible(true);
			}
		});
	}

	private void layoutComponents(){
		JPanel mainPanel=new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.X_AXIS));
		mainPanel.setBackground(blueBg);

		JPanel leftPanel=new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel,BoxLayout.Y_AXIS));
		leftPanel.setOpaque(false);

		//content panel
		leftPanel.add(contentScroll);

		//filter
		for(int i=0;i<filterList.size();i++){
			contentPanel.add(filterList.get(i),0);
		}
		//log
		logPanel.add(logScroll);
		logPanel.add(Box.createRigidArea(new Dimension(0,5)));
		JPanel progressPanel=new JPanel();
		progressPanel.setOpaque(false);
		progressPanel.setLayout(new BoxLayout(progressPanel,BoxLayout.X_AXIS));
		JLabel progressLabel=new JLabel("Progress:");
		progressLabel.setForeground(Color.WHITE);
		progressPanel.add(progressLabel);
		progressPanel.add(progressBar);
		logPanel.add(progressPanel);
		leftPanel.add(logPanel);
		mainPanel.add(leftPanel);

		//button panel
		buttonPanel.setBackground(bgColor);
		buttonPanel.setPreferredSize(new Dimension(200,100000));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,5,10,5));
		buttonPanel.add(addFilterButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(0,20)));
		buttonPanel.add(addTargetButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(0,5)));
		targetPropertiesPanel.setOpaque(false);
		targetPropertiesPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		targetPropertiesPanel.add(Box.createRigidArea(new Dimension(0,5)));
		JLabel theTitle=new JLabel("Title: ");
		theTitle.setAlignmentX((float)0.5);
		theTitle.setForeground(Color.WHITE);
		targetPropertiesPanel.add(theTitle);
		targetPropertiesPanel.add(targetTitle);
		JLabel theSeqLength=new JLabel("Sequence length: ");
		theSeqLength.setAlignmentX((float)0.5);
		theSeqLength.setForeground(Color.WHITE);
		targetPropertiesPanel.add(theSeqLength);
		targetPropertiesPanel.add(targetSeqLength);
		JLabel theGCContent=new JLabel("GC Content: ");
		theGCContent.setAlignmentX((float)0.5);
		theGCContent.setForeground(Color.WHITE);
		targetPropertiesPanel.add(theGCContent);
		targetPropertiesPanel.add(targetGCContent);
		JLabel themRNASecondaryStructure=new JLabel("Secondary structure: ");
		themRNASecondaryStructure.setAlignmentX((float)0.5);
		themRNASecondaryStructure.setForeground(Color.WHITE);
		targetPropertiesPanel.add(themRNASecondaryStructure);
		targetPropertiesPanel.add(targetmRNASecondaryStructure);
		targetPropertiesPanel.add(Box.createRigidArea(new Dimension(0,5)));
		targetPropertiesPanel.add(viewTargetSeq);
		targetPropertiesPanel.add(Box.createRigidArea(new Dimension(0,5)));
		targetPropertiesPanel.add(addTargetGenome);
		targetPropertiesPanel.add(Box.createRigidArea(new Dimension(0,10)));
		buttonPanel.add(targetPropertiesPanel);
		buttonPanel.add(Box.createVerticalGlue());
		buttonPanel.add(saveFilterButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(0,5)));
		buttonPanel.add(loadFilterButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(0,15)));
		buttonPanel.add(optimizeButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(0,5)));
		buttonPanel.add(pauseButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(0,5)));
		buttonPanel.add(resultButton);
		mainPanel.add(buttonPanel);
		OPTIMIZER.add(mainPanel);
	}

	private void setupProperties(){
		((JDialog)OPTIMIZER).setTitle("Optimizer");
		((JDialog)OPTIMIZER).setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		OPTIMIZER.setSize(750,500);
		OPTIMIZER.setMinimumSize(new Dimension(750,500));
		OPTIMIZER.setLocationRelativeTo(null);
		OPTIMIZER.setVisible(true);
		updateStatus();
	}

	public void log(String message){
		StyledDocument doc=logPane.getStyledDocument();
		try{
			doc.insertString(doc.getLength(),"\n"+message,null);
		}catch(BadLocationException ble){
			ble.printStackTrace();
		}
		logPane.setCaretPosition(doc.getLength());
	}

	public void updateStatus(){
		//update the filters
		for(int i=0;i<filterList.size();i++){
			filterList.get(i).updateStatus();
		}

		//target sequence added?//filters all ready?
		boolean allReady=true;
		for(int i=0;i<filterList.size();i++){
			if(!filterList.get(i).filterStatus.getText().equals("Ready to run.")){
				allReady=false;
			}
		}
		if(target!=null&&allReady==true){
			optimizeButton.setEnabled(true);
			pauseButton.setEnabled(false);
			return;
		}else{
			optimizeButton.setEnabled(false);
			pauseButton.setEnabled(false);
		}

		//running?
		if(isRunning){
			optimizeButton.setEnabled(false);
			pauseButton.setEnabled(true);
		}
	}
}
