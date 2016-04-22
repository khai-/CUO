package Tools.OPTIMIZER.Filters;

import Tools.OPTIMIZER.Optimizer;

public class CodonPair extends Filter{

	public CodonPair(Optimizer optimizer,int processNumber){
		super(optimizer,processNumber);
		filterName="Codon Pair";
		filterLabel.setText("Codon Pair");
	}

	public boolean trigger(){
		return true;
	}

	public static String getDescription(){
		return "Host genome will have favorable and unfavorable codon pairs just like codon usage.";
	}
}
