package md;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;

public class Memento<T extends Cloneable> {

	private T originator;
	private Deque<T> memoryStack;

	public Memento(T object) {
		this(object, new LinkedList<T>());
	}

	public Memento(T object, int numberOfTraceableSteps) {
		this(object, new LimitedStack<T>(numberOfTraceableSteps));
	}

	private Memento(T originator, Deque<T> memoryStack) {
		this.originator = originator;
		this.memoryStack = memoryStack;
	}

	public T getCurrent() {
		return originator;
	}

	public void save() {
		try {
			// Since Java has the method Object.clone() protected by default, it
			// must be called via reflection.
			// It is made sure, that this call works by restricting the type T
			// to extend the Cloneable interface.
			Method cloneMethod = this.originator.getClass().getMethod("clone");
			@SuppressWarnings("unchecked")
			T clonedObj = (T) cloneMethod.invoke(this.originator);
			memoryStack.push(clonedObj);
		} catch (Exception e) {
			throw new RuntimeException(
					"Problem while cloning object of class " + originator.getClass()
							+ ". It must implement \"public void clone()\" in order to work in a Memento. Make sure, the method is declared public.",
					e);
		}
	}

	public T undo() {
		this.originator = memoryStack.pop();
		return this.originator;
	}

	public void printHistory() {
		System.out.println(memoryStack);
	}

	public static class LimitedStack<E> extends LinkedList<E> {

		private static final long serialVersionUID = 1L;
		private int maxSize;

		public LimitedStack(int maxSize) {
			this.maxSize = maxSize;
		}

		@Override
		public void push(E e) {
			while (this.size() >= maxSize)
				super.removeLast();
			super.push(e);
		}

		@Override
		public boolean add(E e) {
			throw new UnsupportedOperationException("Please only use the stack mathods (push, pop, peek).");
		}

		@Override
		public boolean addAll(Collection<? extends E> collection) {
			throw new UnsupportedOperationException("Please only use the stack mathods (push, pop, peek).");
		}

		@Override
		public void addLast(E e) {
			throw new UnsupportedOperationException("Please only use the stack mathods (push, pop, peek).");
		}

		@Override
		public boolean offer(E e) {
			throw new UnsupportedOperationException("Please only use the stack mathods (push, pop, peek).");
		}

		@Override
		public boolean offerLast(E e) {
			throw new UnsupportedOperationException("Please only use the stack mathods (push, pop, peek).");
		}

		@Override
		public boolean offerFirst(E e) {
			throw new UnsupportedOperationException("Please only use the stack mathods (push, pop, peek).");
		}

		@Override
		public E remove() {
			throw new UnsupportedOperationException("Please only use the stack mathods (push, pop, peek).");
		}

		@Override
		public E remove(int index) {
			throw new UnsupportedOperationException("Please only use the stack mathods (push, pop, peek).");
		}

		@Override
		public E removeLast() {
			throw new UnsupportedOperationException("Please only use the stack mathods (push, pop, peek).");
		}

		@Override
		public boolean remove(Object o) {
			throw new UnsupportedOperationException("Please only use the stack mathods (push, pop, peek).");
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			throw new UnsupportedOperationException("Please only use the stack mathods (push, pop, peek).");
		}

		@Override
		public boolean removeFirstOccurrence(Object o) {
			throw new UnsupportedOperationException("Please only use the stack mathods (push, pop, peek).");
		}

		@Override
		public boolean removeLastOccurrence(Object o) {
			throw new UnsupportedOperationException("Please only use the stack mathods (push, pop, peek).");
		}

		@Override
		public void add(int index, E element) {
			throw new UnsupportedOperationException("Please only use the stack mathods (push, pop, peek).");
		}

		@Override
		public boolean addAll(int index, Collection<? extends E> c) {
			throw new UnsupportedOperationException("Please only use the stack mathods (push, pop, peek).");
		}

		@Override
		public E pollLast() {
			throw new UnsupportedOperationException("Please only use the stack mathods (push, pop, peek).");
		}

		@Override
		public E poll() {
			throw new UnsupportedOperationException("Please only use the stack mathods (push, pop, peek).");
		}

		@Override
		public E pollFirst() {
			throw new UnsupportedOperationException("Please only use the stack mathods (push, pop, peek).");
		}

	}
}
