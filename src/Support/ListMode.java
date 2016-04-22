package Support;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import Support.parser.GenBank;
import Support.parser.Sequence;

public class ListMode extends JDialog{
	JFrame cuo;
	GenBank genBank;	//the data source
	String title;
	Color bgColor=new Color(70,70,70);
	Color filterColor=new Color(220,240,180);

	JPanel mainPanel;

	JPanel topButtonPanel;
	ImageIcon copyButton_up=new ImageIcon("Icons"+File.separator+"copy_up.gif");
	ImageIcon copyButton_down=new ImageIcon("Icons"+File.separator+"copy_down.gif");
	ImageIcon cutButton_up=new ImageIcon("Icons"+File.separator+"cut_up.gif");
	ImageIcon cutButton_down=new ImageIcon("Icons"+File.separator+"cut_down.gif");
	ImageIcon pasteButton_up=new ImageIcon("Icons"+File.separator+"paste_up.gif");
	ImageIcon pasteButton_down=new ImageIcon("Icons"+File.separator+"paste_down.gif");
	ImageIcon trashButton_up=new ImageIcon("Icons"+File.separator+"trash_up.gif");
	ImageIcon trashButton_down=new ImageIcon("Icons"+File.separator+"trash_down.gif");
	ImageIcon moveupButton_up=new ImageIcon("Icons"+File.separator+"moveup_up.gif");
	ImageIcon moveupButton_down=new ImageIcon("Icons"+File.separator+"moveup_down.gif");
	ImageIcon movedownButton_up=new ImageIcon("Icons"+File.separator+"movedown_up.gif");
	ImageIcon movedownButton_down=new ImageIcon("Icons"+File.separator+"movedown_down.gif");
	ImageIcon undoButton_up=new ImageIcon("Icons"+File.separator+"undo_up.gif");
	ImageIcon undoButton_down=new ImageIcon("Icons"+File.separator+"undo_down.gif");
	ImageIcon redoButton_up=new ImageIcon("Icons"+File.separator+"redo_up.gif");
	ImageIcon redoButton_down=new ImageIcon("Icons"+File.separator+"redo_down.gif");
	ImageIcon printButton_up=new ImageIcon("Icons"+File.separator+"print_up.gif");
	ImageIcon printButton_down=new ImageIcon("Icons"+File.separator+"print_down.gif");
	JButton copyButton;
	JButton cutButton;
	JButton pasteButton;
	JButton trashButton;
	JButton moveupButton;
	JButton movedownButton;
	JButton undoButton;
	JButton redoButton;
	JButton printButton;

	JPanel listPanel;
	JTable listTable;
	Vector<Vector> tableData;
	Vector<String> columnNames;
	JScrollPane listScroll;
	Color proteinColor=new Color(222,184,135);
	Color tRNAColor=new Color(95,158,160);
	Color rRNAColor=new Color(255,165,0);
	Color miRNAColor=new Color(255,215,0);
	Color otherColor=new Color(105,105,105);

	JPanel bottomButtonPanel;

	JPanel pocketPanel;

	JPanel sequenceEditorPanel;

	private class LMRenderer extends JTextArea implements TableCellRenderer{
		public Component getTableCellRendererComponent(JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int column){
			//colouring
			String theType=table.getValueAt(row,0).toString();
			if(theType.equals("P")){	//protein
				setBackground(proteinColor);
			}else if(theType.equals("t")){	//tRNA
				setBackground(tRNAColor);
			}else if(theType.equals("r")){	//rRNA
				setBackground(rRNAColor);
			}else if(theType.equals("mi")){	//miRNA
				setBackground(miRNAColor);
			}else{	//other
				setBackground(otherColor);
			}

			//border
			if(isSelected){
				if(column==0){	//first column
					setBorder(BorderFactory.createMatteBorder(2,2,2,0,Color.RED));
				}else if(column==columnNames.size()){	//last column
					setBorder(BorderFactory.createMatteBorder(2,0,2,2,Color.RED));
				}else{	//mid columns
					setBorder(BorderFactory.createMatteBorder(2,0,2,0,Color.RED));
				}
			}else{
				setBorder(null);
			}

			//line wrap
			setText((String)value);
			setWrapStyleWord(true);
			setLineWrap(true);

			int rowNumber=1;
			if(value!=null){
				int lastIndex=0;
				while(lastIndex!=-1){
					lastIndex=((String)value).indexOf("\n",lastIndex);
					if(lastIndex!=-1){
						rowNumber++;
						lastIndex+=2;
					}
				}
				if(table.getRowHeight(row)<rowNumber*16){
					table.setRowHeight(row,rowNumber*16);
				}
			}

			return this;
		}
	}

