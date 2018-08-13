package datastructures.structure;
import java.util.Arrays;
import markers.Immutable;

/**
 * This describes the boolean variables in a type.
 * @author Benjamin
 *
 */
public class BooleanVariableDescription implements Immutable{
	
	/**
	 * The names of the variables.
	 */
	protected String[] names;
	
	/**
	 * The lengths of the arrays of the variables (-1 if specified in a preceeding 16 bit unsigned int).
	 */
	protected int[] lengths;
	
	/**
	 * This sets up the description of integer variable types.
	 * @param names The names of the variables.
	 * @param lengths The lengths of the arrays of the variables (-1 if specified in a preceeding 16 bit unsigned int).
	 * @throws IllegalArgumentException If any array is a different length.
	 */
	public BooleanVariableDescription(final String[] names, final int[] lengths){
		//check that descriptions match names
		if(names.length != lengths.length){
			throw new IllegalArgumentException("Data must match names.");
		}
		//copy
		this.names = Arrays.copyOf(names, names.length);
		this.lengths = Arrays.copyOf(lengths, lengths.length);
	}
	
	@Override
	public boolean equals(final Object toTest){
		if(toTest instanceof BooleanVariableDescription){
			final BooleanVariableDescription test = (BooleanVariableDescription)toTest;
			return Arrays.equals(names, test.names) && Arrays.equals(lengths, test.lengths);
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
	public int getBooleanVariableIndex(final String name){
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
	public String getBooleanVariableName(final int index){
		return names[index];
	}
	
	/**
	 * Returns the length of a variable given its index.
	 * @param index The variable to get the length of.
	 * @return The length of the variable.
	 */
	public int getBooleanVariableLength(final int index){
		return lengths[index];
	}
	
	/**
	 * This returns the number of boolean variables present.
	 * @return The number of boolean variables present.
	 */
	public int getNumberOfBooleans(){
		return names.length;
	}
}