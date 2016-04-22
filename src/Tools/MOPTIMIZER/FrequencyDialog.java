package Tools.MOPTIMIZER;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import Support.CodonFrequencyTable;

public class FrequencyDialog extends ConfigureDialog{

	public FrequencyDialog(Window ownerFrame,Moptimizer moptimizer){
		super(ownerFrame,moptimizer);
		loadWeightTable.setText("Load Codon Frequency Table");
		minimumWeightLabel.setVisible(false);	//cancel these two
		minimumWeightText.setVisible(false);
	}

	public FrequencyDialog(JDialog ownerFrame,File frequencyFile){	//used in Processor
		super(ownerFrame,frequencyFile);	//use to ConfigureDialog constructor
	}

	void assignFunctions(){	//override
		//loadWeightTable	(loadCodonFrequencyTable)
		loadWeightTable.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				JFileChooser jfc=new JFileChooser("."+File.separator+"Construction");
				jfc.setAcceptAllFileFilterUsed(false);
				jfc.setFileFilter(new javax.swing.filechooser.FileFilter(){
					public boolean accept(File f){
						if(f.isDirectory()){
							return true;
						}
						String extension=f.getName().split("\\.")[f.getName().split("\\.").length-1];
						if(extension.equals("cft")){
							return true;
						}
						return false;
					}
					public String getDescription(){
						return "Codon Frequency Table file (*.cft)";
					}
				});
				if(jfc.showOpenDialog(configureDialog)==JFileChooser.APPROVE_OPTION){
					int[] temp;
					//read *.cft
					File cftFile=jfc.getSelectedFile();
					temp=CodonFrequencyTable.read(cftFile);
					if(temp==null){
						JOptionPane.showMessageDialog(configureDialog,"Error reading file.","Error",JOptionPane.ERROR_MESSAGE);
						return;
					}else{
						moptimizer.codonFrequencyTable=temp;
					}
					//update configure dialog table
					for(int i=0;i<64;i++){
						textField.get(i).setText(""+moptimizer.codonFrequencyTable[i]);
					}
				}else{
					return;
				}
			}
		});

		//configureOKButton
		configureOKButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				//extract the codon frequency table
				for(int i=0;i<64;i++){
					try{
						moptimizer.codonFrequencyTable[i]=Integer.parseInt(textField.get(i).getText());
						moptimizer.hasCodonFrequencyTable=true;
					}catch(NumberFormatException nfe){
						JOptionPane.showMessageDialog(configureDialog,"Insert number only.","Error",JOptionPane.PLAIN_MESSAGE);
						return;
					}
				}

				setVisible(false);
			}
		});

		//configureCancelButton
		configureCancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				setVisible(false);
			}
		});
	}
}
