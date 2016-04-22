import java.awt.Color;
import java.awt.Desktop;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;

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
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.OverlayLayout;
import javax.swing.ToolTipManager;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileFilter;

import java.net.URI;

import com.itextpdf.text.Image;

import Support.Common;
import Support.DirList;
import Support.OperationPanel;
import Support.StageTab;
import Support.graphics.Circuit;
import Support.parser.GenBank;
import Tools.CAI.CAI;
import Tools.CSR.CSR;
import Tools.MOPTIMIZER.Moptimizer;
import Tools.Processor.Processor;
import Tools.SequenceCreator.SequenceCreator;
import Tools.Translator.Translator;
import Tools.Updater.Updater;

public class CUO extends JFrame implements ActionListener{
	JFrame cuo=this;
	String version="";
	Color bgColor=new Color(76,117,35);
	Color panelColor=new Color(70,70,70);
	Color filterColor=new Color(220,240,180);
	String OS;	//the type of operating system

	ArrayList<GenBank> library;	//list to store loaded genbank data
	ArrayList<GenBank> toolbox; 
	ArrayList<GenBank> favourite;

	JPanel stageMainPanel;
	JPanel stageTabPanel;
	JScrollPane stageTabScroll;
	OperationPanel mainOperationPanel;
	JPanel panelOfOperation;
	JPanel stage;
	JLabel beta;

	JPanel browsingPanel;
	JPanel leftSideColor;
	ImageIcon expand_up;
	ImageIcon expand_down;
	ImageIcon collapse_up;
	ImageIcon collapse_down;
	JPanel rightSideColor;
	File homedir=new File(".");
	ImageIcon home_up;
	ImageIcon home_down;
	ImageIcon uponelevel_up;
	ImageIcon uponelevel_down;
	ImageIcon funnel_up;
	ImageIcon funnel_down;
	JButton browsingExpandButton;

	JPanel dirButtonPanel;
	JButton dirHome;
	JButton dirUpOneLevel;
	JPanel filterPanel;
	JTextField filterText;
	JToggleButton filterButton;
	DefaultListModel listBeforeFilter;
	JButton toStageButton;
	DirList dirList;
	JScrollPane dirScroll;

	JPanel dirButtonPanel2;
	JToggleButton selectProteinButton;
	ImageIcon selectProtein_up;
	ImageIcon selectProtein_down;
	JToggleButton selecttRNAButton;
	ImageIcon selecttRNA_up;
	ImageIcon selecttRNA_down;
	JToggleButton selectrRNAButton;
	ImageIcon selectrRNA_up;
	ImageIcon selectrRNA_down;
	JLabel statusLabel;
	JButton selectInverseButton;

	JMenuBar menuBar;
	JMenu File;
	ImageIcon seqCreatIcon;
	JMenuItem sequenceCreator;
	ImageIcon exitIcon;
	JMenuItem exit;
	ImageIcon openProjIcon;
	JMenuItem openProj;
	ImageIcon newProjIcon;
	JMenuItem newProj;
	ImageIcon saveprojIcon;
	JMenuItem saveProj;
	
	JMenu Tools;
	JMenuItem codingSequenceRetriever;
	JMenuItem caiAnalyzer;
	//JMenuItem optimizer;	//development suspended
	JMenuItem moptimizer;
	JMenuItem processor;
	JMenuItem translator;
	JMenu Resources;
	JMenu Help;
	JMenuItem userGuide;
	JMenuItem tutorials;
	JMenuItem shortCuts;
	JMenu Version;
	JMenuItem update;
	JMenuItem about;

	JToolBar toolBar;
	JButton newProjectButton;
	ImageIcon newProjectIcon_up;
	
	//JScrollPane tabScroll;
	
	ImageIcon newProjectIcon_down;
	JButton saveProjectButton;
	ImageIcon saveProjectIcon_up;
	ImageIcon saveProjectIcon_down;
	JButton saveSequenceButton;
	ImageIcon saveSequenceIcon_up;
	ImageIcon saveSequenceIcon_down;
	JToggleButton moverButton;
	ImageIcon moverIcon_up;
	ImageIcon moverIcon_down;

	/*
	 * Constructor
	 */
	CUO(ArrayList<GenBank> libraryList,ArrayList<GenBank> toolboxList,ArrayList<GenBank> favouriteList){
		this.library=libraryList;
		this.toolbox=toolboxList;
		this.favourite=favouriteList;

		createComponents();
		assignFunctions();
		layoutComponents();
		setupProperties();

		ToolTipManager.sharedInstance().setInitialDelay(250);
	}

