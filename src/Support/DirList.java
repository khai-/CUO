package Support;

import java.awt.FlowLayout;
import java.awt.datatransfer.Transferable;
import java.awt.event.HierarchyBoundsAdapter;
import java.awt.event.HierarchyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.TransferHandler;

import Support.parser.GenBank;

public class DirList extends JList{	//a class similar to JList but able to show directories
	public ImageIcon libraryIcon=new ImageIcon("Icons"+File.separator+"libraryIcon.gif");
	public ImageIcon favouriteIcon=new ImageIcon("Icons"+File.separator+"favouriteIcon.gif");
	public ImageIcon toolboxIcon=new ImageIcon("Icons"+File.separator+"toolbox.gif");
	public ImageIcon folderIcon=new ImageIcon("Icons"+File.separator+"folderIcon.gif");
	public ImageIcon cuoProjectIcon=new ImageIcon("Icons"+File.separator+"cuoProject.gif");
	public ImageIcon orgnIcon=new ImageIcon("Icons"+File.separator+"orgnIcon.gif");
	public ImageIcon fnaIcon=new ImageIcon("Icons"+File.separator+"fna.gif");
	public ImageIcon faaIcon=new ImageIcon("Icons"+File.separator+"faa.gif");
	public ImageIcon mfaIcon=new ImageIcon("Icons"+File.separator+"mfa.gif");
	public ImageIcon geneIcon=new ImageIcon("Icons"+File.separator+"geneIcon.gif");
	public ImageIcon proteinIcon=new ImageIcon("Icons"+File.separator+"proteinIcon.gif");
	public ImageIcon tRNAIcon=new ImageIcon("Icons"+File.separator+"tRNAIcon.gif");
	public ImageIcon rRNAIcon=new ImageIcon("Icons"+File.separator+"rRNAIcon.gif");
	public ImageIcon miRNAIcon=new ImageIcon("Icons"+File.separator+"miRNAIcon.gif");

	public File homeDir;
	public File currentDir;
	public ArrayList<GenBank> library;
	public ArrayList<GenBank> toolbox;
	public ArrayList<GenBank> favourite;
	public File[] fileList;
	public int listWidth=190;
	public double magicalRatio=18/30.0;
	public ArrayList<ArrayList<String>> geneSummary;
	public boolean needToRelist=true;

	public DirList(File homeDir,final ArrayList<GenBank> library,final ArrayList<GenBank> toolbox,final ArrayList<GenBank> favourite){
		this.library=library;
		this.toolbox=toolbox;
		this.favourite=favourite;

		//dnd
		setDragEnabled(true);
		setTransferHandler(new TransferHandler(){
			public int getSourceActions(JComponent c){
				return COPY;
			}
			protected Transferable createTransferable(JComponent c){
				DirList dirList=(DirList)c;
				int selected=dirList.getSelectedIndex();
				JPanel thePanel=(JPanel)dirList.getModel().getElementAt(selected);
				String dirName=((JLabel)thePanel.getComponent(1)).getText();
				GenBank gb=Common.getGenBank(dirName,library,toolbox,favourite);
				return gb;
			}
		});

		setCellRenderer(new CustomCellRenderer());
		if(homeDir!=null){	//for using the wrapping ability and icon display
			this.homeDir=homeDir;
			listFiles(homeDir);
		}

		addHierarchyBoundsListener(new HierarchyBoundsAdapter(){
			public void ancestorResized(HierarchyEvent he){
				Thread resizeThread=new Thread(new Runnable(){
					public void run(){
						int newWidth=(int)(getSize().getWidth()*magicalRatio);	//a magical ratio
						if(newWidth!=listWidth){
							listWidth=newWidth;
							relist();
						}
					}
				});
				resizeThread.start();
			}
		});

		addKeyListener(new KeyAdapter(){	//delete function of dirList
			public void keyReleased(KeyEvent ke){
				int[] selected=getSelectedIndices();
				if(ke.getKeyCode()==KeyEvent.VK_DELETE&&selected.length>0){
					if(JOptionPane.showOptionDialog(null,"Delete the selected item?","Delete",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE,null,null,null)==JOptionPane.YES_OPTION){
						for(int i=0;i<selected.length;i++){
							String fileType=((JLabel)((JPanel)getModel().getElementAt(selected[i])).getComponent(2)).getText();
							if(fileType.equals("orgn")||fileType.equals("folder")||fileType.equals("fasta")||fileType.equals("project")){
								//delete the file
								String fileName=((JLabel)((JPanel)getModel().getElementAt(selected[i])).getComponent(1)).getText();
								File file=new File(fileName);
								Common.deleteFolder(file);

								//dump the fragment in library/toolbox/favourite
								if(fileName.startsWith("."+File.separator+"Library"+File.separator)){
									for(int j=0;j<library.size();j++){
										if(fileName.equals(library.get(j).dirName)){
											library.remove(j);
										}
									}
								}else if(fileName.startsWith("."+File.separator+"Toolbox"+File.separator)){
									for(int j=0;j<toolbox.size();j++){
										if(fileName.equals(toolbox.get(j).dirName)){
											toolbox.remove(j);
										}
									}
								}else if(fileName.startsWith("."+File.separator+"Favourite"+File.separator)){
									for(int j=0;j<favourite.size();j++){
										if(fileName.equals(favourite.get(j).dirName)){
											favourite.remove(j);
										}
									}
								}
							}else if(fileType.equals("library")||fileType.equals("favourite")||fileType.equals("toolbox")){
								JOptionPane.showMessageDialog(null,fileType+" must not be deleted","Restricted",JOptionPane.PLAIN_MESSAGE);
							}else if(fileType.equals("gene")){
								JOptionPane.showMessageDialog(null,fileType+" cannot be deleted in a genome, edit it and save as another file.","Restricted",JOptionPane.PLAIN_MESSAGE);
							}
						}
						listFiles(currentDir);
					}
				}
			}
		});
	}

