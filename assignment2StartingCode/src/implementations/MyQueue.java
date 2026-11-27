package implementations;

import exceptions.EmptyQueueException;
import utilities.Iterator;
import utilities.QueueADT;

/**
 * A doubly linked list-backed implementation of the {@link QueueADT} interface,
 * for use in CPRG 304 at SAIT.
 * <p>
 * This class delegates all storage to an internal {@link MyDLL}, treating
 * index {@code 0} as the front of the queue and the last index as the back.
 * {@link #enqueue(String)} appends to the back; {@link #dequeue()} and
 * {@link #peek()} access the front. A queue follows a
 * <strong>First-In, First-Out (FIFO)</strong> ordering policy: the first
 * element enqueued is the first one returned.
 * </p>
 *
 * <p><strong>Design Note – String-Specific Implementation:</strong><br>
 * This class is intentionally designed to store {@link String} elements only.
 * A fully reusable, type-safe design would use Java generics (e.g.,
 * {@code MyQueue<E>}), allowing any object type to be stored in the queue.
 * Generic programming is outside the scope of CPRG 304 and has therefore
 * been omitted. All element fields, parameters, and return types are fixed
 * as {@link String} throughout this class and its implementations.
 * </p>
 */
public class MyQueue implements QueueADT
{
	/* The underlying doubly linked list storing the {@link String} elements in this queue. */
	private MyDLL list;
	/* Constructs an empty {@code MyQueue} backed by a new {@link MyDLL} instance. */
	public MyQueue() {
		list = new MyDLL();
	}
	
	/**
	 * Adds the specified {@link String} element to the back of this queue.
	 *
	 * @param toAdd the {@link String} element to enqueue
	 * @throws NullPointerException if {@code toAdd} is {@code null}
	 */
	@Override
	public void enqueue(String toAdd) throws NullPointerException {
		if (toAdd == null) {
			throw new NullPointerException("input cannot be null");
		}
		list.add(toAdd);
	}

	/**
	 * Removes and returns the {@link String} element at the front of this queue.
	 *
	 * @return the {@link String} element removed from the front of this queue
	 * @throws EmptyQueueException if this queue is empty
	 */
	@Override
	public String dequeue() throws EmptyQueueException {
		if (list.isEmpty()) {
			throw new EmptyQueueException("queue is empty");
		}
		
		return list.remove(0);
	}

	/**
	 * Retrieves, without removing, the {@link String} element at the front of this queue.
	 *
	 * @return the {@link String} element at the front of this queue
	 * @throws EmptyQueueException if this queue is empty
	 */
	@Override
	public String peek() throws EmptyQueueException {
		if (list.isEmpty()) {
			throw new EmptyQueueException("queue is empty");
		}
		return list.get(0);
	}
	
	/**
	 * Removes all elements from this queue, leaving it empty.
	 */
	@Override
	public void dequeueAll() {
		list.clear();
		
	}
	
	/**
	 * Checks whether this queue contains no elements.
	 *
	 * @return {@code true} if this queue is empty, {@code false} otherwise
	 */
	@Override
	public boolean isEmpty() {
		if (list.size() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * Checks whether this queue contains the specified {@link String} element.
	 *
	 * @param toFind the {@link String} element whose presence is to be tested
	 * @return {@code true} if this queue contains the specified element, {@code false} otherwise
	 * @throws NullPointerException if {@code toFind} is {@code null}
	 */
	@Override
	public boolean contains(String toFind) throws NullPointerException {
		if (toFind == null) {
			throw new NullPointerException("input cannot be null");
		}
		return list.contains(toFind);
	}

	/**
	 * Returns the 1-based position of the specified {@link String} element from the
	 * front of this queue, or {@code -1} if it is not found.
	 *
	 * @param toFind the {@link String} element to search for
	 * @return the 1-based position from the front of the queue, or {@code -1} if absent
	 */
	@Override
	public int search(String toFind) {
		if (list.indexOf(toFind) == -1) {
			return -1;
		}
		return (list.indexOf(toFind) + 1);
	}

	/**
	 * Returns an {@link Iterator} over the elements in this queue in proper
	 * sequence from front to back.
	 *
	 * @return an {@link Iterator} over the elements in this queue
	 */
	@Override
	public Iterator iterator() {
		return list.iterator();
	}

	/**
	 * Compares this queue with the specified {@link QueueADT} for equality by checking
	 * whether both queues have the same size and identical elements in the same order.
	 *
	 * @param that the {@link QueueADT} to compare with this queue
	 * @return {@code true} if both queues contain equal elements in the same order,
	 *         {@code false} otherwise
	 */
	@Override
	public boolean equals(QueueADT that) {
		if (list.size() != that.size()) {
			return false;
		}
		Iterator list1 = list.iterator();
		Iterator list2 = that.iterator();
		while (list1.hasNext()) {
			if (!list1.next().equals(list2.next())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Copies all elements of this queue into the provided array in front-to-back order,
	 * or into a newly allocated {@link String} array if the provided array is too small.
	 *
	 * @param holder the array into which the elements of this queue are to be stored
	 * @return an array containing all elements of this queue in front-to-back order
	 * @throws NullPointerException if {@code holder} is {@code null}
	 */
	@Override
	public String[] toArray(String[] holder) throws NullPointerException {
		if (holder == null) {
			throw new NullPointerException("input cannot be null");
		}
		return list.toArray(holder);
	}

	/**
	 * Returns the current number of elements in this queue.
	 *
	 * @return the number of elements currently stored in this queue
	 */
	@Override
	public int size() {
		return list.size();
	}
    
}