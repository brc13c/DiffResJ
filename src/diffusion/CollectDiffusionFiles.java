package diffusion;
import datastructures.structure.Structure;

import resources.structure.readers.StructureInputStream;
import resources.structure.writers.StructureOutputStream;
import resources.structure.writers.BinaryDataWriter;
import resources.structure.readers.BinaryDataReader;
import datastructures.structure.StructureType;
import resources.structure.readers.XMLTypeReader;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import resources.locating.ClasspathCollection;
import resources.locating.ResourceCollection;

/**
 * This will collect multiple diffusion files into a single file.
 * @author Benjamin
 */
public final class CollectDiffusionFiles{
	
	/**
	 * This will collect multiple diffusion files into a single file.
	 * @param args {outputFile, inputFile1, [inputFile2 ...]}
	 * @throws IOException If there is a problem reading or writing.
	 */
	public static void main(String[] args) throws IOException{
		//parse arguments
		String dstNam = null;
		List<String> allInputs = new ArrayList<String>();
		for(int i = 0; i<args.length; i++){
			String curArg = args[i];
			if(curArg.startsWith("-src=")){allInputs.add(curArg.substring(5));}
			else if(curArg.startsWith("-dst=")){dstNam = curArg.substring(5);}
		}
		if(dstNam == null || allInputs.size()==0){
			throw new IllegalArgumentException("Usage: java diffusion.CollectDiffusionFiles -dst=output_file [-src=input_file]+");
		}
		
		//read in types
		ResourceCollection classpath = new ClasspathCollection(null, "", "classpath");
		XMLTypeReader typeRead = new XMLTypeReader(classpath);
		String[] diffFileName = new String[]{"typedefs", "DiffusionCoeffs.stx"};
		List<StructureType> difCofTypes = typeRead.readTypes(classpath.getResource(diffFileName));
		BinaryDataReader inputManage = new BinaryDataReader(difCofTypes);
		BinaryDataWriter outputManage = new BinaryDataWriter(difCofTypes);
		
		//open the output
		StructureOutputStream toWrite = outputManage.prepStructureFile(new BufferedOutputStream(new FileOutputStream(dstNam)));
		//run through all the inputs
		for(int i = 0; i<allInputs.size(); i++){
			StructureInputStream toRead = inputManage.openStructureFile(new BufferedInputStream(new FileInputStream(allInputs.get(i))));
			Structure curStruct = toRead.readNextEntry();
			while(curStruct!=null){
				toWrite.writeNextEntry(curStruct);
				curStruct = toRead.readNextEntry();
			}
			toRead.close();
		}
		toWrite.close();
	}
	
	private CollectDiffusionFiles(){
		//force pure static
	}
}