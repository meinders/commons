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

/**
 * Implements a binary min-heap using an object-oriented tree structure.
 *
 * @param <T> Type of values stored in the heap.
 *
 * @author Gerrit Meinders
 */
public class TreeHeap<T> implements Heap<T> {
    private Comparator<T> comparator;

    private Node root;

    private Node firstSentinel;

    private int size;

    public TreeHeap(Comparator<T> comparator) {
        this.comparator = comparator;

        root = new Node(null);
        firstSentinel = root;
        size = 0;
    }

    public TreeHeap() {
        this(new Comparator<T>() {
            public int compare(T o1, T o2) {
                return ((Comparable<T>) o1).compareTo(o2);
            }
        });
    }

    public void print() {
        print(root, 0);
        System.out.println();
    }

    private void print(Node node, int indent) {
        for (int i = 0; i < indent; i++) {
            System.out.print(' ');
        }
        System.out.println(node);
        if (node.left != null) {
            print(node.left, indent + 2);
        }
        if (node.right != null) {
            print(node.right, indent + 2);
        }
    }

    public void add(T value) {
        firstSentinel = firstSentinel.add(value);
        size++;
    }

    public T getFirst() {
        if (root.isSentinel()) {
            throw new NoSuchElementException();
        } else {
            return root.value;
        }
    }

    public T removeFirst() {
        if (root.isSentinel()) {
            throw new NoSuchElementException();
        } else {
            T removed = root.value;
            firstSentinel = root.remove(firstSentinel.getPrevious());
            size--;
            return removed;
        }
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return root.isSentinel();
    }

    private class Node {
        private Node parent;

        private T value;

        private Node left;

        private Node right;

        public Node(Node parent) {
            this.parent = parent;
            value = null;
            left = null;
            right = null;
        }

        private boolean isSentinel() {
            return value == null;
        }

        private boolean isRoot() {
            return parent == null;
        }

        private boolean isLeaf() {
            assert !left.isSentinel() || right.isSentinel();
            return left.isSentinel();
        }

        public Node add(T value) {
            assert isSentinel() : "add is only allowed on a sentinel";

            // change sentinel into a value node
            this.value = value;
            left = new Node(this);
            right = new Node(this);

            // ensure the heap property
            if (!isRoot()) {
                parent.upHeap(this);
            }

            return getNext();
        }

        public Node remove(Node lastNode) {
            assert !isSentinel() : "remove is only allowed on a value nodes";
            assert lastNode.isLeaf() : "lastNode must be a leaf";

            // take value of last node
            value = lastNode.value;

            // change the last node into a sentinel
            lastNode.value = null;
            lastNode.left = null;
            lastNode.right = null;

            // ensure the heap property
            downHeap();

            return lastNode;
        }

        private void upHeap(Node child) {
            assert child == left || child == right : "non-child node given";
            if (comparator.compare(value, child.value) > 0) {
                T temp = value;
                value = child.value;
                child.value = temp;
                if (!isRoot()) {
                    parent.upHeap(this);
                }
            }
        }

        private void downHeap() {
            if (isSentinel() || isLeaf()) {
                // leaf reached; done
            } else if (right.isSentinel()) {
                if (comparator.compare(value, left.value) > 0) {
                    T temp = value;
                    value = left.value;
                    left.value = temp;
                    left.downHeap();
                }
            } else {
                Node smallest;
                if (comparator.compare(left.value, right.value) > 0) {
                    smallest = right;
                } else {
                    smallest = left;
                }
                T temp = value;
                value = smallest.value;
                smallest.value = temp;
                smallest.downHeap();
            }
        }

        private Node getNext() {
            if (isSentinel()) {
                return this;
            } else if (isRoot()) {
                return left; // magic! ;) finds the bottom left node
            } else if (this == parent.left) {
                return parent.right;
            } else {
                assert this == parent.right : "parent is inconsistent with node";
                return parent.getNext().left;
            }
        }

        private Node getPrevious() {
            if (this == parent.right) {
                return parent.left;
            } else if (parent.isRoot()) {
                return parent;
            } else {
                assert this == parent.left : "parent is inconsistent with node";
                return parent.getPrevious().right;
            }
        }

        @Override
        public String toString() {
            return "Node[" + value + "]";
        }
    }
}
