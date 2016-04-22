package Support;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

public class Grapher extends JDialog{	//a GUI class that plot graphs
	JDialog grapher=this;
	Color bgColor=new Color(70,70,70);
	Font smallFont=new Font(null,Font.PLAIN,9);

	//control area
	//buttonsPanel
	JButton loadDataButton;
	JButton snapshotButton;
	ImageIcon snapshotIcon_up=new ImageIcon("Icons/snapshot_up.gif");
	ImageIcon snapshotIcon_down=new ImageIcon("Icons/snapshot_down.gif");
	JButton exportButton;
	JButton markButton;
	JButton textBoxButton;
	//sizePanel
	JComboBox graphType;
	JTextField graphWidth;
	JTextField graphHeight;
	//axisPanel
	JTextField xAxisName;
	JSlider xAxisSlider;
	JTextField xAxisScale;
	JTextField yAxisName;
	JSlider yAxisSlider;
	JTextField yAxisScale;

	//graph area
	JPanel graphPanel;
	JScrollPane graphScroll;
	JFreeChart chart;	//the graph
	ArrayList<float[]> data=new ArrayList<float[]>();

	public Grapher(JDialog ownerFrame){
		super(ownerFrame,false);
		createComponents();
		assignFunctions();
		layoutComponents();
		setupProperties();
	}

