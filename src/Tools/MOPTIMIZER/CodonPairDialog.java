package Tools.MOPTIMIZER;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import Support.CodonPairCalculator;

public class CodonPairDialog extends JDialog{
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

	JDialog codonPairDialog=this;
	Moptimizer moptimizer;
	Color bgColor=new Color(70,70,70);

	boolean tableLoaded=false;

	JPanel buttonPanel;
	JButton loadCodonPairTableButton;
	JButton showTableButton;	//button to hide or show the gigantic codon pair table
	ImageIcon expand_up=new ImageIcon("Icons"+File.separator+"expandTab_up.gif");
	ImageIcon expand_down=new ImageIcon("Icons"+File.separator+"expandTab_down.gif");
	ImageIcon collapse_up=new ImageIcon("Icons"+File.separator+"collapseTab_up.gif");
	ImageIcon collapse_down=new ImageIcon("Icons"+File.separator+"collapseTab_down.gif");
	JLabel showTableLabel;
	JPanel searchPanel;
	JTextField searchField1;	//left
	JTextField searchField2;	//right
	JButton searchButton;
	JPanel searchResult;	//the panel of last search result
	JLabel minimalValueLabel;
	JTextField minimalValue;
	JPanel tablePanel;
	JScrollPane tableScroll;
	JTextField[][] textField=new JTextField[64][64];	//textFields of codon pair table
	JButton okButton;
	JButton cancelButton;

	public CodonPairDialog(Window ownerFrame,Moptimizer moptimizer){
		super(ownerFrame);
		this.moptimizer=moptimizer;
		initiate();
	}

