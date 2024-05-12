package deque;

import java.util.Comparator;


public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> c) {
        this.comparator = c;
    }

    private T maximize(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }
        int maxDex = 0;
        for (int i = 0; i < this.size(); i++) {
            if (c.compare(this.get(i), this.get(maxDex)) > 0) {
                maxDex = i;
            }
        }
        return this.get(maxDex);
    }

    public T max() {
        return maximize(this.comparator);
    }

    public T max(Comparator<T> c) {
        return maximize(c);
    }
}
