package Tools.CSR;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import Support.parser.GenBank;

public class CSR{
	Window CSR;
	boolean independent;
	Color bgColor=new Color(70,70,70);
	Color filterColor=new Color(220,240,180);

	ArrayList<GenBank> library;

	JPanel searchPanel;
	Frame getFrame;
	JTextField getField;
	JComboBox getCombo;
	JComboBox getSuffix;
	Thread searchThread;

	JPanel statusPanel;
	JLabel statusIcon;
	ImageIcon wheel=new ImageIcon("Icons"+File.separator+"wheel.gif");
	ImageIcon wheel_animated=new ImageIcon("Icons"+File.separator+"wheel_animated.gif");
	ImageIcon note=new ImageIcon("Icons"+File.separator+"note.gif");
	ImageIcon tea=new ImageIcon("Icons"+File.separator+"tea.gif");
	ImageIcon previous_up=new ImageIcon("Icons"+File.separator+"previous_up.gif");
	ImageIcon previous_down=new ImageIcon("Icons"+File.separator+"previous_down.gif");
	ImageIcon next_up=new ImageIcon("Icons"+File.separator+"next_up.gif");
	ImageIcon next_down=new ImageIcon("Icons"+File.separator+"next_down.gif");
	ImageIcon download=new ImageIcon("Icons"+File.separator+"download_up.gif");
	JLabel statusLabel;
	JToggleButton filterButton;
	ArrayList<ArrayList<String>> filterTempo;
	JButton previousButton;
	JButton nextButton;
	JSpinner resultSpinner;
	JToggleButton resultAll;

	JPanel foundPanel;
	JList foundList;
	int indexBuffer=-1;
	JScrollPane foundScroll;
	int page=0;	//0--9 pages
	LinkedList<ArrayList<ArrayList<String>>> foundHistory;	//wooo

	JPanel buttonPanel;
	JButton selectAllButton;
	ImageIcon funnel_up=new ImageIcon("Icons"+File.separator+"funnel_up.gif");
	ImageIcon funnel_down=new ImageIcon("Icons"+File.separator+"funnel_down.gif");
	JToggleButton compact;
	boolean compactMode=false;
	JButton retrieveButton;

	JFileChooser fc;

	public CSR(JFrame ownerFrame,ArrayList<GenBank> library){	//bound to CUO or other application
		CSR=new JDialog(ownerFrame,false);	//true for modal
		this.library=library;
		independent=false;
		initiate();
	}

	public CSR(){	//standalone execution
		CSR=new JFrame();	//differentiate from jdialog as top level container(has taskbar icon...)
		independent=true;
		initiate();
	}

	private void initiate(){
		createComponents();
		assignFunctions();
		layoutComponents();
		setupProperties();
	}

