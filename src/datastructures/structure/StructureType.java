package datastructures.structure;
import java.util.Arrays;
import markers.Immutable;

/**
 * This stores description of a structures contents.
 * @author Benjamin
 */
public class StructureType implements Immutable{
	
	/**
	 * The type that has nothing in it.
	 */
	public static final StructureType EMPTYTYPE;
	
	/**
	 * The name of this type.
	 */
	protected String name;
	
	/**
	 * The boolean variables of this type.
	 */
	protected BooleanVariableDescription booleanVars;
	
	/**
	 * The integer variables of this type.
	 */
	protected IntegerVariableDescription integerVars;

	/**
	 * The float variables of this type.
	 */
	protected FloatVariableDescription floatVars;

	/**
	 * The character variables of this type.
	 */
	protected CharacterVariableDescription stringVars;

	/**
	 * The structure variables of this type.
	 */
	protected StructureVariableDescription structVars;

	/**
	 * The tags this type has.
	 */
	protected String[] tags;
	
	static{
		EMPTYTYPE = new StructureType("NULL", new BooleanVariableDescription(new String[0], new int[0]), new IntegerVariableDescription(new String[0], new int[0], new int[0]), new FloatVariableDescription(new String[0], new int[0], new int[0], new int[0]), new CharacterVariableDescription(new String[0], new int[0]), new StructureVariableDescription(new String[0], new int[0], new String[0]), new String[0]);
	}
	
	/**
	 * This creates a structure type description.
	 * @param name The name of this type.
	 * @param booleanVars The boolean variables of this type.
	 * @param integerVars The integer variables of this type.
	 * @param floatVars The float variables of this type.
	 * @param characterVars The character variables of this type.
	 * @param structureVars The structure variables of this type.
	 * @param tags The tags associated with this type.
	 */
	public StructureType(final String name, final BooleanVariableDescription booleanVars, final IntegerVariableDescription integerVars, final FloatVariableDescription floatVars, final CharacterVariableDescription characterVars, final StructureVariableDescription structureVars, final String[] tags){
		this.name = name;
		this.booleanVars = booleanVars;
		this.integerVars = integerVars;
		this.floatVars = floatVars;
		this.stringVars = characterVars;
		this.structVars = structureVars;
		this.tags = Arrays.copyOf(tags, tags.length);
	}
	
	@Override
	public boolean equals(final Object toTest){
		if(toTest instanceof StructureType){
			final StructureType test = (StructureType) toTest;
			return name.equals(test.name) && Arrays.equals(tags, test.tags) && booleanVars.equals(test.booleanVars) && integerVars.equals(test.integerVars) && floatVars.equals(test.floatVars) && stringVars.equals(test.stringVars) && structVars.equals(test.structVars);
		}
		else{
			return false;
		}
	}
	
	@Override
	public int hashCode(){
		return name.hashCode() + tags.hashCode() + booleanVars.hashCode() + integerVars.hashCode() + floatVars.hashCode() + stringVars.hashCode() + structVars.hashCode();
	}
	
	/**
	 * This will return the name of this type.
	 * @return The name of this type.
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * This returns the boolean variables of this type.
	 * @return The boolean variables of this type.
	 */
	public BooleanVariableDescription getBooleans(){
		return booleanVars;
	}
	
	/**
	 * This returns the integer variables of this type.
	 * @return The integer variables of this type.
	 */
	public IntegerVariableDescription getIntegers(){
		return integerVars;
	}

	/**
	 * This returns the float variables of this type.
	 * @return The float variables of this type.
	 */
	public FloatVariableDescription getFloats(){
		return floatVars;
	}

	/**
	 * This returns the string variables of this type.
	 * @return The string variables of this type.
	 */
	public CharacterVariableDescription getCharacters(){
		return stringVars;
	}

	/**
	 * This returns the structure variables of this type.
	 * @return The structure variables of this type.
	 */
	public StructureVariableDescription getStructures(){
		return structVars;
	}
	
	/**
	 * Returns the number of tags this type has.
	 * @return The number of tags this type has.
	 */
	public int numberOfTags(){
		return tags.length;
	}
	
	/**
	 * This returns one of this type's tags.
	 * @param index The tag to get.
	 * @return The requested tag.
	 */
	public String getTag(final int index){
		return tags[index];
	}
}