package converters;
import datastructures.structure.Structure;

import resources.structure.writers.StructureOutputStream;
import resources.structure.writers.BinaryDataWriter;
import resources.structure.readers.StructureInputStream;
import resources.structure.readers.XMLDataReader;
import datastructures.structure.StructureType;
import resources.structure.readers.XMLTypeReader;
import resources.locating.ClasspathCollection;
import resources.locating.ResourceCollection;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.Map;
import java.util.List;
import java.io.IOException;

/**
 * This program will convert a .sdx (Structured Data XML) particle data file to .sdb (Structured Data Binary).
 * @author Benjamin
 */
public final class ConvertSDXtoSDB{
	
	/**
	 * This will convert .sdx particle data to .sdb.
	 * @param args {name of the sdx file to read, name of the sdb file to write}.
	 * @throws IOException If there is a problem reading the sdx file or writing the sdb file.
	 */
	public static void main(String[] args) throws IOException{
		//parse arguments
		String srcNam = null;
		String dstNam = null;
		for(int i = 0; i<args.length; i++){
			String curArg = args[i];
			if(curArg.startsWith("-src=")){srcNam = curArg.substring(5);}
			else if(curArg.startsWith("-dst=")){dstNam = curArg.substring(5);}
		}
		if(dstNam == null || srcNam==null){
			throw new IllegalArgumentException("Usage: java converters.ConvertSDXtoSDB -src=source_file -dst=output_file");
		}
		
		//read in the type definitions
		ResourceCollection classpath = new ClasspathCollection(null, "", "classpath");
		String[] typeFileName = new String[]{"typedefs", "TrajectoryData.stx"};
		XMLTypeReader typeRead = new XMLTypeReader(classpath);
		List<StructureType> particleTypes = typeRead.readTypes(classpath.getResource(typeFileName));
		Map<String, StructureType> particleTypeMap = typeRead.wrapTypes(particleTypes);
		
		//open up the sdx for reading
		XMLDataReader inputManage = new XMLDataReader(particleTypeMap);
		StructureInputStream sdxIn = inputManage.openStructureFile(new BufferedInputStream(new FileInputStream(srcNam)));
		
		//open up the sdb for writing
		BinaryDataWriter outputManage = new BinaryDataWriter(particleTypes);
		StructureOutputStream sdbOut = outputManage.prepStructureFile(new BufferedOutputStream(new FileOutputStream(dstNam)));
		
		//pipe the sdx to the sdb
		Structure cur = sdxIn.readNextEntry();
		while(cur!=null){
			sdbOut.writeNextEntry(cur);
			cur = sdxIn.readNextEntry();
		}
		
		sdxIn.close();
		sdbOut.close();
	}
	
	private ConvertSDXtoSDB(){
		//force pure static
	}
}