	private void createComponents(){
		//control area
		//buttonsPanel
		loadDataButton=new JButton("Load data");
		loadDataButton.setFont(smallFont);
		snapshotButton=new JButton(snapshotIcon_up);
		snapshotButton.setContentAreaFilled(false);
		snapshotButton.setBorderPainted(false);
		snapshotButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		//sizePanel
		String[] types=new String[]{"Scatter","Line","Histogram"};
		graphType=new JComboBox(types);
		graphType.setPreferredSize(new Dimension(100,20));
		graphType.setMaximumSize(new Dimension(10000,20));
		graphWidth=new JTextField();
		graphWidth.setPreferredSize(new Dimension(100,20));
		graphWidth.setMaximumSize(new Dimension(10000,20));
		graphHeight=new JTextField();
		graphHeight.setPreferredSize(new Dimension(100,20));
		graphHeight.setMaximumSize(new Dimension(10000,20));
		//axisPanel
		xAxisName=new JTextField("X");
		xAxisName.setPreferredSize(new Dimension(100,20));
		xAxisName.setMaximumSize(new Dimension(100,20));
		xAxisSlider=new JSlider(0,100,1);	//min,max,value
		xAxisSlider.setOpaque(false);
		xAxisScale=new JTextField("1");
		xAxisScale.setMinimumSize(new Dimension(40,20));
		xAxisScale.setPreferredSize(new Dimension(40,20));
		xAxisScale.setMaximumSize(new Dimension(40,20));
		yAxisName=new JTextField("Y");
		yAxisName.setPreferredSize(new Dimension(100,20));
		yAxisName.setMaximumSize(new Dimension(100,20));
		yAxisSlider=new JSlider(0,10,1);	//JSlider only accept int,multiply manually by 10
		yAxisSlider.setOpaque(false);
		yAxisScale=new JTextField("0.1");
		yAxisScale.setMinimumSize(new Dimension(40,20));
		yAxisScale.setPreferredSize(new Dimension(40,20));
		yAxisScale.setMaximumSize(new Dimension(40,20));

		//graph area
		graphPanel=new JPanel(){
			public void paintComponent(Graphics g){
				super.paintComponent(g);

				//draw chart into graphic
				try{
					int width=Integer.parseInt(graphWidth.getText());
					int height=Integer.parseInt(graphHeight.getText());
					Graphics2D g2d=(Graphics2D)g;
					Rectangle2D r2d=new Rectangle2D.Double(0,0,width,height);
					chart.draw(g2d,r2d,null);
				}catch(Exception e){
					//do nothing eith incorrect input
				}
			}
		};
		graphScroll=new JScrollPane(graphPanel,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		graphScroll.getViewport().setPreferredSize(new Dimension(450,10000));
	}

	private void assignFunctions(){
		//buttonsPanel
		//snapshotButton
		snapshotButton.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				snapshotButton.setIcon(snapshotIcon_down);
			}
			public void mouseReleased(MouseEvent me){
				snapshotButton.setIcon(snapshotIcon_up);
				JFileChooser jfc=new JFileChooser("."+File.separator+"Construction");
				jfc.setFileFilter(new javax.swing.filechooser.FileFilter(){
					public boolean accept(File f){
						if(f.isDirectory()){
							return true;
						}
						String extension=f.getName().split("\\.")[f.getName().split("\\.").length-1];
						if(extension.equals("jpeg")){
							return true;
						}
						return false;
					}
					public String getDescription(){
						return "JPEG (*.jpeg)";
					}
				});
				if(jfc.showSaveDialog(grapher)==JFileChooser.APPROVE_OPTION){
					Common.takeSnapshot(graphPanel,jfc.getSelectedFile());
				}else{
					return;
				}
			}
		});

		//sizePanel
		//graphType
		graphType.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent ie){
				makeGraph();
				graphScroll.getViewport().repaint();
			}
		});
		//graphWidth
		graphWidth.addActionListener(new ActionListener(){	//upon press enter
			public void actionPerformed(ActionEvent ae){
				try{
					int newWidth=Math.abs(Integer.parseInt(graphWidth.getText()));	//validate the width input
					setGraphWidth(newWidth);
				}catch(NumberFormatException nfe){
					JOptionPane.showMessageDialog(grapher,"Graph width must be integer.","Error",JOptionPane.PLAIN_MESSAGE);
				}
			}
		});
		graphWidth.addFocusListener(new FocusAdapter(){	//upon lost focus
			public void focusLost(FocusEvent fe){
				try{
					int newWidth=Math.abs(Integer.parseInt(graphWidth.getText()));	//validate the width input
					setGraphWidth(newWidth);
				}catch(NumberFormatException nfe){
					JOptionPane.showMessageDialog(grapher,"Graph width must be integer.","Error",JOptionPane.PLAIN_MESSAGE);
				}
			}
		});
		//graphHeight
		graphHeight.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				try{
					int newHeight=Math.abs(Integer.parseInt(graphHeight.getText()));	//validate the width input
					setGraphHeight(newHeight);
				}catch(NumberFormatException nfe){
					JOptionPane.showMessageDialog(grapher,"Graph height must be integer.","Error",JOptionPane.PLAIN_MESSAGE);
				}
			}
		});
		graphHeight.addFocusListener(new FocusAdapter(){
			public void focusLost(FocusEvent fe){
				try{
					int newHeight=Math.abs(Integer.parseInt(graphHeight.getText()));	//validate the width input
					setGraphHeight(newHeight);
				}catch(NumberFormatException nfe){
					JOptionPane.showMessageDialog(grapher,"Graph height must be integer.","Error",JOptionPane.PLAIN_MESSAGE);
				}
			}
		});
	}

	private void layoutComponents(){
		JPanel mainPanel=new JPanel();
		mainPanel.setBackground(bgColor);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.X_AXIS));

		//control area
		JPanel controlPanel=new JPanel();
		controlPanel.setOpaque(false);
		controlPanel.setPreferredSize(new Dimension(170,10000));
		controlPanel.setMaximumSize(controlPanel.getPreferredSize());
		controlPanel.setLayout(new BoxLayout(controlPanel,BoxLayout.Y_AXIS));
		//buttonsPanel
		JPanel buttonsPanel=new JPanel();
		buttonsPanel.setOpaque(false);
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel,BoxLayout.X_AXIS));
		//buttonsPanel.add(loadDataButton);
		buttonsPanel.add(Box.createHorizontalGlue());
		buttonsPanel.add(snapshotButton);
		controlPanel.add(buttonsPanel);
		controlPanel.add(Box.createRigidArea(new Dimension(0,10)));
		//sizePanel
		JPanel sizePanel=new JPanel();
		sizePanel.setOpaque(false);
		sizePanel.setLayout(new BoxLayout(sizePanel,BoxLayout.Y_AXIS));
		JPanel graphTypePanel=new JPanel();
		graphTypePanel.setOpaque(false);
		graphTypePanel.setLayout(new BoxLayout(graphTypePanel,BoxLayout.X_AXIS));
		JLabel graphTypeLabel=new JLabel("Type :");
		graphTypeLabel.setOpaque(false);
		graphTypeLabel.setForeground(Color.WHITE);
		graphTypePanel.add(graphTypeLabel);
		graphTypePanel.add(graphType);
		sizePanel.add(graphTypePanel);
		sizePanel.add(Box.createRigidArea(new Dimension(0,5)));
		JPanel graphWidthPanel=new JPanel();
		graphWidthPanel.setOpaque(false);
		graphWidthPanel.setLayout(new BoxLayout(graphWidthPanel,BoxLayout.X_AXIS));
		JLabel graphWidthLabel=new JLabel("Graph width :");
		graphWidthLabel.setOpaque(false);
		graphWidthLabel.setForeground(Color.WHITE);
		graphWidthPanel.add(graphWidthLabel);
		graphWidthPanel.add(graphWidth);
		sizePanel.add(graphWidthPanel);
		JPanel graphHeightPanel=new JPanel();
		graphHeightPanel.setOpaque(false);
		graphHeightPanel.setLayout(new BoxLayout(graphHeightPanel,BoxLayout.X_AXIS));
		JLabel graphHeightLabel=new JLabel("Graph height:");
		graphHeightLabel.setOpaque(false);
		graphHeightLabel.setForeground(Color.WHITE);
		graphHeightPanel.add(graphHeightLabel);
		graphHeightPanel.add(graphHeight);
		sizePanel.add(graphHeightPanel);
		controlPanel.add(sizePanel);
		controlPanel.add(Box.createRigidArea(new Dimension(0,20)));
		//axisPanel
		JPanel axisPanel=new JPanel();
		axisPanel.setOpaque(false);
		axisPanel.setLayout(new BoxLayout(axisPanel,BoxLayout.Y_AXIS));
		JPanel xAxisPanel=new JPanel();
		xAxisPanel.setOpaque(false);
		xAxisPanel.setLayout(new BoxLayout(xAxisPanel,BoxLayout.Y_AXIS));
		JPanel xAxisNamePanel=new JPanel();
		xAxisNamePanel.setOpaque(false);
		xAxisNamePanel.setLayout(new BoxLayout(xAxisNamePanel,BoxLayout.X_AXIS));
		JLabel xAxisNameLabel=new JLabel("x-axis:");
		xAxisNameLabel.setOpaque(false);
		xAxisNameLabel.setForeground(Color.WHITE);
		xAxisNamePanel.add(xAxisNameLabel);
		xAxisNamePanel.add(xAxisName);
		xAxisNamePanel.add(Box.createHorizontalGlue());
		xAxisPanel.add(xAxisNamePanel);
		xAxisPanel.add(Box.createRigidArea(new Dimension(0,5)));
		JPanel xAxisScalePanel=new JPanel();
		xAxisScalePanel.setOpaque(false);
		xAxisScalePanel.setLayout(new BoxLayout(xAxisScalePanel,BoxLayout.X_AXIS));
		xAxisScalePanel.add(xAxisSlider);
		xAxisScalePanel.add(xAxisScale);
		xAxisPanel.add(xAxisScalePanel);
		axisPanel.add(xAxisPanel);
		axisPanel.add(Box.createRigidArea(new Dimension(0,10)));
		JPanel yAxisPanel=new JPanel();
		yAxisPanel.setOpaque(false);
		yAxisPanel.setLayout(new BoxLayout(yAxisPanel,BoxLayout.Y_AXIS));
		JPanel yAxisNamePanel=new JPanel();
		yAxisNamePanel.setOpaque(false);
		yAxisNamePanel.setLayout(new BoxLayout(yAxisNamePanel,BoxLayout.X_AXIS));
		JLabel yAxisNameLabel=new JLabel("y-axis:");
		yAxisNameLabel.setOpaque(false);
		yAxisNameLabel.setForeground(Color.WHITE);
		yAxisNamePanel.add(yAxisNameLabel);
		yAxisNamePanel.add(yAxisName);
		yAxisNamePanel.add(Box.createHorizontalGlue());
		yAxisPanel.add(yAxisNamePanel);
		yAxisPanel.add(Box.createRigidArea(new Dimension(0,5)));
		JPanel yAxisScalePanel=new JPanel();
		yAxisScalePanel.setOpaque(false);
		yAxisScalePanel.setLayout(new BoxLayout(yAxisScalePanel,BoxLayout.X_AXIS));
		yAxisScalePanel.add(yAxisSlider);
		yAxisScalePanel.add(yAxisScale);
		yAxisPanel.add(yAxisScalePanel);
		axisPanel.add(yAxisPanel);
		//controlPanel.add(axisPanel);
		controlPanel.add(Box.createVerticalGlue());

		mainPanel.add(controlPanel);
		mainPanel.add(Box.createRigidArea(new Dimension(10,0)));

		//graph area
		mainPanel.add(graphScroll);
		add(mainPanel);
	}

	private void setupProperties(){
		setSize(580,340);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);

		setGraphSize(graphScroll.getViewport().getWidth(),graphScroll.getViewport().getHeight());
	}

	public void setXAxisName(String newName){

	}

	public void setYAxisName(String newName){

	}

	public void setXScale(int newScale){

	}

	public void setYScale(int newScale){

	}

	//methods to change graph size (default is the panel size upon initialization)
	public void setGraphSize(int newWidth,int newHeight){
		graphWidth.setText(newWidth+"");
		graphHeight.setText(newHeight+"");
		graphPanel.setSize(newWidth,newHeight);
		graphPanel.setPreferredSize(new Dimension(newWidth,newHeight));
		graphScroll.getViewport().repaint();
	}
	private void setGraphWidth(int newWidth){
		graphWidth.setText(newWidth+"");
		graphPanel.setSize(newWidth,graphPanel.getHeight());
		graphPanel.setPreferredSize(new Dimension(newWidth,graphPanel.getHeight()));
		graphScroll.getViewport().repaint();
	}
	private void setGraphHeight(int newHeight){
		graphHeight.setText(newHeight+"");
		graphPanel.setSize(graphPanel.getWidth(),newHeight);
		graphPanel.setPreferredSize(new Dimension(graphPanel.getWidth(),newHeight));
		graphScroll.getViewport().repaint();
	}

	//change graph type
	public void setGraphType(){

	}

	//set current presented data
	public void setData(float[] codonWeights){
		data.clear();	//clear previous data
		data.add(codonWeights);	//add new data
		makeGraph();
	}

	private void makeGraph(){
		//construct the dataset
		XYSeriesCollection dataset=new XYSeriesCollection();
		for(int i=0;i<data.size();i++){
			XYSeries series=new XYSeries("Weight Distribution");
			for(int j=0;j<data.get(i).length;j++){
				series.add(j+1d,data.get(i)[j]);
			}
			dataset.addSeries(series);
		}

		//make graph of weight vs sequence
		int theType=graphType.getSelectedIndex();
		if(theType==0){	//scatter
			chart=ChartFactory.createScatterPlot(null,"Codon","Weight",dataset,PlotOrientation.VERTICAL,false,true,false);
		}else if(theType==1){	//line
			chart=ChartFactory.createXYLineChart(null,"Codon","Weight",dataset,PlotOrientation.VERTICAL,false,true,false);
		}else if(theType==2){	//histogram
			chart=ChartFactory.createHistogram(null,"Codon","Weight",dataset,PlotOrientation.VERTICAL,false,true,false);
		}else{
			return;
		}
		chart.setBackgroundPaint(Color.WHITE);
		XYPlot plot=chart.getXYPlot();
		plot.setBackgroundPaint(Color.WHITE);
		//customize the range axis
		NumberAxis rangeAxis=(NumberAxis)plot.getRangeAxis();
		rangeAxis.setNumberFormatOverride(new DecimalFormat("0.##"));
		//customize the shape of points
		if(theType==0||theType==1){
			XYLineAndShapeRenderer renderer=(XYLineAndShapeRenderer)plot.getRenderer();
			renderer.setSeriesShape(0,ShapeUtilities.createDiamond(1f));
		}else if(theType==2){
			XYBarRenderer renderer=(XYBarRenderer)plot.getRenderer();
			renderer.setShadowVisible(false);
		}
	}
}
