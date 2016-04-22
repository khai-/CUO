package Support.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import Support.OperationPanel;
import Support.parser.Sequence;

public class Fragment{
	BufferedImage image;
	Graphics2D g2D;
	OperationPanel op;
	Circuit theCir;
	public TextBox textbox;	//textbox attached to this fragment showing its details
	public Sequence theSeq;	//sequence represented by this fragment
	public boolean selected=false;	//whether this gene is selected
	boolean selectedBefore=false;	//whether this gene is selected before

	int x;	//relative position in the circuit
	int y;
	int length;
	int oldW=0;
	int W; //the width of this fragment in pixel
	int oldThickness=0;
	int thickness=-1;
	double oldPixelPerLength=-1;
	double pixelPerLength=0.003;	//graphic size per base pair (also depends on zoom level)


	String type;	//type of fragment: intron,protein,tRNA,rRNA,mobile element,mRNA,uncategorized
	Color color;	//color of this fragment;
	Fragment beforeFrag;	//fragment before this fragment
	Fragment afterFrag;	//fragment after this fragment
	Point[] anchors;	//[0]left top [1]left bottom [2]right top [3]right bottom

	public Fragment(OperationPanel op,Circuit theCir,Sequence seq){
		image=new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
		this.theCir=theCir;
		this.theSeq=seq;
		if(!theSeq.type.equals("intron"))this.textbox=new TextBox(this);
		this.length=seq.getLength();
		this.type=seq.type;
		this.op=op;

		//set color of fragment
		if(type.equals("intron")){	//intron
			this.color=Color.BLACK;
		}else if(type.equals("CDS")){	//protein
			this.color=new Color(205,79,57);//tomato3
		}else if(type.equals("tRNA")){	//tRNA
			this.color=Color.BLUE;
		}else if(type.equals("rRNA")){	//rRNA
			this.color=new Color(104,34,139);//purple
		}else if(type.equals("mobile_element")){	//mobile element
			this.color=new Color(0,134,139);//turquoise4
		}else if(type.equals("mRNA")){	//mRNA
			this.color=Color.ORANGE;
		}else{	//uncategorized
			this.color=new Color(77,77,77);//grey30
		}

		//Graphics2D
		g2D=image.createGraphics();
		updateGraphic();
	}

	public void updateGraphic(){	//method to update graphic
		boolean changed=false;	//to trace whether something has changed

		//update data

		//update size
		if(pixelPerLength!=oldPixelPerLength){
			changed=true;
			oldPixelPerLength=pixelPerLength;

			//length
			W=(int)(pixelPerLength*length);
			if(W==0)W=1;	//allow the fragment to be seen at least

			//thickness
			if(type.equals("intron")){
				thickness=(int)((6/0.003)*pixelPerLength);	//6 is the minimum
			}else{
				thickness=(int)((12/0.003)*pixelPerLength);	//12 is the minimum
			}
		}

		//update color

		//update glow
		if(selectedBefore!=selected){
			changed=true;
		}

		//redraw the image
		if(changed){
			image=new BufferedImage(W,thickness,BufferedImage.TYPE_INT_ARGB);
			g2D=image.createGraphics();

			//the fragment body
			g2D.setColor(color);
			g2D.fillRect(0,0,W,thickness);


			//glow
			if(selected){
//System.out.println("GLOW!");
				g2D.setStroke(new BasicStroke(2));
				g2D.setColor(new Color(0,255,0,180));
				g2D.draw(new Rectangle(0,0,W,thickness));
			}

			theCir.refresh=true;
		}
	}

	public boolean checkHit(int x,int y){	//point check hit
		if((x>=theCir.x+this.x&&x<theCir.x+this.x+W)&&(y>=theCir.y+this.y&&y<theCir.y+this.y+thickness)){
			return true;
		}
		return false;
	}

	public boolean checkHit(int tlx,int tly,int brx,int bry){	//area check hit
		int selectionTopLeftX;
		int selectionTopLeftY;
		int selectionBottomRightX;
		int selectionBottomRightY;
		if(brx-tlx<0){	//negative selection box
			selectionTopLeftX=brx;
			selectionBottomRightX=tlx;
		}else{	//positive selection box
			selectionTopLeftX=tlx;
			selectionBottomRightX=brx;
		}
		if(bry-tly<0){	//negative selection box
			selectionTopLeftY=bry;
			selectionBottomRightY=tly;
		}else{	//positive selection box
			selectionTopLeftY=tly;
			selectionBottomRightY=bry;
		}

		if(selectionTopLeftX<=theCir.x+this.x+W&&selectionBottomRightX>=theCir.x+this.x&&selectionTopLeftY<=theCir.y+this.y+thickness&&selectionBottomRightY>=theCir.y+this.y){
			return true;
		}
		return false;
	}

	public void select(){	//selected by a mouse
//System.out.println("HIT!");
//System.out.println((theCir.x+this.x)+":"+(theCir.x+this.x+W)+" : "+(theCir.y+this.y)+":"+(theCir.y+this.y+thickness));
		if(selected){
			return;
		}else{
			selectedBefore=selected;
			selected=true;
		}
		updateGraphic();
		theCir.refresh=true;
	}

	public void deselect(){	//deselected
		if(!selected){
			return;
		}else{
			selectedBefore=selected;
			selected=false;
		}
		updateGraphic();
		theCir.refresh=true;
	}

	public void setLocation(int x,int y){

	}

	//public Point getLocation(){

	//}
}
