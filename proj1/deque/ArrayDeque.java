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
    /** return the effective first index*/
    private int getFirstIndex(){
        return  Math.floorMod(nextFirst + 1, items.length);
    }
    /** return the effective last index*/
    private int getLastIndex(){
        return Math.floorMod(nextLast - 1, items.length);
    }

    public boolean isEmpty() {
        return  size == 0;
    }


    public int size() {
        return size;
    }


    public T removeFirst() {
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
        return items[(nextFirst + 1 + index) % items.length];
    }

}
