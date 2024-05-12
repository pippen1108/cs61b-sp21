package deque;
import java.util.Iterator;


public class LinkedListDeque<T> implements Deque<T> {
    private class Node {
        private T item;
        private Node next;
        private Node pre;

        public Node(T i, Node p, Node n) {
            item = i;
            pre = p;
            next = n;
        }
    }

    private Node sentinel;
    private int size = 0;

    private class LinkedListDequeIterator implements Iterator<T> {
        private int wizPos = 0;

        @Override
        public boolean hasNext() {
            return wizPos < size;
        }

        @Override
        public T next() {
            T result = get(wizPos);
            wizPos++;
            return result;
        }
    }



    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        sentinel.pre = sentinel;
        sentinel.next = sentinel;
    }


    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (!(other instanceof Deque)) {
            return false;
        }
        Deque<T> otherDeque = (Deque<T>) other;
        if (this.size != otherDeque.size()) {
            return false;
        }
        for (int i = 0; i < this.size; i++) {
            if (!this.get(i).equals(otherDeque.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void addFirst(T x) {
        sentinel.next = new Node(x, sentinel, sentinel.next);
        sentinel.next.next.pre = sentinel.next;
        size++;
    }

    @Override
    public void addLast(T x) {
        sentinel.pre = new Node(x, sentinel.pre, sentinel);
        sentinel.pre.pre.next = sentinel.pre;
        size++;
    }

    public void printDeque() {
        Node next = sentinel.next;
        StringBuilder result = new StringBuilder("");
        while (next != sentinel) {
            result.append(next.item);
            result.append(" ");
            next = next.next;
        }
        result.append("\n");
        System.out.println(result.toString());
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public T removeFirst() {
        if (this.isEmpty()) {
            return null;
        }
        T result = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.pre = sentinel;
        size--;
        return result;
    }

    @Override
    public T removeLast() {
        if (this.isEmpty()) {
            return null;
        }
        T result = sentinel.pre.item;
        sentinel.pre = sentinel.pre.pre;
        sentinel.pre.next = sentinel;
        size--;
        return result;
    }

    @Override
    public T get(int index) {
        if (this.isEmpty()) {
            return null;
        }
        if (index >= size || index < 0) {
            return null;
        }
        Node current = sentinel.next;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.item;
    }

    public T getRecursive(int index) {
        return getRecursivehelper(index, sentinel.next);
    }

    private T getRecursivehelper(int index, Node node) {
        if (this.isEmpty()) {
            return null;
        }
        if (index >= size || index < 0) {
            return null;
        }
        if (index == 0) {
            return node.item;
        }
        return getRecursivehelper(index - 1, node.next);
    }

}
