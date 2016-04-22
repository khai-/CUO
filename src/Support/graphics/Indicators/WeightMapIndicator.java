package Support.graphics.Indicators;

import java.awt.Color;
import java.awt.Dimension;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Support.Common;

public class WeightMapIndicator extends JPanel{
	public WeightMapIndicator(float[] weightTable,float minimumWeight,String theCodon){
		setPreferredSize(new Dimension(45,25));
		setMinimumSize(new Dimension(45,25));
		setMaximumSize(new Dimension(45,25));

		int coord=Common.getCodonCoordinate(theCodon);
		float brightness=weightTable[coord];

		setBackground(new Color((int)(255*brightness),(int)(255*brightness),(int)(255*brightness)));
		if(brightness<minimumWeight){
			setBorder(BorderFactory.createLineBorder(Color.RED,2));
		}else{
			setBorder(BorderFactory.createLineBorder(Color.BLACK,2));
		}
		DecimalFormat df=new DecimalFormat("0.##");
		df.setRoundingMode(RoundingMode.HALF_UP);
		JLabel theLabel=new JLabel(""+df.format(brightness));
		if(brightness<0.4){
			theLabel.setForeground(Color.WHITE);
		}
		add(theLabel);
	}
}
