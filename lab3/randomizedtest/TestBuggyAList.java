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
        for (int i = 0; i < 3; i++){
            Alist.addLast(i + 4);
            Blist.addLast(i + 4);
        }
        assertEquals(Alist.size(), Blist.size());
        assertEquals(Alist.removeLast(), Blist.removeLast());
        assertEquals(Alist.removeLast(), Blist.removeLast());
        assertEquals(Alist.removeLast(), Blist.removeLast());
    }


}
