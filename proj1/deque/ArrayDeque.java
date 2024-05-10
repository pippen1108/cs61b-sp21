package deque;
import java.lang.Math;
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

    /** Resizes the underlying array to the target capacity. */
    private void resize(int capacity) {
        T[] a = (T[]) new Object[capacity];
        for (int i = 0; i < size; i++) {
            a[i] = get(i);
        }
        items = a;
        nextFirst = capacity;
        nextLast = capacity / 2;
    }


    public void addFirst(T x) {
        if (size == items.length) {
            resize(2 * size);
        }
        items[nextFirst] = x;
        if (nextFirst == 0){
            nextFirst = items.length - 1;
        }else {
            nextFirst--;
        }
        size++;
    }


    public void addLast(T x) {
        if (size == items.length) {
            resize(2 * size);
        }
        items[nextLast] = x;
        if (nextLast == items.length - 1){
            nextLast = 0;
        }else {
            nextLast++;
        }
        size++;
    }
    /** return the effective first index*/
    private int getFirstIndex(){
        return  Math.floorMod(nextFirst + 1, items.length);
    }
    /** return the effective last index*/
    private int getLastIndex(){
        return Math.floorMod(nextLast - 1, items.length);
    }

    /** tell if Deque need resize down */
    private boolean isNeedResizeDown(){
        if (items.length < 16) {
            return  false;
        }
        return (double) size - 1 / (double) items.length < 0.25;
    }

    public boolean isEmpty() {
        return  size == 0;
    }


    public int size() {
        return size;
    }


    public T removeFirst() {
        if (isNeedResizeDown()){
            resize(items.length / 2);
        }
        int index = getFirstIndex();
        if (items[index] == null){
            return  null;
        }
        T result = items[index];
        items[index] = null;
        nextFirst = index;
        size--;
        return result;
    }


    public T removeLast() {
        if (isNeedResizeDown()){
            resize(items.length / 2);
        }
        int index = getLastIndex();
        if (items[index] == null){
            return  null;
        }
        T result = items[index];
        items[index] = null;
        nextLast = index;
        size--;
        return result;
    }


    public T get(int index) {
        if (index >= size || index < 0) {
            return null;
        }
        return items[(nextFirst + 1 + index) % items.length];
    }

}
