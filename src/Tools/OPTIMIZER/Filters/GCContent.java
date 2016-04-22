package Tools.OPTIMIZER.Filters;

import Tools.OPTIMIZER.Optimizer;

public class GCContent extends Filter{

	public GCContent(Optimizer optimizer,int processNumber){
		super(optimizer,processNumber);
		filterName="GC Content";
		filterLabel.setText("GC Content");
	}

	public boolean trigger(){
		return true;
	}

	public static String getDescription(){
		return "Different host may adapt to different GC Content level.";
	}
}
