package Tools.Processor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import Tools.MOPTIMIZER.CodonPairDialog;
import Tools.MOPTIMIZER.ConfigureDialog;
import Tools.MOPTIMIZER.FrequencyDialog;

public class Processor extends JDialog{
	final JDialog PROCESSOR=this;
	Color bgColor=new Color(70,70,70);
	Font titleFont=new Font(null,Font.PLAIN,12);

	//GET
	JButton viewTables;
	JButton kasuzaTable;

	//COMPARE
	JButton compareTables;

	//MODIFY
	JButton editTable;

	//CALCULATE

	//PLOT

	public Processor(JFrame ownerFrame){
		super(ownerFrame,false);

		createComponents();
		assignFunctions();
		layoutComponents();
		setupProperties();
	}

	private void createComponents(){
		//GET
		viewTables=new JButton("View Tables");
		kasuzaTable=new JButton("Kasuza Table");

		//COMPARE
		compareTables=new JButton("Compare Tables");

		//MODIFY
		editTable=new JButton("Edit Table");

		//CALCULATE

		//PLOT
	}

	private void assignFunctions(){
		//GET
		//viewTables
		viewTables.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				JFileChooser jfc=new JFileChooser("./Construction");
				jfc.setMultiSelectionEnabled(true);
				jfc.setAcceptAllFileFilterUsed(false);
				jfc.addChoosableFileFilter(new FileFilter(){
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
				jfc.addChoosableFileFilter(new FileFilter(){
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
				jfc.addChoosableFileFilter(new FileFilter(){
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
				jfc.addChoosableFileFilter(new FileFilter(){
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
				jfc.setFileFilter(new FileFilter(){
					public boolean accept(File f){
						if(f.isDirectory()){
							return true;
						}
						String extension=f.getName().split("\\.")[f.getName().split("\\.").length-1];
						if(extension.equals("cpt")||extension.equals("wt")||extension.equals("cft")||extension.equals("cpf")){
							return true;
						}
						return false;
					}
					public String getDescription(){
						return "All generated tables (*.cpt *.wt *.cft *.cpf)";
					}
				});

				if(jfc.showOpenDialog(PROCESSOR)==jfc.APPROVE_OPTION){
					File[] selectedFiles=jfc.getSelectedFiles();
					for(int i=0;i<selectedFiles.length;i++){
						String ext=selectedFiles[i].getName().substring(selectedFiles[i].getName().lastIndexOf('.')+1);
						if(ext.equals("cpt")){
							new CodonPairDialog(PROCESSOR,selectedFiles[i]);
						}else if(ext.equals("wt")){
							new ConfigureDialog(PROCESSOR,selectedFiles[i]);
						}else if(ext.equals("cft")){
							new FrequencyDialog(PROCESSOR,selectedFiles[i]);
						}else if(ext.equals("cpf")){
							new CodonPairDialog(PROCESSOR,selectedFiles[i]);	//its the same anyway
						}
					}
				}
			}
		});
		//kasuzaTable
		kasuzaTable.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				new KasuzaTableDialog(PROCESSOR);
			}
		});

		//COMPARE
		//compareTables
		compareTables.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				new CompareTablesDialog(PROCESSOR);
			}
		});

		//MODIFY
		//editTable
		editTable.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				new EditTableDialog(PROCESSOR);
			}
		});
		

		//CALCULATE

		//PLOT
	}

	private void layoutComponents(){
		JPanel mainPanel=new JPanel();
		mainPanel.setBackground(bgColor);
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

		//GET
		JPanel getPanel=new JPanel();
		getPanel.setOpaque(false);
		getPanel.setLayout(new BorderLayout());
		getPanel.setBorder(BorderFactory.createTitledBorder(getPanel.getBorder(),"GET",TitledBorder.LEFT,TitledBorder.TOP,titleFont,Color.WHITE));
		JPanel getThePanel=new JPanel();
		getThePanel.setOpaque(false);
		getThePanel.setLayout(new BoxLayout(getThePanel,BoxLayout.X_AXIS));
		getThePanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		getThePanel.add(viewTables);
		getThePanel.add(Box.createRigidArea(new Dimension(5,0)));
		getThePanel.add(kasuzaTable);
		getThePanel.add(Box.createHorizontalGlue());
		getPanel.add(getThePanel);
		mainPanel.add(getPanel);

		//COMPARE
		JPanel comparePanel=new JPanel();
		comparePanel.setOpaque(false);
		comparePanel.setLayout(new BorderLayout());
		comparePanel.setBorder(BorderFactory.createTitledBorder(comparePanel.getBorder(),"COMPARE",TitledBorder.LEFT,TitledBorder.TOP,titleFont,Color.WHITE));
		JPanel compareThePanel=new JPanel();
		compareThePanel.setOpaque(false);
		compareThePanel.setLayout(new BoxLayout(compareThePanel,BoxLayout.X_AXIS));
		compareThePanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		compareThePanel.add(compareTables);
		compareThePanel.add(Box.createHorizontalGlue());
		comparePanel.add(compareThePanel);
		mainPanel.add(comparePanel);

		//MODIFY
		JPanel modifyPanel=new JPanel();
		modifyPanel.setOpaque(false);
		modifyPanel.setLayout(new BorderLayout());
		modifyPanel.setBorder(BorderFactory.createTitledBorder(modifyPanel.getBorder(),"MODIFY",TitledBorder.LEFT,TitledBorder.TOP,titleFont,Color.WHITE));
		JPanel modifyThePanel=new JPanel();
		modifyThePanel.setLayout(new BoxLayout(modifyThePanel,BoxLayout.X_AXIS));
		modifyThePanel.setOpaque(false);
		modifyThePanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		modifyThePanel.add(editTable);
		modifyPanel.add(modifyThePanel);
		mainPanel.add(modifyPanel);

		//CALCULATE
		JPanel calculatePanel=new JPanel();
		calculatePanel.setOpaque(false);
		calculatePanel.setLayout(new BorderLayout());
		calculatePanel.setBorder(BorderFactory.createTitledBorder(calculatePanel.getBorder(),"CALCULATE",TitledBorder.LEFT,TitledBorder.TOP,titleFont,Color.WHITE));
		JPanel calculateThePanel=new JPanel();
		calculateThePanel.setOpaque(false);
		calculateThePanel.setLayout(new BoxLayout(calculateThePanel,BoxLayout.X_AXIS));
		calculateThePanel.add(Box.createRigidArea(new Dimension(300,30)));
		calculatePanel.add(calculateThePanel);
		mainPanel.add(calculatePanel);

		//PLOT
		JPanel plotPanel=new JPanel();
		plotPanel.setOpaque(false);
		plotPanel.setLayout(new BorderLayout());
		plotPanel.setBorder(BorderFactory.createTitledBorder(plotPanel.getBorder(),"PLOT",TitledBorder.LEFT,TitledBorder.TOP,titleFont,Color.WHITE));
		JPanel plotThePanel=new JPanel();
		plotThePanel.setOpaque(false);
		plotThePanel.setLayout(new BoxLayout(plotThePanel,BoxLayout.X_AXIS));
		plotThePanel.add(Box.createRigidArea(new Dimension(300,30)));
		plotPanel.add(plotThePanel);
		mainPanel.add(plotPanel);

		add(mainPanel);
	}

	private void setupProperties(){
		setTitle("Processor");
		setSize(400,400);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
}
