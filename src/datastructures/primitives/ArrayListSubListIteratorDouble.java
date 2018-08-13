package datastructures.primitives;
import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;

/*GENERATED CODE, DO NOT MODIFY*/

/**
 * Local implementation.
 * This will iterate through the view.
 */
class ArrayListSubListIteratorDouble implements ListIteratorDouble{
	
	/**
     * The index of the first element.
     */
    protected static final int FIRSTELEMENT = 0;
	
	private int cursor;
    private int lastRet;
    private int expectedModCount;
    private final int offset;
    private final ArrayListDouble handler;
    private final ArrayListSubListDouble subList;
    
    /**
     * This creates an iterator throught the view.
     * @param index The starting index in the view.
     * @param offset The offset in the underlying array for indices.
     * @param handler The underlying array list.
     * @param subList The view being iterated through.
     */
    public ArrayListSubListIteratorDouble(final int index, final int offset, final ArrayListDouble handler, final ArrayListSubListDouble subList){
    	cursor = index;
    	lastRet = -1;
    	expectedModCount = handler.modCount;
    	this.offset = offset;
    	this.handler = handler;
    	this.subList = subList;
    }

    public boolean hasNext() {
        return cursor != subList.cursize;
    }

    
    public Double next() {
        checkForComodification();
        final int index = cursor;
        if (index >= subList.cursize){
            throw new NoSuchElementException();
        }
        final double[] elementData = handler.elementDataArray;
        if (offset + index >= elementData.length){
            throw new ConcurrentModificationException();
        }
        cursor = index + 1;
        return elementData[offset + (lastRet = index)];
    }
    
    public double nextPrimitive() {
        checkForComodification();
        final int index = cursor;
        if (index >= subList.cursize){
            throw new NoSuchElementException();
        }
        final double[] elementData = handler.elementDataArray;
        if (offset + index >= elementData.length){
            throw new ConcurrentModificationException();
        }
        cursor = index + 1;
        return elementData[offset + (lastRet = index)];
    }

    public boolean hasPrevious() {
        return cursor != 0;
    }

    
    public Double previous() {
        checkForComodification();
        final int index = cursor - 1;
        if (index < FIRSTELEMENT){
            throw new NoSuchElementException();
        }
        final double[] elementData = handler.elementDataArray;
        if (offset + index >= elementData.length){
            throw new ConcurrentModificationException();
        }
        cursor = index;
        return elementData[offset + (lastRet = index)];
    }
    
    public double previousPrimitive() {
        checkForComodification();
        final int index = cursor - 1;
        if (index < FIRSTELEMENT){
            throw new NoSuchElementException();
        }
        final double[] elementData = handler.elementDataArray;
        if (offset + index >= elementData.length){
            throw new ConcurrentModificationException();
        }
        cursor = index;
        return elementData[offset + (lastRet = index)];
    }

    public int nextIndex() {
        return cursor;
    }

    public int previousIndex() {
        return cursor - 1;
    }

    public void remove() {
        if (lastRet < FIRSTELEMENT){
            throw new IllegalStateException();
        }
        checkForComodification();

        try {
            subList.removeAt(lastRet);
            cursor = lastRet;
            lastRet = -1;
            expectedModCount = handler.modCount;
        } catch (IndexOutOfBoundsException ex) {
            throw new ConcurrentModificationException(ex);
        }
    }

    public void set(final double element) {
        if (lastRet < FIRSTELEMENT){
            throw new IllegalStateException();
        }
        checkForComodification();

        try {
            handler.set(offset + lastRet, element);
        } catch (IndexOutOfBoundsException ex) {
            throw new ConcurrentModificationException(ex);
        }
    }

    public void add(final double element) {
        checkForComodification();

        try {
            final int index = cursor;
            subList.add(index, element);
            cursor = index + 1;
            lastRet = -1;
            expectedModCount = handler.modCount;
        } catch (IndexOutOfBoundsException ex) {
            throw new ConcurrentModificationException(ex);
        }
    }

    final private void checkForComodification() {
        if (expectedModCount != handler.modCount){
            throw new ConcurrentModificationException();
        }
    }
}