/*
 * Copyright 2018 Gerrit Meinders
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.meinders.common.util;

import java.util.*;

//A..Z = forward
//a..z = backward
//*    = sentinel
//---
//
//* -> A -> B -> C -> D -> E
//* <- A <- B <- C <- D <- E
//
//* <- A -> B
//A <- B -> C
//B <- C -> D
//C <- D -> E
//D <- E -> *
//
//---flip(B..D):
//
//* -> A -> D -> C -> B -> E
//* <- A <- D <- C <- B <- E
//
//* <- A -> D
//A <- D -> C
//D <- C -> B
//C <- B -> E
//B <- E -> *
//
//---flip(first:B .. last:D) preserving C:
//
//* <- A -> d // next = last
//E <- b -> C // previous = last.next
//b <- C -> d // unchanged
//C <- d -> A // next = first.previous
//b <- E -> * // previous = first

// flip before, restore after

public class InvertibleList<E> extends AbstractSequentialList<E> {
	public static void main(String[] args) {
		InvertibleList<Integer> list = new InvertibleList<Integer>();
		ListIterator<Integer> iterator = list.listIterator();
		iterator.add(1);
		iterator.add(2);
		iterator.add(3);
	}

	private Node<E> first;

	private Node<E> last;

	public InvertibleList() {
		first = new Node<E>();
		first.next = last;

		last = new Node<E>();
		last.previous = first;
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		ListIterator<E> result = new ListIteratorImpl(first);
		for (int i = 0; i < index; i++) {
			result.next();
		}
		return result;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	private class ListIteratorImpl implements ListIterator<E> {
		private Node<E> current;

		private IteratorState state;

		public ListIteratorImpl(Node<E> current) {
			super();
			this.current = current;

			state = new IteratorState(true);
		}

		@Override
		public void add(E e) {
			Node<E> node = new Node<E>(e);
//			boolean forward = state.get(current.forward);
			node.previous = current;
			node.next = current.next;
			current.next = node;
			node.next.previous = node;
			current = node;
		}

		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean hasPrevious() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public E next() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int nextIndex() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public E previous() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int previousIndex() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub

		}

		@Override
		public void set(E e) {
			// TODO Auto-generated method stub

		}
	}

	private static class IteratorState {
		private boolean forward;

		public IteratorState(boolean forward) {
			super();
			this.forward = forward;
		}

		public boolean get(boolean forward) {
			return this.forward == forward;
		}

		public boolean update(boolean forward) {
			return this.forward = get(forward);
		}
	}

	private static class Node<E> {
		private E value;

		private Node<E> previous;

		private Node<E> next;

		private boolean forward;

		public Node() {
			forward = true;
		}

		public Node(E value) {
			this();
			this.value = value;
		}

		public Node<E> next(IteratorState state) {
			return state.update(forward) ? previous : next;
		}

		public boolean isSentinel() {
			return (previous == null) || (next == null);
		}
	}
}
