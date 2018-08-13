package datastructures.primitives;
import java.util.RandomAccess;

/*GENERATED CODE, DO NOT MODIFY*/

/**
 * Local implementation.
 * This allows viewing a RandomAccess AbstractList in a RandomAccess manner.
 */
class AbstractListRandomAccessSubListDouble extends AbstractListSubListDouble implements RandomAccess {
	
	/**
	 * This wraps an abstract list as a random access list.
	 * @param list The list to wrap.
	 * @param fromIndex The first element in the view.
	 * @param toIndex The element after the last element in the view.
	 */
    AbstractListRandomAccessSubListDouble(final AbstractListDouble list, final int fromIndex, final int toIndex) {
        super(list, fromIndex, toIndex);
    }

    public ListDouble subList(final int fromIndex, final int toIndex) {
        return new AbstractListRandomAccessSubListDouble(this, fromIndex, toIndex);
    }
}