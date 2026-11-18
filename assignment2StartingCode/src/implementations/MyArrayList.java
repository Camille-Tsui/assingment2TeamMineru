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
public class MyArrayList implements ListADT
{
	String[] list = new String[10];
	int size = 0;
	
	@Override
	public int size() {
//		int counter = 0;
//		for (String element : list) {
//			if (element == null) {
//				return counter;
//			}
//			counter += 1;
//		}
//		return 0;
		
		return size;
	}

	@Override
	public void clear() {
		for (int i = size() - 1; i >= 0; i--) {
			list[i] = null;
			size -= 1;
		}
	}

	@Override
	public boolean add(int index, String toAdd) throws NullPointerException, IndexOutOfBoundsException {
		if (toAdd == null) {
			throw new NullPointerException();
		}
		if (index < 0 || index > size()) {
			throw new IndexOutOfBoundsException();
		}
		
		for (int i = size; i > index; i--) {
			list[i] = list[i-1];
		}
		
		list[index] = toAdd;
		size += 1;
		return true;
	}

	@Override
	public boolean add(String toAdd) throws NullPointerException {
		if (toAdd == null) {
			throw new NullPointerException();
		}
		if (list.length == size()) {
			//add if size too small
		}
		list[size()] = toAdd;
		size += 1;
		return true;
	}

	@Override
	public boolean addAll(ListADT toAdd) throws NullPointerException {
		if (toAdd == null) {
			throw new NullPointerException();
		}
		//add if size is too small

		Iterator it = toAdd.iterator();
		while (it.hasNext()) {
			add(it.next());
		}
		return true;
	}

	@Override
	public String get(int index) throws IndexOutOfBoundsException {
		if (index > size() - 1) {
			throw new IndexOutOfBoundsException();
		}
		return list[index];
	}

	@Override
	public String remove(int index) throws IndexOutOfBoundsException {
		if (index > size() - 1 || index < 0) {
			throw new IndexOutOfBoundsException();
		}
		
		//overwrite index and shifting elements back down
		String element = list[index];
		for (int i = index; i < size(); i++) {
			list[i] = list[i + 1];
		}
		size -= 1;
		return element;
	}

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

	@Override
	public String set(int index, String toChange) throws NullPointerException, IndexOutOfBoundsException {
		if (toChange == null) {
			throw new NullPointerException();
		}
		if (index < 0 || index >= size()) {
			throw new IndexOutOfBoundsException();
		}

		
		String oldElement = list[index];
		list[index] = toChange;
		
		return oldElement;
	}

	@Override
	public boolean isEmpty() {
		if (size <= 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean contains(String toFind) throws NullPointerException {
		if (toFind == null) {
			throw new NullPointerException();
		}
		
		for (int i = 0; i < size(); i++) {
			if (list[i] == toFind) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int indexOf(String toFind) throws NullPointerException {
		if (toFind == null) {
			throw new NullPointerException();
		}
		
		for (int i = 0; i < size(); i++) {
			if (list[i] == toFind) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public String[] toArray(String[] toHold) throws NullPointerException {
		if (toHold == null) {
			throw new NullPointerException();
		}
		
		//check if size large enough
		if (toHold.length < size()) {
			toHold = new String[size()];
		}
		
		//copy elements
		for (int i = 0; i < size(); i++) {
			toHold[i] = list[i];
		}
		
		return toHold;
	}

	@Override
	public Iterator iterator() {
		String[] copy = toArray(new String[size()]);

		
		return new Iterator() {
			int index = 0;

			@Override
			public boolean hasNext() {
				if (index < copy.length) {
					return true;
				}
				return false;
			}

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
	
	//should increase list size by 25%
	private void IncreaseSize() {
		
	}
   
}