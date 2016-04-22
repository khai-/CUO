package Support;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;

import Support.graphics.Circuit;
import Support.graphics.Fragment;
import Support.parser.GenBank;

public class OperationPanel extends JLayeredPane{
	OperationPanel OP=this;
	public StageTab ST;

	//canvas
	public Canvas canvas;

	//surface
	//sequence viewer
	SequenceViewer seqViewer;

	public ArrayList<Circuit> circuitList=new ArrayList<Circuit>();	//list of all the circuits

	ArrayList<Fragment> selectedFragments=new ArrayList<Fragment>();	//for fragments selection

	public OperationPanel(GenBank genBank,StageTab ST){	//initiated with a genBank
		this.ST=ST;

		createComponents();
		assignFunctions();
		layoutComponents();
		setupProperties();
		canvas.addCircuit(genBank,0,canvas.canvasHeight/2-7);
	}
	public OperationPanel(StageTab ST){	//initiated empty
		if(ST==null)return;	//for empty operation panel initiation
		//System.out.println("st is empty");
		this.ST=ST;

		createComponents();
		assignFunctions();
		layoutComponents();
		setupProperties();
	}

	private void createComponents(){
		//canvas
		canvas=new Canvas(OP);

		//sequence viewer
		seqViewer=new SequenceViewer();
	}

	private void assignFunctions(){

	}

	private void layoutComponents(){
		JPanel surfacePanel=new JPanel();
		surfacePanel.setLayout(new BoxLayout(surfacePanel,BoxLayout.Y_AXIS));
		surfacePanel.setOpaque(false);
		surfacePanel.add(Box.createVerticalGlue());
		surfacePanel.add(seqViewer);
		seqViewer.setPreferredSize(new Dimension(1,150));
		seqViewer.setMaximumSize(new Dimension(10000,150));
		add(surfacePanel,JLayeredPane.PALETTE_LAYER);

		setLayout(new OverlayLayout(OP));
		add(canvas,JLayeredPane.DEFAULT_LAYER);
	}

	private void setupProperties(){
		setOpaque(false);	//operation panel is naturally not opaque
		setAlignmentX(JPanel.CENTER_ALIGNMENT);
	}
}
