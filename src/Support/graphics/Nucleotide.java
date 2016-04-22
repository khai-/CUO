package Support.graphics;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

public class Nucleotide extends JLabel{
	JLabel nuc=this;

	public Nucleotide(char base,int position){
		setText(""+Character.toUpperCase(base));
		setForeground(Color.WHITE);
		setToolTipText(""+position);
		addMouseListener(new MouseAdapter(){	//to allow click through
			public void mouseReleased(MouseEvent me){
				getParent().getParent().dispatchEvent(new MouseEvent(getParent().getParent(),me.getID(),me.getWhen(),me.getModifiers(),getParent().getX()+nuc.getX()+me.getX(),me.getY(),me.getClickCount(),false));
			}
		});
	}
}
