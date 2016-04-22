package Support;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import Support.graphics.Circuit;
import Support.graphics.Fragment;
import Support.graphics.TextBox;
import Support.parser.GenBank;
import Support.parser.Sequence;
import Tools.SequenceCreator.SequenceCreator;

public class Canvas extends JPanel{
	Canvas CANVAS=this;
	public OperationPanel OP;
	public ArrayList<Circuit> circuitList;	//list of all the circuits
	ArrayList<Fragment> selectedFragments;	//for fragments selection
	public ArrayList<TextBox> textboxList=new ArrayList<TextBox>();	//list of all the text boxes on the canvas

	public int currentZoom=1;
	boolean fresh=true;	//means this is a newly created canvas with no circuit

	//for selection
	boolean selectionBox=false;	//whether selection box is present
	int selectionStartX;
	int selectionStartY;
	int selectionEndX;
	int selectionEndY;	//position and size of the selection box

	//for air grabbing
	public int oldMouseX;
	public int oldMouseY;

	//modes: 0=default,1=mover
	public int modes=0;
	Circuit movedCircuit;	//circuit moved by mover

	//Alt to show fragments name
	public boolean namesVisible=false;	

	//the image
	public int canvasWidth=600;
	public int canvasHeight=400;
	BufferedImage canvas;
	int topLeftX=0;
	int topLeftY=0;
	int bottomRightX=canvasWidth;
	int bottomRightY=canvasHeight;
	BufferedImage image=new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);

	//popup menu
	JPopupMenu popup;
	JMenuItem newFragmentMenu;

	//dnd state
	boolean dndState =false;
	
	public Canvas(OperationPanel OP){
		this.OP=OP;
		this.circuitList=OP.circuitList;
		this.selectedFragments=OP.selectedFragments;
		createComponents();
		assignFunctions();
		layoutComponents();
		setupProperties();
	}

	private void createComponents(){
		//canvas
		canvas=new BufferedImage(canvasWidth,canvasHeight,BufferedImage.TYPE_INT_ARGB);
		Graphics2D canvas2D=canvas.createGraphics();
		canvas2D.fill(new Rectangle(0,0,canvasWidth,canvasHeight));

		//popup menu
		popup=new JPopupMenu();
		newFragmentMenu=new JMenuItem("New Fragment");
		popup.add(newFragmentMenu);
	}

	private void assignFunctions(){
		//popup
		//newFragmentMenu
		newFragmentMenu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				new SequenceCreator(CANVAS);
			}
		});

		//this operation panel
		addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				//RIGHT MOUSE BUTTON
				if(me.isPopupTrigger()){
					oldMouseX=me.getX();
					oldMouseY=me.getY();
					
					if(dndState == false)
					{
					//check whether the click was on graph region
					for(int i=0;i<circuitList.size();i++){
						Fragment theFragment=circuitList.get(i).checkHit(oldMouseX/currentZoom+topLeftX,oldMouseY/currentZoom+topLeftY);
						if(theFragment!=null){
							popup.show(OP,oldMouseX,oldMouseY);
							return;
							}
						}
					}
					else
					{
						popup.show(OP,oldMouseX,oldMouseY);
					}
				}
				//LEFT MOUSE BUTTON
				if(me.getButton()!=me.BUTTON1)return;
				if(modes==0){	//default
					//pan positioning
					oldMouseX=me.getX();
					oldMouseY=me.getY();
				}else if(modes==1){	//mover
					//move positioning
					oldMouseX=me.getX();
					oldMouseY=me.getY();

					for(int i=0;i<circuitList.size();i++){
						Fragment theFragment=circuitList.get(i).checkHit(me.getX()/currentZoom+topLeftX,me.getY()/currentZoom+topLeftY);
						if(theFragment!=null){
							movedCircuit=circuitList.get(i);
							return;
						}
					}
				}
			}
			public void mouseReleased(MouseEvent me){
				requestFocus();
				//System.out.println(me.getX() + " " + me.getY());
//System.out.println(me.getX()+":"+me.getY());
//System.out.println(topLeftX+":"+topLeftY+"  : "+bottomRightX+":"+bottomRightY);
//System.out.println("ANSWER:"+(me.getX()/currentZoom+topLeftX)+":"+(me.getY()/currentZoom+topLeftY));
//System.out.println("Current zoom:"+currentZoom);

				//RIGHT MOUSE BUTTON
				//popup
				if(me.isPopupTrigger()){
					oldMouseX=me.getX();
					oldMouseY=me.getY();
					
					popup.show(OP,oldMouseX,oldMouseY);
				}
				
				//LEFT MOUSE BUTTON
				if(me.getButton()!=me.BUTTON1)return;
				//check modes
				if(modes==1){
					movedCircuit=null;
					return;
				}//else modes==0, proceed to below

				if(me.isControlDown()){	//control key is down, multiple selection
					if(selectionBox){
						//deselect all fragments
						for(int k=0;k<circuitList.size();k++){
							for(int j=0;j<circuitList.get(k).fragmentList.size();j++){
								circuitList.get(k).fragmentList.get(j).deselect();
							}
						}
						//select included fragments
						ArrayList<Fragment> allSelectedFrags=new ArrayList<Fragment>();
						for(int i=0;i<circuitList.size();i++){
							ArrayList<Fragment> selectedFrags=circuitList.get(i).checkHit(selectionStartX/currentZoom+topLeftX,selectionStartY/currentZoom+topLeftY,selectionEndX/currentZoom+topLeftX,selectionEndY/currentZoom+topLeftY);
							if(selectedFrags!=null){
								allSelectedFrags.addAll(selectedFrags);
							}
						}
						for(int i=0;i<allSelectedFrags.size();i++){
							allSelectedFrags.get(i).select();
						}

						//update sequence viewer
						if(OP.seqViewer.isVisible()){
							OP.seqViewer.updateList(allSelectedFrags);
						}

						repaint();
					}else{	//equal to shift+left click,add/remove single selection
						for(int i=0;i<circuitList.size();i++){
							Fragment theFragment=circuitList.get(i).checkHit(me.getX()/currentZoom+topLeftX,me.getY()/currentZoom+topLeftY);
							if(theFragment!=null){
								if(theFragment.selected){
									theFragment.deselect();
								}else{	//theFragment is not yet selected
									theFragment.select();
								}

								//update sequence viewer
								if(OP.seqViewer.isVisible()){
									OP.seqViewer.updateList(getSelectedFragments());
								}

								repaint();
								return;
							}
						}
					}
				}else if(me.isShiftDown()){	//shift key is down
					if(selectionBox){
						//select included fragments
						ArrayList<Fragment> allSelectedFrags=new ArrayList<Fragment>();
						for(int i=0;i<circuitList.size();i++){
							ArrayList<Fragment> selectedFrags=circuitList.get(i).checkHit(selectionStartX/currentZoom+topLeftX,selectionStartY/currentZoom+topLeftY,selectionEndX/currentZoom+topLeftX,selectionEndY/currentZoom+topLeftY);
							if(selectedFrags!=null){
								allSelectedFrags.addAll(selectedFrags);
							}
						}
						for(int i=0;i<allSelectedFrags.size();i++){
							allSelectedFrags.get(i).select();
						}

						//update sequence viewer
						if(OP.seqViewer.isVisible()){
							OP.seqViewer.updateList(getSelectedFragments());
						}

						repaint();
					}else{	//if no selection box,add/remove single selection
						for(int i=0;i<circuitList.size();i++){
							Fragment theFragment=circuitList.get(i).checkHit(me.getX()/currentZoom+topLeftX,me.getY()/currentZoom+topLeftY);
							if(theFragment!=null){
								if(theFragment.selected){
									theFragment.deselect();
								}else{	//theFragment is not yet selected
									theFragment.select();
								}

								//update sequence viewer
								if(OP.seqViewer.isVisible()){
									OP.seqViewer.updateList(getSelectedFragments());
								}

								repaint();
								return;
							}
						}
					}
				}else{	//no key pressed, exclusive single fragment selection
					for(int i=0;i<circuitList.size();i++){
						Fragment theFragment=circuitList.get(i).checkHit(me.getX()/currentZoom+topLeftX,me.getY()/currentZoom+topLeftY);
						if(theFragment!=null){
							//deselect all fragments
							for(int k=0;k<circuitList.size();k++){
								for(int j=0;j<circuitList.get(k).fragmentList.size();j++){
									circuitList.get(k).fragmentList.get(j).deselect();
								}
							}
							theFragment.select();
							repaint();

							//update sequence viewer
							if(OP.seqViewer.isVisible()){
								OP.seqViewer.updateList(theFragment);
							}

							return;
						}
					}
				}

				//destroy selectionBox
				if(selectionBox){
					selectionBox=false;
					repaint();
				}
			}
		});
		addMouseMotionListener(new MouseAdapter(){
			public void mouseDragged(MouseEvent me){
				//check modes
				if(modes==1){
					if(movedCircuit!=null){
						//get the distance moved
						int newMouseX=me.getX();
						int newMouseY=me.getY();
						int moveX=newMouseX-oldMouseX;
						int moveY=newMouseY-oldMouseY;
						movedCircuit.move(moveX/currentZoom,moveY/currentZoom);
						oldMouseX=newMouseX;
						oldMouseY=newMouseY;
					}
					return;
				}//else modes==0, proceed to below

				if(SwingUtilities.isLeftMouseButton(me)){
					if(me.isControlDown()||me.isShiftDown()){
						if(selectionBox){	//selectionBox is already present
							if(OP.getMousePosition()!=null){
								//expand selection box
								selectionEndX=OP.getMousePosition().x;
								selectionEndY=OP.getMousePosition().y;
								repaint();
							}
						}else{	//selectionBox is not present
							if(OP.getMousePosition()!=null){
								//create selection box
								selectionStartX=OP.getMousePosition().x;
								selectionStartY=OP.getMousePosition().y;
								selectionEndX=selectionStartX;
								selectionEndY=selectionStartY;
								selectionBox=true;
								repaint();
							}
						}
					}else{
						//panning by grabbing air
						int newMouseX=me.getX();
						int newMouseY=me.getY();
						int xPan=newMouseX-oldMouseX;
						int yPan=newMouseY-oldMouseY;
						pan(-xPan,yPan);	//"grab" is opposite to pan
						repaint();
						oldMouseX=newMouseX;
						oldMouseY=newMouseY;
					}
				}
			}
			public void mouseMoved(MouseEvent me){
				if(modes==1){	//change to mover icon
					for(int i=0;i<circuitList.size();i++){
						Fragment theFragment=circuitList.get(i).checkHit(me.getX()/currentZoom+topLeftX,me.getY()/currentZoom+topLeftY);
						if(theFragment!=null){
							setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
							return;
						}
					}
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
		});
		addMouseWheelListener(new MouseAdapter(){	//zooming in and out
			public void mouseWheelMoved(MouseWheelEvent mwe){
				int scrolled=mwe.getWheelRotation();
				if(scrolled>0){	//plus when scroll down (zoom out)
					if(currentZoom==1)return;	//the zoom out limit
					if((currentZoom-scrolled)>=1){
						zoom(-scrolled);
					}else{
						currentZoom=1;
					}
					repaint();
				}else if(scrolled<0){	//minus when scroll up (zoom in)
					int theAmount=Math.abs(scrolled);
					if(currentZoom==10)return;	//the zoom in limit
					if((currentZoom+theAmount)<=10){
						zoom(theAmount);
					}else{
						currentZoom=10;
					}
					repaint();
				}
			}
		});
		addFocusListener(new FocusAdapter(){
			public void focusGained(FocusEvent fe){
				setBorder(BorderFactory.createEtchedBorder(Color.GREEN,Color.DARK_GRAY));
			}
			public void focusLost(FocusEvent fe){
				setBorder(BorderFactory.createEtchedBorder(Color.GRAY,Color.DARK_GRAY));
			}
		});
		addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent ke){
				int theKey=ke.getKeyCode();
				if(theKey==KeyEvent.VK_UP){	//up arrow (pan up)
					pan(0,10);
					repaint();
				}else if(theKey==KeyEvent.VK_A){	//A
					if(ke.isControlDown()){	//ctrl+A (select all fragments)
						for(int i=0;i<circuitList.size();i++){
							for(int j=0;j<circuitList.get(i).fragmentList.size();j++){
								circuitList.get(i).fragmentList.get(j).select();
							}
						}

						//update sequence viewer
						if(OP.seqViewer.isVisible()){
							OP.seqViewer.updateList(getSelectedFragments());
						}
						repaint();
					}else if(theKey==KeyEvent.VK_LEFT){	//left arrow only	(pan left)
						pan(-10,0);
						repaint();
					}
				}else if(theKey==KeyEvent.VK_DOWN){	//down arrow	(pan down)
					pan(0,-10);
					repaint();
				}else if(theKey==KeyEvent.VK_RIGHT){	//right arrow	(pan right)
					pan(10,0);
					repaint();
				}else if(theKey==KeyEvent.VK_SPACE){//spacebar (show/hide selected fragments details)
					if(!isOpaque()){	//nothing to display
						return;
					}

					//show/hide sequence viewer
					if(OP.seqViewer.isVisible()){
						OP.seqViewer.setVisible(false);
					}else{
						OP.seqViewer.setVisible(true);
						OP.seqViewer.updateList(getSelectedFragments()); //update list
					}
				}else if(theKey==KeyEvent.VK_ALT){	//alt (show all fragments name)
					setNamesVisible(true);
				}
			}
			public void keyReleased(KeyEvent ke){
				int theKey=ke.getKeyCode();
				if(theKey==KeyEvent.VK_Z){	//Z (turn on/off mover)
					if(modes!=1){
						modes=1;
						OP.ST.moverButton.setSelected(true);
						//destroy selectionBox
						if(selectionBox){
							selectionBox=false;
							repaint();
						}

						for(int i=0;i<circuitList.size();i++){
							Fragment theFragment=circuitList.get(i).checkHit(MouseInfo.getPointerInfo().getLocation().x/currentZoom+topLeftX,MouseInfo.getPointerInfo().getLocation().y/currentZoom+topLeftY);
							if(theFragment!=null){
								setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
								return;
							}
						}
						setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}else{ //if ==1
						modes=0;
						OP.ST.moverButton.setSelected(false);
						setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
				}else if(theKey==KeyEvent.VK_ALT){
					setNamesVisible(false);
				}
			}
		});
	}

	private void layoutComponents(){

	}

