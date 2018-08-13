package datastructures.primitives;
import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;

/*GENERATED CODE, DO NOT MODIFY*/

/**
 * Local implementation.
 * An optimized version of AbstractList.Itr
 */
class ArrayListIteratorDouble implements IteratorDouble {
	
	/**
	 * The index of the next element to return.
	 */
    protected int cursor;
    
    /**
     * index of last element returned; -1 if no such
     */
    protected int lastRet = -1;
    
    /**
     * The expected structural modification count.
     */
    protected int expectedModCount;
    
    /**
     * The array list being iterated through.
     */
    protected final ArrayListDouble handle;
    
    /**
     * This creates an iterator through the given list.
     * @param handle The array list to iterate through.
     */
    public ArrayListIteratorDouble(final ArrayListDouble handle){
    	this.handle = handle;
    	expectedModCount = handle.modCount;
    }

    public boolean hasNext() {
        return cursor != handle.mlsize;
    }

    
    public Double next() {
        checkForComodification();
        final int index = cursor;
        if (index >= handle.mlsize){
            throw new NoSuchElementException();
        }
        final double[] elementData = handle.elementDataArray;
        if (index >= elementData.length){
            throw new ConcurrentModificationException();
        }
        cursor = index + 1;
        return elementData[lastRet = index];
    }
    
    public double nextPrimitive() {
        checkForComodification();
        final int index = cursor;
        if (index >= handle.mlsize){
            throw new NoSuchElementException();
        }
        final double[] elementData = handle.elementDataArray;
        if (index >= elementData.length){
            throw new ConcurrentModificationException();
        }
        cursor = index + 1;
        return elementData[lastRet = index];
    }

    public void remove() {
        if (lastRet < ArrayListDouble.FIRSTELEMENT){
            throw new IllegalStateException();
        }
        checkForComodification();

        try {
            handle.removeAt(lastRet);
            cursor = lastRet;
            lastRet = -1;
            expectedModCount = handle.modCount;
        } catch (IndexOutOfBoundsException ex) {
            throw new ConcurrentModificationException(ex);
        }
    }
	
	/**
	 * This will check to see if the underlying array has been structurally modified.
	 */
    final protected void checkForComodification() {
        if (handle.modCount != expectedModCount){
            throw new ConcurrentModificationException();
        }
    }
}