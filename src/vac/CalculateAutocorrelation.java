package vac;
import resources.structure.writers.XMLDataWriter;
import mathlibrary.number.ArbitraryFloat;
import resources.structure.writers.StructureOutputStream;
import resources.structure.readers.StructureInputStream;
import datastructures.structure.Structure;
import resources.structure.writers.BinaryDataWriter;
import resources.structure.readers.BinaryDataReader;
import datastructures.structure.StructureType;
import resources.structure.readers.XMLTypeReader;
import resources.locating.ClasspathCollection;
import resources.locating.ResourceCollection;
import datastructures.primitives.ListDouble;
import datastructures.primitives.ArrayListDouble;
import datastructures.array.TwoTuple;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.Map;
import java.io.IOException;
import java.util.List;
import java.io.File;

/**
 * This will calculate the autocorrelation of the velocities in a set of trajectory data.
 * @author Benjamin
 */
public final class CalculateAutocorrelation{
	
	public static int dataDim = -1;
	
	/**
	 * This will calculate the autocorrelation of the velocities in a set of trajectory data.
	 * @param args {File to get autocorrelations for, file to write to, working file (will be deleted afterwards), [-xml write as xml rather than binary]}
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
		if(srcNam == null || dstNam == null || wrkNam == null){
			throw new IllegalArgumentException("Usage: java vac.CalculateAutocorrelation -src=input_trajectory -dst=output_autocorrelation_file -wrk=working_file");
		}
		
		String inputFileName = srcNam;
		String outputFileName = dstNam;
		String workingFileName = wrkNam;
		
		//read in the type definitions
		ResourceCollection classpath = new ClasspathCollection(null, "", "classpath");
		String[] partFileName = new String[]{"typedefs", "TrajectoryData.stx"};
		String[] vactFileName = new String[]{"typedefs", "Autocorrelations.stx"};
		XMLTypeReader typeRead = new XMLTypeReader(classpath);
		List<StructureType> particleTypes = typeRead.readTypes(classpath.getResource(partFileName));
		List<StructureType> velACTypes = typeRead.readTypes(classpath.getResource(vactFileName));
		Map<String, StructureType> velACTypeNames = typeRead.wrapTypes(velACTypes);
		BinaryDataReader particleFilePrep = new BinaryDataReader(particleTypes);
		BinaryDataReader autocorFilePrep = new BinaryDataReader(velACTypes);
		BinaryDataWriter autocorWritePrep = new BinaryDataWriter(velACTypes);
		
		File inputFile = new File(inputFileName);
		File outputFile = new File(outputFileName);
		File workingFile = new File(workingFileName);
		workingFile.delete();
		
		//create a zero file (will iteratively add until all data accounted for)
		TwoTuple<Long, Long> simDesc = buildZeroFile(inputFile, outputFile, particleFilePrep, autocorWritePrep, velACTypeNames);
		long numTimesteps = simDesc.getValue1();
		long numParticles = simDesc.getValue2();
		
		sumDotProducts(inputFile, outputFile, workingFile, numTimesteps, numParticles, particleFilePrep, autocorFilePrep, autocorWritePrep, velACTypeNames);
		divideSums(outputFile, workingFile, numTimesteps, numParticles, autocorFilePrep, autocorWritePrep, velACTypeNames);
		
		//if the user wants output in xml, give it to them
		if(toXML){
			convertToXML(outputFile, workingFile, autocorFilePrep);
		}
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
		if(curStruct == null){
			throw new IOException("MD data is empty.");
		}
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
	 * This will sum the particle velocity dot products.
	 * @param inputFile The velocity data.
	 * @param outputFile The file to write the sums to.
	 * @param workingFile A safe working file name.
	 * @param numTimesteps The number of timesteps in the simulation.
	 * @param numParticles The number of particles in the simulation.
	 * @param particleFilePrep The parser for velocity data.
	 * @param autocorFilePrep The parser for autocorrelation data.
	 * @param autocorWritePrep The compiler for autocorreltation data.
	 * @param velACTypeNames The types in autocorrelation files.
	 * @throws IOException If there is a problem reading or writing.
	 */
	private static void sumDotProducts(File inputFile, File outputFile, File workingFile, long numTimesteps, long numParticles, BinaryDataReader particleFilePrep, BinaryDataReader autocorFilePrep, BinaryDataWriter autocorWritePrep, Map<String, StructureType> velACTypeNames) throws IOException{
		ListDouble startVelocities = new ArrayListDouble();
		StructureInputStream startReader = particleFilePrep.openStructureFile(new BufferedInputStream(new FileInputStream(inputFile)));
		for(long startStep = 0; startStep < numTimesteps; startStep++){
			//rename the output file
			outputFile.renameTo(workingFile);
			//skip the time step
			startReader.readNextEntry();
			for(long startPart = 0; startPart < numParticles; startPart++){
				Structure startPartDat = startReader.readNextEntry();
				ArbitraryFloat[] spdVel = startPartDat.floatVals[startPartDat.getType().getFloats().getFloatVariableIndex("VELOCITY")];
				for(int dim = 0; dim < spdVel.length; dim++){
					startVelocities.add(spdVel[dim].doubleValue());
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
			//open the previous accumulation of autocorrelations
			StructureInputStream prevAuto = autocorFilePrep.openStructureFile(new BufferedInputStream(new FileInputStream(workingFile)));
			//open the next accumulation of autocorrelations
			StructureOutputStream nextAuto = autocorWritePrep.prepStructureFile(new BufferedOutputStream(new FileOutputStream(outputFile)));
			
			//dot products
			Structure nextAutocorrelationStruct = new Structure(velACTypeNames.get("AUTOCORRELATION"));
			int nextAutoValInd = nextAutocorrelationStruct.getType().getFloats().getFloatVariableIndex("VALUE");
			nextAutocorrelationStruct.floatVals[nextAutoValInd] = new ArbitraryFloat[1];
			for(long lagStep = startStep; lagStep<numTimesteps; lagStep++){
				//skip time step info
				curReader.readNextEntry();
				//just copy the lag
				nextAuto.writeNextEntry(prevAuto.readNextEntry());
				int curInd = 0;
				for(long curPart = 0; curPart < numParticles; curPart++){
					Structure prevAutoStruct = prevAuto.readNextEntry();
					double prevAutocorrelation = prevAutoStruct.floatVals[prevAutoStruct.getType().getFloats().getFloatVariableIndex("VALUE")][0].doubleValue();
					Structure curPartDat = curReader.readNextEntry();
					double dotProd = 0;
					ArbitraryFloat[] curPartVel = curPartDat.floatVals[curPartDat.getType().getFloats().getFloatVariableIndex("VELOCITY")];
					if(dataDim >= 0){
						if(curPartVel.length != dataDim){
							throw new IOException("Data dimensionality does not match specification.");
						}
					}
					for(int dim = 0; dim<curPartVel.length; dim++){
						dotProd += (curPartVel[dim].doubleValue() * startVelocities.get(curInd));
						curInd++;
					}
					nextAutocorrelationStruct.floatVals[nextAutoValInd][0] = new ArbitraryFloat(prevAutocorrelation + dotProd);
					nextAuto.writeNextEntry(nextAutocorrelationStruct);
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
			startVelocities.clear();
		}
		startReader.close();
	}
	
	/**
	 * This will divide the summed dot products to get an average.
	 * @param outputFile The file to write the averages to.
	 * @param workingFile A safe working file.
	 * @param numTimesteps The number of timesteps in the simulation.
	 * @param numParticles The number of particles in the simulation.
	 * @param autocorFilePrep The parser for autocorrelation data.
	 * @param autocorWritePrep The compiler for autocorreltation data.
	 * @param velACTypeNames The types in autocorrelation files.
	 * @throws IOException If there is a problem reading or writing.
	 */
	private static void divideSums(File outputFile, File workingFile, long numTimesteps, long numParticles, BinaryDataReader autocorFilePrep, BinaryDataWriter autocorWritePrep, Map<String, StructureType> velACTypeNames) throws IOException{
		//divide for the average
		outputFile.renameTo(workingFile);
		StructureInputStream summedAutoCorrs = autocorFilePrep.openStructureFile(new BufferedInputStream(new FileInputStream(workingFile)));
		StructureOutputStream averageAutoCorrs = autocorWritePrep.prepStructureFile(new BufferedOutputStream(new FileOutputStream(outputFile)));
		Structure divedAutocorrStruct = new Structure(velACTypeNames.get("AUTOCORRELATION"));
		int divAutoValInd = divedAutocorrStruct.getType().getFloats().getFloatVariableIndex("VALUE");
		divedAutocorrStruct.floatVals[divAutoValInd] = new ArbitraryFloat[1];
		for(long curTime = 0; curTime < numTimesteps; curTime++){
			double factor = 1.0 / (numTimesteps - curTime);
			averageAutoCorrs.writeNextEntry(summedAutoCorrs.readNextEntry());
			for(long curPart = 0; curPart < numParticles; curPart++){
				Structure summedAutocorrStruct = summedAutoCorrs.readNextEntry();
				divedAutocorrStruct.floatVals[divAutoValInd][0] = new ArbitraryFloat(factor * summedAutocorrStruct.floatVals[summedAutocorrStruct.getType().getFloats().getFloatVariableIndex("VALUE")][0].doubleValue());
				averageAutoCorrs.writeNextEntry(divedAutocorrStruct);
			}
		}
		summedAutoCorrs.close();
		averageAutoCorrs.close();
		workingFile.delete();
	}
	
	/**
	 * This will convert an autocorrelation file to xml.
	 * @param outputFile The file that will eventually contain xml.
	 * @param workingFile A trash file.
	 * @param autocorFilePrep The binary parser.
	 * @throws IOException If there is a problem reading or writing.
	 */
	private static void convertToXML(File outputFile, File workingFile, BinaryDataReader autocorFilePrep) throws IOException{
		XMLDataWriter xmlOutPrep = new XMLDataWriter("VAC");
		//rename the output file
		outputFile.renameTo(workingFile);
		//open working as binary
		StructureInputStream binRead = autocorFilePrep.openStructureFile(new BufferedInputStream(new FileInputStream(workingFile)));
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
	
	private CalculateAutocorrelation(){
		//force pure static
	}
}