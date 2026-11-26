package implementations;

import java.util.Arrays;
import java.util.NoSuchElementException;
import utilities.Iterator;
import utilities.ListADT;

/**
 * A dynamic array-based implementation of the {@link ListADT} interface,
 * for use in CPRG 304 at SAIT.
 * <p>
 * This class stores {@link String} elements in an internally managed array
 * that grows automatically when capacity is reached. The growth amount is
 * determined by a configurable growth rate expressed as a decimal percentage
 * of the current array length (e.g., {@code 0.25} means 25% growth).
 * All index parameters are zero-based unless otherwise noted.
 * </p>
 *
 * <p><strong>Design Note – String-Specific Implementation:</strong><br>
 * This class is intentionally designed to store {@link String} elements only.
 * A fully reusable, type-safe design would use Java generics (e.g.,
 * {@code MyArrayList<E>}), allowing any object type to be stored in the list.
 * Generic programming is outside the scope of CPRG 304 and has therefore
 * been omitted. All element fields, parameters, and return types are fixed
 * as {@link String} throughout this class and its implementations.
 * </p>
 */
//AI usage disclaimer: AI was used for creating JavaDoc comments

public class MyArrayList implements ListADT
{
	/*The underlying array storing the {@link String} elements in this list.*/
	String[] list = new String[10];
	/*The current number of elements stored in this list.*/
	int size = 0;
	/*The decimal percentage by which the internal array grows when full.*/
	float growthRate = 0.25f;
	
	/**
	 * Returns the current number of elements in this list.
	 *
	 * @return the number of elements currently stored in this list
	 */
	@Override
	public int size() {		
		return size;
	}

	/**
	 * Removes all elements from this list, leaving it empty with a size of zero.
	 */
	@Override
	public void clear() {
		for (int i = size - 1; i >= 0; i--) {
			list[i] = null;
			size -= 1;
		}
	}

	/**
	 * Inserts the specified {@link String} element at the specified position
	 * in this list, shifting any subsequent elements to the right.
	 *
	 * @param index the zero-based index at which the element is to be inserted
	 * @param toAdd the {@link String} element to insert
	 * @return {@code true} if the element was successfully added
	 * @throws NullPointerException if {@code toAdd} is {@code null}
	 * @throws IndexOutOfBoundsException if {@code index} is out of range
	 *         ({@code index < 0 || index > size()})
	 */
	@Override
	public boolean add(int index, String toAdd) throws NullPointerException, IndexOutOfBoundsException {
		if (toAdd == null) {
			throw new NullPointerException();
		}
		if (index < 0 || index > size) {
			throw new IndexOutOfBoundsException();
		}
		
		if (list.length == size) {
			increaseSize();
		}
		
		for (int i = size; i > index; i--) {
			list[i] = list[i-1];
		}
		
		list[index] = toAdd;
		size += 1;
		return true;
	}

	/**
	 * Appends the specified {@link String} element to the end of this list.
	 *
	 * @param toAdd the {@link String} element to append
	 * @return {@code true} if the element was successfully added
	 * @throws NullPointerException if {@code toAdd} is {@code null}
	 */
	@Override
	public boolean add(String toAdd) throws NullPointerException {
		if (toAdd == null) {
			throw new NullPointerException();
		}
		if (list.length == size) {
			increaseSize();
		}
		
		list[size] = toAdd;
		size += 1;
		return true;
	}

	/**
	 * Appends all elements from the specified {@link ListADT} to the end of
	 * this list in the order returned by its iterator.
	 *
	 * @param toAdd the {@link ListADT} containing elements to append
	 * @return {@code true} if all elements were successfully added
	 * @throws NullPointerException if {@code toAdd} is {@code null}
	 */
	@Override
	public boolean addAll(ListADT toAdd) throws NullPointerException {
		if (toAdd == null) {
			throw new NullPointerException();
		}
		
		Iterator it = toAdd.iterator();
		while (it.hasNext()) {
			add(it.next());
		}
		return true;
	}

	/**
	 * Returns the {@link String} element at the specified position in this list.
	 *
	 * @param index the zero-based index of the element to retrieve
	 * @return the {@link String} element at the specified index
	 * @throws IndexOutOfBoundsException if {@code index} is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	@Override
	public String get(int index) throws IndexOutOfBoundsException {
		if (index > size - 1) {
			throw new IndexOutOfBoundsException();
		}
		return list[index];
	}

	/**
	 * Removes and returns the {@link String} element at the specified position
	 * in this list, shifting any subsequent elements to the left.
	 *
	 * @param index the zero-based index of the element to remove
	 * @return the {@link String} element that was removed from the list
	 * @throws IndexOutOfBoundsException if {@code index} is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	@Override
	public String remove(int index) throws IndexOutOfBoundsException {
		if (index > size - 1 || index < 0) {
			throw new IndexOutOfBoundsException();
		}
		
		//overwrite index and shifting elements back down
		String element = list[index];
		for (int i = index; i < size -1; i++) {
			list[i] = list[i + 1];
		}
		size -= 1;
		return element;
	}

	/**
	 * Removes the first occurrence of the specified {@link String} element
	 * from this list, if it is present.
	 *
	 * @param toRemove the {@link String} element to remove from this list
	 * @return the removed {@link String} element, or {@code null} if not found
	 * @throws NullPointerException if {@code toRemove} is {@code null}
	 */
	@Override
	public String remove(String toRemove) throws NullPointerException {
		if (toRemove == null) {
			throw new NullPointerException();
		}
		
		int index = indexOf(toRemove);
		
		if (index != -1) {
			String element = list[index];
			remove(index);
			return element;
		}
		
		return null;
	}