	private void createComponents(){
		//search panel
		searchPanel=new JPanel();
		getField=new JTextField(25);
		getField.setMaximumSize(new Dimension(10000,25));
		getCombo=new JComboBox();
		String[] comboList={"Nucleotide"};
		getCombo.setModel(new DefaultComboBoxModel(comboList));
		getCombo.setMaximumSize(new Dimension(100,25));
		getSuffix=new JComboBox();
		String[] suffixList={"","[orgn]","[gene]","[prot]","[titl]","[uid]","[accn]","[slen]","[auth]"};
		getSuffix.setModel(new DefaultComboBoxModel(suffixList));
		getSuffix.setMaximumSize(new Dimension(30,25));

		//status panel
		statusPanel=new JPanel();
		statusIcon=new JLabel();		
		statusIcon.setIcon(wheel);
		statusLabel=new JLabel("Waiting input.");
		statusLabel.setForeground(Color.WHITE);
		resultSpinner=new JSpinner(new SpinnerNumberModel(20,1,null,5));//init 20,min 1,max 500,step 5
		resultSpinner.setMaximumSize(new Dimension(60,24));	//double comp use number divisible by 2 if possible
		resultSpinner.setPreferredSize(new Dimension(45,24));
		resultAll=new JToggleButton("<html><font size=2>All</font></html>");
		resultAll.setMaximumSize(new Dimension(20,25));
		filterButton=new JToggleButton(funnel_up);
		filterButton.setBorderPainted(false);
		filterButton.setContentAreaFilled(false);
		filterButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		filterButton.setVisible(false);
		previousButton=new JButton(previous_up);
		previousButton.setBorderPainted(false);
		previousButton.setContentAreaFilled(false);
		previousButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		previousButton.setVisible(false);	//only show when result shown
		nextButton=new JButton(next_up);
		nextButton.setBorderPainted(false);
		nextButton.setContentAreaFilled(false);
		nextButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		nextButton.setVisible(false);

		//found panel
		foundPanel=new JPanel();
		foundList=new JList();
		foundScroll=new JScrollPane(foundList,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		foundHistory=new LinkedList<ArrayList<ArrayList<String>>>();	//for storing found history
		ArrayList<ArrayList<String>> firstPage=new ArrayList<ArrayList<String>>();
		for(int i=0;i<8;i++)firstPage.add(new ArrayList<String>());
		firstPage.get(7).add("0");
		firstPage.get(7).add("");
		foundHistory.add(firstPage);	//first empty page added manually here

		//decision buttons panel
		buttonPanel=new JPanel();
		selectAllButton=new JButton("Select All");
		retrieveButton=new JButton("Retrieve");
		fc=new JFileChooser(new File("Library"+File.separator+""));
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		compact=new JToggleButton("Compact");
	}

	private void assignFunctions(){
		//getField,getButton,resultSpinner
		getField.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				//terminate previous searchThread
				if(searchThread!=null){
					searchThread.interrupt();
				}

				//filter mode
				if(filterButton.isSelected()){
					searchThread=new Thread(new Runnable(){	//toss out of AWT thread
						public void run(){
							statusIcon.setIcon(wheel_animated);
							statusLabel.setText("Filtering...");
							filterButton.setVisible(false);
							previousButton.setVisible(false);
							nextButton.setVisible(false);

							ArrayList<ArrayList<String>> filterSummary=new ArrayList<ArrayList<String>>();
							for(int i=0;i<8;i++)filterSummary.add(new ArrayList<String>());

							String filterTerm=getField.getText();
							if(Thread.currentThread().isInterrupted())return;//check thread state
							ArrayList<ArrayList<String>> currentSummary;
							if(filterTempo==null){
								currentSummary=foundHistory.get(page);
							}else{
								currentSummary=filterTempo;
							}
							for(int i=0;i<currentSummary.get(0).size();i++){
								if(currentSummary.get(0).get(i).toLowerCase().indexOf(filterTerm.toLowerCase())>=0){	//if the title contains search term
									filterSummary.get(0).add(currentSummary.get(0).get(i));
									filterSummary.get(1).add(currentSummary.get(1).get(i));
									filterSummary.get(2).add(currentSummary.get(2).get(i));
									filterSummary.get(3).add(currentSummary.get(3).get(i));
									filterSummary.get(4).add(currentSummary.get(4).get(i));
									filterSummary.get(5).add(currentSummary.get(5).get(i));
									filterSummary.get(6).add(currentSummary.get(6).get(i));
								}
							}
							filterSummary.get(7).add(currentSummary.get(7).get(0));
							filterSummary.get(7).add(currentSummary.get(7).get(1));
							filterSummary.get(7).add(currentSummary.get(7).get(2));

							if(Thread.currentThread().isInterrupted())return;//check thread state
							if(filterSummary.get(0).size()>0){
								filterTempo=filterSummary;//save to temporary memory
								foundList.setModel(showSummary(filterSummary));
								statusIcon.setIcon(tea);
								statusLabel.setText(filterSummary.get(0).size()+" filtered.");
							}else{
								statusIcon.setIcon(note);
								statusLabel.setText("No filter result found.");
							}
							filterButton.setVisible(true);
							previousButton.setVisible(true);
							nextButton.setVisible(true);
						}
					});
					searchThread.start();
					return;
				}

				//update status panel
				filterButton.setVisible(false);
				previousButton.setVisible(false);
				nextButton.setVisible(false);
				statusIcon.setIcon(wheel_animated);
				statusLabel.setText("Requesting UIDs...");

				searchThread=new Thread(new Runnable(){	//toss the delay out of AWT thread
					public void run(){
						ArrayList<String> UID=new ArrayList<String>();
						String numberOfResults=null;
						String getTerm=getField.getText();
						String database=(String) getCombo.getSelectedItem();
						String term=getField.getText()+(String)getSuffix.getSelectedItem();;
						int retmax=20;
						//setting retmax(the results number)
						if(resultAll.isSelected()){
							retmax=100000;	//really burn RAM out
						}else{
							retmax=Integer.parseInt(resultSpinner.getValue().toString());
						}

						try{	//retrieve UIDs
							ArrayList<ArrayList<String>> searchResult=eSearch(database,term,retmax);
							if(Thread.currentThread().isInterrupted())return;//check thread state
							numberOfResults=searchResult.get(0).get(0);
							UID=searchResult.get(1);
						}catch(Exception e){
							statusIcon.setIcon(note);
							statusLabel.setText("Error retrieving UIDs.");
							filterButton.setVisible(true);
							previousButton.setVisible(true);
							nextButton.setVisible(true);
							return;
						}
						if(UID.size()==0){	//no result returned
							statusIcon.setIcon(note);
							statusLabel.setText("No UID returned.");
							filterButton.setVisible(true);
							previousButton.setVisible(true);
							nextButton.setVisible(true);
							return;
						}else{	//get and present summary
							statusLabel.setText("Requesting data description...");
							ArrayList<ArrayList<String>> summary=new ArrayList<ArrayList<String>>();
							try{
								summary=eSummary(database,UID);
								if(Thread.currentThread().isInterrupted())return;
							}catch(Exception e){
								statusIcon.setIcon(note);
								statusLabel.setText("Error retrieving summaries.");
								filterButton.setVisible(true);
								previousButton.setVisible(true);
								nextButton.setVisible(true);
								return;
							}
							if(summary.size()==0){
								statusIcon.setIcon(note);
								statusLabel.setText("No summary returned.");
								filterButton.setVisible(true);
								previousButton.setVisible(true);
								nextButton.setVisible(true);
								return;
							}else{	//display the summary in found list
								if(Thread.currentThread().isInterrupted())return;

								foundList.setModel(new DefaultListModel());//clear list
								foundList.setModel(showSummary(summary));//add the new one

								//update status icon and label
								statusIcon.setIcon(tea);
								statusLabel.setText(numberOfResults+" found. "+UID.size()+" shown.");
								filterButton.setVisible(true);
								previousButton.setVisible(true);
								nextButton.setVisible(true);

								//record the summary into foundHistory
								ArrayList<String> theTerm=new ArrayList<String>();
								theTerm.add(numberOfResults);
								theTerm.add(getTerm);
								theTerm.add(database);
								summary.add(theTerm);
								if(foundHistory.size()>=10){	//max ten storage
									foundHistory.add(summary);
									foundHistory.remove(0);
									page=9;
								}else{
									foundHistory.add(summary);
									page=(foundHistory.size()-1);
								}
							}
						}
					}
				});
				searchThread.start();
			}
		});

		//all button
		resultAll.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent ie){
				if(ie.getStateChange()==ie.SELECTED){
					resultSpinner.setEnabled(false);
				}else{
					resultSpinner.setEnabled(true);
				}
			}
		});

		//filterButton
		filterButton.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent ie){
				if(ie.getStateChange()==ie.SELECTED){
					filterButton.setIcon(funnel_down);
					getField.setBackground(filterColor);
				}else{
					filterButton.setIcon(funnel_up);
					filterTempo=null;	//throw away the temporary memory
					getField.setBackground(Color.WHITE);//suppose to use UI color?

					ArrayList<ArrayList<String>> original=foundHistory.get(page);
					statusIcon.setIcon(tea);
					statusLabel.setText(original.get(7).get(0)+" found. "+original.get(0).size()+" shown.");
					getField.setText(original.get(7).get(1));
					foundList.setModel(showSummary(original));
				}
			}
		});

		//previousButton
		previousButton.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				previousButton.setIcon(previous_down);
			}
			public void mouseReleased(MouseEvent me){
				if(page!=0){
					page--;
					ArrayList<ArrayList<String>> theSummary=foundHistory.get(page);
					foundList.setModel(showSummary(theSummary));
					filterTempo=null;
					statusIcon.setIcon(tea);
					statusLabel.setText(theSummary.get(7).get(0)+" found. "+theSummary.get(0).size()+" shown.");
					getField.setText(theSummary.get(7).get(1));
				}
				previousButton.setIcon(previous_up);
			}
		});

		//nextButton
		nextButton.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				nextButton.setIcon(next_down);
			}
			public void mouseReleased(MouseEvent me){
				if(page!=9&&page!=(foundHistory.size()-1)){
					page++;
					ArrayList<ArrayList<String>> theSummary=foundHistory.get(page);
					foundList.setModel(showSummary(theSummary));
					filterTempo=null;
					statusIcon.setIcon(tea);
					statusLabel.setText(theSummary.get(7).get(0)+" found. "+theSummary.get(0).size()+" shown.");
					getField.setText(theSummary.get(7).get(1));
				}
				nextButton.setIcon(next_up);
			}
		});

		//foundList
		foundList.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent me){
				if(me.getClickCount()==2){
					if(compactMode){
						ArrayList<ArrayList<String>> currentSummary;
						if(filterTempo==null){
							currentSummary=foundHistory.get(page);
						}else{
							currentSummary=filterTempo;
						}
						int index=foundList.locationToIndex(me.getPoint());

						//shrink previous extension
						if(indexBuffer!=-1){
							((DefaultListModel)foundList.getModel()).set(indexBuffer,"<html><font color=rgb(43,60,170)><b><u>"+currentSummary.get(0).get(indexBuffer)+"</b></u></font><br><hr></html>");//replace extended view with original
							if(indexBuffer==index){	//shrink current extension
								indexBuffer=-1;
								return;
							}
						}

						//new extension
						indexBuffer=index;
						String original=(String)foundList.getModel().getElementAt(index);
							//seqlength,refseq,GI
						String addOn="<html><font size=2>Length: "+currentSummary.get(1).get(index)+"&nbsp;&nbsp;&nbsp;RefSeq: "+currentSummary.get(2).get(index)+"&nbsp;&nbsp;GI: "+currentSummary.get(3).get(index)+"<br>";
							//createDate,updateDate
						addOn+="Create Date: "+currentSummary.get(4).get(index)+"  Update Date: "+currentSummary.get(5).get(index)+"</font><hr></html>";

						String hybrid=original.substring(0,original.length()-11)+addOn;
						
						((DefaultListModel)foundList.getModel()).set(index,hybrid);//replace the original compact view
					}	
				}
			}
		});

		//select all button
		selectAllButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				foundList.setSelectionInterval(0,(foundList.getModel().getSize()-1));
			}
		});

		//compact button
		compact.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent ie){
				if(ie.getStateChange()==ie.SELECTED){
					compactMode=true;
					if(filterTempo==null){
						foundList.setModel(showSummary(foundHistory.get(page)));
					}else{
						foundList.setModel(showSummary(filterTempo));
					}
				}else{
					compactMode=false;
					indexBuffer=-1;
					if(filterTempo==null){
						foundList.setModel(showSummary(foundHistory.get(page)));
					}else{
						foundList.setModel(showSummary(filterTempo));
					}
				}
			}
		});

		//retrieve button
		retrieveButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				int reaction=fc.showSaveDialog(CSR);
				if(reaction==JFileChooser.APPROVE_OPTION){
					fc.setCurrentDirectory(new File(fc.getCurrentDirectory()+File.separator+"Library"));
					if(foundList.getSelectedIndex()==-1)return;	//if nothing selected,abandon action
					Thread retrieveThread=new Thread(new Runnable(){//spawn a thread for each retrieval
						public void run(){
							//get currentSummary,directory,indices and total seqlength
							ArrayList<ArrayList<String>> currentSummary;
							if(filterTempo==null){
								currentSummary=foundHistory.get(page);
							}else{
								currentSummary=filterTempo;
							}
							String database=currentSummary.get(7).get(2);
							File directory=fc.getSelectedFile();
							int[] indices=foundList.getSelectedIndices();
							int totalseqlength=0;
							for(int i=0;i<indices.length;i++){	//retreive total seqlength
								totalseqlength+=Integer.parseInt(currentSummary.get(1).get(indices[i]));
							}


							//create progressBar
							final Thread downloadThread=Thread.currentThread();
							final JFrame progressFrame=new JFrame();
							JTextPane progressText=new JTextPane();
							StyledDocument doc=progressText.getStyledDocument();
							SimpleAttributeSet keyword=new SimpleAttributeSet();
							StyleConstants.setForeground(keyword,new Color(43,60,170));
							StyleConstants.setFontSize(keyword,9);
							StyleConstants.setBold(keyword,true);
							StyleConstants.setUnderline(keyword,true);
							progressText.setEditable(false);
							JScrollPane progressScroll=new JScrollPane(progressText,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
							JProgressBar progressBar=new JProgressBar();
							progressBar.setMinimum(0);
							progressBar.setMaximum(totalseqlength);
							progressBar.setStringPainted(true);
							JLabel processingWheel=new JLabel(wheel_animated);
							JButton cancelButton=new JButton("Cancel");
							//assign functions
							cancelButton.addActionListener(new ActionListener(){
								public void actionPerformed(ActionEvent ae){
									downloadThread.interrupt();//must not use Thread.currentThread() here as it will interrupt awt thread instead
									progressFrame.dispose();
								}
							});
							progressFrame.addWindowListener(new WindowAdapter(){
								public void windowClosing(WindowEvent we){
									downloadThread.interrupt();
								}
							});
							//layout
							JPanel barAndButton=new JPanel();
							barAndButton.setOpaque(false);
							barAndButton.setLayout(new BoxLayout(barAndButton,BoxLayout.X_AXIS));
							barAndButton.add(processingWheel);
							barAndButton.add(progressBar);
							barAndButton.add(Box.createRigidArea(new Dimension(5,0)));
							barAndButton.add(cancelButton);
							progressFrame.getContentPane().setLayout(new BoxLayout(progressFrame.getContentPane(),BoxLayout.Y_AXIS));
							progressFrame.add(progressScroll);
							progressFrame.add(Box.createRigidArea(new Dimension(0,5)));
							progressFrame.add(barAndButton);
							//setup properties
							progressFrame.setSize(350,200);
							progressFrame.setTitle("Download");
							progressFrame.setIconImage(download.getImage());
							((JPanel)progressFrame.getContentPane()).setBorder(BorderFactory.createEmptyBorder(10,10,5,10));//top,left,down,right
							progressFrame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
							progressFrame.getContentPane().setBackground(bgColor);
							progressFrame.setResizable(false);
							progressFrame.setLocationRelativeTo(CSR);
							progressFrame.setVisible(true);

							//start processing...
							int error=0;	//record number of occurred errors in progress
							for(int i=0;i<indices.length;i++){
								if(Thread.currentThread().isInterrupted())return;

								//call efetch and save each result to text file and genesearch to get .info
								int seqlength=Integer.parseInt(currentSummary.get(1).get(indices[i]));
								String UID=currentSummary.get(6).get(indices[i]);
								//name
								String name=currentSummary.get(0).get(indices[i]);
								name=name.replace(File.separator,".");//avoid conflict with system's directory
								name=name.replace("\\",".");
								name=name.replace("/",",");
								String seqLength=currentSummary.get(1).get(indices[i]);
								String createDate=currentSummary.get(4).get(indices[i]);
								String updateDate=currentSummary.get(5).get(indices[i]);
								append(progressText,doc,name+"\n",keyword);

								//download GenBank data in xml and save it
								File genBankXML=new File(directory.getAbsolutePath()+File.separator+name+".xml");
								try{
									append(progressText,doc,"  Downloading GenBank data in XML...",null);
									GenBank.getXML(database,UID,genBankXML);
									GenBank newGen=new GenBank("."+genBankXML.getPath().substring(genBankXML.getPath().indexOf("CUO"+File.separator+"Library")+3,genBankXML.getPath().length()));
									newGen.parseXML(genBankXML);
									newGen.generateListModel();
									library.add(newGen);
								}catch(Exception e){
e.printStackTrace();
									//delete failed xml file
									if(genBankXML.exists())genBankXML.delete();

									//report
									append(progressText,doc,"ERROR\n",null);
									progressBar.setValue(progressBar.getValue()+seqlength);
									error++;
									continue;
								}
								append(progressText,doc,"OK\n",null);
								progressBar.setValue(progressBar.getValue()+seqlength);
							}

							//upon finishing the progress
							if(error>0){
								Toolkit.getDefaultToolkit().beep();
								progressFrame.setTitle("Error Downloading");
								progressFrame.setIconImage(note.getImage());
								progressBar.setString("Finished with "+error+" errors.");
								processingWheel.setIcon(tea);
								cancelButton.setText("OK");
								cancelButton.removeActionListener(cancelButton.getActionListeners()[0]);
								cancelButton.addActionListener(new ActionListener(){
									public void actionPerformed(ActionEvent ae){
										progressFrame.dispose();
									}
								});
								progressFrame.removeWindowListener(progressFrame.getWindowListeners()[0]);
							}else{
								Toolkit.getDefaultToolkit().beep();
								progressFrame.dispose();
							}
						}
						private void append(JTextPane progressText,StyledDocument doc,String text,SimpleAttributeSet keyword){
							try{
								doc.insertString(doc.getLength(),text,keyword);
								Rectangle r=new Rectangle(0,progressText.getHeight()-2,1,1);
								if(r!=null){
									progressText.scrollRectToVisible(r);
								}
							}catch(javax.swing.text.BadLocationException ble){//unlikely to happen
							}catch(NullPointerException npe){}//sometimes happens to auto scrolling
						}
					});
					retrieveThread.start();
				}		
			}
		});
	}

	private DefaultListModel showSummary(ArrayList<ArrayList<String>> summary){
		DefaultListModel theList=new DefaultListModel();
		for(int i=0;i<summary.get(0).size();i++){
			String presentation=new String();
			//title
			presentation=presentation+"<html><font color=rgb(43,60,170)><b><u>"+summary.get(0).get(i)+"</b></u></font><br>";
			if(!compactMode){
				//seqlength,refseq,GI
				presentation=presentation+"<font size=2>Length: "+summary.get(1).get(i)+"&nbsp;&nbsp;&nbsp;RefSeq: "+summary.get(2).get(i)+"&nbsp;&nbsp;GI: "+summary.get(3).get(i)+"<br>";
				//createDate,updateDate
				presentation=presentation+"Create Date: "+summary.get(4).get(i)+"  Update Date: "+summary.get(5).get(i)+"</font><hr></html>";
			}else{
				presentation+="<hr></html>";
			}
			theList.addElement(presentation);
		}
		return theList;
	}

	public static ArrayList<ArrayList<String>> eSearch(String database,String term,int retmax) throws IOException,ParserConfigurationException,SAXException{	//return list of UID string
		String query=term.replace(" ","+");
		ArrayList<String> numberOfResults=null;
		ArrayList<String> parsedData=null;

		if(Thread.currentThread().isInterrupted())return null;//check for state of interrupt before starting a long job

		//query
		String uri="http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db="+database+"&term="+query+"&retmax="+retmax;
		DocumentBuilder db=DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document dom=db.parse(uri);
		Element rootEle=dom.getDocumentElement();

		//get number of results
		numberOfResults=getXMLValue(rootEle,"Count");

		//get UID in XML and parse it
		parsedData=getXMLValue(rootEle,"Id");

		ArrayList<ArrayList<String>> yield=new ArrayList<ArrayList<String>>();
		yield.add(numberOfResults);
		yield.add(parsedData);
		return yield;
	}

	public static ArrayList<ArrayList<String>> eSummary(String database,ArrayList<String> UID) throws IOException,ParserConfigurationException,SAXException{	//return list of summary according to UID
		ArrayList<ArrayList<String>> allSummary=new ArrayList<ArrayList<String>>();	//store ArrayLists of title,refseq,gi,create,update
		ArrayList<String> title=new ArrayList<String>();
		ArrayList<String> seqlength=new ArrayList<String>();
		ArrayList<String> refseq=new ArrayList<String>();
		ArrayList<String> gi=new ArrayList<String>();
		ArrayList<String> create=new ArrayList<String>();
		ArrayList<String> update=new ArrayList<String>();

		//crack long UID list in smaller 500 UID pieces so that URI request is not too long
		ArrayList<String> crackedUID=new ArrayList<String>();
		for(int i=0;i<UID.size();i+=500){
			String partUID=null;
			if((UID.size()-i)>=500){
				for(int j=0;j<500;j++){
					if(j==0){
						partUID=UID.get(i+j);	//first one in 500
					}else{
						partUID=partUID+","+UID.get(i+j);
					}
				}
			}else{
				for(int j=0;j<(UID.size()%500);j++){
					if(j==0){
						partUID=UID.get(i+j);	//first one in remain
					}else{
						partUID=partUID+","+UID.get(i+j);
					}
				}
			}
			crackedUID.add(partUID);
		}

		for(int z=0;z<crackedUID.size();z++){
			if(Thread.currentThread().isInterrupted())return null;//check for state of interrupt before starting a long job
			String uri="http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db="+database+"&id="+crackedUID.get(z);
			DocumentBuilder db=DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document dom=db.parse(uri);
			Element rootEle=dom.getDocumentElement();
			NodeList docsum=rootEle.getElementsByTagName("DocSum");//extract list of docsum elements

			for(int k=0;k<docsum.getLength();k++){
				NodeList item=((Element)docsum.item(k)).getElementsByTagName("Item");	//the info are stored inside item element of different names
				for(int i=0;i<item.getLength();i++){
					if(((Element)item.item(i)).getAttribute("Name").equals("Title")){
						title.add(((Element)item.item(i)).getFirstChild().getNodeValue());
					}else if(((Element)item.item(i)).getAttribute("Name").equals("Length")){
						seqlength.add(((Element)item.item(i)).getFirstChild().getNodeValue());
					}else if(((Element)item.item(i)).getAttribute("Name").equals("Caption")){
						refseq.add(((Element)item.item(i)).getFirstChild().getNodeValue());
					}else if(((Element)item.item(i)).getAttribute("Name").equals("Gi")){
						gi.add(((Element)item.item(i)).getFirstChild().getNodeValue());
					}else if(((Element)item.item(i)).getAttribute("Name").equals("CreateDate")){
						create.add(((Element)item.item(i)).getFirstChild().getNodeValue());
					}else if(((Element)item.item(i)).getAttribute("Name").equals("UpdateDate")){
						update.add(((Element)item.item(i)).getFirstChild().getNodeValue());
					}
				}
			}
		}

		allSummary.add(title);
		allSummary.add(seqlength);
		allSummary.add(refseq);
		allSummary.add(gi);
		allSummary.add(create);
		allSummary.add(update);
		allSummary.add(UID);
		return allSummary;	
	}

	public static void eFetch(File file,String database,String UID)throws MalformedURLException,IOException{	//return one data string
		if(Thread.currentThread().isInterrupted())return;
		BufferedReader in=null;	
		BufferedWriter out=null;
		out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true),"UTF-8"));
		try{
			URL uri=new URL("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db="+database+"&id="+UID+"&rettype=fasta&retmode=text");
			in=new BufferedReader(new InputStreamReader(uri.openStream()));
			String buffer;
			while((buffer=in.readLine())!=null){
				if(Thread.currentThread().isInterrupted())return;
				out.write(buffer+System.getProperty("line.separator"));
			}
		}finally{
			try{
				out.close();
				in.close();
			}catch(IOException ioe){}//ignore
		}
	}

	public static void geneSearch(File file,String accn,ArrayList<String> header)throws IOException,ParserConfigurationException,SAXException{	//get the genes of the sequence and record in .info
		if(Thread.currentThread().isInterrupted())return;
		ArrayList<ArrayList<String>> searchResult=eSearch("gene",accn,100000);
		ArrayList<String> UID=searchResult.get(1);//use esearch to get uids
		String numberOfResults=searchResult.get(0).get(0);

		//crack long UID list in smaller 500 UID pieces so that URI request is not too long
		ArrayList<String> crackedUID=new ArrayList<String>();
		for(int i=0;i<UID.size();i+=500){
			String partUID=null;
			if((UID.size()-i)>=500){
				for(int j=0;j<500;j++){
					if(j==0){
						partUID=UID.get(i+j);	//first one in 500
					}else{
						partUID=partUID+","+UID.get(i+j);
					}
				}
			}else{
				for(int j=0;j<(UID.size()%500);j++){
					if(j==0){
						partUID=UID.get(i+j);	//first one in remain
					}else{
						partUID=partUID+","+UID.get(i+j);
					}
				}
			}
			crackedUID.add(partUID);
		}

		//retrieve summary of genes
		ArrayList<String> name=new ArrayList<String>();
		ArrayList<String> description=new ArrayList<String>();
		ArrayList<String> otherAliases=new ArrayList<String>();
		ArrayList<String> otherDesignations=new ArrayList<String>();
		ArrayList<String> numberOfSites=new ArrayList<String>();
		ArrayList<String> chrStart=new ArrayList<String>();
		ArrayList<String> chrStop=new ArrayList<String>();
		for(int z=0;z<crackedUID.size();z++){
			String uri="http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=gene&id="+crackedUID.get(z);
			DocumentBuilder db=DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document dom=db.parse(uri);
			Element rootEle=dom.getDocumentElement();
			NodeList docsum=rootEle.getElementsByTagName("DocSum");//extract list of docsum elements
			for(int k=0;k<docsum.getLength();k++){
				if(Thread.currentThread().isInterrupted())return;
				NodeList item=((Element)docsum.item(k)).getElementsByTagName("Item");	//the info are stored inside item element of different names
				int site=0;	//for recording number of sites
				int obs=0;	//for preventing chrstart to be recorded 1 more time
				for(int i=0;i<item.getLength();i++){
					if(((Element)item.item(i)).getAttribute("Name").equals("Name")){
						name.add(((Element)item.item(i)).getFirstChild().getNodeValue());
					}else if(((Element)item.item(i)).getAttribute("Name").equals("Description")){
						try{
							description.add(((Element)item.item(i)).getFirstChild().getNodeValue());
						}catch(NullPointerException npe){
							description.add("<NA>");//when there is no data in the item
						}
					}else if(((Element)item.item(i)).getAttribute("Name").equals("OtherAliases")){
						try{
							otherAliases.add(((Element)item.item(i)).getFirstChild().getNodeValue());
						}catch(NullPointerException npe){
							otherAliases.add("<NA>");//when there is no data in the item
						}
					}else if(((Element)item.item(i)).getAttribute("Name").equals("OtherDesignations")){
						try{
							otherDesignations.add(((Element)item.item(i)).getFirstChild().getNodeValue());
						}catch(NullPointerException npe){
							otherDesignations.add("<NA>");
						}
					}else if(((Element)item.item(i)).getAttribute("Name").equals("GenomicInfoType")){
						site++;
					}else if(((Element)item.item(i)).getAttribute("Name").equals("ChrStart")&&obs<site){	//avoid taking consideration of double chrstart from data at the end
						String dataString=((Element)item.item(i)).getFirstChild().getNodeValue();
						dataString=Integer.toString(Integer.parseInt(dataString)+1);//+1 for computer recognition pattern(start from 0) bias correction
						chrStart.add(dataString);
						obs++;
					}else if(((Element)item.item(i)).getAttribute("Name").equals("ChrStop")){
						String dataString=((Element)item.item(i)).getFirstChild().getNodeValue();
						dataString=Integer.toString(Integer.parseInt(dataString)+1);
						chrStop.add(dataString);
					}
				}
				numberOfSites.add(Integer.toString(site));
			}
		}

		//generate .info file
		BufferedWriter out=null;
		out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true),"UTF-8"));
		try{
			for(int k=0;k<header.size();k++){
				out.write(header.get(k)+System.getProperty("line.separator"));
			}
			out.write(numberOfResults+System.getProperty("line.separator"));
			int accumulation=0;	//record shift of data in chrStart and chrStop
			for(int i=0;i<name.size();i++){
				out.write(name.get(i)+System.getProperty("line.separator"));
				out.write(description.get(i)+System.getProperty("line.separator"));
				out.write(otherAliases.get(i)+System.getProperty("line.separator"));
				out.write(otherDesignations.get(i)+System.getProperty("line.separator"));
				out.write(numberOfSites.get(i)+System.getProperty("line.separator"));
				for(int j=0;j<Integer.parseInt(numberOfSites.get(i));j++){
					out.write(chrStart.get(accumulation+i+j)+System.getProperty("line.separator"));
					out.write(chrStop.get(accumulation+i+j)+System.getProperty("line.separator"));
				}
				accumulation+=Integer.parseInt(numberOfSites.get(i))-1;
			}
			out.write("<END>");
		}finally{
			try{
				out.close();
			}catch(IOException ioe){}//ignore
		}
	}

	public static ArrayList<String> getXMLValue(Element ele,String tag){
		ArrayList<String> XMLValue=new ArrayList<String>();
		NodeList nl=ele.getElementsByTagName(tag);
		if(nl!=null&&nl.getLength()>0){
			for(int i=0;i<nl.getLength();i++){
				Element el=(Element)nl.item(i);
				if(el.getFirstChild()!=null)
					XMLValue.add(el.getFirstChild().getNodeValue());
			}
		}
		return XMLValue;
	}

	private void layoutComponents(){
		//searchPanel
		searchPanel.setLayout(new BoxLayout(searchPanel,BoxLayout.X_AXIS));
		searchPanel.add(Box.createRigidArea(new Dimension(10,0)));
		searchPanel.add(getField);
		searchPanel.add(Box.createRigidArea(new Dimension(5,0)));
		searchPanel.add(getCombo);
		searchPanel.add(Box.createRigidArea(new Dimension(5,0)));
		searchPanel.add(getSuffix);
		searchPanel.add(Box.createRigidArea(new Dimension(10,0)));
		searchPanel.setBackground(bgColor);	//get rid of rigidarea color problem

		//statusPanel
		statusPanel.setLayout(new BoxLayout(statusPanel,BoxLayout.X_AXIS));
		statusPanel.add(Box.createRigidArea(new Dimension(20,0)));
		statusPanel.add(statusIcon);
		statusPanel.add(Box.createRigidArea(new Dimension(10,0)));
		statusPanel.add(statusLabel);
		statusPanel.add(Box.createHorizontalGlue());
		statusPanel.add(filterButton);
		statusPanel.add(previousButton);
		statusPanel.add(nextButton);
		statusPanel.add(Box.createRigidArea(new Dimension(10,0)));
		statusPanel.add(resultSpinner);
		statusPanel.add(Box.createRigidArea(new Dimension(5,0)));
		statusPanel.add(resultAll);
		statusPanel.add(Box.createRigidArea(new Dimension(20,0)));
		statusPanel.setBackground(bgColor);

		//foundPanel
		foundPanel.setLayout(new BoxLayout(foundPanel,BoxLayout.X_AXIS));
		foundPanel.add(Box.createRigidArea(new Dimension(15,0)));
		foundPanel.add(foundScroll);
		foundPanel.add(Box.createRigidArea(new Dimension(15,0)));
		foundPanel.setBackground(bgColor);

		//buttonPanel
		buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.X_AXIS));
		buttonPanel.add(Box.createRigidArea(new Dimension(10,0)));
		buttonPanel.add(selectAllButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(10,0)));
		buttonPanel.add(compact);
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(retrieveButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(10,0)));
		buttonPanel.setBackground(bgColor);

		//this
		JPanel mainPanel=new JPanel();	//for the sake of insets
		mainPanel.setBackground(bgColor);
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
		mainPanel.setBorder(new EmptyBorder(10,0,10,0));	//top,left,bottom,right
		mainPanel.add(searchPanel);
		mainPanel.add(Box.createRigidArea(new Dimension(0,10)));
		JSeparator js1=new JSeparator();
		js1.setForeground(Color.WHITE);
		js1.setMaximumSize(new Dimension(10000,2));
		js1.setMinimumSize(new Dimension(0,2));
		mainPanel.add(js1);
		mainPanel.add(Box.createRigidArea(new Dimension(0,10)));
		mainPanel.add(statusPanel);
		mainPanel.add(Box.createRigidArea(new Dimension(0,10)));
		JSeparator js2=new JSeparator();
		js2.setForeground(Color.WHITE);
		js2.setMaximumSize(new Dimension(10000,2));
		js2.setMinimumSize(new Dimension(0,2));
		mainPanel.add(js2);
		mainPanel.add(Box.createRigidArea(new Dimension(0,10)));
		mainPanel.add(foundPanel);
		mainPanel.add(Box.createRigidArea(new Dimension(0,10)));
		mainPanel.add(buttonPanel);
		CSR.add(mainPanel);
	}

	private void setupProperties(){
		if(independent){
			((JFrame)CSR).setTitle("Coding Sequence Retriever");
			((JFrame)CSR).setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			((JFrame)CSR).setIconImage(wheel.getImage());
		}else{
			((JDialog)CSR).setTitle("Coding Sequence Retriever");
			((JDialog)CSR).setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		}
		CSR.setSize(420,540);
		CSR.setMinimumSize(new Dimension(420,540));
		CSR.setLocationRelativeTo(null);	//center
		CSR.setVisible(true);
	}

	public static void main(String[] args){
		new CSR();//it is independent
	}
}
