package vac;
import mathlibrary.number.ArbitraryFloat;
import resources.structure.writers.StructureOutputStream;
import resources.structure.readers.StructureInputStream;
import datastructures.structure.Structure;
import resources.structure.writers.BinaryDataWriter;
import resources.structure.readers.BinaryDataReader;
import datastructures.structure.StructureType;
import resources.structure.readers.XMLTypeReader;
import datastructures.array.TwoTuple;
import datastructures.primitives.ArrayListDouble;
import datastructures.primitives.ArrayListInt;
import datastructures.primitives.ListDouble;
import datastructures.primitives.ListInt;
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
 * This will calculate the velocity autocorrelation of particles in trajectory data for varying lags, without averaging across all start times.
 * @author Benjamin
 */
public final class CalculateAutocorrelationClipRepeat{
	
	public static int dataDim = -1;
	
	/**
	 * This will calculate the square displacement of the particles in a set of trajectory data.
	 * @param args {File to get autocorrelations for, file to write to, temporary file, number of timesteps before reset}
	 * @throws IOException If there is a problem reading or writing.
	 */
	public static void main(String[] args) throws IOException{
		//parse arguments
		String srcNam = null;
		String dstNam = null;
		String wrkNam = null;
		String clrStr = null;
		for(int i = 0; i<args.length; i++){
			String curArg = args[i];
			if(curArg.startsWith("-src=")){srcNam = curArg.substring(5);}
			else if(curArg.startsWith("-dst=")){dstNam = curArg.substring(5);}
			else if(curArg.startsWith("-wrk=")){wrkNam = curArg.substring(5);}
			else if(curArg.startsWith("-clp=")){clrStr = curArg.substring(5);}
		}
		if(srcNam == null || dstNam == null || wrkNam == null || clrStr == null){
			throw new IllegalArgumentException("Usage: java vac.CalculateAutocorrelationClipRepeat -src=input_trajectory -dst=output_autocorrelation_file -wrk=working_file -clp=clip_number");
		}
		String inputFileName = srcNam;
		String outputFileName = dstNam;
		String workingFileName = wrkNam;
		long timestepRepeat = Long.parseLong(clrStr);
		
		//read in the type definitions
		ResourceCollection classpath = new ClasspathCollection(null, "", "classpath");
		String[] partFileName = new String[]{"typedefs", "TrajectoryData.stx"};
		String[] vactFileName = new String[]{"typedefs", "Autocorrelations.stx"};
		XMLTypeReader typeRead = new XMLTypeReader(classpath);
		List<StructureType> particleTypes = typeRead.readTypes(classpath.getResource(partFileName));
		List<StructureType> squareDTypes = typeRead.readTypes(classpath.getResource(vactFileName));
		Map<String, StructureType> velACTypeNames = typeRead.wrapTypes(squareDTypes);
		BinaryDataReader particleFilePrep = new BinaryDataReader(particleTypes);
		BinaryDataReader squareDispReadPrep = new BinaryDataReader(squareDTypes);
		BinaryDataWriter autocorWritePrep = new BinaryDataWriter(squareDTypes);
		
		File inputFile = new File(inputFileName);
		File outputFile = new File(outputFileName);
		File workingFile = new File(workingFileName);
		workingFile.delete();
		
		//build a zero file
		TwoTuple<Long, Long> fileInfo = buildZeroFile(inputFile, outputFile, particleFilePrep, autocorWritePrep, velACTypeNames);
		long numTimesteps = fileInfo.getValue1();
		long numParticles = fileInfo.getValue2();
		ListInt numReads = new ArrayListInt();
		for(int i = 0; i<timestepRepeat; i++){
			numReads.add(0);
		}
		
		//start reading particle file
		StructureInputStream particleData = particleFilePrep.openStructureFile(new BufferedInputStream(new FileInputStream(inputFile)));
		
		Structure autoZero = new Structure(velACTypeNames.get("AUTOCORRELATION"));
		int valInd = autoZero.getType().getFloats().getFloatVariableIndex("VALUE");
		autoZero.floatVals[valInd] = new ArbitraryFloat[1];
		ListDouble startPositions = new ArrayListDouble();
		
		//run through, handling looping
		for(long curTime = 0; curTime < numTimesteps; curTime += timestepRepeat){
			//move output to working
			outputFile.renameTo(workingFile);
			//start streaming to output file
			StructureInputStream squareDAccum = squareDispReadPrep.openStructureFile(new BufferedInputStream(new FileInputStream(workingFile)));
			StructureOutputStream squareDResult = autocorWritePrep.prepStructureFile(new BufferedOutputStream(new FileOutputStream(outputFile)));
			//run through the loop
			for(int timeInd = 0; timeInd < timestepRepeat; timeInd++){
				Structure partTime = particleData.readNextEntry();
				Structure accTime = squareDAccum.readNextEntry();
				if(partTime == null){
					Structure accCur = accTime;
					while(accCur != null){
						squareDResult.writeNextEntry(accCur);
						accCur = squareDAccum.readNextEntry();
					}
					break;
				}
				numReads.set(timeInd, numReads.get(timeInd) + 1);
				if(!"TIME".equals(partTime.getType().getName()) || !"TIME".equals(accTime.getType().getName())){
					throw new IOException("MD data must begin with a time specification.");
				}
				squareDResult.writeNextEntry(accTime);
				int curInd = 0;
				for(long part = 0; part < numParticles; part++){
					Structure partLoc = particleData.readNextEntry();
					Structure accDisp = squareDAccum.readNextEntry();
					double prevDisp = accDisp.floatVals[accDisp.getType().getFloats().getFloatVariableIndex("VALUE")][0].doubleValue();
					int locInd = partLoc.getType().getFloats().getFloatVariableIndex("VELOCITY");
					if(dataDim >= 0){
						if(partLoc.floatVals[locInd].length != dataDim){
							throw new IOException("Data dimensionality does not match specification.");
						}
					}
					double corr = 0;
					if(timeInd == 0){
						for(int i = 0; i<partLoc.floatVals[locInd].length; i++){
							double curVel = partLoc.floatVals[locInd][i].doubleValue();
							startPositions.add(curVel);
							corr += (curVel * curVel);
						}
					}
					else{
						for(int i = 0; i<partLoc.floatVals[locInd].length; i++){
							corr += (partLoc.floatVals[locInd][i].doubleValue() * startPositions.get(curInd));
							curInd++;
						}
					}
					autoZero.floatVals[valInd][0] = new ArbitraryFloat(corr + prevDisp);
					squareDResult.writeNextEntry(autoZero);
				}
			}
			squareDAccum.close();
			squareDResult.close();
			workingFile.delete();
			startPositions.clear();
		}
		particleData.close();
		//do the division
		outputFile.renameTo(workingFile);
		StructureInputStream squareDAccum = squareDispReadPrep.openStructureFile(new BufferedInputStream(new FileInputStream(workingFile)));
		StructureOutputStream squareDResult = autocorWritePrep.prepStructureFile(new BufferedOutputStream(new FileOutputStream(outputFile)));
		for(int timeInd = 0; timeInd < timestepRepeat; timeInd++){
			Structure accTime = squareDAccum.readNextEntry();
			squareDResult.writeNextEntry(accTime);
			for(long part = 0; part < numParticles; part++){
				Structure accDisp = squareDAccum.readNextEntry();
				double dispWrite = accDisp.floatVals[accDisp.getType().getFloats().getFloatVariableIndex("VALUE")][0].doubleValue() / numReads.get(timeInd);
				autoZero.floatVals[valInd][0] = new ArbitraryFloat(dispWrite);
				squareDResult.writeNextEntry(autoZero);
			}
		}
		squareDAccum.close();
		squareDResult.close();
		workingFile.delete();
	}
	