	public void listFiles(File dir){
		currentDir=dir;
		String currentDirName=currentDir.getAbsolutePath().split("\\"+File.separator)[currentDir.getAbsolutePath().split("\\"+File.separator).length-1];
		fileList=dir.listFiles();
		DefaultListModel listModel=new DefaultListModel();
		if(fileList==null){	//if nothing in the folder or it is not a folder
			setModel(listModel);
			return;
		}

		for(int i=0;i<fileList.length;i++){
			String dirName=fileList[i].getPath();
			dirName="."+File.separator+""+dirName.split("\\.\\"+File.separator+"")[dirName.split("\\.\\"+File.separator+"").length-1];
			String fileName=fileList[i].getName();
			if(dirName.equals("."+File.separator+"Library")){//detect library
				JPanel cellPanel=new JPanel(new FlowLayout(FlowLayout.LEFT));
				cellPanel.add(new JLabel(libraryIcon));
				JLabel invisibleLabel=new JLabel(dirName);
				invisibleLabel.setVisible(false);
				cellPanel.add(invisibleLabel);
				JLabel invisibleLabel2=new JLabel("library");
				invisibleLabel2.setVisible(false);
				cellPanel.add(invisibleLabel2);
				JLabel libraryFolder=new JLabel("Library");
				cellPanel.add(libraryFolder);
				listModel.addElement(cellPanel);
			}else if(dirName.equals("."+File.separator+"Favourite")){	//detect favourite
				JPanel cellPanel=new JPanel(new FlowLayout(FlowLayout.LEFT));
				cellPanel.add(new JLabel(favouriteIcon));
				JLabel invisibleLabel=new JLabel(dirName);
				invisibleLabel.setVisible(false);
				cellPanel.add(invisibleLabel);
				JLabel invisibleLabel2=new JLabel("favourite");
				invisibleLabel2.setVisible(false);
				cellPanel.add(invisibleLabel2);
				JLabel favouriteFolder=new JLabel("Favourite");
				cellPanel.add(favouriteFolder);
				listModel.addElement(cellPanel);
			}else if(dirName.equals("."+File.separator+"Toolbox")){	//detect favourite
				JPanel cellPanel=new JPanel(new FlowLayout(FlowLayout.LEFT));
				cellPanel.add(new JLabel(toolboxIcon));
				JLabel invisibleLabel=new JLabel(dirName);
				invisibleLabel.setVisible(false);
				cellPanel.add(invisibleLabel);
				JLabel invisibleLabel2=new JLabel("toolbox");
				invisibleLabel2.setVisible(false);
				cellPanel.add(invisibleLabel2);
				JLabel toolboxFolder=new JLabel("Toolbox");
				cellPanel.add(toolboxFolder);
				listModel.addElement(cellPanel);
			}else if(fileList[i].isDirectory()&&fileList[i].getName().endsWith(".cuo")){	//detect cuo project folder
				JPanel cellPanel=new JPanel(new FlowLayout(FlowLayout.LEFT));
				cellPanel.add(new JLabel(cuoProjectIcon));
				JLabel invisibleLabel=new JLabel(dirName);
				invisibleLabel.setVisible(false);
				cellPanel.add(invisibleLabel);
				JLabel invisibleLabel2=new JLabel("project");
				invisibleLabel2.setVisible(false);
				cellPanel.add(invisibleLabel2);
				String theName=dirName.split("\\"+File.separator)[dirName.split("\\"+File.separator).length-1];
				theName=theName.substring(0,theName.length()-4);	//get rid of ".cuo"
				JLabel folderName=new JLabel("<html><div style=width:"+(listWidth-10)+"px>"+theName+"</div></html>");
				cellPanel.add(folderName);
				listModel.addElement(cellPanel);
			}else if(fileList[i].isDirectory()){	//detect folder
				JPanel cellPanel=new JPanel(new FlowLayout(FlowLayout.LEFT));
				cellPanel.add(new JLabel(folderIcon));
				JLabel invisibleLabel=new JLabel(dirName);
				invisibleLabel.setVisible(false);
				cellPanel.add(invisibleLabel);
				JLabel invisibleLabel2=new JLabel("folder");
				invisibleLabel2.setVisible(false);
				cellPanel.add(invisibleLabel2);
				JLabel folderName=new JLabel("<html><div style=width:"+(listWidth-10)+"px>"+dirName.split("\\"+File.separator)[dirName.split("\\"+File.separator).length-1]+"</div></html>");
				cellPanel.add(folderName);
				listModel.addElement(cellPanel);
			}else if((dirName.contains("."+File.separator+"Library")||dirName.contains("."+File.separator+"Toolbox")||dirName.contains("."+File.separator+"Favourite"))&&(dirName.endsWith(".xml")||dirName.endsWith(".faa")||dirName.endsWith(".fna")||dirName.endsWith(".mfa"))){	//detect orgn and parse summary
				GenBank genBank=Common.getGenBank(dirName,library,toolbox,favourite);

				if(dirName.endsWith(".xml")){
					JPanel cellPanel=new JPanel(new FlowLayout(FlowLayout.LEFT));
					cellPanel.add(new JLabel(orgnIcon));
					JLabel invisibleLabel=new JLabel(dirName);	//for retrieving dirName properly
					invisibleLabel.setVisible(false);
					cellPanel.add(invisibleLabel);
					JLabel invisibleLabel2=new JLabel("orgn");	//for knowing is orgn
					invisibleLabel2.setVisible(false);
					cellPanel.add(invisibleLabel2);
					JLabel orgnName=new JLabel("<html><div style=width:"+listWidth+"px><font size=2 color=BLUE><u>"+genBank.name+"</u></font><br><font size=2>Length:"+genBank.seqLength+" Create:"+genBank.createDate+" Update:"+genBank.updateDate+"</font></div></html>");
					cellPanel.add(orgnName);
					listModel.addElement(cellPanel);
				}else if(dirName.endsWith(".mfa")){
					JPanel cellPanel=new JPanel(new FlowLayout(FlowLayout.LEFT));
					cellPanel.add(new JLabel(mfaIcon));
					JLabel invisibleLabel=new JLabel(dirName);	//for retrieving dirName properly
					invisibleLabel.setVisible(false);
					cellPanel.add(invisibleLabel);
					JLabel invisibleLabel2=new JLabel("orgn");	//for knowing is multiFasta
					invisibleLabel2.setVisible(false);
					cellPanel.add(invisibleLabel2);
					JLabel orgnName=new JLabel("<html><div style=width:"+listWidth+"px><font size=2 color=BLUE><u>"+genBank.name+"</u></font><br><font size=2>Length:"+genBank.seqLength+" Fragments:"+genBank.sequences.size()+"</font></div></html>");
					cellPanel.add(orgnName);
					listModel.addElement(cellPanel);
				}else if(dirName.endsWith(".faa")||dirName.endsWith(".fna")){
					JPanel cellPanel=new JPanel(new FlowLayout(FlowLayout.LEFT));
					if(dirName.endsWith(".faa")){
						cellPanel.add(new JLabel(faaIcon));
					}else{
						cellPanel.add(new JLabel(fnaIcon));
					}
					JLabel invisibleLabel=new JLabel(dirName);	//for retrieving dirName properly
					invisibleLabel.setVisible(false);
					cellPanel.add(invisibleLabel);
					JLabel invisibleLabel2=new JLabel("fasta");	//for knowing is fasta
					invisibleLabel2.setVisible(false);
					cellPanel.add(invisibleLabel2);
					JLabel orgnName;
					if(genBank.topology.equals("Restriction")){
						int topCut=Integer.parseInt(genBank.note.split("\\|")[0])-1;
						int bottomCut=Integer.parseInt(genBank.note.split("\\|")[1])-1;
						String templateStrand=Common.getOppositeDNAStrand(genBank.completeSequence);
						String siteString="<font face=courier>5'-"+genBank.completeSequence.substring(0,topCut)+"&nbsp;&nbsp;"+genBank.completeSequence.substring(topCut,genBank.completeSequence.length())+"-3'<br>3'-"+templateStrand.substring(0,bottomCut+1)+"&nbsp;&nbsp;"+templateStrand.substring(bottomCut+1,templateStrand.length())+"-5'</font>";

						orgnName=new JLabel("<html><div style=width:"+listWidth+"px><font size=2 color=BLUE><u>"+genBank.name+"</u></font><br><font size=2>"+siteString+"</font></div></html>");
					}else{
						orgnName=new JLabel("<html><div style=width:"+listWidth+"px><font size=2 color=BLUE><u>"+genBank.name+"</u></font><br><font size=2>Length:"+genBank.seqLength+"</font></div></html>");
					}
					cellPanel.add(orgnName);
					listModel.addElement(cellPanel);
				}
			}
			//unidentified files are not shown at all			
		}
		setModel(listModel);
	}

