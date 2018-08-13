package datastructures.primitives;
import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;

/*GENERATED CODE, DO NOT MODIFY*/

/**
 * Local implementation.
 * A list iterator for abstract lists.
 */
 @SuppressWarnings("PMD.PreserveStackTrace")
class AbstractListListIteratorInt extends AbstractListIteratorInt implements ListIteratorInt {
	
	/**
     * This builds an iterator for an abstract list.
     * @param index The first element this iterator should return.
     * @param handler The list being iterated through.
     */
    AbstractListListIteratorInt(final int index, final AbstractListInt handler) {
    	super(handler);
        cursor = index;
    }

    public boolean hasPrevious() {
        return cursor != 0;
    }

    public Integer previous() {
        checkForComodification();
        try {
            final int index = cursor - 1;
            final Integer previous = handler.get(index);
            lastRet = cursor = index;
            return previous;
        } catch (IndexOutOfBoundsException e) {
            checkForComodification();
            throw new NoSuchElementException();
        }
    }
    
    public int previousPrimitive() {
        checkForComodification();
        try {
            final int index = cursor - 1;
            final int previous = handler.get(index);
            lastRet = cursor = index;
            return previous;
        } catch (IndexOutOfBoundsException e) {
            checkForComodification();
            throw new NoSuchElementException();
        }
    }

    public int nextIndex() {
        return cursor;
    }

    public int previousIndex() {
        return cursor-1;
    }

    public void set(final int element) {
        if (lastRet < AbstractListInt.FIRSTELEMENT){
            throw new IllegalStateException();
        }
        checkForComodification();

        try {
            handler.set(lastRet, element);
            expectedModCount = handler.modCount;
        } catch (IndexOutOfBoundsException ex) {
            throw new ConcurrentModificationException(ex);
        }
    }

    public void add(final int element) {
        checkForComodification();

        try {
            final int index = cursor;
            handler.add(index, element);
            lastRet = -1;
            cursor = index + 1;
            expectedModCount = handler.modCount;
        } catch (IndexOutOfBoundsException ex) {
            throw new ConcurrentModificationException(ex);
        }
    }
}