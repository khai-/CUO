package Support.graphics;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class Codon extends JPanel{
	Color themeColor=new Color(76,117,35);

	public String codon;

	public Codon(String codon,int firstPos){
		this.codon=codon;
		setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
		setBackground(themeColor);
		setBorder(BorderFactory.createLineBorder(Color.BLACK,2));
		setPreferredSize(new Dimension(45,25));
		setMinimumSize(new Dimension(45,25));
		setMaximumSize(new Dimension(45,25));
		add(Box.createRigidArea(new Dimension(5,0)));
		add(new Nucleotide(codon.charAt(0),firstPos));
		add(new Nucleotide(codon.charAt(1),firstPos+1));
		add(new Nucleotide(codon.charAt(2),firstPos+2));
	}
}
