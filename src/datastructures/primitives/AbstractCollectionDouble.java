package datastructures.primitives;
import java.util.Arrays;

/*GENERATED CODE, DO NOT MODIFY*/

/**
 * This is an implementation of AbstractCollection for primitive types.
 * It is a modified form of AbstractCollection.
 *
 * This class provides a skeletal implementation of the <tt>CollectionDouble</tt>
 * interface, to minimize the effort required to implement this interface. <p>
 *
 * To implement an unmodifiable collection, the programmer needs only to
 * extend this class and provide implementations for the <tt>iterator</tt> and
 * <tt>size</tt> methods.  (The iterator returned by the <tt>iterator</tt>
 * method must implement <tt>hasNext</tt> and <tt>next</tt>.)<p>
 *
 * To implement a modifiable collection, the programmer must additionally
 * override this class's <tt>add</tt> method (which otherwise throws an
 * <tt>UnsupportedOperationException</tt>), and the iterator returned by the
 * <tt>iterator</tt> method must additionally implement its <tt>remove</tt>
 * method.<p>
 *
 * The programmer should generally provide a void (no argument) and
 * <tt>CollectionDouble</tt> constructor, as per the recommendation in the
 * <tt>CollectionDouble</tt> interface specification.<p>
 *
 * The documentation for each non-abstract method in this class describes its
 * implementation in detail.  Each of these methods may be overridden if
 * the collection being implemented admits a more efficient implementation.<p>
 *
 * This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @author  Josh Bloch
 * @author  Neal Gafter
 * @see CollectionDouble
 * @since 1.2
 *
 * @author Ben Crysup
 */

public abstract class AbstractCollectionDouble implements CollectionDouble {
	
	/**
     * The maximum size of array to allocate.
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    
    /**
     * The cutoff comparison value for maximum size overflow.
     */
    private static final int MAXSIZECUTOFF = 0;
    
    /**
     * The value that signals overflow.
     */
    private static final int OVERFLOWFLAGVALUE = 0;
	
    /**
     * Sole constructor.  (For invocation by subclass constructors, typically
     * implicit.)
     */
    protected AbstractCollectionDouble() {
    	//Placeholder
    }

    // Query Operations

    /**
     * Returns an iterator over the elements contained in this collection.
     *
     * @return an iterator over the elements contained in this collection
     */
    public abstract IteratorDouble iterator();

    public abstract int size();

