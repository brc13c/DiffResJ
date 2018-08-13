package msd;
import resources.structure.writers.XMLDataWriter;

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
import datastructures.array.TwoTuple;
import java.io.File;
import java.util.List;
import java.util.Map;
import resources.locating.ClasspathCollection;
import resources.locating.ResourceCollection;
import java.io.IOException;

/**
 * This will calculate the square displacement of particles in trajectory data for varying lags.
 * @author Benjamin
 */
public final class CalculateSquareDisplacement{
	
	public static int dataDim = -1;
	
	/**
	 * This will calculate the square displacement of the particles in a set of trajectory data.
	 * @param args {File to get displacement for, file to write to, working file (will be deleted afterwards), [-xml write as xml rather than binary]}
	 * @throws IOException If there is a problem reading or writing.
	 */
	public static void main(String[] args) throws IOException{
		//parse arguments
		String srcNam = null;
		String dstNam = null;
		String wrkNam = null;
		boolean toXML = false;
		for(int i = 0; i<args.length; i++){
			String curArg = args[i];
			if(curArg.startsWith("-src=")){srcNam = curArg.substring(5);}
			else if(curArg.startsWith("-dst=")){dstNam = curArg.substring(5);}
			else if(curArg.startsWith("-wrk=")){wrkNam = curArg.substring(5);}
			else if(curArg.equals("-xml")){toXML = true;}
		}
		if(dstNam == null || srcNam==null || wrkNam == null){
			throw new IllegalArgumentException("Usage: java msd.CalculateSquareDisplacement -src=input_file -dst=output_file -wrk=working_file");
		}
		String inputFileName = srcNam;
		String outputFileName = dstNam;
		String workingFileName = wrkNam;
		
		//read in the type definitions
		ResourceCollection classpath = new ClasspathCollection(null, "", "classpath");
		String[] partFileName = new String[]{"typedefs", "TrajectoryData.stx"};
		String[] vactFileName = new String[]{"typedefs", "SquareDisplacement.stx"};
		XMLTypeReader typeRead = new XMLTypeReader(classpath);
		List<StructureType> particleTypes = typeRead.readTypes(classpath.getResource(partFileName));
		List<StructureType> squareDTypes = typeRead.readTypes(classpath.getResource(vactFileName));
		Map<String, StructureType> velACTypeNames = typeRead.wrapTypes(squareDTypes);
		BinaryDataReader particleFilePrep = new BinaryDataReader(particleTypes);
		BinaryDataReader autocorFilePrep = new BinaryDataReader(squareDTypes);
		BinaryDataWriter autocorWritePrep = new BinaryDataWriter(squareDTypes);
		
		File inputFile = new File(inputFileName);
		File outputFile = new File(outputFileName);
		File workingFile = new File(workingFileName);
		workingFile.delete();
		
		//create a zero file (will iteratively add until all data accounted for)
		TwoTuple<Long, Long> simDesc = buildZeroFile(inputFile, outputFile, particleFilePrep, autocorWritePrep, velACTypeNames);
		long numTimesteps = simDesc.getValue1();
		long numParticles = simDesc.getValue2();
		
		sumSquareDisplacement(inputFile, outputFile, workingFile, numTimesteps, numParticles, particleFilePrep, autocorFilePrep, autocorWritePrep, velACTypeNames);
		divideSums(outputFile, workingFile, numTimesteps, numParticles, autocorFilePrep, autocorWritePrep, velACTypeNames);
		
		//if the user wants output in xml, give it to them
		if(toXML){
			convertToXML(outputFile, workingFile, autocorFilePrep);
		}
	}
	
