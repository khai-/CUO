package Tools.OPTIMIZER.Filters;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import Support.parser.Sequence;
import Tools.OPTIMIZER.Optimizer;

public abstract class Filter extends JPanel{
	public static String filterName="Filter";
	Sequence target;
	public final Optimizer optimizer;
	Color bgColor=new Color(70,70,70);
	boolean isConfigured=false;
	public boolean isEngaged=false;
	public  int processNumber;	//the number of the process in the filterList

	public JLabel filterLabel;
	public JButton filterConfigure=new JButton("Configure");
	public JLabel filterStatus=new JLabel();
	JButton filterDelete;
	ImageIcon deleteFilter_up=new ImageIcon("Icons"+File.separator+"closeTabButton_up.gif");
	ImageIcon deleteFilter_down=new ImageIcon("Icons"+File.separator+"closeTabButton_down.gif");

	public Filter(Optimizer optimizer,int processNumber){
		this.target=target;
		this.optimizer=optimizer;
		this.processNumber=processNumber;
		updateStatus();
		initiate();
	}

	private void initiate(){
		createComponents();
		assignFunctions();
		layoutComponents();
	}

	private void createComponents(){
		filterLabel=new JLabel();
		filterDelete=new JButton(deleteFilter_up);
		filterDelete.setContentAreaFilled(false);
		filterDelete.setBorderPainted(false);
		filterDelete.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
	}

	private void assignFunctions(){
		//all the functions are set in individual filters
	}

	private void layoutComponents(){
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		setBackground(Color.LIGHT_GRAY);
		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		JPanel thePanel=new JPanel();
		thePanel.setOpaque(false);
		thePanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		thePanel.setLayout(new BoxLayout(thePanel,BoxLayout.X_AXIS));
		thePanel.add(Box.createRigidArea(new Dimension(5,0)));
		thePanel.add(filterLabel);
		thePanel.add(Box.createRigidArea(new Dimension(10,0)));
		thePanel.add(filterConfigure);
		thePanel.add(Box.createRigidArea(new Dimension(5,0)));
		thePanel.add(filterStatus);
		thePanel.add(Box.createHorizontalGlue());
		thePanel.add(filterDelete);
		add(thePanel);
	}

	public abstract boolean trigger();	//abstract function must be override

	public static String getDescription(){
		return "A filter.";
	}

	public void log(String message){
		StyledDocument doc=optimizer.logPane.getStyledDocument();
		try{
			doc.insertString(doc.getLength(),"\n"+message,null);
		}catch(BadLocationException ble){
			ble.printStackTrace();
		}
	}

	public void updateStatus(){
		//running?
		if(isEngaged){
			return;	//allow custom text to be expressed manually
		}

		//target seq added?
		if(optimizer.target==null){
			filterConfigure.setEnabled(true);
			filterStatus.setText("No target sequence.");
			filterStatus.setForeground(Color.RED);
			return;
		}

		//isConfigured?
		if(!isConfigured){
			filterConfigure.setEnabled(true);
			filterStatus.setText("Not yet configured.");
			filterStatus.setForeground(Color.RED);
			return;
		}

		//everything is fine and ready to run
		filterConfigure.setEnabled(true);
		filterStatus.setText("Ready to run.");
		filterStatus.setForeground(Color.BLUE);
	}
}
