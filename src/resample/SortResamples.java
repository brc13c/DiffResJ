package resample;
import datastructures.structure.Structure;

import resources.structure.writers.StructureOutputStream;
import resources.structure.readers.StructureInputStream;
import resources.structure.writers.DataWriter;
import resources.structure.writers.BinaryDataWriter;
import resources.structure.readers.BinaryDataReader;
import resources.structure.readers.DataReader;
import datastructures.structure.StructureType;
import resources.structure.readers.XMLTypeReader;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import resources.locating.ClasspathCollection;
import resources.locating.ResourceCollection;
import java.util.List;
import java.io.File;
import java.io.IOException;

/**
 * This will sort resampling files.
 * @author Benjamin
 */
public class SortResamples{
	
	/**
	 * This will sort resample files.
	 * @param args {resampleFilePrefix, 000lowIndex, 000highIndex, outputPrefix, workingFile1, workingFile2}
	 * @throws IOException IF there is a problem reading or writing.
	 */
	public static void main(String[] args) throws IOException{
		//parse arguments
		String srcPrefix = null;
		String lowIndStr = null;
		String higIndStr = null;
		String dstPrefix = null;
		String wrk1Nam = null;
		String wrk2Nam = null;
		for(int i = 0; i<args.length; i++){
			String curArg = args[i];
			if(curArg.startsWith("-spr=")){srcPrefix = curArg.substring(5);}
			else if(curArg.startsWith("-low=")){lowIndStr = curArg.substring(5);}
			else if(curArg.startsWith("-hig=")){higIndStr = curArg.substring(5);}
			else if(curArg.startsWith("-dpr=")){dstPrefix = curArg.substring(5);}
			else if(curArg.startsWith("-wk1=")){wrk1Nam = curArg.substring(5);}
			else if(curArg.startsWith("-wk2=")){wrk2Nam = curArg.substring(5);}
		}
		if(srcPrefix == null || lowIndStr == null || higIndStr == null || dstPrefix == null || wrk1Nam == null || wrk2Nam == null){
			throw new IllegalArgumentException("Usage: java resample.SortResamples -spr=source_prefix -low=low_index -hig=high_index -dpr=destination_prefix -wk1=working_file_1 -wk2=working_file_2");
		}
		
		ResourceCollection classpath = new ClasspathCollection(null, "", "classpath");
		String[] resampFileName = new String[]{"typedefs", "Resamples.stx"};
		XMLTypeReader typeRead = new XMLTypeReader(classpath);
		List<StructureType> resampleTypes = typeRead.readTypes(classpath.getResource(resampFileName));
		DataReader parser = new BinaryDataReader(resampleTypes);
		DataWriter compiler = new BinaryDataWriter(resampleTypes);
		
		String randomPrefix = srcPrefix;
		String lowIndexString = lowIndStr;
		String highIndexString = higIndStr;
		String outputPrefix = dstPrefix;
		String workFile1 = wrk1Nam;
		String workFile2 = wrk2Nam;
		
		int lowIndex = Integer.parseInt(lowIndexString);
		int highIndex = Integer.parseInt(highIndexString);
		int numDigits = lowIndexString.length();
		
		//sort each file in turn
		for(int i = lowIndex; i<=highIndex; i++){
			String fullNum = Integer.toString(i);
			while(fullNum.length() < numDigits){
				fullNum = "0" + fullNum;
			}
			
			File inputFile = new File(randomPrefix + fullNum + ".rsm.sdb");
			File outputFile = new File(outputPrefix + fullNum + ".srs.sdb");
			File work1 = new File(workFile1);
			File work2 = new File(workFile2);
			tapeSort(inputFile, outputFile, work1, work2, parser, compiler);
		}
	}
	
