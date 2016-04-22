package Tools.OPTIMIZER.Filters;

import Tools.OPTIMIZER.Optimizer;

public class mRNASecondaryStructure extends Filter{

	public mRNASecondaryStructure(Optimizer optimizer,int processNumber){
		super(optimizer,processNumber);
		filterName="mRNA Secondary Structure";
		filterLabel.setText("mRNA Secondary Structure");
	}

	public boolean trigger(){
		return true;
	}

	public static String getDescription(){
		return "Filter the result with mRNA secondary structure level. More mRNA secondary structure, slower translation efficiency.";
	}
}
