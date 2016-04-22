package Tools.Updater;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.regex.Matcher;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;

import Support.Common;
import Support.CustomCellRenderer;

public class Updater extends JDialog{
	Updater UPDATER=this;
	JFrame ownerFrame;
	Color bgColor=new Color(70,70,70);

	Thread currentThread;	//currently working thread

	//control
	JButton refreshButton;	//reload the update list
	ImageIcon refreshIcon_up=new ImageIcon("Icons/refreshBig_up.gif");
	ImageIcon refreshIcon_down=new ImageIcon("Icons/refreshBig_down.gif");
	JButton historyButton;	//show the update history (allow revert ability)
	ImageIcon historyIcon_up=new ImageIcon("Icons/historyBig_up.gif");
	ImageIcon historyIcon_down=new ImageIcon("Icons/historyBig_down.gif");

	//update list
	JList updateList;
	DefaultListModel updateModel;
	JScrollPane updateScroll;

	//status
	JLabel updateStatusIcon;
	ImageIcon wheelStatic=new ImageIcon("Icons/wheel.gif");
	ImageIcon wheelMove=new ImageIcon("Icons/wheel_animated.gif");
	JLabel updateStatusLabel;

	//progress bar
	JProgressBar updateProgressBar;

	//buttons
	JButton updateButton;
	JButton cancelButton;

	public Updater(JFrame ownerFrame){
		super(ownerFrame,false);
		this.ownerFrame=ownerFrame;

		createComponents();
		assignFunctions();
		layoutComponents();
		setupProperties();

		refresh();
	}

