package deque;
import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private int size;
    private T[] items;
    private int nextFirst;
    private int nextLast;
    private int leastLength = 16;


    private class ArrayDequeIterator implements Iterator<T> {
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

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        nextFirst = 0;
        nextLast = 1;
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


    /** Resizes the underlying array to the target capacity. */
    private void resize(int capacity) {
        T[] a = (T[]) new Object[capacity];
        for (int i = 0; i < size; i++) {
            a[i] = get(i);
        }
        items = a;
        nextFirst = capacity - 1;
        nextLast = capacity / 2;
    }

    @Override
    public void addFirst(T x) {
        if (size == items.length) {
            resize(2 * size);
        }
        items[nextFirst] = x;
        if (nextFirst == 0) {
            nextFirst = items.length - 1;
        } else {
            nextFirst--;
        }
        size++;
    }

    @Override
    public void addLast(T x) {
        if (size == items.length) {
            resize(2 * size);
        }
        items[nextLast] = x;
        if (nextLast == items.length - 1) {
            nextLast = 0;
        } else {
            nextLast++;
        }
        size++;
    }
    /** return the effective first index*/
    private int getFirstIndex() {
        return  Math.floorMod(nextFirst + 1, items.length);
    }
    /** return the effective last index*/
    private int getLastIndex() {
        return Math.floorMod(nextLast - 1, items.length);
    }

    /** tell if Deque need resize down */
    private boolean isNeedResizeDown() {
        if (items.length < leastLength) {
            return  false;
        }
        return (double) (size - 1) / (double) items.length < 0.25;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        StringBuilder result = new StringBuilder("");
        for (T item : this) {
            result.append(item);
            result.append(" ");
        }
        result.append("\n");
        System.out.println(result.toString());
    }

    @Override
    public T removeFirst() {
        if (isNeedResizeDown()) {
            resize(items.length / 2);
        }
        int index = getFirstIndex();
        if (items[index] == null) {
            return  null;
        }
        T result = items[index];
        items[index] = null;
        nextFirst = index;
        size--;
        return result;
    }

    @Override
    public T removeLast() {
        if (isNeedResizeDown()) {
            resize(items.length / 2);
        }
        int index = getLastIndex();
        if (items[index] == null) {
            return  null;
        }
        T result = items[index];
        items[index] = null;
        nextLast = index;
        size--;
        return result;
    }

    @Override
    public T get(int index) {
        if (index >= size || index < 0) {
            return null;
        }
        return items[(nextFirst + 1 + index) % items.length];
    }

}
