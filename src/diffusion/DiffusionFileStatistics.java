package diffusion;
import resources.structure.writers.StructureOutputStream;

import mathlibrary.number.ArbitraryFloat;
import datastructures.structure.Structure;
import resources.structure.readers.StructureInputStream;
import resources.structure.writers.BinaryDataWriter;
import resources.structure.writers.XMLDataWriter;
import resources.structure.writers.DataWriter;
import resources.structure.readers.BinaryDataReader;
import datastructures.structure.StructureType;
import resources.structure.readers.XMLTypeReader;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.util.Map;
import java.io.IOException;
import java.util.List;
import resources.locating.ClasspathCollection;
import resources.locating.ResourceCollection;

/**
 * This will calculate mean and standard deviation from a diffusion file.
 * @author Benjamin
 */
public final class DiffusionFileStatistics{
	
	/**
	 * This will calculate mean and standard deviation from a diffusion file.
	 * @param args {inputDiffusionFile, outputStatisticsFile, [-xml]}
	 * @throws IOException If there is a problem reading or writing.
	 */
	public static void main(String[] args) throws IOException{
		//parse arguments
		String srcNam = null;
		String dstNam = null;
		boolean toXML = false;
		for(int i = 0; i<args.length; i++){
			String curArg = args[i];
			if(curArg.startsWith("-src=")){srcNam = curArg.substring(5);}
			else if(curArg.startsWith("-dst=")){dstNam = curArg.substring(5);}
			else if(curArg.equals("-xml")){toXML = true;}
		}
		if(dstNam == null || srcNam==null){
			throw new IllegalArgumentException("Usage: java diffusion.DiffusionFileStatistics -src=coefficient_list -dst=summary_out [-xml]");
		}
		
		//read in the types
		ResourceCollection classpath = new ClasspathCollection(null, "", "classpath");
		XMLTypeReader typeRead = new XMLTypeReader(classpath);
		
		String[] diffFileName = new String[]{"typedefs", "DiffusionCoeffs.stx"};
		List<StructureType> difCofTypes = typeRead.readTypes(classpath.getResource(diffFileName));
		BinaryDataReader inputManage = new BinaryDataReader(difCofTypes);
		
		String[] statFileName = new String[]{"typedefs", "DiffusionError.stx"};
		List<StructureType> difStatTypes = typeRead.readTypes(classpath.getResource(statFileName));
		Map<String, StructureType> difStatNames = typeRead.wrapTypes(difStatTypes);
		DataWriter outputManage;
		if(toXML){
			outputManage = new XMLDataWriter("DIFFUSION_COEFFICIENT");
		}
		else{
			outputManage = new BinaryDataWriter(difStatTypes);
		}
		
		//read in the file, collecting stats (http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance)
		long num = 0;
		double mean = 0;
		double stdev = 0;
		StructureInputStream toRead = inputManage.openStructureFile(new BufferedInputStream(new FileInputStream(srcNam)));
		Structure curRead = toRead.readNextEntry();
		while(curRead != null){
			double diffCoeff = curRead.floatVals[curRead.getType().getFloats().getFloatVariableIndex("VALUE")][0].doubleValue();
			num++;
			double delta = diffCoeff - mean;
			mean += (delta / num);
			stdev += (delta * (diffCoeff - mean));
			curRead = toRead.readNextEntry();
		}
		stdev = stdev/(num - 1);
		stdev = Math.sqrt(stdev);
		toRead.close();
		
		//write the results
		Structure curWrite = new Structure(difStatNames.get("DIFFCOEFF"));
		curWrite.floatVals[curWrite.getType().getFloats().getFloatVariableIndex("VALUE")] = new ArbitraryFloat[]{new ArbitraryFloat(mean)};
		curWrite.floatVals[curWrite.getType().getFloats().getFloatVariableIndex("ERROR")] = new ArbitraryFloat[]{new ArbitraryFloat(stdev)};
		
		StructureOutputStream toWrite = outputManage.prepStructureFile(new BufferedOutputStream(new FileOutputStream(dstNam)));
		toWrite.writeNextEntry(curWrite);
		toWrite.close();
	}
	
	private DiffusionFileStatistics(){
		//force pure static
	}
}