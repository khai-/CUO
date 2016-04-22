package Support.graphics.Indicators;

import java.awt.Color;
import java.awt.Dimension;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Support.Common;

public class CodonPairIndicator extends JPanel{
	public CodonPairIndicator(float[][] codonPairTable,float minimumCodonPair,String thePair){
		setPreferredSize(new Dimension(90,25));
		setMinimumSize(new Dimension(90,25));
		setMaximumSize(new Dimension(90,25));

		int first=Common.getCodonCoordinate(thePair.substring(0,3));
		int second=Common.getCodonCoordinate(thePair.substring(3,6));
		float brightness=codonPairTable[first][second];

		setBackground(new Color((int)(255*brightness),(int)(255*brightness),(int)(255*brightness)));
		if(brightness<minimumCodonPair){
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
