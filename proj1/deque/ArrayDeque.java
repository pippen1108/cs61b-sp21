package deque;

import java.util.ArrayList;
import java.util.List;

public class ArrayDeque <T> {
    private int size;
    private T[] items;
    private int nextFirst;
    private int nextLast;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        nextFirst = 0;
        nextLast = 1;
    }

    public void addFirst(T x) {
        items[nextFirst] = x;
        if (nextFirst == 0){
            nextFirst = items.length - 1;
        }else {
            nextFirst--;
        }
        size++;
    }


    public void addLast(T x) {
        items[nextLast] = x;
        if (nextLast == items.length - 1){
            nextLast = 0;
        }else {
            nextLast++;
        }
        size++;
    }
    /** return the effect first index*/
    private int getFirstIndex(){
        return 0;
    }


    public boolean isEmpty() {
        if (nextFirst == items.length - 1){
            return items[0] == null;
        }
        return items[nextFirst + 1] == null;
    }


    public int size() {
        return size;
    }


    public T removeFirst() {
        T resul = items[nextFirst + 1];
        items[nextFirst + 1] = null;
        nextFirst++;
        return null;
    }


    public T removeLast() {
        T resul = items[nextLast - 1];
        items[nextLast - 1] = null;
        nextLast--;
        return null;
    }


    public T get(int index) {
        return items[nextFirst + 1 + index];
    }

}
