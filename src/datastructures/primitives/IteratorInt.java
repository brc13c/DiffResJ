package datastructures.primitives;
import java.util.Iterator;
import java.util.NoSuchElementException;

/*GENERATED CODE, DO NOT MODIFY*/

/**
 * This is an implementation of Iterator for primitive types.
 * It is a modified form of Iterator.
 *
 * <p>An iterator over a collection.  Iterators take the place of
 * Enumerations in the Java Collections Framework.  Iterators
 * differ from enumerations in two ways:
 *
 * <ul>
 *      <li> Iterators allow the caller to remove elements from the
 *           underlying collection during the iteration with well-defined
 *           semantics.
 *      <li> Method names have been improved.
 * </ul>
 *
 * <p>This interface is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @author  Josh Bloch
 * @see CollectionInt
 * @see ListIteratorInt
 * @see IterableInt
 * @since 1.2
 *
 * @author Ben Crysup
 */
public interface IteratorInt extends Iterator<Integer> {
	
    /**
     * Returns the next element in the iteration as a primitive.
     *
     * @return the next element in the iteration
     * @throws NoSuchElementException if the iteration has no more elements
     */
    int nextPrimitive();
}
