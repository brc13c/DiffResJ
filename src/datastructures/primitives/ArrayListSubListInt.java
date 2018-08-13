package datastructures.primitives;
import java.util.RandomAccess;
import java.util.ConcurrentModificationException;

/*GENERATED CODE, DO NOT MODIFY*/

/**
 * Local implementation.
 * This class acts as a sub list view for an array list.
 */
class ArrayListSubListInt extends AbstractListInt implements RandomAccess {
        private final AbstractListInt parent;
        private final ArrayListInt handler;
        private final int parentOffset;
        private final int offset;
        
        /**
         * The size of this sub list.
         */
        protected int cursize;
		
		/**
		 * This builds a view of the given array list
		 * (given a previously restricted view of the list).
		 * @param handler The underlying array list.
		 * @param parent The list to create a view of.
		 * @param offset The offset of the first element in the underlying array.
		 * @param fromIndex The index in the view list to start from.
		 * @param toIndex The index in the view list to stop before.
		 */
        ArrayListSubListInt(final ArrayListInt handler, final AbstractListInt parent, final int offset, final int fromIndex, final int toIndex) {
        	super();
        	this.handler = handler;
            this.parent = parent;
            this.parentOffset = fromIndex;
            this.offset = offset + fromIndex;
            this.cursize = toIndex - fromIndex;
            this.modCount = handler.modCount;
        }

        public int set(final int index, final int element) {
            rangeCheck(index);
            checkForComodification();
            final int oldValue = handler.elementData(offset + index);
            handler.elementDataArray[offset + index] = element;
            return oldValue;
        }

        public int get(final int index) {
            rangeCheck(index);
            checkForComodification();
            return handler.elementData(offset + index);
        }

        public int size() {
            checkForComodification();
            return this.cursize;
        }

        public void add(final int index, final int element) {
            rangeCheckForAdd(index);
            checkForComodification();
            parent.add(parentOffset + index, element);
            this.modCount = parent.modCount;
            this.cursize++;
        }

        public int removeAt(final int index) {
            rangeCheck(index);
            checkForComodification();
            final int result = parent.removeAt(parentOffset + index);
            this.modCount = parent.modCount;
            this.cursize--;
            return result;
        }

        protected void removeRange(final int fromIndex, final int toIndex) {
            checkForComodification();
            parent.removeRange(parentOffset + fromIndex,
                               parentOffset + toIndex);
            this.modCount = parent.modCount;
            this.cursize -= toIndex - fromIndex;
        }

        public boolean addAll(final CollectionInt collection) {
            return addAll(this.cursize, collection);
        }

        public boolean addAll(final int index, final CollectionInt collection) {
            rangeCheckForAdd(index);
            final int cSize = collection.size();
            if (cSize==EMPTYLISTSIZE){
                return false;
            }

            checkForComodification();
            parent.addAll(parentOffset + index, collection);
            this.modCount = parent.modCount;
            this.cursize += cSize;
            return true;
        }

        public IteratorInt iterator() {
            return listIterator();
        }
		
        public ListIteratorInt listIterator(final int index) {
            checkForComodification();
            rangeCheckForAdd(index);
            final int offset = this.offset;

            return new ArrayListSubListIteratorInt(index, offset, handler, this);
        }

        public ListInt subList(final int fromIndex, final int toIndex) {
            ArrayListInt.subListRangeCheck(fromIndex, toIndex, cursize);
            return new ArrayListSubListInt(handler, this, offset, fromIndex, toIndex);
        }

        private void rangeCheck(final int index) {
            if (index < 0 || index >= this.cursize){
                throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
            }
        }

        private void rangeCheckForAdd(final int index) {
            if (index < 0 || index > this.cursize){
                throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
            }
        }

        private String outOfBoundsMsg(final int index) {
            return "Index: "+index+", Size: "+this.cursize;
        }

        private void checkForComodification() {
            if (handler.modCount != this.modCount){
                throw new ConcurrentModificationException();
            }
        }
    }