package Support.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class Mfa{	//MultiFASTA file : multiple fna
	public String fileName;	//the file path
	public String name;
	public ArrayList<Fna> fnaList=new ArrayList<Fna>();	//list of contained fna

	public Mfa(String fileName)throws Exception{	//initialized by reading a .mfa file
		this.fileName=fileName;
		File f=new File(fileName);

		BufferedReader br=new BufferedReader(new FileReader(f));
		String nextLine;
		Fna theFna=null;
		while((nextLine=br.readLine())!=null){
			if(nextLine.startsWith(">")){
				//add the previous Fna to the list
				if(theFna!=null)fnaList.add(theFna);

				//parse the new Fna data
				theFna=new Fna();	//manual creation and loading
				String[] temp=nextLine.replaceFirst(">","").split("\\|");
				theFna.name=temp[temp.length-1].trim();
				if(temp[0].equals("type")){
					theFna.type=temp[1];
					//decode more info from header for specific types
					if(theFna.type.equals("Restriction")){
						theFna.topCut=Integer.parseInt(temp[3]);
						theFna.bottomCut=Integer.parseInt(temp[5]);
					}
				}else{
					theFna.type="Gene";
				}
			}else{	//sequence lines
				theFna.sequence+=nextLine.replaceAll(" ","").replaceAll("\n","").toUpperCase();
			}
		}
		//add the previous Fna to the list
		if(theFna!=null)fnaList.add(theFna);

		br.close();
	}

	public Mfa(GenBank genBank){	//convert a genBank to a mfa
		this.fileName=genBank.fileName;
		//make fnaList from sequences
		for(int i=0;i<genBank.sequences.size();i++){
			this.fnaList.add(new Fna(genBank.sequences.get(i)));
		}
	}

	public Mfa(){}	//manual creation

	public void save()throws Exception{	//auto overwrite current "fileName"
		File f=new File(fileName);

		BufferedWriter bw=new BufferedWriter(new FileWriter(f));
		for(int i=0;i<fnaList.size();i++){	//iterate through each fna in the list
			Fna theFna=fnaList.get(i);
			//header
			if(theFna.type.equals("Restriction")){	//site
				bw.write(">type|"+theFna.type+"|topCut|"+theFna.topCut+"|bottomCut|"+theFna.bottomCut+"|"+theFna.name+"\n");
			}else{	//regular gene
				bw.write(">type|"+theFna.type+"|"+theFna.name+"\n");
			}

			//fna sequence
			for(int j=0;j<theFna.sequence.length();j+=10){
				if(j+10<theFna.sequence.length()){
					bw.write(theFna.sequence.substring(j,j+10));
					if(((j+10)%100)==0){
						bw.write("\n");
					}else{
						bw.write(" ");
					}
				}else{	//the final bits
					bw.write(theFna.sequence.substring(j,theFna.sequence.length())+"\n");
				}
			}	
		}

		bw.close();
	}

	public String getCompleteSequence(){
		String completeSequence="";
		for(int i=0;i<fnaList.size();i++){
			completeSequence+=fnaList.get(i).sequence;
		}
		return completeSequence;
	}
}