/*
 * Method to handle the DnD action from browser panel
 */
	private void setupProperties(){
		//dnd from dirList
		setTransferHandler(new TransferHandler(){
			public boolean canImport(TransferSupport info){
				if(!info.isDataFlavorSupported(new DataFlavor(GenBank.class,"GenBank"))){
					return false;
				}
				return true;
			}
			public boolean importData(TransferSupport info){
				if(!info.isDrop())return false;
				if(!canImport(info))return false;
				Point p=info.getDropLocation().getDropPoint();
				//System.out.println("info initial  "+(p.x-170) + " " + (p.y-119));
				GenBank gb=null;
				try{
					gb=(GenBank)(info.getTransferable().getTransferData(new DataFlavor(GenBank.class,"GenBank")));	
				}catch(Exception e){
					return false;
				}
				if(gb==null)return false;
				
				double zoomRatiox = (Math.abs((double)getWidth()))/(Math.abs((double)getCanvasWidth()));
				double zoomRatioy = (Math.abs((double)getHeight()))/(Math.abs((double)getCanvasHeight()));
				//System.out.println(zoomRatiox + "  " + zoomRatioy);
				int calx = (int) Math.round(p.x/zoomRatiox);
				int caly = (int) Math.round(p.y/zoomRatioy);
				addCircuit(gb,(calx+topLeftX),(caly+topLeftY));
				dndState = true;
				return true;	//mark the completion
			}
		});

		setLayout(null);
		setFocusable(true);
		setBorder(BorderFactory.createEtchedBorder(Color.GRAY,Color.DARK_GRAY));
	}

