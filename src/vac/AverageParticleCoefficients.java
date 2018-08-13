package vac;
import mathlibrary.number.ArbitraryFloat;
import resources.structure.readers.StructureInputStream;
import resources.structure.writers.StructureOutputStream;
import datastructures.structure.Structure;
import resources.structure.writers.BinaryDataWriter;
import resources.structure.readers.BinaryDataReader;
import datastructures.structure.StructureType;
import resources.structure.readers.XMLTypeReader;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.util.Map;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import resources.locating.ClasspathCollection;
import resources.locating.ResourceCollection;

/**
 * This will average the particle diffusion coefficients from a file and put the average in a new file.
 * This can also take multiple files (which will create multiple entries in the output file).
 * @author Benjamin
 *
 */
public final class AverageParticleCoefficients{
	
	/**
	 * This will average the particle diffusion coefficients from a file(s) and put the average(s) in a new file.
	 * @param args {outputFileName, inputFile1, [inputFile2, ...]}
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
			throw new IllegalArgumentException("Usage: java vac.AverageParticleCoefficients -dst=collected_file -src=file1 [-src=filei]*");
		}
		
		//read in types
		ResourceCollection classpath = new ClasspathCollection(null, "", "classpath");
		XMLTypeReader typeRead = new XMLTypeReader(classpath);
		
		String[] typeFileName = new String[]{"typedefs", "IntegratedAutocorrelations.stx"};
		List<StructureType> intVACTypes = typeRead.readTypes(classpath.getResource(typeFileName));
		BinaryDataReader inputManage = new BinaryDataReader(intVACTypes);
		
		String[] diffFileName = new String[]{"typedefs", "DiffusionCoeffs.stx"};
		List<StructureType> difCofTypes = typeRead.readTypes(classpath.getResource(diffFileName));
		Map<String, StructureType> difCoefNames = typeRead.wrapTypes(difCofTypes);
		BinaryDataWriter outputManage = new BinaryDataWriter(difCofTypes);
		
		//create a reusable structure
		Structure diffCoeff = new Structure(difCoefNames.get("DIFFCOEFF"));
		int valInd = diffCoeff.getType().getFloats().getFloatVariableIndex("VALUE");
		diffCoeff.floatVals[valInd] = new ArbitraryFloat[1];
		
		//open the output stream
		StructureOutputStream toWrite = outputManage.prepStructureFile(new BufferedOutputStream(new FileOutputStream(dstNam)));
		for(int i = 0; i<allInputs.size(); i++){
			double runningSum = 0;
			long numSamps = 0;
			StructureInputStream toRead = inputManage.openStructureFile(new BufferedInputStream(new FileInputStream(allInputs.get(i))));
			Structure curRead = toRead.readNextEntry();
			while(curRead != null){
				double curVal = curRead.floatVals[curRead.getType().getFloats().getFloatVariableIndex("VALUE")][0].doubleValue();
				runningSum += curVal;
				numSamps++;
				curRead = toRead.readNextEntry();
			}
			toRead.close();
			diffCoeff.floatVals[valInd][0] = new ArbitraryFloat(runningSum / numSamps);
			toWrite.writeNextEntry(diffCoeff);
		}
		toWrite.close();
	}
	
	private AverageParticleCoefficients(){
		//force pure static
	}
}