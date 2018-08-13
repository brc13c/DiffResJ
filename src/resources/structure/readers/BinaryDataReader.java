package resources.structure.readers;
import datastructures.structure.StructureVariableDescription;
import datastructures.structure.CharacterVariableDescription;
import datastructures.structure.FloatVariableDescription;
import mathlibrary.number.ArbitraryFloat;
import mathlibrary.number.ArbitraryInteger;
import datastructures.structure.IntegerVariableDescription;
import datastructures.structure.BooleanVariableDescription;
import datastructures.structure.Structure;
import datastructures.structure.StructureType;
import java.io.EOFException;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.List;
import java.io.IOException;

/**
 * This will open binary files for reading.
 * @author Benjamin
 */
public class BinaryDataReader extends DataReader{
	
	/**
	 * The types of entries to read from binary files.
	 */
	protected List<StructureType> types;
	
	/**
	 * This will create a reader for binary files of a given type system.
	 * @param types The types of entries to read from binary files.
	 */
	public BinaryDataReader(final List<StructureType> types){
		super();
		this.types = types;
	}
	
	@Override
	public StructureInputStream openStructureFile(final InputStream source) throws IOException {
		return new BinaryDataInputStream(source, types);
	}
}

/**
 * This will parse binary files into structures.
 * @author Benjamin
 */
class BinaryDataInputStream extends StructureInputStream{
	
	/**
	 * The result of modulo division by 8 for multiples of eight.
	 */
	protected static final int DIVEIGHT = 0;
	
	/**
	 * The number of bits in a byte.
	 */
	protected static final int BYTELENGTH = 8;
	
	/**
	 * The minimum value of a byte.
	 */
	protected static final int MINBYTE = 0;
	
	/**
	 * The input stream to read from.
	 */
	protected DataInputStream source;
	
	/**
	 * The types of entries to read from the files.
	 */
	protected List<StructureType> types;
	
	/**
	 * Whether the end of the stream has been reached.
	 */
	protected boolean endOfStream;
	
	/**
	 * This creates a structure reader for the given binary stream.
	 * @param source The binary file to parse.
	 * @param types The types of data to read.
	 */
	public BinaryDataInputStream(final InputStream source, final List<StructureType> types) {
		super();
		this.source = new DataInputStream(new BufferedInputStream(source));
		this.types = types;
		endOfStream = false;
	}
	
	@Override
	public Structure readNextEntry() throws IOException {
		if(endOfStream){
			throw new EOFException("Overran end of file.");
		}
		//read the type
		int typeNum;
		try{
			typeNum = source.readInt();
		}
		catch(EOFException e){
			endOfStream = true;
			return null;
		}
		//get the type
		final StructureType curType = types.get(typeNum);
		return readStructure(curType);
	}
	
	/**
	 * This will read a structure from file, given its type.
	 * @param type The type of structure to read.
	 * @return The read structure.
	 * @throws IOException If there is a problem reading.
	 */
	@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops") //no real way around this
	protected Structure readStructure(final StructureType type) throws IOException{
		final Structure toRet = new Structure(type);
		
		final BooleanVariableDescription boolVars = type.getBooleans();
		for(int i = 0; i<toRet.booleanVals.length; i++){
			if(boolVars.getBooleanVariableLength(i)<MINVALIDSIZE){
				toRet.booleanVals[i] = new boolean[source.readShort()];
			}
			else{
				toRet.booleanVals[i] = new boolean[boolVars.getBooleanVariableLength(i)];
			}
			for(int j = 0; j<toRet.booleanVals[i].length; j++){
				toRet.booleanVals[i][j] = source.readBoolean();
			}
		}
		
		final IntegerVariableDescription intVars = type.getIntegers();
		for(int i = 0; i<toRet.integerVals.length; i++){
			if(intVars.getIntegerVariableLength(i)<MINVALIDSIZE){
				toRet.integerVals[i] = new ArbitraryInteger[source.readShort()];
			}
			else{
				toRet.integerVals[i] = new ArbitraryInteger[intVars.getIntegerVariableLength(i)];
			}
			for(int j = 0; j<toRet.integerVals[i].length; j++){
				final int bitsToRead;
				if(intVars.getIntegerVariableDepth(i)<MINVALIDSIZE){
					bitsToRead = source.readShort();
				}
				else{
					bitsToRead = intVars.getIntegerVariableDepth(i);
				}
				toRet.integerVals[i][j] = readInteger(bitsToRead);
			}
		}
		
		final FloatVariableDescription floatVars = type.getFloats();
		for(int i = 0; i<floatVars.getNumberOfFloats(); i++){
			if(floatVars.getFloatVariableLength(i)<MINVALIDSIZE){
				toRet.floatVals[i] = new ArbitraryFloat[source.readShort()];
			}
			else{
				toRet.floatVals[i] = new ArbitraryFloat[floatVars.getFloatVariableLength(i)];
			}
			for(int j = 0; j<toRet.floatVals[i].length; j++){
				int eDepth;
				if(floatVars.getFloatVariableExponentDepth(i)<MINVALIDSIZE){
					eDepth = source.readShort();
				}
				else{
					eDepth = floatVars.getFloatVariableExponentDepth(i);
				}
				int mDepth;
				if(floatVars.getFloatVariableMantissaDepth(i)<MINVALIDSIZE){
					mDepth = source.readShort();
				}
				else{
					mDepth = floatVars.getFloatVariableMantissaDepth(i);
				}
				toRet.floatVals[i][j] = readFloat(eDepth, mDepth);
			}
		}
		
		final CharacterVariableDescription charVars = type.getCharacters();
		for(int i = 0; i<charVars.getNumberOfStrings(); i++){
			if(charVars.getStringVariableLength(i)<MINVALIDSIZE){
				toRet.characterVals[i] = new byte[source.readShort()];
			}
			else{
				toRet.characterVals[i] = new byte[charVars.getStringVariableLength(i)]; 
			}
			final int numRead = source.read(toRet.characterVals[i]);
			if(numRead < toRet.characterVals[i].length){
				throw new EOFException();
			}
		}
		
		final StructureVariableDescription structVars = type.getStructures();
		for(int i = 0; i<structVars.getNumberOfStructures(); i++){
			if(structVars.getStructureVariableLength(i)<MINVALIDSIZE){
				toRet.structVals[i] = new Structure[source.readShort()];
			}
			else{
				toRet.structVals[i] = new Structure[structVars.getStructureVariableLength(i)];
			}
			StructureType expected = null;
			if(!EMPTYSTRING.equals(structVars.getStructureVariableType(i))){
				//find the type
				final String structType = structVars.getStructureVariableType(i);
				for(final StructureType cur : types){
					if(structType.equals(cur.getName())){
						expected = cur;
						break;
					}
				}
				if(expected==null){
					throw new IOException("Unknown type.");
				}
			}
			for(int j = 0; j<toRet.structVals[i].length; j++){
				if(EMPTYSTRING.equals(structVars.getStructureVariableType(i))){
					toRet.structVals[i][j] = readNextEntry();
				}
				else{
					toRet.structVals[i][j] = readStructure(expected);
				}
			}
		}
		
		return toRet;
	}
	
