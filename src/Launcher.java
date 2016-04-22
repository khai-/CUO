import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.LineBorder;

import Support.parser.Faa;
import Support.parser.Fna;
import Support.parser.GenBank;
import Support.parser.Mfa;

public class Launcher extends JFrame{
	Color panelColor=new Color(70,70,70);
	JPanel mainPanel;
	JLabel logoIcon;
	JLabel logoLabel;
	ImageIcon logo=new ImageIcon("logo.png");
	JLabel versionLabel;
	String version="";
	JLabel loadingLabel;
	JLabel loadingIcon;
	ImageIcon wheel_animated=new ImageIcon("Icons"+File.separator+"wheel_animated.gif");
	
	JProgressBar loadingBar;

	public Launcher(){
		createComponents();
		assignFunctions();
		layoutComponents();
		setupProperties();
		launch();
	}

	private void createComponents(){
		mainPanel=new JPanel();
		mainPanel.setBackground(panelColor);
		mainPanel.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.LIGHT_GRAY,4),new LineBorder(Color.GRAY,3)));
		logoIcon=new JLabel(logo);
		logoLabel=new JLabel("<html><font color=white><font size=6>C</font>odon <font size=6>U</font>sage <font size=6>O</font>ptimizer</font></html>");
		logoLabel.setHorizontalAlignment(JLabel.CENTER);
		versionLabel=new JLabel();
		String modification="";
		String temp;	//temporary String to store the readLine()
		try{
			BufferedReader versionBuffer=new BufferedReader(new FileReader("."+File.separator+"version"));
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
		versionLabel.setText(versionString);
		loadingLabel=new JLabel();
		loadingLabel.setForeground(Color.WHITE);
		loadingIcon=new JLabel(wheel_animated);
		loadingBar=new JProgressBar();
	}

	private void assignFunctions(){

	}

	private void layoutComponents(){
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.X_AXIS));
		//leftHandSide
		JPanel leftHandSide=new JPanel();
		leftHandSide.setOpaque(false);
		leftHandSide.setLayout(new BoxLayout(leftHandSide,BoxLayout.Y_AXIS));
		leftHandSide.add(logoIcon);
		leftHandSide.add(logoLabel);
		mainPanel.add(Box.createRigidArea(new Dimension(10,0)));
		mainPanel.add(leftHandSide);
		mainPanel.add(Box.createRigidArea(new Dimension(10,0)));

		//rightHandSide
		JPanel rightHandSide=new JPanel();
		rightHandSide.setOpaque(false);
		rightHandSide.setLayout(new BoxLayout(rightHandSide,BoxLayout.Y_AXIS));
		rightHandSide.add(Box.createRigidArea(new Dimension(0,20)));
		JPanel versionPanel=new JPanel();
		versionPanel.setOpaque(false);
		versionPanel.setLayout(new BoxLayout(versionPanel,BoxLayout.X_AXIS));
		versionPanel.add(Box.createHorizontalGlue());
		versionPanel.add(versionLabel);
		versionPanel.add(Box.createHorizontalGlue());
		rightHandSide.add(versionPanel);
		rightHandSide.add(Box.createVerticalGlue());
		JPanel loadingPanel=new JPanel();
		loadingPanel.setOpaque(false);
		loadingPanel.setLayout(new BoxLayout(loadingPanel,BoxLayout.X_AXIS));
		loadingPanel.add(loadingIcon);
		loadingPanel.add(loadingLabel);
		rightHandSide.add(loadingPanel);
		rightHandSide.add(loadingBar);
		rightHandSide.add(Box.createRigidArea(new Dimension(0,20)));
		mainPanel.add(rightHandSide);
		mainPanel.add(Box.createRigidArea(new Dimension(10,0)));

		add(mainPanel);
	}

	private void setupProperties(){
		setIconImage(Toolkit.getDefaultToolkit().getImage("logo.png"));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(550,300);
		setResizable(false);
		setUndecorated(true);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void launch(){
		//acquire amount of data need to be processed
		loadingLabel.setText("Counting data...");
		File currentDir=new File(".");
		File libraryFolder=null;
		File favouriteFolder=null;
		File toolboxFolder=null;
		File[] theFiles=currentDir.listFiles();
		for(int i=0;i<theFiles.length;i++){
			String fileName=theFiles[i].getName();
			if(fileName.equals("Library")){
				libraryFolder=theFiles[i];
			}else if(fileName.equals("Favourite")){
				favouriteFolder=theFiles[i];
			}else if(fileName.equals("Toolbox")){
				toolboxFolder=theFiles[i];
			}
		}

		File[] libraryFiles=loadFiles(libraryFolder);
		File[] favouriteFiles=loadFiles(favouriteFolder);;
		File[] toolboxFiles=loadFiles(toolboxFolder);
		int dataAmount=libraryFiles.length+favouriteFiles.length+toolboxFiles.length;
		loadingBar.setMaximum(dataAmount);

		//preload Library
		loadingLabel.setText("Loading library...");
		ArrayList<GenBank> libraryList=new ArrayList<GenBank>();
		for(int i=0;i<libraryFiles.length;i++){
			GenBank genBank;
			String fName=libraryFiles[i].getName().toLowerCase();
			if(fName.endsWith(".xml")){	//genBank
				genBank=new GenBank(libraryFiles[i].getPath());
				try{
					genBank.parseXML(libraryFiles[i]);
					genBank.generateListModel();
				}catch(Exception e){
					loadingBar.setValue(loadingBar.getValue()+1);
					continue;//fail to parse means the format is wrong then no need to do anything
				}
			}else if(fName.endsWith(".mfa")){	//MultiFasta
				Mfa mfa;
				try{
					mfa=new Mfa(libraryFiles[i].getPath());
				}catch(Exception e){
					loadingBar.setValue(loadingBar.getValue()+1);
					continue;
				}
				genBank=new GenBank(mfa);
				genBank.generateListModel();
			}else if(fName.endsWith(".faa")){	//FastaAA
				Faa faa;
				try{
					faa=new Faa(libraryFiles[i].getPath());
				}catch(Exception e){
					loadingBar.setValue(loadingBar.getValue()+1);
					continue;
				}
				genBank=new GenBank(faa);
				genBank.generateListModel();
			}else if(fName.endsWith(".fna")){
				Fna fna;
				try{
					fna=new Fna(libraryFiles[i].getPath());
				}catch(Exception e){
					loadingBar.setValue(loadingBar.getValue()+1);
					continue;
				}
				genBank=new GenBank(fna);
				genBank.generateListModel();
			}else{	//unknown file format
				loadingBar.setValue(loadingBar.getValue()+1);
				continue;
			}
			libraryList.add(genBank);
			loadingBar.setValue(loadingBar.getValue()+1);
		}

		//preload Toolbox
		loadingLabel.setText("Loading toolbox...");
		ArrayList<GenBank> toolboxList=new ArrayList<GenBank>();
		for(int i=0;i<toolboxFiles.length;i++){
			GenBank genBank;
			String fName=toolboxFiles[i].getName().toLowerCase();
			if(fName.endsWith(".xml")){	//genBank
				genBank=new GenBank(toolboxFiles[i].getPath());
				try{
					genBank.parseXML(toolboxFiles[i]);
					genBank.generateListModel();
				}catch(Exception e){
					loadingBar.setValue(loadingBar.getValue()+1);
					continue;//fail to parse means the format is wrong then no need to do anything
				}
			}else if(fName.endsWith(".mfa")){	//MultiFasta
				Mfa mfa;
				try{
					mfa=new Mfa(libraryFiles[i].getPath());
				}catch(Exception e){
					loadingBar.setValue(loadingBar.getValue()+1);
					continue;
				}
				genBank=new GenBank(mfa);
				genBank.generateListModel();
			}else if(fName.endsWith(".faa")){	//FastaAA
				Faa faa;
				try{
					faa=new Faa(toolboxFiles[i].getPath());
				}catch(Exception e){
					loadingBar.setValue(loadingBar.getValue()+1);
					continue;
				}
				genBank=new GenBank(faa);
				genBank.generateListModel();
			}else if(fName.endsWith(".fna")){
				Fna fna;
				try{
					fna=new Fna(toolboxFiles[i].getPath());
				}catch(Exception e){
					loadingBar.setValue(loadingBar.getValue()+1);
					continue;
				}
				genBank=new GenBank(fna);
				genBank.generateListModel();
			}else{	//unknown file format
				loadingBar.setValue(loadingBar.getValue()+1);
				continue;
			}
			toolboxList.add(genBank);
			loadingBar.setValue(loadingBar.getValue()+1);
		}

		//preload Favourite
		loadingLabel.setText("Loading favourite...");
		ArrayList<GenBank> favouriteList=new ArrayList<GenBank>();
		for(int i=0;i<favouriteFiles.length;i++){
			GenBank genBank;
			String fName=favouriteFiles[i].getName().toLowerCase();
			if(fName.endsWith(".xml")){	//genBank
				genBank=new GenBank(favouriteFiles[i].getPath());
				try{
					genBank.parseXML(favouriteFiles[i]);
					genBank.generateListModel();
				}catch(Exception e){
					loadingBar.setValue(loadingBar.getValue()+1);
					continue;//fail to parse means the format is wrong then no need to do anything
				}
			}else if(fName.endsWith(".mfa")){	//MultiFasta
				Mfa mfa;
				try{
					mfa=new Mfa(favouriteFiles[i].getPath());
				}catch(Exception e){
					loadingBar.setValue(loadingBar.getValue()+1);
					continue;
				}
				genBank=new GenBank(mfa);
				genBank.generateListModel();
			}else if(fName.endsWith(".faa")){	//FastaAA
				Faa faa;
				try{
					faa=new Faa(favouriteFiles[i].getPath());
				}catch(Exception e){
					loadingBar.setValue(loadingBar.getValue()+1);
					continue;
				}
				genBank=new GenBank(faa);
				genBank.generateListModel();
			}else if(fName.endsWith(".fna")){
				Fna fna;
				try{
					fna=new Fna(favouriteFiles[i].getPath());
				}catch(Exception e){
					loadingBar.setValue(loadingBar.getValue()+1);
					continue;
				}
				genBank=new GenBank(fna);
				genBank.generateListModel();
			}else{	//unknown file format
				loadingBar.setValue(loadingBar.getValue()+1);
				continue;
			}
			favouriteList.add(genBank);
			loadingBar.setValue(loadingBar.getValue()+1);
		}

		//passing resources to CUO and cleaning up
		new CUO(libraryList,toolboxList,favouriteList);
		dispose();
	}

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
				if(fName.endsWith(".xml")||fName.endsWith(".faa")||fName.endsWith(".fna")||fName.endsWith(".mfa")){
					fList.add(theFiles[i]);
				}
			}
		}
		return fList.toArray(new File[fList.size()]);
	}

	public static void main(String[] args){
		new Launcher();
	}
}
