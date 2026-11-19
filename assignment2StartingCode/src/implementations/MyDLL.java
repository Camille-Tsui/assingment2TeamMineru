package implementations;

import java.util.Arrays;
import java.util.NoSuchElementException;
import utilities.Iterator;
import utilities.ListADT;

/**
 * A doubly linked list (DLL) implementation of the {@link ListADT} interface,
 * for use in CPRG 304 at SAIT.
 * <p>
 * This class stores {@link String} elements in a chain of {@link MyDLLNode}
 * objects, each of which holds a reference to both its predecessor ({@code pred})
 * and its successor ({@code succ}). The list maintains explicit {@code head}
 * and {@code tail} references for O(1) access to both ends of the chain.
 * Insertions and removals at arbitrary positions require O(n) traversal.
 * All index parameters are zero-based unless otherwise noted.
 * </p>
 *
 * <p><strong>Design Note – String-Specific Implementation:</strong><br>
 * This class is intentionally designed to store {@link String} elements only.
 * A fully reusable, type-safe design would use Java generics (e.g.,
 * {@code MyDLL<E>}), allowing any object type to be stored in the list.
 * Generic programming is outside the scope of CPRG 304 and has therefore
 * been omitted. All element fields, parameters, and return types are fixed
 * as {@link String} throughout this class and its implementations.
 * </p>
 */
public class MyDLL implements ListADT
{
	/** The first node in the list (null when the list is empty). */
	private MyDLLNode head;

	/** The last node in the list (null when the list is empty). */
	private MyDLLNode tail;

	/** The number of elements in the list. */
	private int size;

	/**
	 * Creates a new, empty doubly linked list.
	 */
	public MyDLL() {
		head = null;
		tail = null;
		size = 0;
	}

	/**
	 * Returns the number of elements currently in the list.
	 *
	 * @return the current size of the list
	 */
	@Override
	public int size() {
		return size;
	}

	/**
	 * Removes all elements from the list, leaving it empty.
	 */
	@Override
	public void clear() {
		head = null;
		tail = null;
		size = 0;
	}

	/**
	 * Inserts the element at the given index. Elements at or after that
	 * index move one place to the right.
	 *
	 * @param index the position to insert at; valid range is 0 to size()
	 * @param toAdd the String to insert; must not be null
	 * @return true if the element was added
	 * @throws NullPointerException      if toAdd is null
	 * @throws IndexOutOfBoundsException if index is less than 0 or greater than size()
	 */
	@Override
	public boolean add(int index, String toAdd) throws NullPointerException, IndexOutOfBoundsException {
		if (toAdd == null) {
			throw new NullPointerException("Cannot add null to the list.");
		}
		if (index < 0 || index > size) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
		}

		// Adding at the end is the same as a normal add
		if (index == size) {
			return add(toAdd);
		}

		MyDLLNode newNode = new MyDLLNode(toAdd);

		if (index == 0) {
			// Add at the front: new node becomes the head
			newNode.setSucc(head);
			head.setPred(newNode);
			head = newNode;
		} else {
			// Add in the middle: put new node before the node at "index"
			MyDLLNode current = getNode(index);
			MyDLLNode before = current.getPred();

			newNode.setPred(before);
			newNode.setSucc(current);
			before.setSucc(newNode);
			current.setPred(newNode);
		}

