package Tools.Translator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import Support.Common;

public class Translator extends JDialog{
	Translator translator=this;
	Color bgColor=new Color(70,70,70);
	Font theFont=new Font(null,Font.PLAIN,9);

	//left sequence area
	//leftButtonsPanel
	//leftSequencePane
	JTextPane leftSequencePane;
	JScrollPane leftSequenceScroll;

	//operation buttons
	JButton translateButton;
	ImageIcon translateRight_up=new ImageIcon("Icons"+File.separator+"translateRight_up.gif");
	ImageIcon translateRight_down=new ImageIcon("Icons"+File.separator+"translateRight_down.gif");
	ImageIcon translateLeft_up=new ImageIcon("Icons"+File.separator+"translateLeft_up.gif");
	ImageIcon translateLeft_down=new ImageIcon("Icons"+File.separator+"translateLeft_down.gif");
	JButton transcribeButton;
	ImageIcon transcribeRight_up=new ImageIcon("Icons"+File.separator+"transcribeRight_up.gif");
	ImageIcon transcribeRight_down=new ImageIcon("Icons"+File.separator+"transcribeRight_down.gif");
	ImageIcon transcribeLeft_up=new ImageIcon("Icons"+File.separator+"transcribeLeft_up.gif");
	ImageIcon transcribeLeft_down=new ImageIcon("Icons"+File.separator+"transcribeLeft_down.gif");
	JButton complementaryButton;	//get the complementary strand
	ImageIcon complementaryRight_up=new ImageIcon("Icons"+File.separator+"complementaryRight_up.gif");
	ImageIcon complementaryRight_down=new ImageIcon("Icons"+File.separator+"complementaryRight_down.gif");
	ImageIcon complementaryLeft_up=new ImageIcon("Icons"+File.separator+"complementaryLeft_up.gif");
	ImageIcon complementaryLeft_down=new ImageIcon("Icons"+File.separator+"complementaryLeft_down.gif");

	//right sequence area
	//rightButtonsPanel
	//rightSequencePane
	JTextPane rightSequencePane;
	JScrollPane rightSequenceScroll;

	public Translator(JFrame ownerFrame){
		super(ownerFrame,false);
		initialize();
	}
	public Translator(JDialog ownerFrame){
		super(ownerFrame,false);
		initialize();
	}

	private void initialize(){
		createComponents();
		assignFunctions();
		layoutComponents();
		setupProperties();
	}

	private void createComponents(){
		//left sequence area
		//leftButtonsPanel
		//leftSequencePane
		leftSequencePane=new JTextPane();
		leftSequenceScroll=new JScrollPane(leftSequencePane,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		//operation buttons
		translateButton=new JButton(translateRight_up);
		translateButton.setToolTipText("Translate");
		translateButton.setFont(theFont);
		translateButton.setContentAreaFilled(false);
		translateButton.setBorderPainted(false);
		translateButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		transcribeButton=new JButton(transcribeRight_up);
		transcribeButton.setToolTipText("Transcribe");
		transcribeButton.setContentAreaFilled(false);
		transcribeButton.setBorderPainted(false);
		transcribeButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		complementaryButton=new JButton(complementaryRight_up);
		complementaryButton.setToolTipText("Complementary");
		complementaryButton.setContentAreaFilled(false);
		complementaryButton.setBorderPainted(false);
		complementaryButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));

		//right sequence area
		//rightButtonsPanel
		//rightSequencePane
		rightSequencePane=new JTextPane();
		rightSequenceScroll=new JScrollPane(rightSequencePane,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	}

