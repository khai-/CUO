package Tools.SequenceCreator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import Support.Canvas;
import Support.Common;
import Support.SelectGeneDialog;
import Support.parser.Faa;
import Support.parser.Fna;
import Support.parser.GenBank;
import Support.parser.Mfa;
import Support.parser.Sequence;

public class SequenceCreator extends JDialog{
	final JDialog seqCreator=this;
	Color bgColor=new Color(70,70,70);
	Color themeColor=new Color(76,117,35);
	Font smallFont=new Font(null,Font.PLAIN,9);

	ArrayList<GenBank> library;
	ArrayList<GenBank> toolbox;
	ArrayList<GenBank> favourite;

	Canvas canvas;

	//file and format
	JComboBox formatBox;

	//name and properties
	JTextField nameField;
	JPanel detailsPanel;
	JLabel lengthText;
	JLabel giText;
	JLabel gbText;
	JPanel typePanel;
	JLabel typeLabel;
	String[] FastaNATypes={"Gene","Restriction"};
	DefaultComboBoxModel NAModel=new DefaultComboBoxModel(FastaNATypes);
	String[] FastaAATypes={"Gene"};	//AA is less useful than NA
	DefaultComboBoxModel AAModel=new DefaultComboBoxModel(FastaAATypes);
	JComboBox typeBox;
	JPanel radioPanel;
	JLabel circularLabel;
	JRadioButton circularYes;
	JRadioButton circularNo;
	ButtonGroup circularGroup;
	JLabel doubleStrandedLabel;
	JRadioButton doubleStrandedYes;
	JRadioButton doubleStrandedNo;
	ButtonGroup doubleStrandedGroup;

	//genes
	JPanel genesPanel;
	JButton newButton;
	JButton loadButton;
	JButton removeButton;
	JTable genesTable;
	DefaultTableModel genesTableModel;
	ArrayList<GenBank> geneList=new ArrayList<GenBank>();	//GenBank list of genes in the table
	GenBank currentGenBank; //to store currently selected GenBank, must be synchronized with sequencePane
	int previousSelection=-1;	//int to store previous selection in the list
	JScrollPane genesScroll;
	JSeparator js3;

	//sitePanel
	JPanel sitePanel;
	JLabel siteLabel;
	JTextField siteField;
	JLabel siteHelpLabel;

	//sequence
	JPanel sequencePanel;
	JButton viewButton;
	JButton scanButton;
	JTextField gotoField;
	JTextPane sequencePane;
	JScrollPane sequenceScroll;

	//buttons
	JLabel saveStatus;
	JButton saveButton;
	JButton placeButton;

	public SequenceCreator(JFrame ownerFrame,ArrayList<GenBank> library,ArrayList<GenBank> toolbox,ArrayList<GenBank> favourite){
		super(ownerFrame,false);
		this.library=library;
		this.toolbox=toolbox;
		this.favourite=favourite;
		initiate();
	}

	public SequenceCreator(Canvas canvas){
		super((JFrame)canvas.getTopLevelAncestor(),true);
		this.canvas=canvas;
		this.library=canvas.OP.ST.library;
		this.toolbox=canvas.OP.ST.toolbox;
		this.favourite=canvas.OP.ST.favourite;

		initiate();
	}

	private void initiate(){
		createComponents();
		assignFunctions();
		layoutComponents();
		setupProperties();
	}

