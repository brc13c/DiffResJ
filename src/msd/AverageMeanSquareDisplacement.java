package msd;
import mathlibrary.number.ArbitraryFloat;

import datastructures.structure.Structure;
import resources.structure.writers.StructureOutputStream;
import resources.structure.readers.StructureInputStream;
import resources.structure.writers.BinaryDataWriter;
import resources.structure.readers.BinaryDataReader;
import datastructures.structure.StructureType;
import resources.structure.readers.XMLTypeReader;
import java.util.Map;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.List;
import resources.locating.ClasspathCollection;
import resources.locating.ResourceCollection;

/**
 * This will average the square displacement across all particles.
 * @author Benjamin
 */
public final class AverageMeanSquareDisplacement{
	
	/**
	 * This will average the square displacement across all particles.
	 * @param args {input file, output file}
	 * @throws IOException If there is a problem reading or writing.
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
			throw new IllegalArgumentException("Usage: java msd.AverageMeanSquareDisplacement -src=square_displacement_file -dst=mean_square_displacement_file");
		}
		//read in the type definitions
		ResourceCollection classpath = new ClasspathCollection(null, "", "classpath");
		String[] typeFileName = new String[]{"typedefs", "SquareDisplacement.stx"};
		String[] msdTypeFileName = new String[]{"typedefs", "MeanSquareDisplacement.stx"};
		XMLTypeReader typeRead = new XMLTypeReader(classpath);
		List<StructureType> squareDTypes = typeRead.readTypes(classpath.getResource(typeFileName));
		BinaryDataReader inputManage = new BinaryDataReader(squareDTypes);
		List<StructureType> msdTypes = typeRead.readTypes(classpath.getResource(msdTypeFileName));
		Map<String, StructureType> msdTypeNames = typeRead.wrapTypes(msdTypes);
		BinaryDataWriter outputManage = new BinaryDataWriter(msdTypes);
		
		//open the input and output files
		StructureInputStream inputFile = inputManage.openStructureFile(new BufferedInputStream(new FileInputStream(srcNam)));
		StructureOutputStream outputFile = outputManage.prepStructureFile(new BufferedOutputStream(new FileOutputStream(dstNam)));
		Structure lagStruct = new Structure(msdTypeNames.get("TIME"));
		int lagInd = lagStruct.getType().getFloats().getFloatVariableIndex("LAG");
		lagStruct.floatVals[lagInd] = new ArbitraryFloat[1];
		Structure writeStruct = new Structure(msdTypeNames.get("DISPLACEMENT"));
		
		Structure curStruct = inputFile.readNextEntry();
		boolean lagHasData = false;
		long num = 0;
		double mean = 0;
		double mean2 = 0;
		while(curStruct != null){
			if("TIME".equals(curStruct.getType().getName())){
				if(lagHasData){
					writeData(num, mean, mean2, writeStruct, outputFile);
					lagHasData = false;
					num = 0;
					mean = 0;
					mean2 = 0;
				}
				lagStruct.floatVals[lagInd][0] = new ArbitraryFloat(curStruct.floatVals[curStruct.getType().getFloats().getFloatVariableIndex("LAG")][0].doubleValue());
				outputFile.writeNextEntry(lagStruct);
			}
			else if("DISPLACEMENT".equals(curStruct.getType().getName())){
				double curVal = curStruct.floatVals[curStruct.getType().getFloats().getFloatVariableIndex("VALUE")][0].doubleValue();
				num++;
				double delta = curVal - mean;
				mean = mean + (delta / num);
				mean2 = mean2 + delta*(curVal - mean);
				lagHasData = true;
			}
			curStruct = inputFile.readNextEntry();
		}
		if(lagHasData){
			writeData(num, mean, mean2, writeStruct, outputFile);
		}
		
		inputFile.close();
		outputFile.close();
	}
	
	/**
	 * This will write the averaged data for a lag step.
	 * @param num The number of particles in the step.
	 * @param mean The mean displacement.
	 * @param mean2 ... see http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance#Online_algorithm
	 * @param writeStruct The structure to use to write.
	 * @param outputFile The file to write to.
	 * @throws IOException If there is a problem writing.
	 */
	private static void writeData(long num, double mean, double mean2, Structure writeStruct, StructureOutputStream outputFile) throws IOException{
		writeStruct.floatVals[writeStruct.getType().getFloats().getFloatVariableIndex("VALUE")] = new ArbitraryFloat[]{new ArbitraryFloat(mean)};
		double variance = mean2 / (num-1);
		writeStruct.floatVals[writeStruct.getType().getFloats().getFloatVariableIndex("DEVIATION")] = new ArbitraryFloat[]{new ArbitraryFloat(Math.sqrt(variance))};
		outputFile.writeNextEntry(writeStruct);
	}
	
	private AverageMeanSquareDisplacement(){
		//force pure static
	}
}