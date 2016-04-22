package Support.graphics;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Support.Common;

public class AminoAcid extends JPanel{
	public AminoAcid(String theCodon,int position){
		add(new JLabel(""+Common.getAAShortForm(theCodon)));
		setBackground(Common.getAAColor(theCodon));
		setBorder(BorderFactory.createLineBorder(Color.BLACK,2));
		setPreferredSize(new Dimension(45,25));
		setMinimumSize(new Dimension(45,25));
		setMaximumSize(new Dimension(45,25));
		setToolTipText(""+position);
	}
}
