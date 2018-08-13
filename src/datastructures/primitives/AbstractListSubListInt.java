package datastructures.primitives;
import java.util.ConcurrentModificationException;

/*GENERATED CODE, DO NOT MODIFY*/

/**
 * Local implementation.
 * This will create a view of an abstract list.
 */
class AbstractListSubListInt extends AbstractListInt {
	
	/**
	 * The list being limited.
	 */
    protected final AbstractListInt wrapped;
    
    /**
     * The offset of this sub list in the wrapped list.
     */
    protected final int offset;
    
    /**
     * The size of this sub list.
     */
    protected int cursize;
	
	/**
	 * This creates a view of an AbstractList.
	 * @param list The list to wrap.
	 * @param fromIndex The first element in the view.
	 * @param toIndex The element after the last element in the view.
	 */
    AbstractListSubListInt(final AbstractListInt list, final int fromIndex, final int toIndex) {
    	super();
    	
        if (fromIndex < FIRSTELEMENT){
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
        }
        if (toIndex > list.size()){
            throw new IndexOutOfBoundsException("toIndex = " + toIndex);
        }
        if (fromIndex > toIndex){
            throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
        }
        
        wrapped = list;
        offset = fromIndex;
        cursize = toIndex - fromIndex;
        this.modCount = wrapped.modCount;
    }

    public int set(final int index, final int element) {
        rangeCheck(index);
        checkForComodification();
        return wrapped.set(index+offset, element);
    }

    public int get(final int index) {
        rangeCheck(index);
        checkForComodification();
        return wrapped.get(index+offset);
    }

    public int size() {
        checkForComodification();
        return cursize;
    }

    public void add(final int index, final int element) {
        rangeCheckForAdd(index);
        checkForComodification();
        wrapped.add(index+offset, element);
        this.modCount = wrapped.modCount;
        cursize++;
    }

    public int removeAt(final int index) {
        rangeCheck(index);
        checkForComodification();
        final int result = wrapped.removeAt(index+offset);
        this.modCount = wrapped.modCount;
        cursize--;
        return result;
    }

    protected void removeRange(final int fromIndex, final int toIndex) {
        checkForComodification();
        wrapped.removeRange(fromIndex+offset, toIndex+offset);
        this.modCount = wrapped.modCount;
        cursize -= toIndex-fromIndex;
    }

    public boolean addAll(final CollectionInt collection) {
        return addAll(cursize, collection);
    }

    public boolean addAll(final int index, final CollectionInt collection) {
        rangeCheckForAdd(index);
        final int cSize = collection.size();
        if (cSize==EMPTYLISTSIZE){
            return false;
        }

        checkForComodification();
        wrapped.addAll(offset+index, collection);
        this.modCount = wrapped.modCount;
        cursize += cSize;
        return true;
    }

    public IteratorInt iterator() {
        return listIterator();
    }

    public ListIteratorInt listIterator(final int index) {
        checkForComodification();
        rangeCheckForAdd(index);

        return new AbstractListSubListIteratorInt(index, this);
    }

    public ListInt subList(final int fromIndex, final int toIndex) {
        return new AbstractListSubListInt(this, fromIndex, toIndex);
    }

    private void rangeCheck(final int index) {
        if (index < 0 || index >= cursize){
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
        }
    }

    private void rangeCheckForAdd(final int index) {
        if (index < 0 || index > cursize){
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
        }
    }

    private String outOfBoundsMsg(final int index) {
        return "Index: "+index+", Size: "+cursize;
    }

    private void checkForComodification() {
        if (this.modCount != wrapped.modCount){
            throw new ConcurrentModificationException();
        }
    }
}