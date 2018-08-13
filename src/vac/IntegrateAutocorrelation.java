package vac;
import mathlibrary.number.ArbitraryFloat;

import datastructures.structure.Structure;
import resources.structure.writers.StructureOutputStream;
import resources.structure.readers.StructureInputStream;
import resources.structure.writers.BinaryDataWriter;
import resources.structure.writers.DataWriter;
import resources.structure.writers.XMLDataWriter;
import resources.structure.readers.BinaryDataReader;
import datastructures.structure.StructureType;
import resources.structure.readers.XMLTypeReader;
import resources.locating.ClasspathCollection;
import resources.locating.ResourceCollection;
import java.util.Map;
import datastructures.primitives.ArrayListDouble;
import datastructures.primitives.ListDouble;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This will integrate the autocorrelations of particle velocities over all lags.
 * @author Benjamin
 */
public final class IntegrateAutocorrelation{
	
	/**
	 * This will integrate autocorrelations across lags.
	 * @param args {Autocorrelation file, integrated file, dimension, [-xml]}
	 * @throws IOException If there is a problem reading or writing.
	 */
	public static void main(String[] args) throws IOException{
		//parse arguments
		String srcNam = null;
		String dstNam = null;
		String dimStr = null;
		boolean toXML = false;
		double varRatio = 0.0;
		for(int i = 0; i<args.length; i++){
			String curArg = args[i];
			if(curArg.startsWith("-src=")){srcNam = curArg.substring(5);}
			else if(curArg.startsWith("-dst=")){dstNam = curArg.substring(5);}
			else if(curArg.startsWith("-dim=")){dimStr = curArg.substring(5);}
			else if(curArg.startsWith("-vcr=")){varRatio = Double.parseDouble(curArg.substring(5));}
			else if(curArg.equals("-xml")){toXML = true;}
		}
		if(srcNam == null || dstNam == null || dimStr == null){
			throw new IllegalArgumentException("Usage: java vac.IntegrateAutocorrelation -src=input_autocorrelation -dst=output_integration_file -dim=source_dimensionality [-vcr=variance_cutoff_ratio]");
		}
		String inputFileName = srcNam;
		String outputFileName = dstNam;
		int dimension = Integer.parseInt(dimStr);
		
		//read in the type definitions
		ResourceCollection classpath = new ClasspathCollection(null, "", "classpath");
		String[] vactFileName = new String[]{"typedefs", "Autocorrelations.stx"};
		String[] intVACFileName = new String[]{"typedefs", "IntegratedAutocorrelations.stx"};
		XMLTypeReader typeRead = new XMLTypeReader(classpath);
		List<StructureType> velACTypes = typeRead.readTypes(classpath.getResource(vactFileName));
		List<StructureType> intVACTypes = typeRead.readTypes(classpath.getResource(intVACFileName));
		Map<String, StructureType> intVACTypeNames = typeRead.wrapTypes(intVACTypes);
		
		//prep the files
		BinaryDataReader autocorFilePrep = new BinaryDataReader(velACTypes);
		DataWriter intAutocorFilePrep = toXML ? new XMLDataWriter("AUTOCORRELATION_INTEGRALS") : new BinaryDataWriter(intVACTypes);
		File inputFile = new File(inputFileName);
		File outputFile = new File(outputFileName);
		StructureInputStream autocors = autocorFilePrep.openStructureFile(new BufferedInputStream(new FileInputStream(inputFile)));
		StructureOutputStream integs = intAutocorFilePrep.prepStructureFile(new BufferedOutputStream(new FileOutputStream(outputFile)));
		
		//prep storage
		double prevLag = 0;
		ListDouble prevAutos = new ArrayListDouble();
		double curLag = 0;
		ListDouble integrals = new ArrayListDouble();
		int curPart = 0;
		//and the variance
		boolean firstTS = true;
		double startVar = 0;
		double curVar = 0;
		double avgAutocor = 0;
		boolean hasHitCutoff = false;
		long numTS = 0;
		
		//start reading
		Structure curStruct = autocors.readNextEntry();
		while(curStruct!=null){
			if("TIME".equals(curStruct.getType().getName())){
				if(firstTS){
					if(curPart > 0){
						startVar = avgAutocor / curPart;
						startVar = startVar * startVar;
						firstTS = false;
						numTS++;
					}
				}
				else{
					if(curPart > 0){
						numTS++;
						double curVarAdd = avgAutocor / curPart;
						curVar = curVar + (curVarAdd*curVarAdd);
						avgAutocor = 0;
						double curRatio = ((curVar / numTS) / startVar);
						if(curRatio < varRatio){
							hasHitCutoff = true;
						}
					}
				}
				prevLag = curLag;
				curLag = curStruct.floatVals[curStruct.getType().getFloats().getFloatVariableIndex("LAG")][0].doubleValue();
				curPart = 0;
			}
			else if("AUTOCORRELATION".equals(curStruct.getType().getName())){
				if(!hasHitCutoff){
					double curAutocor = curStruct.floatVals[curStruct.getType().getFloats().getFloatVariableIndex("VALUE")][0].doubleValue();
					avgAutocor += curAutocor;
					if(curPart < prevAutos.size()){
						double prevAutocor = prevAutos.get(curPart);
						double delLag = curLag - prevLag;
						double area = delLag * (curAutocor + prevAutocor) / 2;
						prevAutos.set(curPart, curAutocor);
						integrals.set(curPart, integrals.get(curPart) + area);
					}
					else{
						prevAutos.add(curAutocor);
						integrals.add(0);
					}
					curPart++;
				}
			}
			else{
				throw new IOException("Unknown structure type " + curStruct.getType().getName());
			}
			curStruct = autocors.readNextEntry();
		}
		autocors.close();
		
		//write
		Structure writeReuse = new Structure(intVACTypeNames.get("INTEGRATED_AUTOCORRELATION"));
		int valInd = writeReuse.getType().getFloats().getFloatVariableIndex("VALUE");
		writeReuse.floatVals[valInd] = new ArbitraryFloat[1];
		for(double curInt : integrals){
			writeReuse.floatVals[valInd][0] = new ArbitraryFloat(curInt / dimension);
			integs.writeNextEntry(writeReuse);
		}
		integs.close();
	}
	
	private IntegrateAutocorrelation(){
		//force pure static
	}
}