/*
 * graphics method for drawing and updating text labels on the graph
*/
	//override
	public void paintComponent(Graphics g){
		updateImage();
		Graphics2D g2D=(Graphics2D)g;
		g2D.clearRect(0,0,getSize().width,getSize().height);
		g2D.setPaint(Color.LIGHT_GRAY);
		g2D.fill(new Rectangle(0,0,getSize().width,getSize().height));
		g2D.drawImage(image,0,0,getSize().width,getSize().height,topLeftX,topLeftY,bottomRightX,bottomRightY,null);

		//fragments name
		if(namesVisible){
			for(int i=0;i<textboxList.size();i++){	//de-establish all the textbox
				textboxList.get(i).changed=false;
			}
			for(int i=0;i<circuitList.size();i++){
				for(int j=0;j<circuitList.get(i).fragmentList.size();j++){
					TextBox theBox=circuitList.get(i).fragmentList.get(j).textbox;
					if(theBox==null)continue;
					theBox.clearOverlap(textboxList);
					theBox.updateImage();
					//box image
					int xPos=(theBox.x-this.topLeftX)*currentZoom;	//corrected x position
					int yPos=(theBox.y-this.topLeftY)*currentZoom;	//corrected y position
					g2D.drawImage(theBox.image,xPos,yPos,null);//box image
					//whisker
					g2D.setColor(Color.BLUE);
					int whiskerY;
					if(theBox.y<theBox.yAim)whiskerY=yPos+theBox.height;	//box above aim
					else whiskerY=yPos;//box below aim
					g2D.drawLine((theBox.xAim-this.topLeftX)*currentZoom,(theBox.yAim-this.topLeftY)*currentZoom,xPos+(theBox.width/2),whiskerY);
				}
			}
		}

		//selectionBox
		if(selectionBox){
			g2D.setColor(new Color(0,150,0,30));	//selectionBox color
			int selectionTopLeftX;
			int selectionTopLeftY;
			int selectionWidth;
			int selectionHeight;
			if(selectionEndX-selectionStartX<0){	//negative selection box
				selectionTopLeftX=selectionEndX;
			}else{	//positive selection box
				selectionTopLeftX=selectionStartX;
			}
			if(selectionEndY-selectionStartY<0){	//negative selection box
				selectionTopLeftY=selectionEndY;
			}else{	//positive selection box
				selectionTopLeftY=selectionStartY;
			}
			selectionWidth=Math.abs(selectionEndX-selectionStartX);
			selectionHeight=Math.abs(selectionEndY-selectionStartY);
			g2D.fill(new Rectangle(selectionTopLeftX,selectionTopLeftY,selectionWidth,selectionHeight));
		}
	}

	//override
	public void paint(Graphics g){
		if(!isOpaque())return;
		super.paint(g);
	}

	public void updateImage(){
		//detect the change
		boolean changed=false;
		for(int i=0;i<circuitList.size();i++){
			Circuit theCir=circuitList.get(i);
			if(theCir.refresh){
				theCir.updateGraphic();//update the circuit image

				theCir.refresh=false;
				changed=true;
			}
		}
		if(fresh){	//new canvas, go ahead syhtesize the background
			fresh=false;
		}else if(!changed)return;

		//redraw the entire image
		image=new BufferedImage(getSize().width,getSize().height,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2D=image.createGraphics();
		g2D.drawImage(canvas,0,0,null);	//canvas
		for(int i=0;i<circuitList.size();i++){	//circuits
			Circuit theCir=circuitList.get(i);
			g2D.drawImage(theCir.image,theCir.x,theCir.y,null);
		}
	}

	public void pan(int xPan,int yPan){
		int adjustedXPan;
		if(Math.abs(xPan/currentZoom)>1){
			adjustedXPan=xPan/currentZoom;
		}else{
			if(xPan>0)adjustedXPan=1;
			else if(xPan<0) adjustedXPan=-1;
			else adjustedXPan=0;
		}
		int adjustedYPan;
		if(Math.abs(yPan/currentZoom)>1){
			adjustedYPan=yPan/currentZoom;
		}else{
			if(yPan>0)adjustedYPan=1;
			else if(yPan<0) adjustedYPan=-1;
			else adjustedYPan=0;
		}
		//System.out.println(" topLeftX "+topLeftX + " topLeftY "+topLeftY +" bottomRightX "+ bottomRightX +" bottomRightY "+ bottomRightY + "  canvasHeight "+canvasHeight + "  canvasWidth " + canvasWidth + " getWidth " + getWidth() + " getHeight " +getHeight());
		panWithoutAdjust(adjustedXPan,adjustedYPan);
	}
	public void panWithoutAdjust(int xPan,int yPan){
		//defining page border
		if((topLeftX<=-50&&xPan>0)||(bottomRightX>=canvasWidth+50&&xPan<0)||(topLeftX>-50&&bottomRightX<canvasWidth+50)){	//50 is page border thickness
			topLeftX+=xPan;
			bottomRightX+=xPan;
		}

		if((topLeftY<=-50&&yPan<0)||(bottomRightY>=canvasHeight+50&&yPan>0)||(topLeftY>-50&&bottomRightY<canvasHeight+50)){
			topLeftY-=yPan;	//- due to reversive behaviour of swing coordination compared to Cartesan
			bottomRightY-=yPan;
		}
	}
	
	/*
	 * directly view this coordinate as topleft corner point
	 */
	public void setView(int x,int y){	
		int viewWidth=bottomRightX-topLeftX;
		int viewHeight=bottomRightY-topLeftY;
		topLeftX=x;
		topLeftY=y;
		bottomRightX=topLeftX+viewWidth;
		bottomRightY=topLeftY+viewHeight;
		//System.out.println("Set view " + " topLeftX " + topLeftX + " topLeftY " + topLeftY + " bottomRightX " + bottomRightX + " bottomRightY " + bottomRightY);
	}
	
	/*
	 * Zoom function to the canvas
	 */
	public void zoom(int amount){
		currentZoom+=amount;
		//centered zooming
		int oldViewWidth=bottomRightX-topLeftX;
		int oldViewHeight=bottomRightY-topLeftY;
		int viewWidth=getWidth()/currentZoom;
		int viewHeight=getHeight()/currentZoom;
		topLeftX=topLeftX+(oldViewWidth-viewWidth)/2;
		topLeftY=topLeftY+(oldViewHeight-viewHeight)/2;
		bottomRightX=bottomRightX-(oldViewWidth-viewWidth)/2;
		bottomRightY=bottomRightY-(oldViewHeight-viewHeight)/2;
		//System.out.println("Zoom " + amount + "  " + currentZoom   + " topLeftX "+topLeftX + " topLeftY "+topLeftY +" bottomRightX "+ bottomRightX +" bottomRightY "+ bottomRightY + "  canvasHeight "+canvasHeight + "  canvasWidth " + canvasWidth + " getWidth " + getWidth() + " getHeight " +getHeight());
	}

	/*
	 * add circuit
	 */
	public void addCircuit(GenBank genBank,int x,int y){
		Circuit theCir=new Circuit(OP,genBank,x,y);
		circuitList.add(theCir);
		//add text boxes to the list
		for(int i=0;i<theCir.fragmentList.size();i++){
			if(theCir.fragmentList.get(i).textbox!=null)textboxList.add(theCir.fragmentList.get(i).textbox);
		}
	}

	/*
	 * add circuit
	 */
	public void addCircuit(Sequence seq,int x,int y){
			Circuit theCir=new Circuit(OP,seq,x,y);
			circuitList.add(theCir);
			//add text boxes to the list
			for(int i=0;i<theCir.fragmentList.size();i++){
				if(theCir.fragmentList.get(i).textbox!=null)textboxList.add(theCir.fragmentList.get(i).textbox);
			}
		}
	/*
	 * centering a circuit while suit to zoom factor standard
	 */
	public void center(Circuit theCir){
		int theWidth=theCir.getWidth();
		int theHeight=theCir.getHeight();
		//zoom to fittable dimension
		boolean fit=true;
		while(fit){	//loop until the level that won't fit
			if(bottomRightX-topLeftX<theWidth||bottomRightY-topLeftY<theHeight){
				fit=false;
				break;
			}
			if(currentZoom==10)break;	//zoom in limit
			zoom(+1);
		}
		zoom(-1);	//go back to the previous fittable zoom
		//pan to correct location
		int viewWidth=bottomRightX-topLeftX;
		int viewHeight=bottomRightY-topLeftY;
		setView(theCir.x-(viewWidth-theWidth)/2,theCir.y-(viewHeight-theHeight)/2);
	}
	/*
	 * get the canvas width
	 */
	public int getCanvasWidth(){
		return topLeftX-bottomRightX;
	}
	/*
	 * get the canvas height
	 */
	public int getCanvasHeight(){
		return topLeftY-bottomRightY;
	}
	/*
	 * method to retrieve an arrylist of  Fragment list
	 */
	public ArrayList<Fragment> getSelectedFragments(){
		ArrayList<Fragment> selectedFrags=new ArrayList<Fragment>();
		for(int i=0;i<circuitList.size();i++){
			for(int j=0;j<circuitList.get(i).fragmentList.size();j++){
				if(circuitList.get(i).fragmentList.get(j).selected){
					selectedFrags.add(circuitList.get(i).fragmentList.get(j));
				}
			}
		}
		return selectedFrags;
	}

	public void setNamesVisible(boolean boo){
		this.namesVisible=boo;
		this.fresh=true;
		repaint();
	}
}
