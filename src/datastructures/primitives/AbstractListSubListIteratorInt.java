package datastructures.primitives;
import java.util.NoSuchElementException;

/*GENERATED CODE, DO NOT MODIFY*/

/**
 * Local implementation.
 * This will iterate through a sub list, by limiting a full list iterator.
 */
class AbstractListSubListIteratorInt implements ListIteratorInt{
	private final ListIteratorInt iter;
	
	private final AbstractListSubListInt handler;
	
	/**
	 * This will create an iterator through a sub list.
	 * @param index The index to start at.
	 * @param handler The sub list being iterated through.
	 */
	public AbstractListSubListIteratorInt(final int index, final AbstractListSubListInt handler){
		this.handler = handler;
		iter = handler.wrapped.listIterator(index+handler.offset);
	}

    public boolean hasNext() {
        return nextIndex() < handler.cursize;
    }

    public Integer next() {
        if (hasNext()){
            return iter.next();
        }
        else{
            throw new NoSuchElementException();
        }
    }
    
    public int nextPrimitive() {
        if (hasNext()){
            return iter.next();
        }
        else{
            throw new NoSuchElementException();
        }
    }

    public boolean hasPrevious() {
        return previousIndex() >= 0;
    }

    public Integer previous() {
        if (hasPrevious()){
            return iter.previous();
        }
        else{
            throw new NoSuchElementException();
        }
    }
    
    public int previousPrimitive() {
        if (hasPrevious()){
            return iter.previous();
        }
        else
        {
            throw new NoSuchElementException();
        }
    }

    public int nextIndex() {
        return iter.nextIndex() - handler.offset;
    }

    public int previousIndex() {
        return iter.previousIndex() - handler.offset;
    }

    public void remove() {
        iter.remove();
        handler.modCount = handler.wrapped.modCount;
        handler.cursize--;
    }

    public void set(final int element) {
        iter.set(element);
    }

    public void add(final int element) {
        iter.add(element);
        handler.modCount = handler.wrapped.modCount;
        handler.cursize++;
    }
}