import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.block.BorderArrangement;

public class ShortcutsUI extends JFrame {

	JPanel listPanel;
	JLabel listlabel;
	JButton closeButn;
	ImageIcon shortcutsImg;
	
	public ShortcutsUI() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Codon Usage Optimizer Shortcuts");
		setIconImage(Toolkit.getDefaultToolkit().getImage("logo.png"));
		setResizable(true);
		setSize(800,500);
		setLocationRelativeTo(null);
		createComponents();
		
	}

	public void createComponents()
	{
		listPanel = new JPanel();
		shortcutsImg=new ImageIcon("Icons"+java.io.File.separator+"shortcuts.png");
		listlabel = new JLabel(shortcutsImg);
		listPanel.add(listlabel,BorderLayout.CENTER);

		this.add(listPanel);
	}
	
}
