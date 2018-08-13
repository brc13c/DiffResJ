package splits;
import datastructures.structure.Structure;

import resources.structure.writers.StructureOutputStream;
import resources.structure.writers.BinaryDataWriter;
import resources.structure.readers.StructureInputStream;
import resources.structure.readers.BinaryDataReader;
import datastructures.structure.StructureType;
import resources.structure.readers.XMLTypeReader;
import resources.locating.ClasspathCollection;
import resources.locating.ResourceCollection;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * This will split a particle simulation file into multiple files.
 * @author Benjamin
 */
public final class SplitData{
	
	/**
	 * This splits particle simulation data (as .sdb files) into multiple files (by particle, again .sdb).
	 * The split is done in rotating order [1,4,7...],[2,5,8...],[3,6,9...]; not [1,2,3...][...][...].
	 * @param args {name of the file to split, number of files to split it into, created file prefix}
	 * @throws IOException If there is a problem reading or writing the files.
	 */
	public static void main(String[] args) throws IOException{
		//parse arguments
		String srcNam = null;
		String numStr = null;
		String outPre = null;
		for(int i = 0; i<args.length; i++){
			String curArg = args[i];
			if(curArg.startsWith("-src=")){srcNam = curArg.substring(5);}
			else if(curArg.startsWith("-num=")){numStr = curArg.substring(5);}
			else if(curArg.startsWith("-dpr=")){outPre = curArg.substring(5);}
		}
		if(srcNam == null || numStr == null || outPre == null){
			throw new IllegalArgumentException("Usage: java splits.SplitData -src=input_trajectory -num=number_of_splits -dpr=output_file_prefix");
		}
		
		//parse arguments
		String inputFile = srcNam;
		int numFiles = Integer.parseInt(numStr);
		int numDigits = numStr.length();
		String outputPrefix = outPre;
		
		//read in the type definitions
		ResourceCollection classpath = new ClasspathCollection(null, "", "classpath");
		String[] typeFileName = new String[]{"typedefs", "TrajectoryData.stx"};
		XMLTypeReader typeRead = new XMLTypeReader(classpath);
		List<StructureType> particleTypes = typeRead.readTypes(classpath.getResource(typeFileName));
		
		//open the file
		BinaryDataReader inputManage = new BinaryDataReader(particleTypes);
		StructureInputStream input = inputManage.openStructureFile(new BufferedInputStream(new FileInputStream(inputFile)));
		
		//create the output files
		BinaryDataWriter outputManage = new BinaryDataWriter(particleTypes);
		StructureOutputStream[] outputs = new StructureOutputStream[numFiles];
		for(int i = 0; i<outputs.length; i++){
			String fullNum = Integer.toString(i);
			while(fullNum.length() < numDigits){
				fullNum = "0" + fullNum;
			}
			String newFileName = outputPrefix + fullNum + ".mdp.sdb";
			outputs[i] = outputManage.prepStructureFile(new BufferedOutputStream(new FileOutputStream(newFileName)));
		}
		
		int curFile = 0;
		Structure cur = input.readNextEntry();
		while(cur!=null){
			if("TIME".equals(cur.getType().getName())){
				//time resets the shuffle, and get sent to everybody
				curFile = 0;
				for(int i = 0; i<outputs.length; i++){
					outputs[i].writeNextEntry(cur);
				}
			}
			else if("PARTICLE".equals(cur.getType().getName())){
				//push to file, then rotate file
				outputs[curFile].writeNextEntry(cur);
				curFile = (curFile + 1) % numFiles;
			}
			//else skip anything I don't understand
			
			cur = input.readNextEntry();
		}
		
		input.close();
		for(int i = 0; i<outputs.length; i++){
			outputs[i].close();
		}
	}
	
	private SplitData(){
		//force pure static
	}
}