	/**
	 * This will create an initial autocorrelation accumulator of zeros.
	 * @param inputFile The particle trajectory data.
	 * @param outputFile The file that will eventually hold the autocorrelation data.
	 * @param particleFilePrep The parser for trajectory data.
	 * @param autocorWritePrep The compiler for autocorrelation data.
	 * @param velACTypeNames The type descriptions for autocorrelation.
	 * @return The number of time steps in the trajectory file, and the number of particles.
	 * @throws IOException If there is a problem reading or writing.
	 */
	private static TwoTuple<Long, Long> buildZeroFile(File inputFile, File outputFile, BinaryDataReader particleFilePrep, BinaryDataWriter autocorWritePrep, Map<String, StructureType> velACTypeNames) throws IOException{
		long numTimesteps = 0;
		long numParticles = -1;
		long curParticles = 0;
		double startTime = 0;
		boolean firstTime = true;
		Structure autoLag = new Structure(velACTypeNames.get("TIME"));
		int lagInd = autoLag.getType().getFloats().getFloatVariableIndex("LAG");
		autoLag.floatVals[lagInd] = new ArbitraryFloat[]{new ArbitraryFloat(0.0)};
		Structure autoZero = new Structure(velACTypeNames.get("AUTOCORRELATION"));
		autoZero.floatVals[autoZero.getType().getFloats().getFloatVariableIndex("VALUE")] = new ArbitraryFloat[]{new ArbitraryFloat(0.0)};
		StructureInputStream particleData = particleFilePrep.openStructureFile(new BufferedInputStream(new FileInputStream(inputFile)));
		StructureOutputStream vacZeroData = autocorWritePrep.prepStructureFile(new BufferedOutputStream(new FileOutputStream(outputFile)));
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
}