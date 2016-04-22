package Tools.Processor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;

import Support.CodonFrequencyTable;
import Support.CodonPairCalculator;
import Support.Common;
import Support.WeightTable;

public class CompareTablesDialog extends JDialog{
	CompareTablesDialog compareTablesDialog=this;
	JDialog PROCESSOR;
	Font titleFont=new Font(null,Font.PLAIN,12);
	Color bgColor=new Color(70,70,70);

	//tables list
	JButton addTableButton;
	JList compareTablesList;
	DefaultListModel theModel;
	ArrayList<File> fileList;
	JScrollPane compareTablesScroll;

	//filters
	JComboBox belowAboveCombo;
	JTextField belowAboveField;
	JCheckBox noStopCodon;

	//return
	ButtonGroup returnButtonGroup;
	JRadioButton returnAllButton;
	JRadioButton returnSimilar;

	//compare button
	JButton compareButton;

	public CompareTablesDialog(JDialog ownerFrame){
		super(ownerFrame,false);
		this.PROCESSOR=ownerFrame;
		createComponents();
		assignFunctions();
		layoutComponents();
		setupProperties();
	}

	private void createComponents(){
		//tables list
		addTableButton=new JButton("Add Table");
		compareTablesList=new JList();
		theModel=new DefaultListModel();
		compareTablesList.setModel(theModel);
		fileList=new ArrayList<File>();
		compareTablesScroll=new JScrollPane(compareTablesList,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		//filters
		belowAboveCombo=new JComboBox(new String[]{"Below","Above"});
		belowAboveCombo.setMaximumSize(new Dimension(10000,20));
		belowAboveField=new JTextField();
		belowAboveField.setMaximumSize(new Dimension(10000,20));
		noStopCodon=new JCheckBox("No STOP codon");
		noStopCodon.setSelected(true);
		noStopCodon.setForeground(Color.WHITE);
		noStopCodon.setOpaque(false);

		//return
		returnButtonGroup=new ButtonGroup();
		returnAllButton=new JRadioButton("All");
		returnAllButton.setSelected(true);
		returnAllButton.setOpaque(false);
		returnAllButton.setForeground(Color.WHITE);
		returnButtonGroup.add(returnAllButton);
		returnSimilar=new JRadioButton("Similar");
		returnSimilar.setOpaque(false);
		returnSimilar.setForeground(Color.WHITE);
		returnButtonGroup.add(returnSimilar);

		//compare button
		compareButton=new JButton("Compare");
	}

	private void assignFunctions(){
		//addTableButton
		addTableButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				JFileChooser fc=new JFileChooser("."+File.separator+"Construction");
				fc.setMultiSelectionEnabled(true);
				fc.setAcceptAllFileFilterUsed(false);
				fc.setFileFilter(new FileFilter(){
					public boolean accept(File f){
						if(f.isDirectory()){
							return true;
						}
						String extension=f.getName().split("\\.")[f.getName().split("\\.").length-1];
						if(extension.equals("cpt")){
							return true;
						}
						return false;
					}
					public String getDescription(){
						return "Codon pair table file (*.cpt)";
					}
				});
				fc.addChoosableFileFilter(new FileFilter(){
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
						return "Weight table file (*.wt)";
					}
				});
				fc.addChoosableFileFilter(new FileFilter(){
					public boolean accept(File f){
						if(f.isDirectory()){
							return true;
						}
						String extension=f.getName().split("\\.")[f.getName().split("\\.").length-1];
						if(extension.equals("cft")){
							return true;
						}
						return false;
					}
					public String getDescription(){
						return "Codon frequency table file (*.cft)";
					}
				});
				fc.addChoosableFileFilter(new FileFilter(){
					public boolean accept(File f){
						if(f.isDirectory()){
							return true;
						}
						String extension=f.getName().split("\\.")[f.getName().split("\\.").length-1];
						if(extension.equals("cpf")){
							return true;
						}
						return false;
					}
					public String getDescription(){
						return "Codon pair frequency table file (*.cpf)";
					}
				});

				if(fc.showOpenDialog(PROCESSOR)==fc.APPROVE_OPTION){
					File[] theFiles=fc.getSelectedFiles();
					//validate selection
					String currentFormat="";
					if(fileList.size()!=0)currentFormat=fileList.get(0).getName().substring(fileList.get(0).getName().lastIndexOf('.')+1);
					for(int i=0;i<theFiles.length;i++){
						//match with existing fileList file type
						if(!currentFormat.equals("")&&!theFiles[i].getName().substring(theFiles[i].getName().lastIndexOf('.')+1).equals(currentFormat)){
							JOptionPane.showMessageDialog(compareTablesDialog,"Comparison is only possible between tables of the same type.");
							return;	//do not add
						}
					}

					//add to the model and the file list
					for(int i=0;i<theFiles.length;i++){
						theModel.addElement(theFiles[i].getName());
						compareTablesList.setModel(theModel);
						fileList.add(theFiles[i]);
					}
				}
			}
		});

		//compareTablesList
		compareTablesList.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent ke){
				int theKey=ke.getKeyCode();
				if(theKey==ke.VK_DELETE){
					int[] indices=compareTablesList.getSelectedIndices();
					Arrays.sort(indices);	//in ascending order
					for(int i=indices.length-1;i>=0;i--){	//reversely
						theModel.remove(indices[i]);	//remove from model
						fileList.remove(indices[i]);	//remove from fileList
					}
				}
			}
		});

		//compareButton
		compareButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				//check for valid input
				if(fileList.size()==0)return;
				String belowAboveFieldText=belowAboveField.getText();
				if(belowAboveFieldText.equals(""))return;
				String comparedTablesType=fileList.get(0).getName().substring(fileList.get(0).getName().lastIndexOf('.')+1);	//identify compared tables type
				Float belowAboveValue=0f;
				try{
					belowAboveValue=Float.parseFloat(belowAboveFieldText);
				}catch(NumberFormatException nfe){
					JOptionPane.showMessageDialog(compareTablesDialog,"Please put numbers only into filter text field");
					return;
				}
				if(comparedTablesType.equals("cft")||comparedTablesType.equals("cpf")){
					if(belowAboveValue!=Math.round(belowAboveValue)){
						JOptionPane.showMessageDialog(compareTablesDialog,"Instead of decimal, integer is used as comparison value between .cft or .cpf.");
						return;
					}
				}

				//get filtering conditions
				String boundaryCondition=(String)belowAboveCombo.getSelectedItem();

				//get returnMode
				String returnMode="";
				if(returnAllButton.isSelected()){
					returnMode="All";
				}else if(returnSimilar.isSelected()){
					returnMode="Similar";
				}

				//respond differently with different tables type
				if(comparedTablesType.equals("cpt")){
					//read the tables from files
					ArrayList<float[][]> codonPairTables=new ArrayList<float[][]>();
					for(int i=0;i<fileList.size();i++){
						codonPairTables.add(CodonPairCalculator.read(fileList.get(i)));
					}

					compareCPT(codonPairTables,boundaryCondition,belowAboveValue,noStopCodon.isSelected(),returnMode);
				}else if(comparedTablesType.equals("wt")){
					//read the tables from files
					ArrayList<float[]> weightTables=new ArrayList<float[]>();
					for(int i=0;i<fileList.size();i++){
						weightTables.add(WeightTable.read(fileList.get(i)));
					}

					compareWT(weightTables,boundaryCondition,belowAboveValue,noStopCodon.isSelected(),returnMode);
				}else if(comparedTablesType.equals("cft")){
					//read the tables from files
					ArrayList<int[]> codonFrequencyTables=new ArrayList<int[]>();
					for(int i=0;i<fileList.size();i++){
						codonFrequencyTables.add(CodonFrequencyTable.read(fileList.get(i)));
					}

					compareCFT(codonFrequencyTables,boundaryCondition,belowAboveValue,noStopCodon.isSelected(),returnMode);
				}else if(comparedTablesType.equals("cpf")){
					//read the tables from files
					ArrayList<int[][]> codonPairFrequency=new ArrayList<int[][]>();
					for(int i=0;i<fileList.size();i++){
						codonPairFrequency.add(CodonPairCalculator.readCPF(fileList.get(i)));
					}

					compareCPF(codonPairFrequency,boundaryCondition,belowAboveValue,noStopCodon.isSelected(),returnMode);
				}
			}
		});
	}

	private void layoutComponents(){
		JPanel mainPanel=new JPanel();
		mainPanel.setBackground(bgColor);
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.X_AXIS));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

		//tables list
		JPanel listPanel=new JPanel();
		listPanel.setOpaque(false);
		listPanel.setLayout(new BoxLayout(listPanel,BoxLayout.Y_AXIS));
		listPanel.add(addTableButton);
		listPanel.add(Box.createRigidArea(new Dimension(0,3)));
		listPanel.add(compareTablesScroll);
		mainPanel.add(listPanel);
		mainPanel.add(Box.createRigidArea(new Dimension(5,0)));

		//filters
		JPanel preferencesPanel=new JPanel();
		preferencesPanel.setOpaque(false);
		preferencesPanel.setLayout(new BoxLayout(preferencesPanel,BoxLayout.Y_AXIS));
		JPanel filtersPanel=new JPanel();
		filtersPanel.setOpaque(false);
		filtersPanel.setLayout(new BoxLayout(filtersPanel,BoxLayout.Y_AXIS));
		filtersPanel.setBorder(BorderFactory.createTitledBorder(filtersPanel.getBorder(),"Filters:",TitledBorder.LEFT,TitledBorder.TOP,titleFont,Color.WHITE));
		JPanel belowAbovePanel=new JPanel();
		belowAbovePanel.setOpaque(false);
		belowAbovePanel.setLayout(new BoxLayout(belowAbovePanel,BoxLayout.X_AXIS));
		belowAbovePanel.add(belowAboveCombo);
		JLabel semicolon=new JLabel(" : ");
		semicolon.setForeground(Color.WHITE);
		belowAbovePanel.add(semicolon);
		belowAbovePanel.add(belowAboveField);
		filtersPanel.add(belowAbovePanel);
		filtersPanel.add(Box.createRigidArea(new Dimension(0,3)));
		JPanel noStopCodonPanel=new JPanel();
		noStopCodonPanel.setOpaque(false);
		noStopCodonPanel.setLayout(new BoxLayout(noStopCodonPanel,BoxLayout.X_AXIS));
		noStopCodonPanel.add(noStopCodon);
		noStopCodonPanel.add(Box.createHorizontalGlue());
		filtersPanel.add(noStopCodonPanel);
		preferencesPanel.add(filtersPanel);

		//return
		JPanel returnPanel=new JPanel();
		returnPanel.setOpaque(false);
		returnPanel.setLayout(new BoxLayout(returnPanel,BoxLayout.X_AXIS));
		returnPanel.setBorder(BorderFactory.createTitledBorder(returnPanel.getBorder(),"Return:",TitledBorder.LEFT,TitledBorder.TOP,titleFont,Color.WHITE));
		returnPanel.add(returnAllButton);
		returnPanel.add(returnSimilar);
		returnPanel.add(Box.createHorizontalGlue());
		preferencesPanel.add(returnPanel);

		//compare button
		preferencesPanel.add(Box.createVerticalGlue());
		preferencesPanel.add(compareButton);
		mainPanel.add(preferencesPanel);

		add(mainPanel);
	}

	private void setupProperties(){
		//setupProperties
		setTitle("Compare between tables");
		setSize(500,300);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	private void showPairResult(ArrayList<int[]> returnResult,String title){	//show a codon pair result
		//bake styleddocument
		DefaultStyledDocument doc=new DefaultStyledDocument();
		try{
			for(int n=0;n<returnResult.size();n++){
				String output=Common.getAAByCoordinate(returnResult.get(n)[0])+":"+Common.getAAByCoordinate(returnResult.get(n)[1])+"\t"+Common.getCodonByCoordinate(returnResult.get(n)[0])+":"+Common.getCodonByCoordinate(returnResult.get(n)[1]);
				doc.insertString(doc.getLength(),output+"\n",null);
			}
		}catch(BadLocationException ble){return;}//abort upon failure

		//present the result in new dialog
		JDialog resultDialog=new JDialog(compareTablesDialog,false);
		JTextPane thePane=new JTextPane();
		thePane.setStyledDocument(doc);
		JScrollPane theScroll=new JScrollPane(thePane,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JPanel mainPanel=new JPanel();
		mainPanel.setBackground(bgColor);
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(theScroll);
		resultDialog.add(mainPanel);
		resultDialog.setTitle(title);
		resultDialog.setSize(200,300);
		resultDialog.setLocationRelativeTo(null);
		resultDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		resultDialog.setVisible(true);
	}

	private void showCodonResult(ArrayList<Integer> returnResult,String title){	//show a single codon result
		//bake styleddocument
		DefaultStyledDocument doc=new DefaultStyledDocument();
		try{
			for(int n=0;n<returnResult.size();n++){
				String output=Common.getAAByCoordinate(returnResult.get(n))+"\t"+Common.getCodonByCoordinate(returnResult.get(n));
				doc.insertString(doc.getLength(),output+"\n",null);
			}
		}catch(BadLocationException ble){return;}//abort upon failure

		//present the result in new dialog
		JDialog resultDialog=new JDialog(compareTablesDialog,false);
		JTextPane thePane=new JTextPane();
		thePane.setStyledDocument(doc);
		JScrollPane theScroll=new JScrollPane(thePane,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JPanel mainPanel=new JPanel();
		mainPanel.setBackground(bgColor);
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(theScroll);
		resultDialog.add(mainPanel);
		resultDialog.setTitle(title);
		resultDialog.setSize(200,300);
		resultDialog.setLocationRelativeTo(null);
		resultDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		resultDialog.setVisible(true);
	}

	private void compareCPT(ArrayList<float[][]> codonPairTables,String boundaryCondition,float belowAboveValue,boolean noStop,String returnMode){
		//pass through the fillters
		ArrayList<ArrayList<int[]>> passedList=new ArrayList<ArrayList<int[]>>();	//0=first codon 1=second codon
		for(int i=0;i<codonPairTables.size();i++){
			ArrayList<int[]> passedCoordinates=new ArrayList<int[]>();
			for(int j=0;j<64;j++){
				for(int k=0;k<64;k++){
					//no stop codon filter
					if(noStop&&(Common.getAAByCoordinate(j).equals("STOP")||Common.getAAByCoordinate(k).equals("STOP"))){
						continue;	//skip this
					}

					//belowAbove boundary filter
					if(boundaryCondition.equals("Above")){	//Above
						if(codonPairTables.get(i)[j][k]>=belowAboveValue){
							passedCoordinates.add(new int[]{j,k});
						}
					}else if(boundaryCondition.equals("Below")){	//Below
						if(codonPairTables.get(i)[j][k]<=belowAboveValue){
							passedCoordinates.add(new int[]{j,k});
						}
					}
				}
			}
			passedList.add(passedCoordinates);
		}

		//getting the return
		if(returnMode.equals("All")){	//All
			//for each tables,present the filtered result
			for(int p=0;p<passedList.size();p++){
				showPairResult(passedList.get(p),"All: "+fileList.get(p).getName()+" "+boundaryCondition.toLowerCase()+" "+belowAboveValue);
			}
		}else if(returnMode.equals("Similar")){	//Similar
			ArrayList<int[]> returnResult=passedList.get(0);//first sample
			for(int p=1;p<passedList.size();p++){
				ArrayList<Integer> toRemove=new ArrayList<Integer>();
				for(int k=0;k<returnResult.size();k++){
					boolean gotAMatch=false;
					for(int j=0;j<passedList.get(p).size();j++){
						if(returnResult.get(k)[0]==passedList.get(p).get(j)[0]&&returnResult.get(k)[1]==passedList.get(p).get(j)[1]){
							gotAMatch=true;
							break;
						}
					}
					if(!gotAMatch){
						toRemove.add(k);
					}
				}
				Collections.sort(toRemove,Collections.reverseOrder());	//remove from largest index downwards to avoid index shifting
				for(int m=0;m<toRemove.size();m++){
					returnResult.remove((int)toRemove.get(m));
				}
			}
			showPairResult(returnResult,"Similar: "+passedList.size()+" tables "+boundaryCondition.toLowerCase()+" "+belowAboveValue);
		}
	}

	private void compareWT(ArrayList<float[]> weightTables,String boundaryCondition,float belowAboveValue,boolean noStop,String returnMode){
		//pass through the fillters
		ArrayList<ArrayList<Integer>> passedList=new ArrayList<ArrayList<Integer>>();	//list of passed tables' codons
		for(int i=0;i<weightTables.size();i++){
			ArrayList<Integer> passedCoordinates=new ArrayList<Integer>();
			for(int j=0;j<64;j++){
				//no stop codon filter
				if(noStop&&Common.getAAByCoordinate(j).equals("STOP")){
					continue;	//skip this
				}

				//belowAbove boundary filter
				if(boundaryCondition.equals("Above")){	//Above
					if(weightTables.get(i)[j]>=belowAboveValue){
						passedCoordinates.add(j);
					}
				}else if(boundaryCondition.equals("Below")){	//Below
					if(weightTables.get(i)[j]<=belowAboveValue){
						passedCoordinates.add(j);
					}
				}
			}
			passedList.add(passedCoordinates);
		}

		//getting the return
		if(returnMode.equals("All")){	//All
			//for each tables,present the filtered result
			for(int p=0;p<passedList.size();p++){
				showCodonResult(passedList.get(p),"All: "+fileList.get(p).getName()+" "+boundaryCondition.toLowerCase()+" "+belowAboveValue);
			}
		}else if(returnMode.equals("Similar")){	//Similar
			ArrayList<Integer> returnResult=passedList.get(0);//first sample
			for(int p=1;p<passedList.size();p++){
				ArrayList<Integer> toRemove=new ArrayList<Integer>();
				for(int k=0;k<returnResult.size();k++){
					boolean gotAMatch=false;
					for(int j=0;j<passedList.get(p).size();j++){
						if(returnResult.get(k)==passedList.get(p).get(j)){
							gotAMatch=true;
							break;
						}
					}
					if(!gotAMatch){
						toRemove.add(k);
					}
				}
				Collections.sort(toRemove,Collections.reverseOrder());	//remove from largest index downwards to avoid index shifting
				for(int m=0;m<toRemove.size();m++){
					returnResult.remove((int)toRemove.get(m));
				}
			}
			showCodonResult(returnResult,"Similar: "+passedList.size()+" tables "+boundaryCondition.toLowerCase()+" "+belowAboveValue);
		}
	}

	private void compareCFT(ArrayList<int[]> codonFrequencyTables,String boundaryCondition,float belowAboveValue,boolean noStop,String returnMode){
		ArrayList<float[]> theTables=new ArrayList<float[]>();
		for(int i=0;i<codonFrequencyTables.size();i++){ //convert arraylist int[] to float[]
			float[] theFloat=new float[64];
			for(int j=0;j<64;j++){
				theFloat[j]=codonFrequencyTables.get(i)[j];
			}
			theTables.add(theFloat);
		}
		compareWT(theTables,boundaryCondition,belowAboveValue,noStop,returnMode);	//they are actually the same :D
	}

	private void compareCPF(ArrayList<int[][]> codonPairFrequency,String boundaryCondition,float belowAboveValue,boolean noStop,String returnMode){
		ArrayList<float[][]> theTables=new ArrayList<float[][]>();
		for(int i=0;i<codonPairFrequency.size();i++){ //convert arraylist int[][] to float[][]
			float[][] theFloat=new float[64][64];
			for(int j=0;j<64;j++){
				for(int k=0;k<64;k++){
					theFloat[j][k]=codonPairFrequency.get(i)[j][k];
				}
			}
			theTables.add(theFloat);
		}
		compareCPT(theTables,boundaryCondition,belowAboveValue,noStop,returnMode);	//they are actually the same :D
	}
}
