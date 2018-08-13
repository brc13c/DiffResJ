package datastructures.primitives;
import java.util.RandomAccess;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.io.IOException;

/*GENERATED CODE, DO NOT MODIFY*/

/**
 * This is an implementation of ArrayList for primitive types.
 * It is a modified form of ArrayList.
 *
 * <BR><BR>Resizable-array implementation of the <tt>List</tt> interface.  Implements
 * all optional list operations.
 * In addition to implementing the <tt>List</tt> interface,
 * this class provides methods to manipulate the size of the array that is
 * used internally to store the list.
 *
 * <BR><BR>The <tt>size</tt>, <tt>isEmpty</tt>, <tt>get</tt>, <tt>set</tt>,
 * <tt>iterator</tt>, and <tt>listIterator</tt> operations run in constant
 * time.  The <tt>add</tt> operation runs in <i>amortized constant time</i>,
 * that is, adding n elements requires O(n) time.  All of the other operations
 * run in linear time (roughly speaking).  The constant factor is low compared
 * to that for the <tt>LinkedList</tt> implementation.
 *
 * <BR><BR>Each <tt>ArrayList</tt> instance has a <i>capacity</i>.  The capacity is
 * the size of the array used to store the elements in the list.  It is always
 * at least as large as the list size.  As elements are added to an ArrayList,
 * its capacity grows automatically.  The details of the growth policy are not
 * specified beyond the fact that adding an element has constant amortized
 * time cost.
 *
 * <BR><BR>An application can increase the capacity of an <tt>ArrayList</tt> instance
 * before adding a large number of elements using the <tt>ensureCapacity</tt>
 * operation.  This may reduce the amount of incremental reallocation.
 *
 * <BR><BR><strong>Note that this implementation is not synchronized.</strong>
 * If multiple threads access an <tt>ArrayList</tt> instance concurrently,
 * and at least one of the threads modifies the list structurally, it
 * <i>must</i> be synchronized externally.  (A structural modification is
 * any operation that adds or deletes one or more elements, or explicitly
 * resizes the backing array; merely setting the value of an element is not
 * a structural modification.)  This is typically accomplished by
 * synchronizing on some object that naturally encapsulates the list.
 *
 * If no such object exists, the list should be "wrapped".
 * This is best done at creation time, to prevent accidental
 * unsynchronized access to the list.
 * <BR><BR><a name="fail-fast"></a>
 * The iterators returned by this class's {@link #iterator() iterator} and
 * {@link #listIterator(int) listIterator} methods are <em>fail-fast</em>:
 * if the list is structurally modified at any time after the iterator is
 * created, in any way except through the iterator's own
 * {@link ListIteratorDouble#remove() remove} or
 * {@link ListIteratorDouble#add(double) add} methods, the iterator will throw a
 * {@link ConcurrentModificationException}.  Thus, in the face of
 * concurrent modification, the iterator fails quickly and cleanly, rather
 * than risking arbitrary, non-deterministic behavior at an undetermined
 * time in the future.
 *
 * <BR><BR>Note that the fail-fast behavior of an iterator cannot be guaranteed
 * as it is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification.  Fail-fast iterators
 * throw {@code ConcurrentModificationException} on a best-effort basis.
 * Therefore, it would be wrong to write a program that depended on this
 * exception for its correctness:  <i>the fail-fast behavior of iterators
 * should be used only to detect bugs.</i>
 *
 * <BR><BR>This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @author  Josh Bloch
 * @author  Neal Gafter
 * @see     CollectionDouble
 * @see     ListDouble
 * @since   1.2
 *
 * @author Ben Crysup
 */
 //I'm mirroring the java standard library
@SuppressWarnings("PMD.TooManyMethods")
public class ArrayListDouble extends AbstractListDouble implements ListDouble, RandomAccess, Cloneable, java.io.Serializable
{
    private static final long serialVersionUID = 8683452581122892189L;
    
    /**
     * The index of the first element.
     */
    protected static final int FIRSTELEMENT = 0;
    
    /**
     * The size of an empty list.
     */
    protected static final int EMPTYLISTSIZE = 0;
    
    /**
     * A number representing no elements.
     */
    protected static final int NOELEMENTS = 0;
    
