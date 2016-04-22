package Tools.TestDigest;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import Support.Common;
import Support.SelectGeneDialog;
import Support.parser.GenBank;

public class TestDigest extends JDialog{
	JDialog TESTDIGEST=this;
	Color bgColor=new Color(70,70,70);
	ArrayList<GenBank> library;
	ArrayList<GenBank> toolbox;
	ArrayList<GenBank> favourite;

	public ArrayList<GenBank> genBanks;	//genBanks of merged sequences
	public ArrayList<GenBank> restrictions=new ArrayList<GenBank>();	//list of restriction sites to be scanned

	Vector<Vector<String>> tableData;
	Vector<String> columnNames;
	public JTable resultTable;
	JScrollPane tableScroll;

	JButton loadButton;
	JButton clearButton;

	JTextArea fragmentsSize;
	JScrollPane fragmentsScroll;

	public JPanel execPanel;
	public JButton detectSitesButton;

	private class TDRenderer extends JTextArea implements TableCellRenderer{
		public Component getTableCellRendererComponent(JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int column){
			//colouring
			setBackground(Color.WHITE);

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

	public TestDigest(JFrame ownerframe,ArrayList<GenBank> genBanks,ArrayList<GenBank> library,ArrayList<GenBank> toolbox,ArrayList<GenBank> favourite){
		super(ownerframe,false);
		this.genBanks=genBanks;
		this.library=library;
		this.toolbox=toolbox;
		this.favourite=favourite;
		initiate();
	}

	public TestDigest(Window ownerframe,ArrayList<GenBank> genBanks,ArrayList<GenBank> library,ArrayList<GenBank> toolbox,ArrayList<GenBank> favourite){
		super(ownerframe);
		this.genBanks=genBanks;
		this.library=library;
		this.toolbox=toolbox;
		this.favourite=favourite;
		initiate();
	}

	private void initiate(){
		createComponents();
		assignFunctions();
		layoutComponents();
		setupProperties();
	}

	private void createComponents(){
		tableData=new Vector<Vector<String>>();
		columnNames=new Vector<String>();
		columnNames.add("Name");
		columnNames.add("Sequence");
		columnNames.add("Count");
		columnNames.add("Positions");
		columnNames.add("Sequences cut");
		resultTable=new JTable(tableData,columnNames){
			public boolean isCellEditable(int rowIndex,int colIndex){return false;}	//not editable
		};
		resultTable.setDefaultRenderer(Object.class,new TDRenderer());
		DefaultTableColumnModel dtcm=(DefaultTableColumnModel)resultTable.getColumnModel();//column size
		dtcm.getColumn(2).setMaxWidth(80);
		dtcm.getColumn(1).setMinWidth(100);
		TableRowSorter<TableModel> sorter=new TableRowSorter<TableModel>(resultTable.getModel());//sorter
		resultTable.setRowSorter(sorter);
		Comparator<String> intcomparator=new Comparator<String>(){
			public int compare(String one,String two){
				int numberOne=Integer.parseInt(one);
				int numberTwo=Integer.parseInt(two);
				if(numberOne>numberTwo){
					return 1;
				}else if(numberOne<numberTwo){
					return -1;
				}else{
					return 0;
				}
			}
		};
		sorter.setComparator(2,intcomparator);
		tableScroll=new JScrollPane(resultTable,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		loadButton=new JButton("Load");
		clearButton=new JButton("Clear");

		fragmentsSize=new JTextArea();
		fragmentsScroll=new JScrollPane(fragmentsSize,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		fragmentsScroll.setPreferredSize(new Dimension(9999999,50));

		execPanel=new JPanel();
		execPanel.setLayout(new BoxLayout(execPanel,BoxLayout.Y_AXIS));
		execPanel.setOpaque(false);
		detectSitesButton=new JButton("<html>Detect sites</html>");
	}

	private void assignFunctions(){
		//delete key
		resultTable.addKeyListener(new KeyAdapter(){
			public void keyReleased(KeyEvent ke){
				int[] selectedRows=resultTable.getSelectedRows();
				if(selectedRows.length==0)return;
				if(ke.getKeyCode()==KeyEvent.VK_DELETE){	//delete key
					for(int i=0;i<selectedRows.length;i++){
						if(i==0){
							((DefaultTableModel)resultTable.getModel()).removeRow(selectedRows[i]);
						}else{
							((DefaultTableModel)resultTable.getModel()).removeRow(selectedRows[i-1]);
						}

						restrictions.remove(selectedRows[i]);
					}
				}
			}
		});

		//loadButton
		loadButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				final SelectGeneDialog sgd=new SelectGeneDialog((Window)TESTDIGEST,library,toolbox,favourite,"Pick restriction sites");
				sgd.viewer.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				sgd.viewer.homeDir=new File("."+File.separator+"Toolbox"+File.separator+"Restriction");
				sgd.viewer.listFiles(sgd.viewer.homeDir);
				sgd.OKButton.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent ae){
						//identify the selected thing and copy selected gene/genome
						if(sgd.viewer.getSelectedValue()==null){//assure something is chosen
							JOptionPane.showMessageDialog(sgd.SGD,"Please choose a gene.","Error",JOptionPane.PLAIN_MESSAGE);
							return;
						}

						Object[] panels=sgd.viewer.getSelectedValues();
						for(int z=0;z<panels.length;z++){
							JPanel thePanel=(JPanel)panels[z];
							String type=((JLabel)thePanel.getComponent(2)).getText();
							GenBank passedGen=null;
							GenBank newgen;
							String name=((JLabel)thePanel.getComponent(1)).getText();
							if(type.equals("gene")){
								final ArrayList<int[]> sites=new ArrayList<int[]>();
								String theText=((JLabel)thePanel.getComponent(3)).getText();
								String[] temp=theText.split("<br>")[1].split("Length:")[0].replaceAll("\\<.*?>","").split(" ");
								for(int j=0;j<temp.length;j++){
									int[] site=new int[2];
									site[0]=Integer.parseInt(temp[j].split("~")[0]);
									site[1]=Integer.parseInt(temp[j].split("~")[1]);
									sites.add(site);
								}

								//get the genBank from library,toolbox and favourite list
								passedGen=Common.getGenBank(name,library,toolbox,favourite);

								newgen=passedGen.copy(sites);//copy only the gene
							}else if(type.equals("fasta")){
								passedGen=Common.getGenBank(name,library,toolbox,favourite);
								newgen=passedGen.copy();
							}else{
								JOptionPane.showMessageDialog(sgd.SGD,"Please choose a gene.","Error",JOptionPane.PLAIN_MESSAGE);
								return;
							}

							boolean notRepeated=true;	//check if the restriction site is already added
							for(int i=0;i<restrictions.size();i++){
								if(restrictions.get(i).dirName.equals(newgen.dirName)){
									notRepeated=false;
								}
							}
							if(notRepeated){
								restrictions.add(newgen);
								Vector<String> info=new Vector<String>();
								info.add(newgen.name);
								String theSite="";
								int topCut=Integer.parseInt(newgen.note.split("\\|")[0]);
								int bottomCut=Integer.parseInt(newgen.note.split("\\|")[1]);
								if(topCut>bottomCut){
									theSite=new StringBuilder(newgen.completeSequence).insert(topCut-1,'>').insert(bottomCut-1+1,'<').toString();
								}else{	//bottomCut>topCut
									theSite=new StringBuilder(newgen.completeSequence).insert(topCut-1,'>').insert(bottomCut-1+1+1,'<').toString();
								}
								info.add(theSite);
								info.add("?");
								info.add("?");
								info.add("?");
								((DefaultTableModel)resultTable.getModel()).addRow(info);
							}
						}

						sgd.SGD.dispose();
						resultTable.revalidate();
					}
				});
			}
		});

		//clearButton
		clearButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				//clear restrictions
				restrictions.clear();

				//clear resutlTable
				tableData.clear();
				resultTable.revalidate();
				resultTable.repaint();
			}
		});

		//detectSitesButton
		detectSitesButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				for(int i=0;i<restrictions.size();i++){	//for all restriction sites in queue
					//reset the tableData to "?"
					tableData.get(i).set(2,"?");
					tableData.get(i).set(3,"?");
					tableData.get(i).set(4,"?");

					String theRes=restrictions.get(i).completeSequence;
					String[] resPatterns={theRes,new StringBuffer(theRes).reverse().toString(),Common.getOppositeDNAStrand(theRes),new StringBuffer(Common.getOppositeDNAStrand(theRes)).reverse().toString()};	//4 different cutting patterns

					//detect for inverted repeat
					boolean goAhead=true;
					boolean changed=false;
					while(true){
						goAhead=true;
						changed=false;
						for(int n=0;n<resPatterns.length;n++){
							for(int m=0;m<resPatterns.length;m++){
								if(m==n)continue;//no check against itself
								if(resPatterns[n].equals(resPatterns[m])){
									String[] newPatterns=new String[resPatterns.length-1];
									for(int p=0;p<resPatterns.length;p++){
										if(p==m)continue;
										newPatterns[p]=resPatterns[p];
									}
									resPatterns=newPatterns;
									goAhead=false;//terminate this loop
									changed=true;//the array is changed,replay
									break;
								}
							}
							if(!goAhead)break;
						}
						if(!changed)break;
					}

					//dealing with wild card
					for(int r=0;r<resPatterns.length;r++){
						resPatterns[r]=resPatterns[r].replaceAll("N","[ATGCU]").replaceAll("R","[AG]").replaceAll("Y","[CTU]").replaceAll("K","[GTU]").replaceAll("M","[AC]").replaceAll("S","[CG]").replaceAll("W","[ATU]").replaceAll("B","[CGTU]").replaceAll("D","[AGTU]").replaceAll("H","[ACTU]").replaceAll("V","[ACG]");
					}

					for(int j=0;j<genBanks.size();j++){	//for all passed genBanks
						GenBank theGen=genBanks.get(j);
						for(int k=0;k<(theGen.completeSequence.length()-theRes.length()+1);k++){//for every base
							String checkThis=theGen.completeSequence.substring(k,k+theRes.length());	//get the checked string
							for(int z=0;z<resPatterns.length;z++){
								if(checkThis.matches(resPatterns[z])){
									//increase count
									String count=tableData.get(i).get(2);
									if(count.equals("?")){
										tableData.get(i).set(2,"1");
									}else{
										tableData.get(i).set(2,""+(Integer.parseInt(count)+1));
									}

									//quote position
									String position=tableData.get(i).get(3);
									if(position.equals("?")){
										tableData.get(i).set(3,""+(k+1));
									}else{
										tableData.get(i).set(3,tableData.get(i).get(3)+"\n"+(k+1));
									}

									//mention seq cut
									String seqCut=tableData.get(i).get(4);
									if(position.equals("?")){
										tableData.get(i).set(4,theGen.getSequenceByLocation(k+1).gene);
									}else{
										tableData.get(i).set(4,tableData.get(i).get(4)+"\n"+theGen.getSequenceByLocation(k+1).gene);
									}
								}
							}
						}
						String count=tableData.get(i).get(2);
						if(count.equals("?")){	//0 site detected
							tableData.get(i).set(2,"0");
						}
					}
				}
				resultTable.validate();
				resultTable.repaint();
			}
		});
	}

	private void layoutComponents(){
		JPanel mainPanel=new JPanel();
		mainPanel.setBackground(bgColor);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));

		mainPanel.add(tableScroll);

		JPanel bottomPanel=new JPanel();
		bottomPanel.setOpaque(false);
		bottomPanel.setLayout(new BoxLayout(bottomPanel,BoxLayout.X_AXIS));

		JPanel bottomLeftPanel=new JPanel();
		bottomLeftPanel.setOpaque(false);
		bottomLeftPanel.setLayout(new BoxLayout(bottomLeftPanel,BoxLayout.Y_AXIS));

		JPanel buttonsPanel=new JPanel();
		buttonsPanel.setOpaque(false);
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel,BoxLayout.X_AXIS));
		buttonsPanel.add(loadButton);
		buttonsPanel.add(Box.createRigidArea(new Dimension(5,0)));
		buttonsPanel.add(clearButton);
		buttonsPanel.add(Box.createHorizontalGlue());
		bottomLeftPanel.add(buttonsPanel);
		bottomLeftPanel.add(Box.createRigidArea(new Dimension(0,5)));

		JPanel fragmentsSizePanel=new JPanel();
		fragmentsSizePanel.setOpaque(false);
		fragmentsSizePanel.setLayout(new BoxLayout(fragmentsSizePanel,BoxLayout.Y_AXIS));
		JLabel fragmentsSizeLabel=new JLabel("Fragments size:");
		fragmentsSizeLabel.setForeground(Color.WHITE);
		fragmentsSizePanel.add(fragmentsSizeLabel);
		fragmentsSizePanel.add(fragmentsScroll);
		bottomLeftPanel.add(fragmentsSizePanel);

		bottomPanel.add(bottomLeftPanel);
		bottomPanel.add(Box.createRigidArea(new Dimension(5,0)));

		execPanel.add(detectSitesButton);
		bottomPanel.add(execPanel);

		mainPanel.add(bottomPanel);

		add(mainPanel);
	}

	private void setupProperties(){
		setTitle("Test Digest");
		setSize(520,300);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
	}
}
