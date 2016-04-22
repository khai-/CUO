package Support.graphics;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import Support.Common;
import Support.OperationPanel;
import Support.parser.Fna;
import Support.parser.GenBank;
import Support.parser.Mfa;
import Support.parser.Sequence;

public class Circuit{	//container class to store multiple fragments
	public String name;	//name of this circuit
	public GenBank genBank;
	public ArrayList<Fragment> fragmentList=new ArrayList<Fragment>();
	Graphics2D g2D;
	OperationPanel op;
	public BufferedImage image;

	public boolean refresh=true;	//the need to redraw this graphics

	public int oldX=0;
	public int oldY=0;
	public int x;
	public int y;
	public int width=0;
	public int thickness=0;
	String dirName; //
	
	public Circuit(OperationPanel op,GenBank genBank,int x,int y){	//initiate circuit with a genBank
		this.op=op;
		this.genBank=genBank;
		this.name=genBank.name;
		setLocation(x,y);
		ArrayList<Sequence> seqList=Common.makeSequence(genBank);
		for(int i=0;i<seqList.size();i++){
			fragmentList.add(new Fragment(op,this,seqList.get(i)));
		}
	}
	public Circuit(OperationPanel op,Sequence seq,int x,int y){	//initiate circuit with a sequence
		this.op=op;
		this.genBank=new GenBank(seq);
		this.name=seq.gene;
		setLocation(x,y);
		ArrayList<Sequence> seqList=Common.makeSequence(seq);
		for(int i=0;i<seqList.size();i++){
			fragmentList.add(new Fragment(op,this,seqList.get(i)));
		}
	}

	public void updateGraphic(){
		int newWidth=0;
		int thickest=0;
		for(int i=0;i<fragmentList.size();i++){
			fragmentList.get(i).updateGraphic();	//update fragments first
			newWidth+=fragmentList.get(i).W;
			if(fragmentList.get(i).thickness>thickest){
				thickest=fragmentList.get(i).thickness;
			}
		}

		if(newWidth==0)return;

		image=new BufferedImage(newWidth,thickest,BufferedImage.TYPE_INT_ARGB);
		this.width=newWidth;
		this.thickness=thickest;
		g2D=image.createGraphics();

		drawLinearCircuit();
	}

	private void drawLinearCircuit(){
		int widthRendered=0;
		for(int i=0;i<fragmentList.size();i++){
			Fragment theFrag=fragmentList.get(i);
			// draw the fragment
			if(theFrag.type.equals("intron")){
				g2D.drawImage(theFrag.image,null,widthRendered,3);
				theFrag.y=3;	//relative position
			}else{
				g2D.drawImage(theFrag.image,null,widthRendered,0);
				theFrag.y=0;	//relative position
			}
			theFrag.x=widthRendered;	//relative position
			widthRendered+=theFrag.W;
		}
	}

	public void add(GenBank genBank){	//add extra genBank data to the end

	}
	public void add(Sequence seq){	//add extra sequence data to the end

	}
	public void add(GenBank genBank,int index){	//add extra genBank at specific index squeezing all to bottom

	}
	public void add(Sequence sequence,int index){	//add extra sequence at specific index squeezing all to bottom

	}

	public int getWidth(){	//get width of this entire circuit
		int theWidth=0;
		for(int i=0;i<fragmentList.size();i++){
			theWidth+=fragmentList.get(i).W;
		}
		return theWidth;
	}

	public int getHeight(){	//get the highest thickness
		int thickest=0;
		for(int i=0;i<fragmentList.size();i++){
			if(fragmentList.get(i).thickness>thickest)thickest=fragmentList.get(i).thickness;
		}
		return thickest;
	}

	public void setLocation(int x,int y){
		this.x=x;
		this.y=y;
		refresh=true;
		op.repaint();
	}

	public void move(int moveX,int moveY){	//move the circuit by x,y translation
		if(x+moveX>0&&x+width+moveX<op.canvas.canvasWidth){//check for stage boundary condition
			x+=moveX;
		}
		if(y+moveY>0&&y+thickness+moveY<op.canvas.canvasHeight){//check for stage boundary condition
			y+=moveY;
		}
		refresh=true;
		op.repaint();
	}

	public Fragment checkHit(int x,int y){	//check whether this circuit is hit,if yes,which fragment is hit
		for(int i=0;i<fragmentList.size();i++){
			if(fragmentList.get(i).checkHit(x,y)){
				return fragmentList.get(i);
			}
		}
		return null;
	}

	public ArrayList<Fragment> checkHit(int tlx,int tly,int brx,int bry){	//Aoe checkHit
		ArrayList<Fragment> selectedFrags=new ArrayList<Fragment>();
		for(int i=0;i<fragmentList.size();i++){
			if(fragmentList.get(i).checkHit(tlx,tly,brx,bry)){
				selectedFrags.add(fragmentList.get(i));
			}
		}
		if(selectedFrags.size()>0){
			return selectedFrags;
		}else{
			return null;
		}
	}

	public void save(File directory){	//save the circuit in the directory
		//if more than one fragment, save as multifna
		if(fragmentList.size()>1){
			Mfa mfa=new Mfa(genBank);
			mfa.fileName=directory.getPath()+File.separator+this.name+".mfa";
			try{
				mfa.save();
			}catch(Exception e){
				JOptionPane.showMessageDialog(null,"Saving "+mfa.fileName+" failed.","Error",JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		//if only one fragment, save as fna
		else if(fragmentList.size()==1){
			Fna fna=new Fna(genBank.sequences.get(0));	//the only sequence
			fna.fileName=directory.getPath()+File.separator+this.name+".fna";
			try{
				fna.save();
			}catch(Exception e){
				JOptionPane.showMessageDialog(null,"Saving "+fna.fileName+" failed.","Error",JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		//assign the new dirName of associated genBank
		dirName=directory.getPath()+File.separator+this.name;
		if(fragmentList.size()>1){
			dirName+=".mfa";
		}else if(fragmentList.size()==1){
			dirName+=".fna";
		}
		String rootFolder=new File(".").getAbsolutePath().substring(0,new File(".").getAbsolutePath().length()-2);	//rootFolder directory of CUO
		if(dirName.startsWith(rootFolder)){
			dirName="."+dirName.replace(rootFolder,"");
		}
		this.genBank.dirName=dirName;
		System.out.println(dirName);
	}
	
	public String getSavdFileName()
	{
		return dirName;
	}
}
