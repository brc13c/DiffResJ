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
 * This will convert a velocity autocorrelation file to csv.
 * @author Benjamin
 */
public final class ConvertVACToCSV{
	
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
			throw new IllegalArgumentException("Usage: java converters._test_ConvertVACToCSV -src=source_file -dst=destination_file");
		}
		//read in the type definitions
		ResourceCollection classpath = new ClasspathCollection(null, "", "classpath");
		String[] typeFileName = new String[]{"typedefs", "Autocorrelations.stx"};
		XMLTypeReader typeRead = new XMLTypeReader(classpath);
		List<StructureType> particleTypes = typeRead.readTypes(classpath.getResource(typeFileName));
		
		//open up the sdb for reading
		BinaryDataReader inputManage = new BinaryDataReader(particleTypes);
		StructureInputStream sdxIn = inputManage.openStructureFile(new BufferedInputStream(new FileInputStream(srcFile)));
		
		//open up the csv for writing
		Writer csvOut = new FileWriter(dstFile);
		
		//pipe the sdx to the sdb
		double sum = 0;
		long numParts = 0;
		Structure cur = sdxIn.readNextEntry();
		while(cur!=null){
			if("TIME".equals(cur.getType().getName())){
				if(numParts > 0){
					csvOut.write("," + (sum / numParts));
				}
				sum = 0;
				numParts = 0;
				csvOut.write("\n" + cur.floatVals[cur.getType().getFloats().getFloatVariableIndex("LAG")][0].doubleValue());
			}
			else if("AUTOCORRELATION".equals(cur.getType().getName())){
				int posInd = cur.getType().getFloats().getFloatVariableIndex("VALUE");
				sum += cur.floatVals[posInd][0].doubleValue();
				numParts++;
			}
			cur = sdxIn.readNextEntry();
		}
		
		sdxIn.close();
		csvOut.close();
	}
}