	public void listGenes(File genBankXML){
		currentDir=genBankXML;
		DefaultListModel listModel=new DefaultListModel();
		for(int i=0;i<library.size();i++){	//seek in library
			if(("."+File.separator+""+genBankXML.getPath().split("\\.\\"+File.separator+"")[genBankXML.getPath().split("\\.\\"+File.separator+"").length-1]).equals(library.get(i).dirName)){
				setModel(library.get(i).listModel);
				return;
			}
		}
		for(int i=0;i<toolbox.size();i++){	//seek in toolbox
			if(("."+File.separator+""+genBankXML.getPath().split("\\.\\"+File.separator+"")[genBankXML.getPath().split("\\.\\"+File.separator+"").length-1]).equals(toolbox.get(i).dirName)){
				setModel(toolbox.get(i).listModel);
				return;
			}
		}
		for(int i=0;i<favourite.size();i++){	//seek in favourite
			if(("."+File.separator+""+genBankXML.getPath().split("\\.\\"+File.separator+"")[genBankXML.getPath().split("\\.\\"+File.separator+"").length-1]).equals(favourite.get(i).dirName)){
				setModel(favourite.get(i).listModel);
				return;
			}
		}
	}

	public void relist(){
		if(!needToRelist)return;
		final DefaultListModel model=(DefaultListModel)getModel();
		for(int i=0;i<model.size();i++){
			JLabel theInfo=(JLabel)((JPanel)model.getElementAt(i)).getComponent(3);	//get the info showing JLabel
			String oldText=theInfo.getText();
			int startIndex=oldText.indexOf(":");
			int endIndex=oldText.indexOf("px");
			if(startIndex!=-1&&endIndex!=-1){
				String newText=oldText.substring(0,startIndex+1)+listWidth+oldText.substring(endIndex,oldText.length());
				theInfo.setText(newText);
			}
		}
		repaint();
	}
}