	/*
	 * Create Components
	 */
	private void createComponents()
	{
		OS=System.getProperty("os.name").toLowerCase();
		//stage
		stageMainPanel=new JPanel();
		stageTabPanel=new JPanel();
		mainOperationPanel=new OperationPanel(null);
		panelOfOperation=new JPanel();
		panelOfOperation.setOpaque(false);
		panelOfOperation.setLayout(new BoxLayout(panelOfOperation,BoxLayout.Y_AXIS));
		stage=new JPanel();
		stage.setOpaque(false);
		String modification="";
		String temp;	//temporary String to store the readLine()
		try{
			BufferedReader versionBuffer=new BufferedReader(new FileReader("."+java.io.File.separator+"version"));
			version=versionBuffer.readLine()+"<br>";
			temp=versionBuffer.readLine();
			while(temp!=null){
				modification+=temp+"<br>";
				temp=versionBuffer.readLine();
			}
			versionBuffer.close();
		}catch(Exception e){	//ignore the not found problem
			e.printStackTrace();
		}
		String versionString="<html><center><font size=10 color=white><b>"+version+"</b></font><font color=white>"+modification+"</font></center></html>";
		beta=new JLabel(versionString,new ImageIcon("logo.png"),JLabel.LEADING);

		//adding image icons
		expand_up=new ImageIcon("Icons"+java.io.File.separator+"expand_up.gif");
		expand_down=new ImageIcon("Icons"+java.io.File.separator+"expand_down.gif");
		collapse_up=new ImageIcon("Icons"+java.io.File.separator+"collapse_up.gif");
		collapse_down=new ImageIcon("Icons"+java.io.File.separator+"collapse_down.gif");
		
		home_up=new ImageIcon("Icons"+java.io.File.separator+"home_up.gif");
		home_down=new ImageIcon("Icons"+java.io.File.separator+"home_down.gif");
		uponelevel_up=new ImageIcon("Icons"+java.io.File.separator+"uponelevel_up.gif");
		uponelevel_down=new ImageIcon("Icons"+java.io.File.separator+"uponelevel_down.gif");
		funnel_up=new ImageIcon("Icons"+java.io.File.separator+"funnel_up.gif");
		funnel_down=new ImageIcon("Icons"+java.io.File.separator+"funnel_down.gif");
		
		selectProtein_up=new ImageIcon("Icons"+java.io.File.separator+"selectProtein_up.gif");
		selectProtein_down=new ImageIcon("Icons"+java.io.File.separator+"selectProtein_down.gif");
		
		selecttRNA_up=new ImageIcon("Icons"+java.io.File.separator+"selecttRNA_up.gif");
		selecttRNA_down=new ImageIcon("Icons"+java.io.File.separator+"selecttRNA_down.gif");

		selectrRNA_up=new ImageIcon("Icons"+java.io.File.separator+"selectrRNA_up.gif");
		selectrRNA_down=new ImageIcon("Icons"+java.io.File.separator+"selectrRNA_down.gif");
		
		newProjectIcon_up=new ImageIcon("Icons"+java.io.File.separator+"newProject_up.gif");
		
		newProjectIcon_down=new ImageIcon("Icons"+java.io.File.separator+"newProject_down.gif");
		saveProjectIcon_up=new ImageIcon("Icons"+java.io.File.separator+"saveProject_up.gif");
		saveProjectIcon_down=new ImageIcon("Icons"+java.io.File.separator+"saveProject_down.gif");
		saveSequenceIcon_up=new ImageIcon("Icons"+java.io.File.separator+"saveSequence_up.gif");
		saveSequenceIcon_down=new ImageIcon("Icons"+java.io.File.separator+"saveSequence_down.gif");
		moverIcon_up=new ImageIcon("Icons"+java.io.File.separator+"mover_up.gif");
		moverIcon_down=new ImageIcon("Icons"+java.io.File.separator+"mover_down.gif");
		
		seqCreatIcon = new ImageIcon("Icons"+java.io.File.separator+"newProject_down.gif");
		newProjIcon = new ImageIcon("Icons"+java.io.File.separator+"newProj.png");
		exitIcon = new ImageIcon("Icons"+java.io.File.separator+"exit.png");
		openProjIcon = new ImageIcon("Icons"+java.io.File.separator+"folOpen.png");
		saveprojIcon = new ImageIcon("Icons"+java.io.File.separator+"save.png");
		
		//browsingPanel
		browsingPanel=new JPanel();
		rightSideColor=new JPanel();
		leftSideColor=new JPanel();
		browsingExpandButton=new JButton(collapse_up);
		browsingExpandButton.setBorderPainted(false);
		browsingExpandButton.setContentAreaFilled(false);
		browsingExpandButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		dirButtonPanel=new JPanel();
		dirButtonPanel.setBackground(panelColor);
		dirHome=new JButton(home_up);
		dirHome.setBorderPainted(false);
		dirHome.setContentAreaFilled(false);
		dirHome.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		dirUpOneLevel=new JButton(uponelevel_up);
		dirUpOneLevel.setBorderPainted(false);
		dirUpOneLevel.setContentAreaFilled(false);
		dirUpOneLevel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		filterPanel=new JPanel();
		filterPanel.setBackground(panelColor);
		filterText=new JTextField();
		filterText.setPreferredSize(new Dimension(110,20));
		filterText.setMaximumSize(new Dimension(10000,20));
		filterButton=new JToggleButton(funnel_up);
		filterButton.setBorderPainted(false);
		filterButton.setContentAreaFilled(false);
		filterButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		toStageButton=new JButton("<html><font size=2>toStage</html>");
		toStageButton.setMaximumSize(new Dimension(60,20));
		dirList=new DirList(homedir,library,toolbox,favourite);
		dirList.setFixedCellWidth(300);
		dirList.magicalRatio=18/30.0;
		dirScroll=new JScrollPane(dirList,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		dirButtonPanel2=new JPanel();
		dirButtonPanel2.setBackground(panelColor);
		selectProteinButton=new JToggleButton(selectProtein_up);
		selectProteinButton.setBorderPainted(false);
		selectProteinButton.setContentAreaFilled(false);
		selectProteinButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		selecttRNAButton=new JToggleButton(selecttRNA_up);
		selecttRNAButton.setBorderPainted(false);
		selecttRNAButton.setContentAreaFilled(false);
		selecttRNAButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		selectrRNAButton=new JToggleButton(selectrRNA_up);
		selectrRNAButton.setBorderPainted(false);
		selectrRNAButton.setContentAreaFilled(false);
		selectrRNAButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		statusLabel=new JLabel();
		statusLabel.setForeground(Color.WHITE);
		selectInverseButton=new JButton("<html><font size=2>Select Inverse</html>");
		selectInverseButton.setMaximumSize(new Dimension(100,20));

		//JMenubar
		menuBar=new JMenuBar();

		File=new JMenu("File");
		menuBar.add(File);
		sequenceCreator=new JMenuItem("Sequence Creator");
		sequenceCreator.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,InputEvent.CTRL_MASK|InputEvent.SHIFT_MASK)); 
		newProj = new JMenuItem("New project");
		newProj.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask())); 
		saveProj = new JMenuItem("Save Project");
		saveProj.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask())); 
		//saveSeq.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_MASK|InputEvent.SHIFT_MASK)); 
		openProj = new JMenuItem("Open project");
		openProj.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask())); 
		openProj.addActionListener(this);
		File.add(sequenceCreator);
		File.add(newProj);
		File.add(openProj);
		File.add(saveProj);
		exit=new JMenuItem("Exit");
		exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask())); 
		File.add(exit);

		Tools=new JMenu("Tools");
		menuBar.add(Tools);
		codingSequenceRetriever=new JMenuItem("Coding Sequence Retriever");
		Tools.add(codingSequenceRetriever);
		caiAnalyzer=new JMenuItem("CAI Analyzer");
		Tools.add(caiAnalyzer);
		/*optimizer=new JMenuItem("Optimizer");
		Tools.add(optimizer);*/	//development suspended
		moptimizer=new JMenuItem("Moptimizer");
		Tools.add(moptimizer);
		Tools.addSeparator();
		processor=new JMenuItem("Processor");
		Tools.add(processor);
		translator=new JMenuItem("Translator");
		Tools.add(translator);

		//Resources=new JMenu("Resources");
		//menuBar.add(Resources);

		Help=new JMenu("Help");
		userGuide = new JMenuItem("User guide");
		tutorials = new JMenuItem("Video tutorials");
		shortCuts = new JMenuItem("Shortcuts");
		Help.add(userGuide);
		Help.add(tutorials);
		Help.add(shortCuts);
		menuBar.add(Help);

		Version=new JMenu("Version");
		menuBar.add(Version);
		update=new JMenuItem("Update");
		Version.add(update);
		about=new JMenuItem("About");
		Version.add(about);

		setJMenuBar(menuBar);

		//toolBar
		toolBar=new JToolBar();
		toolBar.setFloatable(false);
		toolBar.setBackground(panelColor);
		toolBar.setBorderPainted(false);
		newProjectButton=new JButton(newProjectIcon_up);
		newProjectButton.setToolTipText("New project");
		newProjectButton.setContentAreaFilled(false);
		newProjectButton.setBorderPainted(false);
		newProjectButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		toolBar.add(newProjectButton);
		saveProjectButton=new JButton(saveProjectIcon_up);
		saveProjectButton.setToolTipText("Save project");
		saveProjectButton.setContentAreaFilled(false);
		saveProjectButton.setBorderPainted(false);
		saveProjectButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		toolBar.add(saveProjectButton);
		saveSequenceButton=new JButton(saveSequenceIcon_up);
		saveSequenceButton.setToolTipText("Save sequence");
		saveSequenceButton.setContentAreaFilled(false);
		saveSequenceButton.setBorderPainted(false);
		saveSequenceButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		toolBar.add(saveSequenceButton);
		toolBar.add(Box.createHorizontalGlue());
		moverButton=new JToggleButton(moverIcon_up);
		moverButton.setContentAreaFilled(false);
		moverButton.setBorderPainted(false);
		moverButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		toolBar.add(moverButton);
	}

 /*
  * Assign functions(Action performed)
  */
	private void assignFunctions(){
		//JMenuBar
		//File
		sequenceCreator.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				new SequenceCreator(cuo,library,toolbox,favourite);
			}
		});
		newProj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newProject();
			}
		});
		saveProj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveProject();
			}
		});
		exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				System.exit(0);
			}
		});
		//Tools
		codingSequenceRetriever.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				new CSR(cuo,library);
			}
		});
		caiAnalyzer.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				new CAI(cuo,library,toolbox,favourite);
			}
		});
		/*optimizer.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				new Optimizer(cuo,library,toolbox,favourite);
			}
		});*/
		moptimizer.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				new Moptimizer(cuo,library,toolbox,favourite);
			}
		});
		processor.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				new Processor(cuo);
			}
		});
		translator.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				new Translator(cuo);
			}
		});
		//Help
		userGuide.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				//search for the userguide file
				File theGuide=null;
				File[] theFiles=new File(".").listFiles();
				for(int i=0;i<theFiles.length;i++){
					if(theFiles[i].getName().startsWith("User Guide CUO")&&theFiles[i].getName().endsWith(".pdf")){
						theGuide=theFiles[i];
					}
				}
				if(theGuide==null){ //no userguide found
					JOptionPane.showMessageDialog(cuo,"Cannot find any user guide in the program folder.","File not found",JOptionPane.ERROR_MESSAGE);
					return;
				}
				String fileName=theGuide.getPath();
				//open the userguide
				try{
					 String osName = System.getProperty("os.name").toLowerCase();
	                 if(osName.contains("windows")){
	                    //windows systems only, this is annoying bug workaround, but it works
	                    Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler \"" + fileName + "\"");                                        
	                 }
	                 else{
	                 	//all other systems
	                    Desktop.getDesktop().open(new File(fileName));
	                 }
				}catch(Exception e){
					JOptionPane.showMessageDialog(cuo,"Fail to open user guide.","Error",JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		tutorials.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				Desktop dt=Desktop.getDesktop();
				try{
					dt.browse(new URI("http://www.youtube.com/channel/UCbm54Pc1_CRQQtHKiB7q-9A/videos?flow=grid&view=1"));
				}catch(Exception e){
					JOptionPane.showMessageDialog(cuo,"Fail to open website. Why not come to MorphBioinformatics channel on Youtube.","Error",JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		shortCuts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ShortcutsUI shortcutuI = new ShortcutsUI();
				shortcutuI.setVisible(true);
			}
		});
		//Version
		update.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				if(OS.indexOf("win")>=0){
					int r=JOptionPane.showOptionDialog(cuo,"Internal Updater not available for Windows. Please use Update.jar in the main folder.\nMake sure the program is closed during update.","Windows specific problem",JOptionPane.YES_NO_OPTION,JOptionPane.PLAIN_MESSAGE,null,new String[]{"Fine","Close now and launch Update.jar"},"Fine");
					if(r==JOptionPane.NO_OPTION){	//launch and close
						try{
							Runtime.getRuntime().exec("java -jar Update.jar");
							System.exit(0);
						}catch(Exception e){return;}
					}else{
						return;
					}
				}else{
					new Updater(cuo);
				}
			}
		});
		about.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				JDialog credit=new JDialog(cuo,"About",true);
				JLabel info=new JLabel(
"<html>"+
"<b><u><font size=4><center><legend>CUO (Codon Usage Optimizer)</legend></font></u></b></center><br>"+
"<font size=2><center>"+
"A third year research project in UCL under supervision of <br>"+
"Dr.Saul Purton. Designed primarily to optimize synthetic gene <br>"+
"to be transformed into Chlamydomonas reinhardtii chloroplast. <br>"+
"Hopefully it works.</center></font><br>"+
"<hr><center>"+version+
"12 July 2011<br><br>"+
"Kong Khai Jien<br>"+
"zcbtfi0@live.ucl.ac.uk<br><br><hr></center>"+
"<center><u>Acknowledgements</u><br>"+
"<font size=2>"+
"Saul Purton,Henry Taunt,<br>"+
"Sofie Vonlanthen,Joanna Szaub,Rosanna Young,<br>"+
"Janet Waterhouse,Tommaso Barbi,Lamya Al-Haj.<br></center>"+
"</font>"+
"</html>");
				info.setForeground(Color.WHITE);
				info.setHorizontalAlignment(JLabel.CENTER);	//default is left
				JPanel creditPanel=new JPanel();
				creditPanel.setBackground(panelColor);
				creditPanel.setLayout(new BoxLayout(creditPanel,BoxLayout.X_AXIS));
				creditPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
				creditPanel.add(Box.createHorizontalGlue());
				creditPanel.add(info);
				creditPanel.add(Box.createHorizontalGlue());
				credit.add(creditPanel);
				credit.pack();
				credit.setResizable(false);
				credit.setLocationRelativeTo(cuo);
				credit.setVisible(true);
			}
		});

		//toolBar
		//newProjectButton
		newProjectButton.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				newProjectButton.setIcon(newProjectIcon_down);
			}
			public void mouseReleased(MouseEvent me){					
				newProject();
				newProjectButton.setIcon(newProjectIcon_up);
			}
		});
		//saveProjectButton
		saveProjectButton.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				saveProjectButton.setIcon(saveProjectIcon_down);
			}
			public void mouseReleased(MouseEvent me){
				saveProject();
				saveProjectButton.setIcon(saveProjectIcon_up);
			}
		});
		//saveSequenceButton
		saveSequenceButton.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				saveSequenceButton.setIcon(saveSequenceIcon_down);
			}
			public void mouseReleased(MouseEvent me){
				saveSequenceButton.setIcon(saveSequenceIcon_up);
			}
		});
		//moverButton
		moverButton.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent ie){
				int newModes=0;
				if(ie.getStateChange()==ItemEvent.DESELECTED){
					moverButton.setIcon(moverIcon_up);
					newModes=0;
				}else if(ie.getStateChange()==ItemEvent.SELECTED){
					moverButton.setIcon(moverIcon_down);
					newModes=1;
				}

				if(stageTabPanel.getComponentCount()-1==0){	//if there is no any stagetab
					return;
				}

				//change modes
				for(int i=0;i<stageTabPanel.getComponentCount()-1;i++){//identify which stagetab is focused
					if(((StageTab)(stageTabPanel.getComponent(i))).operationPanel.equals(mainOperationPanel)){
						((StageTab)(stageTabPanel.getComponent(i))).operationPanel.canvas.modes=newModes;
						return;
					}
				}
			}
		});

		//browsingExpandButton
		browsingExpandButton.addMouseListener(new MouseAdapter(){
			public void mouseEntered(MouseEvent me){
				if(browsingExpandButton.getIcon().equals(expand_up)){
					browsingExpandButton.setIcon(expand_down);
				}else{
					browsingExpandButton.setIcon(collapse_down);
				}
			}
			public void mouseExited(MouseEvent me){
				if(browsingExpandButton.getIcon().equals(expand_down)){
					browsingExpandButton.setIcon(expand_up);
				}else if(browsingExpandButton.getIcon().equals(expand_up)){
					//do nothing
				}else{
					browsingExpandButton.setIcon(collapse_up);
				}
			}
		});
		browsingExpandButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				if(browsingPanel.isVisible()){
					browsingPanel.setVisible(false);
					rightSideColor.setVisible(false);
					browsingExpandButton.setIcon(expand_up);
					//browsingExpandButton.setToolTipText("expand here");
				}else{
					browsingPanel.setVisible(true);
					rightSideColor.setVisible(true);
					browsingExpandButton.setIcon(collapse_up);
				}
			}
		});

		//dirHome
		dirHome.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				dirHome.setIcon(home_down);
			}
			public void mouseReleased(MouseEvent me){
				listBeforeFilter=null;
				filterButton.setSelected(false);

				dirList.listFiles(dirList.homeDir);
				dirHome.setIcon(home_up);
			}
		});

		//dirUpOneLevel
		dirUpOneLevel.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				dirUpOneLevel.setIcon(uponelevel_down);
			}
			public void mouseReleased(MouseEvent me){
				listBeforeFilter=null;
				filterButton.setSelected(false);
				if(!dirList.currentDir.getAbsolutePath().equals(dirList.homeDir.getAbsolutePath())){
					dirList.currentDir=dirList.currentDir.getParentFile();
					dirList.listFiles(dirList.currentDir);
					statusLabel.setText(((DefaultListModel)dirList.getModel()).size()+" found");
				}else{	//as if homeButton
					dirList.listFiles(dirList.homeDir);
					statusLabel.setText(((DefaultListModel)dirList.getModel()).size()+" found");
				}
				dirUpOneLevel.setIcon(uponelevel_up);
			}
		});

		//filterText and filterButton
		filterText.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				filterButton.setSelected(true);
				listBeforeFilter=(DefaultListModel)dirList.getModel();
				DefaultListModel newList=new DefaultListModel();
				for(int i=0;i<listBeforeFilter.size();i++){
					JLabel theLabel=((JLabel)((JPanel)listBeforeFilter.getElementAt(i)).getComponent(3));
					if(theLabel.getText().matches("(?i:.*"+filterText.getText()+".*)")==true){//case insensitive
						newList.addElement(listBeforeFilter.getElementAt(i));
					}
				}
				dirList.setModel(newList);
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
						dirList.setModel(listBeforeFilter);
					}
					listBeforeFilter=null;
					filterButton.setIcon(funnel_up);
				}else if(ie.getStateChange()==ItemEvent.SELECTED){
					filterText.setBackground(filterColor);
					filterButton.setIcon(funnel_down);
				}
			}
		});

		//toStageButton
		toStageButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				int[] selected=dirList.getSelectedIndices();
				for(int i=0;i<selected.length;i++){
					JPanel thePanel=(JPanel)dirList.getModel().getElementAt(selected[i]);
					String folderType=((JLabel)thePanel.getComponent(2)).getText();
					//cuoproject folder
					File[] projectFiles=null;
					if(folderType.equals("project")){
						File projectDirectory=new File(((JLabel)thePanel.getComponent(1)).getText());
						//System.out.println(((JLabel)thePanel.getComponent(1)).getText());
						projectFiles=projectDirectory.listFiles();
					}

					if(projectFiles==null){	//if this is a project file
						toStage(thePanel);
						
					}else{	//projectFiles!=null
						//System.out.println("nt null " + );
						toStage(projectFiles);
					}
				}
			}
		});
		
		//dirList
		dirList.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent me){
				if(me.getClickCount()==2){
					if(((DefaultListModel)dirList.getModel()).size()==0)return; //nothing in the list

					cuo.setCursor(new Cursor(Cursor.WAIT_CURSOR));	//set mouse pointer to busy
					listBeforeFilter=null;
					filterButton.setSelected(false);

					int index=dirList.locationToIndex(me.getPoint());
					String fileName=((JLabel)((JPanel)dirList.getModel().getElementAt(index)).getComponent(1)).getText();
					File newDir=new File(fileName);
					String type=((JLabel)((JPanel)dirList.getModel().getElementAt(index)).getComponent(2)).getText();
					if(type.equals("orgn")){
						//System.out.println("orgn");
						dirList.listGenes(newDir);
						if(((DefaultListModel)dirList.getModel()).size()>0){
							statusLabel.setText(((DefaultListModel)dirList.getModel()).size()+" found");
						}else{	//no info yet for the orgn, need update
							statusLabel.setText("No info yet");
						}
					}else if(type.equals("gene")||type.equals("fasta")){
						//do nothing
					}else{
						dirList.listFiles(newDir);
						statusLabel.setText(((DefaultListModel)dirList.getModel()).size()+" found");
					}
					cuo.setCursor(null);	//set cursor back to normal
				}
			}
		});

		//selectProteinButton
		selectProteinButton.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				selectProteinButton.setIcon(selectProtein_down);
			}
			public void mouseReleased(MouseEvent me){
				int size=dirList.getModel().getSize();
				if(size==0){
					selectProteinButton.setIcon(selectProtein_up);
					return;
				}
				dirList.clearSelection();
				for(int i=0;i<size;i++){
					if(((JLabel)((JPanel)dirList.getModel().getElementAt(i)).getComponent(0)).getIcon().toString().equals(((Icon)dirList.proteinIcon).toString())){
						dirList.addSelectionInterval(i,i);
					}
				}
				selectProteinButton.setIcon(selectProtein_up);
			}
		});

		//selecttRNAButton
		selecttRNAButton.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				selecttRNAButton.setIcon(selecttRNA_down);
			}
			public void mouseReleased(MouseEvent me){
				int size=dirList.getModel().getSize();
				if(size==0){
					selecttRNAButton.setIcon(selecttRNA_up);
					return;
				}
				dirList.clearSelection();
				for(int i=0;i<size;i++){
					if(((JLabel)((JPanel)dirList.getModel().getElementAt(i)).getComponent(0)).getIcon().toString().equals(((Icon)dirList.tRNAIcon).toString())){
						dirList.addSelectionInterval(i,i);
					}
				}
				selecttRNAButton.setIcon(selecttRNA_up);
			}
		});

		//selectrRNAButton
		selectrRNAButton.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				selectrRNAButton.setIcon(selectrRNA_down);
			}
			public void mouseReleased(MouseEvent me){
				int size=dirList.getModel().getSize();
				if(size==0){
					selectrRNAButton.setIcon(selectrRNA_up);
					return;
				}
				dirList.clearSelection();
				for(int i=0;i<size;i++){
					if(((JLabel)((JPanel)dirList.getModel().getElementAt(i)).getComponent(0)).getIcon().toString().equals(((Icon)dirList.rRNAIcon).toString())){
						dirList.addSelectionInterval(i,i);
					}
				}
				selectrRNAButton.setIcon(selectrRNA_up);
			}
		});

		//selectInverseButton
		selectInverseButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				ArrayList<Integer> unselected=new ArrayList<Integer>();
				for(int i=0;i<((DefaultListModel)dirList.getModel()).size();i++){
					if(!dirList.isSelectedIndex(i))unselected.add(i);
				}
				Integer[] unselectedInteger=unselected.toArray(new Integer[unselected.size()]);
				int[] unselectedint=new int[unselectedInteger.length];
				for(int i=0;i<unselectedInteger.length;i++){
					unselectedint[i]=(int)unselectedInteger[i];
				}
				dirList.setSelectedIndices(unselectedint);
			}
		});
	}
	
	/*
	 * Action performed method for open project menu item
	 */
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == openProj)
		{
			JFileChooser chooser = new JFileChooser("."+java.io.File.separator+"Favourite");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setFileFilter(new FileFilter() {
		        public boolean accept(File f) {
		            return f.getName().toLowerCase().endsWith(".cuo")
		                || f.isDirectory();
		          }

		          public String getDescription() {
		            return "CUO Files";
		          }
		        });
			    int returnVal = chooser.showOpenDialog(this);
			    if(returnVal == JFileChooser.APPROVE_OPTION) {
			    	File[] projFiles=loadFiles(new File( chooser.getSelectedFile().getAbsolutePath()));
			    	openProject(projFiles);
			    }
		}
	}
	
	/*
	 * load the project content into a array
	 */
	private File[] loadFiles(File f){
	ArrayList<File> fList=new ArrayList<File>();
	File[] theFiles=f.listFiles();
	for(int i=0;i<theFiles.length;i++){
		String fName=theFiles[i].getName();
		if(theFiles[i].isDirectory()){
			File[] subFiles=loadFiles(theFiles[i]);
			for(int j=0;j<subFiles.length;j++){
				fList.add(subFiles[j]);
			}
		}else{
				fList.add(theFiles[i]);
				//System.out.println("load files "+theFiles[i].getName());
		}
	}
	return fList.toArray(new File[fList.size()]);
}
	
	/*
	 * open saved project files 
	 */
	
	public void openProject(final File[] projectFiles)
	{
		final String projectDirectory=projectFiles[0].getParent();
		String fileName = "";
		//decode project files
		for(int i=0;i<projectFiles.length;i++){
			if(projectFiles[i].getName().equals("properties")){
				try{
					//make streams
					BufferedReader br=new BufferedReader(new FileReader(projectFiles[i]));
					String nextLine;
					while((nextLine=br.readLine())!=null){
						//decode project properties
						if(nextLine.startsWith("File")){	//name
							fileName=nextLine.split("\\|")[1];
							toStageOpnProject(fileName);
						}
					}
					br.close();
				}catch(Exception e){
					JOptionPane.showMessageDialog(cuo,"Fail to load properties of "+projectDirectory,"Error",JOptionPane.ERROR_MESSAGE);
				}
				break;
			}
		}
		
	}
	
	/*
	 * function to handle open project
	 */
	public void toStageOpnProject(String file)
	{
		String folderType="fasta";
		if(!(folderType.equals("orgn")||folderType.equals("gene")||folderType.equals("fasta"))){	//check for correct fodler type of things added to stage
			return;
		}

		final String type="";
		
		final String name=file;
		final ArrayList<int[]> sites;
		if(folderType.equals("orgn")||folderType.equals("fasta")){
			sites=null;
		}else{	//==gene,extract the sites
			sites=new ArrayList<int[]>();
			String theText=file;
			String[] temp=theText.split("<br>")[1].split("Length:")[0].replaceAll("\\<.*?>","").split(" ");
			for(int j=0;j<temp.length;j++){
				int[] site=new int[2];
				site[0]=Integer.parseInt(temp[j].split("~")[0]);
				site[1]=Integer.parseInt(temp[j].split("~")[1]);
				System.out.println(site[0] + " " + site[1]);
				sites.add(site);
			}
		}

		GenBank passedGen=Common.getGenBank(name,library,toolbox,favourite);

		//check if the request is valid	
		final GenBank newgen;
		if(passedGen==null){
			JOptionPane.showMessageDialog(cuo,"Cannot find "+name,"Error",JOptionPane.PLAIN_MESSAGE);
			return;
		}else{
			newgen=passedGen;
		}

		new Thread(new Runnable(){
			public void run(){
				final StageTab newTab=new StageTab(library,toolbox,favourite,null,type,name,newgen,moverButton);
				newTab.setAlignmentY(0);
				panelOfOperation.remove(panelOfOperation.getComponentCount()-1);
				mainOperationPanel= newTab.operationPanel;
				panelOfOperation.add(mainOperationPanel);
				mainOperationPanel.requestFocus();
				panelOfOperation.validate();
				panelOfOperation.repaint();
				newTab.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
				for(int j=0;j<stageTabPanel.getComponentCount()-1;j++){
					StageTab theTab=(StageTab)(stageTabPanel.getComponent(j));
					if(!theTab.equals(newTab)){
						theTab.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
					}
				}

				//tab itself
				newTab.addMouseListener(new MouseAdapter(){
					public void mouseReleased(MouseEvent me){
						for(int j=0;j<stageTabPanel.getComponentCount()-1;j++){
							StageTab theTab=(StageTab)(stageTabPanel.getComponent(j));
							if(!theTab.equals(newTab)){
									theTab.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
							}
						}

						panelOfOperation.remove(panelOfOperation.getComponentCount()-1);
						mainOperationPanel= newTab.operationPanel;
						panelOfOperation.add(mainOperationPanel);
						mainOperationPanel.requestFocus();
						panelOfOperation.validate();
						panelOfOperation.repaint();
						newTab.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

						//update the mover button toggle
						if(newTab.operationPanel.canvas.modes==1){
							newTab.moverButton.setSelected(true);
						}else{	//modes==0
							newTab.moverButton.setSelected(false);
						}
					}
				});
				//closeTabButton
				newTab.closeTabButton.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent ae){
						//change the mainOperationPanel
						if(mainOperationPanel.equals(newTab.operationPanel)){
							OperationPanel aPanel=new OperationPanel(null);
							if(stageTabPanel.getComponentCount()==2){	//this is the last stageTab
								aPanel.setOpaque(false);
								//update the mover button toggle
								newTab.moverButton.setSelected(false);
							}else{	//show the tab before
								for(int j=0;j<stageTabPanel.getComponentCount()-1;j++){
									if(((StageTab)(stageTabPanel.getComponent(j))).equals(newTab)){
										if(j>0){
											aPanel=((StageTab)stageTabPanel.getComponent(j-1)).operationPanel;
											((StageTab)stageTabPanel.getComponent(j-1)).setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

											//update the mover button toggle
											if(((StageTab)stageTabPanel.getComponent(j-1)).operationPanel.canvas.modes==1){
												((StageTab)stageTabPanel.getComponent(j-1)).moverButton.setSelected(true);
											}else{	//modes==0
												((StageTab)stageTabPanel.getComponent(j-1)).moverButton.setSelected(false);
											}
										}else{
											aPanel=((StageTab)stageTabPanel.getComponent(j+1)).operationPanel;
											((StageTab)stageTabPanel.getComponent(j+1)).setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

											//update the mover button toggle
											if(((StageTab)stageTabPanel.getComponent(j+1)).operationPanel.canvas.modes==1){
												((StageTab)stageTabPanel.getComponent(j+1)).moverButton.setSelected(true);
											}else{	//modes==0
												((StageTab)stageTabPanel.getComponent(j+1)).moverButton.setSelected(false);
											}
										}
										break;
									}
								}
							}

							panelOfOperation.remove(panelOfOperation.getComponentCount()-1);
							mainOperationPanel= aPanel;
							panelOfOperation.add(mainOperationPanel);
							panelOfOperation.validate();
							panelOfOperation.repaint();
						}

						//dispose the tab
						for(int j=0;j<stageTabPanel.getComponentCount()-1;j++){
							if(((StageTab)(stageTabPanel.getComponent(j))).equals(newTab)){
								stageTabPanel.remove(j);
								stageTabPanel.validate();
								stageTabPanel.repaint();
								break;
							}
						}
					}
				});

				stageTabPanel.add(newTab,stageTabPanel.getComponentCount()-1);
				newTab.moverButton.setSelected(false);
				stageMainPanel.validate();
				stageMainPanel.repaint();
				//center view the circuit
				newTab.operationPanel.canvas.center(newTab.operationPanel.circuitList.get(0));
			}
		}).start();
	}