    /**
     * The minimum allowed capacity.
     */
    protected static final int MINCAPACITYLIMIT = 0;
    
    /**
     * The maximum size of array to allocate.
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * The array buffer into which the elements of the ArrayList are stored.
     * The capacity of the ArrayList is the length of this array buffer.
     */
    protected transient double[] elementDataArray;

    /**
     * The size of the ArrayList (the number of elements it contains).
     *
     * @serial
     */
    protected int mlsize;

    /**
     * Constructs an empty list with the specified initial capacity.
     *
     * @param  initialCapacity  the initial capacity of the list
     * @throws IllegalArgumentException if the specified initial capacity
     *         is negative
     */
    public ArrayListDouble(final int initialCapacity) {
        super();
        if (initialCapacity < NOELEMENTS){
            throw new IllegalArgumentException("Illegal Capacity: "+initialCapacity);
        }
        this.elementDataArray = new double[initialCapacity];
    }

    /**
     * Constructs an empty list with an initial capacity of ten.
     */
    public ArrayListDouble() {
        this(10);
    }

    /**
     * Constructs a list containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.
     *
     * @param collection the collection whose elements are to be placed into this list
     * @throws NullPointerException if the specified collection is null
     */
    public ArrayListDouble(final CollectionDouble collection) {
    	super();
        elementDataArray = collection.toArray();
        mlsize = elementDataArray.length;
    }

    /**
     * Trims the capacity of this <tt>ArrayList</tt> instance to be the
     * list's current size.  An application can use this operation to minimize
     * the storage of an <tt>ArrayList</tt> instance.
     */
    public void trimToSize() {
        modCount++;
        final int oldCapacity = elementDataArray.length;
        if (mlsize < oldCapacity) {
            elementDataArray = Arrays.copyOf(elementDataArray, mlsize);
        }
    }

    /**
     * Increases the capacity of this <tt>ArrayList</tt> instance, if
     * necessary, to ensure that it can hold at least the number of elements
     * specified by the minimum capacity argument.
     *
     * @param   minCapacity   the desired minimum capacity
     */
    public void ensureCapacity(final int minCapacity) {
        if (minCapacity > NOELEMENTS){
            ensureCapacityInternal(minCapacity);
        }
    }

    private void ensureCapacityInternal(final int minCapacity) {
        modCount++;
        // overflow-conscious code
        if (minCapacity - elementDataArray.length > NOELEMENTS){
            grow(minCapacity);
        }
    }

