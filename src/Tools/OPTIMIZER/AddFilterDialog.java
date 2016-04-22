package Tools.OPTIMIZER;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileFilter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import Tools.OPTIMIZER.Filters.Filter;

public class AddFilterDialog extends JDialog{
	Optimizer optimizer;
	Color bgColor=new Color(70,70,70);

	//left list
	JLabel availableFiltersLabel;
	JList availableFiltersList;
	DefaultListModel availableFiltersModel;
	JScrollPane availableFiltersScroll;

	//button
	JButton addFilterButton;
	ImageIcon addFilter_up=new ImageIcon("Icons"+File.separator+"add_up.gif");
	ImageIcon addFilter_down=new ImageIcon("Icons"+File.separator+"add_down.gif");

	//right list
	JLabel inUseFiltersLabel;
	JList inUseFiltersList;
	DefaultListModel inUseFiltersModel;
	JScrollPane inUseFiltersScroll;

	//description
	JLabel description;

	public AddFilterDialog(Optimizer optimizer){
		super(optimizer.OPTIMIZER);
		this.optimizer=optimizer;
		setTitle("Add Filter");
		initiate();
	}

	private void initiate(){
		createComponents();
		assignFunctions();
		layoutComponents();
		setupProperties();
	}

	private void createComponents(){
		//left side
		availableFiltersLabel=new JLabel("Available Filters:");
		availableFiltersLabel.setForeground(Color.WHITE);
		availableFiltersModel=new DefaultListModel();
		availableFiltersList=new JList(availableFiltersModel);
		availableFiltersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		File[] filterFiles=new File("."+File.separator+"Tools"+File.separator+"OPTIMIZER"+File.separator+"Filters").listFiles(new FileFilter(){
			public boolean accept(File f){
				if(f.isDirectory()){
					return false;
				}
				if(f.getName().equals("Filter.java")||f.getName().equals("CodonUsageOptimizer.java")){	//exclude some
					return false;
				}
				String extension=f.getName().split("\\.")[f.getName().split("\\.").length-1];
				if(extension.equals("java")){
					return true;
				}
				return false;
			}
		});	//recognize all installed filters in Filters folder
		for(int i=0;i<filterFiles.length;i++){	//add to the available filters list
			availableFiltersModel.addElement(filterFiles[i].getName().substring(0,filterFiles[i].getName().length()-5));
		}
		availableFiltersScroll=new JScrollPane(availableFiltersList,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		availableFiltersScroll.getViewport().setPreferredSize(new Dimension(217,200));
		availableFiltersScroll.getViewport().setMaximumSize(new Dimension(217,200));

		//button
		addFilterButton=new JButton(addFilter_up);
		addFilterButton.setContentAreaFilled(false);
		addFilterButton.setBorderPainted(false);
		addFilterButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));

		//right side
		inUseFiltersLabel=new JLabel("Filters In Use:");
		inUseFiltersLabel.setForeground(Color.WHITE);
		inUseFiltersModel=new DefaultListModel();
		inUseFiltersList=new JList(inUseFiltersModel);
		for(int i=1;i<optimizer.filterList.size();i++){	//show filters on contentPanel(skip CUO)
			inUseFiltersModel.addElement(optimizer.filterList.get(i).filterLabel.getText());
		}
		inUseFiltersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		inUseFiltersScroll=new JScrollPane(inUseFiltersList,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		inUseFiltersScroll.getViewport().setPreferredSize(new Dimension(217,200));
		inUseFiltersScroll.getViewport().setMaximumSize(new Dimension(217,200));

		//description
		description=new JLabel();
		description.setBorder(BorderFactory.createLineBorder(Color.WHITE,1));
		description.setForeground(Color.WHITE);
		description.setVerticalAlignment(JLabel.TOP);
		description.setPreferredSize(new Dimension(100000,50));
		description.setMaximumSize(new Dimension(100000,50));
	}

	private void assignFunctions(){
		//availableFiltersList
		availableFiltersList.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent lse){
				String theDescription="?";
				try{	//dynamically invoke
					theDescription=(String)(Class.forName("Tools.OPTIMIZER.Filters."+(String)availableFiltersList.getSelectedValue()).getMethod("getDescription",new Class[]{}).invoke(null,new Object[]{}));
				}catch(Exception e){
					e.printStackTrace();
				}
				description.setText("<html><u>"+(String)availableFiltersList.getSelectedValue()+"</u><br>"+theDescription+"</html>");
			}
		});

		//addFilterButton
		addFilterButton.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				addFilterButton.setIcon(addFilter_down);
			}
			public void mouseReleased(MouseEvent me){
				inUseFiltersModel.addElement((String)availableFiltersList.getSelectedValue());
				try{
					Filter newFilter=(Filter)(Class.forName("Tools.OPTIMIZER.Filters."+(String)availableFiltersList.getSelectedValue()).getConstructor(new Class[]{Optimizer.class,int.class}).newInstance(new Object[]{optimizer,optimizer.filterList.size()}));
					optimizer.filterList.add(newFilter);
					optimizer.contentPanel.add(newFilter,optimizer.contentPanel.getComponentCount());
					optimizer.contentPanel.validate();
					optimizer.contentScroll.validate();
				}catch(Exception e){
					e.printStackTrace();
				}
				addFilterButton.setIcon(addFilter_up);
			}
		});

		//inUseFiltersList
		inUseFiltersList.addKeyListener(new KeyAdapter(){
			public void keyReleased(KeyEvent ke){
				if(ke.getKeyCode()==KeyEvent.VK_DELETE){
					int index=inUseFiltersList.getSelectedIndex();
					if(index==-1){
						return;
					}
					optimizer.contentPanel.remove(index+1);
					optimizer.contentPanel.validate();
					optimizer.contentPanel.repaint();
					optimizer.contentScroll.validate();
					optimizer.filterList.remove(index+1);
					inUseFiltersModel.remove(index);
				}
			}
		});
	}

	private void layoutComponents(){
		JPanel mainPanel=new JPanel();
		mainPanel.setBackground(bgColor);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));

		JPanel topPanel=new JPanel();
		topPanel.setOpaque(false);
		topPanel.setLayout(new BoxLayout(topPanel,BoxLayout.X_AXIS));

		JPanel leftPanel=new JPanel();
		leftPanel.setOpaque(false);
		leftPanel.setLayout(new BoxLayout(leftPanel,BoxLayout.Y_AXIS));
		leftPanel.add(availableFiltersLabel);
		leftPanel.add(availableFiltersScroll);
		topPanel.add(leftPanel);

		topPanel.add(Box.createRigidArea(new Dimension(5,0)));
		topPanel.add(addFilterButton);
		topPanel.add(Box.createRigidArea(new Dimension(5,0)));

		JPanel rightPanel=new JPanel();
		rightPanel.setOpaque(false);
		rightPanel.setLayout(new BoxLayout(rightPanel,BoxLayout.Y_AXIS));
		rightPanel.add(inUseFiltersLabel);
		rightPanel.add(inUseFiltersScroll);
		topPanel.add(rightPanel);

		mainPanel.add(topPanel);
		mainPanel.add(Box.createRigidArea(new Dimension(0,5)));

		JPanel gridPanel=new JPanel();
		gridPanel.setOpaque(false);
		gridPanel.setLayout(new GridLayout(0,1));
		gridPanel.add(description);
		mainPanel.add(gridPanel);

		add(mainPanel);
	}

	private void setupProperties(){
		setSize(500,400);
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}
}
