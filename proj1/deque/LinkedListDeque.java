package deque;

import java.util.ArrayList;
import java.util.List;

public class LinkedListDeque<T> {
    private class Node {
        public T item;
        public Node next;
        public Node pre;

        public Node(T i, Node p, Node n) {
            item = i;
            pre = p;
            next = n;
        }
    }

    private Node sentinel;
    private int size = 0;


    public LinkedListDeque(){
        sentinel = new Node(null, null, null);
        sentinel.pre = sentinel;
        sentinel.next = sentinel;
    }


    public void addFirst(T x) {
        sentinel.next = new Node(x, sentinel, sentinel.next);
        sentinel.next.next.pre = sentinel.next;
        size++;
    }

    public void addLast(T x) {
        sentinel.pre = new Node(x, sentinel.pre, sentinel);
        sentinel.pre.pre.next = sentinel.pre;
        size++;
    }


    public List<T> toList() {
        List<T> returnList = new ArrayList<>();
        Node next = sentinel.next;
        while (next != sentinel){
            returnList.add(next.item);
            next = next.next;
        }
        return returnList;
    }


    public boolean isEmpty() {
        return sentinel.next == sentinel;
    }


    public int size() {
        return size;
    }


    public T removeFirst() {
        if (this.isEmpty()){
            return null;
        }
        T result = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.pre = sentinel;
        return result;
    }


    public T removeLast() {
        if (this.isEmpty()){
            return null;
        }
        T result = sentinel.pre.item;
        sentinel.pre = sentinel.pre.pre;
        sentinel.pre.next = sentinel;
        return result;
    }


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

    public T getRecursivehelper(int index, Node node){
        if (this.isEmpty()) {
            return null;
        }
        if (index >= size || index < 0) {
            return null;
        }
        if (index == 0){
            return node.item;
        }
        return getRecursivehelper(index - 1, node.next);
    }

}
