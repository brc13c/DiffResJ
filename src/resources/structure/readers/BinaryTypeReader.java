package resources.structure.readers;
import datastructures.structure.StructureVariableDescription;

import datastructures.structure.CharacterVariableDescription;
import datastructures.structure.FloatVariableDescription;
import datastructures.structure.IntegerVariableDescription;
import datastructures.structure.BooleanVariableDescription;
import datastructures.structure.StructureType;
import exceptions.CompilationException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.io.IOException;
import java.util.List;
import resources.locating.Resource;

/**
 * This will read types from a binary file.
 * @author Benjamin
 */
public class BinaryTypeReader extends TypeReader{
	
	/**
	 * The maximum supported version.
	 */
	protected static final int MAXVERSION = 0;
	
	@Override
	public List<StructureType> readTypes(final Resource target) throws IOException {
		final List<StructureType> toRet = new ArrayList<StructureType>();
		//the zeroth element is always the empty structure type
		toRet.add(StructureType.EMPTYTYPE);
		
		try {
			readTypes(target.read(), toRet);
		} catch (CompilationException e) {
			throw new IOException(e);
		}
		
		return toRet;
	}
	
	/**
	 * This will read types from a binary stream into a list (filtering out duplicates).
	 * @param reading The stream to read.
	 * @param toAdd The list to add new types to.
	 * @throws IOException If there is a problem reading.
	 * @throws CompilationException If there is an incompatible duplicate.
	 */
	//need a large amount of one off, immutable objects
	@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
	protected void readTypes(final InputStream reading, final List<StructureType> toAdd) throws IOException, CompilationException{
		final DataInputStream source = new DataInputStream(new BufferedInputStream(reading));
		
		//make sure it's version zero
		final int version = source.readInt();
		if(version > MAXVERSION){
			throw new IOException("Unsupported file version.");
		}
		
		//now read types
		final int numTypes = source.readInt();
		for(int i = 0; i<numTypes; i++){
			final StructureType newType = readType(source);
			//make sure it's not a duplicate
			boolean needAdd = true;
			//make sure there's not already a type of the same name
			for(final StructureType existing : toAdd){
				if(existing.getName().equals(newType.getName())){
					if(existing.equals(newType)){
						needAdd = false;
						break;
					}
					else{
						throw new CompilationException("Incompatible type definition for " + newType.getName());
					}
				}
			}
			if(needAdd){
				toAdd.add(newType);
			}
		}
		
		source.close();
	}
	
	/**
	 * This will read a single type from file.
	 * @param source The file to read from.
	 * @return The read type.
	 * @throws IOException If there is a problem reading.
	 */
	@SuppressWarnings("PMD.PrematureDeclaration") //side effects
	protected StructureType readType(final DataInputStream source) throws IOException{
		final String typeName = source.readUTF();
		
		final int numBooleans = source.readShort();
		String[] booleanNames = new String[numBooleans];
		int[] booleanLengths = new int[numBooleans];
		for(int i = 0; i<numBooleans; i++){
			booleanNames[i] = source.readUTF();
			booleanLengths[i] = source.readShort();
		}
		final BooleanVariableDescription booleanVars = new BooleanVariableDescription(booleanNames, booleanLengths);
		
		final int numInts = source.readShort();
		String[] intNames = new String[numInts];
		int[] intDepths = new int[numInts];
		int[] intLengths = new int[numInts];
		for(int i = 0; i<numInts; i++){
			intNames[i] = source.readUTF();
			intDepths[i] = source.readShort();
			intLengths[i] = source.readShort();
		}
		final IntegerVariableDescription intVars = new IntegerVariableDescription(intNames, intDepths, intLengths);
		
		final int numFloats = source.readShort();
		String[] floatNames = new String[numFloats];
		int[] floatMDepths = new int[numFloats];
		int[] floatEDepths = new int[numFloats];
		int[] floatLengths = new int[numFloats];
		for(int i = 0; i<numFloats; i++){
			floatNames[i] = source.readUTF();
			floatMDepths[i] = source.readShort();
			floatEDepths[i] = source.readShort();
			floatLengths[i] = source.readShort();
		}
		final FloatVariableDescription floatVars = new FloatVariableDescription(floatNames, floatMDepths, floatEDepths, floatLengths);
		
		final int numChars = source.readShort();
		String[] charNames = new String[numChars];
		int[] charLengths = new int[numChars];
		for(int i = 0; i<numChars; i++){
			charNames[i] = source.readUTF();
			charLengths[i] = source.readShort();
		}
		final CharacterVariableDescription charVars = new CharacterVariableDescription(charNames, charLengths);
		
		final int numStructs = source.readShort();
		String[] structNames = new String[numStructs];
		String[] structTypes = new String[numStructs];
		int[] structLengths = new int[numStructs];
		for(int i = 0; i<numStructs; i++){
			structNames[i] = source.readUTF();
			structTypes[i] = source.readUTF();
			structLengths[i] = source.readShort();
		}
		final StructureVariableDescription structVars = new StructureVariableDescription(structNames, structLengths, structTypes);
		
		final int numTags = source.readShort();
		String[] tags = new String[numTags];
		for(int i = 0; i<numTags; i++){
			tags[i] = source.readUTF();
		}
		
		return new StructureType(typeName, booleanVars, intVars, floatVars, charVars, structVars, tags);
	}
}