	/**
	 * This will create an initial square displacement accumulator of zeros.
	 * @param inputFile The particle trajectory data.
	 * @param outputFile The file that will eventually hold the square displacement data.
	 * @param particleFilePrep The parser for trajectory data.
	 * @param squareDWritePrep The compiler for square displacement data.
	 * @param squareDTypeNames The type descriptions for square displacement.
	 * @return The number of time steps in the trajectory file, and the number of particles.
	 * @throws IOException If there is a problem reading or writing.
	 */
	private static TwoTuple<Long, Long> buildZeroFile(File inputFile, File outputFile, BinaryDataReader particleFilePrep, BinaryDataWriter squareDWritePrep, Map<String, StructureType> squareDTypeNames) throws IOException{
		long numTimesteps = 0;
		long numParticles = -1;
		long curParticles = 0;
		double startTime = 0;
		boolean firstTime = true;
		Structure autoLag = new Structure(squareDTypeNames.get("TIME"));
		int lagInd = autoLag.getType().getFloats().getFloatVariableIndex("LAG");
		autoLag.floatVals[lagInd] = new ArbitraryFloat[1];
		Structure autoZero = new Structure(squareDTypeNames.get("DISPLACEMENT"));
		autoZero.floatVals[autoZero.getType().getFloats().getFloatVariableIndex("VALUE")] = new ArbitraryFloat[]{new ArbitraryFloat(0.0)};
		StructureInputStream particleData = particleFilePrep.openStructureFile(new BufferedInputStream(new FileInputStream(inputFile)));
		StructureOutputStream vacZeroData = squareDWritePrep.prepStructureFile(new BufferedOutputStream(new FileOutputStream(outputFile)));
		Structure curStruct = particleData.readNextEntry();
		if(!"TIME".equals(curStruct.getType().getName())){
			throw new IOException("MD data must begin with a time specification.");
		}
		while(curStruct!=null){
			if("TIME".equals(curStruct.getType().getName())){
				double csTime = curStruct.floatVals[curStruct.getType().getFloats().getFloatVariableIndex("NEWTIME")][0].doubleValue();
				if(firstTime){
					firstTime = false;
					startTime = csTime;
				}
				else{
					if(numParticles < 0){
						numParticles = curParticles;
					}
					else if(curParticles!=numParticles){
						throw new IOException("Number of particles not constant.");
					}
				}
				curParticles = 0;
				double curLag = csTime - startTime;
				autoLag.floatVals[lagInd][0] = new ArbitraryFloat(curLag);
				vacZeroData.writeNextEntry(autoLag);
				numTimesteps++;
			}
			else if("PARTICLE".equals(curStruct.getType().getName())){
				//put another zero on the file
				vacZeroData.writeNextEntry(autoZero);
				curParticles++;
			}
			else{
				throw new IOException("Unknown structure type " + curStruct.getType().getName());
			}
			curStruct = particleData.readNextEntry();
		}
		particleData.close();
		vacZeroData.close();
		
		return new TwoTuple<Long, Long>(numTimesteps, numParticles);
	}
	
	/**
	 * This will sum the particle square displacements across the same lag.
	 * @param inputFile The position data.
	 * @param outputFile The file to write the sums to.
	 * @param workingFile A safe working file name.
	 * @param numTimesteps The number of timesteps in the simulation.
	 * @param numParticles The number of particles in the simulation.
	 * @param particleFilePrep The parser for trajectory data.
	 * @param squareDFilePrep The parser for square displacement data.
	 * @param squareDWritePrep The compiler for square displacement data.
	 * @param squareDTypeNames The types in square displacement files.
	 * @throws IOException If there is a problem reading or writing.
	 */
	private static void sumSquareDisplacement(File inputFile, File outputFile, File workingFile, long numTimesteps, long numParticles, BinaryDataReader particleFilePrep, BinaryDataReader squareDFilePrep, BinaryDataWriter squareDWritePrep, Map<String, StructureType> squareDTypeNames) throws IOException{
		ListDouble startPositions = new ArrayListDouble();
		StructureInputStream startReader = particleFilePrep.openStructureFile(new BufferedInputStream(new FileInputStream(inputFile)));
		for(long startStep = 0; startStep < numTimesteps; startStep++){
			//rename the output file
			outputFile.renameTo(workingFile);
			//skip the time step
			startReader.readNextEntry();
			for(long startPart = 0; startPart < numParticles; startPart++){
				Structure startPartDat = startReader.readNextEntry();
				ArbitraryFloat[] spdPos = startPartDat.floatVals[startPartDat.getType().getFloats().getFloatVariableIndex("POSITION")];
				for(int dim = 0; dim < spdPos.length; dim++){
					startPositions.add(spdPos[dim].doubleValue());
				}
			}
			//now, open the input again
			StructureInputStream curReader = particleFilePrep.openStructureFile(new BufferedInputStream(new FileInputStream(inputFile)));
			//skip to the current time step
			for(long skipStep = 0; skipStep < startStep; skipStep++){
				curReader.readNextEntry();
				for(long skipPart = 0; skipPart < numParticles; skipPart++){
					curReader.readNextEntry();
				}
			}
			//open the previous accumulation of square displacements
			StructureInputStream prevAuto = squareDFilePrep.openStructureFile(new BufferedInputStream(new FileInputStream(workingFile)));
			//open the next accumulation of autocorrelations
			StructureOutputStream nextAuto = squareDWritePrep.prepStructureFile(new BufferedOutputStream(new FileOutputStream(outputFile)));
			
			//square displacements
			Structure nextSquareDispStruct = new Structure(squareDTypeNames.get("DISPLACEMENT"));
			int nsdValInd = nextSquareDispStruct.getType().getFloats().getFloatVariableIndex("VALUE");
			nextSquareDispStruct.floatVals[nsdValInd] = new ArbitraryFloat[1];
			for(long lagStep = startStep; lagStep<numTimesteps; lagStep++){
				//skip time step info
				curReader.readNextEntry();
				//just copy the lag
				nextAuto.writeNextEntry(prevAuto.readNextEntry());
				int curInd = 0;
				for(long curPart = 0; curPart < numParticles; curPart++){
					Structure prevAutoVal = prevAuto.readNextEntry();
					double prevSquareD = prevAutoVal.floatVals[prevAutoVal.getType().getFloats().getFloatVariableIndex("VALUE")][0].doubleValue();
					Structure curPartDat = curReader.readNextEntry();
					double squareDel = 0;
					ArbitraryFloat[] curPartPos = curPartDat.floatVals[curPartDat.getType().getFloats().getFloatVariableIndex("POSITION")];
					if(dataDim >= 0){
						if(curPartPos.length != dataDim){
							throw new IOException("Data dimensionality does not match specification.");
						}
					}
					for(int dim = 0; dim<curPartPos.length; dim++){
						double del = curPartPos[dim].doubleValue() - startPositions.get(curInd);
						squareDel += (del * del);
						curInd++;
					}
					nextSquareDispStruct.floatVals[nsdValInd][0] = new ArbitraryFloat(prevSquareD + squareDel);
					nextAuto.writeNextEntry(nextSquareDispStruct);
				}
			}
			//copy through the final entries
			for(long passStep = 0; passStep < startStep; passStep++){
				nextAuto.writeNextEntry(prevAuto.readNextEntry());
				for(long curPart = 0; curPart < numParticles; curPart++){
					nextAuto.writeNextEntry(prevAuto.readNextEntry());
				}
			}
			
			//cleanup from previous pass
			curReader.close();
			prevAuto.close();
			nextAuto.close();
			workingFile.delete();
			startPositions.clear();
		}
		startReader.close();
	}
	
