package vac;
import datastructures.structure.Structure;

import resources.structure.writers.StructureOutputStream;
import resources.structure.readers.StructureInputStream;
import resources.structure.writers.BinaryDataWriter;
import resources.structure.readers.BinaryDataReader;
import datastructures.structure.StructureType;
import resources.structure.readers.XMLTypeReader;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.util.List;
import java.util.ArrayList;
import resources.locating.ClasspathCollection;
import resources.locating.ResourceCollection;
import java.io.IOException;

/**
 * This will resample particle diffusion coefficients from a file containing particlewise integrated velocity autocorrelations.
 * @author Benjamin
 */
public final class ResampleParticleDiffusion{
	
	/**
	 * This will resample a collection of particle diffusions.
	 * @param args {individual diffusion file, resample file 1, output file 1, [resample file 2, output file 2, ...]}
	 * @throws IOException If there is a problem reading or writing.
	 */
	public static void main(String[] args) throws IOException{
		//parse arguments
		String srcNam = null;
		List<String> allInputs = new ArrayList<String>();
		List<String> allOutputs = new ArrayList<String>();
		for(int i = 0; i<args.length; i++){
			String curArg = args[i];
			if(curArg.startsWith("-rsi=")){allInputs.add(curArg.substring(5));}
			else if(curArg.startsWith("-rso=")){allOutputs.add(curArg.substring(5));}
			else if(curArg.startsWith("-src=")){srcNam = curArg.substring(5);}
		}
		if(srcNam == null || allInputs.size() == 0 || allInputs.size() != allOutputs.size()){
			throw new IllegalArgumentException("Usage: java vac.ResampleParticleDiffusion -src=source_integrated_file [-rsi=resample_file -rso=output_file]+");
		}
		//read in the type definitions
		ResourceCollection classpath = new ClasspathCollection(null, "", "classpath");
		XMLTypeReader typeRead = new XMLTypeReader(classpath);
		
		String[] typeFileName = new String[]{"typedefs", "IntegratedAutocorrelations.stx"};
		List<StructureType> intVACTypes = typeRead.readTypes(classpath.getResource(typeFileName));
		BinaryDataReader inputManage = new BinaryDataReader(intVACTypes);
		BinaryDataWriter outputManage = new BinaryDataWriter(intVACTypes);
		
		//open up the starting file
		StructureInputStream startingFile = inputManage.openStructureFile(new BufferedInputStream(new FileInputStream(srcNam)));
		
		String[] resampFileName = new String[]{"typedefs", "Resamples.stx"};
		List<StructureType> resampleTypes = typeRead.readTypes(classpath.getResource(resampFileName));
		BinaryDataReader resampManage = new BinaryDataReader(resampleTypes);
		
		//open up the resample files
		int numResampleFiles = allInputs.size();
		StructureInputStream[] resampFiles = new StructureInputStream[numResampleFiles];
		StructureOutputStream[] resampled = new StructureOutputStream[numResampleFiles];
		
		for(int i = 0; i<numResampleFiles; i++){
			String resampName = allInputs.get(i);
			String outName = allOutputs.get(i);
			resampFiles[i] = resampManage.openStructureFile(new BufferedInputStream(new FileInputStream(resampName)));
			resampled[i] = outputManage.prepStructureFile(new BufferedOutputStream(new FileOutputStream(outName)));
		}
		
		//read in the first resample for each resample file
		long[] curRes = new long[numResampleFiles];
		for(int i = 0; i<numResampleFiles; i++){
			Structure curStru = resampFiles[i].readNextEntry();
			if(curStru == null){
				curRes[i] = -1;
			}
			else{
				curRes[i] = curStru.integerVals[curStru.getType().getIntegers().getIntegerVariableIndex("PARTICLE")][0].longValue();
			}
		}
		
		//now, run through the particles diffusion coefficients, writing to any requisite resample
		long curPart = 0;
		Structure startStruct = startingFile.readNextEntry();
		while(startStruct != null){
			for(int i = 0; i<numResampleFiles; i++){
				if(curRes[i]>=0 && curRes[i]<curPart){
					throw new IOException("Resample files must be sorted; offending file " + allInputs.get(i));
				}
				while(curRes[i]>=0 && curRes[i]==curPart){
					resampled[i].writeNextEntry(startStruct);
					Structure nextResamp = resampFiles[i].readNextEntry();
					if(nextResamp==null){
						curRes[i] = -1;
					}
					else{
						curRes[i] = nextResamp.integerVals[nextResamp.getType().getIntegers().getIntegerVariableIndex("PARTICLE")][0].longValue();
					}
				}
			}
			curPart++;
			startStruct = startingFile.readNextEntry();
		}
		
		//close everything down
		startingFile.close();
		for(int i = 0; i<numResampleFiles; i++){
			resampFiles[i].close();
			resampled[i].close();
		}
	}
	
	private ResampleParticleDiffusion(){
		//force pure static
	}
}