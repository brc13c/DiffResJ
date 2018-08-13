import java.io.*;
import resources.locating.*;
import java.util.*;
import resources.structure.readers.*;
import datastructures.primitives.*;
import datastructures.structure.*;
import datastructures.array.*;

/**
 * This will perform an all in one calculation to get velocity autocorrelation information, storing information in RAM.
 * @author Benjamin
 */
public final class VACnRAM{
	
	/**
	 * This will perform an all in one calculation to get mean square displacement information.
	 * @param args {binary trajectory file, output description file, number of resamples, maximum curvature, minimum signal to noise, dimension}
	 * @throws IOException If there is a problem reading or writing.
	 */
	public static void main(String[] args) throws IOException{
		//parse arguments
		String srcNam = null;
		String dstNam = null;
		String numStr = null;
		String dimStr = null;
		String disNam = "";
		double varRatio = 0.0;
		boolean calcTrue = false;
		String compAlg = "full";
		String aggCountStr = "16";
		String clipCountStr = "500";
		for(int i = 0; i<args.length; i++){
			String curArg = args[i];
			if(curArg.startsWith("-src=")){srcNam = curArg.substring(5);}
			else if(curArg.startsWith("-dst=")){dstNam = curArg.substring(5);}
			else if(curArg.startsWith("-num=")){numStr = curArg.substring(5);}
			else if(curArg.startsWith("-dim=")){dimStr = curArg.substring(5);}
			else if(curArg.startsWith("-adc=")){disNam = curArg.substring(5);}
			else if(curArg.startsWith("-vcr=")){varRatio = Double.parseDouble(curArg.substring(5));}
			else if(curArg.equals("-nor")){calcTrue = true;}
			else if(curArg.startsWith("-alg=")){compAlg = curArg.substring(5);}
			else if(curArg.startsWith("-agg=")){aggCountStr = curArg.substring(5);}
			else if(curArg.startsWith("-clp=")){clipCountStr = curArg.substring(5);}
		}
		if(dstNam == null || srcNam==null || numStr==null || dimStr==null){
			throw new IllegalArgumentException("Usage: java VACnRAM -src=trajectory -dst=diffusion_summary -num=number_of_resamples -dim=data_dimensionality [-adc=diffusion_distribution_file] [-vcr=variance_cutoff_ratio] [-nor] [-alg=msd_calculation_algorithm] [-mta=multitau_aggregation_factor] [-crl=clip_repeat_length]");
		}
		if(compAlg.equals("full")){}
		else if(compAlg.equals("onepass")){}
		else if(compAlg.equals("multitau")){}
		else if(compAlg.equals("cliprepeat")){}
		else{
			throw new IllegalArgumentException("Unknown algorithm specified (" + compAlg + "), must be one of full, onepass, multitau or cliprepeat.");
		}
		
		final File sourceFile = new File(srcNam);
		final File outputFile = new File(dstNam);
		String distDest = disNam;
		final int numResamps = Integer.parseInt(numStr);
		final int dimension = Integer.parseInt(dimStr);
		final int mtAgg = Integer.parseInt(aggCountStr);
		final int crLen = Integer.parseInt(clipCountStr);
		
		//read the data
		TwoTuple<double[], double[][][]> srcData = readTrajectoryData(sourceFile, dimension);
		double[] timeData = srcData.getValue1();
		double[][][] trajData = srcData.getValue2();
		if(trajData.length == 0){
			throw new IOException("Cannot calculate a diffusion coefficient with no timesteps.");
		}
		if(trajData[0].length == 0){
			throw new IOException("Cannot calculate a diffusion coefficient with no particles.");
		}
		if(timeData.length != trajData.length){
			throw new IOException("Mismatch between timesteps and particles.");
		}
		int numTS = trajData.length;
		int numPart = trajData[0].length;
		
		//calculate the AC
		double[][] partwiseData;
		if(compAlg.equals("full")){
			partwiseData = new double[numTS][numPart];
			int[] hitCount = new int[numTS];
			for(int i = 0; i<numTS; i++){
				for(int j = i; j<numTS; j++){
					hitCount[j-i] += 1;
					for(int p = 0; p<numPart; p++){
						double disp = 0;
						for(int k = 0; k<dimension; k++){
							disp += (trajData[i][p][k] * trajData[j][p][k]);
						}
						partwiseData[j-i][p] += disp;
					}
				}
			}
			for(int i = 0; i<numTS; i++){
				for(int p = 0; p<numPart; p++){
					partwiseData[i][p] = partwiseData[i][p] / hitCount[i];
				}
			}
		}
		else if(compAlg.equals("onepass")){
			partwiseData = new double[numTS][numPart];
			for(int i = 0; i<numTS; i++){
				for(int p = 0; p<numPart; p++){
					double disp = 0;
					for(int k = 0; k<dimension; k++){
						disp += (trajData[i][p][k] * trajData[0][p][k]);
					}
					partwiseData[i][p] = disp;
				}
			}
		}
		else if(compAlg.equals("multitau")){
			ListDouble newLags = new ArrayListDouble();
			newLags.add(0.0);
			List<ListDouble> partwiseDataList = new ArrayList<ListDouble>();
			for(int i = 0; i<numPart; i++){
				ListDouble curDist = new ArrayListDouble();
				curDist.add(0.0);
				partwiseDataList.add(curDist);
			}
			ListInt hitCount = new ArrayListInt();
			hitCount.add(1);
			int curStep = 1;
			while(true){
				if(curStep >= numTS){
					break;
				}
				for(int m = 1; m<mtAgg; m++){
					if(m*curStep >= numTS){
						break;
					}
					int curInd = newLags.size();
					newLags.add(timeData[m*curStep] - timeData[0]);
					hitCount.add(0);
					for(int p = 0; p<numPart; p++){
						partwiseDataList.get(p).add(0.0);
					}
					for(int t0 = 0; t0<numTS; t0+=curStep){
						int t1 = t0 + m*curStep;
						if(t1 >= numTS){
							break;
						}
						hitCount.set(curInd, hitCount.get(curInd) + 1);
						for(int p = 0; p<numPart; p++){
							double disp = 0;
							for(int k = 0; k<dimension; k++){
								disp += (trajData[t1][p][k] * trajData[t0][p][k]);
							}
							ListDouble curPSD = partwiseDataList.get(p);
							curPSD.set(curInd, curPSD.get(curInd) + disp);
						}
					}
				}
				curStep = curStep * mtAgg;
			}
			timeData = newLags.toArray();
			numTS = timeData.length;
			partwiseData = new double[numTS][numPart];
			for(int t = 0; t < numTS; t++){
				int curHits = hitCount.get(t);
				for(int p = 0; p<numPart; p++){
					partwiseData[t][p] = partwiseDataList.get(p).get(t) / curHits;
				}
			}
		}
		else if(compAlg.equals("cliprepeat")){
			int numUnTS = Math.min(numTS, crLen);
			partwiseData = new double[numUnTS][numPart];
			int[] hitCount = new int[numUnTS];
			for(int i = 0; i<numTS; i += crLen){
				for(int j = 0; j<crLen; j++){
					if(i+j >= numTS){
						break;
					}
					hitCount[j] += 1;
					for(int p = 0; p<numPart; p++){
						double disp = 0;
						for(int k = 0; k<dimension; k++){
							disp += (trajData[i][p][k] * trajData[i+j][p][k]);
						}
						partwiseData[j][p] += disp;
					}
				}
			}
			for(int i = 0; i<numUnTS; i++){
				for(int p = 0; p<numPart; p++){
					partwiseData[i][p] = partwiseData[i][p] / hitCount[i];
				}
			}
			numTS = numUnTS;
		}
		else{
			throw new IllegalArgumentException("wat");
		}
		trajData = null; //allow memory to be reclaimed
		
		//run bootstrapping
		double[] bootCoeffs = new double[numResamps];
		double[] curBootVAC = new double[numTS];
		int[] selectedItems = new int[numPart];
		Random rand = new Random(System.nanoTime());
		for(int b = 0; b<numResamps; b++){
			//random numbers
			if(calcTrue){
				for(int i = 0; i<numPart; i++){
					selectedItems[i] = i;
				}
			}
			else{
				for(int i = 0; i<numPart; i++){
					selectedItems[i] = rand.nextInt(numPart);
				}
			}
			//means
			for(int t = 0; t<numTS; t++){
				double curMean = 0;
				for(int i = 0; i<numPart; i++){
					curMean += partwiseData[t][selectedItems[i]];
				}
				curMean = curMean / numPart;
				curBootVAC[t] = curMean;
			}
			//find where the variance drops below the given value
			int finalTS = numTS;
			double firstVar = curBootVAC[0] * curBootVAC[0];
			double curVar = 0;
			for(int t = 0; t<numTS; t++){
				curVar = curVar + (curBootVAC[t]*curBootVAC[t]);
				if(((curVar / (t+1)) / firstVar) < varRatio){
					finalTS = t;
					break;
				}
			}
			//integrate up to final
			double integralVal = 0;
			for(int t = 1; t<finalTS; t++){
				integralVal += (timeData[t] - timeData[t-1])*((curBootVAC[t] + curBootVAC[t-1])/2);
			}
			integralVal = integralVal / dimension;
			//store
			bootCoeffs[b] = integralVal;
		}
		
		//output distribution
		if(!("".equals(distDest))){
			Writer outputManage = new FileWriter(distDest);
			for(int b = 0; b<numResamps; b++){
				outputManage.write(bootCoeffs[b] + "\n");
			}
			outputManage.close();
		}
		
		//find mean and dev of distribution
		double distMean = 0;
		for(int b = 0; b<numResamps; b++){
			distMean += bootCoeffs[b];
		}
		distMean = distMean / numResamps;
		double distDev = 0;
		for(int b = 0; b<numResamps; b++){
			double diff = (bootCoeffs[b] - distMean);
			distDev += (diff*diff);
		}
		distDev = Math.sqrt(distDev / (numResamps - 1));
		
		//and write to final file
		Writer outputManage = new FileWriter(outputFile);
		outputManage.write("<DIFFUSION_COEFFICIENT>\n");
		outputManage.write("	<DIFFCOEFF>\n");
		outputManage.write("		<VALUE>" + distMean + "</VALUE>\n");
		outputManage.write("		<ERROR>" + distDev + "</ERROR>\n");
		outputManage.write("	</DIFFCOEFF>\n");
		outputManage.write("</DIFFUSION_COEFFICIENT>\n");
		outputManage.close();
	}
	
