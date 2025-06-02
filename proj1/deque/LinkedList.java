package deque;

import java.util.Iterator;
import java.util.Objects;

public class LinkedList <T> implements Iterable<T>{

    private class node {
        T value;
        node previous;
        node next;
        node (T item, node n, node p) {
            value = item;
            next = n;
            previous = p;
        }
        node () {}
    }

    private  class iterator implements Iterator<T>{
        int cur = 0;
        node result = sentinel;
        @Override
        public boolean hasNext() {
            return cur < size;
        }

        @Override
        public T next() {
            result = result.next;
            cur = cur + 1;
            return result.value;
        }
    }
    private int size;
    private final node sentinel = new node();

    public LinkedList(){
        size = 0;
        sentinel.next = sentinel;
        sentinel.previous = sentinel;
    };

    public void addFirst(T item) {
        size = size + 1;
        node i = new node(item, sentinel.next, sentinel);
        sentinel.next.previous = i;
        sentinel.next = i;
    }

    public void addLast(T item){
        size = size + 1;
        node i = new node(item, sentinel, sentinel.previous);
        sentinel.previous.next = i;
        sentinel.previous = i;
    }

    public boolean isEmpty(){
        return size == 0;
    }

    public int size(){
        return size;
    }

    public void printDeque(){
        StringBuilder result = new StringBuilder();
        for (T n : this) {
            result.append(n);
            result.append(" ");
        }
        result.append("\n");
        System.out.println(result);
    }

    public T removeFirst(){
        if (isEmpty()) {
            return null;
        } else {
            node first = sentinel.next;
            sentinel.next = sentinel.next.next;
            sentinel.next.previous = sentinel;
            size = size - 1;
            return first.value;
        }
    }
    public T removeLast(){
        if (isEmpty()) {
            return null;
        } else {
            node last = sentinel.previous;
            sentinel.previous = sentinel.previous.previous;
            sentinel.previous.next = sentinel;
            size = size - 1;
            return last.value;
        }
    }

    public T get(int index) {
        if (index >= size || index < 0) {
            return null;
        }
        int cur = 0;
        node first = sentinel.next;
        while (cur < index) {
            first = first.next;
            cur = cur + 1;
        }
        return first.value;
    }


    public T getRecursive(int index) {
        if (index >= size || index < 0) {
            return null;
        }
        return getRecursiveHelper(sentinel.next, index);
    }


    private   T getRecursiveHelper(node n, int i) {
        if (i == 0) {
            return n.value;
        } else {
            return getRecursiveHelper(n.next, i - 1);
        }
    }

    public Iterator<T> iterator(){
        return new iterator();
    }
    public boolean equals(Object o){
        if (this == o) {
            return true;
        }
        if (o instanceof Deque) {
            Deque<?> other = (Deque<?>) o;
            if (this.size != other.size()) {
                return false;
            }
            for (int i = 0; i < size; i++) {
                if (!Objects.equals(get(i), other.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }


}
