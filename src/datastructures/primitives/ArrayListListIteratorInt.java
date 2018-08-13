package datastructures.primitives;
import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;

/*GENERATED CODE, DO NOT MODIFY*/

/**
 * Local implementation.
 * An optimized version of AbstractList.ListItr
 */
class ArrayListListIteratorInt extends ArrayListIteratorInt implements ListIteratorInt {

	/**
	 * This builds a list iterator.
	 * @param index The first element the iterator should return.
	 * @param handle The array list being iterated through.
	 */
    ArrayListListIteratorInt(final int index, final ArrayListInt handle) {
        super(handle);
        cursor = index;
    }

    public boolean hasPrevious() {
        return cursor != 0;
    }

    public int nextIndex() {
        return cursor;
    }

    public int previousIndex() {
        return cursor - 1;
    }

    
    public Integer previous() {
        checkForComodification();
        final int index = cursor - 1;
        if (index < ArrayListInt.FIRSTELEMENT){
            throw new NoSuchElementException();
        }
        final int[] elementData = handle.elementDataArray;
        if (index >= elementData.length){
            throw new ConcurrentModificationException();
        }
        cursor = index;
        return elementData[lastRet = index];
    }
    
    public int previousPrimitive() {
        checkForComodification();
        final int index = cursor - 1;
        if (index < ArrayListInt.FIRSTELEMENT){
            throw new NoSuchElementException();
        }
        final int[] elementData = handle.elementDataArray;
        if (index >= elementData.length){
            throw new ConcurrentModificationException();
        }
        cursor = index;
        return elementData[lastRet = index];
    }

    public void set(final int element) {
        if (lastRet < ArrayListInt.FIRSTELEMENT){
            throw new IllegalStateException();
        }
        checkForComodification();

        try {
            handle.set(lastRet, element);
        } catch (IndexOutOfBoundsException ex) {
            throw new ConcurrentModificationException(ex);
        }
    }

    public void add(final int element) {
        checkForComodification();

        try {
            final int index = cursor;
            handle.add(index, element);
            cursor = index + 1;
            lastRet = -1;
            expectedModCount = handle.modCount;
        } catch (IndexOutOfBoundsException ex) {
            throw new ConcurrentModificationException(ex);
        }
    }
}