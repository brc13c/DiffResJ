package datastructures.primitives;
import java.util.RandomAccess;

/*GENERATED CODE, DO NOT MODIFY*/

/**
 * Local implementation.
 * This allows viewing a RandomAccess AbstractList in a RandomAccess manner.
 */
class AbstractListRandomAccessSubListInt extends AbstractListSubListInt implements RandomAccess {
	
	/**
	 * This wraps an abstract list as a random access list.
	 * @param list The list to wrap.
	 * @param fromIndex The first element in the view.
	 * @param toIndex The element after the last element in the view.
	 */
    AbstractListRandomAccessSubListInt(final AbstractListInt list, final int fromIndex, final int toIndex) {
        super(list, fromIndex, toIndex);
    }

    public ListInt subList(final int fromIndex, final int toIndex) {
        return new AbstractListRandomAccessSubListInt(this, fromIndex, toIndex);
    }
}