	private void assignFunctions(){
		//translateButton
		translateButton.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				if(translateButton.getIcon().equals(translateRight_up))translateButton.setIcon(translateRight_down);
				else if(translateButton.getIcon().equals(translateLeft_up))translateButton.setIcon(translateLeft_down);
			}
			public void mouseReleased(MouseEvent me){
				String direction="";
				if(translateButton.getIcon().equals(translateRight_down))direction="right";
				else if(translateButton.getIcon().equals(translateLeft_down))direction="left";
				//get input from correct side
				String naSequence="";
				if(direction.equals("right"))naSequence=leftSequencePane.getText();
				else if(direction.equals("left"))naSequence=rightSequencePane.getText();
				//validate input
				if(!Common.isNA(naSequence)){
					JOptionPane.showMessageDialog(translator,"Translate is only applicable to A,T,G,C,U only.","Illegal letter",JOptionPane.ERROR_MESSAGE);
					if(direction.equals("right"))translateButton.setIcon(translateRight_up);
					else if(direction.equals("left"))translateButton.setIcon(translateLeft_up);
					return;
				}
				//translate
				String aaSequence=translate(naSequence);
				//present output
				if(direction.equals("right"))rightSequencePane.setText(aaSequence);
				else if(direction.equals("left"))leftSequencePane.setText(aaSequence);

				if(direction.equals("right"))translateButton.setIcon(translateRight_up);
				else if(direction.equals("left"))translateButton.setIcon(translateLeft_up);
			}
		});

		//transcribeButton
		transcribeButton.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				if(transcribeButton.getIcon().equals(transcribeRight_up))transcribeButton.setIcon(transcribeRight_down);
				else if(transcribeButton.getIcon().equals(transcribeLeft_up))transcribeButton.setIcon(transcribeLeft_down);
			}
			public void mouseReleased(MouseEvent me){
				String direction="";
				if(transcribeButton.getIcon().equals(transcribeRight_down))direction="right";
				else if(transcribeButton.getIcon().equals(transcribeLeft_down))direction="left";
				//get input from correct side
				String aaSequence="";
				if(direction.equals("right"))aaSequence=leftSequencePane.getText();
				else if(direction.equals("left"))aaSequence=rightSequencePane.getText();
				//validate input
				if(!Common.isAA(aaSequence)){
					JOptionPane.showMessageDialog(translator,"Transcribe is only applicable to single letter amino acid code only.","Illegal letter",JOptionPane.ERROR_MESSAGE);
					if(direction.equals("right"))transcribeButton.setIcon(transcribeRight_up);
					else if(direction.equals("left"))transcribeButton.setIcon(transcribeLeft_up);
					return;
				}
				//transcribe
				String naSequence=transcribe(aaSequence);
				//present output
				if(direction.equals("right"))rightSequencePane.setText(naSequence);
				else if(direction.equals("left"))leftSequencePane.setText(naSequence);

				if(direction.equals("right"))transcribeButton.setIcon(transcribeRight_up);
				else if(direction.equals("left"))transcribeButton.setIcon(transcribeLeft_up);
			}
		});

		//complementaryButton
		complementaryButton.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				if(complementaryButton.getIcon().equals(complementaryRight_up))complementaryButton.setIcon(complementaryRight_down);
				else if(complementaryButton.getIcon().equals(complementaryLeft_up))complementaryButton.setIcon(complementaryLeft_down);
			}
			public void mouseReleased(MouseEvent me){
				String direction="";
				if(complementaryButton.getIcon().equals(complementaryRight_down))direction="right";
				else if(complementaryButton.getIcon().equals(complementaryLeft_down))direction="left";
				//get input from correct side
				String naSequence="";
				if(direction.equals("right"))naSequence=leftSequencePane.getText();
				else if(direction.equals("left"))naSequence=rightSequencePane.getText();
				//validate input
				if(!Common.isNA(naSequence)){
					JOptionPane.showMessageDialog(translator,"Complementary is only applicable to A,T,G,C,U only.","Illegal letter",JOptionPane.ERROR_MESSAGE);
					if(direction.equals("right"))complementaryButton.setIcon(complementaryRight_up);
					else if(direction.equals("left"))complementaryButton.setIcon(complementaryLeft_up);
					return;
				}
				//complementary
				String comSequence=complementary(naSequence);
				//present output
				if(direction.equals("right"))rightSequencePane.setText(comSequence);
				else if(direction.equals("left"))leftSequencePane.setText(comSequence);

				if(direction.equals("right"))complementaryButton.setIcon(complementaryRight_up);
				else if(direction.equals("left"))complementaryButton.setIcon(complementaryLeft_up);
			}
		});

		//leftSequencePane
		leftSequencePane.addFocusListener(new FocusAdapter(){
			public void focusGained(FocusEvent fe){
				translateButton.setIcon(translateRight_up);
				transcribeButton.setIcon(transcribeRight_up);
				complementaryButton.setIcon(complementaryRight_up);
			}
		});

		//rightSequencePane
		rightSequencePane.addFocusListener(new FocusAdapter(){
			public void focusGained(FocusEvent fe){
				translateButton.setIcon(translateLeft_up);
				transcribeButton.setIcon(transcribeLeft_up);
				complementaryButton.setIcon(complementaryLeft_up);
			}
		});
	}

	private void layoutComponents(){
		JPanel mainPanel=new JPanel();
		mainPanel.setBackground(bgColor);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.X_AXIS));

		//left sequence area
		JPanel leftSequencePanel=new JPanel();
		leftSequencePanel.setOpaque(false);
		leftSequencePanel.setLayout(new BoxLayout(leftSequencePanel,BoxLayout.Y_AXIS));
		//leftButtonsPanel
		//leftSequencePane
		leftSequencePanel.add(leftSequenceScroll);
		mainPanel.add(leftSequencePanel);

		//operation buttons
		JPanel operationButtonsPanel=new JPanel();
		operationButtonsPanel.setOpaque(false);
		operationButtonsPanel.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
		operationButtonsPanel.setLayout(new BoxLayout(operationButtonsPanel,BoxLayout.Y_AXIS));
		operationButtonsPanel.add(Box.createVerticalGlue());
		operationButtonsPanel.add(translateButton);
		operationButtonsPanel.add(Box.createRigidArea(new Dimension(0,30)));
		operationButtonsPanel.add(transcribeButton);
		operationButtonsPanel.add(Box.createRigidArea(new Dimension(0,30)));
		operationButtonsPanel.add(complementaryButton);
		operationButtonsPanel.add(Box.createVerticalGlue());
		mainPanel.add(operationButtonsPanel);

		//aa sequence area
		JPanel rightSequencePanel=new JPanel();
		rightSequencePanel.setOpaque(false);
		rightSequencePanel.setLayout(new BoxLayout(rightSequencePanel,BoxLayout.Y_AXIS));
		//aaButtonsPanel
		//aaSequencePane
		rightSequencePanel.add(rightSequenceScroll);
		mainPanel.add(rightSequencePanel);

		add(mainPanel);
	}

	private void setupProperties(){
		setTitle("Translator");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setSize(550,350);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public static String translate(String na){
		String result="";
		String[] preInput=na.split("\\s+");	//split at white spaces with continuity
		if(preInput.length==0)return "";
		String[] input;
		if(preInput[0].length()==0){	//detect null input string
			input=new String[preInput.length-1];
			for(int i=1;i<preInput.length;i++){
				input[i-1]=preInput[i];
			}
		}else input=preInput;
		if(input.length==0)return "";
		String[] spaces=na.split("\\S+");	//split at non-white space to get the spaces
		String[] output=new String[input.length];	//store the output
		for(int i=0;i<input.length;i++){
			output[i]="";
			for(int j=0;j<(int)Math.ceil(input[i].length()/3d);j++){
				if(j*3+3<=input[i].length())output[i]+=Common.sequenceToAA(input[i].substring(j*3,j*3+3));
				else{
					output[i]+=Common.sequenceToAA(input[i].substring(j*3));	//reach the end of non triplet sequence
				}
			}
		}
		//recover input format's white spaces
		if(spaces.length==0){	//no spaces at all
			result=output[0];
		}else{
			if(spaces[0].length()==0){	//first space is a false detection,skip it
				result=output[0];
				for(int i=1;i<spaces.length;i++){
					result+=spaces[i];
					if(i<output.length)result+=output[i];
				}
			}else{	//first space is valid
				for(int i=0;i<spaces.length;i++){
					result+=spaces[i];
					if(i<output.length)result+=output[i];
				}
			}
		}

		return result;
	}

	public static String transcribe(String aa){
		String result="";
		String[] preInput=aa.split("\\s+");	//split at white spaces with continuity
		if(preInput.length==0)return "";
		String[] input;
		if(preInput[0].length()==0){	//detect null input string
			input=new String[preInput.length-1];
			for(int i=1;i<preInput.length;i++){
				input[i-1]=preInput[i];
			}
		}else input=preInput;
		if(input.length==0)return "";
		String[] spaces=aa.split("\\S+");	//split at non-white space to get the spaces
		String[] output=new String[input.length];	//store the output
		for(int i=0;i<input.length;i++){
			output[i]=Common.aaToSequence(input[i]);
		}
		//recover input format's white spaces
		if(spaces.length==0){	//no spaces at all
			result=output[0];
		}else{
			if(spaces[0].length()==0){	//first space is a false detection,skip it
				result=output[0];
				for(int i=1;i<spaces.length;i++){
					result+=spaces[i];
					if(i<output.length)result+=output[i];
				}
			}else{	//first space is valid
				for(int i=0;i<spaces.length;i++){
					result+=spaces[i];
					if(i<output.length)result+=output[i];
				}
			}
		}

		return result;
	}

	public static String complementary(String na){
		String result="";
		String[] preInput=na.split("\\s+");	//split at white spaces with continuity
		if(preInput.length==0)return "";
		String[] input;
		if(preInput[0].length()==0){	//detect null input string
			input=new String[preInput.length-1];
			for(int i=1;i<preInput.length;i++){
				input[i-1]=preInput[i];
			}
		}else input=preInput;
		if(input.length==0)return "";
		String[] spaces=na.split("\\S+");	//split at non-white space to get the spaces
		String[] output=new String[input.length];	//store the output
		for(int i=0;i<input.length;i++){
			output[i]=Common.getComplementarySequence(input[i]);
		}
		//recover input format's white spaces
		if(spaces.length==0){	//no spaces at all
			result=output[0];
		}else{
			if(spaces[0].length()==0){	//first space is a false detection,skip it
				result=output[0];
				for(int i=1;i<spaces.length;i++){
					result+=spaces[i];
					if(i<output.length)result+=output[i];
				}
			}else{	//first space is valid
				for(int i=0;i<spaces.length;i++){
					result+=spaces[i];
					if(i<output.length)result+=output[i];
				}
			}
		}

		return result;
	}
}
