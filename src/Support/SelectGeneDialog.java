package Support;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;

import Support.parser.GenBank;

public class SelectGeneDialog{
	SelectGeneDialog sgd=this;
	Window ownerFrame;
	public Window SGD;
	ArrayList<GenBank> library;
	ArrayList<GenBank> toolbox;
	ArrayList<GenBank> favourite;
	String title;
	Color bgColor=new Color(70,70,70);
	Color filterColor=new Color(220,240,180);

	JTextField filterText;	
	JToggleButton filterButton;

	JButton homeButton;
	ImageIcon home_up=new ImageIcon("Icons"+File.separator+"home_up.gif");
	ImageIcon home_down=new ImageIcon("Icons"+File.separator+"home_down.gif");
	JButton uponelevelButton;
	ImageIcon uponelevel_up=new ImageIcon("Icons"+File.separator+"uponelevel_up.gif");
	ImageIcon uponelevel_down=new ImageIcon("Icons"+File.separator+"uponelevel_down.gif");
	JLabel statusLabel;

	DefaultListModel listBeforeFilter;
	public DirList viewer;
	JScrollPane dirScroll;

	public JButton OKButton;

	public SelectGeneDialog(Window ownerFrame,ArrayList<GenBank> library,ArrayList<GenBank> toolbox,ArrayList<GenBank> favourite,String title){
		this.library=library;
		this.toolbox=toolbox;
		this.favourite=favourite;
		this.ownerFrame=ownerFrame;
		SGD=new JDialog(ownerFrame);
		this.title=title;
		initiate();
	}

	private void initiate(){
		createComponents();
		assignFunctions();
		layoutComponents();
		setupProperties();
	}

	private void createComponents(){
		filterText=new JTextField();
		filterText.setMaximumSize(new Dimension(350,20));
		filterButton=new JToggleButton("Filter");

		statusLabel=new JLabel();
		statusLabel.setForeground(Color.WHITE);
		homeButton=new JButton(home_up);
		homeButton.setBorderPainted(false);
		homeButton.setContentAreaFilled(false);
		homeButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		uponelevelButton=new JButton(uponelevel_up);
		uponelevelButton.setBorderPainted(false);
		uponelevelButton.setContentAreaFilled(false);
		uponelevelButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));

		viewer=new DirList(new File("."),library,toolbox,favourite);
		viewer.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		dirScroll=new JScrollPane(viewer,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		OKButton=new JButton("OK");
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

		//viewer
		viewer.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent me){
				if(me.getClickCount()==2){
					if(((DefaultListModel)viewer.getModel()).size()==0)return; //nothing in the list

					SGD.setCursor(new Cursor(Cursor.WAIT_CURSOR));	//set mouse pointer to busy
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
					SGD.setCursor(null);	//set cursor back to normal
				}
			}
		});

		//OKButton fuction is decided by the usage externally
	}

	private void layoutComponents(){
		JPanel mainPanel=new JPanel();
		mainPanel.setBackground(bgColor);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));

		JPanel filterPanel=new JPanel();
		filterPanel.setOpaque(false);
		filterPanel.setLayout(new BoxLayout(filterPanel,BoxLayout.X_AXIS));
		filterPanel.add(filterText);
		filterPanel.add(Box.createRigidArea(new Dimension(5,0)));
		filterPanel.add(filterButton);
		mainPanel.add(filterPanel);

		JPanel controlPanel=new JPanel();
		controlPanel.setOpaque(false);
		controlPanel.setLayout(new BoxLayout(controlPanel,BoxLayout.X_AXIS));
		controlPanel.add(homeButton);
		controlPanel.add(uponelevelButton);
		controlPanel.add(Box.createHorizontalGlue());
		controlPanel.add(statusLabel);
		mainPanel.add(controlPanel);

		mainPanel.add(dirScroll);
		mainPanel.add(Box.createRigidArea(new Dimension(0,5)));

		JPanel buttonPanel=new JPanel();
		buttonPanel.setOpaque(false);
		buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.X_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(OKButton);
		mainPanel.add(buttonPanel);

		SGD.add(mainPanel);
	}

	private void setupProperties(){
		((JDialog)SGD).setTitle(title);
		((JDialog)SGD).setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		SGD.setSize(320,480);
		SGD.setMinimumSize(new Dimension(320,480));
		((JDialog)SGD).setResizable(false);
		SGD.setLocationRelativeTo(null);
		SGD.setVisible(true);
	}
}