    /**
     * Increases the capacity to ensure that it can hold at least the
     * number of elements specified by the minimum capacity argument.
     *
     * @param minCapacity the desired minimum capacity
     */
    private void grow(final int minCapacity) {
        // overflow-conscious code
        final int oldCapacity = elementDataArray.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < NOELEMENTS){
            newCapacity = minCapacity;
        }
        if (newCapacity - MAX_ARRAY_SIZE > NOELEMENTS){
            newCapacity = hugeCapacity(minCapacity);
        }
        // minCapacity is usually close to size, so this is a win:
        elementDataArray = Arrays.copyOf(elementDataArray, newCapacity);
    }

    private static int hugeCapacity(final int minCapacity) {
        if (minCapacity < MINCAPACITYLIMIT){ // overflow
            throw new OutOfMemoryError();
        }
        return (minCapacity > MAX_ARRAY_SIZE) ?
            Integer.MAX_VALUE :
            MAX_ARRAY_SIZE;
    }

    /**
     * Returns the number of elements in this list.
     *
     * @return the number of elements in this list
     */
     @Override
    public int size() {
        return mlsize;
    }

    /**
     * Returns <tt>true</tt> if this list contains no elements.
     *
     * @return <tt>true</tt> if this list contains no elements
     */
     @Override
    public boolean isEmpty() {
        return mlsize == 0;
    }

    /**
     * Returns <tt>true</tt> if this list contains the specified element.
     * More formally, returns <tt>true</tt> if and only if this list contains
     * at least one element <tt>e</tt> such that
     * <tt>o == e</tt>.
     *
     * @param object element whose presence in this list is to be tested
     * @return <tt>true</tt> if this list contains the specified element
     */
     @Override
    public boolean contains(final double object) {
        return indexOf(object) >= 0;
    }

    /**
     * Returns the index of the first occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the lowest index <tt>i</tt> such that
     * <tt>o == get(i)</tt>,
     * or -1 if there is no such index.
     */
     @Override
    public int indexOf(final double object) {
        for (int i = 0; i < mlsize; i++){
            if (object == elementDataArray[i]){
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the index of the last occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the highest index <tt>i</tt> such that
     * <tt>object == get(i)</tt>,
     * or -1 if there is no such index.
     */
     @Override
    public int lastIndexOf(final double object) {
        for (int i = mlsize-1; i >= 0; i--){
            if (object==elementDataArray[i]){
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns a deep copy of this instance.  (The
     * elements themselves are copied.)
     *
     * @return a clone of this <tt>ArrayListDouble</tt> instance
     * @throws CloneNotSupportedException Should not occur.
     */
    @SuppressWarnings("PMD.PreserveStackTrace")
    public Object clone() throws CloneNotSupportedException{
        try {
        	final ArrayListDouble newArr = (ArrayListDouble)super.clone();
            newArr.elementDataArray = Arrays.copyOf(elementDataArray, mlsize);
            newArr.modCount = 0;
            return newArr;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }

    /**
     * Returns an array containing all of the elements in this list
     * in proper sequence (from first to last element).
     *
     * <p>The returned array will be "safe" in that no references to it are
     * maintained by this list.  (In other words, this method must allocate
     * a new array).  The caller is thus free to modify the returned array.
     *
     * <p>This method acts as bridge between array-based and collection-based
     * APIs.
     *
     * @return an array containing all of the elements in this list in
     *         proper sequence
     */
     @Override
    public double[] toArray() {
        return Arrays.copyOf(elementDataArray, mlsize);
    }

    /**
     * Returns an array containing all of the elements in this list in proper
     * sequence (from first to last element); the runtime type of the returned
     * array is that of the specified array.  If the list fits in the
     * specified array, it is returned therein.  Otherwise, a new array is
     * allocated with the runtime type of the specified array and the size of
     * this list.
     *
     * @param store the array into which the elements of the list are to
     *          be stored, if it is big enough; otherwise,
     *			a new array is allocated for this purpose.
     * @return an array containing the elements of the list
     * @throws NullPointerException if the specified array is null
     */
    
    @Override
    public double[] toArray(final double[] store) {
        if (store.length < mlsize){
            // Make a new array of a's runtime type, but my contents:
            return Arrays.copyOf(elementDataArray, mlsize);
        }
        System.arraycopy(elementDataArray, 0, store, 0, mlsize);
        return store;
    }

    // Positional Access Operations
	
	/**
	 * This will return the entry at the given index.
	 * @param index The index to get.
	 * @return The entry at index.
	 */
    
    protected double elementData(final int index) {
        return elementDataArray[index];
    }

    /**
     * Returns the element at the specified position in this list.
     *
     * @param  index index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
     @Override
    public double get(final int index) {
        rangeCheck(index);

        return elementData(index);
    }

    /**
     * Replaces the element at the specified position in this list with
     * the specified element.
     *
     * @param index index of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
     @Override
    public double set(final int index, final double element) {
        rangeCheck(index);

        final double oldValue = elementData(index);
        elementDataArray[index] = element;
        return oldValue;
    }

    /**
     * Appends the specified element to the end of this list.
     *
     * @param element element to be appended to this list
     * @return <tt>true</tt> (as specified by {@link CollectionDouble#add})
     */
     @Override
    public boolean add(final double element) {
        ensureCapacityInternal(mlsize + 1);  // Increments modCount!!
        elementDataArray[mlsize++] = element;
        return true;
    }

    /**
     * Inserts the specified element at the specified position in this
     * list. Shifts the element currently at that position (if any) and
     * any subsequent elements to the right (adds one to their indices).
     *
     * @param index index at which the specified element is to be inserted
     * @param element element to be inserted
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
     @Override
    public void add(final int index, final double element) {
        rangeCheckForAdd(index);

        ensureCapacityInternal(mlsize + 1);  // Increments modCount!!
        System.arraycopy(elementDataArray, index, elementDataArray, index + 1,
                         mlsize - index);
        elementDataArray[index] = element;
        mlsize++;
    }

    /**
     * Removes the element at the specified position in this list.
     * Shifts any subsequent elements to the left (subtracts one from their
     * indices).
     *
     * @param index the index of the element to be removed
     * @return the element that was removed from the list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
     @Override
    public double removeAt(final int index) {
        rangeCheck(index);

        modCount++;
        final double oldValue = elementData(index);

        final int numMoved = mlsize - index - 1;
        if (numMoved > NOELEMENTS){
            System.arraycopy(elementDataArray, index+1, elementDataArray, index, numMoved);
        }
		mlsize--;
        return oldValue;
    }

    /**
     * Removes the first occurrence of the specified element from this list,
     * if it is present.  If the list does not contain the element, it is
     * unchanged.  More formally, removes the element with the lowest index
     * <tt>i</tt> such that
     * <tt>o == get(i)</tt>
     * (if such an element exists).  Returns <tt>true</tt> if this list
     * contained the specified element (or equivalently, if this list
     * changed as a result of the call).
     *
     * @param element element to be removed from this list, if present
     * @return <tt>true</tt> if this list contained the specified element
     */
     @Override
    public boolean remove(final double element) {
	    for (int index = 0; index < mlsize; index++){
	        if (element == elementDataArray[index]) {
	            fastRemove(index);
	            return true;
	        }
	    }
        return false;
    }

    /*
     * Private remove method that skips bounds checking and does not
     * return the value removed.
     */
    private void fastRemove(final int index) {
        modCount++;
        final int numMoved = mlsize - index - 1;
        if (numMoved > NOELEMENTS){
            System.arraycopy(elementDataArray, index+1, elementDataArray, index, numMoved);
        }
    }

    /**
     * Removes all of the elements from this list.  The list will
     * be empty after this call returns.
     */
     @Override
    public void clear() {
        modCount++;
        mlsize = 0;
    }

    /**
     * Appends all of the elements in the specified collection to the end of
     * this list, in the order that they are returned by the
     * specified collection's Iterator.  The behavior of this operation is
     * undefined if the specified collection is modified while the operation
     * is in progress.  (This implies that the behavior of this call is
     * undefined if the specified collection is this list, and this
     * list is nonempty.)
     *
     * @param collection collection containing elements to be added to this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws NullPointerException if the specified collection is null
     */
     @Override
    public boolean addAll(final CollectionDouble collection) {
        final double[] arr = collection.toArray();
        final int numNew = arr.length;
        ensureCapacityInternal(mlsize + numNew);  // Increments modCount
        System.arraycopy(arr, 0, elementDataArray, mlsize, numNew);
        mlsize += numNew;
        return numNew != 0;
    }

    /**
     * Inserts all of the elements in the specified collection into this
     * list, starting at the specified position.  Shifts the element
     * currently at that position (if any) and any subsequent elements to
     * the right (increases their indices).  The new elements will appear
     * in the list in the order that they are returned by the
     * specified collection's iterator.
     *
     * @param index index at which to insert the first element from the
     *              specified collection
     * @param collection collection containing elements to be added to this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @throws NullPointerException if the specified collection is null
     */
     @Override
    public boolean addAll(final int index, final CollectionDouble collection) {
        rangeCheckForAdd(index);

        final double[] arr = collection.toArray();
        final int numNew = arr.length;
        ensureCapacityInternal(mlsize + numNew);  // Increments modCount

        final int numMoved = mlsize - index;
        if (numMoved > NOELEMENTS){
            System.arraycopy(elementDataArray, index, elementDataArray, index + numNew, numMoved);
        }

        System.arraycopy(arr, 0, elementDataArray, index, numNew);
        mlsize += numNew;
        return numNew != 0;
    }

    /**
     * Removes from this list all of the elements whose index is between
     * {@code fromIndex}, inclusive, and {@code toIndex}, exclusive.
     * Shifts any succeeding elements to the left (reduces their index).
     * This call shortens the list by {@code (toIndex - fromIndex)} elements.
     * (If {@code toIndex==fromIndex}, this operation has no effect.)
     *
     * @throws IndexOutOfBoundsException if {@code fromIndex} or
     *         {@code toIndex} is out of range
     *         ({@code fromIndex < 0 ||
     *          fromIndex >= size() ||
     *          toIndex > size() ||
     *          toIndex < fromIndex})
     */
    protected void removeRange(final int fromIndex, final int toIndex) {
        modCount++;
        final int numMoved = mlsize - toIndex;
        System.arraycopy(elementDataArray, toIndex, elementDataArray, fromIndex,
                         numMoved);
        mlsize = mlsize - (toIndex-fromIndex);
    }

    /**
     * Checks if the given index is in range.  If not, throws an appropriate
     * runtime exception.  This method does *not* check if the index is
     * negative: It is always used immediately prior to an array access,
     * which throws an ArrayIndexOutOfBoundsException if index is negative.
     * 
     * @param index The index to check.
     */
    private void rangeCheck(final int index) {
        if (index >= mlsize){
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
        }
    }

    /**
     * A version of rangeCheck used by add and addAll.
     * @param index The index to check against the set of valid indices.
     */
    private void rangeCheckForAdd(final int index) {
        if (index > mlsize || index < FIRSTELEMENT){
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
        }
    }

    /**
     * Constructs an IndexOutOfBoundsException detail message.
     * Of the many possible refactorings of the error handling code,
     * this "outlining" performs best with both server and client VMs.
     * @param index The index that was out of bounds.
     * @return The error message for an index out of bounds.
     */
    private String outOfBoundsMsg(final int index) {
        return "Index: "+index+", Size: "+mlsize;
    }

    /**
     * Removes from this list all of its elements that are contained in the
     * specified collection.
     *
     * @param collection collection containing elements to be removed from this list
     * @return {@code true} if this list changed as a result of the call
     * @throws IllegalArgumentException if an element of this list
     *         is incompatible with the specified collection
     * (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified collection is null
     * @see CollectionDouble#contains(double)
     */
     @Override
    public boolean removeAll(final CollectionDouble collection) {
        return batchRemove(collection, false);
    }

    /**
     * Retains only the elements in this list that are contained in the
     * specified collection.  In other words, removes from this list all
     * of its elements that are not contained in the specified collection.
     *
     * @param collection collection containing elements to be retained in this list
     * @return {@code true} if this list changed as a result of the call
     * @throws IllegalArgumentException if an element of this list
     *         is incompatible with the specified collection
     * (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified collection is null
     * @see CollectionDouble#contains(double)
     */
     @Override
    public boolean retainAll(final CollectionDouble collection) {
        return batchRemove(collection, true);
    }

    private boolean batchRemove(final CollectionDouble collection, final boolean complement) {
        final double[] elementData = this.elementDataArray;
        int rhead = 0;
        int whead = 0;
        boolean modified = false;
        try {
            for (; rhead < mlsize; rhead++){
                if (collection.contains(elementData[rhead]) == complement){
                    elementData[whead++] = elementData[rhead];
                }
            }
        } finally {
            // Preserve behavioral compatibility with AbstractCollection,
            // even if c.contains() throws.
            if (rhead != mlsize) {
                System.arraycopy(elementData, rhead,
                                 elementData, whead,
                                 mlsize - rhead);
                whead += mlsize - rhead;
            }
            if (whead != mlsize) {
                modCount += mlsize - whead;
                mlsize = whead;
                modified = true;
            }
        }
        return modified;
    }

    /**
     * Save the state of the <tt>ArrayList</tt> instance to a stream (that
     * is, serialize it).
     * 
     * @param oos The stream to write object data to.
     * @throws IOException If there is a problem writing the object.
     * 
     * @serialData The length of the array backing the <tt>ArrayList</tt>
     *             instance is emitted (int), followed by all of its elements
     *             in the proper order.
     */
    private void writeObject(final java.io.ObjectOutputStream oos) throws IOException{
        // Write out element count, and any hidden stuff
        final int expectedModCount = modCount;
        oos.defaultWriteObject();

        // Write out array length
        oos.writeInt(elementDataArray.length);

        // Write out all elements in the proper order.
        for (int i=0; i<mlsize; i++){
            oos.writeDouble(elementDataArray[i]);
        }

        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }

    }

    /**
     * Reconstitute the <tt>ArrayList</tt> instance from a stream (that is,
     * deserialize it).
     * @param ois The input stream to read the list data from.
     * @throws IOException If there is a problem reading the object.
     * @throws ClassNotFoundException If there is a problem reading the object.
     */
    private void readObject(final java.io.ObjectInputStream ois)
        throws IOException, ClassNotFoundException {
        // Read in size, and any hidden stuff
        ois.defaultReadObject();

        // Read in array length and allocate array
        final int arrayLength = ois.readInt();
        final double[] arr = elementDataArray = new double[arrayLength];

        // Read in all elements in the proper order.
        for (int i=0; i<mlsize; i++){
            arr[i] = ois.readDouble();
        }
    }

    /**
     * Returns a list iterator over the elements in this list (in proper
     * sequence), starting at the specified position in the list.
     * The specified index indicates the first element that would be
     * returned by an initial call to {@link ListIteratorDouble#next next}.
     * An initial call to {@link ListIteratorDouble#previous previous} would
     * return the element with the specified index minus one.
     *
     * <p>The returned list iterator is <a href="#fail-fast"><i>fail-fast</i></a>.
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
     @Override
    public ListIteratorDouble listIterator(final int index) {
        if (index < FIRSTELEMENT || index > mlsize){
            throw new IndexOutOfBoundsException("Index: "+index);
        }
        return new ArrayListListIteratorDouble(index, this);
    }

    /**
     * Returns a list iterator over the elements in this list (in proper
     * sequence).
     *
     * <p>The returned list iterator is <a href="#fail-fast"><i>fail-fast</i></a>.
     *
     * @see #listIterator(int)
     */
     @Override
    public ListIteratorDouble listIterator() {
        return new ArrayListListIteratorDouble(0, this);
    }

    /**
     * Returns an iterator over the elements in this list in proper sequence.
     *
     * <p>The returned iterator is <a href="#fail-fast"><i>fail-fast</i></a>.
     *
     * @return an iterator over the elements in this list in proper sequence
     */
     @Override
    public IteratorDouble iterator() {
        return new ArrayListIteratorDouble(this);
    }

    /**
     * Returns a view of the portion of this list between the specified
     * {@code fromIndex}, inclusive, and {@code toIndex}, exclusive.  (If
     * {@code fromIndex} and {@code toIndex} are equal, the returned list is
     * empty.)  The returned list is backed by this list, so non-structural
     * changes in the returned list are reflected in this list, and vice-versa.
     * The returned list supports all of the optional list operations.
     *
     * <p>This method eliminates the need for explicit range operations (of
     * the sort that commonly exist for arrays).  Any operation that expects
     * a list can be used as a range operation by passing a subList view
     * instead of a whole list.  For example, the following idiom
     * removes a range of elements from a list:
     * <pre>
     *      list.subList(from, to).clear();
     * </pre>
     * Similar idioms may be constructed for {@link #indexOf(double)} and
     * {@link #lastIndexOf(double)}, and all of the algorithms in the
     * Collections class can be applied to a subList.
     *
     * <p>The semantics of the list returned by this method become undefined if
     * the backing list (i.e., this list) is <i>structurally modified</i> in
     * any way other than via the returned list.  (Structural modifications are
     * those that change the size of this list, or otherwise perturb it in such
     * a fashion that iterations in progress may yield incorrect results.)
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
     @Override
    public ListDouble subList(final int fromIndex, final int toIndex) {
        subListRangeCheck(fromIndex, toIndex, mlsize);
        return new ArrayListSubListDouble(this, this, 0, fromIndex, toIndex);
    }
	
	/**
	 * This will check that the bounds on a requested sub-list are valid.
	 * @param fromIndex The first index in the main list for the sub list.
	 * @param toIndex The first index in the main list after the sub list.
	 * @param size The size of the main list.
	 * @throws IndexOutOfBoundsException If one of the indices is invalid.
	 * @throws IllegalArgumentException If the indices are out of order.
	 */
    protected static void subListRangeCheck(final int fromIndex, final int toIndex, final int size) {
        if (fromIndex < FIRSTELEMENT){
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
        }
        if (toIndex > size){
            throw new IndexOutOfBoundsException("toIndex = " + toIndex);
        }
        if (fromIndex > toIndex){
            throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
        }
    }
}
