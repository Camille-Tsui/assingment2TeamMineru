package appDomain;

import implementations.MyQueue;
import implementations.MyStack;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import exceptions.EmptyQueueException;

/**
 * Command-line driver that checks a text file for syntax errors in mismatched brackets.
 * 
 * <p>
 * All brackets are tracked with line and column in which they are found. 
 * Openers are pushed onto stack, and closers are matched against stack to remove correctly paired brackets.
 * Un-paired brackets are added to queues, reconciled, and printed in order indicating line and bracket which caused the error.
 * </p>
 * 
 * @author Iris
 */
public class AppDriver {

	// All valid opening brackets, indexes of each matches closers.
	private static final String OPENERS = "({[<";

	// All valid closing brackets indexes in same order as openers.
	private static final String CLOSERS = ")}]>";

	/**
	 * Program entry point. Reads the file named by the single command-line
	 * argument, scans it character by character, and reports every bracket
	 * that is unmatched or mismatched.
	 * <p>
	 * Processing happens in three phases:
	 * 	<ol>
	 * 		<li>Scans each line, pushing openers to stack and closers to handleClosing function</li>
	 * 		<li>Move openers still on stack into error queue</li>
	 * 		<li>Reconcile error queue against extras queue, record remaining entries as confirmed errors</li>
	 * 	</ol>
	 * Prints all errors to command line using printResults function
	 * </p>
	 *
	 * @param One command line argument containing the file path to be checked
	 * 
	 * @author Iris
	 */
	public static void main(String[] args) {
		
		if (args.length != 1) {
			System.out.println("Usage: java appDomain.AppDriver <filename>");
			return;
		}
 
		MyStack stack = new MyStack();
		MyQueue errorQ = new MyQueue();
		MyQueue extrasQ = new MyQueue();
		MyQueue reported = new MyQueue();
 
		try (BufferedReader reader = new BufferedReader(new FileReader(args[0]))) {
 
			String line;
			int lineNumber = 0;
 
			while ((line = reader.readLine()) != null) {
				lineNumber++;
 
				for (int i = 0; i < line.length(); i++) {
					char c = line.charAt(i);
 
					if (OPENERS.indexOf(c) != -1) {
						stack.push(encode(c, lineNumber, i));
					} else if (CLOSERS.indexOf(c) != -1) {
						handleClosing(c, lineNumber, i, stack, errorQ, extrasQ, reported);
					}
				}
			}
 
			while (!stack.isEmpty()) {
				errorQ.enqueue(stack.pop());
			}
 
			while (!errorQ.isEmpty() || !extrasQ.isEmpty()) {
 
				if (errorQ.isEmpty() || extrasQ.isEmpty()) {
					drain(errorQ, reported);
					drain(extrasQ, reported);
					break;
				}
 
				if (matches(charOf(errorQ.peek()), charOf(extrasQ.peek()))) {
					recordError(reported, errorQ.dequeue());
					recordError(reported, extrasQ.dequeue());
				} else {
					recordError(reported, errorQ.dequeue());
				}
			}
 
		} catch (IOException e) {
			System.out.println("Could not read file '" + args[0] + "': " + e.getMessage());
			return;
		} catch (EmptyQueueException e) {
			System.out.println("Unexpected empty queue: " + e.getMessage());
			return;
		}
		printResults(reported, args[0]);
	}
 
	/**
	 * Processes closing brackets checking against stack of openers, and sorting accordingly
	 * <p>
	 * Cases handled in following order:
	 * 	<ol>
	 * 		<li>If top of stack is matching opener, pop stack</li>
	 * 		<li>If front of error queue is matching opener, dequeue</li>
	 * 		<li>If stack empty, enqueue into error queue.</li>
	 * 		<li>
	 * 			Search stack for matching opener. If one exists, pop entries down to and including match.
	 * 			Skipped openers sent to error queue and final report queue. If none exists, enqueue closer in extras queue.
	 * 		</li>
	 * 	</ol>
	 * </p>
	 *
	 * @param closing bracket character
	 * @param line number where closer was found
	 * @param column where closer was found
	 * @param stack of openers still awaiting closer
	 * @param queue of brackets flagged as potential errors
	 * @param queue of closers that have no matching opener
	 * @param queue of brackets confirmed as errors, with duplicates removed
	 * 
	 * @throws EmptyQueueException if queue operation attempted on empty queue
	 * 
	 * @author Iris
	 */
	private static void handleClosing(char c, int lineNumber, int column, MyStack stack, MyQueue errorQ, MyQueue extrasQ, MyQueue reported) throws EmptyQueueException {
		if (!stack.isEmpty() && matches(charOf(stack.peek()), c)) {
			stack.pop();
			return;
		}
		
		if (!errorQ.isEmpty() && matches(charOf(errorQ.peek()), c)) {
			errorQ.dequeue();
			return;
		}
		
		if (stack.isEmpty()) {
			errorQ.enqueue(encode(c, lineNumber, column));
			return;
		}
 
		boolean found = false;
		String[] items = stack.toArray(new String[stack.size()]);
		for (int i = 0; i < items.length; i++) {
			if (matches(charOf(items[i]), c)) {
				found = true;
				break;
			}
		}
 
		if (found) {
			while (true) {
				String top = stack.pop();
				if (matches(charOf(top), c)) {
					break;
				}
				errorQ.enqueue(top);
				recordError(reported, top);
			}
		} else {
			extrasQ.enqueue(encode(c, lineNumber, column));
		}
	}
 