/*
 * Create new project
 */
	public void newProject()
	{
		new Thread(new Runnable(){
			public void run(){
				final StageTab newTab=new StageTab(library,toolbox,favourite,null,"unknown","NEW",null,moverButton);
				newTab.setAlignmentY(0);
				panelOfOperation.remove(panelOfOperation.getComponentCount()-1);
				mainOperationPanel= newTab.operationPanel;
				panelOfOperation.add(mainOperationPanel);
				mainOperationPanel.requestFocus();
				panelOfOperation.validate();
				panelOfOperation.repaint();
				newTab.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
				for(int j=0;j<stageTabPanel.getComponentCount()-1;j++){
					StageTab theTab=(StageTab)(stageTabPanel.getComponent(j));
					if(!theTab.equals(newTab)){
						theTab.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
					}
				}

				//tab itself
				newTab.addMouseListener(new MouseAdapter(){
					public void mouseReleased(MouseEvent me){
						for(int j=0;j<stageTabPanel.getComponentCount()-1;j++){
							StageTab theTab=(StageTab)(stageTabPanel.getComponent(j));
							if(!theTab.equals(newTab)){
									theTab.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
							}
						}

						panelOfOperation.remove(panelOfOperation.getComponentCount()-1);
						mainOperationPanel= newTab.operationPanel;
						panelOfOperation.add(mainOperationPanel);
						mainOperationPanel.requestFocus();
						panelOfOperation.validate();
						panelOfOperation.repaint();
						newTab.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

						//update the mover button toggle
						if(newTab.operationPanel.canvas.modes==1){
							newTab.moverButton.setSelected(true);
						}else{	//modes==0
							newTab.moverButton.setSelected(false);
						}
					}
				});
				//closeTabButton
				newTab.closeTabButton.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent ae){
						//change the mainOperationPanel
						if(mainOperationPanel.equals(newTab.operationPanel)){
							OperationPanel aPanel=new OperationPanel(null);
							if(stageTabPanel.getComponentCount()==2){	//this is the last stageTab
								aPanel.setOpaque(false);

								//update the mover button toggle
								newTab.moverButton.setSelected(false);
							}else{	//show the tab before
								for(int j=0;j<stageTabPanel.getComponentCount()-1;j++){
									if(((StageTab)(stageTabPanel.getComponent(j))).equals(newTab)){
										if(j>0){
											aPanel=((StageTab)stageTabPanel.getComponent(j-1)).operationPanel;
											((StageTab)stageTabPanel.getComponent(j-1)).setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

											//update the mover button toggle
											if(((StageTab)stageTabPanel.getComponent(j-1)).operationPanel.canvas.modes==1){
												((StageTab)stageTabPanel.getComponent(j-1)).moverButton.setSelected(true);
											}else{	//modes==0
												((StageTab)stageTabPanel.getComponent(j-1)).moverButton.setSelected(false);
											}
										}else{
											aPanel=((StageTab)stageTabPanel.getComponent(j+1)).operationPanel;
											((StageTab)stageTabPanel.getComponent(j+1)).setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

											//update the mover button toggle
											if(((StageTab)stageTabPanel.getComponent(j+1)).operationPanel.canvas.modes==1){
												((StageTab)stageTabPanel.getComponent(j+1)).moverButton.setSelected(true);
											}else{	//modes==0
												((StageTab)stageTabPanel.getComponent(j+1)).moverButton.setSelected(false);
											}
										}
										break;
									}
								}
							}

							panelOfOperation.remove(panelOfOperation.getComponentCount()-1);
							mainOperationPanel= aPanel;
							panelOfOperation.add(mainOperationPanel);
							panelOfOperation.validate();
							panelOfOperation.repaint();
						}

						//dispose the tab
						for(int j=0;j<stageTabPanel.getComponentCount()-1;j++){
							if(((StageTab)(stageTabPanel.getComponent(j))).equals(newTab)){
								stageTabPanel.remove(j);
								stageTabPanel.validate();
								stageTabPanel.repaint();
								break;
							}
						}
					}
				});

				stageTabPanel.add(newTab,stageTabPanel.getComponentCount()-1);
				newTab.moverButton.setSelected(false);
				stageMainPanel.validate();
				stageMainPanel.repaint();
			}
		}).start();
	}
	