	private void createComponents(){
		//control
		refreshButton=new JButton(refreshIcon_up);
		refreshButton.setBorderPainted(false);
		refreshButton.setContentAreaFilled(false);
		refreshButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		historyButton=new JButton(historyIcon_up);
		historyButton.setBorderPainted(false);
		historyButton.setContentAreaFilled(false);
		historyButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));

		//update list
		updateList=new JList();
		updateModel=new DefaultListModel();
		updateList.setModel(updateModel);
		updateList.setCellRenderer(new CustomCellRenderer());
		updateList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		updateScroll=new JScrollPane(updateList,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		//status
		updateStatusIcon=new JLabel(wheelStatic);
		updateStatusLabel=new JLabel();
		updateStatusLabel.setForeground(Color.WHITE);

		//progress bar
		updateProgressBar=new JProgressBar();

		//buttons
		updateButton=new JButton("Update");
		cancelButton=new JButton("Cancel");
	}

	private void assignFunctions(){
		//refreshButton
		refreshButton.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				refreshButton.setIcon(refreshIcon_down);
			}
			public void mouseReleased(MouseEvent me){
				refresh();
				refreshButton.setIcon(refreshIcon_up);
			}
		});

		//historyButton
		historyButton.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				historyButton.setIcon(historyIcon_down);
			}
			public void mouseReleased(MouseEvent me){
				historyButton.setIcon(historyIcon_up);
			}
		});

		//updateButton
		updateButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				update();
			}
		});

		//cancelButton
		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				currentThread.interrupt();
				dispose();
			}
		});
	}

	private void layoutComponents(){
		JPanel mainPanel=new JPanel();
		mainPanel.setBackground(bgColor);
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5,10,10,10));

		JPanel controlPanel=new JPanel();
		controlPanel.setOpaque(false);
		controlPanel.setLayout(new BoxLayout(controlPanel,BoxLayout.X_AXIS));
		controlPanel.add(refreshButton);
		controlPanel.add(historyButton);
		controlPanel.add(Box.createHorizontalGlue());
		mainPanel.add(controlPanel);

		mainPanel.add(updateScroll);

		JPanel statusPanel=new JPanel();
		statusPanel.setOpaque(false);
		statusPanel.setLayout(new BoxLayout(statusPanel,BoxLayout.X_AXIS));
		statusPanel.add(updateStatusIcon);
		statusPanel.add(updateStatusLabel);
		mainPanel.add(statusPanel);

		mainPanel.add(updateProgressBar);
		mainPanel.add(Box.createRigidArea(new Dimension(0,5)));

		JPanel buttonPanel=new JPanel();
		buttonPanel.setOpaque(false);
		buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.X_AXIS));
		buttonPanel.add(updateButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(5,0)));
		buttonPanel.add(cancelButton);
		mainPanel.add(buttonPanel);

		add(mainPanel);
	}

	private void setupProperties(){
		setTitle("Updater");
		setSize(400,350);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	private boolean ping(){
		updateStatusIcon.setIcon(wheelMove);
		updateStatusLabel.setText("Connecting...");
		if(!Common.ping("http://codonusageoptimizer.org")){
			updateStatusIcon.setIcon(wheelStatic);
			updateStatusLabel.setText("Connection failure");
			return false;
		}else{
			return true;
		}
	}

	private File getUpdateList(){
		updateStatusLabel.setText("Getting update list...");
		File updateFile=Common.saveTempURL("http://codonusageoptimizer.org/update/updateList");
		if(updateFile==null){
			updateStatusIcon.setIcon(wheelStatic);
			updateStatusLabel.setText("Fail to get update list");
			return null;
		}else{
			return updateFile;
		}
	}

	private ArrayList<String[]> compareUpdateListAndHistory(File updateFile){
		updateStatusLabel.setText("Reading history...");

		ArrayList<String[]> updateList=new ArrayList<String[]>();
		ArrayList<String[]> historyList=new ArrayList<String[]>();

		try{
			//read and parse updateList
			FileInputStream updateFis=new FileInputStream(updateFile);
			DataInputStream updateDis=new DataInputStream(updateFis);
			BufferedReader updateBr=new BufferedReader(new InputStreamReader(updateDis));
			String updateLine;
			while((updateLine=updateBr.readLine())!=null){
				updateList.add(updateLine.split(";"));
			}
			updateFis.close();
			updateDis.close();
			updateBr.close();

			//read and parse history
			FileInputStream historyFis=new FileInputStream("history");
			DataInputStream historyDis=new DataInputStream(historyFis);
			BufferedReader historyBr=new BufferedReader(new InputStreamReader(historyDis));
			String historyLine;
			while((historyLine=historyBr.readLine())!=null){
				historyList.add(historyLine.split(";"));
			}
			historyFis.close();
			historyDis.close();
			historyBr.close();
		}catch(Exception e){
			updateStatusIcon.setIcon(wheelStatic);
			updateStatusLabel.setText("Fail to read history");
			return null;
		}

		//compare
		int latestHistory=-1;
		if(historyList.size()!=0&&updateList.size()!=0){
			for(int i=0;i<updateList.size();i++){
				if(updateList.get(i)[0].equals(historyList.get(historyList.size()-1)[0])){	//only last one in history will be checked
					latestHistory=i;
				}
			}
		}

		ArrayList<String[]> returnList=new ArrayList<String[]>();
		for(int i=latestHistory+1;i<updateList.size();i++){
			returnList.add(updateList.get(i));
		}
		return returnList;
	}

	private void showUpdates(ArrayList<String[]> listToUpdate){
		updateStatusLabel.setText("Showing recent updates..");
		updateModel.clear();
		for(int i=0;i<listToUpdate.size();i++){
			String name=listToUpdate.get(i)[1];
			String description=listToUpdate.get(i)[2];
			String date=listToUpdate.get(i)[3];

			JLabel newUpdateLabel=new JLabel("<html><div style=width:380;><b><u><font color=BLUE>"+name+"</font></b></u> ("+date+")<br>"+description+"</div></html>");
			newUpdateLabel.setOpaque(true);
			newUpdateLabel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
			updateModel.addElement(newUpdateLabel);
		}
	}

	private boolean updatePackage(String[] thePackage){
		updateStatusLabel.setText("Updating "+thePackage[1]+"...");

		for(int i=0;i<Integer.parseInt(thePackage[4]);i++){	//for each subpackages
			//get and parse instruction file
			try{
				URI uri=new URI("http","codonusageoptimizer.org","/update/"+thePackage[1]+"/"+(i+1)+"/instruction",null);	//parse the address into friendly form
				File instructionFile=Common.saveTempURL(uri.toURL());
				if(instructionFile==null){
					updateStatusIcon.setIcon(wheelStatic);
					updateStatusLabel.setText("Fail to update "+thePackage[1]);
					return false;	//do not go to next subpackage to avoid old classes override
				}

				ArrayList<String[]> instruction=new ArrayList<String[]>();
				//read and parse instruction
				FileInputStream instructionFis=new FileInputStream(instructionFile);
				DataInputStream instructionDis=new DataInputStream(instructionFis);
				BufferedReader instructionBr=new BufferedReader(new InputStreamReader(instructionDis));
				String instructionLine;
				while((instructionLine=instructionBr.readLine())!=null){
					instruction.add(instructionLine.split(";"));
				}
				instructionFis.close();
				instructionDis.close();
				instructionBr.close();

				//download all the files
				File[] downloadedFiles=new File[instruction.get(0).length-1];//paste directory;file;file;...
				new File("."+File.separator+"temp").mkdir();	//make the temp folder for storing downloaded files
				for(int j=0;j<downloadedFiles.length;j++){
					URI theUri=new URI("http","codonusageoptimizer.org","/update/"+thePackage[1]+"/"+(i+1)+"/"+instruction.get(0)[j+1],null);
					String theDirectory=instruction.get(0)[0].replaceAll("\\.",Matcher.quoteReplacement(File.separator))+File.separator;
					if(theDirectory.equals(File.separator+File.separator)){
						theDirectory="";
					}else{
						new File("temp"+File.separator+theDirectory).mkdirs();
					}
					downloadedFiles[j]=Common.saveURL(theUri.toURL(),"temp"+File.separator+theDirectory+instruction.get(0)[j+1]);
					if(downloadedFiles[j]==null){
						updateStatusIcon.setIcon(wheelStatic);
						updateStatusLabel.setText("Fail to download "+thePackage[1]);
						return false;
					}
				}

				//replace the local files with downloaded files
				String theList="";
				for(int j=0;j<downloadedFiles.length;j++){
					if(j==downloadedFiles.length-1){	//last one
						theList+="-C temp "+instruction.get(0)[0].replaceAll("\\.",Matcher.quoteReplacement(File.separator))+File.separator+downloadedFiles[j].getName();
					}else{
						theList+="-C temp "+instruction.get(0)[0].replaceAll("\\.",Matcher.quoteReplacement(File.separator))+File.separator+downloadedFiles[j].getName()+" ";
					}
				}
				try{
					Runtime.getRuntime().exec("jar uf Launch.jar "+theList);
					Thread.currentThread().sleep(2000);	//wait for the exec to start off
				}catch(IOException ioe){
ioe.printStackTrace();
					updateStatusIcon.setIcon(wheelStatic);
					updateStatusLabel.setText("Fail to replace "+thePackage[1]);	
					return false;
				}

				//wait for completing application of jar files
				boolean applyingUpdate=true;
				while(applyingUpdate){
					applyingUpdate=false;
					String[] rootFiles=new File(".").list();
					for(int k=0;k<rootFiles.length;k++){
						if(rootFiles[k].startsWith("jartmp")&&rootFiles[k].endsWith(".tmp")){
							applyingUpdate=true;
							break;
						}
					}
					if(!applyingUpdate)break;
					Thread.currentThread().sleep(1500);	//1.5secs timer
				}
			}catch(Exception e){
				updateStatusIcon.setIcon(wheelStatic);
				updateStatusLabel.setText("Fail to update "+thePackage[1]);
				return false;
			}
		}
		return true;
	}

	private boolean recordHistory(String[] newHistory){
		updateStatusLabel.setText("Recording history...");

		//make the line to insert
		String theLine="";
		for(int i=0;i<newHistory.length;i++){
			if(i==newHistory.length-1){	//last one
				theLine+=newHistory[i];
			}else{
				theLine+=newHistory[i]+";";
			}
		}

		//insert to line to history file
		File historyFile=new File("history");
		if(!Common.writeToFile(historyFile,theLine+"\n")){	//if fail
			//revert back to previous files???


			updateStatusIcon.setIcon(wheelStatic);
			updateStatusLabel.setText("Fail to record history of "+newHistory[1]);
			return false;
		}
		return true;
	}

	private void refresh(){
		if(currentThread!=null)currentThread.interrupt();
		currentThread=new Thread(){
			public void run(){
				//ping the site
				if(Thread.currentThread().isInterrupted())return;
				if(!ping())return;

				//get updateList
				if(Thread.currentThread().isInterrupted())return;
				File updateFile=getUpdateList();
				if(updateFile==null)return;

				//compare updateList and history
				if(Thread.currentThread().isInterrupted())return;
				ArrayList<String[]> listToUpdate=compareUpdateListAndHistory(updateFile);	
				if(listToUpdate==null)return;

				//show recent applicable updates
				if(Thread.currentThread().isInterrupted())return;
				showUpdates(listToUpdate);

				updateStatusIcon.setIcon(wheelStatic);
				updateStatusLabel.setText("Refresh complete");
			}
		};currentThread.start();
	}

	private void update(){
		if(currentThread!=null)currentThread.interrupt();
		currentThread=new Thread(){
			public void run(){
				//ping the site
				if(Thread.currentThread().isInterrupted())return;
				if(!ping())return;

				//get updateList
				if(Thread.currentThread().isInterrupted())return;
				File updateFile=getUpdateList();
				if(updateFile==null)return;

				//compare updateList and history
				if(Thread.currentThread().isInterrupted())return;
				ArrayList<String[]> listToUpdate=compareUpdateListAndHistory(updateFile);	
				if(listToUpdate==null)return;

				//show recent applicable updates
				if(Thread.currentThread().isInterrupted())return;
				showUpdates(listToUpdate);

				//get the files to update
				if(Thread.currentThread().isInterrupted())return;
				for(int i=0;i<listToUpdate.size();i++){
					if(!updatePackage(listToUpdate.get(i)))return;	//do not continue if fail to update this package
					//remove completed package from the update list
					updateModel.remove(i);
					//record in history
					if(!recordHistory(listToUpdate.get(i)))return;	//do not continue if fail to record
				}

				//delete the downloaded files in local temp folder
				File theTemp=new File("temp");
				if(theTemp.exists()){
					Common.deleteFolder(theTemp);
				}

				updateStatusIcon.setIcon(wheelStatic);
				updateStatusLabel.setText("Update complete");

				//ask for restart
				String theMessage="";
				String theButtonMessage="";
				if(ownerFrame==null){	//externally run
					theMessage="Update completed. Run the program?";
					theButtonMessage="Launch";
				}else{
					theMessage="The program needs restart for updates to take effect.";
					theButtonMessage="Restart now";
				}
				int reply=JOptionPane.showOptionDialog(UPDATER,theMessage,"",JOptionPane.YES_NO_OPTION,JOptionPane.PLAIN_MESSAGE,null,new String[]{theButtonMessage,"Nevermind"},theButtonMessage);
				if(reply==JOptionPane.YES_OPTION){
					try{
						Common.restartApplication(null);//restart
					}catch(Exception e){}
				}
			}
		};currentThread.start();
	}

	public static void main(String[] args){
		new Updater(null);
	}
}
