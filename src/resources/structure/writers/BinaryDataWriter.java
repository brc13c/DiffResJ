package resources.structure.writers;
import mathlibrary.number.ArbitraryFloat;
import mathlibrary.number.ArbitraryInteger;
import datastructures.structure.StructureVariableDescription;
import datastructures.structure.CharacterVariableDescription;
import datastructures.structure.FloatVariableDescription;
import datastructures.structure.IntegerVariableDescription;
import datastructures.structure.BooleanVariableDescription;
import datastructures.structure.Structure;
import datastructures.structure.StructureType;
import java.io.DataOutputStream;
import java.util.List;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * This will write binary files corresponding to a given type system.
 * @author Benjamin
 */
public class BinaryDataWriter extends DataWriter{
	
	/**
	 * The types of entries to write to binary files.
	 */
	protected List<StructureType> types;
	
	/**
	 * This sets up a binary writer.
	 * @param types The types of entries to write to binary files.
	 */
	public BinaryDataWriter(final List<StructureType> types) {
		super();
		this.types = types;
	}
	
	@Override
	public StructureOutputStream prepStructureFile(final OutputStream toWrite) throws IOException {
		return new BinaryDataOutputStream(toWrite, types);
	}
}

/**
 * This will write a stream of structures as a binary file.
 * @author Benjamin
 */
class BinaryDataOutputStream extends StructureOutputStream{
	
	/**
	 * The number of bits in a byte.
	 */
	protected static final int BYTELENGTH = 8;
	
	/**
	 * The types of entries to write to binary files.
	 */
	protected List<StructureType> types;
	
	/**
	 * The file to write to.
	 */
	protected DataOutputStream write;
	
	/**
	 * This sets up a binary file.
	 * @param toOut The file to write to.
	 * @param types The types of entries to write to binary files.
	 */
	public BinaryDataOutputStream(final OutputStream toOut, final List<StructureType> types){
		super();
		write = new DataOutputStream(new BufferedOutputStream(toOut));
		this.types = types;
	}

	@Override
	public void writeNextEntry(final Structure toWrite) throws IOException {
		//get the type number
		final StructureType type = toWrite.getType();
		boolean found = false;
		for(int i = 0; i<types.size(); i++){
			if(types.get(i).getName().equals(type.getName())){
				if(!types.get(i).equals(type)){
					throw new IOException("Given structure has an unknown type.");
				}
				//check that the type matches the structure itself
				checkType(toWrite, type);
				write.writeInt(i);
				found = true;
				break;
			}
		}
		if(!found){
			throw new IOException("Given structure has an unknown type.");
		}
		
		writeStructureData(toWrite);
	}
	