	/**
	 * This will read the entire trajectory data into memory.
	 * @param sourceFile The binary trajectory file.
	 * @param expDimension The expected dimensionality of the data.
	 * @return The timesteps and particle positions in the simulation.
	 */
	protected static TwoTuple<double[], double[][][]> readTrajectoryData(File sourceFile, int expDimension) throws IOException{
		//read in the type definitions
		ResourceCollection classpath = new ClasspathCollection(null, "", "classpath");
		String[] partFileName = new String[]{"typedefs", "TrajectoryData.stx"};
		XMLTypeReader typeRead = new XMLTypeReader(classpath);
		List<StructureType> particleTypes = typeRead.readTypes(classpath.getResource(partFileName));
		BinaryDataReader particleFilePrep = new BinaryDataReader(particleTypes);
		
		//read in the trajectory data
		boolean firstTS = true;
		int numParts = -1;
		List<double[][]> partPos = new ArrayList<double[][]>();
		List<double[]> curTimestep = new ArrayList<double[]>();
		ListDouble timesteps = new ArrayListDouble();
		StructureInputStream startReader = particleFilePrep.openStructureFile(new BufferedInputStream(new FileInputStream(sourceFile)));
		Structure curSt = startReader.readNextEntry();
		while(curSt != null){
			if("TIME".equals(curSt.getType().getName())){
				timesteps.add(curSt.floatVals[curSt.getType().getFloats().getFloatVariableIndex("NEWTIME")][0].doubleValue());
				if(firstTS){
					firstTS = false;
				}
				else{
					if(numParts < 0){
						numParts = curTimestep.size();
					}
					else{
						if(numParts != curTimestep.size()){
							throw new IOException("Number of particles not constant.");
						}
					}
					double[][] curTimestepArr = curTimestep.toArray(new double[curTimestep.size()][]);
					partPos.add(curTimestepArr);
					curTimestep.clear();
				}
			}
			else if("PARTICLE".equals(curSt.getType().getName())){
				if(firstTS){
					throw new IOException("Encountered data before a timestep specification.");
				}
				int datInd = curSt.getType().getFloats().getFloatVariableIndex("VELOCITY");
				if(curSt.floatVals[datInd].length != expDimension){
					throw new IOException("Encountered particle with incorrect dimension.");
				}
				double[] curPart = new double[expDimension];
				for(int i = 0; i<expDimension; i++){
					curPart[i] = curSt.floatVals[datInd][i].doubleValue();
				}
				curTimestep.add(curPart);
			}
			curSt = startReader.readNextEntry();
		}
		//handle anything at the end
		if(curTimestep.size() > 0){
			if(!firstTS){
				if(numParts >= 0){
					if(numParts != curTimestep.size()){
						throw new IOException("Number of particles not constant.");
					}
				}
				double[][] curTimestepArr = curTimestep.toArray(new double[curTimestep.size()][]);
				partPos.add(curTimestepArr);
				curTimestep.clear();
			}
		}
		//close
		startReader.close();
		//put lists into array
		double[] timeDat = timesteps.toArray();
		double[][][] trajDat = partPos.toArray(new double[partPos.size()][][]);
		return new TwoTuple<double[], double[][][]>(timeDat, trajDat);
	}
	
	private VACnRAM(){
		//force pure static
	}
}