	/**
	 * Replaces the {@link String} element at the specified position in this list
	 * with the specified element.
	 *
	 * @param index the zero-based index of the element to replace
	 * @param toChange the new {@link String} element to store at the specified index
	 * @return the previous {@link String} element stored at the specified index
	 * @throws NullPointerException if {@code toChange} is {@code null}
	 * @throws IndexOutOfBoundsException if {@code index} is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	@Override
	public String set(int index, String toChange) throws NullPointerException, IndexOutOfBoundsException {
		if (toChange == null) {
			throw new NullPointerException();
		}
		if (index < 0 || index >= size) {
			throw new IndexOutOfBoundsException();
		}

		
		String oldElement = list[index];
		list[index] = toChange;
		
		return oldElement;
	}

	/**
	 * Checks whether this list contains no elements.
	 *
	 * @return {@code true} if this list contains no elements, {@code false} otherwise
	 */
	@Override
	public boolean isEmpty() {
		if (size <= 0) {
			return true;
		}
		return false;
	}

	/**
	 * Checks whether this list contains the specified {@link String} element.
	 *
	 * @param toFind the {@link String} element whose presence is to be tested
	 * @return {@code true} if this list contains the specified element, {@code false} otherwise
	 * @throws NullPointerException if {@code toFind} is {@code null}
	 */
	@Override
	public boolean contains(String toFind) throws NullPointerException {
		if (toFind == null) {
			throw new NullPointerException();
		}
		
		for (int i = 0; i < size; i++) {
			if (list[i] == toFind) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the zero-based index of the first occurrence of the specified
	 * {@link String} element in this list, or {@code -1} if not found.
	 *
	 * @param toFind the {@link String} element to search for
	 * @return the zero-based index of the first matching element, or {@code -1} if absent
	 * @throws NullPointerException if {@code toFind} is {@code null}
	 */
	@Override
	public int indexOf(String toFind) throws NullPointerException {
		if (toFind == null) {
			throw new NullPointerException();
		}
		
		for (int i = 0; i < size; i++) {
			if (list[i] == toFind) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Copies all elements of this list into the provided array, or into a newly
	 * allocated {@link String} array if the provided array is too small.
	 *
	 * @param toHold the array into which the elements of this list are to be stored
	 * @return an array containing all elements of this list in proper sequence
	 * @throws NullPointerException if {@code toHold} is {@code null}
	 */
	@Override
	public String[] toArray(String[] toHold) throws NullPointerException {
		if (toHold == null) {
			throw new NullPointerException();
		}
		
		//check if size large enough
		if (toHold.length < size) {
			toHold = new String[size];
		}
		
		//copy elements
		for (int i = 0; i < size; i++) {
			toHold[i] = list[i];
		}
		
		return toHold;
	}

	/**
	 * Returns an {@link Iterator} over a snapshot copy of the elements in this
	 * list in proper sequence.
	 *
	 * @return an {@link Iterator} over the elements in this list
	 */
	@Override
	public Iterator iterator() {
		String[] copy = toArray(new String[size]);

		
		return new Iterator() {
			int index = 0;

			/**
			 * Returns {@code true} if the iteration has more elements.
			 *
			 * @return {@code true} if there are remaining elements to iterate over
			 */
			@Override
			public boolean hasNext() {
				if (index < copy.length) {
					return true;
				}
				return false;
			}

			/**
			 * Returns the next {@link String} element in the iteration.
			 *
			 * @return the next {@link String} element in the iteration
			 * @throws NoSuchElementException if the iteration has no more elements
			 */
			@Override
			public String next() throws NoSuchElementException {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				
				String element = copy[index];
				index += 1;
				return element;
			}
		};
	}
	
	/**
	 * Resizes the internal array when capacity is reached by increasing its length
	 * according to {@code growthRate} (defaulting to a 25% increase, or at least
	 * one element) and copies all existing elements into the new array.
	 */
	private void increaseSize() {
		int newSize = (int)((float)size * (1 + growthRate));
		//make sure size increases by at least 1
		if (newSize == size) {
			newSize += 1;
		}
		
		String[] newList = new String[newSize];
		
		
		for (int i = 0; i < size; i++) {
			newList[i] = list[i];
		}
		
		list = newList;
	}
   
}