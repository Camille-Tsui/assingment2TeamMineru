package implementations;

import java.util.EmptyStackException;
import java.util.NoSuchElementException;
import utilities.Iterator;
import utilities.StackADT;

/**
 * An array-backed implementation of the {@link StackADT} interface,
 * for use in CPRG 304 at SAIT.
 * <p>
 * This class delegates all storage to an internal {@link MyArrayList},
 * treating the end of the list as the top of the stack. {@link #push(String)}
 * appends to the end; {@link #pop()} and {@link #peek()} access the last
 * element. A stack follows a <strong>Last-In, First-Out (LIFO)</strong>
 * ordering policy: the last element pushed is the first one returned.
 * </p>
 *
 * <p><strong>Design Note – String-Specific Implementation:</strong><br>
 * This class is intentionally designed to store {@link String} elements only.
 * A fully reusable, type-safe design would use Java generics (e.g.,
 * {@code MyStack<E>}), allowing any object type to be stored in the stack.
 * Generic programming is outside the scope of CPRG 304 and has therefore
 * been omitted. All element fields, parameters, and return types are fixed
 * as {@link String} throughout this class and its implementations.
 * </p>
 */
public class MyStack implements StackADT
{
	private MyArrayList list; // last element = top of stack

	/**
	 * Makes an empty stack.
	 */
	public MyStack() {
		list = new MyArrayList();
	}

	/**
	 * Adds an element to the top of the stack.
	 * @param toAdd the element to push
	 * @throws NullPointerException if toAdd is null
	 */
	@Override
	public void push(String toAdd) throws NullPointerException {
		if (toAdd == null) {
			throw new NullPointerException();
		}
		list.add(toAdd);
	}

	/**
	 * Removes the top element and returns it.
	 * @return the top element
	 * @throws EmptyStackException if the stack is empty
	 */
	@Override
	public String pop() throws EmptyStackException {
		if (list.isEmpty()) {
			throw new EmptyStackException();
		}
		return list.remove(list.size() - 1);
	}

	/**
	 * Returns the top element but doesn't remove it.
	 * @return the top element
	 * @throws EmptyStackException if the stack is empty
	 */
	@Override
	public String peek() throws EmptyStackException {
		if (list.isEmpty()) {
			throw new EmptyStackException();
		}
		return list.get(list.size() - 1);
	}

	/**
	 * Empties the stack.
	 */
	@Override
	public void clear() {
		list.clear();
	}

	/**
	 * Checks if the stack is empty.
	 * @return true if empty
	 */
	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	/**
	 * Copies the stack into an array (bottom to top).
	 * @param holder the array to fill
	 * @return array with the elements from bottom to top
	 * @throws NullPointerException if holder is null
	 */
	@Override
	public String[] toArray(String[] holder) throws NullPointerException {
		if (holder == null) {
			throw new NullPointerException();
		}
		return list.toArray(holder);
	}

	/**
	 * Checks if the stack has the given element.
	 * @param toFind the element to look for
	 * @return true if it's in the stack
	 * @throws NullPointerException if toFind is null
	 */
	@Override
	public boolean contains(String toFind) throws NullPointerException {
		if (toFind == null) {
			throw new NullPointerException();
		}
		return list.contains(toFind);
	}

	/**
	 * Returns how far the element is from the top (top = 1).
	 * @param toFind the element to look for
	 * @return position from the top, or -1 if not found
	 */
	@Override
	public int search(String toFind) {
		if (toFind == null) {
			return -1;
		}

		// go from the top down
		for (int i = list.size() - 1; i >= 0; i--) {
			if (list.get(i).equals(toFind)) {
				return list.size() - i;
			}
		}
		return -1;
	}

	/**
	 * Returns an iterator that goes bottom to top.
	 * @return iterator for the stack
	 */
	@Override
	public Iterator iterator() {
		return list.iterator();
	}

	/**
	 * Checks if both stacks have the same elements in the same order.
	 * @param that the other stack
	 * @return true if they are the same
	 */
	@Override
	public boolean equals(StackADT that) {
		if (that == null || this.size() != that.size()) {
			return false;
		}

		Iterator it1 = this.iterator();
		Iterator it2 = that.iterator();
		while (it1.hasNext()) {
			if (!it1.next().equals(it2.next())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Gets the number of elements in the stack.
	 * @return size of the stack
	 */
	@Override
	public int size() {
		return list.size();
	}
}