		size++;
		return true;
	}

	/**
	 * Adds the element to the end of the list.
	 *
	 * @param toAdd the String to add; must not be null
	 * @return true if the element was added
	 * @throws NullPointerException if toAdd is null
	 */
	@Override
	public boolean add(String toAdd) throws NullPointerException {
		if (toAdd == null) {
			throw new NullPointerException("Cannot add null to the list.");
		}

		MyDLLNode newNode = new MyDLLNode(toAdd);

		if (head == null) {
			// List is empty: the new node is both head and tail
			head = newNode;
			tail = newNode;
		} else {
			// Link the new node after the current tail
			tail.setSucc(newNode);
			newNode.setPred(tail);
			tail = newNode;
		}

		size++;
		return true;
	}

	/**
	 * Adds all elements from another list to the end of this list, in order.
	 *
	 * @param toAdd the list whose elements will be added; must not be null
	 * @return true if this list changed
	 * @throws NullPointerException if toAdd is null
	 */
	@Override
	public boolean addAll(ListADT toAdd) throws NullPointerException {
		if (toAdd == null) {
			throw new NullPointerException("Cannot add a null list.");
		}

		Iterator it = toAdd.iterator();
		boolean changed = false;
		while (it.hasNext()) {
			add(it.next());
			changed = true;
		}
		return changed;
	}

	/**
	 * Returns the element at the given index.
	 *
	 * @param index the position of the element; valid range is 0 to size() - 1
	 * @return the element at that index
	 * @throws IndexOutOfBoundsException if index is less than 0 or not less than size()
	 */
	@Override
	public String get(int index) throws IndexOutOfBoundsException {
		checkIndex(index);
		return getNode(index).getElement();
	}

	/**
	 * Removes and returns the element at the given index.
	 *
	 * @param index the position of the element to remove; valid range is 0 to size() - 1
	 * @return the element that was removed
	 * @throws IndexOutOfBoundsException if index is less than 0 or not less than size()
	 */
	@Override
	public String remove(int index) throws IndexOutOfBoundsException {
		checkIndex(index);
		MyDLLNode toRemove = getNode(index);
		unlink(toRemove);
		return toRemove.getElement();
	}

	/**
	 * Removes and returns the first element equal to toRemove.
	 *
	 * @param toRemove the String to remove; must not be null
	 * @return the removed element, or null if it was not found
	 * @throws NullPointerException if toRemove is null
	 */
	@Override
	public String remove(String toRemove) throws NullPointerException {
		if (toRemove == null) {
			throw new NullPointerException("Cannot remove null.");
		}

		MyDLLNode current = head;
		while (current != null) {
			if (current.getElement().equals(toRemove)) {
				unlink(current);
				return current.getElement();
			}
			current = current.getSucc();
		}
		return null;
	}

	/**
	 * Replaces the element at the given index and returns the old element.
	 *
	 * @param index    the position to change; valid range is 0 to size() - 1
	 * @param toChange the new String to store; must not be null
	 * @return the element that was there before
	 * @throws NullPointerException      if toChange is null
	 * @throws IndexOutOfBoundsException if index is less than 0 or not less than size()
	 */
	@Override
	public String set(int index, String toChange) throws NullPointerException, IndexOutOfBoundsException {
		if (toChange == null) {
			throw new NullPointerException("Cannot set null in the list.");
		}
		checkIndex(index);

		MyDLLNode node = getNode(index);
		String oldElement = node.getElement();
		node.setElement(toChange);
		return oldElement;
	}

	/**
	 * Checks if the list has no elements.
	 *
	 * @return true if the list is empty, otherwise false
	 */
	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Checks if the list contains the given element.
	 *
	 * @param toFind the String to look for; must not be null
	 * @return true if the element is in the list, otherwise false
	 * @throws NullPointerException if toFind is null
	 */
	@Override
	public boolean contains(String toFind) throws NullPointerException {
		return indexOf(toFind) != -1;
	}

	/**
	 * Returns the index of the first element equal to toFind.
	 *
	 * @param toFind the String to look for; must not be null
	 * @return the index of the first match, or -1 if not found
	 * @throws NullPointerException if toFind is null
	 */
	@Override
	public int indexOf(String toFind) throws NullPointerException {
		if (toFind == null) {
			throw new NullPointerException("Cannot search for null.");
		}

		MyDLLNode current = head;
		int index = 0;
		while (current != null) {
			if (current.getElement().equals(toFind)) {
				return index;
			}
			current = current.getSucc();
			index++;
		}
		return -1;
	}

	/**
	 * Copies the list elements, in order, into an array.
	 * If toHold is big enough it is used, otherwise a new array is made.
	 *
	 * @param toHold the array to fill; must not be null
	 * @return an array containing all elements of this list in order
	 * @throws NullPointerException if toHold is null
	 */
	@Override
	public String[] toArray(String[] toHold) throws NullPointerException {
		if (toHold == null) {
			throw new NullPointerException("The array cannot be null.");
		}

		// Make a bigger array if the given one is too small
		if (toHold.length < size) {
			toHold = Arrays.copyOf(toHold, size);
		}

		MyDLLNode current = head;
		int i = 0;
		while (current != null) {
			toHold[i] = current.getElement();
			current = current.getSucc();
			i++;
		}
		return toHold;
	}

	/**
	 * Returns an iterator that goes through the list from first to last.
	 * The iterator works on a copy, so later changes to the list do not affect it.
	 *
	 * @return an iterator over the elements of this list
	 */
	@Override
	public Iterator iterator() {
		return new DLLIterator();
	}

	// ---------------------------------------------------------------
	// Private helper methods
	// ---------------------------------------------------------------

	/**
	 * Checks that the index points to an existing element.
	 *
	 * @param index the index to check
	 * @throws IndexOutOfBoundsException if index is less than 0 or not less than size()
	 */
	private void checkIndex(int index) throws IndexOutOfBoundsException {
		if (index < 0 || index >= size) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
		}
	}

	/**
	 * Walks from the head and returns the node at the given index.
	 * The index must already be checked before calling this method.
	 *
	 * @param index the position of the node
	 * @return the node at that position
	 */
	private MyDLLNode getNode(int index) {
		MyDLLNode current = head;
		for (int i = 0; i < index; i++) {
			current = current.getSucc();
		}
		return current;
	}

	/**
	 * Removes a node from the chain by connecting its neighbours to each other.
	 * Updates head and tail if needed and lowers the size by one.
	 *
	 * @param node the node to remove
	 */
	private void unlink(MyDLLNode node) {
		MyDLLNode before = node.getPred();
		MyDLLNode after = node.getSucc();

		if (before == null) {
			head = after;          // removing the head
		} else {
			before.setSucc(after);
		}

		if (after == null) {
			tail = before;         // removing the tail
		} else {
			after.setPred(before);
		}

		node.setPred(null);
		node.setSucc(null);
		size--;
	}

	// ---------------------------------------------------------------
	// Inner iterator class
	// ---------------------------------------------------------------

	/**
	 * Iterator for MyDLL. It makes a copy of the list elements when it is
	 * created, so changes to the list afterwards do not affect it.
	 */
	private class DLLIterator implements Iterator
	{
		/** Copy of the list elements made when the iterator was created. */
		private String[] copy;

		/** Index of the next element to return. */
		private int position;

		/**
		 * Creates the iterator and copies the current list elements.
		 */
		public DLLIterator() {
			copy = toArray(new String[size]);
			position = 0;
		}

		/**
		 * Checks if there are more elements to return.
		 *
		 * @return true if there is another element, otherwise false
		 */
		@Override
		public boolean hasNext() {
			return position < copy.length;
		}

		/**
		 * Returns the next element and moves forward by one.
		 *
		 * @return the next element
		 * @throws NoSuchElementException if there are no more elements
		 */
		@Override
		public String next() throws NoSuchElementException {
			if (!hasNext()) {
				throw new NoSuchElementException("No more elements in the list.");
			}
			String element = copy[position];
			position++;
			return element;
		}
	}
}
