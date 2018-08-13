package msd;
import datastructures.structure.Structure;

import resources.structure.readers.StructureInputStream;
import resources.structure.writers.StructureOutputStream;
import resources.structure.writers.BinaryDataWriter;
import resources.structure.readers.BinaryDataReader;
import datastructures.structure.StructureType;
import resources.structure.readers.XMLTypeReader;
import resources.locating.ClasspathCollection;
import resources.locating.ResourceCollection;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * This will take square displacements from several trajectory files (created by using splits.SplitData)
 * and collect them into one file.
 * @author Benjamin
 */
public final class CollectSquareDisplacement{
	
	/**
	 * This will collect several square displacement files into one file.
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
		if(dstNam == null || allInputs.size()==0){
			throw new IllegalArgumentException("Usage: java msd.CollectSquareDisplacement -dst=output_file [-src=input_file]+");
		}
		//read in the type definitions
		ResourceCollection classpath = new ClasspathCollection(null, "", "classpath");
		String[] typeFileName = new String[]{"typedefs", "SquareDisplacement.stx"};
		XMLTypeReader typeRead = new XMLTypeReader(classpath);
		List<StructureType> squareDTypes = typeRead.readTypes(classpath.getResource(typeFileName));
		BinaryDataReader inputManage = new BinaryDataReader(squareDTypes);
		BinaryDataWriter outputManage = new BinaryDataWriter(squareDTypes);
		
		//open the collected file
		StructureOutputStream output = outputManage.prepStructureFile(new BufferedOutputStream(new FileOutputStream(dstNam)));
		//open the scattered files
		StructureInputStream[] inputs = new StructureInputStream[allInputs.size()];
		Structure[] curStructs = new Structure[allInputs.size()];
		for(int i = 0; i<allInputs.size(); i++){
			inputs[i] = inputManage.openStructureFile(new BufferedInputStream(new FileInputStream(allInputs.get(i))));
			curStructs[i] = inputs[i].readNextEntry();
		}
		
		boolean isMore;
		do{
			//first, check that not everything is null
			boolean allNull = true;
			for(int i = 0; i<curStructs.length; i++){
				if(curStructs[i] != null){
					allNull = false;
					break;
				}
			}
			if(allNull){
				isMore = false;
				break;
			}
			else{
				isMore = true;
			}
			//run through, writing everything that isn't lag or null
			boolean allTime = true;
			for(int i = 0; i<inputs.length; i++){
				if(curStructs[i] != null && !"TIME".equals(curStructs[i].getType().getName())){
					allTime = false;
					output.writeNextEntry(curStructs[i]);
					curStructs[i] = inputs[i].readNextEntry();
				}
			}
			//if time, write the lag and read next entries
			if(allTime){
				output.writeNextEntry(curStructs[0]);
				for(int i = 0; i<inputs.length; i++){
					curStructs[i] = inputs[i].readNextEntry();
				}
			}
		} while(isMore);
		
		output.close();
		for(StructureInputStream input : inputs){
			input.close();
		}
	}
	
	private CollectSquareDisplacement(){
		//force pure static
	}
}