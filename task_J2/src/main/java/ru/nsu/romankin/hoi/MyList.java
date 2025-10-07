package ru.nsu.romankin.hoi;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyList<T> implements Iterable<T>{

    private Node<T> head;
    private int size;

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T data = current.data;
                current = current.next;
                return data;
            }
        };
    }

    private static class Node<T> {
        T data;
        Node<T> next;
        final Object lock = new Object();

        Node(T data) {
            this.data = data;
            next = null;
        }
    }

    public MyList() {
        head = null;
        size = 0;
    }
    public int size() {
        return size;
    }
    public void add(T data) {
        Node<T> node = new Node<>(data);
        synchronized (head != null ? head.lock : this) {
            node.next = head;
            head = node;
            size++;
        }
    }

    public synchronized T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }

    public void swap(int index) {
        if (index < 0 || index >= size - 1) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        Node<T> first, second, prev = null;
        synchronized (this) {
            if (index == 0) {
                first = head;
                second = head.next;
            } else {
                prev = head;
                for (int i = 0; i < index - 1; i++) {
                    prev = prev.next;
                }
                first = prev.next;
                second = first.next;
            }
        }

        Object lock1 = first.lock;
        Object lock2 = second.lock;
        Object lockPrev = prev != null ? prev.lock : null;

        if (lockPrev != null) {
            synchronized (lockPrev) {
                synchronized (lock1) {
                    synchronized (lock2) {
                        doSwap(prev, first, second, false);
                    }
                }
            }
        } else {
            synchronized (lock1) {
                synchronized (lock2) {
                    doSwap(null, first, second, true);
                }
            }
        }
    }

    private void doSwap(Node<T> prev, Node<T> first, Node<T> second, boolean isHead) {
        first.next = second.next;
        second.next = first;
        if (isHead) {
            head = second;
        } else {
            prev.next = second;
        }
    }

}
