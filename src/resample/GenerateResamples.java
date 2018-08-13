package resample;
import mathlibrary.number.ArbitraryInteger;

import resources.structure.writers.StructureOutputStream;
import resources.structure.writers.BinaryDataWriter;
import datastructures.structure.Structure;
import resources.structure.readers.StructureInputStream;
import resources.structure.readers.BinaryDataReader;
import datastructures.structure.StructureType;
import resources.structure.readers.XMLTypeReader;
import resources.locating.ClasspathCollection;
import resources.locating.ResourceCollection;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.util.Random;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.util.Map;
import java.util.List;
import java.io.IOException;

/**
 * This will generate resampling guides for a trajectory file.
 * @author Benjamin
 */
public final class GenerateResamples{
	
	/**
	 * This will generate resampling guides for a trajectory file.
	 * @param args {trajectory file, resample files prefix, number of resample files}
	 * @throws IOException If there is a problem reading or writing.
	 */
	public static void main(String[] args) throws IOException{
		//parse arguments
		String srcFile = null;
		String dstPrefix = null;
		String numFiles = null;
		for(int i = 0; i<args.length; i++){
			String curArg = args[i];
			if(curArg.startsWith("-for=")){srcFile = curArg.substring(5);}
			else if(curArg.startsWith("-nam=")){dstPrefix = curArg.substring(5);}
			else if(curArg.startsWith("-num=")){numFiles = curArg.substring(5);}
		}
		if(srcFile == null || dstPrefix == null || numFiles == null){
			throw new IllegalArgumentException("Usage: java resample.GenerateResamples -for=trajectory_file -nam=resample_file_prefix -num=number_of_files");
		}
		
		String trajectoryFileName = srcFile;
		String resamplePrefix = dstPrefix;
		int numberOfResamples = Integer.parseInt(numFiles);
		int numDigits = numFiles.length();
		
		//read in the type definitions
		ResourceCollection classpath = new ClasspathCollection(null, "", "classpath");
		String[] typeFileName = new String[]{"typedefs", "TrajectoryData.stx"};
		String[] resampFileName = new String[]{"typedefs", "Resamples.stx"};
		XMLTypeReader typeRead = new XMLTypeReader(classpath);
		List<StructureType> particleTypes = typeRead.readTypes(classpath.getResource(typeFileName));
		List<StructureType> resampleTypes = typeRead.readTypes(classpath.getResource(resampFileName));
		Map<String, StructureType> resampleNames = typeRead.wrapTypes(resampleTypes);
		
		//open up the trajectory file, and count the number of particles in the first time step
		long numParticles = 0;
		BinaryDataReader trajectoryPrep = new BinaryDataReader(particleTypes);
		StructureInputStream trajectoryFile = trajectoryPrep.openStructureFile(new BufferedInputStream(new FileInputStream(trajectoryFileName)));
		Structure curStruct = trajectoryFile.readNextEntry();
		while(curStruct!=null){
			if("TIME".equals(curStruct.getType().getName())){
				if(numParticles > 0){
					break;
				}
			}
			else if("PARTICLE".equals(curStruct.getType().getName())){
				numParticles++;
			}
			curStruct = trajectoryFile.readNextEntry();
		}
		trajectoryFile.close();
		
		//generate resamples
		BinaryDataWriter writePrep = new BinaryDataWriter(resampleTypes);
		Random rand = new Random();
		Structure toWrite = new Structure(resampleNames.get("RESAMPLE"));
		final int particleInd = toWrite.getType().getIntegers().getIntegerVariableIndex("PARTICLE");
		toWrite.integerVals[particleInd] = new ArbitraryInteger[1];
		for(int i = 0; i<numberOfResamples; i++){
			String fullNum = Integer.toString(i);
			while(fullNum.length() < numDigits){
				fullNum = "0" + fullNum;
			}
			String newFileName = resamplePrefix + fullNum + ".rsm.sdb";
			
			//open
			StructureOutputStream curResample = writePrep.prepStructureFile(new BufferedOutputStream(new FileOutputStream(newFileName)));
			for(long j = 0; j<numParticles; j++){
				long nextVal = rand.nextLong();
				long nextPart = nextVal % numParticles;
				while(nextPart < 0){
					nextPart += numParticles;
				}
				toWrite.integerVals[particleInd][0] = new ArbitraryInteger(nextPart);
				curResample.writeNextEntry(toWrite);
			}
			curResample.close();
		}
	}
	
	private GenerateResamples(){
		//force pure static
	}
}