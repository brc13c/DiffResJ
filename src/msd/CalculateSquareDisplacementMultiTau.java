package msd;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import mathlibrary.number.ArbitraryFloat;
import resources.locating.ClasspathCollection;
import resources.locating.ResourceCollection;
import resources.structure.readers.BinaryDataReader;
import resources.structure.readers.StructureInputStream;
import resources.structure.readers.XMLTypeReader;
import resources.structure.writers.BinaryDataWriter;
import resources.structure.writers.StructureOutputStream;
import datastructures.primitives.ArrayListDouble;
import datastructures.primitives.ArrayListInt;
import datastructures.primitives.ListDouble;
import datastructures.primitives.ListInt;
import datastructures.structure.Structure;
import datastructures.structure.StructureType;

/**
 * This will calculate the square displacement of the particles in a set of trajectory data, using the multi-tau method (using the first entry of each block).
 * @author Benjamin
 */
public final class CalculateSquareDisplacementMultiTau{
	
	public static int dataDim = -1;
	
	/**
	 * This will calculate the autocorrelation of the velocities in a set of trajectory data.
	 * @param args {File to get autocorrelations for, file to write to, number of samples to aggregate}
	 * @throws IOException If there is a problem reading or writing.
	 */
	public static void main(String[] args) throws IOException{
		//parse arguments
		String srcNam = null;
		String dstNam = null;
		String aggStr = null;
		for(int i = 0; i<args.length; i++){
			String curArg = args[i];
			if(curArg.startsWith("-src=")){srcNam = curArg.substring(5);}
			else if(curArg.startsWith("-dst=")){dstNam = curArg.substring(5);}
			else if(curArg.startsWith("-agg=")){aggStr = curArg.substring(5);}
		}
		if(dstNam == null || srcNam==null || aggStr == null){
			throw new IllegalArgumentException("Usage: java msd.CalculateSquareDisplacementMultiTau -src=input_file -dst=output_file -agg=aggregation_num");
		}
		String inputFileName = srcNam;
		String outputFileName = dstNam;
		int aggregationNumber = Integer.parseInt(aggStr);
		
		//read in the type definitions
		ResourceCollection classpath = new ClasspathCollection(null, "", "classpath");
		String[] partFileName = new String[]{"typedefs", "TrajectoryData.stx"};
		String[] vactFileName = new String[]{"typedefs", "SquareDisplacement.stx"};
		XMLTypeReader typeRead = new XMLTypeReader(classpath);
		List<StructureType> particleTypes = typeRead.readTypes(classpath.getResource(partFileName));
		List<StructureType> velACTypes = typeRead.readTypes(classpath.getResource(vactFileName));
		Map<String, StructureType> velACTypeNames = typeRead.wrapTypes(velACTypes);
		BinaryDataReader particleFilePrep = new BinaryDataReader(particleTypes);
		BinaryDataWriter autocorWritePrep = new BinaryDataWriter(velACTypes);
		
		File inputFile = new File(inputFileName);
		File outputFile = new File(outputFileName);
		
		//prepare the output file and structures used for the output
		StructureOutputStream vacData = autocorWritePrep.prepStructureFile(new BufferedOutputStream(new FileOutputStream(outputFile)));
		Structure autoLag = new Structure(velACTypeNames.get("TIME"));
		int lagInd = autoLag.getType().getFloats().getFloatVariableIndex("LAG");
		autoLag.floatVals[lagInd] = new ArbitraryFloat[1];
		Structure autoZero = new Structure(velACTypeNames.get("DISPLACEMENT"));
		int valInd = autoZero.getType().getFloats().getFloatVariableIndex("VALUE");
		autoZero.floatVals[valInd] = new ArbitraryFloat[1];
		
		long curAggregation = 1;
		boolean overrun = false;
		tauLoop:
		while(!overrun){
			//prepare storage for the first aggregation timesteps/averages
			double[] times = new double[aggregationNumber];
			ListInt numDims = new ArrayListInt();
			ListDouble[] avgVelocities = new ListDouble[aggregationNumber];
			for(int i = 0; i<avgVelocities.length; i++){
				avgVelocities[i] = new ArrayListDouble();
			}
			
			//open the trajectory data
			StructureInputStream particleData = particleFilePrep.openStructureFile(new BufferedInputStream(new FileInputStream(inputFile)));
			Structure curStruct = particleData.readNextEntry();
			
			//read the first sets of particle data
			for(int curRead = 0; curRead < aggregationNumber; curRead++){
				if(curStruct == null){
					overrun = true;
					particleData.close();
					break tauLoop;
				}
				if(!"TIME".equals(curStruct.getType().getName())){
					throw new IOException("Expected time data.");
				}
				times[curRead] = curStruct.floatVals[curStruct.getType().getFloats().getFloatVariableIndex("NEWTIME")][0].doubleValue();
				for(long sampRead = 0; sampRead < curAggregation; sampRead ++){
					curStruct = particleData.readNextEntry();
					if(curStruct == null){
						overrun = true;
						particleData.close();
						break tauLoop;
					}
					while(!"TIME".equals(curStruct.getType().getName())){
						int velInd = curStruct.getType().getFloats().getFloatVariableIndex("POSITION");
						int velDim = curStruct.floatVals[velInd].length;
						if(dataDim >= 0){
							if(velDim != dataDim){
								throw new IOException("Data dimensionality does not match specification.");
							}
						}
						//store the dimension data if necessary and add the velocity data to the correct array
						if(sampRead == 0){
							if(curRead == 0){
								numDims.add(velDim);
							}
							for(int i = 0; i<velDim; i++){
								avgVelocities[curRead].add(curStruct.floatVals[velInd][i].doubleValue());
							}
						}
						else{/*Only store the first timestep.*/}
						curStruct = particleData.readNextEntry();
						if(curStruct == null){
							overrun = true;
							if(curRead == (aggregationNumber-1) && sampRead == (curAggregation-1)){
								break;
							}
							particleData.close();
							break tauLoop;
						}
					}
				}
			}
			
			//prepare storage for the output information
			ListDouble[] autocorrelations = new ListDouble[aggregationNumber];
			int[] autocorNumber = new int[aggregationNumber];
			for(int i = 0; i<autocorrelations.length; i++){
				autocorrelations[i] = new ArrayListDouble();
				autocorNumber[i] = 0;
				for(int j = 0; j<numDims.size(); j++){
					autocorrelations[i].add(0);
				}
			}
			
			
			//calculate the first set of lag data
			for(int i = 0; i < aggregationNumber; i++){
				for(int j = i; j<aggregationNumber; j++){
					int lagNum = j - i;
					int basePtr = 0;
					for(int p = 0; p < numDims.size(); p++){
						int velDim = numDims.get(p);
						double autocor = 0;
						for(int d = 0; d<velDim; d++){
							double posi = avgVelocities[i].get(basePtr + d);
							double posj = avgVelocities[j].get(basePtr + d);
							double del = posi - posj;
							autocor += (del * del);
						}
						autocorrelations[lagNum].set(p, autocorrelations[lagNum].get(p) + autocor);
						basePtr += velDim;
					}
					autocorNumber[lagNum]++;
				}
			}
			
			//read the remaining particle data
			finishLoop:
			while(curStruct != null){
				//rotate storage
				ListDouble tmpStr = avgVelocities[0];
				tmpStr.clear();
				for(int i = 0; i<(aggregationNumber-1); i++){
					avgVelocities[i] = avgVelocities[i+1];
				}
				avgVelocities[aggregationNumber-1] = tmpStr;
				//read one new set
				int curRead = aggregationNumber - 1;
				for(long sampRead = 0; sampRead < curAggregation; sampRead ++){
					curStruct = particleData.readNextEntry();
					if(curStruct == null){
						break finishLoop;
					}
					while(!"TIME".equals(curStruct.getType().getName())){
						int velInd = curStruct.getType().getFloats().getFloatVariableIndex("POSITION");
						int velDim = curStruct.floatVals[velInd].length;
						//add the velocity data to the correct array
						if(sampRead == 0){
							for(int i = 0; i<velDim; i++){
								avgVelocities[curRead].add(curStruct.floatVals[velInd][i].doubleValue());
							}
						}
						else{/*Only write first timestep.*/}
						curStruct = particleData.readNextEntry();
						if(curStruct == null){
							if(sampRead == (curAggregation - 1)){
								break;
							}
							break finishLoop;
						}
					}
				}
				//calculate the autocorrelations for that new set
				for(int i = 0; i < aggregationNumber; i++){
					int j = aggregationNumber - 1;
					int lagNum = j - i;
					int basePtr = 0;
					for(int p = 0; p < numDims.size(); p++){
						int velDim = numDims.get(p);
						double autocor = 0;
						for(int d = 0; d<velDim; d++){
							double posi = avgVelocities[i].get(basePtr + d);
							double posj = avgVelocities[j].get(basePtr + d);
							double del = posi - posj;
							autocor += (del * del);
						}
						autocorrelations[lagNum].set(p, autocorrelations[lagNum].get(p) + autocor);
						basePtr += velDim;
					}
					autocorNumber[lagNum]++;
				}
			}
			particleData.close();
			
			//output results
			for(int i = 0; i<aggregationNumber; i++){
				if(i==0 && curAggregation > 1){
					//only output zero lag on first set
					continue;
				}
				autoLag.floatVals[lagInd][0] = new ArbitraryFloat(times[i] - times[0]);
				vacData.writeNextEntry(autoLag);
				for(int j = 0; j<autocorrelations[i].size(); j++){
					autoZero.floatVals[valInd][0] = new ArbitraryFloat(autocorrelations[i].get(j) / autocorNumber[i]);
					vacData.writeNextEntry(autoZero);
				}
			}
			
			//prepare for next level of blocking
			curAggregation = curAggregation * aggregationNumber;
		}
		vacData.close();
	}
	
	private CalculateSquareDisplacementMultiTau(){
		//force pure static
	}
}