	private void createComponents(){
		//file and format
		String[] theFormats={"FastaNA","FastaAA","MultiFasta"};	//add "GenBank" in the future
		formatBox=new JComboBox(theFormats);
		formatBox.setMaximumSize(new Dimension(60,25));

		//name
		nameField=new JTextField("NoName");
		nameField.setMaximumSize(new Dimension(999999,25));

		//details
		detailsPanel=new JPanel();
		detailsPanel.setOpaque(false);
		detailsPanel.setLayout(new BoxLayout(detailsPanel,BoxLayout.X_AXIS));
		lengthText=new JLabel("-");
		lengthText.setForeground(Color.WHITE);
		giText=new JLabel("-");
		giText.setForeground(Color.WHITE);
		gbText=new JLabel("-");
		gbText.setForeground(Color.WHITE);
		typePanel=new JPanel();
		typePanel.setOpaque(false);
		typePanel.setLayout(new BoxLayout(typePanel,BoxLayout.X_AXIS));
		typeLabel=new JLabel("Type:");
		typeLabel.setForeground(Color.WHITE);
		typeBox=new JComboBox(FastaNATypes);
		typeBox.setMaximumSize(new Dimension(100,25));

		//radioPanel
		radioPanel=new JPanel();
		radioPanel.setOpaque(false);
		radioPanel.setLayout(new BoxLayout(radioPanel,BoxLayout.X_AXIS));
		radioPanel.setVisible(false);
		circularLabel=new JLabel("Circular:");
		circularLabel.setForeground(Color.WHITE);
		circularYes=new JRadioButton("Yes");
		circularYes.setBackground(bgColor);
		circularYes.setForeground(Color.WHITE);
		circularYes.setSelected(true);
		circularNo=new JRadioButton("No");
		circularNo.setBackground(bgColor);
		circularNo.setForeground(Color.WHITE);
		circularGroup=new ButtonGroup();
		circularGroup.add(circularYes);
		circularGroup.add(circularNo);
		doubleStrandedLabel=new JLabel("Double Stranded:");
		doubleStrandedLabel.setForeground(Color.WHITE);
		doubleStrandedYes=new JRadioButton("Yes");
		doubleStrandedYes.setBackground(bgColor);
		doubleStrandedYes.setForeground(Color.WHITE);
		doubleStrandedYes.setSelected(true);
		doubleStrandedNo=new JRadioButton("No");
		doubleStrandedNo.setBackground(bgColor);
		doubleStrandedNo.setForeground(Color.WHITE);
		doubleStrandedGroup=new ButtonGroup();
		doubleStrandedGroup.add(doubleStrandedYes);
		doubleStrandedGroup.add(doubleStrandedNo);

		//genes
		genesPanel=new JPanel();
		genesPanel.setOpaque(false);
		genesPanel.setLayout(new BoxLayout(genesPanel,BoxLayout.X_AXIS));
		genesPanel.setVisible(false);
		Dimension geneDimension=new Dimension(50,20);
		newButton=new JButton("<html>New</html>");
		newButton.setFont(smallFont);
		newButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		newButton.setPreferredSize(geneDimension);
		newButton.setMaximumSize(geneDimension);
		loadButton=new JButton("<html>Load</html>");
		loadButton.setFont(smallFont);
		loadButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		loadButton.setPreferredSize(geneDimension);
		loadButton.setMaximumSize(geneDimension);
		removeButton=new JButton("<html>Remove</html>");
		removeButton.setFont(smallFont);
		removeButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		removeButton.setPreferredSize(geneDimension);
		removeButton.setMaximumSize(geneDimension);
		genesTable=new JTable();
		class DraggableTableModel extends DefaultTableModel implements Reorderable{
			DraggableTableModel(Object[][] data,Object[] columnNames){
				super(data,columnNames);
			}
			public boolean isCellEditable(int rowIndex,int columnIndex){
				if(columnIndex==2)return false;	//length is uneditable
				else return true;
			}
			public void reorder(int fromIndex,int toIndex){	//drag and drop row in jtable
				if(fromIndex<toIndex)toIndex=toIndex-1;	//some adjustment
				//move the row
				moveRow(fromIndex,fromIndex,toIndex);
				//move geneList synchronously
				GenBank theE=geneList.remove(fromIndex);
				geneList.add(toIndex,theE);
				genesTable.setRowSelectionInterval(toIndex,toIndex);
			}
		}
		genesTableModel=new DraggableTableModel(null,new String[]{"Name","Type","Length"});
		genesTable.setModel(genesTableModel);
		genesTable.getTableHeader().setReorderingAllowed(false);
		genesTable.setRowHeight(20);
		genesTable.getColumnModel().getColumn(0).setPreferredWidth(200);
		genesTable.getColumnModel().getColumn(1).setPreferredWidth(80);
		genesTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JComboBox(new String[]{"Protein","Intron","tRNA","rRNA","mRNA","Mobile Element","Special site","Restriction site","Uncategorized"})));
		genesTable.getColumnModel().getColumn(2).setPreferredWidth(50);
		genesTable.setDragEnabled(true);
		genesTable.setDropMode(DropMode.INSERT_ROWS);
		genesTable.setTransferHandler(new TableRowTransferHandler(genesTable));
		genesScroll=new JScrollPane(genesTable,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		genesScroll.setPreferredSize(new Dimension(150,170));

		js3=new JSeparator();
		js3.setForeground(Color.WHITE);
		js3.setMaximumSize(new Dimension(10000,2));
		js3.setMinimumSize(new Dimension(0,2));
		js3.setVisible(false);

		//sitePanel
		sitePanel=new JPanel();
		sitePanel.setOpaque(false);
		sitePanel.setLayout(new BoxLayout(sitePanel,BoxLayout.Y_AXIS));
		sitePanel.setVisible(false);
		siteLabel=new JLabel("Sequence:");
		siteLabel.setForeground(Color.WHITE);
		siteField=new JTextField();
		siteField.setMaximumSize(new Dimension(999999,25));
		siteHelpLabel=new JLabel("<html><font color=WHITE>> for coding strand cut; &lt for template strand cut; >&lt for blunt cut.</font></html>");

		//sequence
		sequencePanel=new JPanel();
		sequencePanel.setOpaque(false);
		sequencePanel.setLayout(new BoxLayout(sequencePanel,BoxLayout.X_AXIS));
		viewButton=new JButton("View");
		viewButton.setFont(smallFont);
		scanButton=new JButton("Scan");
		scanButton.setFont(smallFont);
		gotoField=new JTextField();
		gotoField.setMaximumSize(new Dimension(10000,20));
		sequencePane=new JTextPane();
		sequenceScroll=new JScrollPane(sequencePane,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sequenceScroll.setPreferredSize(new Dimension(10000,340));

		//buttons
		saveStatus=new JLabel();
		saveStatus.setForeground(Color.WHITE);
		saveButton=new JButton("Save");
		if(canvas!=null)saveButton.setVisible(false);
		placeButton=new JButton("Place");
		if(canvas==null)placeButton.setVisible(false);
	}

	private void assignFunctions(){
		//formatBox
		formatBox.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent ie){
				String currentFormat=(String)formatBox.getSelectedItem();
				if(currentFormat.equals("FastaNA")){
					detailsPanel.setVisible(true);
					radioPanel.setVisible(false);
					typePanel.setVisible(true);
					genesPanel.setVisible(false);
					js3.setVisible(false);
					typeBox.setModel(NAModel);
					typeBox.setSelectedItem("Gene");
					sequencePanel.setVisible(true);
					sequenceScroll.setVisible(true);
					sitePanel.setVisible(false);
				}else if(currentFormat.equals("FastaAA")){
					detailsPanel.setVisible(true);
					radioPanel.setVisible(false);
					typePanel.setVisible(true);
					genesPanel.setVisible(false);
					js3.setVisible(false);
					typeBox.setModel(AAModel);
					typeBox.setSelectedItem("Gene");
					sequencePanel.setVisible(true);
					sequenceScroll.setVisible(true);
					sitePanel.setVisible(false);
				}else if(currentFormat.equals("GenBank")){
					detailsPanel.setVisible(true);
					radioPanel.setVisible(true);
					typePanel.setVisible(false);
					genesPanel.setVisible(true);
					js3.setVisible(true);
					sitePanel.setVisible(false);
					sequencePanel.setVisible(true);
					sequenceScroll.setVisible(true);
				}else if(currentFormat.equals("MultiFasta")){
					detailsPanel.setVisible(true);
					radioPanel.setVisible(false);
					typePanel.setVisible(false);
					genesPanel.setVisible(true);
					js3.setVisible(true);
					sitePanel.setVisible(false);
					sequencePanel.setVisible(true);
					sequenceScroll.setVisible(false);
				}
			}
		});

		//typeBox
		typeBox.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent ie){
				if(!typeBox.isVisible())return;
				String currentType=(String)typeBox.getSelectedItem();
				if(currentType.equals("Gene")){
					sequencePanel.setVisible(true);
					sequenceScroll.setVisible(true);
					sitePanel.setVisible(false);
				}else if(currentType.equals("Restriction")){
					sequencePanel.setVisible(false);
					sequenceScroll.setVisible(false);
					sitePanel.setVisible(true);
				}
			}
		});

		//newButton
		newButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				//add to geneList
				Sequence seq=new Sequence();
				seq.gene="NoName";
				seq.type="CDS";
				seq.codingSequence="";
				GenBank newgen=new GenBank(seq);
				geneList.add(newgen);

				//add to genesTable
				Vector<Object> newRow=new Vector<Object>();
				newRow.add(seq.gene);	//name
				newRow.add("Protein");	//type combo box, CDS is default type
				newRow.add(0);	//length
				genesTableModel.addRow(newRow);
			}
		});

		//loadButton
		loadButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				final SelectGeneDialog sgd=new SelectGeneDialog(seqCreator,library,toolbox,favourite,"Load");
				sgd.viewer.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				sgd.OKButton.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent ae){
						//assure something is chosen
						Object[] theObjects=sgd.viewer.getSelectedValues();
						JPanel[] thePanels=new JPanel[theObjects.length];
						for(int i=0;i<thePanels.length;i++){
							thePanels[i]=(JPanel)theObjects[i];
						}
						if(thePanels.length==0){
							JOptionPane.showMessageDialog(sgd.SGD,"Please choose a gene.","Error",JOptionPane.PLAIN_MESSAGE);
							return;
						}


						GenBank[] selectedGenes=new GenBank[thePanels.length];
						for(int i=0;i<thePanels.length;i++){
							//identify the selected thing and copy selected gene/genome
							JPanel thePanel=thePanels[i];
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

								String fragmentName=theText.split("<br>")[0];

								//get the genBank from library,toolbox and favourite list
								passedGen=Common.getGenBank(name,library,toolbox,favourite);

								newgen=passedGen.copy(sites);//copy only the gene
							}else if(type.equals("fasta")){
								passedGen=Common.getGenBank(name,library,toolbox,favourite);
								newgen=passedGen.copy();
							}else{
								JOptionPane.showMessageDialog(sgd.SGD,"Please choose a gene.","Error",JOptionPane.PLAIN_MESSAGE);
								return;
							}

							//add to selected gene list
							if(newgen!=null)selectedGenes[i]=newgen;
							else continue;

							//add selected gene to the table
							Sequence seq=newgen.sequences.get(0);
							Vector<Object> newRow=new Vector<Object>();
							newRow.add(seq.gene);	//name
							if(seq.type.equals("CDS"))newRow.add("Protein");
							else newRow.add(seq.type);	//type
							newRow.add(selectedGenes[i].completeSequence.length());	//length
							genesTableModel.addRow(newRow);

							//add selected genes to the list
							geneList.add(selectedGenes[i]);
						}

						sgd.SGD.dispose();
					}
				});
			}
		});

		//removeButton
		removeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				int[] indices=genesTable.getSelectedRows();
				Arrays.sort(indices);	//ascending order
				Common.reverse(indices);//reverse it

				for(int i=0;i<indices.length;i++){
					if(indices[i]==previousSelection){
						//clear the show selection mode
						currentGenBank=null;
						previousSelection=-1;
						sequencePane.setText("");
					}
					geneList.remove(indices[i]);
					genesTableModel.removeRow(indices[i]);
				}
			}
		});

		//genesTable
		genesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent lse){
				int rowNum=genesTable.getSelectedRow();
				if(rowNum==previousSelection)return;	//avoid repetition of selection
				if(rowNum>=0){
					GenBank genBank=geneList.get(rowNum);
					if(currentGenBank==null||saveCurrentSequence()){	//save current sequence
						currentGenBank=genBank;
						previousSelection=rowNum;
						sequencePane.setText(currentGenBank.completeSequence);	//display the sequence of current selection
					}else{	//save sequence fail
						if(previousSelection!=-1)genesTable.setRowSelectionInterval(previousSelection,previousSelection);
						return;
					}
				}
			}
		});

		//viewButton
		viewButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				if(sequenceScroll.isVisible()){
					if(currentGenBank!=null&&!saveCurrentSequence())return;
					sequencePane.setText("");

					sequenceScroll.setVisible(false);
					seqCreator.validate();
				}else{
					//display the sequence of current selection
					int rowNum=genesTable.getSelectedRow();
					if(rowNum>=0){
						GenBank genBank=geneList.get(rowNum);
						currentGenBank=genBank;
						previousSelection=rowNum;
						sequencePane.setText(genBank.completeSequence);
					}

					sequenceScroll.setVisible(true);
					seqCreator.validate();
				}
			}
		});

		//scanButton

		//saveButton
		saveButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				//selected format
				String selectedFormat=(String)(formatBox.getSelectedItem());
				String selectedType=(String)typeBox.getSelectedItem();
				//decide workingDirectory
				final String fileName;
				String startDirectory=".";
				if(selectedFormat.equals("GenBank")||(selectedFormat.equals("FastaNA")&&selectedType.equals("Gene"))||(selectedFormat.equals("FastaAA")&&selectedType.equals("Gene"))||selectedFormat.equals("MultiFasta")){
					startDirectory="."+File.separator+"Library";
				}else if(selectedFormat.equals("FastaNA")&&selectedType.equals("Restriction")){
					startDirectory="."+File.separator+"Toolbox"+File.separator+"Restriction";
				}
				JFileChooser jfc=new JFileChooser(startDirectory);
				jfc.setSelectedFile(new File(nameField.getText()));
				
				Common.disableTextComponent(jfc);	//disable jfc text field

				if(jfc.showSaveDialog(seqCreator)==JFileChooser.APPROVE_OPTION){
					String str=(jfc.getCurrentDirectory().getPath()+File.separator+nameField.getText());
					String rootFolder=new File(".").getAbsolutePath().substring(0,new File(".").getAbsolutePath().length()-2);	//rootFolder directory of CUO
					if(str.startsWith(rootFolder)){
						fileName="."+str.replace(rootFolder,"");
					}else{
						fileName=str;
					}
				}else return;

				GenBank newGen=null;//for the library
				//GenBank
				if(selectedFormat.equals("GenBank")){
					return;
				}
				//MultiFasta
				else if(selectedFormat.equals("MultiFasta")){
					//save current selected fragment's edit
					if(currentGenBank!=null&&!saveCurrentSequence())return;

					Mfa mfa=new Mfa();
					mfa.fileName=fileName+".mfa";
					for(int i=0;i<geneList.size();i++){
						Fna fna=new Fna();
						fna.name=(String)genesTableModel.getValueAt(i,0);
						fna.type=(String)genesTableModel.getValueAt(i,1);
						fna.sequence=geneList.get(i).completeSequence;
						mfa.fnaList.add(fna);
					}
					newGen=new GenBank(mfa);
					try{
						mfa.save();
						DateFormat df=new SimpleDateFormat("HH:mm");
						Date time=new Date();
						saveStatus.setText("Saved at "+df.format(time)+".");
					}catch(Exception e){
						JOptionPane.showMessageDialog(seqCreator,"Save failed.","Fail",JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				//FastaAA
				else if(selectedFormat.equals("FastaAA")){
					Faa faa=new Faa();
					faa.fileName=fileName+".faa";
					//type
					String theType=(String)typeBox.getSelectedItem();
					faa.type=theType;
					faa.AA=sequencePane.getText().replaceAll(" ","").replaceAll("\n","").toUpperCase();
					faa.name=nameField.getText().trim();
					newGen=new GenBank(faa);
					try{
						faa.save();
						DateFormat df=new SimpleDateFormat("HH:mm");
						Date time=new Date();
						saveStatus.setText("Saved at "+df.format(time)+".");
					}catch(Exception e){
						JOptionPane.showMessageDialog(seqCreator,"Save failed.","Fail",JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				//FastaNA
				else if(selectedFormat.equals("FastaNA")){
					Fna fna=new Fna();
					fna.fileName=fileName+".fna";

					//type specific adjustment
					String theType=(String)typeBox.getSelectedItem();
					fna.type=theType;
					if(theType.equals("Gene")){
						String theSequence=sequencePane.getText().replaceAll(" ","").replaceAll(System.getProperty("line.separator"),"").toUpperCase();
						//check for valid input
						for(int i=0;i<theSequence.length();i++){
							char theChar=theSequence.charAt(i);
							if(theChar!='A'&&theChar!='T'&&theChar!='G'&&theChar!='C'&&theChar!='N'){
								JOptionPane.showMessageDialog(seqCreator,"Please use only 'A''T''G''C''N' only for gene.","Letter error",JOptionPane.PLAIN_MESSAGE);
								return;
							}
						}

						fna.sequence=theSequence;
					}else if(theType.equals("Restriction")){
						String temp=siteField.getText().replaceAll(" ","").replaceAll("\n","").toUpperCase();
						//check for valid input
						int counterA=0;
						int counterB=0;
						for(int i=0;i<temp.length();i++){
							char theChar=temp.charAt(i);
							if(theChar=='>')counterA++;
							if(theChar=='<')counterB++;
							if(theChar!='A'&&theChar!='T'&&theChar!='G'&&theChar!='C'&&theChar!='<'&&theChar!='>'&&theChar!='N'&&theChar!='R'&&theChar!='Y'&&theChar!='K'&&theChar!='M'&&theChar!='S'&&theChar!='W'&&theChar!='B'&&theChar!='D'&&theChar!='H'&&theChar!='V'){
								JOptionPane.showMessageDialog(seqCreator,"Please use only 'A''T''G''C''>''<''N''R''Y''K''M''S''W''B''D''H''V' only for site.","Letter error",JOptionPane.PLAIN_MESSAGE);
								return;
							}
						}
						if(counterA>1||counterB>1){
							JOptionPane.showMessageDialog(seqCreator,"Please use only one instance of '>' or '<'.","Too many",JOptionPane.PLAIN_MESSAGE);
							return;
						}

						//decode input
						int topPos=-1;
						int botPos=-1;
						for(int i=0;i<temp.length();i++){
							if(temp.charAt(i)=='>'){	//topCut
								topPos=i;
							}else if(temp.charAt(i)=='<'){	//bottomCut
								botPos=i-1;
							}
						}
						if(topPos>botPos){
							topPos=topPos-1+1;	//+1 to align with common sense
							botPos+=1;
						}else{	//botPos>topPos
							topPos+=1;
							botPos=botPos-1+1;
						}
						fna.topCut=topPos;
						fna.bottomCut=botPos;

						temp=temp.replaceFirst(">","").replaceFirst("<","");
						fna.sequence=temp;
					}

					fna.name=nameField.getText().trim();
					newGen=new GenBank(fna);
					try{
						fna.save();
						DateFormat df=new SimpleDateFormat("HH:mm");
						Date time=new Date();
						saveStatus.setText("Saved at "+df.format(time)+".");
					}catch(Exception e){
						JOptionPane.showMessageDialog(seqCreator,"Save failed.","Fail",JOptionPane.ERROR_MESSAGE);
						return;
					}
				}

				//update library or toolbox or favourtie
				if(newGen!=null){
					//remove the overwritten genBank if there is any
					GenBank overwritten=Common.getGenBank(newGen.dirName,library,toolbox,favourite);
					if(overwritten!=null){
						if(!toolbox.remove(overwritten)){
							if(!library.remove(overwritten)){
								favourite.remove(overwritten);
							}
						}		
					}

					//add to list
					newGen.generateListModel();
					if(fileName.contains("."+File.separator+"Toolbox"+File.separator)){
						toolbox.add(newGen);
					}else if(fileName.contains("."+File.separator+"Library"+File.separator)){
						library.add(newGen);
					}else if(fileName.contains("."+File.separator+"Favourite"+File.separator)){
						favourite.add(newGen);
					}else{
						//ignore the outside files
					}
				}
			}
		});

		//placeButton
		placeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				//validate
				//avoid having same name on one canvas
				for(int i=0;i<canvas.circuitList.size();i++){
					if(canvas.circuitList.get(i).name.equals(nameField.getText().trim())){
						JOptionPane.showMessageDialog(seqCreator,"There is already a circuit called \""+nameField.getText().trim()+"\" on the canvas.\nPlease use other name.","Repetition",JOptionPane.ERROR_MESSAGE);
						nameField.requestFocus();
						return;
					}
				}

				//create the sequence
				//selected format
				String selectedFormat=(String)(formatBox.getSelectedItem());
				String selectedType=(String)typeBox.getSelectedItem();
				GenBank newGen=null;//for the library
				//GenBank
				if(selectedFormat.equals("GenBank")){
					return;
				}
				//MultiFasta
				else if(selectedFormat.equals("MultiFasta")){
					//save current selected fragment's edit
					if(currentGenBank!=null&&!saveCurrentSequence())return;

					Mfa mfa=new Mfa();
					mfa.name=nameField.getText().trim();
					for(int i=0;i<geneList.size();i++){
						Fna fna=new Fna();
						fna.name=(String)genesTableModel.getValueAt(i,0);
						fna.type=(String)genesTableModel.getValueAt(i,1);
						fna.sequence=geneList.get(i).completeSequence;
						mfa.fnaList.add(fna);
					}
					newGen=new GenBank(mfa);
				}
				//FastaAA
				else if(selectedFormat.equals("FastaAA")){
					return;
					//create random sequence from the AA?
				}
				//FastaNA
				else if(selectedFormat.equals("FastaNA")){
					Fna fna=new Fna();

					//type specific adjustment
					String theType=(String)typeBox.getSelectedItem();
					fna.type=theType;
					if(theType.equals("Gene")){
						String theSequence=sequencePane.getText().replaceAll(" ","").replaceAll(System.getProperty("line.separator"),"").toUpperCase();
						//check for valid input
						for(int i=0;i<theSequence.length();i++){
							char theChar=theSequence.charAt(i);
							if(theChar!='A'&&theChar!='T'&&theChar!='G'&&theChar!='C'&&theChar!='N'){
								JOptionPane.showMessageDialog(seqCreator,"Please use only 'A''T''G''C''N' only for gene.","Letter error",JOptionPane.PLAIN_MESSAGE);
								return;
							}
						}

						fna.sequence=theSequence;
					}else if(theType.equals("Restriction")){
						String temp=siteField.getText().replaceAll(" ","").replaceAll("\n","").toUpperCase();
						//check for valid input
						int counterA=0;
						int counterB=0;
						for(int i=0;i<temp.length();i++){
							char theChar=temp.charAt(i);
							if(theChar=='>')counterA++;
							if(theChar=='<')counterB++;
							if(theChar!='A'&&theChar!='T'&&theChar!='G'&&theChar!='C'&&theChar!='<'&&theChar!='>'&&theChar!='N'&&theChar!='R'&&theChar!='Y'&&theChar!='K'&&theChar!='M'&&theChar!='S'&&theChar!='W'&&theChar!='B'&&theChar!='D'&&theChar!='H'&&theChar!='V'){
								JOptionPane.showMessageDialog(seqCreator,"Please use only 'A''T''G''C''>''<''N''R''Y''K''M''S''W''B''D''H''V' only for site.","Letter error",JOptionPane.PLAIN_MESSAGE);
								return;
							}
						}
						if(counterA>1||counterB>1){
							JOptionPane.showMessageDialog(seqCreator,"Please use only one instance of '>' or '<'.","Too many",JOptionPane.PLAIN_MESSAGE);
							return;
						}

						//decode input
						int topPos=-1;
						int botPos=-1;
						for(int i=0;i<temp.length();i++){
							if(temp.charAt(i)=='>'){	//topCut
								topPos=i;
							}else if(temp.charAt(i)=='<'){	//bottomCut
								botPos=i-1;
							}
						}
						if(topPos>botPos){
							topPos=topPos-1+1;	//+1 to align with common sense
							botPos+=1;
						}else{	//botPos>topPos
							topPos+=1;
							botPos=botPos-1+1;
						}
						fna.topCut=topPos;
						fna.bottomCut=botPos;

						temp=temp.replaceFirst(">","").replaceFirst("<","");
						fna.sequence=temp;
					}

					fna.name=nameField.getText().trim();
					newGen=new GenBank(fna);
				}

				//place the sequence onto the canvas
				canvas.addCircuit(newGen,canvas.oldMouseX,canvas.oldMouseY);

				dispose();	//dispose this sequence creator
			}
		});
	}

	private void layoutComponents(){
		JPanel mainPanel=new JPanel();
		mainPanel.setBackground(bgColor);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));

		//name,format and details
		JPanel namePanel=new JPanel();
		namePanel.setOpaque(false);
		namePanel.setLayout(new BoxLayout(namePanel,BoxLayout.X_AXIS));
		JLabel nameLabel=new JLabel("Name:");
		nameLabel.setForeground(Color.WHITE);
		namePanel.add(nameLabel);
		namePanel.add(nameField);
		namePanel.add(Box.createRigidArea(new Dimension(5,0)));
		JLabel formatLabel=new JLabel("Format:");
		formatLabel.setForeground(Color.WHITE);
		namePanel.add(formatLabel);
		namePanel.add(formatBox);
		mainPanel.add(namePanel);
		mainPanel.add(Box.createRigidArea(new Dimension(0,5)));

		JLabel lengthLabel=new JLabel("Length:");
		lengthLabel.setForeground(Color.WHITE);
		detailsPanel.add(lengthLabel);
		detailsPanel.add(lengthText);
		detailsPanel.add(Box.createHorizontalGlue());
		JLabel giLabel=new JLabel("gi:");
		giLabel.setForeground(Color.WHITE);
		detailsPanel.add(giLabel);
		detailsPanel.add(giText);
		detailsPanel.add(Box.createHorizontalGlue());
		JLabel gbLabel=new JLabel("gb:");
		gbLabel.setForeground(Color.WHITE);
		detailsPanel.add(gbLabel);
		detailsPanel.add(gbText);
		detailsPanel.add(Box.createHorizontalGlue());
		typePanel.add(typeLabel);
		typePanel.add(typeBox);
		detailsPanel.add(typePanel);
		mainPanel.add(detailsPanel);

		radioPanel.add(circularLabel);
		radioPanel.add(circularYes);
		radioPanel.add(circularNo);
		radioPanel.add(Box.createRigidArea(new Dimension(10,0)));
		radioPanel.add(doubleStrandedLabel);
		radioPanel.add(doubleStrandedYes);
		radioPanel.add(doubleStrandedNo);
		radioPanel.add(Box.createHorizontalGlue());
		mainPanel.add(radioPanel);

		JSeparator js2=new JSeparator();
		js2.setForeground(Color.WHITE);
		js2.setMaximumSize(new Dimension(10000,2));
		js2.setMinimumSize(new Dimension(0,2));
		mainPanel.add(js2);
		mainPanel.add(Box.createRigidArea(new Dimension(0,5)));

		//genes
		JPanel genesLeftPanel=new JPanel();
		genesLeftPanel.setOpaque(false);
		genesLeftPanel.setLayout(new BoxLayout(genesLeftPanel,BoxLayout.Y_AXIS));
		JPanel genesLeftTopPanel=new JPanel();
		genesLeftTopPanel.setOpaque(false);
		genesLeftTopPanel.setLayout(new BoxLayout(genesLeftTopPanel,BoxLayout.X_AXIS));
		JLabel genesLabel=new JLabel("Genes:");
		genesLabel.setForeground(Color.WHITE);
		genesLeftTopPanel.add(genesLabel);
		genesLeftTopPanel.add(Box.createHorizontalGlue());
		genesLeftTopPanel.add(newButton);
		genesLeftTopPanel.add(Box.createRigidArea(new Dimension(5,0)));
		genesLeftTopPanel.add(loadButton);
		genesLeftTopPanel.add(Box.createRigidArea(new Dimension(5,0)));
		genesLeftTopPanel.add(removeButton);
		genesLeftPanel.add(genesLeftTopPanel);
		genesLeftPanel.add(genesScroll);

		genesPanel.add(genesLeftPanel);

		mainPanel.add(genesPanel);
		mainPanel.add(Box.createRigidArea(new Dimension(0,5)));

		mainPanel.add(js3);
		mainPanel.add(Box.createRigidArea(new Dimension(0,5)));

		//sitePanel
		JPanel siteTopPanel=new JPanel();
		siteTopPanel.setOpaque(false);
		siteTopPanel.setLayout(new BoxLayout(siteTopPanel,BoxLayout.X_AXIS));
		siteTopPanel.add(siteLabel);
		siteTopPanel.add(siteField);
		sitePanel.add(siteTopPanel);
		JPanel siteBottomPanel=new JPanel();
		siteBottomPanel.setOpaque(false);
		siteBottomPanel.add(siteHelpLabel);
		sitePanel.add(siteBottomPanel);
		mainPanel.add(sitePanel);

		//sequence
		JLabel sequenceLabel=new JLabel("Sequence:");
		sequenceLabel.setForeground(Color.WHITE);
		sequencePanel.add(sequenceLabel);
		sequencePanel.add(Box.createRigidArea(new Dimension(5,0)));
		sequencePanel.add(viewButton);
		sequencePanel.add(Box.createRigidArea(new Dimension(5,0)));
		sequencePanel.add(scanButton);
		sequencePanel.add(Box.createRigidArea(new Dimension(5,0)));
		JLabel gotoLabel=new JLabel("Go to:");
		gotoLabel.setForeground(Color.WHITE);
		sequencePanel.add(gotoLabel);
		sequencePanel.add(gotoField);
		mainPanel.add(sequencePanel);
		mainPanel.add(Box.createRigidArea(new Dimension(0,5)));
		mainPanel.add(sequenceScroll);
		mainPanel.add(Box.createRigidArea(new Dimension(0,5)));

		JSeparator js4=new JSeparator();
		js4.setForeground(Color.WHITE);
		js4.setMaximumSize(new Dimension(10000,2));
		js4.setMinimumSize(new Dimension(0,2));
		mainPanel.add(js4);
		mainPanel.add(Box.createRigidArea(new Dimension(0,5)));

		//buttons
		JPanel buttonsPanel=new JPanel();
		buttonsPanel.setOpaque(false);
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel,BoxLayout.X_AXIS));
		buttonsPanel.add(saveStatus);
		buttonsPanel.add(Box.createHorizontalGlue());
		buttonsPanel.add(saveButton);
		buttonsPanel.add(placeButton);
		mainPanel.add(buttonsPanel);

		add(mainPanel);
	}

	private void setupProperties(){
		setTitle("Sequence Creator");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setSize(500,450);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public boolean saveCurrentSequence(){	//save current selected sequence
		if(currentGenBank!=null){	//if there is a selected genbank in focus
			String newCoding=sequencePane.getText();
			if(validateSequence(newCoding)){
				saveSequence(newCoding,currentGenBank);
				currentGenBank=null;	//nullify currentGenBank
				return true;
			}else{	//wrong format
				JOptionPane.showMessageDialog(seqCreator,"The sequence has incorrect character.","Error saving",JOptionPane.PLAIN_MESSAGE);
				return false;
			}
		}
		return false;
	}

	private boolean validateSequence(String newCoding){	//method to validate the input new sequence
		//eliminate carriage return
		//change U to T
		newCoding=newCoding.replaceAll("\n","").replaceAll("U","T").toUpperCase();
		//only A T G C allowed
		for(int i=0;i<newCoding.length();i++){
			char theChar=newCoding.charAt(i);
			if(!(theChar=='A'||theChar=='T'||theChar=='G'||theChar=='C'))return false;
		}
		return true;
	}

	private void saveSequence(String newCoding,GenBank currentGenBank){	//method to save the sequence to genbank
		if(currentGenBank!=null){
			currentGenBank.completeSequence=newCoding;
		}
	}
}

interface Reorderable{	//for drag and drop function
	public void reorder(int fromIndex,int toIndex);
}
