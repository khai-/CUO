package Support;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.BevelBorder;

import Support.parser.GenBank;

public class StageTab extends JPanel{
	StageTab ST=this;
	public ArrayList<GenBank> library;
	public ArrayList<GenBank> toolbox;
	public ArrayList<GenBank> favourite;
	public File directory;	//the sequence file represented by this stagetab
	String type;
	public String name;
	GenBank genBank;	//this stagetab's genbank data
	public JToggleButton moverButton;

	JPanel tab;
	ImageIcon expandTab_up=new ImageIcon("Icons"+File.separator+"expandTab_up.gif");
	ImageIcon expandTab_down=new ImageIcon("Icons"+File.separator+"expandTab_down.gif");
	ImageIcon collapseTab_up=new ImageIcon("Icons"+File.separator+"collapseTab_up.gif");
	ImageIcon collapseTab_down=new ImageIcon("Icons"+File.separator+"collapseTab_down.gif");
	ImageIcon closeTabButton_up=new ImageIcon("Icons"+File.separator+"closeTabButton_up.gif");
	ImageIcon closeTabButton_down=new ImageIcon("Icons"+File.separator+"closeTabButton_down.gif");
	ImageIcon listModeButton_up=new ImageIcon("Icons"+File.separator+"ListModeButton_up.gif");
	ImageIcon listModeButton_down=new ImageIcon("Icons"+File.separator+"ListModeButton_down.gif");
	public JLabel nameLabel;
	public JButton closeTabButton;

	public OperationPanel operationPanel;	//the place to graphically edit genes

	public StageTab(ArrayList<GenBank> library,ArrayList<GenBank> toolbox,ArrayList<GenBank> favourite,File directory,String type,String name,GenBank genBank,JToggleButton moverButton){
		this.library=library;
		this.toolbox=toolbox;
		this.favourite=favourite;
		this.directory=directory;
		this.type=type;
		this.name=name;
		this.genBank=genBank;
		this.moverButton=moverButton;
		createComponents();
		assignFunctions();
		layoutComponents();
		setupProperties();
	}

	private void createComponents(){
		//tab
		tab=new JPanel();
		if(type.equals("genome")){
			tab.setBackground(new Color(143,188,143));	//dark sea green
		}else if(type.equals("CDS")){
			tab.setBackground(new Color(222,184,135));	//burly wood
		}else if(type.equals("tRNA")){
			tab.setBackground(new Color(95,158,160));	//cadet blue
		}else if(type.equals("rRNA")){
			tab.setBackground(new Color(255,165,0));	//orange
		}else if(type.equals("misc_RNA")){
			tab.setBackground(new Color(255,215,0));	//gold
		}else if(type.equals("unknown")){
			tab.setBackground(new Color(105,105,105));	//dim gray
		}else{
			tab.setBackground(Color.GRAY);
		}
		tab.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		tab.setPreferredSize(new Dimension(140,25));
		tab.setMaximumSize(tab.getPreferredSize());
		tab.setMinimumSize(tab.getPreferredSize());
		nameLabel=new JLabel(name.split("\\"+File.separator)[name.split("\\"+File.separator).length-1]);
		nameLabel.setPreferredSize(new Dimension(115,25));
		closeTabButton=new JButton(closeTabButton_up);
		closeTabButton.setBorderPainted(false);
		closeTabButton.setContentAreaFilled(false);
		closeTabButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));

		//operationPanel
		if(genBank!=null){
			operationPanel=new OperationPanel(genBank,ST);
		}else{
			operationPanel=new OperationPanel(ST);
		}
	}

	private void assignFunctions(){
		//closeTabButton
		closeTabButton.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				closeTabButton.setIcon(closeTabButton_down);
			}
			public void mouseReleased(MouseEvent me){
				setVisible(false);
				closeTabButton.setIcon(closeTabButton_up);
			}
		});
	}

	private void layoutComponents(){
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));

		//tab
		tab.setLayout(new BoxLayout(tab,BoxLayout.X_AXIS));
		tab.add(nameLabel);
		tab.add(Box.createHorizontalGlue());
		tab.add(closeTabButton);
		add(tab);
	}

	private void setupProperties(){

	}
}
