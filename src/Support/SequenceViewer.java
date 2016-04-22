package Support;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import Support.graphics.Fragment;

public class SequenceViewer extends JPanel{
	SequenceViewer SV=this;
	Color bgColor=new Color(120,120,120);

	public ArrayList<Fragment> selectedFrags=new ArrayList<Fragment>();

	//selectedFrags site
	JPanel fragsPanel;
	JList fragsList;
	DefaultListModel fragsModel=new DefaultListModel();
	JScrollPane fragsScroll;

	//sequence site
	JPanel sequencePanel;
	//sequence site toolbar
	JPanel seqToolbarPanel;
	//sequence site pane
	JTextPane sequencePane;
	StyledDocument seqDoc;
	JScrollPane seqScroll;

	public SequenceViewer(){
		createComponents();
		assignFunctions();
		layoutComponents();
		setupProperties();
	}

	public SequenceViewer(Fragment frag){
		this();
		updateList(frag);
	}

	public SequenceViewer(ArrayList<Fragment> selectedFrags){
		this();
		updateList(selectedFrags);
	}

	private void createComponents(){
		//selectedFrags site
		fragsPanel=new JPanel();
		fragsList=new JList();
		fragsList.setModel(fragsModel);
		fragsScroll=new JScrollPane(fragsList,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		fragsScroll.getViewport().setPreferredSize(new Dimension(145,140));

		sequencePanel=new JPanel();
		//sequence site toolbar
		seqToolbarPanel=new JPanel();
		seqToolbarPanel.setOpaque(false);
		//sequence site
		sequencePane=new JTextPane();
		seqDoc=sequencePane.getStyledDocument();
		seqScroll=new JScrollPane(sequencePane,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		seqScroll.getViewport().setPreferredSize(new Dimension(450,140));
	}

	private void assignFunctions(){
		//fragsList
		fragsList.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e){
				//clear the previous sequences
				sequencePane.setText("");

				int caretPosBefore=sequencePane.getCaretPosition();
				int[] indices=fragsList.getSelectedIndices();
				for(int i=0;i<indices.length;i++){
					String sequence=selectedFrags.get(indices[i]).theSeq.codingSequence;
					try{
						//present the fragment's sequence in sequence site
						if(seqDoc.getLength()==0){
							seqDoc.insertString(0,sequence,null);						
						}else{
							seqDoc.insertString(seqDoc.getLength(),"\n\n"+sequence,null);
						}
					}catch(BadLocationException ble){}	//do nothing
				}
				sequencePane.setCaretPosition(caretPosBefore);
			}
		});

		//sequencePane
	}

	private void layoutComponents(){
		setLayout(new BorderLayout());
		JPanel mainPanel=new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.X_AXIS));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		mainPanel.setOpaque(false);

		//selectedFrags site
		fragsPanel.setLayout(new BorderLayout());
		fragsPanel.setOpaque(false);
		fragsPanel.add(fragsScroll);
		fragsPanel.setMaximumSize(fragsScroll.getViewport().getPreferredSize());
		mainPanel.add(fragsPanel);

		//sequence site
		JPanel sequenceMainPanel=new JPanel();
		sequenceMainPanel.setOpaque(false);
		sequenceMainPanel.setLayout(new BorderLayout());
		sequencePanel.setOpaque(false);
		sequencePanel.setLayout(new BoxLayout(sequencePanel,BoxLayout.Y_AXIS));
		//sequencePanel.add(seqToolbarPanel);
		sequencePanel.add(seqScroll);
		sequenceMainPanel.add(sequencePanel);
		mainPanel.add(sequenceMainPanel);

		add(mainPanel);
	}

	private void setupProperties(){
		setBackground(bgColor);
		setVisible(false);
	}

	public void updateList(Fragment frag){
		ArrayList<Fragment> theFrags=new ArrayList<Fragment>();
		theFrags.add(frag);
		updateList(theFrags);
	}

	public void updateList(ArrayList<Fragment> selectedFrags){
		this.selectedFrags=selectedFrags;

		fragsModel.clear();	//clean the original list
		for(int i=0;i<selectedFrags.size();i++){	//add the selectedFrags to list
			String seqGene=selectedFrags.get(i).theSeq.gene;
			if(seqGene==null){
				seqGene=selectedFrags.get(i).theSeq.type;
			}
			fragsModel.addElement(seqGene);
		}

		fragsList.setSelectedIndex(0);
	}
}
