package datastructures.structure;
import java.util.Arrays;
import markers.Immutable;

/**
 * This describes the integer variables in a type.
 * @author Benjamin
 *
 */
public class IntegerVariableDescription implements Immutable{
	
	/**
	 * The names of the variables.
	 */
	protected String[] names;
	
	/**
	 * The bit depths of the variables (-1 if specified in a preceeding 32 bit int).
	 */
	protected int[] depths;
	
	/**
	 * The lengths of the arrays of the variables (-1 if specified in a preceeding 16 bit unsigned int).
	 */
	protected int[] lengths;
	
	/**
	 * This sets up the description of integer variable types.
	 * @param names The names of the variables.
	 * @param depths The bit depths of the variables (-1 if specified in a preceeding 32 bit int).
	 * @param lengths The lengths of the arrays of the variables (-1 if specified in a preceeding 16 bit unsigned int).
	 * @throws IllegalArgumentException If any array is a different length, or if any depth is not a multiple of 8 (or negative).
	 */
	public IntegerVariableDescription(final String[] names, final int[] depths, final int[] lengths){
		//check that descriptions match names
		if(names.length != depths.length || names.length != lengths.length){
			throw new IllegalArgumentException("Data must match names.");
		}
		//check that all positive depths are multiples of 8
		for(int i = 0; i<depths.length; i++){
			if(depths[i]>=0 && depths[i]%8 != 0){
				throw new IllegalArgumentException("Integer bit depths must be multiples of eight.");
			}
		}
		//copy
		this.names = Arrays.copyOf(names, names.length);
		this.depths = Arrays.copyOf(depths, depths.length);
		this.lengths = Arrays.copyOf(lengths, lengths.length);
	}
	
	@Override
	public boolean equals(final Object toTest){
		if(toTest instanceof IntegerVariableDescription){
			final IntegerVariableDescription test = (IntegerVariableDescription)toTest;
			return Arrays.equals(names, test.names) && Arrays.equals(depths, test.depths) && Arrays.equals(lengths, test.lengths);
		}
		else{
			return false;
		}
	}
	
	@Override
	public int hashCode(){
		int hash = 0;
		for(final String cur : names){
			hash += cur.hashCode();
		}
		for(final int cur : depths){
			hash += cur;
		}
		for(final int cur : lengths){
			hash += cur;
		}
		return hash;
	}
	
	/**
	 * This finds the index associated with the given name.
	 * @param name The name of the variable to find.
	 * @return The index of the variable, of -1 if it's not found.
	 */
	public int getIntegerVariableIndex(final String name){
		for(int i = 0; i<names.length; i++){
			if(names[i].equals(name)){
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Returns the name of a variable given its index.
	 * @param index The variable to get the name of.
	 * @return The name of the variable.
	 */
	public String getIntegerVariableName(final int index){
		return names[index];
	}
	
	/**
	 * Returns the bit depth of a variable given its index.
	 * @param index The variable to get the bit depth of.
	 * @return The bit depth of the variable.
	 */
	public int getIntegerVariableDepth(final int index){
		return depths[index];
	}
	
	/**
	 * Returns the length of a variable given its index.
	 * @param index The variable to get the length of.
	 * @return The length of the variable.
	 */
	public int getIntegerVariableLength(final int index){
		return lengths[index];
	}
	
	/**
	 * This returns the number of integer variables present.
	 * @return The number of integer variables present.
	 */
	public int getNumberOfIntegers(){
		return names.length;
	}
}