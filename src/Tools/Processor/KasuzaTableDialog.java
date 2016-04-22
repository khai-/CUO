package Tools.Processor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import Support.Common;
import Support.WeightTable;

public class KasuzaTableDialog extends JDialog{
	KasuzaTableDialog kasuzaTableDialog=this;
	JDialog ownerFrame;
	Color bgColor=new Color(70,70,70);

	//pasteArea
	JTextPane kasuzaPane;
	JScrollPane kasuzaScroll;

	//buttonsArea
	JButton parseButton;

	public KasuzaTableDialog(JDialog ownerFrame){
		super(ownerFrame,false);
		this.ownerFrame=ownerFrame;
		initialize();
	}

	private void initialize(){
		createComponents();
		assignFunctions();
		layoutComponents();
		setupProperties();
	}

	private void createComponents(){
		//pasteArea
		kasuzaPane=new JTextPane();
		kasuzaScroll=new JScrollPane(kasuzaPane,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		//buttonsArea
		parseButton=new JButton("Parse");
	}

	private void assignFunctions(){
		//buttonsArea
		//parseButton
		parseButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				String kasu=kasuzaPane.getText();
				//parse codon frequency table from the data in pane
				int[] codonFrequency=new int[64];
				try{
					codonFrequency=parseCFTKasuza(kasu);
				}catch(Exception e){
					JOptionPane.showMessageDialog(kasuzaTableDialog,"Kasuza table format unrecognized. Please copy and paste the codon table part only from http://www.kazusa.or.jp/codon/","Parse error",JOptionPane.ERROR_MESSAGE);
					return;
				}
				//calculate weight
				float[] weightTable=WeightTable.getWeight(codonFrequency);
				//call edittabledialog and present weight table and codon frequency	
				EditTableDialog editTableDialog=new EditTableDialog(ownerFrame);
				DecimalFormat df=new DecimalFormat("0.#####");
				df.setRoundingMode(RoundingMode.HALF_UP);
				for(int i=0;i<64;i++){
					editTableDialog.frequencyTextField.get(i).setText(""+codonFrequency[i]);
					editTableDialog.weightTextField.get(i).setText(""+df.format(weightTable[i]));
				}
				//close this parse dialog
				dispose();
			}
		});
	}

	private void layoutComponents(){
		JPanel mainPanel=new JPanel();
		mainPanel.setBackground(bgColor);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,5,10));
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));

		//pasteArea
		JPanel pastePanel=new JPanel();
		pastePanel.setOpaque(false);
		pastePanel.setLayout(new BorderLayout());
		pastePanel.add(kasuzaScroll);
		mainPanel.add(pastePanel);
		mainPanel.add(Box.createRigidArea(new Dimension(0,5)));

		//buttonsArea
		JPanel buttonsPanel=new JPanel();
		buttonsPanel.setOpaque(false);
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel,BoxLayout.X_AXIS));
		buttonsPanel.add(Box.createHorizontalGlue());
		buttonsPanel.add(parseButton);
		mainPanel.add(buttonsPanel);

		add(mainPanel);
	}

	private void setupProperties(){
		setTitle("Parse Table from Kasuza Database");
		setSize(520,300);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	//parse codon frequency table from given kasuza table
	public static int[] parseCFTKasuza(String kasu) throws Exception{
		int[] codonFrequency=new int[64];
		int counter=-1;	//counter to record which codon's frequency is being parsed now
		for(int i=0;i<kasu.length();i++){
			if(kasu.charAt(i)=='('){
				counter++;
				i++;	//go to the first character in the bracket
				//seek the codon frequency in bracket
				String theNumber="";
				while(kasu.charAt(i)!=')'){
					theNumber+=kasu.charAt(i);
					i++;
				}
				i++;	//pump i to after ")"
				int codonFrequencyNum=Integer.parseInt(theNumber.trim());

				//assign to correct codon frequency table position
				codonFrequency[Common.horizontalToVerticalNumbering(counter)]=codonFrequencyNum;
			}
		}
		return codonFrequency;
	}
}
