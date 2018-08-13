package datastructures.array.listwrap;
import java.util.Iterator;

/**
 * This will iterate through an array.
 * @author Benjamin
 *
 * @param <T> The type of element in the array.
 */
public class ArrayIterator<T> implements Iterator<T>{
	
	/**
	 * The array to iterate through.
	 */
	protected T[] target;
	
	/**
	 * The next element in the array.
	 */
	protected int curIndex;
	
	/**
	 * This will set up an iterator through the given array.
	 * @param target The array to iterate through.
	 */
	//Don't want to copy, just want to step through
	@SuppressWarnings("PMD.ArrayIsStoredDirectly")
	public ArrayIterator(final T[] target){
		super();
		this.target = target;
		curIndex = 0;
	}
	
	@Override
	public boolean hasNext() {
		return curIndex < target.length;
	}
	
	@Override
	public T next() {
		final T toRet = target[curIndex];
		curIndex++;
		return toRet;
	}
	
	@Override
	public void remove(){
		throw new UnsupportedOperationException("Arrays are fixed-length.");
	}
}