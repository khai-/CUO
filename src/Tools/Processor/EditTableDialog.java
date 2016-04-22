package Tools.Processor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.RoundingMode;
import java.text.DateFormat;
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
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

public class EditTableDialog extends JDialog{	//view tables but with "load","save","switch tab" functions
	EditTableDialog editTableDialog=this;
	Color bgColor=new Color(70,70,70);

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

	//weightTableTab
	JPanel weightTableTab;
	public ArrayList<JTextField> weightTextField;	//textFields to store weight of all codons
	JButton loadWeightButton;
	JButton clearWeightButton;
	JLabel saveWeightMessage;
	JButton saveWeightButton;

	//frequencyTableTab
	JPanel frequencyTableTab;
	public ArrayList<JTextField> frequencyTextField;	//textFields to store frequency of all codons
	JButton loadFrequencyButton;
	JButton clearFrequencyButton;
	JLabel saveFrequencyMessage;
	JButton saveFrequencyButton;

	//codonPairTableTab
	JPanel codonPairTableTab;

	public EditTableDialog(JDialog ownerFrame){
		super(ownerFrame,false);
		initialize();
	}

	private void initialize(){
		createComponents();
		assignFunctions();
		layoutComponents();
		setupProperties();
	}

	private void createComponents(){
		//weightTableTab
		weightTableTab=new JPanel();
		weightTextField=new ArrayList<JTextField>();
		for(int i=0;i<64;i++){
			weightTextField.add(new JTextField());
		}
		loadWeightButton=new JButton("Load");
		clearWeightButton=new JButton("Clear");
		saveWeightMessage=new JLabel();
		saveWeightMessage.setForeground(Color.WHITE);
		saveWeightButton=new JButton("Save");

		//frequencyTableTab
		frequencyTableTab=new JPanel();
		frequencyTextField=new ArrayList<JTextField>();
		for(int i=0;i<64;i++){
			frequencyTextField.add(new JTextField());
		}
		loadFrequencyButton=new JButton("Load");
		clearFrequencyButton=new JButton("Clear");
		saveFrequencyMessage=new JLabel();
		saveFrequencyMessage.setForeground(Color.WHITE);
		saveFrequencyButton=new JButton("Save");

		//codonPairTableTab
		codonPairTableTab=new JPanel();
	}