	/**
	 * Encodes bracket and its position within file into single string
	 *
	 * @param bracket character
	 * @param line number where bracket appears
	 * @param column where bracket appears
	 * 
	 * @return encoded string
	 * 
	 * @author Iris
	 */
	private static String encode(char c, int lineNumber, int column) {
		return c + ":" + lineNumber + ":" + column;
	}
 
	/**
	 * Extracts bracket character from encoded string
	 *
	 * @param encoded string for bracket
	 * @return bracket character
	 * 
	 * @author Iris
	 */
	private static char charOf(String entry) {
		return entry.charAt(0);
	}
 
	/**
	 * Extracts line number from encoded string
	 *
	 * @param encoded string for bracket
	 * @return line number of given bracket
	 * 
	 * @throws NumberFormatException 
	 * 
	 * @author Iris
	 */
	private static int lineOf(String entry) {
		return Integer.parseInt(entry.substring(2, entry.indexOf(':', 2)));
	}
 
	/**
	 * Extracts column number from encoded string
	 *
	 * @param encoded string for bracket
	 * @return column number of given bracket
	 * 
	 * @throws NumberFormatException
	 * 
	 * @author Iris
	 */
	private static int columnOf(String entry) {
		return Integer.parseInt(entry.substring(entry.indexOf(':', 2) + 1));
	}
 
	/**
	 * Determines matching bracket pairs
	 *
	 * @param opening bracket to check
	 * @param closing bracket to check
	 * 
	 * @return boolean indicating whether brackets pair
	 * 
	 * @author Iris
	 */
	private static boolean matches(char open, char close) {
		int i = OPENERS.indexOf(open);
		return i != -1 && CLOSERS.indexOf(close) == i;
	}
 
	/**
	 * Empties queue, recording all entries as confirmed errors
	 *
	 * @param queue to empty
	 * @param reports queue
	 * 
	 * @throws EmptyQueueException
	 */
	private static void drain(MyQueue q, MyQueue reported) throws EmptyQueueException {
		while (!q.isEmpty()) {
			recordError(reported, q.dequeue());
		}
	}

	/**
	 * Adds entry to confirmed errors queue unless excluding duplicates
	 *
	 * @param reports queue 
	 * @param bracket to record
	 * 
	 * @author Iris
	 */
	private static void recordError(MyQueue reported, String entry) {
		if (!reported.contains(entry)) {
			reported.enqueue(entry);
		}
	}
 
	/**
	 * Prints final report of confirmed errors. If no errors recorded, prints "no errors" message. Else, prints message indicating file with errors.
	 * Errors sorted into ascending order prior to printing. Prints each error including line and offending character.
	 *
	 * @param errors queue
	 * @param file being checked
	 * 
	 * @author Iris
	 */
	private static void printResults(MyQueue reported, String filename) {
		if (reported.isEmpty()) {
			System.out.println("File: " + filename + " has no errors.");
			return;
		} else {
			System.out.println("Errors in file " + filename + ":");
		}
 
		String[] raw = reported.toArray(new String[reported.size()]);
		int n = raw.length;
		int[] lines = new int[n];
		int[] cols = new int[n];
		char[] brackets = new char[n];
		for (int i = 0; i < n; i++) {
			lines[i] = lineOf(raw[i]);
			cols[i] = columnOf(raw[i]);
			brackets[i] = charOf(raw[i]);
		}

		for (int i = 1; i < n; i++) {
			int l = lines[i];
			int c = cols[i];
			char b = brackets[i];
			int j = i - 1;
			while (j >= 0 && (lines[j] > l || (lines[j] == l && cols[j] > c))) {
				lines[j + 1] = lines[j];
				cols[j + 1] = cols[j];
				brackets[j + 1] = brackets[j];
				j--;
			}
			lines[j + 1] = l;
			cols[j + 1] = c;
			brackets[j + 1] = b;
		}
 
		for (int i = 0; i < n; i++) {
			System.out.println("Line " + lines[i] + ": Syntax error on \"" + brackets[i] + "\".");
		}
	}
}