    /**
     * {@inheritDoc}
     *
     * <p>This implementation returns <tt>size() == 0</tt>.
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation iterates over the elements in the collection,
     * checking each element in turn for equality with the specified element.
     *
     * @throws ClassCastException   {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    public boolean contains(final double object) {
        final IteratorDouble iter = iterator();
        while(iter.hasNext()){
        	if(object == iter.nextPrimitive()){
        		return true;
        	}
        }
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation returns an array containing all the elements
     * returned by this collection's iterator, in the same order, stored in
     * consecutive elements of the array, starting with index {@code 0}.
     * The length of the returned array is equal to the number of elements
     * returned by the iterator, even if the size of this collection changes
     * during iteration, as might happen if the collection permits
     * concurrent modification during iteration.  The {@code size} method is
     * called only as an optimization hint; the correct result is returned
     * even if the iterator returns a different number of elements.
     *
     * <p>This method is equivalent to:
     *
     *  <pre> {@code
     * ListDouble list = new ArrayListDouble(size());
     * for (double e : this)
     *     list.add(e);
     * return list.toArray();
     * }</pre>
     */
    public double[] toArray() {
        // Estimate size of array; be prepared to see more or fewer elements
        double[] arr = new double[size()];
        final IteratorDouble iter = iterator();
        for (int i = 0; i < arr.length; i++) {
            if (! iter.hasNext()){ // fewer elements than expected
                return Arrays.copyOf(arr, i);
            }
            arr[i] = iter.nextPrimitive();
        }
        return iter.hasNext() ? finishToArray(arr, iter) : arr;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation returns an array containing all the elements
     * returned by this collection's iterator in the same order, stored in
     * consecutive elements of the array, starting with index {@code 0}.
     * If the number of elements returned by the iterator is too large to
     * fit into the specified array, then the elements are returned in a
     * newly allocated array with length equal to the number of elements
     * returned by the iterator, even if the size of this collection
     * changes during iteration, as might happen if the collection permits
     * concurrent modification during iteration.  The {@code size} method is
     * called only as an optimization hint; the correct result is returned
     * even if the iterator returns a different number of elements.
     *
     * <p>This method is equivalent to:
     *
     *  <pre> {@code
     * ListDouble list = new ArrayListDouble(size());
     * for (double e : this)
     *     list.add(e);
     * return list.toArray(a);
     * }</pre>
     *
     * @throws ArrayStoreException  {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    public double[] toArray(final double[] store) {
        // Estimate size of array; be prepared to see more or fewer elements
        final int size = size();
        double[] arr = store.length >= size ? store : new double[size];
        final IteratorDouble iter = iterator();

        for (int i = 0; i < arr.length; i++) {
            if (! iter.hasNext()) { // fewer elements than expected
                if (store != arr){
                    return Arrays.copyOf(arr, i);
                }
                return arr;
            }
            arr[i] = iter.nextPrimitive();
        }
        return iter.hasNext() ? finishToArray(arr, iter) : arr;
    }

    /**
     * Reallocates the array being used within toArray when the iterator
     * returned more elements than expected, and finishes filling it from
     * the iterator.
     *
     * @param toFinish the array, replete with previously stored elements
     * @param iter the in-progress iterator over this collection
     * @return array containing the elements in the given array, plus any
     *         further elements returned by the iterator, trimmed to size
     */
    private static double[] finishToArray(final double[] toFinish, final IteratorDouble iter) {
    	double[] arr = toFinish;
        int index = arr.length;
        while (iter.hasNext()) {
            final int cap = arr.length;
            if (index == cap) {
                int newCap = cap + (cap >> 1) + 1;
                // overflow-conscious code
                if (newCap - MAX_ARRAY_SIZE > MAXSIZECUTOFF){
                    newCap = hugeCapacity(cap + 1);
                }
                arr = Arrays.copyOf(arr, newCap);
            }
            arr[index++] = iter.nextPrimitive();
        }
        // trim if overallocated
        return (index == arr.length) ? arr : Arrays.copyOf(arr, index);
    }

    private static int hugeCapacity(final int minCapacity) {
        if (minCapacity < OVERFLOWFLAGVALUE){ // overflow
            throw new OutOfMemoryError("Required array size too large");
        }
        return minCapacity > MAX_ARRAY_SIZE ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
    }

    // Modification Operations

    /**
     * {@inheritDoc}
     *
     * <p>This implementation always throws an
     * <tt>UnsupportedOperationException</tt>.
     *
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     * @throws IllegalArgumentException      {@inheritDoc}
     * @throws IllegalStateException         {@inheritDoc}
     */
    public boolean add(final double element) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation iterates over the collection looking for the
     * specified element.  If it finds the element, it removes the element
     * from the collection using the iterator's remove method.
     *
     * <p>Note that this implementation throws an
     * <tt>UnsupportedOperationException</tt> if the iterator returned by this
     * collection's iterator method does not implement the <tt>remove</tt>
     * method and this collection contains the specified object.
     *
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     */
    public boolean remove(final double object) {
        final IteratorDouble iter = iterator();
        while(iter.hasNext()){
        	if(object==iter.next()){
        		iter.remove();
        		return true;
        	}
        }
        return false;
    }


    // Bulk Operations

    /**
     * {@inheritDoc}
     *
     * <p>This implementation iterates over the specified collection,
     * checking each element returned by the iterator in turn to see
     * if it's contained in this collection.  If all elements are so
     * contained <tt>true</tt> is returned, otherwise <tt>false</tt>.
     *
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     * @see #contains(double)
     */
    public boolean containsAll(final CollectionDouble collection) {
        for (final double element : collection){
            if (!contains(element)){
                return false;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation iterates over the specified collection, and adds
     * each object returned by the iterator to this collection, in turn.
     *
     * <p>Note that this implementation will throw an
     * <tt>UnsupportedOperationException</tt> unless <tt>add</tt> is
     * overridden (assuming the specified collection is non-empty).
     *
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     * @throws IllegalArgumentException      {@inheritDoc}
     * @throws IllegalStateException         {@inheritDoc}
     *
     * @see #add(double)
     */
    public boolean addAll(final CollectionDouble collection) {
        boolean modified = false;
        for (final double element : collection){
            if (add(element)){
                modified = true;
            }
        }
        return modified;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation iterates over this collection, checking each
     * element returned by the iterator in turn to see if it's contained
     * in the specified collection.  If it's so contained, it's removed from
     * this collection with the iterator's <tt>remove</tt> method.
     *
     * <p>Note that this implementation will throw an
     * <tt>UnsupportedOperationException</tt> if the iterator returned by the
     * <tt>iterator</tt> method does not implement the <tt>remove</tt> method
     * and this collection contains one or more elements in common with the
     * specified collection.
     *
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     *
     * @see #remove(double)
     * @see #contains(double)
     */
    public boolean removeAll(final CollectionDouble collection) {
        boolean modified = false;
        final IteratorDouble iter = iterator();
        while (iter.hasNext()) {
            if (collection.contains(iter.nextPrimitive())) {
                iter.remove();
                modified = true;
            }
        }
        return modified;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation iterates over this collection, checking each
     * element returned by the iterator in turn to see if it's contained
     * in the specified collection.  If it's not so contained, it's removed
     * from this collection with the iterator's <tt>remove</tt> method.
     *
     * <p>Note that this implementation will throw an
     * <tt>UnsupportedOperationException</tt> if the iterator returned by the
     * <tt>iterator</tt> method does not implement the <tt>remove</tt> method
     * and this collection contains one or more elements not present in the
     * specified collection.
     *
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     *
     * @see #remove(double)
     * @see #contains(double)
     */
    public boolean retainAll(final CollectionDouble collection) {
        boolean modified = false;
        final IteratorDouble iter = iterator();
        while (iter.hasNext()) {
            if (!collection.contains(iter.nextPrimitive())) {
                iter.remove();
                modified = true;
            }
        }
        return modified;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation iterates over this collection, removing each
     * element using the <tt>Iterator.remove</tt> operation.  Most
     * implementations will probably choose to override this method for
     * efficiency.
     *
     * <p>Note that this implementation will throw an
     * <tt>UnsupportedOperationException</tt> if the iterator returned by this
     * collection's <tt>iterator</tt> method does not implement the
     * <tt>remove</tt> method and this collection is non-empty.
     *
     * @throws UnsupportedOperationException {@inheritDoc}
     */
    public void clear() {
        final IteratorDouble iter = iterator();
        while (iter.hasNext()) {
            iter.nextPrimitive();
            iter.remove();
        }
    }


    //  String conversion

    /**
     * Returns a string representation of this collection.  The string
     * representation consists of a list of the collection's elements in the
     * order they are returned by its iterator, enclosed in square brackets
     * (<tt>"[]"</tt>).  Adjacent elements are separated by the characters
     * <tt>", "</tt> (comma and space).  Elements are converted to strings as
     * by {@link String#valueOf(Object)}.
     *
     * @return a string representation of this collection
     */
    public String toString() {
        final IteratorDouble iter = iterator();
        if (! iter.hasNext()){
            return "[]";
		}
        final StringBuilder toBuild = new StringBuilder();
        toBuild.append('[');
        for (;;) {
            final double toAdd = iter.nextPrimitive();
            toBuild.append(toAdd);
            if (! iter.hasNext()){
                return toBuild.append(']').toString();
            }
            toBuild.append(", ");
        }
    }

}
