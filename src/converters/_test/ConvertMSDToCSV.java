package converters._test;
import resources.structure.readers.BinaryDataReader;
import datastructures.structure.Structure;
import java.io.FileWriter;
import java.io.Writer;
import java.io.IOException;
import datastructures.structure.StructureType;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.List;
import resources.locating.ClasspathCollection;
import resources.locating.ResourceCollection;
import resources.structure.readers.StructureInputStream;
import resources.structure.readers.XMLTypeReader;

/**
 * This will convert a mean square displacement file to csv.
 * @author Benjamin
 */
public final class ConvertMSDToCSV{
	
	/**
	 * This will convert a trajectory to csv.
	 * @param args {source sdb msd file, destination csv file}.
	 * @throws IOException If there is a problem reading or writing.
	 */
	public static void main(String[] args) throws IOException{
		//parse arguments
		String srcFile = null;
		String dstFile = null;
		for(int i = 0; i<args.length; i++){
			String curArg = args[i];
			if(curArg.startsWith("-src=")){srcFile = curArg.substring(5);}
			else if(curArg.startsWith("-dst=")){dstFile = curArg.substring(5);}
		}
		if(srcFile == null || dstFile == null){
			throw new IllegalArgumentException("Usage: java converters._test_ConvertMSDToCSV -src=source_file -dst=destination_file");
		}
		//read in the type definitions
		ResourceCollection classpath = new ClasspathCollection(null, "", "classpath");
		String[] typeFileName = new String[]{"typedefs", "MeanSquareDisplacement.stx"};
		XMLTypeReader typeRead = new XMLTypeReader(classpath);
		List<StructureType> particleTypes = typeRead.readTypes(classpath.getResource(typeFileName));
		
		//open up the sdb for reading
		BinaryDataReader inputManage = new BinaryDataReader(particleTypes);
		StructureInputStream sdxIn = inputManage.openStructureFile(new BufferedInputStream(new FileInputStream(srcFile)));
		
		//open up the csv for writing
		Writer csvOut = new FileWriter(dstFile);
		
		//pipe the sdx to the sdb
		Structure cur = sdxIn.readNextEntry();
		while(cur!=null){
			if("TIME".equals(cur.getType().getName())){
				csvOut.write("\n" + cur.floatVals[cur.getType().getFloats().getFloatVariableIndex("LAG")][0].doubleValue());
			}
			else if("DISPLACEMENT".equals(cur.getType().getName())){
				int posInd = cur.getType().getFloats().getFloatVariableIndex("VALUE");
				int velInd = cur.getType().getFloats().getFloatVariableIndex("DEVIATION");
				csvOut.write(",," + cur.floatVals[posInd][0].doubleValue() + "," + cur.floatVals[velInd][0].doubleValue());
			}
			csvOut.flush();
			cur = sdxIn.readNextEntry();
		}
		
		sdxIn.close();
		csvOut.close();
	}
}