	public ListMode(JFrame cuo,GenBank genBank){
		super(cuo,false);
		this.cuo=cuo;
		this.genBank=genBank;
		this.title=genBank.name;
		initiate();
	}

	private void initiate(){
		createComponents();
		assignFunctions();
		layoutComponents();
		setupProperties();
	}

	private void createComponents(){
		mainPanel=new JPanel();
		mainPanel.setBackground(bgColor);
		//topButtonPanel
		topButtonPanel=new JPanel();
		topButtonPanel.setOpaque(false);
		copyButton=new JButton(copyButton_up);
		copyButton.setBorderPainted(false);
		copyButton.setContentAreaFilled(false);
		copyButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		cutButton=new JButton(cutButton_up);
		cutButton.setBorderPainted(false);
		cutButton.setContentAreaFilled(false);
		cutButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		pasteButton=new JButton(pasteButton_up);
		pasteButton.setBorderPainted(false);
		pasteButton.setContentAreaFilled(false);
		pasteButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		trashButton=new JButton(trashButton_up);
		trashButton.setBorderPainted(false);
		trashButton.setContentAreaFilled(false);
		trashButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		moveupButton=new JButton(moveupButton_up);
		moveupButton.setBorderPainted(false);
		moveupButton.setContentAreaFilled(false);
		moveupButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		movedownButton=new JButton(movedownButton_up);
		movedownButton.setBorderPainted(false);
		movedownButton.setContentAreaFilled(false);
		movedownButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		undoButton=new JButton(undoButton_up);
		undoButton.setBorderPainted(false);
		undoButton.setContentAreaFilled(false);
		undoButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		redoButton=new JButton(redoButton_up);
		redoButton.setBorderPainted(false);
		redoButton.setContentAreaFilled(false);
		redoButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		printButton=new JButton(printButton_up);
		printButton.setBorderPainted(false);
		printButton.setContentAreaFilled(false);
		printButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));

		//listPanel
		listPanel=new JPanel();
		listPanel.setOpaque(false);
		tableData=new Vector<Vector>();
		for(int i=0;i<genBank.sequences.size();i++){
			Sequence seqInject=genBank.sequences.get(i);	//the sequence to be injected
			String seqType=seqInject.type;
			if(seqType.equals("source")||seqType.equals("gene")||seqType.equals("exon")||seqType.equals("intron")||seqType.equals("misc_difference")||seqType.equals("old_sequence")){	//skip these
				continue;
			}

			String seqstart=new String();
			String seqstop=new String();
			for(int j=0;j<seqInject.interval.size();j++){
				if(j==0){
					seqstart+=seqInject.interval.get(j)[0];
				}else{
					seqstart+="\n"+seqInject.interval.get(j)[0];
				}
			}
			for(int j=0;j<seqInject.interval.size();j++){
				if(j==0){
					seqstop+=seqInject.interval.get(j)[1];
				}else{
					seqstop+="\n"+seqInject.interval.get(j)[1];
				}
			}
			int seqlength=0;
			for(int j=0;j<seqInject.interval.size();j++){	//get length of sequence
				seqlength+=Math.abs(seqInject.interval.get(j)[0]-seqInject.interval.get(j)[1])+1;
			}

			Vector<String> infoInject=new Vector<String>();	//the info to inject
			 //type
			if(seqType.equals("CDS")){	//protein
				infoInject.add("P");
			}else if(seqType.equals("tRNA")){	//tRNA
				infoInject.add("t");
			}else if(seqType.equals("rRNA")){	//rRNA
				infoInject.add("r");
			}else if(seqType.equals("misc_RNA")){	//miRNA
				infoInject.add("mi");
			}else{	//other type
				infoInject.add(" ");
			}
			if(seqInject.gene!=null){	//name
				infoInject.add(seqInject.gene);
			}else{
				infoInject.add(seqInject.type);
			}
			if((Boolean)seqInject.isComplementary()){	//strand
				infoInject.add("+");
			}else{
				infoInject.add("-");
			}
			infoInject.add(seqstart);	//start
			infoInject.add(seqstop);	//stop
			infoInject.add(((Integer)seqlength).toString());	//length
			infoInject.add(seqInject.product);	//description
			tableData.add(infoInject);
		}
		columnNames=new Vector<String>();
		columnNames.add(" ");
		columnNames.add("Name");
		columnNames.add(" ");
		columnNames.add("Start");
		columnNames.add("End");
		columnNames.add("Length");
		columnNames.add("Description");
		listTable=new JTable(tableData,columnNames);
		listTable.setDefaultRenderer(Object.class,new LMRenderer());
		listTable.setBackground(Color.WHITE);
		listTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		TableRowSorter<TableModel> sorter=new TableRowSorter<TableModel>(listTable.getModel());//sorter
		listTable.setRowSorter(sorter);
		Comparator<String> intcomparator=new Comparator<String>(){
			public int compare(String one,String two){
				String tempOne=one.split("\n")[0];
				String tempTwo=two.split("\n")[0];
				int numberOne=Integer.parseInt(tempOne);
				int numberTwo=Integer.parseInt(tempTwo);
				if(numberOne>numberTwo){
					return 1;
				}else if(numberOne<numberTwo){
					return -1;
				}else{
					return 0;
				}
			}
		};
		sorter.setComparator(3,intcomparator);
		sorter.setComparator(4,intcomparator);
		sorter.setComparator(5,intcomparator);
		DefaultTableColumnModel dtcm=(DefaultTableColumnModel)listTable.getColumnModel();//column size
		dtcm.getColumn(dtcm.getColumnCount()-1).setMinWidth(400);
		dtcm.getColumn(0).setMaxWidth(20);
		dtcm.getColumn(2).setMaxWidth(15);
		listScroll=new JScrollPane(listTable,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		listScroll.setPreferredSize(new Dimension(560,listScroll.getPreferredSize().height));

		//bottomButtonPanel
		bottomButtonPanel=new JPanel();

		//pocketPanel
		pocketPanel=new JPanel();

		//sequenceEditorPanel
		sequenceEditorPanel=new JPanel();
	}

	private void assignFunctions(){
		//copyButton
		copyButton.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				copyButton.setIcon(copyButton_down);
			}
			public void mouseReleased(MouseEvent me){
				copyButton.setIcon(copyButton_up);
			}
		});

		//cutButton
		cutButton.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				cutButton.setIcon(cutButton_down);
			}
			public void mouseReleased(MouseEvent me){
				cutButton.setIcon(cutButton_up);
			}
		});

		//pasteButton
		pasteButton.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				pasteButton.setIcon(pasteButton_down);
			}
			public void mouseReleased(MouseEvent me){
				pasteButton.setIcon(pasteButton_up);
			}
		});	

		//trashButton
		trashButton.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				trashButton.setIcon(trashButton_down);
			}
			public void mouseReleased(MouseEvent me){
				trashButton.setIcon(trashButton_up);
			}
		});

		//moveupButton
		moveupButton.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				moveupButton.setIcon(moveupButton_down);
			}
			public void mouseReleased(MouseEvent me){
				moveupButton.setIcon(moveupButton_up);
			}
		});

		//movedownButton
		movedownButton.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				movedownButton.setIcon(movedownButton_down);
			}
			public void mouseReleased(MouseEvent me){
				movedownButton.setIcon(movedownButton_up);
			}
		});

		//undoButton
		undoButton.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				undoButton.setIcon(undoButton_down);
			}
			public void mouseReleased(MouseEvent me){
				undoButton.setIcon(undoButton_up);
			}
		});

		//redoButton
		redoButton.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				redoButton.setIcon(redoButton_down);
			}
			public void mouseReleased(MouseEvent me){
				redoButton.setIcon(redoButton_up);
			}
		});

		//printButton
		printButton.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				printButton.setIcon(printButton_down);
			}
			public void mouseReleased(MouseEvent me){
				printButton.setIcon(printButton_up);
			}
		});
	}

	private void layoutComponents(){
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
		//topButtonPanel
		topButtonPanel.setLayout(new BoxLayout(topButtonPanel,BoxLayout.X_AXIS));
		topButtonPanel.add(Box.createRigidArea(new Dimension(10,0)));
		topButtonPanel.add(copyButton);
		topButtonPanel.add(cutButton);
		topButtonPanel.add(pasteButton);
		topButtonPanel.add(trashButton);
		topButtonPanel.add(Box.createRigidArea(new Dimension(10,0)));
		topButtonPanel.add(moveupButton);
		topButtonPanel.add(movedownButton);
		topButtonPanel.add(Box.createRigidArea(new Dimension(10,0)));
		topButtonPanel.add(undoButton);
		topButtonPanel.add(redoButton);
		topButtonPanel.add(Box.createRigidArea(new Dimension(10,0)));
		topButtonPanel.add(printButton);
		topButtonPanel.add(Box.createHorizontalGlue());
		mainPanel.add(topButtonPanel);

		//listPanel
		listPanel.add(listScroll);
		mainPanel.add(listPanel);

		add(mainPanel);
	}

	private void setupProperties(){
		setTitle(title);
		setSize(600,500);
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);	//center
		setVisible(true);
	}
}
