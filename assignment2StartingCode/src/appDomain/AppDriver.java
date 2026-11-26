package appDomain;

import implementations.MyQueue;
import implementations.MyStack;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import exceptions.EmptyQueueException;


public class AppDriver {

	private static final String OPENERS = "({[<";
	private static final String CLOSERS = ")}]>";
	
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
 
	private static String encode(char c, int lineNumber, int column) {
		return c + ":" + lineNumber + ":" + column;
	}
 
	private static char charOf(String entry) {
		return entry.charAt(0);
	}
 
	private static int lineOf(String entry) {
		return Integer.parseInt(entry.substring(2, entry.indexOf(':', 2)));
	}
 
	private static int columnOf(String entry) {
		return Integer.parseInt(entry.substring(entry.indexOf(':', 2) + 1));
	}
 
	private static boolean matches(char open, char close) {
		int i = OPENERS.indexOf(open);
		return i != -1 && CLOSERS.indexOf(close) == i;
	}
 
	private static void drain(MyQueue q, MyQueue reported) throws EmptyQueueException {
		while (!q.isEmpty()) {
			recordError(reported, q.dequeue());
		}
	}

	private static void recordError(MyQueue reported, String entry) {
		if (!reported.contains(entry)) {
			reported.enqueue(entry);
		}
	}
 
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
