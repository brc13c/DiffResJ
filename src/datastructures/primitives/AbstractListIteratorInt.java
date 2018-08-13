package datastructures.primitives;
import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;

/*GENERATED CODE, DO NOT MODIFY*/

/**
 * Local implementation.
 * An iterator for abstract lists.
 */
@SuppressWarnings("PMD.PreserveStackTrace")
class AbstractListIteratorInt implements IteratorInt {
	
    /**
     * Index of element to be returned by subsequent call to next.
     */
    protected int cursor = 0;

    /**
     * Index of element returned by most recent call to next or
     * previous.  Reset to -1 if this element is deleted by a call
     * to remove.
     */
    protected int lastRet = -1;

    /**
     * The modCount value that the iterator believes that the backing
     * List should have.  If this expectation is violated, the iterator
     * has detected concurrent modification.
     */
    protected int expectedModCount;
    
    /**
     * The list being iterated through.
     */
    protected final AbstractListInt handler;
    
    /**
     * This builds an iterator for an abstract list.
     * @param handler The list being iterated through.
     */
    public AbstractListIteratorInt(final AbstractListInt handler){
    	this.handler = handler;
    	expectedModCount = handler.modCount;
    }

    public boolean hasNext() {
        return cursor != handler.size();
    }
	
    public Integer next() {
        checkForComodification();
        try {
            final int index = cursor;
            final Integer next = handler.get(index);
            lastRet = index;
            cursor = index + 1;
            return next;
        } catch (IndexOutOfBoundsException e) {
            checkForComodification();
            throw new NoSuchElementException();
        }
    }
    
    public int nextPrimitive() {
        checkForComodification();
        try {
            final int index = cursor;
            final int next = handler.get(index);
            lastRet = index;
            cursor = index + 1;
            return next;
        } catch (IndexOutOfBoundsException e) {
            checkForComodification();
            throw new NoSuchElementException();
        }
    }

    public void remove() {
        if (lastRet < AbstractListInt.FIRSTELEMENT){
            throw new IllegalStateException();
        }
        checkForComodification();

        try {
            handler.removeAt(lastRet);
            if (lastRet < cursor){
                cursor--;
            }
            lastRet = -1;
            expectedModCount = handler.modCount;
        } catch (IndexOutOfBoundsException e) {
            throw new ConcurrentModificationException(e);
        }
    }
	
	/**
	 * This will attempt to see if the list is being modified by another thread.
	 */
    final protected void checkForComodification() {
        if (handler.modCount != expectedModCount){
            throw new ConcurrentModificationException();
        }
    }
}