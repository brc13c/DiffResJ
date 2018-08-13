package resources.structure.writers;
import datastructures.structure.StructureType;
import datastructures.structure.Structure;
import java.io.IOException;

/**
 * This represents a enpoint of structures.
 * @author Benjamin
 */
public abstract class StructureOutputStream{
	
	/**
	 * The minimum meaningful length.
	 */
	protected static final int MINVALIDSIZE = 0;
	
	/**
	 * The empty string.
	 */
	protected static final String EMPTYSTRING = "";
	
	/**
	 * This will write the next entry to file.
	 * @param toWrite The next entry to write.
	 * @throws IOException If there is a problem writing the structure.
	 */
	public abstract void writeNextEntry(Structure toWrite) throws IOException;
	
	/**
	 * Closes the file.
	 * @throws IOException If there is a problem.
	 */
	public abstract void close() throws IOException;
	
	/**
	 * This checks that a structure conforms to a type specification.
	 * This is a best-effort check; in particular, if you somehow have
	 * two types with the same name but different contents, this method will not catch the difference).
	 * @param toCheck The structure in question.
	 * @param toComp The type to compare it to.
	 * @throws IOException If the structure is non-conforming.
	 */
	protected void checkType(final Structure toCheck, final StructureType toComp) throws IOException{
		if(toCheck.booleanVals.length != toComp.getBooleans().getNumberOfBooleans()){
			throw new IOException("Given structure doesn't have the correct number of booleans.");
		}
		for(int i = 0; i<toCheck.booleanVals.length; i++){
			if(toComp.getBooleans().getBooleanVariableLength(i)>=MINVALIDSIZE && toCheck.booleanVals[i].length!=toComp.getBooleans().getBooleanVariableLength(i)){
				throw new IOException("Boolean doesn't have the correct size: " + toComp.getBooleans().getBooleanVariableName(i));
			}
		}
		if(toCheck.integerVals.length != toComp.getIntegers().getNumberOfIntegers()){
			throw new IOException("Given structure doesn't have the correct number of integers.");
		}
		for(int i = 0; i<toCheck.integerVals.length; i++){
			if(toComp.getIntegers().getIntegerVariableLength(i)>=MINVALIDSIZE && toCheck.integerVals[i].length!=toComp.getIntegers().getIntegerVariableLength(i)){
				throw new IOException("Integer doesn't have the correct size: " + toComp.getIntegers().getIntegerVariableName(i));
			}
		}
		if(toCheck.floatVals.length != toComp.getFloats().getNumberOfFloats()){
			throw new IOException("Given structure doesn't have the correct number of floats.");
		}
		for(int i = 0; i<toCheck.floatVals.length; i++){
			if(toComp.getFloats().getFloatVariableLength(i)>=MINVALIDSIZE && toCheck.floatVals[i].length!=toComp.getFloats().getFloatVariableLength(i)){
				throw new IOException("Float doesn't have the correct size: " + toComp.getFloats().getFloatVariableName(i));
			}
		}
		if(toCheck.characterVals.length != toComp.getCharacters().getNumberOfStrings()){
			throw new IOException("Given structure doesn't have the correct number of characters.");
		}
		for(int i = 0; i<toCheck.characterVals.length; i++){
			if(toComp.getCharacters().getStringVariableLength(i)>=MINVALIDSIZE && toCheck.characterVals[i].length!=toComp.getCharacters().getStringVariableLength(i)){
				throw new IOException("Character doesn't have the correct size: " + toComp.getCharacters().getStringVariableName(i));
			}
		}
		if(toCheck.structVals.length != toComp.getStructures().getNumberOfStructures()){
			throw new IOException("Given structure doesn't have the correct number of structures.");
		}
		for(int i = 0; i<toCheck.structVals.length; i++){
			if(toComp.getStructures().getStructureVariableLength(i)>=MINVALIDSIZE && toCheck.structVals[i].length!=toComp.getStructures().getStructureVariableLength(i)){
				throw new IOException("Structure doesn't have the correct size: " + toComp.getStructures().getStructureVariableName(i));
			}
			final String expectedType = toComp.getStructures().getStructureVariableType(i);
			if(!EMPTYSTRING.equals(expectedType)){
				for(int j = 0; j<toCheck.structVals[i].length; j++){
					if(!expectedType.equals(toCheck.structVals[i][j].getType().getName())){
						throw new IOException("Structure has invalid type: " + toComp.getStructures().getStructureVariableName(i) + "[" + j + "]");
					}
				}
			}
		}
	}
}