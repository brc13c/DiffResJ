package datastructures.structure;
import java.util.Arrays;
import markers.Immutable;

/**
 * This describes the float variables in a type.
 * @author Benjamin
 *
 */
public class FloatVariableDescription implements Immutable{
	
	/**
	 * The names of the variables.
	 */
	protected String[] names;
	
	/**
	 * The bit depths of the variable's mantissas (-1 if specified in a preceeding 32 bit int).
	 */
	protected int[] mantissaDepths;
	
	/**
	 * The bit depths of the variable's exponents (-1 if specified in a preceeding 32 bit int).
	 */
	protected int[] exponentDepths;
	
	/**
	 * The lengths of the arrays of the variables (-1 if specified in a preceeding 16 bit unsigned int).
	 */
	protected int[] lengths;
	
	/**
	 * This sets up the description of integer variable types.
	 * @param names The names of the variables.
	 * @param mantissaDepths The bit depths of the variable's mantissas (-1 if specified in a preceeding 32 bit int).
	 * @param exponentDepths The bit depths of the variable's exponents (-1 if specified in a preceeding 32 bit int).
	 * @param lengths The lengths of the arrays of the variables (-1 if specified in a preceeding 16 bit unsigned int).
	 * @throws IllegalArgumentException If any array is a different length, or if any depth is not a multiple of 8 (or negative).
	 */
	public FloatVariableDescription(final String[] names, final int[] mantissaDepths, final int[] exponentDepths, final int[] lengths){
		//check that descriptions match names
		if(names.length != mantissaDepths.length || names.length != lengths.length || names.length != exponentDepths.length){
			throw new IllegalArgumentException("Data must match names.");
		}
		//check that all positive depths are multiples of 8
		for(int i = 0; i<mantissaDepths.length; i++){
			if(mantissaDepths[i]>=0 && exponentDepths[i]>=0 && (mantissaDepths[i] + exponentDepths[i] + 1)%8 != 0){
				throw new IllegalArgumentException("Float bit depths must be multiples of eight.");
			}
		}
		//copy
		this.names = Arrays.copyOf(names, names.length);
		this.mantissaDepths = Arrays.copyOf(mantissaDepths, mantissaDepths.length);
		this.exponentDepths = Arrays.copyOf(exponentDepths, exponentDepths.length);
		this.lengths = Arrays.copyOf(lengths, lengths.length);
	}
	
	@Override
	public boolean equals(final Object toTest){
		if(toTest instanceof FloatVariableDescription){
			final FloatVariableDescription test = (FloatVariableDescription)toTest;
			return Arrays.equals(names, test.names) && Arrays.equals(mantissaDepths, test.mantissaDepths) && Arrays.equals(exponentDepths, test.exponentDepths) && Arrays.equals(lengths, test.lengths);
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
		for(final int cur : mantissaDepths){
			hash += cur;
		}
		for(final int cur : exponentDepths){
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
	public int getFloatVariableIndex(final String name){
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
	public String getFloatVariableName(final int index){
		return names[index];
	}
	
	/**
	 * Returns the bit depth of a variable's mantissa given its index.
	 * @param index The variable to get the bit depth of.
	 * @return The bit depth of the variable.
	 */
	public int getFloatVariableMantissaDepth(final int index){
		return mantissaDepths[index];
	}
	
	/**
	 * Returns the bit depth of a variable's exponent given its index.
	 * @param index The variable to get the bit depth of.
	 * @return The bit depth of the variable.
	 */
	public int getFloatVariableExponentDepth(final int index){
		return exponentDepths[index];
	}
	
	/**
	 * Returns the length of a variable given its index.
	 * @param index The variable to get the length of.
	 * @return The length of the variable.
	 */
	public int getFloatVariableLength(final int index){
		return lengths[index];
	}
	
	/**
	 * This returns the number of integer variables present.
	 * @return The number of integer variables present.
	 */
	public int getNumberOfFloats(){
		return names.length;
	}
}