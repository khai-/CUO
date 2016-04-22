package Tools.OPTIMIZER.Filters;

import Tools.OPTIMIZER.Optimizer;

public class CodonRepeats extends Filter{

	public CodonRepeats(Optimizer optimizer,int processNumber){
		super(optimizer,processNumber);
		filterName="Codon Repeats";
		filterLabel.setText("Codon Repeats");
	}

	public boolean trigger(){
		return true;
	}

	public static String getDescription(){
		return "Avoiding repetitive usage of certain aminoacyl-tRNA that may cause local aminoacyl-tRNA starvation.";
	}
}