	@Override
	public void close() throws IOException {
		source.close();
	}
	
	/**
	 * This read the bits of an integer from file.
	 * @param bitDepth The number of bits to read.
	 * @return The read integer.
	 * @throws IOException If there is a problem reading.
	 */
	protected ArbitraryInteger readInteger(final int bitDepth) throws IOException{
		if(bitDepth % 8 != DIVEIGHT){
			throw new IOException("Invalid bit depth for integer.");
		}
		boolean[] vals = new boolean[bitDepth];
		int curBit = vals.length-1;
		while(curBit >= 0){
			final int curVal = source.read();
			if(curVal < MINBYTE){
				throw new EOFException();
			}
			vals[curBit  ] = ((curVal >> 7) & 0x01) == 1;
			vals[curBit-1] = ((curVal >> 6) & 0x01) == 1;
			vals[curBit-2] = ((curVal >> 5) & 0x01) == 1;
			vals[curBit-3] = ((curVal >> 4) & 0x01) == 1;
			vals[curBit-4] = ((curVal >> 3) & 0x01) == 1;
			vals[curBit-5] = ((curVal >> 2) & 0x01) == 1;
			vals[curBit-6] = ((curVal >> 1) & 0x01) == 1;
			vals[curBit-7] = ((curVal) & 0x01) == 1;
			curBit -= 8;
		}
		return new ArbitraryInteger(vals);
	}
	
	/**
	 * This reads a float from file.
	 * @param eDepth The number of bits to read for the exponent.
	 * @param mDepth The number of bits to read for the mantissa.
	 * @return The read float
	 * @throws IOException If there is a problem writing.
	 */
	protected ArbitraryFloat readFloat(final int eDepth, final int mDepth) throws IOException{
		if((eDepth + mDepth + 1)%8 != DIVEIGHT){
			throw new IOException("Invalid bit depth for float.");
		}
		
		int waiting = source.read();
		if(waiting < MINBYTE){
			throw new EOFException();
		}
		@SuppressWarnings("PMD.PrematureDeclaration") //waiting changes too quickly
		final boolean signBit = (waiting & 0x0080) != 0;
		waiting = waiting << 1;
		int curR = 1;
		
		boolean[] exponent = new boolean[eDepth];
		for(int i = eDepth - 1; i>=0; i--){
			if(curR >= BYTELENGTH){
				curR = 0;
				waiting = source.read();
				if(waiting < MINBYTE){
					throw new EOFException();
				}
			}
			exponent[i] = (waiting & 0x0080) != 0;
			waiting = waiting << 1;
			curR++;
		}
		
		boolean[] mantissa = new boolean[mDepth];
		for(int i = mDepth - 1; i>=0; i--){
			if(curR >= BYTELENGTH){
				curR = 0;
				waiting = source.read();
				if(waiting < MINBYTE){
					throw new EOFException();
				}
			}
			mantissa[i] = (waiting & 0x0080) != 0;
			waiting = waiting << 1;
			curR++;
		}
		
		return new ArbitraryFloat(signBit, exponent, mantissa);
	}
}