	public CodonPairDialog(JDialog ownerFrame,File pairFile){
		super(ownerFrame,false);
		initiate();
		loadCodonPairTableButton.setVisible(false);
		showTableButton.setVisible(false);
		showTableLabel.setVisible(false);
		searchPanel.setVisible(true);
		minimalValueLabel.setVisible(false);
		minimalValue.setVisible(false);
		okButton.setVisible(false);
		cancelButton.setVisible(false);
		tableScroll.setVisible(true);
		setTitle(pairFile.getName());
		float[][] codonPairTable=CodonPairCalculator.read(pairFile);
		DecimalFormat df=new DecimalFormat("0.#####");
		df.setRoundingMode(RoundingMode.HALF_UP);
		for(int i=0;i<64;i++){
			for(int j=0;j<64;j++){
				textField[i][j].setText(""+df.format(codonPairTable[i][j]));
				textField[i][j].setEditable(false);
			}
		}
		//generate the 4096 labels and textFields
		String[] theCodons={"UUU","UUC","UUA","UUG","CUU","CUC","CUA","CUG","AUU","AUC","AUA","AUG", "GUU","GUC","GUA","GUG","UCU","UCC","UCA","UCG","CCU","CCC","CCA","CCG","ACU","ACC","ACA","ACG","GCU","GCC", "GCA","GCG","UAU","UAC","UAA","UAG","CAU","CAC","CAA","CAG","AAU","AAC","AAA","AAG","GAU","GAC","GAA","GAG", "UGU","UGC","UGA","UGG","CGU","CGC","CGA","CGG","AGU","AGC","AGA","AGG","GGU","GGC","GGA","GGG"};
		for(int k=0;k<8;k++){	//divide into 8 parts each 512 rows
			JPanel bigPanel=new JPanel();
			bigPanel.setOpaque(false);
			bigPanel.setLayout(new BoxLayout(bigPanel,BoxLayout.Y_AXIS));
			for(int i=0;i<8;i++){
				for(int j=0;j<64;j++){
					JPanel thePanel=new JPanel();
					thePanel.setOpaque(false);
					thePanel.setLayout(new BoxLayout(thePanel,BoxLayout.X_AXIS));
					JLabel theLabel=new JLabel(theCodons[k*8+i]+":"+theCodons[j]);
					JTextField theField=textField[k*8+i][j];
					thePanel.add(theLabel);
					thePanel.add(theField);

					bigPanel.add(thePanel);
				}
			}
			tablePanel.add(bigPanel);
			if(k!=7)tablePanel.add(Box.createRigidArea(new Dimension(5,0)));
		}
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				pack();
			}
		});
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	private void initiate(){
		createComponents();
		assignFunctions();
		layoutComponents();
		setupProperties();
	}

	private void createComponents(){
		loadCodonPairTableButton=new JButton("Load Codon Pair Table");
		showTableButton=new JButton(expand_up);
		showTableButton.setContentAreaFilled(false);
		showTableButton.setBorderPainted(false);
		showTableButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		showTableLabel=new JLabel("Show table");
		showTableLabel.setForeground(Color.WHITE);
		searchPanel=new JPanel();
		searchField1=new JTextField();
		searchField1.setPreferredSize(new Dimension(20,20));
		searchField2=new JTextField();
		searchField2.setPreferredSize(new Dimension(20,20));
		searchButton=new JButton("Search");
		Font theFont=new Font(null,Font.PLAIN,9);
		searchButton.setFont(theFont);
		minimalValue=new JTextField();
		minimalValue.setText("0.5");	//default is 0.5
		minimalValue.setPreferredSize(new Dimension(100,20));
		minimalValue.setMaximumSize(new Dimension(100,20));
		okButton=new JButton("OK");
		cancelButton=new JButton("Cancel");
		tablePanel=new JPanel();
		tablePanel.setOpaque(false);
		tablePanel.setLayout(new BoxLayout(tablePanel,BoxLayout.X_AXIS));
		tableScroll=new JScrollPane(tablePanel,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		tableScroll.getViewport().setBackground(Color.WHITE);
		tableScroll.getVerticalScrollBar().setUnitIncrement(10);
		tableScroll.setVisible(false);
		for(int i=0;i<64;i++){
			for(int j=0;j<64;j++){
				textField[i][j]=new JTextField();
			}
		}
	}

	private void assignFunctions(){
		//loadCodonPairTableButton
		loadCodonPairTableButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				JFileChooser jfc=new JFileChooser("."+File.separator+"Construction");
				jfc.setAcceptAllFileFilterUsed(false);
				jfc.setFileFilter(new javax.swing.filechooser.FileFilter(){
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
						return "Codon Pair Table (*.cpt)";
					}
				});
				if(jfc.showOpenDialog(codonPairDialog)==JFileChooser.APPROVE_OPTION){
					//read *.cpt
					File cptFile=jfc.getSelectedFile();
					float[][] temp=CodonPairCalculator.read(cptFile);
					
					//update codon pair dialog table
					DecimalFormat df=new DecimalFormat("0.#####");
					df.setRoundingMode(RoundingMode.HALF_UP);
					for(int i=0;i<64;i++){
						for(int j=0;j<64;j++){
							textField[i][j].setText(""+df.format(temp[i][j]));
						}
					}
				}else{
					return;
				}
			}
		});

		//showTableButton
		showTableButton.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				if(showTableButton.getIcon().equals(expand_up)){
					showTableButton.setIcon(expand_down);
				}else{ //showTableButton.getIcon().equals(collapse_up)
					showTableButton.setIcon(collapse_down);
				}
			}
			public void mouseReleased(MouseEvent me){
				if(showTableButton.getIcon().equals(expand_down)){
					showTableButton.setIcon(collapse_up);
					searchPanel.setVisible(true);

					if(!tableLoaded){	//table is not loaded yet
						//generate the 4096 labels and textFields
						String[] theCodons={"UUU","UUC","UUA","UUG","CUU","CUC","CUA","CUG","AUU","AUC","AUA","AUG", "GUU","GUC","GUA","GUG","UCU","UCC","UCA","UCG","CCU","CCC","CCA","CCG","ACU","ACC","ACA","ACG","GCU","GCC", "GCA","GCG","UAU","UAC","UAA","UAG","CAU","CAC","CAA","CAG","AAU","AAC","AAA","AAG","GAU","GAC","GAA","GAG", "UGU","UGC","UGA","UGG","CGU","CGC","CGA","CGG","AGU","AGC","AGA","AGG","GGU","GGC","GGA","GGG"};
						for(int k=0;k<8;k++){	//divide into 8 parts each 512 rows
							JPanel bigPanel=new JPanel();
							bigPanel.setOpaque(false);
							bigPanel.setLayout(new BoxLayout(bigPanel,BoxLayout.Y_AXIS));
							for(int i=0;i<8;i++){
								for(int j=0;j<64;j++){
									JPanel thePanel=new JPanel();
									thePanel.setOpaque(false);
									thePanel.setLayout(new BoxLayout(thePanel,BoxLayout.X_AXIS));
									JLabel theLabel=new JLabel(theCodons[k*8+i]+":"+theCodons[j]);
									JTextField theField=textField[k*8+i][j];
									thePanel.add(theLabel);
									thePanel.add(theField);

									bigPanel.add(thePanel);
								}
							}
							tablePanel.add(bigPanel);
							if(k!=7)tablePanel.add(Box.createRigidArea(new Dimension(5,0)));
						}
						tableLoaded=true;
						tableScroll.setVisible(true);
						SwingUtilities.invokeLater(new Runnable(){
							public void run(){
								pack();
							}
						});
						setLocationRelativeTo(null);
					}else{	//table is already loaded
						tableScroll.setVisible(true);
						pack();
						setLocationRelativeTo(null);
					}
				}else{	//showTableButton.getIcon().equals(collapse_down)
					showTableButton.setIcon(expand_up);
					searchPanel.setVisible(false);
					tableScroll.setVisible(false);
					pack();
					setLocationRelativeTo(null);
				}
			}
		});

		//searchFields
		searchField1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				searchButton.doClick();
			}
		});
		searchField2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				searchButton.doClick();
			}
		});
		//searchButton
		searchButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				//format the searchFields
				String fieldString1=searchField1.getText();
				fieldString1=fieldString1.toUpperCase();
				fieldString1=fieldString1.replaceAll("T","U");
				fieldString1=fieldString1.trim();
				if(fieldString1.length()>=3){
					fieldString1=fieldString1.substring(0,3);
				}else{
					return;
				}
				searchField1.setText(fieldString1);
				String fieldString2=searchField2.getText();
				fieldString2=fieldString2.toUpperCase();
				fieldString2=fieldString2.replaceAll("T","U");
				fieldString2=fieldString2.trim();
				if(fieldString2.length()>=3){
					fieldString2=fieldString2.substring(0,3);
				}else{
					fieldString2="";
				}
				searchField2.setText(fieldString2);

				//clear previous search highlight
				if(searchResult!=null){
					searchResult.setBorder(null);
				}

				//check for validity of codons inserted
				if(fieldString1.matches("[AUGC][AUGC][AUGC]")&&(fieldString2.matches("[AUGC][AUGC][AUGC]")||fieldString2.equals(""))){
					for(int i=0;i<tablePanel.getComponentCount();i++){
						if(i%2!=0){	//strip away the rigid areas
							continue;
						}

						JPanel bigPanel=(JPanel)tablePanel.getComponent(i);
						for(int j=0;j<bigPanel.getComponentCount();j++){
							searchResult=(JPanel)bigPanel.getComponent(j);
							JLabel theLabel=(JLabel)searchResult.getComponent(0);
							String theText=theLabel.getText();
							JTextField theField=(JTextField)searchResult.getComponent(1);

							if(fieldString2.equals("")){	//single codon search
								if(theText.substring(0,3).equals(fieldString1)){
									searchResult.setBorder(BorderFactory.createLineBorder(Color.RED,3));
									Rectangle r=new Rectangle(0,0,searchResult.getWidth(),searchResult.getHeight()+10);
									searchResult.scrollRectToVisible(r);
									return;
								}
							}else{	//codon pair search
								if(theText.substring(0,3).equals(fieldString1)&&theText.substring(4,7).equals(fieldString2)){
									searchResult.setBorder(BorderFactory.createLineBorder(Color.RED,3));
									Rectangle r=new Rectangle(0,0,searchResult.getWidth(),searchResult.getHeight()+10);
									searchResult.scrollRectToVisible(r);
									return;
								}
							}
						}
					}
				}
			}
		});

		//okButton
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				try{
					for(int i=0;i<64;i++){
						for(int j=0;j<64;j++){
							moptimizer.codonPairTable[i][j]=Float.parseFloat(textField[i][j].getText());
						}
					}
					moptimizer.minimumCodonPair=Float.parseFloat(minimalValue.getText());
					moptimizer.hasCodonPairTable=true;
				}catch(Exception e){
					JOptionPane.showMessageDialog(codonPairDialog,"Insert number or decimal only.","Error",JOptionPane.PLAIN_MESSAGE);
					return;
				}
				setVisible(false);
			}
		});

		//cancelButton
		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				setVisible(false);
			}
		});
	}

	private void layoutComponents(){
		JPanel mainPanel=new JPanel();
		mainPanel.setBackground(bgColor);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));

		buttonPanel=new JPanel();
		buttonPanel.setOpaque(false);
		buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.X_AXIS));
		buttonPanel.add(loadCodonPairTableButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(10,0)));
		buttonPanel.add(showTableButton);
		buttonPanel.add(showTableLabel);
		buttonPanel.add(Box.createRigidArea(new Dimension(10,0)));

		//searchPanel
		searchPanel.setVisible(false);
		searchPanel.setOpaque(false);
		searchPanel.setLayout(new BoxLayout(searchPanel,BoxLayout.X_AXIS));
		searchPanel.setPreferredSize(new Dimension(150,20));
		searchPanel.setMinimumSize(new Dimension(150,20));
		searchPanel.setMaximumSize(new Dimension(150,20));
		searchPanel.add(searchField1);
		JLabel searchLabel=new JLabel(":");
		searchLabel.setForeground(Color.WHITE);
		searchPanel.add(searchLabel);
		searchPanel.add(searchField2);
		searchPanel.add(Box.createRigidArea(new Dimension(3,0)));
		searchPanel.add(searchButton);
		buttonPanel.add(searchPanel);
		buttonPanel.add(Box.createRigidArea(new Dimension(100,0)));
		buttonPanel.add(Box.createHorizontalGlue());

		minimalValueLabel=new JLabel("Minimal Value:");
		minimalValueLabel.setForeground(Color.WHITE);
		buttonPanel.add(minimalValueLabel);
		buttonPanel.add(minimalValue);
		buttonPanel.add(Box.createRigidArea(new Dimension(10,0)));
		buttonPanel.add(okButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(5,0)));
		buttonPanel.add(cancelButton);
		mainPanel.add(buttonPanel);
		mainPanel.add(Box.createRigidArea(new Dimension(0,5)));

		mainPanel.add(tableScroll);

		add(mainPanel);
	}

	private void setupProperties(){
		setTitle("Load Codon Pair Table");
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
		setVisible(false);
	}
}
