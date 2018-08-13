package msd;
import mathlibrary.number.ArbitraryFloat;

import resources.structure.readers.StructureInputStream;
import resources.structure.writers.StructureOutputStream;
import datastructures.structure.Structure;
import resources.structure.writers.BinaryDataWriter;
import resources.structure.readers.BinaryDataReader;
import datastructures.structure.StructureType;
import resources.structure.readers.XMLTypeReader;
import datastructures.array.TwoTuple;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import resources.locating.ClasspathCollection;
import resources.locating.ResourceCollection;

/**
 * This will perform a weighted fit on mean square displacement data.
 * @author Benjamin
 */
public final class FitDiffusionCoefficient{
	
	/**
	 * This will perform a weighted linear fit on mean square displacement data.
	 * @param args {maximumAllowableCurvature, minimumAllowableSignalToNoise, dimension, outputFile, inputFiles...}
	 * @throws IOException If there is a problem reading or writing files.
	 */
	public static void main(String[] args) throws IOException{
		//parse arguments
		boolean unweighted = true;
		String mcvStr = null;
		String msnStr = null;
		String dimStr = null;
		String dstNam = null;
		List<String> allInputs = new ArrayList<String>();
		for(int i = 0; i<args.length; i++){
			String curArg = args[i];
			if(curArg.startsWith("-src=")){allInputs.add(curArg.substring(5));}
			else if(curArg.startsWith("-dst=")){dstNam = curArg.substring(5);}
			else if(curArg.startsWith("-cur=")){mcvStr = curArg.substring(5);}
			else if(curArg.startsWith("-sig=")){msnStr = curArg.substring(5);}
			else if(curArg.startsWith("-dim=")){dimStr = curArg.substring(5);}
			else if(curArg.equals("-wls")){unweighted = false;}
		}
		if(mcvStr==null || msnStr==null || dimStr==null || dstNam == null || allInputs.size()==0){
			throw new IllegalArgumentException("Usage: java msd.FitDiffusionCoefficient -cur=maximum_curvature -sig=minimum_signal_to_noise -dim=data_dimensionality -dst=output_file [-src=input_file]+ [-wls]");
		}
		
		//read in types
		ResourceCollection classpath = new ClasspathCollection(null, "", "classpath");
		XMLTypeReader typeRead = new XMLTypeReader(classpath);
		
		String[] typeFileName = new String[]{"typedefs", "MeanSquareDisplacement.stx"};
		List<StructureType> msdTypes = typeRead.readTypes(classpath.getResource(typeFileName));
		BinaryDataReader inputManage = new BinaryDataReader(msdTypes);
		
		String[] diffFileName = new String[]{"typedefs", "DiffusionCoeffs.stx"};
		List<StructureType> difCofTypes = typeRead.readTypes(classpath.getResource(diffFileName));
		Map<String, StructureType> difCoefNames = typeRead.wrapTypes(difCofTypes);
		BinaryDataWriter outputManage = new BinaryDataWriter(difCofTypes);
		
		final double maxCurve = Double.parseDouble(mcvStr);
		final double maxSTN = Double.parseDouble(msnStr);
		final int dimension = Integer.parseInt(dimStr);
		
		//create a reusable structure
		Structure diffCoeff = new Structure(difCoefNames.get("DIFFCOEFF"));
		int difInd = diffCoeff.getType().getFloats().getFloatVariableIndex("VALUE");
		diffCoeff.floatVals[difInd] = new ArbitraryFloat[1];
		//open the output stream
		StructureOutputStream toWrite = outputManage.prepStructureFile(new BufferedOutputStream(new FileOutputStream(dstNam)));
		
		//run through each input stream, performing analysis
		for(int i = 0; i<allInputs.size(); i++){
			StructureInputStream toRead = inputManage.openStructureFile(new BufferedInputStream(new FileInputStream(allInputs.get(i))));
			//build the linear system
			double a00 = 0;
			double a01 = 0;
			double a11 = 0;
			double b00 = 0;
			double b10 = 0;
			TwoTuple<Structure, Structure> curVal = skipExtremeCurvature(toRead, maxCurve);
			boolean isFirstTS = true;
			double firstSTN = 0;
			while(curVal != null){
				double cvVal = curVal.getValue2().floatVals[curVal.getValue2().getType().getFloats().getFloatVariableIndex("VALUE")][0].doubleValue();
				double cvDev = curVal.getValue2().floatVals[curVal.getValue2().getType().getFloats().getFloatVariableIndex("DEVIATION")][0].doubleValue();
				double signalToNoise = Math.abs(cvVal / cvDev);
				if(isFirstTS){
					firstSTN = signalToNoise;
					isFirstTS = false;
				}
				else{
					if((signalToNoise / firstSTN) < maxSTN){
						break;
					}
				}
				
				double curTime = curVal.getValue1().floatVals[curVal.getValue1().getType().getFloats().getFloatVariableIndex("LAG")][0].doubleValue();
				double curMSD = curVal.getValue2().floatVals[curVal.getValue2().getType().getFloats().getFloatVariableIndex("VALUE")][0].doubleValue();
				double curDev = curVal.getValue2().floatVals[curVal.getValue2().getType().getFloats().getFloatVariableIndex("DEVIATION")][0].doubleValue();
				if(unweighted){
					curDev = 1.0;
				}
				a00 = a00 + (1 / (curDev*curDev));
				a01 = a01 + (curTime / (curDev*curDev));
				a11 = a11 + ((curTime*curTime) / (curDev*curDev));
				b00 = b00 + (curMSD / (curDev*curDev));
				b10 = b10 + (curTime * curMSD / (curDev*curDev));
				
				curVal = readNextPoint(toRead);
			}
			toRead.close();
			//solve for diffusion
			double diffusionCoeff = (a00*b10 - a01*b00) / (2*dimension*(a00*a11 - a01*a01));
			//write
			diffCoeff.floatVals[difInd][0] = new ArbitraryFloat(diffusionCoeff);
			toWrite.writeNextEntry(diffCoeff);
		}
		toWrite.close();
	}
	
