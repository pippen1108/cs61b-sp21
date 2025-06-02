package deque;

import java.util.Comparator;

public class MaxArray<T> extends Array<T>{
    Comparator<T> cmp;
    public MaxArray(Comparator<T> c) {
        cmp = c;
    }

    public T max() {
        return max(cmp);
    }

    public T max(Comparator<T> c) {
        if (isEmpty()) return null;
        T max = get(0);
        int icmp;
        for (int i = 1; i < size(); i++){
            icmp = c.compare(max, get(i));
            if (icmp < 0) {
                max = get(i);
            }
        }
        return max;
    }
}
