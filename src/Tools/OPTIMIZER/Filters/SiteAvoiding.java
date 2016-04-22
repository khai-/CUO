package Tools.OPTIMIZER.Filters;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import Tools.OPTIMIZER.Optimizer;

public class SiteAvoiding extends Filter{
	final ArrayList<String[]> sitesList=new ArrayList<String[]>();//one coding one template in String[2]

	public SiteAvoiding(final Optimizer optimizer,int processNumber){
		super(optimizer,processNumber);
		filterName="Site Avoiding";
		filterLabel.setText("Site Avoiding");

		//configureDialog
		final JDialog configureDialog=new JDialog(optimizer.OPTIMIZER);
		JLabel sitesSavedLabel=new JLabel("Sites Saved:");	//sites Saved
		sitesSavedLabel.setForeground(Color.WHITE);
		final DefaultListModel sitesSavedModel=new DefaultListModel();
		JList sitesSavedList=new JList(sitesSavedModel);
		sitesSavedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		final JScrollPane sitesSavedScroll=new JScrollPane(sitesSavedList,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		final ImageIcon addSite_up=new ImageIcon("Icons"+File.separator+"add_up.gif");
		final ImageIcon addSite_down=new ImageIcon("Icons"+File.separator+"add_down.gif");
		final JButton addSiteButton=new JButton(addSite_up);	//addSiteButton
		addSiteButton.setContentAreaFilled(false);
		addSiteButton.setBorderPainted(false);
		addSiteButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		JLabel sitesInUseLabel=new JLabel("Sites In Use:");	//sites in use
		sitesInUseLabel.setForeground(Color.WHITE);
		final DefaultListModel sitesInUseModel=new DefaultListModel();
		final JList sitesInUseList=new JList(sitesInUseModel);
		sitesInUseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		final JScrollPane sitesInUseScroll=new JScrollPane(sitesInUseList,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		JLabel siteNameLabel=new JLabel("Name:");	//new site creation
		siteNameLabel.setForeground(Color.WHITE);
		final JTextField siteNameText=new JTextField();
		siteNameText.setMinimumSize(new Dimension(200,25));
		siteNameText.setMaximumSize(new Dimension(100000,25));
		JLabel codingLabel=new JLabel("Coding strand:");
		codingLabel.setForeground(Color.WHITE);
		codingLabel.setHorizontalAlignment(JLabel.LEFT);
		final JTextField codingText=new JTextField();
		codingText.setMinimumSize(new Dimension(200,25));
		codingText.setMaximumSize(new Dimension(100000,25));
		JLabel templateLabel=new JLabel("Template strand:");
		templateLabel.setForeground(Color.WHITE);
		templateLabel.setHorizontalAlignment(JLabel.LEFT);
		final JTextField templateText=new JTextField();
		templateText.setMinimumSize(new Dimension(200,25));
		templateText.setMaximumSize(new Dimension(100000,25));
		JButton addButton=new JButton("Add");
		JButton saveButton=new JButton("Save");
		sitesInUseList.addKeyListener(new KeyAdapter(){
			public void keyReleased(KeyEvent ke){
				if(ke.getKeyCode()==KeyEvent.VK_DELETE){
					int index=sitesInUseList.getSelectedIndex();
					sitesInUseModel.remove(index);
					sitesList.remove(index);

					//update status
					if(sitesInUseModel.getSize()!=0){
						isConfigured=true;
						optimizer.updateStatus();
					}else{
						isConfigured=false;
						optimizer.updateStatus();
					}
				}
			}
		});
		addSiteButton.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				addSiteButton.setIcon(addSite_down);
			}
			public void mouseReleased(MouseEvent me){
				if(sitesInUseModel.getSize()!=0){
					isConfigured=true;
					optimizer.updateStatus();
				}else{
					isConfigured=false;
					optimizer.updateStatus();
				}
				addSiteButton.setIcon(addSite_up);
			}
		});
		addButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				//check validity
				if(siteNameText.getText().trim().equals("")||codingText.getText().trim().equals("")||templateText.getText().trim().equals("")){	//no name
					Toolkit.getDefaultToolkit().beep();
					JOptionPane.showMessageDialog(configureDialog,"Insert a name, a coding strand and a template strand.","Fill",JOptionPane.PLAIN_MESSAGE);
					return;
				}
				codingText.setText(codingText.getText().toUpperCase());
				templateText.setText(templateText.getText().toUpperCase());
				String coding=codingText.getText();
				String template=templateText.getText();
				if(coding.length()!=template.length()){
					Toolkit.getDefaultToolkit().beep();
					JOptionPane.showMessageDialog(configureDialog,"Coding strand and template strand have unequal length.","Error",JOptionPane.PLAIN_MESSAGE);
					return;
				}
				for(int i=0;i<coding.length();i++){	//coding strand
					char theChar=coding.charAt(i);
					if(theChar!='A'&&theChar!='T'&&theChar!='G'&&theChar!='C'&&theChar!='N'){
						Toolkit.getDefaultToolkit().beep();
						JOptionPane.showMessageDialog(configureDialog,"In coding strand, use only A,T,G,C or N.","Error",JOptionPane.PLAIN_MESSAGE);
						return;
					}
				}
				for(int i=0;i<template.length();i++){	//template strand
					char theChar=template.charAt(i);
					if(theChar!='A'&&theChar!='T'&&theChar!='G'&&theChar!='C'&&theChar!='N'){
						Toolkit.getDefaultToolkit().beep();
						JOptionPane.showMessageDialog(configureDialog,"In template strand, use only A,T,G,C or N.","Error",JOptionPane.PLAIN_MESSAGE);
						return;
					}
				}

