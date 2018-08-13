package msd;
import mathlibrary.number.ArbitraryFloat;
import resources.structure.writers.StructureOutputStream;
import resources.structure.readers.StructureInputStream;
import datastructures.structure.Structure;
import resources.structure.writers.BinaryDataWriter;
import resources.structure.readers.BinaryDataReader;
import datastructures.structure.StructureType;
import resources.structure.readers.XMLTypeReader;
import datastructures.primitives.ArrayListDouble;
import datastructures.primitives.ListDouble;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.List;
import java.util.Map;
import resources.locating.ClasspathCollection;
import resources.locating.ResourceCollection;
import java.io.IOException;

/**
 * This will calculate the square displacement of particles in trajectory data for varying lags, without averaging across all start times.
 * @author Benjamin
 */
public final class CalculateSquareDisplacementOnePass{
	
	public static int dataDim = -1;
	
	/**
	 * This will calculate the square displacement of the particles in a set of trajectory data.
	 * @param args {File to get displacement for, file to write to}
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
			throw new IllegalArgumentException("Usage: java msd.CalculateSquareDisplacementOnePass -src=input_file -dst=output_file");
		}
		String inputFileName = srcNam;
		String outputFileName = dstNam;
		
		//read in the type definitions
		ResourceCollection classpath = new ClasspathCollection(null, "", "classpath");
		String[] partFileName = new String[]{"typedefs", "TrajectoryData.stx"};
		String[] vactFileName = new String[]{"typedefs", "SquareDisplacement.stx"};
		XMLTypeReader typeRead = new XMLTypeReader(classpath);
		List<StructureType> particleTypes = typeRead.readTypes(classpath.getResource(partFileName));
		List<StructureType> squareDTypes = typeRead.readTypes(classpath.getResource(vactFileName));
		Map<String, StructureType> velACTypeNames = typeRead.wrapTypes(squareDTypes);
		BinaryDataReader particleFilePrep = new BinaryDataReader(particleTypes);
		BinaryDataWriter autocorWritePrep = new BinaryDataWriter(squareDTypes);
		
		File inputFile = new File(inputFileName);
		File outputFile = new File(outputFileName);
		
		//open the files
		StructureInputStream particleData = particleFilePrep.openStructureFile(new BufferedInputStream(new FileInputStream(inputFile)));
		StructureOutputStream vacData = autocorWritePrep.prepStructureFile(new BufferedOutputStream(new FileOutputStream(outputFile)));
		
		//handle the first timestep
		double startTime = 0;
		boolean firstTime = true;
		Structure autoLag = new Structure(velACTypeNames.get("TIME"));
		int lagInd = autoLag.getType().getFloats().getFloatVariableIndex("LAG");
		autoLag.floatVals[lagInd] = new ArbitraryFloat[1];
		Structure autoZero = new Structure(velACTypeNames.get("DISPLACEMENT"));
		int valInd = autoZero.getType().getFloats().getFloatVariableIndex("VALUE");
		autoZero.floatVals[valInd] = new ArbitraryFloat[1];
		ListDouble startPositions = new ArrayListDouble();
		
		Structure curStruct = particleData.readNextEntry();
		if(!"TIME".equals(curStruct.getType().getName())){
			throw new IOException("MD data must begin with a time specification.");
		}
		else{
			startTime = curStruct.floatVals[curStruct.getType().getFloats().getFloatVariableIndex("NEWTIME")][0].doubleValue();
			autoLag.floatVals[lagInd][0] = new ArbitraryFloat(0.0);
			vacData.writeNextEntry(autoLag);
		}
		
		do{
			curStruct = particleData.readNextEntry();
			if("TIME".equals(curStruct.getType().getName())){
				firstTime = false;
			}
			else{
				//add positions to list
				int posInd = curStruct.getType().getFloats().getFloatVariableIndex("POSITION");
				if(dataDim >= 0){
					if(curStruct.floatVals[posInd].length != dataDim){
						throw new IOException("Data dimensionality does not match specification.");
					}
				}
				for(int i = 0; i<curStruct.floatVals[posInd].length; i++){
					double curVel = curStruct.floatVals[posInd][i].doubleValue();
					startPositions.add(curVel);
				}
				autoZero.floatVals[valInd][0] = new ArbitraryFloat(0.0);
				vacData.writeNextEntry(autoZero);
			}
		} while(firstTime);
		
		//read the remaining timesteps
		int curInd = 0;
		while(curStruct != null){
			if("TIME".equals(curStruct.getType().getName())){
				autoLag.floatVals[lagInd][0] = new ArbitraryFloat(curStruct.floatVals[curStruct.getType().getFloats().getFloatVariableIndex("NEWTIME")][0].doubleValue() - startTime);
				vacData.writeNextEntry(autoLag);
				curInd = 0;
			}
			else{
				int velInd = curStruct.getType().getFloats().getFloatVariableIndex("POSITION");
				double curSD = 0;
				for(int i = 0; i<curStruct.floatVals[velInd].length; i++){
					double curVel = curStruct.floatVals[velInd][i].doubleValue();
					double del = startPositions.get(curInd) - curVel;
					curSD += (del * del);
					curInd++;
				}
				autoZero.floatVals[valInd][0] = new ArbitraryFloat(curSD);
				vacData.writeNextEntry(autoZero);
			}
			curStruct = particleData.readNextEntry();
		}
		
		//close
		particleData.close();
		vacData.close();
	}
}