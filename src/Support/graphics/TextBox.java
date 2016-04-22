package Support.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class TextBox{	//a GUI class to display textbox
	public BufferedImage image;	//image of this textbox
	Fragment ownerFragment;	//the fragment which this textbox attaches to

	public boolean changed=false;	//whether this textbox is established properly

	public int x=-123123;	//x coordinate
	public int y=-123123;	//y coordinate
	public int xAim;	//x coordinate of pointed coordinate
	public int yAim;	//y coordinate of pointed coordinate
	public int xDeviation=-123123;	//deviation of the x coordinate from xAim
	public int yDeviation=-123123;	//deviation of the y coordinate from yAim
	public int width;
	public int height;
	int inset;	//inset
	Font mainFont=new Font("Calibri",Font.PLAIN,9);

	public TextBox(Fragment ownerFragment){	//initialized with the ownerFragment
		this.ownerFragment=ownerFragment;
		image=new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
		this.width=35;
		this.height=20;
		this.inset=5;
		updateImage();
	}

	public void clearOverlap(ArrayList<TextBox> textboxList){	//get closest non-colliding with other box or fragments position
		//update aim location
		this.xAim=ownerFragment.theCir.x+ownerFragment.x+(ownerFragment.W/2);
		this.yAim=ownerFragment.theCir.y+ownerFragment.y+(ownerFragment.thickness/2);

		//initial assumed deviations
		if(x==-123123&&y==-123123&&xDeviation==-123123&&yDeviation==-123123){
			Random seed=new Random();
			int chance=seed.nextInt(6);
			if(chance<3)this.xDeviation=-5*(chance+1)-this.width;
			else this.xDeviation=5*(chance-2);
			chance=seed.nextInt(6);
			if(chance<3)this.yDeviation=-10*(chance+1)-this.height;
			else this.yDeviation=10*(chance-2);
		}

		//assumed position
		this.x=xAim+xDeviation;
		this.y=yAim+yDeviation;

		//check for overlap and slip the box if overlap occurs (Sequeeze Model)
		boolean overlap=true;
		while(overlap){
			overlap=false;
			int squeezeDirection=-100;	//remain a constant squeeze direction (in degree)
			//check if overlap
			for(int i=0;i<textboxList.size();i++){
				TextBox checkedBox=textboxList.get(i);
				if(!checkedBox.changed)continue;	//skip not established box
				//calculate squeeze direction
				if(checkOverlap(this,checkedBox)){
					i=0;	//as long as one is overlapped,the entire check repeats
					if(squeezeDirection==-100){
						Rectangle thisRec=new Rectangle(this.x,this.y,this.width,this.height);
						Rectangle anotherRec=new Rectangle(checkedBox.x,checkedBox.y,checkedBox.width,checkedBox.height);
						squeezeDirection=(int)(Math.atan2(anotherRec.getCenterY()-thisRec.getCenterY(),anotherRec.getCenterX()-thisRec.getCenterX())*180/Math.PI);
					}
				}
				//squeeze it out of the checkedbox
				while(checkOverlap(this,checkedBox)){
					int transX=(int)(Math.cos(squeezeDirection*Math.PI/180)*10);	//5px distance
					int transY=(int)(Math.sin(squeezeDirection*Math.PI/180)*10);	//5px distance
					this.xDeviation+=transX;
					this.yDeviation+=transY;
					this.x=xAim+xDeviation;
					this.y=yAim+yDeviation;
				}
			}
		}

		this.changed=true;	//established
	}

	private boolean checkOverlap(TextBox thisBox,TextBox anotherBox){
		Rectangle thisRec=new Rectangle(thisBox.x,thisBox.y,thisBox.width,thisBox.height);
		Rectangle anotherRec=new Rectangle(anotherBox.x,anotherBox.y,anotherBox.width,anotherBox.height);
		if(thisRec.intersects(anotherRec)){
			return true;
		}
		return false;
	}

	public void updateImage(){
		image=new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2D=(Graphics2D)image.getGraphics();
		//background
		g2D.setColor(Color.YELLOW);
		g2D.fillRect(1,1,width-2,height-2);	//with 1 pixels borders
		//border
		g2D.setColor(Color.BLUE);
		g2D.drawRect(0,0,width-1,height-1);	//final px does not count

		//details
		g2D.setFont(mainFont);
		g2D.setColor(Color.BLUE);
		//gene name
		try{
			g2D.drawString(ownerFragment.theSeq.gene,inset,inset+8);
		}catch(Exception e){
			g2D.drawString(ownerFragment.theSeq.type,inset,inset+8);
		}
	}
}