/*
 * save current project
 */
	public void saveProject()
	{
		String savedFileName=""; //temp variable to store name of the saved seq file name
		//get the calling canvas
		Support.Canvas callingCanvas=mainOperationPanel.canvas;
		if(callingCanvas==null){
			saveProjectButton.setIcon(saveProjectIcon_up);
			return;
		}
		StageTab theTab=mainOperationPanel.ST;

		//if not editing existing project,create new project folder
		if(theTab.directory==null){	//"save as" mode
			JFileChooser jfc=new JFileChooser("."+java.io.File.separator+"Favourite");
			if(jfc.showSaveDialog(cuo)==JFileChooser.APPROVE_OPTION){
				try{
					File f=new File(jfc.getSelectedFile().getPath()+".cuo");
					//project folder
					f.mkdirs();
					//project properties file
					File properties=new File(f+java.io.File.separator+"properties");
					BufferedWriter bw=new BufferedWriter(new FileWriter(properties));
					bw.write("NAME|"+jfc.getSelectedFile().getName()+".cuo\n");	//name
					bw.write("DATE|"+new Date().toString()+"\n");	//date
					
					//save each circuit from the circuitList
					for(int i=0;i<callingCanvas.circuitList.size();i++){
						Circuit theCir=callingCanvas.circuitList.get(i);
						bw.write("CIRCUIT|"+theCir.name+"|"+theCir.x+"|"+theCir.y+"\n");	//circuit
						theCir.save(f);
						Common.addToLibrary(library,toolbox,favourite,theCir.genBank);
						savedFileName=theCir.getSavdFileName();
					}
					bw.write("File|" +savedFileName + "\n");
					theTab.directory=f;
					theTab.name=f.getPath().substring(f.getPath().lastIndexOf(java.io.File.separator)+1);
					theTab.nameLabel.setText(theTab.name);
					bw.close();
				}catch(Exception e){}	//do nothing
			}
		}else{	//directory!=null save on existing file
			//ask for overwrite
			int r=JOptionPane.showOptionDialog(cuo,"Overwirte "+theTab.directory+"?","Save",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,new String[]{"Overwrite","Save as","Cancel"},"Overwrite");
			if(r==JOptionPane.YES_OPTION){	//overwrite
				try{
					File f=theTab.directory;
					//project folder
					f.mkdirs();
					//project properties file
					File properties=new File(f+java.io.File.separator+"properties");
					BufferedWriter bw=new BufferedWriter(new FileWriter(properties));
					bw.write("NAME|"+theTab.name+"\n");	//name
					bw.write("DATE|"+new Date().toString()+"\n");	//date
					//save each circuit from the circuitList
					for(int i=0;i<callingCanvas.circuitList.size();i++){
						Circuit theCir=callingCanvas.circuitList.get(i);
						bw.write("CIRCUIT|"+theCir.name+"|"+theCir.x+"|"+theCir.y+"\n");	//circuit
						theCir.save(f);
						Common.addToLibrary(library,toolbox,favourite,theCir.genBank);
					}

					bw.close();
				}catch(Exception e){}	//do nothing
			}else if(r==JOptionPane.NO_OPTION){	//save as new project file
				JFileChooser jfc=new JFileChooser("."+java.io.File.separator+"Favourite");
				if(jfc.showSaveDialog(cuo)==JFileChooser.APPROVE_OPTION){
					try{
						File f=new File(jfc.getSelectedFile().getPath()+".cuo");
						//project folder
						f.mkdirs();
						//project properties file
						File properties=new File(f+java.io.File.separator+"properties");
						BufferedWriter bw=new BufferedWriter(new FileWriter(properties));
						bw.write("NAME|"+jfc.getSelectedFile().getName()+".cuo\n");	//name
						bw.write("DATE|"+new Date().toString()+"\n");	//date
						//save each circuit from the circuitList
						for(int i=0;i<callingCanvas.circuitList.size();i++){
							Circuit theCir=callingCanvas.circuitList.get(i);
							bw.write("CIRCUIT|"+theCir.name+"|"+theCir.x+"|"+theCir.y+"\n");	//circuit
							theCir.save(f);
							Common.addToLibrary(library,toolbox,favourite,theCir.genBank);
						}

						theTab.directory=f;
						theTab.name=f.getPath().substring(f.getPath().lastIndexOf(java.io.File.separator)+1);
						theTab.nameLabel.setText(theTab.name);
						bw.close();
					}catch(Exception e){}	//do nothing
				}
			}
		}
	}
	
