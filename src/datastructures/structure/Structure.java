package datastructures.structure;
import mathlibrary.number.ArbitraryFloat;
import mathlibrary.number.ArbitraryInteger;

/**
 * This stores structure data from a file.
 * @author Benjamin
 */
public class Structure{
	
	/**
	 * The type of this structure.
	 */
	protected StructureType type;
	
	/**
	 * The boolean variables of this structure.
	 */
	public boolean[][] booleanVals;
	
	/**
	 * The integer variables of this structure.
	 */
	public ArbitraryInteger[][] integerVals;
	
	/**
	 * The float variables of this structure.
	 */
	public ArbitraryFloat[][] floatVals;
	
	/**
	 * The character variables of this structure.
	 */
	public byte[][] characterVals;
	
	/**
	 * The structure variables of this structure.
	 */
	public Structure[][] structVals;
	
	/**
	 * This creates a structure of the given type with zeroed values.
	 * @param toInit The type of structure to create.
	 */
	public Structure(final StructureType toInit){
		type = toInit;
		
		final BooleanVariableDescription boolType = toInit.getBooleans();
		booleanVals = new boolean[boolType.getNumberOfBooleans()][0];
		
		final IntegerVariableDescription intType = toInit.getIntegers();
		integerVals = new ArbitraryInteger[intType.getNumberOfIntegers()][0];
		
		final FloatVariableDescription floatType = toInit.getFloats();
		floatVals = new ArbitraryFloat[floatType.getNumberOfFloats()][0];
		
		final CharacterVariableDescription charType = toInit.getCharacters();
		characterVals = new byte[charType.getNumberOfStrings()][0];
		
		final StructureVariableDescription structType = toInit.getStructures();
		structVals = new Structure[structType.getNumberOfStructures()][0];
	}
	
	/**
	 * This returns the type of this structure.
	 * @return The type of this structure.
	 */
	public StructureType getType(){
		return type;
	}
}