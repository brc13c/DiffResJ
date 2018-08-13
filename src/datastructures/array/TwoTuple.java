package datastructures.array;

/**
 * This class holds two values.
 * @author Benjamin
 *
 * @param <A> The type of the first held value.
 * @param <B> The type of the second held value.
 */
public class TwoTuple<A,B>{
	
	/**
	 * The first held value.
	 */
	protected A value1;
	
	/**
	 * The second held value.
	 */
	protected B value2;
	
	/**
	 * This method returns the first held value.
	 * @return The first held value.
	 */
	public A getValue1(){
		return value1;
	}
	
	/**
	 * This method returns the second held value.
	 * @return The second held value.
	 */
	public B getValue2(){
		return value2;
	}
	
	/**
	 * This constructor sets up a tuple holding two values.
	 * @param value1 The first value.
	 * @param value2 The second value.
	 */
	public TwoTuple(final A value1, final B value2){
		this.value1 = value1;
		this.value2 = value2;
	}
	
	@Override
	public boolean equals(final Object arg0) {
		if(arg0 instanceof TwoTuple){
			final TwoTuple<?, ?> possible = (TwoTuple<?, ?>)arg0;
			return value1.equals(possible.value1) && value2.equals(possible.value2);
		}
		else{
			return false;
		}
	};
	
	@Override
	public int hashCode() {
		return value1.hashCode() + value2.hashCode();
	};
	
	/**
	 * Creates a combined representation of the two data values this holds.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return "(" + value1.toString() + "," + value2.toString() + ")";
	}
}