	/**
	 * This will produce a sorted version of a file.
	 * @param input The file to sort.
	 * @param output Will hold the sorted file.
	 * @param work1 A working file.
	 * @param work2 Another working file.
	 * @param parser The reader for resample data.
	 * @param compiler The writer for resample data.
	 * @throws IOException If there is a problem reading or writing.
	 */
	private static void tapeSort(File input, File output, File work1, File work2, DataReader parser, DataWriter compiler) throws IOException{
		//copy input to output
		long fileLen = 0;
		StructureInputStream inputFrom = parser.openStructureFile(new BufferedInputStream(new FileInputStream(input)));
		StructureOutputStream outputTo = compiler.prepStructureFile(new BufferedOutputStream(new FileOutputStream(output)));
		Structure cur = inputFrom.readNextEntry();
		while(cur != null){
			outputTo.writeNextEntry(cur);
			fileLen++;
			cur = inputFrom.readNextEntry();
		}
		inputFrom.close();
		outputTo.close();
		
		long listLen = 1;
		do {
			tapeSortStep(output, work1, work2, listLen, parser, compiler);
			listLen = listLen << 1;
		} while (listLen < fileLen);
		
		work1.delete();
		work2.delete();
	}
	
	/**
	 * This will run a single stage of merges for tape sort.
	 * @param output The input/output file.
	 * @param work1 One working file.
	 * @param work2 Another working file.
	 * @param curListLen The current list length.
	 * @param parser The parser for resample data.
	 * @param compiler The compiler for resample data.
	 * @throws IOException If there is a problem reading or writing.
	 */
	private static void tapeSortStep(File output, File work1, File work2, long curListLen, DataReader parser, DataWriter compiler) throws IOException{
		//split the input to the two work files
		StructureInputStream read = parser.openStructureFile(new BufferedInputStream(new FileInputStream(output)));
		StructureOutputStream writeW1 = compiler.prepStructureFile(new BufferedOutputStream(new FileOutputStream(work1)));
		StructureOutputStream writeW2 = compiler.prepStructureFile(new BufferedOutputStream(new FileOutputStream(work2)));
		splitLoop:
			while(true){
				for(long i = 0; i<curListLen; i++){
					Structure toW1 = read.readNextEntry();
					if(toW1 == null){
						break splitLoop;
					}
					writeW1.writeNextEntry(toW1);
				}
				for(long i = 0; i<curListLen; i++){
					Structure toW2 = read.readNextEntry();
					if(toW2 == null){
						break splitLoop;
					}
					writeW2.writeNextEntry(toW2);
				}
			}
		read.close();
		writeW1.close();
		writeW2.close();
		
		//merge the lists to output
		StructureOutputStream write = compiler.prepStructureFile(new BufferedOutputStream(new FileOutputStream(output)));
		StructureInputStream readW1 = parser.openStructureFile(new BufferedInputStream(new FileInputStream(work1)));
		StructureInputStream readW2 = parser.openStructureFile(new BufferedInputStream(new FileInputStream(work2)));
		Structure curW1 = readW1.readNextEntry();
		Structure curW2 = readW2.readNextEntry();
		int partInd = curW1.getType().getIntegers().getIntegerVariableIndex("PARTICLE");
		while((curW1!=null) || (curW2!=null)){
			long numW1 = (curW1==null) ? curListLen : 0;
			long numW2 = (curW2==null) ? curListLen : 0;
			while(numW1<curListLen || numW2<curListLen){
				if(numW1>=curListLen){
					//just push from W2
					write.writeNextEntry(curW2);
					curW2 = readW2.readNextEntry();
					if(curW2 == null){
						numW2 = curListLen;
					}
					else{
						numW2++;
					}
				}
				else if(numW2>=curListLen){
					//just push from W1
					write.writeNextEntry(curW1);
					curW1 = readW1.readNextEntry();
					if(curW1 == null){
						numW1 = curListLen;
					}
					else{
						numW1++;
					}
				}
				else{
					//push the smaller entry
					long curP1 = curW1.integerVals[partInd][0].longValue();
					long curP2 = curW2.integerVals[partInd][0].longValue();
					if(curP1 <= curP2){
						write.writeNextEntry(curW1);
						curW1 = readW1.readNextEntry();
						if(curW1 == null){
							numW1 = curListLen;
						}
						else{
							numW1++;
						}
					}
					else{
						//just push from W2
						write.writeNextEntry(curW2);
						curW2 = readW2.readNextEntry();
						if(curW2 == null){
							numW2 = curListLen;
						}
						else{
							numW2++;
						}
					}
				}
			}
		}
		write.close();
		readW1.close();
		readW2.close();
	}
	
	private SortResamples(){
		//force pure static
	}
}