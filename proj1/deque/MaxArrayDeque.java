package deque;

import java.util.Collections;
import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T>{
    private Comparator comparator;
    public MaxArrayDeque(Comparator<T> c){
        comparator = c;
    }



    public T Maximizer(Comparator c) {
        int maxDex = 0;
        for (int i = 0; i < this.size(); i += 1) {
            int cmp = c.compare(this.get(i), this.get(maxDex));
            if (cmp > 0) {
                maxDex = i;
            }
        }
        return this.get(maxDex);
    }



    public T max(){
        if (isEmpty()){
            return  null;
        }
        return Maximizer(comparator);
    }

    public T max(Comparator<T> c){
        if (isEmpty()){
            return  null;
        }
        return Maximizer(c);
    }
}
