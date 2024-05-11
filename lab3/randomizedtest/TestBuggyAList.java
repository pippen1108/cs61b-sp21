package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import timingtest.AList;

import java.util.function.Predicate;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> Alist = new AListNoResizing<>();
        BuggyAList<Integer> Blist = new BuggyAList<>();
        for (int i = 0; i < 3; i++) {
            Alist.addLast(i + 4);
            Blist.addLast(i + 4);
        }
        assertEquals(Alist.size(), Blist.size());
        assertEquals(Alist.removeLast(), Blist.removeLast());
        assertEquals(Alist.removeLast(), Blist.removeLast());
        assertEquals(Alist.removeLast(), Blist.removeLast());
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> AL = new AListNoResizing<>();
        BuggyAList<Integer> BL = new BuggyAList<>();
        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                AL.addLast(randVal);
                BL.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                int alsize = AL.size();
                int blsizt = BL.size();
            } else if (operationNumber == 2) {
                if (AL.size() > 0) {
                    int algetLast = AL.getLast();
                    int blgetLast = BL.getLast();
                }
            } else if (operationNumber == 3) {
                if (AL.size() > 0) {
                    int alremoveLast = AL.removeLast();
                    int blremoveLast = BL.removeLast();
                }
            }
        }
    }
}