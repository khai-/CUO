package Tools.CAI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.filechooser.FileFilter;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import Support.CAICalculator;
import Support.CodonFrequencyTable;
import Support.CodonPairCalculator;
import Support.DirList;
import Support.WeightTable;
import Support.parser.GenBank;
import Support.parser.Sequence;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.itextpdf.text.pdf.draw.LineSeparator;

public class CAI{
	CAI cai=this;
	Window CAI;
	ArrayList<GenBank> library;
	ArrayList<GenBank> toolbox;
	ArrayList<GenBank> favourite;
	boolean independent;
	Color bgColor=new Color(70,70,70);
	Color filterColor=new Color(220,240,180);
	Color themeColor=new Color(76,117,35);
	ImageIcon wheel=new ImageIcon("Icons"+File.separator+"wheel.gif");
	ImageIcon wheel_animated=new ImageIcon("Icons"+File.separator+"wheel_animated.gif");

	JPanel filterPanel;
	JTextField filterText;
	JToggleButton filterButton;
	DefaultListModel listBeforeFilter;

	JPanel buttonsPanel;
	JButton homeButton;
	ImageIcon home_up=new ImageIcon("Icons"+File.separator+"home_up.gif");
	ImageIcon home_down=new ImageIcon("Icons"+File.separator+"home_down.gif");
	JButton uponelevelButton;
	ImageIcon uponelevel_up=new ImageIcon("Icons"+File.separator+"uponelevel_up.gif");
	ImageIcon uponelevel_down=new ImageIcon("Icons"+File.separator+"uponelevel_down.gif");
	JButton selectProteinButton;
	ImageIcon selectProtein_up=new ImageIcon("Icons"+File.separator+"selectProtein_up.gif");
	ImageIcon selectProtein_down=new ImageIcon("Icons"+File.separator+"selectProtein_down.gif");
	JLabel statusLabel;

	DirList viewer;
	JScrollPane viewerScroll;

	JButton selectInverseButton;

	ImageIcon add_up=new ImageIcon("Icons"+File.separator+"add_up.gif");
	ImageIcon add_down=new ImageIcon("Icons"+File.separator+"add_down.gif");

	JPanel transgenePanel;
	JButton transgeneAddButton;
	JLabel transgeneLabel;
	DirList transgeneList;
	JScrollPane transgeneScroll;
	DefaultListModel transgeneModel;

	JPanel weightPanel;
	JButton weightAddButton;
	JLabel weightLabel;
	JButton weightConfigButton;
	ImageIcon configuration_up=new ImageIcon("Icons"+File.separator+"configuration_up.gif");
	ImageIcon configuration_down=new ImageIcon("Icons"+File.separator+"configuration_down.gif");
	DirList weightList;
	JScrollPane weightScroll;
	DefaultListModel weightModel;
	ImageIcon fnaIcon=new ImageIcon("Icons"+File.separator+"fna.gif");
	ImageIcon faaIcon=new ImageIcon("Icons"+File.separator+"faa.gif");
	ImageIcon orgnIcon=new ImageIcon("Icons"+File.separator+"orgnIcon.gif");
	ImageIcon orgn_auto=new ImageIcon("Icons"+File.separator+"orgn_auto.gif");
	ImageIcon mfaIcon=new ImageIcon("Icons"+File.separator+"mfa.gif");
	ImageIcon mfa_auto=new ImageIcon("Icons"+File.separator+"mfa_auto.gif");
	ImageIcon geneIcon=new ImageIcon("Icons"+File.separator+"geneIcon.gif");

	JPanel analyzePanel;
	JButton importWeight;
	JButton importCancelButton;
	JButton insertButton;
	float[] importedWeight;	//to store imported weight table from .wt file
	JLabel importLabel;
	JButton analyzeButton;
	Thread analyzeThread;
	Image fnaIconPdf;
	Image faaIconPdf;
	Image mfaIconPdf;
	Image mfaAutoPdf;
	Image orgnIconPdf;
	Image geneIconPdf;
	Image orgnAutoPdf;
	Image proteinIconPdf;
	Image tRNAIconPdf;
	Image rRNAIconPdf;
	Image miRNAIconPdf;

	public CAI(JFrame ownerFrame,ArrayList<GenBank> library,ArrayList<GenBank> toolbox,ArrayList<GenBank> favourite){
		this.library=library;
		this.toolbox=toolbox;
		this.favourite=favourite;
		CAI=new JDialog(ownerFrame,false);
		independent=false;
		initiate();
	}

