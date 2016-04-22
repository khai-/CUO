package Tools.OPTIMIZER.Filters;

import Tools.OPTIMIZER.Optimizer;

public class PatternImitation extends Filter{

	public PatternImitation(Optimizer optimizer,int processNumber){
		super(optimizer,processNumber);
		filterName="Pattern Imitation";
		filterLabel.setText("Pattern Imitation");
	}

	public boolean trigger(){
		return true;
	}

	public static String getDescription(){
		return "Take only the results that have similar codon usage pattern with the original target sequence. The purpose is to imitate the folding speed and phase of original protein.";
	}
}