	/**
	 * This will divide the summed square displacements to get a lag average.
	 * @param outputFile The file to write the averages to.
	 * @param workingFile A safe working file.
	 * @param numTimesteps The number of timesteps in the simulation.
	 * @param numParticles The number of particles in the simulation.
	 * @param squareDFilePrep The parser for square displacement data.
	 * @param squareDWritePrep The compiler for square displacement data.
	 * @param squareDTypeNames The types in square displacement files.
	 * @throws IOException If there is a problem reading or writing.
	 */
	private static void divideSums(File outputFile, File workingFile, long numTimesteps, long numParticles, BinaryDataReader squareDFilePrep, BinaryDataWriter squareDWritePrep, Map<String, StructureType> squareDTypeNames) throws IOException{
		//divide for the average
		outputFile.renameTo(workingFile);
		StructureInputStream summedAutoCorrs = squareDFilePrep.openStructureFile(new BufferedInputStream(new FileInputStream(workingFile)));
		StructureOutputStream averageAutoCorrs = squareDWritePrep.prepStructureFile(new BufferedOutputStream(new FileOutputStream(outputFile)));
		Structure divedAutocorrStruct = new Structure(squareDTypeNames.get("DISPLACEMENT"));
		int dasValInd = divedAutocorrStruct.getType().getFloats().getFloatVariableIndex("VALUE");
		divedAutocorrStruct.floatVals[dasValInd] = new ArbitraryFloat[1];
		for(long curTime = 0; curTime < numTimesteps; curTime++){
			double factor = 1.0 / (numTimesteps - curTime);
			averageAutoCorrs.writeNextEntry(summedAutoCorrs.readNextEntry());
			for(long curPart = 0; curPart < numParticles; curPart++){
				Structure summedAutocorrStruct = summedAutoCorrs.readNextEntry();
				double sasVal = summedAutocorrStruct.floatVals[summedAutocorrStruct.getType().getFloats().getFloatVariableIndex("VALUE")][0].doubleValue();
				divedAutocorrStruct.floatVals[dasValInd][0] = new ArbitraryFloat(factor * sasVal);
				averageAutoCorrs.writeNextEntry(divedAutocorrStruct);
			}
		}
		summedAutoCorrs.close();
		averageAutoCorrs.close();
		workingFile.delete();
	}
	
	/**
	 * This will convert a square displacement file to xml.
	 * @param outputFile The file that will eventually contain xml.
	 * @param workingFile A trash file.
	 * @param squareDFilePrep The binary parser.
	 * @throws IOException If there is a problem reading or writing.
	 */
	private static void convertToXML(File outputFile, File workingFile, BinaryDataReader squareDFilePrep) throws IOException{
		XMLDataWriter xmlOutPrep = new XMLDataWriter("SD");
		//rename the output file
		outputFile.renameTo(workingFile);
		//open working as binary
		StructureInputStream binRead = squareDFilePrep.openStructureFile(new BufferedInputStream(new FileInputStream(workingFile)));
		//open output as xml
		StructureOutputStream xmlWrite = xmlOutPrep.prepStructureFile(new BufferedOutputStream(new FileOutputStream(outputFile)));
		//pipe
		Structure curStruct = binRead.readNextEntry();
		while(curStruct!=null){
			xmlWrite.writeNextEntry(curStruct);
			curStruct = binRead.readNextEntry();
		}
		binRead.close();
		xmlWrite.close();
		
		workingFile.delete();
	}
}