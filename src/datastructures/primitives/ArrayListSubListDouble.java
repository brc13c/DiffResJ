package datastructures.primitives;
import java.util.RandomAccess;
import java.util.ConcurrentModificationException;

/*GENERATED CODE, DO NOT MODIFY*/

/**
 * Local implementation.
 * This class acts as a sub list view for an array list.
 */
class ArrayListSubListDouble extends AbstractListDouble implements RandomAccess {
        private final AbstractListDouble parent;
        private final ArrayListDouble handler;
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
        ArrayListSubListDouble(final ArrayListDouble handler, final AbstractListDouble parent, final int offset, final int fromIndex, final int toIndex) {
        	super();
        	this.handler = handler;
            this.parent = parent;
            this.parentOffset = fromIndex;
            this.offset = offset + fromIndex;
            this.cursize = toIndex - fromIndex;
            this.modCount = handler.modCount;
        }

        public double set(final int index, final double element) {
            rangeCheck(index);
            checkForComodification();
            final double oldValue = handler.elementData(offset + index);
            handler.elementDataArray[offset + index] = element;
            return oldValue;
        }

        public double get(final int index) {
            rangeCheck(index);
            checkForComodification();
            return handler.elementData(offset + index);
        }

        public int size() {
            checkForComodification();
            return this.cursize;
        }

        public void add(final int index, final double element) {
            rangeCheckForAdd(index);
            checkForComodification();
            parent.add(parentOffset + index, element);
            this.modCount = parent.modCount;
            this.cursize++;
        }

        public double removeAt(final int index) {
            rangeCheck(index);
            checkForComodification();
            final double result = parent.removeAt(parentOffset + index);
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

        public boolean addAll(final CollectionDouble collection) {
            return addAll(this.cursize, collection);
        }

        public boolean addAll(final int index, final CollectionDouble collection) {
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

        public IteratorDouble iterator() {
            return listIterator();
        }
		
        public ListIteratorDouble listIterator(final int index) {
            checkForComodification();
            rangeCheckForAdd(index);
            final int offset = this.offset;

            return new ArrayListSubListIteratorDouble(index, offset, handler, this);
        }

        public ListDouble subList(final int fromIndex, final int toIndex) {
            ArrayListDouble.subListRangeCheck(fromIndex, toIndex, cursize);
            return new ArrayListSubListDouble(handler, this, offset, fromIndex, toIndex);
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