/*
 * Layout the User interface components
 */
	private void layoutComponents(){
		//layout on JFrame
		getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.X_AXIS));

		//stage
		stageMainPanel.setLayout(new OverlayLayout(stageMainPanel));
		stageMainPanel.setBackground(bgColor);
		stage.setLayout(new BoxLayout(stage,BoxLayout.Y_AXIS));
		stage.add(Box.createVerticalGlue());
		JPanel betaPanel=new JPanel();
		betaPanel.setBackground(bgColor);
		betaPanel.setLayout(new BoxLayout(betaPanel,BoxLayout.X_AXIS));
		betaPanel.add(Box.createHorizontalGlue());
		betaPanel.add(beta);
		betaPanel.add(Box.createHorizontalGlue());
		stage.add(betaPanel);
		stage.add(Box.createVerticalGlue());
		stageMainPanel.add(stage);
		stageTabPanel.setLayout(new BoxLayout(stageTabPanel,BoxLayout.X_AXIS));
		stageTabPanel.setOpaque(false);
		stageTabPanel.add(Box.createHorizontalGlue());
		panelOfOperation.add(toolBar);
		panelOfOperation.add(stageTabPanel);
		panelOfOperation.add(mainOperationPanel);
		stageMainPanel.add(panelOfOperation,0);
		add(stageMainPanel);

		//browsingPanel
		browsingPanel.setLayout(new BoxLayout(browsingPanel,BoxLayout.Y_AXIS));
		browsingPanel.setBackground(panelColor);
		browsingPanel.add(Box.createRigidArea(new Dimension(0,5)));
		dirButtonPanel.setLayout(new BoxLayout(dirButtonPanel,BoxLayout.X_AXIS));
		dirButtonPanel.add(dirHome);
		dirButtonPanel.add(dirUpOneLevel);
		dirButtonPanel.add(Box.createRigidArea(new Dimension(5,0)));
		filterPanel.setLayout(new BoxLayout(filterPanel,BoxLayout.X_AXIS));
		filterPanel.add(filterText);
		filterPanel.add(filterButton);
		dirButtonPanel.add(filterPanel);
		dirButtonPanel.add(Box.createHorizontalGlue());
		dirButtonPanel.add(toStageButton);
		browsingPanel.add(dirButtonPanel);
		browsingPanel.add(Box.createRigidArea(new Dimension(0,3)));
		browsingPanel.add(dirScroll);
		browsingPanel.add(Box.createRigidArea(new Dimension(0,5)));
		dirButtonPanel2.setLayout(new BoxLayout(dirButtonPanel2,BoxLayout.X_AXIS));
		dirButtonPanel2.add(selectProteinButton);
		dirButtonPanel2.add(selecttRNAButton);
		dirButtonPanel2.add(selectrRNAButton);
		dirButtonPanel2.add(Box.createHorizontalGlue());
		dirButtonPanel2.add(statusLabel);
		dirButtonPanel2.add(Box.createRigidArea(new Dimension(5,0)));
		dirButtonPanel2.add(selectInverseButton);
		browsingPanel.add(dirButtonPanel2);
		browsingPanel.add(Box.createRigidArea(new Dimension(0,5)));
		JPanel area1=new JPanel();
		area1.setPreferredSize(new Dimension(3,10000));
		area1.setMaximumSize(area1.getPreferredSize());
		area1.setBackground(panelColor);
		add(area1);
		leftSideColor.setBackground(panelColor);
		leftSideColor.setLayout(new BoxLayout(leftSideColor,BoxLayout.Y_AXIS));
		leftSideColor.add(Box.createVerticalGlue());
		leftSideColor.add(browsingExpandButton);
		leftSideColor.add(Box.createVerticalGlue());
		add(leftSideColor);
		JPanel area2=new JPanel();
		area2.setBackground(panelColor);
		area2.setPreferredSize(new Dimension(3,10000));
		area2.setMaximumSize(area2.getPreferredSize());
		add(area2);
		add(browsingPanel);
		rightSideColor.setBackground(panelColor);
		rightSideColor.setMaximumSize(new Dimension(5,10000));
		add(rightSideColor);
	}

	private void setupProperties(){
		setTitle("Codon Usage Optimizer  CUO");
		setIconImage(Toolkit.getDefaultToolkit().getImage("logo.png"));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(true);
		setSize(700,500);
		setMinimumSize(new Dimension(700,500));
		setExtendedState(JFrame.MAXIMIZED_BOTH);	//maximize to fullscreen
		setLocationRelativeTo(null);	//center
		setVisible(true);
	}

	//send a file represented  by the its panel in dirlist to stage
	private void toStage(JPanel thePanel){
		String folderType=((JLabel)thePanel.getComponent(2)).getText();
		if(!(folderType.equals("orgn")||folderType.equals("gene")||folderType.equals("fasta"))){	//check for correct fodler type of things added to stage
			return;
		}

		final String type;
		Icon theIcon=((JLabel)thePanel.getComponent(0)).getIcon();
		if(theIcon==(Icon)dirList.geneIcon){
			type="unknown";
		}else if(theIcon==(Icon)dirList.proteinIcon){
			type="CDS";
		}else if(theIcon==(Icon)dirList.tRNAIcon){
			type="tRNA";
		}else if(theIcon==(Icon)dirList.rRNAIcon){
			type="rRNA";
		}else if(theIcon==(Icon)dirList.miRNAIcon){
			type="misc_RNA";
		}else if(theIcon==(Icon)dirList.orgnIcon||theIcon==(Icon)dirList.faaIcon||theIcon==(Icon)dirList.fnaIcon){
			type="genome";
		}else{
			type="";
		}

		final String name=((JLabel)thePanel.getComponent(1)).getText();

		final ArrayList<int[]> sites;
		if(folderType.equals("orgn")||folderType.equals("fasta")){
			sites=null;
		}else{	//==gene,extract the sites
			sites=new ArrayList<int[]>();
			String theText=((JLabel)thePanel.getComponent(3)).getText();
			String[] temp=theText.split("<br>")[1].split("Length:")[0].replaceAll("\\<.*?>","").split(" ");
			for(int j=0;j<temp.length;j++){
				int[] site=new int[2];
				site[0]=Integer.parseInt(temp[j].split("~")[0]);
				site[1]=Integer.parseInt(temp[j].split("~")[1]);
				sites.add(site);
			}
		}

		GenBank passedGen=Common.getGenBank(name,library,toolbox,favourite);

		//check if the request is valid	
		final GenBank newgen;
		if(passedGen==null){
			JOptionPane.showMessageDialog(cuo,"Cannot find "+name,"Error",JOptionPane.PLAIN_MESSAGE);
			return;
		}else{
			newgen=passedGen;
		}

		new Thread(new Runnable(){
			public void run(){
				final StageTab newTab=new StageTab(library,toolbox,favourite,null,type,name,newgen,moverButton);
				newTab.setAlignmentY(0);
				panelOfOperation.remove(panelOfOperation.getComponentCount()-1);
				mainOperationPanel= newTab.operationPanel;
				panelOfOperation.add(mainOperationPanel);
				mainOperationPanel.requestFocus();
				panelOfOperation.validate();
				panelOfOperation.repaint();
				newTab.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
				for(int j=0;j<stageTabPanel.getComponentCount()-1;j++){
					StageTab theTab=(StageTab)(stageTabPanel.getComponent(j));
					if(!theTab.equals(newTab)){
						theTab.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
					}
				}

				//tab itself
				newTab.addMouseListener(new MouseAdapter(){
					public void mouseReleased(MouseEvent me){
						for(int j=0;j<stageTabPanel.getComponentCount()-1;j++){
							StageTab theTab=(StageTab)(stageTabPanel.getComponent(j));
							if(!theTab.equals(newTab)){
									theTab.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
							}
						}

						panelOfOperation.remove(panelOfOperation.getComponentCount()-1);
						mainOperationPanel= newTab.operationPanel;
						panelOfOperation.add(mainOperationPanel);
						mainOperationPanel.requestFocus();
						panelOfOperation.validate();
						panelOfOperation.repaint();
						newTab.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

						//update the mover button toggle
						if(newTab.operationPanel.canvas.modes==1){
							newTab.moverButton.setSelected(true);
						}else{	//modes==0
							newTab.moverButton.setSelected(false);
						}
					}
				});
				//closeTabButton
				newTab.closeTabButton.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent ae){
						//change the mainOperationPanel
						if(mainOperationPanel.equals(newTab.operationPanel)){
							OperationPanel aPanel=new OperationPanel(null);
							if(stageTabPanel.getComponentCount()==2){	//this is the last stageTab
								aPanel.setOpaque(false);
								//update the mover button toggle
								newTab.moverButton.setSelected(false);
							}else{	//show the tab before
								for(int j=0;j<stageTabPanel.getComponentCount()-1;j++){
									if(((StageTab)(stageTabPanel.getComponent(j))).equals(newTab)){
										if(j>0){
											aPanel=((StageTab)stageTabPanel.getComponent(j-1)).operationPanel;
											((StageTab)stageTabPanel.getComponent(j-1)).setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

											//update the mover button toggle
											if(((StageTab)stageTabPanel.getComponent(j-1)).operationPanel.canvas.modes==1){
												((StageTab)stageTabPanel.getComponent(j-1)).moverButton.setSelected(true);
											}else{	//modes==0
												((StageTab)stageTabPanel.getComponent(j-1)).moverButton.setSelected(false);
											}
										}else{
											aPanel=((StageTab)stageTabPanel.getComponent(j+1)).operationPanel;
											((StageTab)stageTabPanel.getComponent(j+1)).setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

											//update the mover button toggle
											if(((StageTab)stageTabPanel.getComponent(j+1)).operationPanel.canvas.modes==1){
												((StageTab)stageTabPanel.getComponent(j+1)).moverButton.setSelected(true);
											}else{	//modes==0
												((StageTab)stageTabPanel.getComponent(j+1)).moverButton.setSelected(false);
											}
										}
										break;
									}
								}
							}

							panelOfOperation.remove(panelOfOperation.getComponentCount()-1);
							mainOperationPanel= aPanel;
							panelOfOperation.add(mainOperationPanel);
							panelOfOperation.validate();
							panelOfOperation.repaint();
						}

						//dispose the tab
						for(int j=0;j<stageTabPanel.getComponentCount()-1;j++){
							if(((StageTab)(stageTabPanel.getComponent(j))).equals(newTab)){
								stageTabPanel.remove(j);
								stageTabPanel.validate();
								stageTabPanel.repaint();
								break;
							}
						}
					}
				});

				stageTabPanel.add(newTab,stageTabPanel.getComponentCount()-1);
				newTab.moverButton.setSelected(false);
				stageMainPanel.validate();
				stageMainPanel.repaint();
				//center view the circuit
				newTab.operationPanel.canvas.center(newTab.operationPanel.circuitList.get(0));
			}
		}).start();
	}

	//send a project folder's files to stage
	private void toStage(final File[] projectFiles){
		final String projectDirectory=projectFiles[0].getParent();
		String projectName=null;
		String projectDate;
		final ArrayList<String> circuitString=new ArrayList<String>();

		//decode project files
		for(int i=0;i<projectFiles.length;i++){
			if(projectFiles[i].getName().equals("properties")){
				try{
					//make streams
					BufferedReader br=new BufferedReader(new FileReader(projectFiles[i]));
					String nextLine;
					while((nextLine=br.readLine())!=null){
						//decode project properties
						if(nextLine.startsWith("NAME")){	//name
							projectName=nextLine.split("\\|")[1];
						}else if(nextLine.startsWith("DATE")){	//date
							projectDate=nextLine.split("\\|")[1];
							//DO SOMETHING with DATE??
						}
						//load the circuits
						else if(nextLine.startsWith("CIRCUIT")){
							circuitString.add(nextLine);
						}
					}
					br.close();
				}catch(Exception e){
					JOptionPane.showMessageDialog(cuo,"Fail to load properties of "+projectDirectory,"Error",JOptionPane.ERROR_MESSAGE);
				}
				break;
			}
		}

		//present in new stage tab
		final String type="genome";
		final String name=projectName;
		new Thread(new Runnable(){
			public void run(){
				final StageTab newTab=new StageTab(library,toolbox,favourite,new File(projectDirectory),type,name,null,moverButton);
				newTab.setAlignmentY(0);
				panelOfOperation.remove(panelOfOperation.getComponentCount()-1);
				mainOperationPanel= newTab.operationPanel;
				panelOfOperation.add(mainOperationPanel);
				mainOperationPanel.requestFocus();
				panelOfOperation.validate();
				panelOfOperation.repaint();
				newTab.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
				for(int j=0;j<stageTabPanel.getComponentCount()-1;j++){
					StageTab theTab=(StageTab)(stageTabPanel.getComponent(j));
					if(!theTab.equals(newTab)){
						theTab.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
					}
				}

				//tab itself
				newTab.addMouseListener(new MouseAdapter(){
					public void mouseReleased(MouseEvent me){
						for(int j=0;j<stageTabPanel.getComponentCount()-1;j++){
							StageTab theTab=(StageTab)(stageTabPanel.getComponent(j));
							if(!theTab.equals(newTab)){
									theTab.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
							}
						}

						panelOfOperation.remove(panelOfOperation.getComponentCount()-1);
						mainOperationPanel= newTab.operationPanel;
						panelOfOperation.add(mainOperationPanel);
						mainOperationPanel.requestFocus();
						panelOfOperation.validate();
						panelOfOperation.repaint();
						newTab.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

						//update the mover button toggle
						if(newTab.operationPanel.canvas.modes==1){
							newTab.moverButton.setSelected(true);
						}else{	//modes==0
							newTab.moverButton.setSelected(false);
						}
					}
				});
				//closeTabButton
				newTab.closeTabButton.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent ae){
						//change the mainOperationPanel
						if(mainOperationPanel.equals(newTab.operationPanel)){
							OperationPanel aPanel=new OperationPanel(null);
							if(stageTabPanel.getComponentCount()==2){	//this is the last stageTab
								aPanel.setOpaque(false);
								//update the mover button toggle
								newTab.moverButton.setSelected(false);
							}else{	//show the tab before
								for(int j=0;j<stageTabPanel.getComponentCount()-1;j++){
									if(((StageTab)(stageTabPanel.getComponent(j))).equals(newTab)){
										if(j>0){
											aPanel=((StageTab)stageTabPanel.getComponent(j-1)).operationPanel;
											((StageTab)stageTabPanel.getComponent(j-1)).setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

											//update the mover button toggle
											if(((StageTab)stageTabPanel.getComponent(j-1)).operationPanel.canvas.modes==1){
												((StageTab)stageTabPanel.getComponent(j-1)).moverButton.setSelected(true);
											}else{	//modes==0
												((StageTab)stageTabPanel.getComponent(j-1)).moverButton.setSelected(false);
											}
										}else{
											aPanel=((StageTab)stageTabPanel.getComponent(j+1)).operationPanel;
											((StageTab)stageTabPanel.getComponent(j+1)).setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

											//update the mover button toggle
											if(((StageTab)stageTabPanel.getComponent(j+1)).operationPanel.canvas.modes==1){
												((StageTab)stageTabPanel.getComponent(j+1)).moverButton.setSelected(true);
											}else{	//modes==0
												((StageTab)stageTabPanel.getComponent(j+1)).moverButton.setSelected(false);
											}
										}
										break;
									}
								}
							}

							panelOfOperation.remove(panelOfOperation.getComponentCount()-1);
							mainOperationPanel= aPanel;
							panelOfOperation.add(mainOperationPanel);
							panelOfOperation.validate();
							panelOfOperation.repaint();
						}

						//dispose the tab
						for(int j=0;j<stageTabPanel.getComponentCount()-1;j++){
							if(((StageTab)(stageTabPanel.getComponent(j))).equals(newTab)){
								stageTabPanel.remove(j);
								stageTabPanel.validate();
								stageTabPanel.repaint();
								break;
							}
						}
					}
				});

				stageTabPanel.add(newTab,stageTabPanel.getComponentCount()-1);
				newTab.moverButton.setSelected(false);
				stageMainPanel.validate();
				stageMainPanel.repaint();

				//add the circuits to the canvas
				for(int i=0;i<circuitString.size();i++){
					String circuitName=circuitString.get(i).split("\\|")[1];
					for(int j=0;j<projectFiles.length;j++){	//seek up the extension of circuitName
						String theName=projectFiles[j].getName();
						int lastIndex=theName.lastIndexOf('.');
						if(lastIndex==-1)continue;	//no extension
						if(theName.substring(0,lastIndex).equals(circuitName)){
							circuitName+=theName.substring(lastIndex);
							break;
						}
					}
					int circuitX=Integer.parseInt(circuitString.get(i).split("\\|")[2]);
					int circuitY=Integer.parseInt(circuitString.get(i).split("\\|")[3]);
					GenBank genBank=Common.getGenBank(projectDirectory+java.io.File.separator+circuitName,library,toolbox,favourite);
					if(genBank!=null){
						newTab.operationPanel.canvas.addCircuit(genBank,circuitX,circuitY);
					}else{
						JOptionPane.showMessageDialog(cuo,"Fail to load "+circuitName,"Error",JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}).start();
	}
}
