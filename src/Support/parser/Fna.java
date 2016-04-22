package Support.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
	
public class Fna{	//fasta nucleic acid file
	public String fileName;
	public String sequence="";
	public String name;
	public String type;

	//site
	public int topCut=-1;	//the cut position of the site at the coding strand (-1 for no cut)
	public int bottomCut=-1;	//the cut position of the site at the template strand

	public Fna(String fileName)throws Exception{	//initialized by reading a file
		this.fileName=fileName;
		File f=new File(fileName);

		BufferedReader br=new BufferedReader(new FileReader(f));
		//decode header
		String[] temp=br.readLine().replaceFirst(">","").split("\\|");
		name=temp[temp.length-1].trim();
		if(temp[0].equals("type")){
			type=temp[1];
			//decode more info from header for specific types
			if(type.equals("Restriction")){
				topCut=Integer.parseInt(temp[3]);
				bottomCut=Integer.parseInt(temp[5]);
			}
		}else{
			type="Gene";
		}

		//decode sequence
		String nextLine;
		while((nextLine=br.readLine())!=null){
			sequence+=nextLine.replaceAll(" ","").replaceAll("\n","").toUpperCase();
		}
		br.close();
	}

	public Fna(Sequence seq){	//convert a sequence to fna
		this.fileName=seq.genBank.fileName;
		this.sequence=seq.getTranscriptInLine(seq.genBank.completeSequence);
		this.name=seq.gene;
		this.type=seq.type;
	}

	public Fna(){}	//manual creation

	public void save()throws Exception{	//auto overwrite current "fileName"
		File f=new File(fileName);

		BufferedWriter bw=new BufferedWriter(new FileWriter(f));
		//header
		if(type.equals("Restriction")){	//site
			bw.write(">type|"+type+"|topCut|"+topCut+"|bottomCut|"+bottomCut+"|"+name+"\n");
		}else{	//regular gene
			bw.write(">type|"+type+"|"+name+"\n");
		}

		//fna sequence
		for(int i=0;i<sequence.length();i+=10){
			if(i+10<sequence.length()){
				bw.write(sequence.substring(i,i+10));
				if(((i+10)%100)==0){
					bw.write("\n");
				}else{
					bw.write(" ");
				}
			}else{	//the final bits
				bw.write(sequence.substring(i,sequence.length()));
			}
		}
		bw.close();
	}

	public void saveAs(File f){

	}
}