	private void assignFunctions(){
		//weightTableTab
		//loadWeightButton
		loadWeightButton.addActionListener(new ActionListener(){
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
				if(jfc.showOpenDialog(editTableDialog)==JFileChooser.APPROVE_OPTION){
					float[] temp=new float[64];
					//read *.wt to importedWeight[64]
					File weightFile=jfc.getSelectedFile();
					try{
						BufferedReader br=new BufferedReader(new FileReader(weightFile));
						for(int i=0;i<64;i++){
							temp[i]=Float.parseFloat(br.readLine());
						}
					
						br.close();
					}catch(Exception e){
						JOptionPane.showMessageDialog(editTableDialog,e.toString(),"Error",JOptionPane.ERROR_MESSAGE);
						return;
					}
					//update weight table
					DecimalFormat df=new DecimalFormat("0.#####");
					df.setRoundingMode(RoundingMode.HALF_UP);
					for(int i=0;i<64;i++){
						weightTextField.get(i).setText(""+df.format(temp[i]));
					}
				}else{
					return;
				}
			}
		});
		//clearWeightButton
		clearWeightButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				for(int i=0;i<weightTextField.size();i++){
					weightTextField.get(i).setText("");
				}
			}
		});
		//saveWeightButton
		saveWeightButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				//retrieve and validate the weights in the table
				float[] theWeight=new float[64];
				for(int i=0;i<weightTextField.size();i++){
					try{
						theWeight[i]=Float.parseFloat(weightTextField.get(i).getText());
					}catch(Exception e){
						JOptionPane.showMessageDialog(editTableDialog,"Please insert number between 0 and 1 (inclusive) only.","Incorrect input",JOptionPane.ERROR_MESSAGE);
						return;
					}
				}

				//get the file location
				String fileName="";
				File weightFile;
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
				if(jfc.showSaveDialog(editTableDialog)==JFileChooser.APPROVE_OPTION){
					weightFile=jfc.getSelectedFile();
					//create new one
					if(!weightFile.getName().endsWith(".wt")){
						weightFile=new File(weightFile.getPath()+".wt");
					}
				}else{
					return;
				}

				//save the file (overwrite existing)
				try{
					BufferedWriter bw=new BufferedWriter(new FileWriter(weightFile));
					for(int i=0;i<theWeight.length;i++){
						bw.write(Float.toString(theWeight[i])+"\n");
					}
					bw.close();
				}catch(Exception e){
					JOptionPane.showMessageDialog(editTableDialog,"Fail to save file.\n"+e.toString(),"Error",JOptionPane.ERROR_MESSAGE);
					return;
				}

				//show save successful
				DateFormat df=new SimpleDateFormat("HH:mm");
				Date time=new Date();
				saveWeightMessage.setText("Save successful at "+df.format(time)+".");
			}
		});

		//frequencyTabletab
		//loadFrequencyButton
		loadFrequencyButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				JFileChooser jfc=new JFileChooser("."+File.separator+"Construction");
				jfc.setAcceptAllFileFilterUsed(false);
				jfc.setFileFilter(new javax.swing.filechooser.FileFilter(){
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
						return "Codon Frequency Table file (*.cft)";
					}
				});
				if(jfc.showOpenDialog(editTableDialog)==JFileChooser.APPROVE_OPTION){
					int[] temp=new int[64];
					//read *.cft to temp[64]
					File frequencyFile=jfc.getSelectedFile();
					try{
						BufferedReader br=new BufferedReader(new FileReader(frequencyFile));
						for(int i=0;i<64;i++){
							temp[i]=Integer.parseInt(br.readLine());
						}
					
						br.close();
					}catch(Exception e){
						JOptionPane.showMessageDialog(editTableDialog,e.toString(),"Error",JOptionPane.ERROR_MESSAGE);
						return;
					}
					//update frequency table
					for(int i=0;i<64;i++){
						frequencyTextField.get(i).setText(""+temp[i]);
					}
				}else{
					return;
				}
			}
		});
		//clearFrequencyButton
		clearFrequencyButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				for(int i=0;i<frequencyTextField.size();i++){
					frequencyTextField.get(i).setText("");
				}
			}
		});
		//saveFrequencyButton
		saveFrequencyButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				//retrieve and validate the frequency in the table
				int[] theFrequency=new int[64];
				for(int i=0;i<frequencyTextField.size();i++){
					try{
						theFrequency[i]=Integer.parseInt(frequencyTextField.get(i).getText());
					}catch(Exception e){
						JOptionPane.showMessageDialog(editTableDialog,"Please insert integer only.","Incorrect input",JOptionPane.ERROR_MESSAGE);
						return;
					}
				}

				//get the file location
				String fileName="";
				File frequencyFile;
				JFileChooser jfc=new JFileChooser("."+File.separator+"Construction");
				jfc.setAcceptAllFileFilterUsed(false);
				jfc.setFileFilter(new javax.swing.filechooser.FileFilter(){
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
						return "Codon Frequency Table file (*.cft)";
					}
				});
				if(jfc.showSaveDialog(editTableDialog)==JFileChooser.APPROVE_OPTION){
					frequencyFile=jfc.getSelectedFile();
					//create new one
					if(!frequencyFile.getName().endsWith(".cft")){
						frequencyFile=new File(frequencyFile.getPath()+".cft");
					}
				}else{
					return;
				}

				//save the file (overwrite existing)
				try{
					BufferedWriter bw=new BufferedWriter(new FileWriter(frequencyFile));
					for(int i=0;i<theFrequency.length;i++){
						bw.write(Integer.toString(theFrequency[i])+"\n");
					}
					bw.close();
				}catch(Exception e){
					JOptionPane.showMessageDialog(editTableDialog,"Fail to save file.\n"+e.toString(),"Error",JOptionPane.ERROR_MESSAGE);
					return;
				}

				//show save successful
				DateFormat df=new SimpleDateFormat("HH:mm");
				Date time=new Date();
				saveFrequencyMessage.setText("Save successful at "+df.format(time)+".");
			}
		});
	}

	private void layoutComponents(){
		JPanel mainPanel=new JPanel();
		mainPanel.setBackground(bgColor);
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		JTabbedPane tabbedPane=new JTabbedPane();
		tabbedPane.setBackground(bgColor);
		tabbedPane.setForeground(Color.WHITE);

		//weightTableTab
		weightTableTab.setBackground(bgColor);
		weightTableTab.setLayout(new BoxLayout(weightTableTab,BoxLayout.Y_AXIS));
		populateTable(weightTableTab,weightTextField);	//populate weightTabeTab
		JPanel weightButtonPanel=new JPanel();
		weightButtonPanel.setOpaque(false);
		weightButtonPanel.setLayout(new BoxLayout(weightButtonPanel,BoxLayout.X_AXIS));
		weightButtonPanel.setBorder(BorderFactory.createEmptyBorder(0,5,5,5));
		weightButtonPanel.add(loadWeightButton);
		weightButtonPanel.add(Box.createRigidArea(new Dimension(20,0)));
		weightButtonPanel.add(clearWeightButton);
		weightButtonPanel.add(Box.createHorizontalGlue());
		weightButtonPanel.add(saveWeightMessage);
		weightButtonPanel.add(Box.createRigidArea(new Dimension(5,0)));
		weightButtonPanel.add(saveWeightButton);
		weightTableTab.add(weightButtonPanel);
		tabbedPane.addTab("Weight Table",weightTableTab);

		//frequencyTableTab
		frequencyTableTab.setBackground(bgColor);
		frequencyTableTab.setLayout(new BoxLayout(frequencyTableTab,BoxLayout.Y_AXIS));
		populateTable(frequencyTableTab,frequencyTextField);	//populate frequencyTableTab
		JPanel frequencyButtonPanel=new JPanel();
		frequencyButtonPanel.setOpaque(false);
		frequencyButtonPanel.setLayout(new BoxLayout(frequencyButtonPanel,BoxLayout.X_AXIS));
		frequencyButtonPanel.setBorder(BorderFactory.createEmptyBorder(0,5,5,5));
		frequencyButtonPanel.add(loadFrequencyButton);
		frequencyButtonPanel.add(Box.createRigidArea(new Dimension(20,0)));
		frequencyButtonPanel.add(clearFrequencyButton);
		frequencyButtonPanel.add(Box.createHorizontalGlue());
		frequencyButtonPanel.add(saveFrequencyMessage);
		frequencyButtonPanel.add(Box.createRigidArea(new Dimension(5,0)));
		frequencyButtonPanel.add(saveFrequencyButton);
		frequencyTableTab.add(frequencyButtonPanel);
		tabbedPane.addTab("Codon Frequency Table",frequencyTableTab);

		//codonPairTableTab
		codonPairTableTab.setBackground(bgColor);
		codonPairTableTab.setLayout(new BoxLayout(codonPairTableTab,BoxLayout.Y_AXIS));
		tabbedPane.addTab("Codon Pair Table",codonPairTableTab);

		mainPanel.add(tabbedPane);
		add(mainPanel);
	}

	private void setupProperties(){
		setTitle("Edit Table");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setSize(600,450);
		setResizable(true);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	//methodized table population for weight and codon frequency table
	private void populateTable(JPanel tab,ArrayList<JTextField> textField){
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
			codonPanel.add(textField.get(i));
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
			codonPanel.add(textField.get(16+i));
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
			codonPanel.add(textField.get(32+i));
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
			codonPanel.add(textField.get(48+i));
			rightPanel.add(codonPanel);
		}
		tablePanel.add(rightPanel);
		tab.add(tablePanel);
	}
}
