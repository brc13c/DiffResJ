package converters;
import java.io.FileWriter;
import java.io.Writer;
import datastructures.structure.Structure;
import resources.structure.readers.StructureInputStream;
import resources.structure.readers.BinaryDataReader;
import datastructures.structure.StructureType;
import resources.structure.readers.XMLTypeReader;
import resources.locating.ClasspathCollection;
import resources.locating.ResourceCollection;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.List;
import java.io.IOException;

/**
 * This program will convert a .sdb (Structured Data Binary) diffusion coefficient sample file to .csv.
 * @author Benjamin
 */
public final class ConvertDiffSamplesToCSV{
	
	/**
	 * This will convert .sdb particle data to .sdx.
	 * @param args {name of the sdb file to read, name of the sdx file to write}.
	 * @throws IOException If there is a problem reading the sdb file or writing the sdx file.
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
			throw new IllegalArgumentException("Usage: java converters.ConvertDiffSamplesToCSV -src=source_file -dst=output_file");
		}
		
		//read in the type definitions
		ResourceCollection classpath = new ClasspathCollection(null, "", "classpath");
		String[] typeFileName = new String[]{"typedefs", "DiffusionCoeffs.stx"};
		XMLTypeReader typeRead = new XMLTypeReader(classpath);
		List<StructureType> particleTypes = typeRead.readTypes(classpath.getResource(typeFileName));
		
		//open up the sdb for reading
		BinaryDataReader inputManage = new BinaryDataReader(particleTypes);
		StructureInputStream sdxIn = inputManage.openStructureFile(new BufferedInputStream(new FileInputStream(srcNam)));
		
		//open up the csv for writing
		Writer outputManage = new FileWriter(dstNam);
		
		//pipe the sdx to the sdb
		Structure cur = sdxIn.readNextEntry();
		while(cur!=null){
			double value = cur.floatVals[cur.getType().getFloats().getFloatVariableIndex("VALUE")][0].doubleValue();
			outputManage.write(value + "\n");
			cur = sdxIn.readNextEntry();
		}
		
		sdxIn.close();
		outputManage.close();
	}
	
	private ConvertDiffSamplesToCSV(){
		//force pure static
	}
}