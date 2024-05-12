package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.*;

public class ArrayDequeTest {
    @org.junit.jupiter.api.Test
    public void addFirstTestBasic() {
        ArrayDeque<String> lld1 = new ArrayDeque<>();

        lld1.addFirst("back"); // after this call we expect: ["back"]


        lld1.addFirst("middle"); // after this call we expect: ["middle", "back"]


        lld1.addFirst("front"); // after this call we expect: ["front", "middle", "back"]

    }

    @org.junit.jupiter.api.Test
    /** In this test, we use only one assertThat statement. IMO this test is just as good as addFirstTestBasic.
     *  In other words, the tedious work of adding the extra assertThat statements isn't worth it. */
    public void addLastTestBasic() {
        ArrayDeque<String> lld1 = new ArrayDeque<>();

        lld1.addLast("front"); // after this call we expect: ["front"]
        lld1.addLast("middle"); // after this call we expect: ["front", "middle"]
        lld1.addLast("back"); // after this call we expect: ["front", "middle", "back"]

    }

    @org.junit.jupiter.api.Test
    /** This test performs interspersed addFirst and addLast calls. */
    public void addFirstAndAddLastTest() {
        ArrayDeque<Integer> lld1 = new ArrayDeque<>();

         /* I've decided to add in comments the state after each call for the convenience of the
            person reading this test. Some programmers might consider this excessively verbose. */
        lld1.addLast(0);   // [0]
        lld1.addLast(1);   // [0, 1]
        lld1.addFirst(-1); // [-1, 0, 1]
        lld1.addLast(2);   // [-1, 0, 1, 2]
        lld1.addFirst(-2); // [-2, -1, 0, 1, 2]

    }

    @Test
    /** This test is for get */
    public  void  getTest(){
        ArrayDeque<Integer> lld1 = new ArrayDeque<>();
        assertThat(lld1.get(1)).isEqualTo(null);
        lld1.addLast(1);
        lld1.addLast(2);
        lld1.addLast(3);
        assertThat(lld1.get(1)).isEqualTo(2);
        assertThat(lld1.get(-1)).isEqualTo(null);
        assertThat(lld1.get(4)).isEqualTo(null);

    }

    @Test
    public void randomizedTest() {
        Deque<Integer> AL = new LinkedListDeque<>();
        Deque<Integer> BL = new ArrayDeque<>();
        int N = 100;
        for (int i = 0; i < N; i += 1) {
            // addLast
            int randVal = StdRandom.uniform(0, 100);
            BL.addLast(randVal);
        }
        N = 100;
        for (int i = 0; i < N; i += 1) {
            // addLast
            int randVal = StdRandom.uniform(0, 100);
            BL.removeLast();
        }
    }

}