	/**
	 * This writes a structure without writing its type number, and assuming the structure is valid.
	 * @param toWrite The structure to write.
	 * @throws IOException If there is a problem writing.
	 */
	protected void writeStructureData(final Structure toWrite) throws IOException{
		final StructureType type = toWrite.getType();
		
		final BooleanVariableDescription boolVars = type.getBooleans();
		for(int i = 0; i<boolVars.getNumberOfBooleans(); i++){
			if(boolVars.getBooleanVariableLength(i)<MINVALIDSIZE){
				//length determined at read
				write.writeShort(toWrite.booleanVals[i].length);
			}
			for(int j = 0; j<toWrite.booleanVals[i].length; j++){
				write.writeBoolean(toWrite.booleanVals[i][j]);
			}
		}
		
		final IntegerVariableDescription intVars = type.getIntegers();
		for(int i = 0; i<intVars.getNumberOfIntegers(); i++){
			if(intVars.getIntegerVariableLength(i)<MINVALIDSIZE){
				//length determined at read
				write.writeShort(toWrite.integerVals[i].length);
			}
			for(int j = 0; j<toWrite.integerVals[i].length; j++){
				int bitsToWrite;
				if(intVars.getIntegerVariableDepth(i)<MINVALIDSIZE){
					//depth determined at read
					bitsToWrite = toWrite.integerVals[i][j].bitDepth();
					bitsToWrite = (bitsToWrite % 8 == 0) ? bitsToWrite : (bitsToWrite - (bitsToWrite%8) + 8);
					write.writeShort(bitsToWrite);
				}
				else{
					bitsToWrite = intVars.getIntegerVariableDepth(i);
				}
				writeInteger(toWrite.integerVals[i][j], bitsToWrite);
			}
		}
		
		final FloatVariableDescription floatVars = type.getFloats();
		for(int i = 0; i<floatVars.getNumberOfFloats(); i++){
			if(floatVars.getFloatVariableLength(i)<MINVALIDSIZE){
				//length determined at read
				write.writeShort(toWrite.floatVals[i].length);
			}
			for(int j = 0; j<toWrite.floatVals[i].length; j++){
				int eDepth;
				if(floatVars.getFloatVariableExponentDepth(i)<MINVALIDSIZE){
					eDepth = toWrite.floatVals[i][j].exponentBitDepth();
					if(floatVars.getFloatVariableMantissaDepth(i)>=MINVALIDSIZE){
						final int totalBits = eDepth + floatVars.getFloatVariableMantissaDepth(i) + 1;
						eDepth = (totalBits % 8 == 0) ? eDepth : eDepth + (8 - (totalBits % 8));
					}
					write.writeShort(eDepth);
				}
				else{
					eDepth = floatVars.getFloatVariableExponentDepth(i);
				}
				int mDepth;
				if(floatVars.getFloatVariableMantissaDepth(i)<MINVALIDSIZE){
					mDepth = toWrite.floatVals[i][j].mantissaBitDepth();
					final int totalBits = eDepth + mDepth + 1;
					mDepth = (totalBits % 8 == 0) ? mDepth : mDepth + (8 - (totalBits % 8));
					write.writeShort(mDepth);
				}
				else{
					mDepth = floatVars.getFloatVariableMantissaDepth(i);
				}
				writeFloat(toWrite.floatVals[i][j], eDepth, mDepth);
			}
		}
		
		final CharacterVariableDescription charVars = type.getCharacters();
		for(int i = 0; i<charVars.getNumberOfStrings(); i++){
			if(charVars.getStringVariableLength(i)<MINVALIDSIZE){
				//length determined at read
				write.writeShort(toWrite.characterVals[i].length);
			}
			write.write(toWrite.characterVals[i]);
		}
		
		final StructureVariableDescription structVars = type.getStructures();
		for(int i = 0; i<structVars.getNumberOfStructures(); i++){
			if(structVars.getStructureVariableLength(i)<MINVALIDSIZE){
				//length determined at read
				write.writeShort(toWrite.structVals[i].length);
			}
			for(int j = 0; j<toWrite.structVals[i].length; j++){
				if(EMPTYSTRING.equals(structVars.getStructureVariableType(i))){
					//type determined at read
					writeNextEntry(toWrite.structVals[i][j]);
				}
				else{
					writeStructureData(toWrite.structVals[i][j]);
				}
			}
		}
	}
	
	/**
	 * This writes the bits of an integer to file.
	 * @param toWrite The integer to write.
	 * @param bitDepth The number of bits to write.
	 * @throws IOException If there is a problem writing.
	 */
	protected void writeInteger(final ArbitraryInteger toWrite, final int bitDepth) throws IOException{
		int curW = 0;
		int waiting = 0;
		for(int i = bitDepth - 1; i>=0; i--){
			waiting = waiting << 1;
			waiting = waiting + (toWrite.getBit(i) ? 1 : 0);
			curW++;
			if(curW >= BYTELENGTH){
				write.write(waiting);
				curW = 0;
				waiting = 0;
			}
		}
	}
	
	/**
	 * This writes a float to file.
	 * @param toWrite The float to write.
	 * @param eDepth The number of bits to write for the exponent.
	 * @param mDepth The number of bits to write for the mantissa.
	 * @throws IOException If there is a problem writing.
	 */
	protected void writeFloat(final ArbitraryFloat toWrite, final int eDepth, final int mDepth) throws IOException{
		//first, translate to the target bits
		final ArbitraryFloat toW = toWrite.changeBitDepth(eDepth, mDepth);
		//then, write the translation
		int curW = 1;
		int waiting = toW.getSignBit() ? 1 : 0;
		for(int i = eDepth - 1; i>=0; i--){
			waiting = waiting << 1;
			waiting = waiting + (toW.getExponentBit(i) ? 1 : 0);
			curW++;
			if(curW >= BYTELENGTH){
				write.write(waiting);
				curW = 0;
				waiting = 0;
			}
		}
		for(int i = mDepth - 1; i>=0; i--){
			waiting = waiting << 1;
			waiting = waiting + (toW.getMantissaBit(i) ? 1 : 0);
			curW++;
			if(curW >= BYTELENGTH){
				write.write(waiting);
				curW = 0;
				waiting = 0;
			}
		}
	}
	
	@Override
	public void close() throws IOException {
		write.close();
	}
}