	/**
	 * This will skip through points that have too large a curvature.
	 * @param toRead The file to read through.
	 * @param maxCurvature The maximum curvature to allow.
	 * @return The point after the first point with a sufficiently low curvature, or null if the end of the file was reached.
	 * @throws IOException If there is a problem reading.
	 */
	private static TwoTuple<Structure, Structure> skipExtremeCurvature(StructureInputStream toRead, double maxCurvature) throws IOException{
		int startTS = 2;
		TwoTuple<Structure, Structure> prevMSD = readNextPoint(toRead);
		if(prevMSD == null){
			return null;
		}
		TwoTuple<Structure, Structure> curMSD = readNextPoint(toRead);
		if(curMSD == null){
			return null;
		}
		TwoTuple<Structure, Structure> nextMSD = readNextPoint(toRead);
		if(nextMSD == null){
			return null;
		}
		double nextVal = nextMSD.getValue2().floatVals[nextMSD.getValue2().getType().getFloats().getFloatVariableIndex("VALUE")][0].doubleValue();
		double nextDev = nextMSD.getValue2().floatVals[nextMSD.getValue2().getType().getFloats().getFloatVariableIndex("DEVIATION")][0].doubleValue();
		double nextTim = nextMSD.getValue1().floatVals[nextMSD.getValue1().getType().getFloats().getFloatVariableIndex("LAG")][0].doubleValue();
		double curVal = curMSD.getValue2().floatVals[curMSD.getValue2().getType().getFloats().getFloatVariableIndex("VALUE")][0].doubleValue();
		double curDev = curMSD.getValue2().floatVals[curMSD.getValue2().getType().getFloats().getFloatVariableIndex("DEVIATION")][0].doubleValue();
		double curTim = curMSD.getValue1().floatVals[curMSD.getValue1().getType().getFloats().getFloatVariableIndex("LAG")][0].doubleValue();
		double prevVal = prevMSD.getValue2().floatVals[prevMSD.getValue2().getType().getFloats().getFloatVariableIndex("VALUE")][0].doubleValue();
		double prevDev = prevMSD.getValue2().floatVals[prevMSD.getValue2().getType().getFloats().getFloatVariableIndex("DEVIATION")][0].doubleValue();
		double prevTim = prevMSD.getValue1().floatVals[prevMSD.getValue1().getType().getFloats().getFloatVariableIndex("LAG")][0].doubleValue();
		double timRat = (curTim - prevTim)/(nextTim-curTim);
		double curvMag = timRat*(nextVal-curVal)-(curVal-prevVal);
		double curvErr = Math.sqrt(timRat*timRat*nextDev*nextDev + (1+timRat)*(1+timRat)*curDev*curDev + prevDev*prevDev);
		double curvature = Math.abs(curvMag / curvErr);
		while(curvature > maxCurvature){
			startTS++;
			prevMSD = curMSD;
			curMSD = nextMSD;
			nextMSD = readNextPoint(toRead);
			if(nextMSD == null){
				return null;
			}
			nextVal = nextMSD.getValue2().floatVals[nextMSD.getValue2().getType().getFloats().getFloatVariableIndex("VALUE")][0].doubleValue();
			nextDev = nextMSD.getValue2().floatVals[nextMSD.getValue2().getType().getFloats().getFloatVariableIndex("DEVIATION")][0].doubleValue();
			nextTim = nextMSD.getValue1().floatVals[nextMSD.getValue1().getType().getFloats().getFloatVariableIndex("LAG")][0].doubleValue();
			curVal = curMSD.getValue2().floatVals[curMSD.getValue2().getType().getFloats().getFloatVariableIndex("VALUE")][0].doubleValue();
			curDev = curMSD.getValue2().floatVals[curMSD.getValue2().getType().getFloats().getFloatVariableIndex("DEVIATION")][0].doubleValue();
			curTim = curMSD.getValue1().floatVals[curMSD.getValue1().getType().getFloats().getFloatVariableIndex("LAG")][0].doubleValue();
			prevVal = prevMSD.getValue2().floatVals[prevMSD.getValue2().getType().getFloats().getFloatVariableIndex("VALUE")][0].doubleValue();
			prevDev = prevMSD.getValue2().floatVals[prevMSD.getValue2().getType().getFloats().getFloatVariableIndex("DEVIATION")][0].doubleValue();
			prevTim = prevMSD.getValue1().floatVals[prevMSD.getValue1().getType().getFloats().getFloatVariableIndex("LAG")][0].doubleValue();
			timRat = (curTim - prevTim)/(nextTim-curTim);
			curvMag = timRat*(nextVal-curVal)-(curVal-prevVal);
			curvErr = Math.sqrt(timRat*timRat*nextDev*nextDev + (1+timRat)*(1+timRat)*curDev*curDev + prevDev*prevDev);
			curvature = Math.abs(curvMag / curvErr);
		}
		//System.out.println("Curavture breached at timestep: " + startTS);
		return nextMSD;
	}
	
	/**
	 * This will read the next lag/msd pair.
	 * @param toRead The file to read from.
	 * @return The next lag/msd pair, or null if end of file was hit.
	 * @throws IOException If there is a problem reading.
	 */
	private static TwoTuple<Structure, Structure> readNextPoint(StructureInputStream toRead) throws IOException{
		Structure lag = null;
		Structure msd = null;
		
		while(lag == null || msd == null){
			Structure cur = toRead.readNextEntry();
			if(cur == null){
				return null;
			}
			else if("TIME".equals(cur.getType().getName())){
				lag = cur;
			}
			else if("DISPLACEMENT".equals(cur.getType().getName())){
				msd = cur;
			}
		}
		
		TwoTuple<Structure, Structure> toRet = new TwoTuple<Structure, Structure>(lag, msd);
		return toRet;
	}
	
	private FitDiffusionCoefficient(){
		//force pure static
	}
}