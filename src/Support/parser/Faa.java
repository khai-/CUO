package Support.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class Faa{	//fasta amino acid file
	public String fileName;
	public String AA="";
	public String name;
	public String type;

	public Faa(String fileName)throws Exception{	//initialized by reading a file
		this.fileName=fileName;
		File f=new File(fileName);

		BufferedReader br=new BufferedReader(new FileReader(f));

		//decode header
		String[] temp=br.readLine().replaceFirst(">","").split("\\|");
		name=temp[temp.length-1].trim();
		if(temp[0].equals("type")){
			type=temp[1];
		}else{
			type="Gene";
		}

		//decode sequence
		String nextLine;
		while((nextLine=br.readLine())!=null){
			AA+=nextLine.replaceAll(" ","").replaceAll("\n","").toUpperCase();
		}
		br.close();
	}

	public Faa(){}	//manual creation

	public void save()throws Exception{	//auto overwrite current "fileName"
		File f=new File(fileName);

		BufferedWriter bw=new BufferedWriter(new FileWriter(f));
		//header
		bw.write(">type|"+type+"|"+name+"\n");
		//aa sequence
		for(int i=0;i<AA.length();i+=10){
			if(i+10<AA.length()){
				bw.write(AA.substring(i,i+10));
				if(((i+10)%100)==0){
					bw.write("\n");
				}else{
					bw.write(" ");
				}
			}else{	//the final bits
				bw.write(AA.substring(i,AA.length()));
			}
		}
		bw.close();
	}

	public void saveAs(File f){

	}
}