	public CAI(){
		CAI=new JFrame();
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
		//filterPanel
		filterPanel=new JPanel();
		filterText=new JTextField();
		filterText.setMaximumSize(new Dimension(10000,25));
		filterButton=new JToggleButton("Filter");

		//buttonsPanel
		buttonsPanel=new JPanel();
		homeButton=new JButton(home_up);
		homeButton.setBorderPainted(false);
		homeButton.setContentAreaFilled(false);
		homeButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		uponelevelButton=new JButton(uponelevel_up);
		uponelevelButton.setBorderPainted(false);
		uponelevelButton.setContentAreaFilled(false);
		uponelevelButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		selectProteinButton=new JButton(selectProtein_up);
		selectProteinButton.setForeground(Color.WHITE);
		selectProteinButton.setBorderPainted(false);
		selectProteinButton.setContentAreaFilled(false);
		selectProteinButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		statusLabel=new JLabel("");
		statusLabel.setForeground(Color.WHITE);

		//viewer
		File homedir=new File(".");
		viewer=new DirList(homedir,library,toolbox,favourite);
		viewer.setFixedCellWidth(250);
		viewerScroll=new JScrollPane(viewer,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		viewerScroll.setPreferredSize(new Dimension(250,(int)viewerScroll.getPreferredSize().getHeight()));

		//selectInverseButton
		selectInverseButton=new JButton("Select Inverse");

		//transgeneList
		transgenePanel=new JPanel();
		transgeneAddButton=new JButton(add_up);
		transgeneAddButton.setBorderPainted(false);
		transgeneAddButton.setContentAreaFilled(false);
		transgeneAddButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		transgeneLabel=new JLabel("Tested sequence");
		transgeneLabel.setForeground(Color.WHITE);
		transgeneList=new DirList(null,null,null,null);
		transgeneScroll=new JScrollPane(transgeneList,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		transgeneList.setFixedCellWidth(250);
		transgeneScroll.setPreferredSize(new Dimension(250,30));
		transgeneModel=new DefaultListModel();
		transgeneList.setModel(transgeneModel);

		//weightList
		weightPanel=new JPanel();
		weightAddButton=new JButton(add_up);
		weightAddButton.setBorderPainted(false);
		weightAddButton.setContentAreaFilled(false);
		weightAddButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		weightLabel=new JLabel("Reference set");
		weightLabel.setForeground(Color.WHITE);
		weightConfigButton=new JButton(configuration_up);
		weightConfigButton.setBorderPainted(false);
		weightConfigButton.setContentAreaFilled(false);
		weightConfigButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		weightList=new DirList(null,null,null,null);
		weightScroll=new JScrollPane(weightList,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		weightList.setFixedCellWidth(250);
		weightScroll.setPreferredSize(new Dimension(250,(int)weightScroll.getPreferredSize().getHeight()));
		weightModel=new DefaultListModel();
		weightList.setModel(weightModel);

		//analyzePanel
		analyzePanel=new JPanel();
		importWeight=new JButton("Import");
		importCancelButton=new JButton("Cancel import");
		insertButton=new JButton("Insert");
		importLabel=new JLabel();
		analyzeButton=new JButton("Analyze");
		try{
			fnaIconPdf=Image.getInstance("Icons"+File.separator+"fna.gif");
			fnaIconPdf.scaleAbsolute(20f,20f);
			fnaIconPdf.setAlignment(Image.TEXTWRAP|Image.LEFT);
			faaIconPdf=Image.getInstance("Icons"+File.separator+"faa.gif");
			faaIconPdf.scaleAbsolute(20f,20f);
			faaIconPdf.setAlignment(Image.TEXTWRAP|Image.LEFT);
			mfaIconPdf=Image.getInstance("Icons"+File.separator+"mfa.gif");
			mfaIconPdf.scaleAbsolute(20f,20f);
			mfaIconPdf.setAlignment(Image.TEXTWRAP|Image.LEFT);
			mfaAutoPdf=Image.getInstance("Icons"+File.separator+"mfa_auto.gif");
			mfaAutoPdf.scaleAbsolute(20f,20f);
			mfaAutoPdf.setAlignment(Image.TEXTWRAP|Image.LEFT);
			orgnIconPdf=Image.getInstance("Icons"+File.separator+"orgnIcon.gif");
			orgnIconPdf.scaleAbsolute(20f,20f);
			orgnIconPdf.setAlignment(Image.TEXTWRAP|Image.LEFT);
			geneIconPdf=Image.getInstance("Icons"+File.separator+"geneIcon.gif");
			geneIconPdf.scaleAbsolute(20f,20f);
			geneIconPdf.setAlignment(Image.TEXTWRAP|Image.LEFT);
			orgnAutoPdf=Image.getInstance("Icons"+File.separator+"orgn_auto.gif");
			orgnAutoPdf.scaleAbsolute(20f,20f);
			orgnAutoPdf.setAlignment(Image.TEXTWRAP|Image.LEFT);
			proteinIconPdf=Image.getInstance("Icons"+File.separator+"proteinIcon.gif");
			proteinIconPdf.scaleAbsolute(20f,20f);
			proteinIconPdf.setAlignment(Image.TEXTWRAP|Image.LEFT);
			tRNAIconPdf=Image.getInstance("Icons"+File.separator+"tRNAIcon.gif");
			tRNAIconPdf.scaleAbsolute(20f,20f);
			tRNAIconPdf.setAlignment(Image.TEXTWRAP|Image.LEFT);
			rRNAIconPdf=Image.getInstance("Icons"+File.separator+"rRNAIcon.gif");
			rRNAIconPdf.scaleAbsolute(20f,20f);
			rRNAIconPdf.setAlignment(Image.TEXTWRAP|Image.LEFT);
			miRNAIconPdf=Image.getInstance("Icons"+File.separator+"miRNAIcon.gif");
			miRNAIconPdf.scaleAbsolute(20f,20f);
			miRNAIconPdf.setAlignment(Image.TEXTWRAP|Image.LEFT);
		}catch(Exception e){
			JOptionPane.showMessageDialog(CAI,"Cannot find icon.","ERROR",JOptionPane.ERROR_MESSAGE);
		}
	}

	private void assignFunctions(){
		//filterText and filterButton
		filterText.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				filterButton.setSelected(true);
				listBeforeFilter=(DefaultListModel)viewer.getModel();
				DefaultListModel newList=new DefaultListModel();
				for(int i=0;i<listBeforeFilter.size();i++){
					JLabel theLabel=((JLabel)((JPanel)listBeforeFilter.getElementAt(i)).getComponent(3));
					if(theLabel.getText().matches("(?i:.*"+filterText.getText()+".*)")==true){//case insensitive
						newList.addElement(listBeforeFilter.getElementAt(i));
					}
				}
				viewer.setModel(newList);
				statusLabel.setText(newList.size()+" filtered");
			}
		});
		filterText.addKeyListener(new KeyAdapter(){
			public void keyReleased(KeyEvent ke){
				if(ke.getKeyCode()==KeyEvent.VK_ESCAPE){
					filterText.setText("");
					filterButton.setSelected(false);
				}
			}
		});
		filterButton.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent ie){
				if(ie.getStateChange()==ItemEvent.DESELECTED){
					filterText.setBackground(Color.WHITE);
					if(listBeforeFilter!=null){
						viewer.setModel(listBeforeFilter);
					}
					listBeforeFilter=null;
				}else if(ie.getStateChange()==ItemEvent.SELECTED){
					filterText.setBackground(filterColor);
				}
			}
		});

		//homeButton
		homeButton.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				homeButton.setIcon(home_down);
			}
			public void mouseReleased(MouseEvent me){
				listBeforeFilter=null;
				filterButton.setSelected(false);

				viewer.listFiles(viewer.homeDir);
				statusLabel.setText(((DefaultListModel)viewer.getModel()).size()+" found");
				homeButton.setIcon(home_up);
			}
		});

		//uponelevelButton
		uponelevelButton.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				uponelevelButton.setIcon(uponelevel_down);
			}
			public void mouseReleased(MouseEvent me){
				listBeforeFilter=null;
				filterButton.setSelected(false);

				if(!viewer.currentDir.getAbsolutePath().equals(viewer.homeDir.getAbsolutePath())){
					viewer.currentDir=viewer.currentDir.getParentFile();
					viewer.listFiles(viewer.currentDir);
					statusLabel.setText(((DefaultListModel)viewer.getModel()).size()+" found");
				}else{	//as if homeButton
					viewer.listFiles(viewer.homeDir);
					statusLabel.setText(((DefaultListModel)viewer.getModel()).size()+" found");
				}
				uponelevelButton.setIcon(uponelevel_up);
			}
		});

		//selectProteinButton
		selectProteinButton.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				selectProteinButton.setIcon(selectProtein_down);
			}
			public void mouseReleased(MouseEvent me){
				int size=viewer.getModel().getSize();
				if(size==0){
					selectProteinButton.setIcon(selectProtein_up);
					return;
				}
				viewer.clearSelection();
				for(int i=0;i<size;i++){
					if(((JLabel)((JPanel)viewer.getModel().getElementAt(i)).getComponent(0)).getIcon().toString().equals(((Icon)viewer.proteinIcon).toString())){
						viewer.addSelectionInterval(i,i);
					}
				}
				selectProteinButton.setIcon(selectProtein_up);
			}
		});

		//viewer
		viewer.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent me){
				if(me.getClickCount()==2){
					if(((DefaultListModel)viewer.getModel()).size()==0)return; //nothing in the list

					CAI.setCursor(new Cursor(Cursor.WAIT_CURSOR));	//set mouse pointer to busy
					listBeforeFilter=null;
					filterButton.setSelected(false);

					int index=viewer.locationToIndex(me.getPoint());
					String fileName=((JLabel)((JPanel)viewer.getModel().getElementAt(index)).getComponent(1)).getText();
					File newDir=new File(fileName);
					String type=((JLabel)((JPanel)viewer.getModel().getElementAt(index)).getComponent(2)).getText();
					if(type.equals("orgn")){
						viewer.listGenes(newDir);
						if(((DefaultListModel)viewer.getModel()).size()>0){
							statusLabel.setText(((DefaultListModel)viewer.getModel()).size()+" found");
						}else{	//no info yet for the orgn, need update
							statusLabel.setText("No info yet");
						}
					}else if(type.equals("gene")||type.equals("fasta")){
						//do nothing
					}else{
						viewer.listFiles(newDir);
						statusLabel.setText(((DefaultListModel)viewer.getModel()).size()+" found");
					}
					CAI.setCursor(null);	//set cursor back to normal
				}
			}
		});


		//selectInverseButton
		selectInverseButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				ArrayList<Integer> unselected=new ArrayList<Integer>();
				for(int i=0;i<((DefaultListModel)viewer.getModel()).size();i++){
					if(!viewer.isSelectedIndex(i))unselected.add(i);
				}
				Integer[] unselectedInteger=unselected.toArray(new Integer[unselected.size()]);
				int[] unselectedint=new int[unselectedInteger.length];
				for(int i=0;i<unselectedInteger.length;i++){
					unselectedint[i]=(int)unselectedInteger[i];
				}
				viewer.setSelectedIndices(unselectedint);
			}
		});

		//transgeneAddButton
		transgeneAddButton.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				transgeneAddButton.setIcon(add_down);
			}
			public void mouseReleased(MouseEvent me){
				int[] viewerIndices=viewer.getSelectedIndices();
				DefaultListModel viewerModel=(DefaultListModel)viewer.getModel();
				for(int i=0;i<viewerIndices.length;i++){
					JPanel viewerPanel=(JPanel)viewerModel.elementAt(viewerIndices[i]);
					if(((JLabel)viewerPanel.getComponent(2)).getText().equals("orgn")||((JLabel)viewerPanel.getComponent(2)).getText().equals("gene")||(((JLabel)viewerPanel.getComponent(2)).getText().equals("fasta")&&(!((JLabel)viewerPanel.getComponent(0)).getIcon().toString().equals(faaIcon.toString())))){
						//clone the panel
						JPanel basketPanel=new JPanel(new FlowLayout(FlowLayout.LEFT));
						JLabel basketIcon=new JLabel(((JLabel)viewerPanel.getComponent(0)).getIcon());
						JLabel basketLabel1;
						basketLabel1=new JLabel(((JLabel)viewerPanel.getComponent(1)).getText());
						basketLabel1.setVisible(false);
						JLabel basketLabel2=new JLabel(((JLabel)viewerPanel.getComponent(2)).getText());
						basketLabel2.setVisible(false);
						JLabel basketLabel3=new JLabel(((JLabel)viewerPanel.getComponent(3)).getText());
						basketPanel.add(basketIcon);
						basketPanel.add(basketLabel1);
						basketPanel.add(basketLabel2);
						basketPanel.add(basketLabel3);
					
						transgeneModel.addElement(basketPanel);
					}
				}

				transgeneAddButton.setIcon(add_up);
			}
		});

		//weightAddButton
		weightAddButton.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				weightAddButton.setIcon(add_down);
			}
			public void mouseReleased(MouseEvent me){
				if(importedWeight!=null){
					weightAddButton.setIcon(add_up);
					return;
				}
				int[] viewerIndices=viewer.getSelectedIndices();
				DefaultListModel viewerModel=(DefaultListModel)viewer.getModel();
				for(int i=0;i<viewerIndices.length;i++){
					JPanel viewerPanel=(JPanel)viewerModel.elementAt(viewerIndices[i]);
					if(((JLabel)viewerPanel.getComponent(2)).getText().equals("orgn")||((JLabel)viewerPanel.getComponent(2)).getText().equals("gene")||(((JLabel)viewerPanel.getComponent(2)).getText().equals("fasta")&&(!((JLabel)viewerPanel.getComponent(0)).getIcon().toString().equals(faaIcon.toString())))){
						//clone the panel
						JPanel basketPanel=new JPanel(new FlowLayout(FlowLayout.LEFT));
						JLabel basketIcon;
						if(((JLabel)viewerPanel.getComponent(2)).getText().equals("orgn")){
							if(((JLabel)viewerPanel.getComponent(0)).getIcon().toString().equals(orgnIcon.toString())){
								basketIcon=new JLabel(orgn_auto);
							}else{
								basketIcon=new JLabel(mfaIcon);
							}
						}else{
							basketIcon=new JLabel(((JLabel)viewerPanel.getComponent(0)).getIcon());
						}
						JLabel basketLabel1;
						basketLabel1=new JLabel(((JLabel)viewerPanel.getComponent(1)).getText());
						basketLabel1.setVisible(false);
						JLabel basketLabel2=new JLabel(((JLabel)viewerPanel.getComponent(2)).getText());
						basketLabel2.setVisible(false);
						JLabel basketLabel3=new JLabel(((JLabel)viewerPanel.getComponent(3)).getText());
						JLabel basketLabel4=new JLabel();	//extra invisible label to store analysis parameters
						basketLabel4.setVisible(false);
						if(basketIcon.getIcon()==(Icon)orgn_auto||basketIcon.getIcon()==(Icon)mfaIcon){	//autopick parameters
							basketLabel4.setText("1,50");	//default: threshold=1% shrinkage=50%
						}
						basketPanel.add(basketIcon);
						basketPanel.add(basketLabel1);
						basketPanel.add(basketLabel2);
						basketPanel.add(basketLabel3);
						basketPanel.add(basketLabel4);
					
						weightModel.addElement(basketPanel);
					}
				}

				weightAddButton.setIcon(add_up);
			}
		});

		//transgeneList
		KeyListener[] kl=transgeneList.getKeyListeners();
		for(int i=0;i<kl.length;i++){
			transgeneList.removeKeyListener(kl[i]);//cancel the original listener
		}
		transgeneList.addKeyListener(new KeyAdapter(){
			public void keyReleased(KeyEvent ke){
				int[] selected=transgeneList.getSelectedIndices();
				if(ke.getKeyCode()==KeyEvent.VK_DELETE&&selected.length>0){
					if(selected.length>0){
						transgeneList.setSelectedIndex(selected[selected.length-1]+1);
					}
					DefaultListModel model=(DefaultListModel)transgeneList.getModel();
					for(int i=0;i<selected.length;i++){
						if(i==0){
							model.remove(selected[i]);
						}else{
							model.remove(selected[i]-i);	//to accurately delete item int the rearranged defaultlistmodel after its remove method
						}
					}
				}
			}
		});

		//weightConfigButton
		weightConfigButton.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				weightConfigButton.setIcon(configuration_down);
			}
			public void mouseReleased(MouseEvent me){
				try{
					JPanel thePanel=(JPanel)weightList.getSelectedValue();
					JLabel theLabel=(JLabel)thePanel.getComponent(0);
					if(theLabel.getIcon()==(Icon)orgn_auto||theLabel.getIcon()==(Icon)mfa_auto){	//autopick genome configuration
						final JDialog configDialog=new JDialog(CAI);
						CAI.setEnabled(false);
						final JLabel parametersLabel=(JLabel)thePanel.getComponent(4);
						String parameters=parametersLabel.getText();
						String thresholdParameter=parameters.split(",")[0];
						String shrinkageParameter=parameters.split(",")[1];
						//components
						JLabel percentageSymbol1=new JLabel("%");
						percentageSymbol1.setForeground(Color.WHITE);
						JLabel thresholdLabel=new JLabel("Threshold: ");
						thresholdLabel.setForeground(Color.WHITE);
						final JTextField thresholdField=new JTextField(thresholdParameter);
						thresholdField.setPreferredSize(new Dimension(45,20));
						thresholdField.setMaximumSize(new Dimension(45,20));
						JLabel percentageSymbol2=new JLabel("%");
						percentageSymbol2.setForeground(Color.WHITE);
						JLabel shrinkageLabel=new JLabel("Shrinkage per iteration: ");
						shrinkageLabel.setForeground(Color.WHITE);
						final JTextField shrinkageField=new JTextField(shrinkageParameter);
						shrinkageField.setPreferredSize(new Dimension(45,20));
						shrinkageField.setMaximumSize(new Dimension(45,20));
						final JButton okButton=new JButton("OK");
						final JButton cancelButton=new JButton("Cancel");
						//function
						okButton.addActionListener(new ActionListener(){
							public void actionPerformed(ActionEvent ae){
								try{
									int thres=Integer.parseInt(thresholdField.getText());
									int shrink=Integer.parseInt(shrinkageField.getText());
									if(thres>=100||shrink>=100){
										Toolkit.getDefaultToolkit().beep();
										return;
									}
								}catch(Exception e){
									Toolkit.getDefaultToolkit().beep();
									return;
								}
								parametersLabel.setText(thresholdField.getText()+","+shrinkageField.getText());
								configDialog.dispose();
								CAI.setEnabled(true);
								CAI.getOwner().requestFocus();
							}
						});
						cancelButton.addActionListener(new ActionListener(){
							public void actionPerformed(ActionEvent ae){
								configDialog.dispose();
								CAI.setEnabled(true);
								CAI.getOwner().requestFocus();
							}
						});
						configDialog.addWindowListener(new WindowAdapter(){
							public void windowClosing(WindowEvent we){
								CAI.setEnabled(true);
								CAI.getOwner().requestFocus();
							}
						});
						thresholdField.addKeyListener(new KeyAdapter(){
							public void keyReleased(KeyEvent ke){
								if(ke.getKeyCode()==KeyEvent.VK_ENTER){	//press enter=click ok
									okButton.doClick();
								}else if(ke.getKeyCode()==KeyEvent.VK_ESCAPE){	//press esc=click cancel
									cancelButton.doClick();
								}
							}
						});
						shrinkageField.addKeyListener(new KeyAdapter(){
							public void keyReleased(KeyEvent ke){
								if(ke.getKeyCode()==KeyEvent.VK_ENTER){	//press enter=click ok
									okButton.doClick();
								}else if(ke.getKeyCode()==KeyEvent.VK_ESCAPE){	//press esc=click cancel
									cancelButton.doClick();
								}
							}
						});
						//layout
						JPanel configPanel=new JPanel();
						configPanel.setBackground(bgColor);
						configPanel.setLayout(new BoxLayout(configPanel,BoxLayout.Y_AXIS));
						configPanel.setBorder(BorderFactory.createEmptyBorder(15,10,15,10));
						JPanel thresholdPanel=new JPanel();
						thresholdPanel.setAlignmentX(0.5f);
						thresholdPanel.setOpaque(false);
						thresholdPanel.setLayout(new BoxLayout(thresholdPanel,BoxLayout.X_AXIS));
						thresholdPanel.add(thresholdLabel);
						thresholdPanel.add(thresholdField);
						thresholdPanel.add(percentageSymbol1);
						configPanel.add(thresholdPanel);
						configPanel.add(Box.createRigidArea(new Dimension(0,10)));
						JPanel shrinkagePanel=new JPanel();
						shrinkagePanel.setAlignmentX(0.5f);
						shrinkagePanel.setOpaque(false);
						shrinkagePanel.setLayout(new BoxLayout(shrinkagePanel,BoxLayout.X_AXIS));
						shrinkagePanel.add(shrinkageLabel);
						shrinkagePanel.add(shrinkageField);
						shrinkagePanel.add(percentageSymbol2);
						configPanel.add(shrinkagePanel);
						configPanel.add(Box.createVerticalGlue());
						JPanel buttonPanel=new JPanel();
						buttonPanel.setOpaque(false);
						buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.X_AXIS));
						buttonPanel.add(okButton);
						buttonPanel.add(Box.createRigidArea(new Dimension(20,0)));
						buttonPanel.add(cancelButton);
						configPanel.add(buttonPanel);
						configDialog.add(configPanel);
						//setup properties
						int width=270;
						int height=120;
						configDialog.setLocationRelativeTo(CAI);
						configDialog.setLocation(configDialog.getLocation().x-width/2,configDialog.getLocation().y-height/2);
						configDialog.setSize(width,height);
						configDialog.setUndecorated(true);
						configDialog.setResizable(false);
						configDialog.setVisible(true);
					}
				}catch(NullPointerException npe){}	//if no item is selected in the list

				weightConfigButton.setIcon(configuration_up);
			}
		});

		//weightList
		KeyListener[] kl2=weightList.getKeyListeners();
		for(int i=0;i<kl2.length;i++){
			weightList.removeKeyListener(kl2[i]);//remove original key listener
		}
		weightList.addKeyListener(new KeyAdapter(){
			public void keyReleased(KeyEvent ke){
				int[] selected=weightList.getSelectedIndices();
				if(ke.getKeyCode()==KeyEvent.VK_DELETE&&selected.length>0){
					if(importedWeight!=null)return;
					if(selected.length>0){
						weightList.setSelectedIndex(selected[selected.length-1]+1);
					}
					DefaultListModel model=(DefaultListModel)weightList.getModel();
					for(int i=0;i<selected.length;i++){
						if(i==0){
							model.remove(selected[i]);
						}else{
							model.remove(selected[i]-i);
						}
					}
				}
			}
		});
		weightList.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent me){
				if(me.getClickCount()==2){
					if(importedWeight!=null)return;
					int index=weightList.locationToIndex(me.getPoint());
					String folderType=((JLabel)((JPanel)weightList.getModel().getElementAt(index)).getComponent(2)).getText();
					if(folderType.equals("orgn")){
						JLabel iconLabel=((JLabel)((JPanel)weightList.getModel().getElementAt(index)).getComponent(0));
						//genBank .xml
						if(iconLabel.getIcon()==orgnIcon){
							iconLabel.setIcon(orgn_auto);
						}else if(iconLabel.getIcon()==orgn_auto){
							iconLabel.setIcon(orgnIcon);
						}else if(iconLabel.getIcon()==mfaIcon){	//multifasta .mfa
							iconLabel.setIcon(mfa_auto);
						}else if(iconLabel.getIcon()==mfa_auto){
							iconLabel.setIcon(mfaIcon);
						}

						//cheaty way to referesh a model
						DefaultListModel currentList=(DefaultListModel)weightList.getModel();
						weightList.setModel(new DefaultListModel());
						weightList.setModel(currentList);
						weightList.setSelectedIndex(index);
					}
				}
			}
		});

		//importWeight button
		importWeight.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				if(importedWeight!=null){
					return;
				}
				CAI.setEnabled(false);
				JFileChooser jfc=new JFileChooser("."+File.separator+"Construction");
				jfc.setAcceptAllFileFilterUsed(false);
				jfc.setFileFilter(new FileFilter(){
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
				if(jfc.showOpenDialog(CAI)==JFileChooser.APPROVE_OPTION){
					//read *.wt to importedWeight[64]
					File weightFile=jfc.getSelectedFile();
					try{
						BufferedReader br=new BufferedReader(new FileReader(weightFile));
						importedWeight=new float[64];
						for(int i=0;i<64;i++){
							importedWeight[i]=Float.parseFloat(br.readLine());
						}
					
						br.close();
					}catch(Exception e){
						JOptionPane.showMessageDialog(CAI,e.toString(),"Error",JOptionPane.ERROR_MESSAGE);
						importedWeight=null;
						CAI.setEnabled(true);
						return;
					}

					//generate a JPanel
					final JPanel thePanel=new JPanel();
					thePanel.setLayout(new BoxLayout(thePanel,BoxLayout.Y_AXIS));
					JLabel theLabel=new JLabel("<html><u>Imported weight table:</u></html>");
					JLabel theLabel2=new JLabel("<html><div style=width:"+(weightList.listWidth+20)+"px><font color=BLUE>"+weightFile.getName()+"</font></div></html>");	//label with info about the imported weight table
					thePanel.add(theLabel);
					thePanel.add(theLabel2);
					//replace the listmodel in weightList with imported weight table list
					DefaultListModel importModel=new DefaultListModel();
					importModel.addElement(thePanel);
					String[] codons={"UUU","UUC","UUA","UUG","CUU","CUC","CUA","CUG","AUU","AUC","AUA","AUG","GUU","GUC","GUA","GUG","UCU","UCC","UCA","UCG", "CCU","CCC","CCA","CCG","ACU","ACC","ACA","ACG","GCU","GCC","GCA","GCG","UAU","UAC","UAA","UAG","CAU","CAC","CAA","CAG", "AAU","AAC","AAA","AAG","GAU","GAC","GAA","GAG","UGU","UGC","UGA","UGG","CGU","CGC","CGA","CGG","AGU","AGC","AGA","AGG", "GGU","GGC","GGA","GGG"};
					for(int i=0;i<64;i++){	//list out the values of all weights
						JPanel weightValuePanel=new JPanel();
						weightValuePanel.setLayout(new BoxLayout(weightValuePanel,BoxLayout.X_AXIS));
						JLabel weightValueLabel=new JLabel("<html><font size=2>"+codons[i]+": "+importedWeight[i]+"<br></font></html>");
						weightValuePanel.add(weightValueLabel);
						weightValuePanel.add(Box.createHorizontalGlue());
						importModel.addElement(weightValuePanel);
					}
					weightList.needToRelist=false;	//disable the relist function
					weightList.setModel(importModel);

					//replace importWeight with importCancelButton
					analyzePanel.remove(1);
					analyzePanel.add(importCancelButton,1);
					analyzePanel.validate();
					analyzePanel.repaint();

					CAI.setEnabled(true);
				}else{
					CAI.setEnabled(true);
					return;
				}
			}
		});

		//importCancelButton
		importCancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				//recover the weightList model to normal
				weightList.needToRelist=true;
				weightList.setModel(new DefaultListModel());
				weightList.setModel(weightModel);
				importedWeight=null;
				//recover the importWeight button
				analyzePanel.remove(1);
				analyzePanel.add(importWeight,1);
				analyzePanel.validate();
				analyzePanel.repaint();
			}
		});

		//insertButton	(can be number,frequency or raw sequence)
		insertButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				new CAIInsert(cai);
			}
		});

		//analyzeButton
		final Font font12=new Font(FontFamily.HELVETICA,12,Font.UNDERLINE|Font.BOLD);
		final Font font10=new Font(FontFamily.HELVETICA,10);	//defaultly used for this analysis
		final Font font8=new Font(FontFamily.HELVETICA,8);
		analyzeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				//immediately freeze main CAI window
				CAI.setEnabled(false);
				//make sure only one analyzeThread is running
				if(analyzeThread!=null){
					return;
				}
				//make sure weightList is not empty
				if(weightList.getModel().getSize()==0){
					CAI.setEnabled(true);
					return;	//break out of action
				}
				//make sure the selected files exists
				boolean existenceError=false;
				String errorString="<html><u>ERROR</u><ul>";
				final int transgeneSize=transgeneList.getModel().getSize();
				for(int i=0;i<transgeneSize;i++){
					String filePath=((JLabel)((JPanel)((DefaultListModel)transgeneList.getModel()).elementAt(i)).getComponent(1)).getText();
					if(!new File(filePath).exists()){
						existenceError=true;
						errorString+="<br><li><font color=blue>"+filePath+"</font> doesn't exists.</li>";
					}
				}
				final int weightSize=weightList.getModel().getSize();
				if(importedWeight==null){
					errorString+="<hr>";
					for(int i=0;i<weightSize;i++){
						String filePath=((JLabel)((JPanel)((DefaultListModel)weightList.getModel()).elementAt(i)).getComponent(1)).getText();
						if(!new File(filePath).exists()){
							existenceError=true;
							errorString+="<br><li><font color=blue>"+filePath+"</font> doesn't exists.</li>";
						}
					}
					if(existenceError){
						JOptionPane.showMessageDialog(CAI,errorString,"ERROR",JOptionPane.ERROR_MESSAGE);
						CAI.setEnabled(true);
						return;
					}
				}

				//decide workingDirectory
				File workingDirectory=null;
				final String fileName;
				JFileChooser jfc=new JFileChooser("."+File.separator+"Construction");
				if(jfc.showSaveDialog(CAI)==JFileChooser.APPROVE_OPTION){
					workingDirectory=jfc.getCurrentDirectory();
					fileName=jfc.getSelectedFile().getPath();
				}else{
					CAI.setEnabled(true);
					return;
				}

				analyzeThread=new Thread(new Runnable(){	//toss out of AWT Thread
					public void run(){
						final Document pdf=new Document();
						//show loading dialog
						final JDialog progressDialog=new JDialog(CAI);
						JPanel progressPanel=new JPanel();
						progressPanel.setLayout(new BoxLayout(progressPanel,BoxLayout.Y_AXIS));
						progressPanel.setOpaque(false);
						progressPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
						JLabel progressStatus=new JLabel(wheel_animated);
						progressStatus.setText("Initializing...");
						progressStatus.setForeground(Color.WHITE);
						progressStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
						progressPanel.add(progressStatus);
						progressPanel.add(Box.createRigidArea(new Dimension(0,5)));
						JPanel barAndButton=new JPanel();
						barAndButton.setLayout(new BoxLayout(barAndButton,BoxLayout.X_AXIS));
						barAndButton.setOpaque(false);
						JProgressBar progressBar=new JProgressBar(0,100);
						progressBar.setStringPainted(true);
						barAndButton.add(progressBar);
						barAndButton.add(Box.createRigidArea(new Dimension(5,0)));
						final JButton abortButton=new JButton("Abort");
						abortButton.addActionListener(new ActionListener(){
							public void actionPerformed(ActionEvent ae){
								analyzeThread.interrupt();
								File pdfFail=new File(fileName+".pdf");
								if(pdfFail.exists()){
									pdfFail.delete();//delete the aborted pdf file
								}
								progressDialog.dispose();
								analyzeThread=null;
								CAI.setEnabled(true);
							}
						});
						barAndButton.add(abortButton);
						progressPanel.add(barAndButton);
						progressDialog.add(progressPanel);
						progressDialog.getContentPane().setBackground(bgColor);
						progressDialog.setResizable(false);
						progressDialog.setUndecorated(true);
						progressDialog.setLocationRelativeTo(CAI);
						progressDialog.setLocation(progressDialog.getLocation().x-300/2,progressDialog.getLocation().y-80/2);
						progressDialog.setSize(300,80);
						progressDialog.setVisible(true);
						progressDialog.addWindowListener(new WindowAdapter(){
							public void windowClosing(WindowEvent we){
								abortButton.doClick();
							}
						});

						try{
							//create pdf file
							pdf.setMargins(50f,50f,90f,80f);
							PdfWriter writer=PdfWriter.getInstance(pdf,new FileOutputStream(fileName+".pdf"));
							pdf.open();
							PdfContentByte canvas=writer.getDirectContentUnder();

							//define useful pdf objects
							LineSeparator line=new LineSeparator(2f,100f,BaseColor.BLACK,LineSeparator.ALIGN_BASELINE,-10f);//horizontal line
							
							//writing to pdf
							//stamp a logo on every page
							writer.setPageEvent(new PdfPageEventHelper(){
								public void onStartPage(PdfWriter writer,Document document){
									try{
										stampLogo(writer.getDirectContent());
									}catch(Exception e){
										e.printStackTrace();
										abortButton.doClick(); //no logo, no way!
									}
								}
							});
							stampLogo(canvas);

							//title
							String tempo=fileName.split("\\"+File.separator)[fileName.split("\\"+File.separator).length-1];	//Unix
							String title=tempo.split("\\\\")[tempo.split("\\\\").length-1];	//Windows
							Paragraph titleParagraph=new Paragraph(26,title,new Font(FontFamily.TIMES_ROMAN,21));
							pdf.add(titleParagraph);
							pdf.add(line);

							//date
							Calendar cal=Calendar.getInstance();
							SimpleDateFormat sdf=new SimpleDateFormat("dd.MM.yyyy");
							String date=sdf.format(cal.getTime());
							Paragraph dateParagraph=new Paragraph(25,"CAI Analyzer:  "+date);
							dateParagraph.setAlignment(Paragraph.ALIGN_RIGHT);
							pdf.add(dateParagraph);

							//list target sequences
							if(Thread.currentThread().isInterrupted())return;
							if(transgeneSize>0){	//if there is target sequence
								pdf.add(Chunk.NEWLINE);
								pdf.add(new Paragraph("Target sequences:",font12));
								for(int i=0;i<transgeneSize;i++){
									Paragraph target=new Paragraph(12,"",font10);
									JPanel thePanel=(JPanel)((DefaultListModel)transgeneList.getModel()).elementAt(i);
									Icon theIcon=((JLabel)thePanel.getComponent(0)).getIcon();
									if(theIcon.toString().equals(((Icon)viewer.orgnIcon).toString())){
										target.add(orgnIconPdf);
									}else if(theIcon.toString().equals(((Icon)viewer.mfaIcon).toString())){
										target.add(mfaIconPdf);
									}else if(theIcon.toString().equals(((Icon)viewer.fnaIcon).toString())){
										target.add(fnaIconPdf);
									}else if(theIcon.toString().equals(((Icon)viewer.proteinIcon).toString())){
										target.add(proteinIconPdf);
									}else if(theIcon.toString().equals(((Icon)viewer.tRNAIcon).toString())){
										target.add(tRNAIconPdf);
									}else if(theIcon.toString().equals(((Icon)viewer.rRNAIcon).toString())){
										target.add(rRNAIconPdf);
									}else if(theIcon.toString().equals(((Icon)viewer.miRNAIcon).toString())){
										target.add(miRNAIconPdf);
									}else if(theIcon.toString().equals(((Icon)viewer.geneIcon).toString())){
										target.add(geneIconPdf);
									}
									String[] targetInfoArray=((JLabel)thePanel.getComponent(3)).getText().split("\\<.*?>");	//parse the html text
									String targetInfo="";
									for(int j=0;j<targetInfoArray.length;j++){
										if(!targetInfoArray[j].equals("")){
											targetInfo+=targetInfoArray[j]+"\n";
										}
									}
									target.add("\n"+targetInfo.replaceAll("&nbsp;"," ")+"\n");
									pdf.add(target);
								}
							}

							WeightTable weightTable;
							CodonPairCalculator cpc=new CodonPairCalculator();	//calculator for codon pair(store and calculate);
							if(Thread.currentThread().isInterrupted())return;
							if(importedWeight==null){//jump to presenting weight table if weight table is imported
								//list reference set
								pdf.add(Chunk.NEWLINE);
								pdf.add(new Paragraph("Reference set:",font12));
								for(int i=0;i<weightSize;i++){
									Paragraph reference=new Paragraph(12,"",font10);
									JPanel thePanel=(JPanel)((DefaultListModel)weightList.getModel()).elementAt(i);
									Icon theIcon=((JLabel)thePanel.getComponent(0)).getIcon();
									if(theIcon.toString().equals(((Icon)viewer.orgnIcon).toString())){
										reference.add(orgnIconPdf);
									}else if(theIcon.toString().equals(((Icon)viewer.mfaIcon).toString())){
										reference.add(mfaIconPdf);
									}else if(theIcon.toString().equals(((Icon)mfa_auto).toString())){
										reference.add(mfaAutoPdf);
									}else if(theIcon.toString().equals(((Icon)viewer.fnaIcon).toString())){
										reference.add(fnaIconPdf);
									}else if(theIcon.toString().equals(((Icon)orgn_auto).toString())){
										reference.add(orgnAutoPdf);
									}else if(theIcon.toString().equals(((Icon)viewer.proteinIcon).toString())){
										reference.add(proteinIconPdf);
									}else if(theIcon.toString().equals(((Icon)viewer.tRNAIcon).toString())){
										reference.add(tRNAIconPdf);
									}else if(theIcon.toString().equals(((Icon)viewer.rRNAIcon).toString())){
										reference.add(rRNAIconPdf);
									}else if(theIcon.toString().equals(((Icon)viewer.miRNAIcon).toString())){
										reference.add(miRNAIconPdf);
									}else if(theIcon.toString().equals(((Icon)viewer.geneIcon).toString())){
										reference.add(geneIconPdf);
									}
									String[] referenceInfoArray=((JLabel)thePanel.getComponent(3)).getText().split("\\<.*?>");	//parse the html text
									String referenceInfo="";
									for(int j=0;j<referenceInfoArray.length;j++){
										if(!referenceInfoArray[j].equals("")){
											referenceInfo+=referenceInfoArray[j]+"\n";
										}
									}
									reference.add("\n"+referenceInfo.replaceAll("&nbsp;"," ")+"\n");
									pdf.add(reference);
								}

								progressBar.setValue(5);

								//generate codon frequency table of reference set
								ArrayList<CodonFrequencyTable> summaryTableList=new ArrayList<CodonFrequencyTable>();	//an arrayList to store all the generated codon frequency tables

								progressStatus.setText("Counting reference set...");
								pdf.add(Chunk.NEWLINE);
								pdf.add(new Paragraph("Codon frequency of reference set:",font12));
								for(int i=0;i<weightSize;i++){
									if(Thread.currentThread().isInterrupted())return;
									JPanel thePanel=(JPanel)((DefaultListModel)weightList.getModel()).elementAt(i);
									String name=((JLabel)thePanel.getComponent(3)).getText().split("\\<.*?>")[4];
									String path=((JLabel)thePanel.getComponent(1)).getText();
									//find the GenBank
									GenBank genBank=null;
									for(int j=0;j<library.size();j++){
										if(path.equals(library.get(j).dirName)){
											genBank=library.get(j);
										}
									}
									if(genBank==null){
										for(int j=0;j<toolbox.size();j++){
											if(path.equals(toolbox.get(j).dirName)){
												genBank=toolbox.get(j);
											}
										}
									}
									if(genBank==null){
										for(int j=0;j<favourite.size();j++){
											if(path.equals(favourite.get(j).dirName)){
												genBank=favourite.get(j);
											}
										}
									}
									if(genBank==null){
										JOptionPane.showMessageDialog(CAI,"Cannot read "+name+"\nIt is ignored.","ERROR",JOptionPane.ERROR_MESSAGE);
										continue;
									}
									//count codon frequency for different cases
									if(((JLabel)thePanel.getComponent(0)).getIcon()==(Icon)orgnIcon||((JLabel)thePanel.getComponent(0)).getIcon()==(Icon)mfaIcon||((JLabel)thePanel.getComponent(0)).getIcon().toString().equals(fnaIcon.toString())){	//orgn manual,mfa or fna
										if(genBank.getSequencesByType("CDS")==null){	//there is no protein genes or the genome is "empty"
											Paragraph manualTitle=new Paragraph("\n",font10);
											manualTitle.add(new Phrase(genBank.name+":",new Font(FontFamily.HELVETICA,10,Font.UNDERLINE,BaseColor.BLUE)));
											manualTitle.add(new Phrase("\nComplete genome analysis..."));
											manualTitle.add(new Phrase("\nNo protein genes detected.",new Font(FontFamily.HELVETICA,10,Font.NORMAL,BaseColor.RED)));
											pdf.add(manualTitle);
											continue;
										}
										CodonFrequencyTable theTable=new CodonFrequencyTable(genBank,pdf);
										summaryTableList.add(theTable);
										cpc.add(CodonPairCalculator.getCodonPairFrequency(genBank.getSequencesByType("CDS")));	//digest all the sequences in th genBank into codonpaircalculator
									}else if(((JLabel)thePanel.getComponent(0)).getIcon()==(Icon)orgn_auto||((JLabel)thePanel.getComponent(0)).getIcon()==(Icon)mfa_auto){	//orgn auto or mfa auto
										//add title
										Paragraph gTitle=new Paragraph("\n",font10);
										gTitle.add(new Phrase(genBank.name+":",new Font(FontFamily.HELVETICA,10,Font.UNDERLINE,BaseColor.BLUE)));
										gTitle.add(new Phrase("\nAutopick analysis...",font10));
										pdf.add(gTitle);

										ArrayList<Sequence> completeProteinList=genBank.getSequencesByType("CDS");	//all protein genes
										if(completeProteinList==null){//if there is no sequence identified yet in the genome
											pdf.add(new Paragraph("No protein genes detected.",new Font(FontFamily.HELVETICA,10,Font.NORMAL,BaseColor.RED)));
											continue;
										}
										ArrayList<Sequence> proteinList=genBank.getSequencesByType("CDS");	//currently remaining protein genes
										XYSeriesCollection dataset=new XYSeriesCollection();	//store result of each iteration
										//generate codon frequency table virtually
										for(int j=0;j<completeProteinList.size();j++){
											int[] codonFrequency=completeProteinList.get(j).getCodonFrequency(genBank.completeSequence);
											completeProteinList.get(j).codonFrequency=codonFrequency;	//record into the sequence
										}

										int loopCount=0;
										float leftOverPercentage;
										float threshold=Float.parseFloat(((JLabel)thePanel.getComponent(4)).getText().split(",")[0]);
										float shrinkage=Float.parseFloat(((JLabel)thePanel.getComponent(4)).getText().split(",")[1]);
										if((threshold/100)*proteinList.size()<1){
											pdf.add(new Paragraph("Too small to be autopicked. Try increase threshold to at least: "+((float)1/proteinList.size())*100+"%",new Font(FontFamily.HELVETICA,10,Font.NORMAL,BaseColor.RED)));
											continue;
										}
										while(true){	//keep looping to reduce genes number
											loopCount++;
											//check if selection threshold is satisfied
											leftOverPercentage=((float)proteinList.size()/completeProteinList.size())*100;
											if(leftOverPercentage<=threshold){
												loopCount--;
												break;//stop looping
											}

											//get codon frequency list from remain genes
											ArrayList<int[]> codonFrequencyList=new ArrayList<int[]>();
											for(int j=0;j<proteinList.size();j++){
												codonFrequencyList.add(proteinList.get(j).codonFrequency);
											}
											//generate weight table virtually
											float[] weight=WeightTable.getWeight(codonFrequencyList);
											//generate gWeight table virtually
											float[] gWeight=WeightTable.getGWeight(weight,codonFrequencyList);
											//calculate gCAI for all genes
											for(int j=0;j<completeProteinList.size();j++){
												double gCAI=CAICalculator.getCAI(gWeight,completeProteinList.get(j).codonFrequency);
												completeProteinList.get(j).gCAI=gCAI;	//record into sequence
											}
											//sort the data to ascending gCAI values in completeProteinList (only first iteration) and in proteinList
											if(loopCount==1){	//only on first iteration we sort the complete protein list
												Collections.sort(completeProteinList,new Comparator<Sequence>(){
													public int compare(Sequence sequenceOne,Sequence sequenceTwo){
														if(sequenceOne.gCAI==sequenceTwo.gCAI){
															return 0;
														}else if(sequenceOne.gCAI>sequenceTwo.gCAI){
															return 1;
														}else{
															return -1;
														}
													}
												});
											}
											Collections.sort(proteinList,new Comparator<Sequence>(){
												public int compare(Sequence sequenceOne,Sequence sequenceTwo){
													if(sequenceOne.gCAI==sequenceTwo.gCAI){
														return 0;
													}else if(sequenceOne.gCAI>sequenceTwo.gCAI){
														return 1;
													}else{
														return -1;
													}
												}
											});
											//dispose lower gCAI genes from proteinList by shrinkage percentage
											int numberToBeReduced=Math.round(proteinList.size()*(shrinkage/100));
											for(int j=0;j<numberToBeReduced;j++){
												proteinList.remove(0);
											}
											//cook the dataset to be injected into graph
											XYSeries series=new XYSeries("Iteration "+loopCount);
											for(int j=0;j<completeProteinList.size();j++){
												Number gCAIInject=new Float(completeProteinList.get(j).gCAI);
												series.add(j,gCAIInject);
											}
											dataset.addSeries(series);
										}
										//show threshold,shrinkage value used and total iterations
										pdf.add(new Paragraph("Protein genes: "+completeProteinList.size()+"   Threshold: "+threshold+"%   Shrinkage per iteration: "+shrinkage+"%"+"  Total iterations: "+loopCount,font10));
										//make graph
										Image graphImage=CAICalculator.createGraph(writer,dataset,"Rank","gCAI");
										PdfPTable graphTable=new PdfPTable(1);	//need to be inserted into a pdfptable to resolve a bug with image layout across pages
										graphTable.setSpacingBefore(5f);
										PdfPCell graphCell=new PdfPCell(graphImage);
										graphCell.setBorder(0);
										graphTable.addCell(graphCell);
										graphTable.setWidthPercentage(100f);
										pdf.add(graphTable);
										//list out picked genes
										DecimalFormat gCAIFormat=new DecimalFormat("0.000");
										gCAIFormat.setRoundingMode(RoundingMode.HALF_UP);
										Paragraph picked=new Paragraph("",font10);
										picked.add(new Phrase("Picked genes and their final gCAI:",new Font(FontFamily.HELVETICA,10,Font.UNDERLINE)));
										Chunk dottedTab=new Chunk(new DottedLineSeparator(),400,true);
										for(int j=0;j<proteinList.size();j++){
											picked.add(new Chunk("\n"+proteinList.get(j).gene+"("+proteinList.get(j).product+")"));
											picked.add(new Chunk(dottedTab));
											picked.add(new Chunk("(gCAI="+gCAIFormat.format(proteinList.get(j).gCAI)+")"));
										}
										picked.add(new Phrase("\n\n",font10));
										picked.setKeepTogether(true);
										pdf.add(picked);
										//show codonFrequency of the picked genes
										CodonFrequencyTable theTable=new CodonFrequencyTable(proteinList,pdf);
										summaryTableList.add(theTable);
										cpc.add(CodonPairCalculator.getCodonPairFrequency(proteinList));//digest the picked protein genes
									}else if(((JLabel)thePanel.getComponent(2)).getText().equals("gene")){	//gene
										//get the type
										String type="";
										String panelIconName=(((JLabel)thePanel.getComponent(0)).getIcon()).toString();
										if(panelIconName.equals(((Icon)viewer.geneIcon).toString())){
											type="unknown";
										}else if(panelIconName.equals(((Icon)viewer.proteinIcon).toString())){
											type="CDS";
										}else if(panelIconName.equals(((Icon)viewer.tRNAIcon).toString())){
											type="tRNA";
										}else if(panelIconName.equals(((Icon)viewer.rRNAIcon).toString())){
											type="rRNA";
										}else if(panelIconName.equals(((Icon)viewer.miRNAIcon).toString())){
											type="misc_RNA";
										}
										//get the start and stop site
										String temp=((JLabel)thePanel.getComponent(3)).getText();	//parse the gene name from the underlined part of label
										String anotherTemp=temp.split("<br>")[1].replaceAll("\\<.*?>","");
										int start=Integer.parseInt(anotherTemp.split(" ")[0].split("~")[0]);
										int stop=Integer.parseInt(anotherTemp.split(" ")[0].split("~")[1]);
										//get the sequence
										Sequence sequence=genBank.getSequenceBySite(type,start,stop);
										//get the geneName
										String geneName;
										if(sequence.gene==null){
											geneName=sequence.type;
										}else{
											geneName=sequence.gene;
										}
										//write the title
										Paragraph geneTitle=new Paragraph("\n",font10);
										geneTitle.add(new Phrase(geneName,new Font(FontFamily.HELVETICA,10,Font.UNDERLINE,BaseColor.BLUE)));
										if(sequence.product!=null){
											geneTitle.add(new Phrase("("+sequence.product+")",new Font(FontFamily.HELVETICA,10,Font.UNDERLINE,BaseColor.BLUE)));
										}
										geneTitle.add(new Phrase("\nGene analysis..."));
										pdf.add(geneTitle);
										//calculate the sequence's codon frequency
										sequence.codonFrequency=sequence.getCodonFrequency(genBank.completeSequence);
										//make the table
										CodonFrequencyTable theTable=new CodonFrequencyTable(sequence,pdf);
										summaryTableList.add(theTable);
										cpc.add(CodonPairCalculator.getCodonPairFrequency(sequence));	//digest the sequence codon pair
									}
								}
								//generate summary codon frequency table if more than one genome
								CodonFrequencyTable summaryTable=new CodonFrequencyTable();
								if(summaryTableList.size()>1){	//if there is more than one codonfrequency table generated
									//add all the codon frequency tables together
									for(int i=0;i<summaryTableList.size();i++){
										summaryTable.addTable(summaryTableList.get(i));
									}
									//write to pdf
									pdf.add(new Paragraph("\n",font10));
									pdf.add(new Paragraph("Summary table:",new Font(FontFamily.HELVETICA,10,Font.UNDERLINE|Font.BOLD,BaseColor.BLUE)));
									summaryTable.setHeader();
									summaryTable.setTable();
									summaryTable.setFooter();
									summaryTable.show(pdf);
								}else if(summaryTableList.size()==1){	//only one codonfrequencytable
									summaryTable=summaryTableList.get(0);
								}else{	//no codonfrequencytable generated
									//open pdf in pdfviewer(default)
									Desktop dt=Desktop.getDesktop();
									dt.open(new File(fileName+".pdf"));

									return;	//abandon following operations
								}
								//record CodonfrequencyTable into .cft file
								summaryTable.export(fileName);
								progressBar.setValue(55);

								//generate weight table from reference set
								progressStatus.setText("Generating weight table...");	
								pdf.add(Chunk.NEWLINE);
								pdf.add(new Paragraph("Weight table from reference set:",font12));
								pdf.add(new Paragraph("\n",font10));
								weightTable=new WeightTable(summaryTable.codonFrequency,pdf);
								//save weight table into file
								if(Thread.currentThread().isInterrupted())return;
								progressStatus.setText("Saving weight table...");
								File weightFile=new File(fileName+".wt");
								BufferedWriter bw=new BufferedWriter(new FileWriter(weightFile));
								for(int i=0;i<weightTable.weight.length;i++){
									bw.write(Float.toString(weightTable.weight[i])+"\n");
								}
								bw.close();

								//generate cpf and cpt
								if(Thread.currentThread().isInterrupted())return;
								progressStatus.setText("Generating codon pair table...");
								cpc.getCodonPairTable(null);//also generate cpf
								//write codon pair frequency to pdf
								pdf.add(Chunk.NEWLINE);
								pdf.add(new Paragraph("Codon pair frequency of reference set:",font12));
								pdf.add(new Paragraph("\n",font10));
								cpc.showTable(pdf);
								//write cpf and cpt to file
								cpc.exportWeight(fileName);
								cpc.exportFrequency(fileName);

								progressBar.setValue(60);
							}else{	//importedWeight!=null use imported weight values directly
								if(Thread.currentThread().isInterrupted())return;
								progressBar.setValue(5);
								progressStatus.setText("Generating weight table...");	
								pdf.add(Chunk.NEWLINE);
								pdf.add(new Paragraph("Imported weight table:",font12));
								String importedName=((JLabel)((JPanel)weightList.getModel().getElementAt(0)).getComponent(1)).getText().replaceAll("\\<.*?>","");
								Paragraph importedPara=new Paragraph(importedName,new Font(FontFamily.HELVETICA,10,Font.NORMAL,BaseColor.BLUE));
								importedPara.setSpacingBefore(5f);
								importedPara.setSpacingAfter(3f);
								importedPara.setAlignment(Paragraph.ALIGN_CENTER);
								pdf.add(importedPara);
								weightTable=new WeightTable(importedWeight,pdf);
								progressBar.setValue(60);
							}

							if(transgeneSize>0){
								//generate codon frequency table of target sequences
								ArrayList<CodonFrequencyTable> summaryTableList=new ArrayList<CodonFrequencyTable>();	//an arrayList to store all the generated codon frequency tables

								ArrayList<int[][]> summaryCPFList=new ArrayList<int[][]>();	//an arrayList to store all the generated codon pair frequencies

								ArrayList<Paragraph> caiPara=new ArrayList<Paragraph>();	//temporary storage for paragraphs to present in CAI

								ArrayList<Paragraph> cpiPara=new ArrayList<Paragraph>();	//temporary storage for paragraphs to present in CPI

								progressStatus.setText("Counting target sequences...");
								pdf.add(Chunk.NEWLINE);
								pdf.add(new Paragraph("Codon frequency of target sequences:",font12));
								for(int i=0;i<transgeneSize;i++){
									if(Thread.currentThread().isInterrupted())return;
									JPanel thePanel=(JPanel)((DefaultListModel)transgeneList.getModel()).elementAt(i);
									String name=((JLabel)thePanel.getComponent(3)).getText().split("\\<.*?>")[4];
									String path=((JLabel)thePanel.getComponent(1)).getText();
									//find the GenBank
									GenBank genBank=null;
									for(int j=0;j<library.size();j++){
										if(path.equals(library.get(j).dirName)){
											genBank=library.get(j);
										}
									}
									if(genBank==null){
										for(int j=0;j<toolbox.size();j++){
											if(path.equals(toolbox.get(j).dirName)){
												genBank=toolbox.get(j);
											}
										}
									}
									if(genBank==null){
										for(int j=0;j<favourite.size();j++){
											if(path.equals(favourite.get(j).dirName)){
												genBank=favourite.get(j);
											}
										}
									}
									if(genBank==null){
										JOptionPane.showMessageDialog(CAI,"Cannot read "+name+"\nIt is ignored.","ERROR",JOptionPane.ERROR_MESSAGE);
										continue;
									}

									//count codon frequency for different cases
									if(((JLabel)thePanel.getComponent(2)).getText().equals("orgn")){	//orgn
										if(genBank.getSequencesByType("CDS")==null){	//there is no protein genes or the genome is "empty"
											Paragraph manualTitle=new Paragraph("\n",font10);
											manualTitle.add(new Phrase(genBank.name+":",new Font(FontFamily.HELVETICA,10,Font.UNDERLINE,BaseColor.BLUE)));
											manualTitle.add(new Phrase("\nComplete genome analysis..."));
											manualTitle.add(new Phrase("\nNo protein genes detected.",new Font(FontFamily.HELVETICA,10,Font.NORMAL,BaseColor.RED)));
											pdf.add(manualTitle);
											continue;
										}

										//calculate cft
										CodonFrequencyTable theTable=new CodonFrequencyTable(genBank,pdf);
										summaryTableList.add(theTable);
										//calculate and generate CAI list paragraph
										caiPara.add(CAICalculator.getCAI(weightTable.weight,genBank));

										//calculate cpf
										int[][] theCodonPairFrequency=CodonPairCalculator.getCodonPairFrequency(genBank.getSequencesByType("CDS"));
										summaryCPFList.add(theCodonPairFrequency);
										//calculate and generate CPI list paragraph
										cpiPara.add(CodonPairCalculator.getCPI(cpc.codonPairTable,genBank));
									}else if(((JLabel)thePanel.getComponent(2)).getText().equals("fasta")){
										//get the sequence
										Sequence sequence=genBank.sequences.get(0);
										//get the geneName
										String geneName;
										if(sequence.gene==null){
											geneName=sequence.type;
										}else{
											geneName=sequence.gene;
										}

										//write the title
										Paragraph geneTitle=new Paragraph("\n",font10);
										geneTitle.add(new Phrase(geneName,new Font(FontFamily.HELVETICA,10,Font.UNDERLINE,BaseColor.BLUE)));
										if(sequence.product!=null){
											geneTitle.add(new Phrase("("+sequence.product+")",new Font(FontFamily.HELVETICA,10,Font.UNDERLINE,BaseColor.BLUE)));
										}
										geneTitle.add(new Phrase("\nGene analysis..."));
										pdf.add(geneTitle);
										//calculate the sequence's codon frequency
										sequence.codonFrequency=sequence.getCodonFrequency(genBank.completeSequence);
										//make the table
										CodonFrequencyTable theTable=new CodonFrequencyTable(sequence,pdf);
										summaryTableList.add(theTable);

										//calculate and store CAI paragraph to be presented later
										caiPara.add(CAICalculator.getCAI(weightTable.weight,sequence,writer));

										//calculate the sequence's cpf
										int[][] theCodonPairFrequency=sequence.getCodonPairFrequency();
										summaryCPFList.add(theCodonPairFrequency);
										//calculate and store CPI
										cpiPara.add(CodonPairCalculator.getCPI(cpc.codonPairTable,sequence,writer));
									}else if(((JLabel)thePanel.getComponent(2)).getText().equals("gene")){	//gene
										//get the type
										String type="";
										String panelIconName=(((JLabel)thePanel.getComponent(0)).getIcon()).toString();
										if(panelIconName.equals(((Icon)viewer.geneIcon).toString())){
											type="unknown";
										}else if(panelIconName.equals(((Icon)viewer.proteinIcon).toString())){
											type="CDS";
										}else if(panelIconName.equals(((Icon)viewer.tRNAIcon).toString())){
											type="tRNA";
										}else if(panelIconName.equals(((Icon)viewer.rRNAIcon).toString())){
											type="rRNA";
										}else if(panelIconName.equals(((Icon)viewer.miRNAIcon).toString())){
											type="misc_RNA";
										}
										//get the gene name
										String temp=((JLabel)thePanel.getComponent(3)).getText();	//parse the gene name from the underlined part of label
										String anotherTemp=temp.split("<br>")[1].replaceAll("\\<.*?>","");
										int start=Integer.parseInt(anotherTemp.split(" ")[0].split("~")[0]);
										int stop=Integer.parseInt(anotherTemp.split(" ")[0].split("~")[1]);
										//get the sequence
										Sequence sequence=genBank.getSequenceBySite(type,start,stop);
										//get the geneName
										String geneName;
										if(sequence.gene==null){
											geneName=sequence.type;
										}else{
											geneName=sequence.gene;
										}

										//write the title
										Paragraph geneTitle=new Paragraph("\n",font10);
										geneTitle.add(new Phrase(geneName,new Font(FontFamily.HELVETICA,10,Font.UNDERLINE,BaseColor.BLUE)));
										if(sequence.product!=null){
											geneTitle.add(new Phrase("("+sequence.product+")",new Font(FontFamily.HELVETICA,10,Font.UNDERLINE,BaseColor.BLUE)));
										}
										geneTitle.add(new Phrase("\nGene analysis..."));
										pdf.add(geneTitle);
										//calculate the sequence's codon frequency
										sequence.codonFrequency=sequence.getCodonFrequency(genBank.completeSequence);
										//make the table
										CodonFrequencyTable theTable=new CodonFrequencyTable(sequence,pdf);
										summaryTableList.add(theTable);

										//calculate and store CAI paragraph to be presented later
										caiPara.add(CAICalculator.getCAI(weightTable.weight,sequence,writer));

										//calculate the sequence's cpf
										int[][] theCodonPairFrequency=sequence.getCodonPairFrequency();
										summaryCPFList.add(theCodonPairFrequency);
										//calculate and store CPI
										cpiPara.add(CodonPairCalculator.getCPI(cpc.codonPairTable,sequence,writer));
									}
								}

								//generate summary codon frequency table if more than one genome in target sequences
								if(Thread.currentThread().isInterrupted())return;
								CodonFrequencyTable summaryTable=new CodonFrequencyTable();
								if(summaryTableList.size()>1){	//if there is more than one codonfrequency table generated
									//add all the codon frequency tables together
									for(int i=0;i<summaryTableList.size();i++){
										summaryTable.addTable(summaryTableList.get(i));
									}
									//write to pdf
									pdf.add(new Paragraph("\n",font10));
									pdf.add(new Paragraph("Summary table:",new Font(FontFamily.HELVETICA,10,Font.UNDERLINE|Font.BOLD,BaseColor.BLUE)));
									summaryTable.setHeader();
									summaryTable.setTable();
									summaryTable.setFooter();
									summaryTable.show(pdf);
								}else if(summaryTableList.size()==1){	//only one codonfrequencytable
									summaryTable=summaryTableList.get(0);
								}else{	//no codonfrequencytable generated
									//open pdf in pdfviewer(default)
									Desktop dt=Desktop.getDesktop();
									dt.open(new File(fileName+".pdf"));

									return;	//abandon following operations
								}

								//generate summary codon pair frequency table if more than one genome in target sequences
								if(Thread.currentThread().isInterrupted())return;
								int[][] summaryCPF=new int[64][64];
								if(summaryCPFList.size()>1){	//if there is more than one codon pair frequency table generated
									//add all the codon pair frequency tables together
									for(int i=0;i<summaryCPFList.size();i++){
										for(int j=0;j<64;j++){
											for(int k=0;k<64;k++){
												summaryCPF[j][k]+=summaryCPFList.get(i)[j][k];
											}
										}
									}
									//do not write to pdf?
								}else if(summaryTableList.size()==1){	//only one codonfrequencytable
									summaryCPF=summaryCPFList.get(0);
								}else{	//no codonfrequencytable generated
									//open pdf in pdfviewer(default)
									Desktop dt=Desktop.getDesktop();
									dt.open(new File(fileName+".pdf"));

									return;	//abandon following operations
								}
								progressBar.setValue(90);

								//present CAI of target sequences in reference set
								if(Thread.currentThread().isInterrupted())return;
								progressStatus.setText("Calculating CAI...");
								pdf.add(Chunk.NEWLINE);
								pdf.add(new Paragraph("Codon Adaptation Index(CAI):",font12));
								//individual CAI
								for(int j=0;j<caiPara.size();j++){
									pdf.add(caiPara.get(j));
								}
								//summary CAI
								DecimalFormat df=new DecimalFormat("0.000");
								df.setRoundingMode(RoundingMode.HALF_UP);
								double cai=CAICalculator.getCAI(weightTable.weight,summaryTable.codonFrequency);
								pdf.add(line);
								pdf.add(new Paragraph("\n\nOverall CAI of all target sequences = "+df.format(cai),new Font(FontFamily.HELVETICA,11,Font.BOLD)));


								//present CPI of target sequences in reference set
								if(Thread.currentThread().isInterrupted())return;
								double cpi=CodonPairCalculator.getCPI(cpc.codonPairTable,summaryCPF);
								if(!(cpi<0)){
									progressStatus.setText("Calculating CPI...");
									pdf.add(Chunk.NEWLINE);
									pdf.add(new Paragraph("Codon Pair Index(CPI):",font12));
									//individual CPI
									for(int j=0;j<cpiPara.size();j++){
										if(cpiPara.get(j)!=null)pdf.add(cpiPara.get(j));
									}
									//summary CPI
									pdf.add(line);
									pdf.add(new Paragraph("\n\nOverall CPI of all target sequences = "+df.format(cpi),new Font(FontFamily.HELVETICA,11,Font.BOLD)));
								}

								progressBar.setValue(100);
							}

							//open pdf in pdfviewer(default) upon completion
							if(Thread.currentThread().isInterrupted())return;
							Desktop dt=Desktop.getDesktop();
							dt.open(new File(fileName+".pdf"));
						}catch(DocumentException de){
							if(Thread.currentThread().isInterrupted())return;
							JOptionPane.showMessageDialog(CAI,"Document exception.","ERROR",JOptionPane.ERROR_MESSAGE);
							de.printStackTrace();
						}catch(FileNotFoundException fnfe){
							if(Thread.currentThread().isInterrupted())return;
							JOptionPane.showMessageDialog(CAI,"File not found.","ERROR",JOptionPane.ERROR_MESSAGE);
							fnfe.printStackTrace();
						}catch(IOException ioe){
							if(Thread.currentThread().isInterrupted())return;
							JOptionPane.showMessageDialog(CAI,"IO exception.","ERROR",JOptionPane.ERROR_MESSAGE);
							ioe.printStackTrace();
						}catch(ArrayIndexOutOfBoundsException aoobe){
							if(Thread.currentThread().isInterrupted())return;
							JOptionPane.showMessageDialog(CAI,"Array index out of bounds exception.","ERROR",JOptionPane.ERROR_MESSAGE);
							aoobe.printStackTrace();
						}catch(NullPointerException npe){
							if(Thread.currentThread().isInterrupted())return;
							JOptionPane.showMessageDialog(CAI,"Null pointer exception.","ERROR",JOptionPane.ERROR_MESSAGE);
							npe.printStackTrace();
						}catch(IllegalArgumentException iae){
							//handle reading of deleted pdf file error(can be ignored)
							iae.printStackTrace();
						}finally{
							if(Thread.currentThread().isInterrupted())return;
							pdf.close();
							progressDialog.dispose();
							analyzeThread=null;
							CAI.setEnabled(true);
						}
					}
				});
				analyzeThread.start();
			}
		});
	}

	public void stampLogo(PdfContentByte canvas)throws DocumentException,IOException,ArrayIndexOutOfBoundsException,NullPointerException{
		//logo resource
		Image logo=Image.getInstance("logo.png");
		logo.scaleAbsolute(50f,50f);
		logo.setAbsolutePosition(canvas.getPdfDocument().getPageSize().getWidth()-50f-60f,canvas.getPdfDocument().getPageSize().getHeight()-50f-20f);
		//version resource
		String version="";
		try{
			BufferedReader versionBuffer=new BufferedReader(new FileReader("."+File.separator+"version"));
			version=versionBuffer.readLine();
			versionBuffer.close();
		}catch(Exception e){	//ignore the not found problem as this program may execute independently
			e.printStackTrace();
		}

		//writing to pdf
		canvas.addImage(logo);	//add logo
		canvas.beginText();
		float startX=canvas.getPdfDocument().getPageSize().getWidth()-65f;//starting position
		float startY=canvas.getPdfDocument().getPageSize().getHeight()-32f;
		BaseFont logoFont=BaseFont.createFont(BaseFont.COURIER,BaseFont.CP1250,false);
		canvas.setFontAndSize(logoFont,8);
		canvas.showTextAligned(PdfContentByte.ALIGN_LEFT,"Codon",startX,startY,0);	//write version
		canvas.showTextAligned(PdfContentByte.ALIGN_LEFT,"Usage",startX,startY-9f,0);
		canvas.showTextAligned(PdfContentByte.ALIGN_LEFT,"Optimizer",startX,startY-18f,0);
		canvas.showTextAligned(PdfContentByte.ALIGN_LEFT,version,startX,startY-29f,0);
		canvas.endText();
	}

	private void layoutComponents(){
		//left wing
		//filterPanel
		filterPanel.setOpaque(false);
		filterPanel.setLayout(new BoxLayout(filterPanel,BoxLayout.X_AXIS));
		filterPanel.add(filterText);
		filterPanel.add(Box.createRigidArea(new Dimension(5,0)));
		filterPanel.add(filterButton);
		//buttonsPanel
		buttonsPanel.setOpaque(false);
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel,BoxLayout.X_AXIS));
		buttonsPanel.add(homeButton);
		buttonsPanel.add(uponelevelButton);
		buttonsPanel.add(Box.createRigidArea(new Dimension(5,0)));
		buttonsPanel.add(selectProteinButton);
		buttonsPanel.add(Box.createHorizontalGlue());
		buttonsPanel.add(statusLabel);
		//selectInversePanel
		JPanel selectInversePanel=new JPanel();
		selectInversePanel.setOpaque(false);
		selectInversePanel.setLayout(new BoxLayout(selectInversePanel,BoxLayout.X_AXIS));
		selectInversePanel.add(selectInverseButton);
		selectInversePanel.add(Box.createHorizontalGlue());
		//left wing panel
		JPanel leftWingPanel=new JPanel();
		leftWingPanel.setOpaque(false);
		leftWingPanel.setLayout(new BoxLayout(leftWingPanel,BoxLayout.Y_AXIS));
		leftWingPanel.add(filterPanel);
		leftWingPanel.add(Box.createRigidArea(new Dimension(0,5)));
		leftWingPanel.add(buttonsPanel);
		leftWingPanel.add(viewerScroll);
		leftWingPanel.add(Box.createRigidArea(new Dimension(0,5)));
		leftWingPanel.add(selectInversePanel);

		//right wing
		//transgenePanel
		transgenePanel.setOpaque(false);
		transgenePanel.setLayout(new BoxLayout(transgenePanel,BoxLayout.Y_AXIS));
		JPanel transgeneButtonPanel=new JPanel();
		transgeneButtonPanel.setOpaque(false);
		transgeneButtonPanel.setLayout(new BoxLayout(transgeneButtonPanel,BoxLayout.X_AXIS));
		transgeneButtonPanel.add(transgeneLabel);
		transgeneButtonPanel.add(Box.createHorizontalGlue());
		transgenePanel.add(transgeneButtonPanel);
		transgenePanel.add(transgeneScroll);
		//weightPanel
		weightPanel.setOpaque(false);
		weightPanel.setLayout(new BoxLayout(weightPanel,BoxLayout.Y_AXIS));
		JPanel weightButtonPanel=new JPanel();
		weightButtonPanel.setOpaque(false);
		weightButtonPanel.setLayout(new BoxLayout(weightButtonPanel,BoxLayout.X_AXIS));
		weightButtonPanel.add(weightLabel);
		weightButtonPanel.add(Box.createHorizontalGlue());
		weightButtonPanel.add(weightConfigButton);
		weightButtonPanel.add(Box.createRigidArea(new Dimension(5,0)));
		weightPanel.add(weightButtonPanel);
		weightPanel.add(weightScroll);
		//analyzePanel
		analyzePanel.setOpaque(false);
		analyzePanel.setLayout(new BoxLayout(analyzePanel,BoxLayout.X_AXIS));
		analyzePanel.add(Box.createRigidArea(new Dimension(40,0)));
		analyzePanel.add(importWeight);
		analyzePanel.add(Box.createHorizontalGlue());
		analyzePanel.add(insertButton);
		analyzePanel.add(Box.createRigidArea(new Dimension(10,0)));
		analyzePanel.add(analyzeButton);
		//right wing panel
		JPanel rightWingPanel=new JPanel();
		rightWingPanel.setOpaque(false);
		rightWingPanel.setLayout(new BoxLayout(rightWingPanel,BoxLayout.Y_AXIS));
		JPanel rightWingTopPanel=new JPanel();
		rightWingTopPanel.setOpaque(false);
		rightWingTopPanel.setLayout(new BoxLayout(rightWingTopPanel,BoxLayout.X_AXIS));
		JPanel rightTopButtonPanel=new JPanel();
		rightTopButtonPanel.setOpaque(false);
		rightTopButtonPanel.setLayout(new BoxLayout(rightTopButtonPanel,BoxLayout.Y_AXIS));
		rightTopButtonPanel.add(Box.createRigidArea(new Dimension(0,20)));
		rightTopButtonPanel.add(transgeneAddButton);
		rightWingTopPanel.add(rightTopButtonPanel);
		rightWingTopPanel.add(Box.createRigidArea(new Dimension(5,0)));
		rightWingTopPanel.add(transgenePanel);
		rightWingPanel.add(rightWingTopPanel);
		rightWingPanel.add(Box.createRigidArea(new Dimension(0,2)));
		JPanel rightWingBottomPanel=new JPanel();
		rightWingBottomPanel.setOpaque(false);
		rightWingBottomPanel.setLayout(new BoxLayout(rightWingBottomPanel,BoxLayout.X_AXIS));
		JPanel rightBottomButtonPanel=new JPanel();
		rightBottomButtonPanel.setOpaque(false);
		rightBottomButtonPanel.setLayout(new BoxLayout(rightBottomButtonPanel,BoxLayout.Y_AXIS));
		rightBottomButtonPanel.add(Box.createRigidArea(new Dimension(0,20)));
		rightBottomButtonPanel.add(weightAddButton);
		rightWingBottomPanel.add(rightBottomButtonPanel);
		rightWingBottomPanel.add(Box.createRigidArea(new Dimension(5,0)));
		rightWingBottomPanel.add(weightPanel);
		rightWingPanel.add(rightWingBottomPanel);
		rightWingPanel.add(Box.createRigidArea(new Dimension(0,5)));
		rightWingPanel.add(analyzePanel);

		//main content pane
		JPanel mainPanel=new JPanel();
		mainPanel.setBorder(BorderFactory.createEmptyBorder(15,15,10,15));
		mainPanel.setBackground(bgColor);
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.X_AXIS));
		mainPanel.add(leftWingPanel);
		mainPanel.add(Box.createRigidArea(new Dimension(5,0)));
		mainPanel.add(rightWingPanel);
		CAI.add(mainPanel);
	}

	private void setupProperties(){
		if(independent){
			((JFrame)CAI).setTitle("CAI Analyzer");
			((JFrame)CAI).setIconImage(wheel.getImage());
			((JFrame)CAI).setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}else{
			((JDialog)CAI).setTitle("CAI Analyzer");
			((JDialog)CAI).setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		}
		CAI.setSize(700,500);
		CAI.setMinimumSize(new Dimension(700,500));
		CAI.setLocationRelativeTo(null);
		CAI.setVisible(true);
	}

	public static void main(String[] args){
		new CAI();
	}
}
