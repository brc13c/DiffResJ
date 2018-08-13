package vac;
import datastructures.structure.Structure;

import resources.structure.readers.StructureInputStream;
import resources.structure.writers.StructureOutputStream;
import resources.structure.writers.BinaryDataWriter;
import datastructures.structure.StructureType;
import resources.structure.readers.BinaryDataReader;
import resources.structure.readers.XMLTypeReader;
import resources.locating.ClasspathCollection;
import resources.locating.ResourceCollection;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * This will take integrated autocorrelations from several trajectory files (created by using splits.SplitData)
 * and collect them into one file.
 * @author Benjamin
 */
public final class CollectAutocorrelations{
	
	/**
	 * This will collect several integrated autocorrelations into one file.
	 * @param args {output file, input file 1, input file 2, ...}
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
		if(dstNam == null || allInputs.size() == 0){
			throw new IllegalArgumentException("Usage: java vac.CollectAutocorrelations -dst=collected_file -src=file1 [-src=filei]*");
		}
		//read in the type definitions
		ResourceCollection classpath = new ClasspathCollection(null, "", "classpath");
		String[] typeFileName = new String[]{"typedefs", "IntegratedAutocorrelations.stx"};
		XMLTypeReader typeRead = new XMLTypeReader(classpath);
		List<StructureType> intVACTypes = typeRead.readTypes(classpath.getResource(typeFileName));
		BinaryDataReader inputManage = new BinaryDataReader(intVACTypes);
		BinaryDataWriter outputManage = new BinaryDataWriter(intVACTypes);
		
		//open the collected file
		StructureOutputStream output = outputManage.prepStructureFile(new BufferedOutputStream(new FileOutputStream(dstNam)));
		//open the scattered files
		StructureInputStream[] inputs = new StructureInputStream[allInputs.size()];
		Structure[] curStructs = new Structure[allInputs.size()];
		for(int i = 0; i<allInputs.size(); i++){
			inputs[i] = inputManage.openStructureFile(new BufferedInputStream(new FileInputStream(allInputs.get(i))));
			curStructs[i] = inputs[i].readNextEntry();
		}
		
		boolean wasStuff;
		do {
			wasStuff = false;
			for(int i = 0; i<inputs.length; i++){
				if(curStructs[i]!=null){
					wasStuff = true;
					output.writeNextEntry(curStructs[i]);
					curStructs[i] = inputs[i].readNextEntry();
				}
			}
		} while (wasStuff);
		
		output.close();
		for(StructureInputStream input : inputs){
			input.close();
		}
	}
	
	private CollectAutocorrelations(){
		//force pure static
	}
}