				sitesInUseModel.addElement(siteNameText.getText());//update sitesInUseList
				//add new site to sitesList
				String[] theString=new String[2];
				theString[0]=codingText.getText();
				theString[1]=templateText.getText();
				sitesList.add(theString);

				//updateStatus
				isConfigured=true;
				optimizer.updateStatus();
			}
		});
		JPanel mainPanel=new JPanel();
		mainPanel.setBackground(bgColor);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.X_AXIS));
		JPanel leftPanel=new JPanel();
		leftPanel.setOpaque(false);
		leftPanel.setLayout(new BoxLayout(leftPanel,BoxLayout.Y_AXIS));
		leftPanel.add(sitesSavedLabel);
		leftPanel.add(sitesSavedScroll);
		mainPanel.add(leftPanel);
		mainPanel.add(Box.createRigidArea(new Dimension(5,0)));
		mainPanel.add(addSiteButton);
		mainPanel.add(Box.createRigidArea(new Dimension(5,0)));
		JPanel midPanel=new JPanel();
		midPanel.setOpaque(false);
		midPanel.setLayout(new BoxLayout(midPanel,BoxLayout.Y_AXIS));
		midPanel.add(sitesInUseLabel);
		midPanel.add(sitesInUseScroll);
		mainPanel.add(midPanel);
		mainPanel.add(Box.createRigidArea(new Dimension(5,0)));
		JPanel rightPanel=new JPanel();
		rightPanel.setOpaque(false);
		rightPanel.setLayout(new BoxLayout(rightPanel,BoxLayout.Y_AXIS));
		JPanel siteNamePanel=new JPanel();
		siteNamePanel.setOpaque(false);
		siteNamePanel.setLayout(new BoxLayout(siteNamePanel,BoxLayout.X_AXIS));
		siteNamePanel.add(siteNameLabel);
		siteNamePanel.add(siteNameText);
		rightPanel.add(Box.createRigidArea(new Dimension(0,20)));
		rightPanel.add(siteNamePanel);
		rightPanel.add(Box.createRigidArea(new Dimension(0,20)));
		JPanel codingPanel=new JPanel();
		codingPanel.setOpaque(false);
		codingPanel.setLayout(new BoxLayout(codingPanel,BoxLayout.X_AXIS));
		codingPanel.add(codingLabel);
		rightPanel.add(codingPanel);
		rightPanel.add(codingText);
		rightPanel.add(Box.createRigidArea(new Dimension(0,10)));
		JPanel templatePanel=new JPanel();
		templatePanel.setOpaque(false);
		templatePanel.setLayout(new BoxLayout(templatePanel,BoxLayout.X_AXIS));
		templatePanel.add(templateLabel);
		rightPanel.add(templatePanel);
		rightPanel.add(templateText);
		rightPanel.add(Box.createVerticalGlue());
		JPanel rightBottomPanel=new JPanel();
		rightBottomPanel.setOpaque(false);
		rightBottomPanel.setLayout(new BoxLayout(rightBottomPanel,BoxLayout.X_AXIS));
		rightBottomPanel.add(Box.createHorizontalGlue());
		rightBottomPanel.add(addButton);
		rightBottomPanel.add(Box.createRigidArea(new Dimension(10,0)));
		rightBottomPanel.add(saveButton);
		rightPanel.add(rightBottomPanel);
		mainPanel.add(rightPanel);
		configureDialog.add(mainPanel);
		configureDialog.setTitle("SiteAvoiding Configuration");
		configureDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		configureDialog.setSize(600,350);
		configureDialog.setResizable(false);
		configureDialog.setLocationRelativeTo(null);
		configureDialog.setVisible(false);

		//assignFunctions
		filterConfigure.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				configureDialog.setVisible(true);
			}
		});
	}

	public boolean trigger(){
		String[] codingArray=optimizer.resultInProcess.resultSequence.getTranscript(optimizer.resultInProcess.resultSequence.genBank.completeSequence);
		String coding="";
		for(int i=0;i<codingArray.length;i++){
			coding+=codingArray[i];
		}
		String[] templateArray;
		for(int i=0;i<sitesList.size();i++){
			if(coding.contains(sitesList.get(i)[0])){
				return false;
			}
		}

		return true;
	}

	public static String getDescription(){
		return "Avoid certain sites in optimized results. For example, enzyme restriction site, primer site, recombination site, ribosomal entry site and terminator.";
	}
}
