package Tools.MOPTIMIZER;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

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

import Support.WeightTable;

public class ConfigureDialog extends JDialog{
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

	JDialog configureDialog=this;
	Moptimizer moptimizer;
	Color bgColor=new Color(70,70,70);

	JPanel buttonPanel;
	JButton loadWeightTable;
	JLabel minimumWeightLabel=new JLabel("Minimum weight:");
	JTextField minimumWeightText;
	ArrayList<JTextField> textField;	//textFields to store weights of all codons
	JPanel confirmPanel;
	JButton configureOKButton;
	JButton configureCancelButton;

	ConfigureDialog(Window ownerFrame,Moptimizer moptimizer){
		super(ownerFrame);
		this.moptimizer=moptimizer;
		initiate();
	}

	public ConfigureDialog(JDialog ownerFrame,File weightFile){	//used in Processor
		super(ownerFrame,false);
		initiate();
		buttonPanel.setVisible(false);
		confirmPanel.setVisible(false);
		setTitle(weightFile.getName());
		float[] weightTable=WeightTable.read(weightFile);
		DecimalFormat df=new DecimalFormat("0.#####");
		df.setRoundingMode(RoundingMode.HALF_UP);
		for(int i=0;i<textField.size();i++){
			textField.get(i).setText(""+df.format(weightTable[i]));
			textField.get(i).setEditable(false);
		}
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	void initiate(){
		createComponents();
		assignFunctions();
		layoutComponents();
		setupProperties();
	}

	void createComponents(){
		loadWeightTable=new JButton("Load Weight Table");
		minimumWeightLabel.setForeground(Color.WHITE);
		minimumWeightText=new JTextField("0.5");	//default is 0.5
		minimumWeightText.setPreferredSize(new Dimension(50,20));
		minimumWeightText.setMaximumSize(new Dimension(50,20));
		textField=new ArrayList<JTextField>();
		for(int i=0;i<64;i++){
			textField.add(new JTextField());
		}
		configureOKButton=new JButton("OK");
		configureCancelButton=new JButton("Cancel");
	}

	void assignFunctions(){
		//loadWeightTable
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
						temp=moptimizer.weightTable;
						moptimizer.weightTable=new float[64];
						for(int i=0;i<64;i++){
							moptimizer.weightTable[i]=Float.parseFloat(br.readLine());
						}
					
						br.close();
					}catch(Exception e){
						JOptionPane.showMessageDialog(configureDialog,e.toString(),"Error",JOptionPane.ERROR_MESSAGE);
						moptimizer.weightTable=temp;
						return;
					}
					//update configure dialog table
					DecimalFormat df=new DecimalFormat("0.#####");
					df.setRoundingMode(RoundingMode.HALF_UP);
					for(int i=0;i<64;i++){
						textField.get(i).setText(""+df.format(moptimizer.weightTable[i]));
					}
				}else{
					return;
				}
			}
		});

		//configureOKButton
		configureOKButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				//extract the weight table
				for(int i=0;i<64;i++){
					try{
						moptimizer.weightTable[i]=Float.parseFloat(textField.get(i).getText());
					}catch(NumberFormatException nfe){
						JOptionPane.showMessageDialog(configureDialog,"Insert number or decimal only.","Error",JOptionPane.PLAIN_MESSAGE);
						return;
					}
				}

				//extract the minimum weight
				try{
					moptimizer.minimumWeight=Float.parseFloat(minimumWeightText.getText());
					moptimizer.hasWeightTable=true;
				}catch(NumberFormatException nfe){
					JOptionPane.showMessageDialog(configureDialog,"Insert number or decimal only.","Error",JOptionPane.PLAIN_MESSAGE);
					return;
				}

				setVisible(false);
			}
		});

		//configureCancelButton
		configureCancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				setVisible(false);
			}
		});
	}

	void layoutComponents(){
		JPanel mainPanel=new JPanel();
		mainPanel.setBackground(bgColor);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
		buttonPanel=new JPanel();
		buttonPanel.setOpaque(false);
		buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.X_AXIS));
		buttonPanel.add(loadWeightTable);
		buttonPanel.add(Box.createRigidArea(new Dimension(150,0)));
		buttonPanel.add(Box.createHorizontalGlue());
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
		mainPanel.add(tablePanel);
		mainPanel.add(Box.createRigidArea(new Dimension(0,5)));
		confirmPanel=new JPanel();
		confirmPanel.setOpaque(false);
		confirmPanel.setLayout(new BoxLayout(confirmPanel,BoxLayout.X_AXIS));
		confirmPanel.add(Box.createHorizontalGlue());
		confirmPanel.add(configureOKButton);
		confirmPanel.add(Box.createRigidArea(new Dimension(5,0)));
		confirmPanel.add(configureCancelButton);
		mainPanel.add(confirmPanel);
		this.add(mainPanel);
	}

	void setupProperties(){
		setTitle("Load Weight Table");
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		pack();
		setResizable(true);
		setLocationRelativeTo(null);
		setVisible(false);
	}
}
