package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast2();
    }

    public static void timeGetLast2() {
        int[] tests = new int[]{1000, 2000, 4000, 8000, 16000, 32000, 64000, 128000};
        AList<Integer> Ns = new AList<>();
        AList<Integer> opCounts = new AList<>();
        AList<Double> times = new AList<>();

        for (int j : tests) {
            opCounts.addLast(10000);
            SLList<Integer> test = new SLList<>();
            for (int x = 0; x < j; x++) {
                test.addLast(1);
            }
            Stopwatch sw = new Stopwatch();
            for (int i = 0; i < 10000; i++) {
                test.getLast();
            }
            double timeInSeconds = sw.elapsedTime();
            Ns.addLast(test.size());
            times.addLast(timeInSeconds);
        }
        printTimingTable(Ns, times, opCounts);
    }

    public static void timeGetLast() {
        int M = 10000;
        int[] tests = new int[]{1000, 2000, 4000, 8000, 16000, 32000, 64000, 128000};
        AList<Integer> Ns = new AList<>();
        AList<Integer> opCounts = new AList<>();
        AList<Double> times =new AList<>();

        for (int j : tests) {
            opCounts.addLast(M);
            SLList<Integer> test = new SLList<>();
            for (int x = 0; x < j; x++) {
                test.addFirst(1);
            }
            Stopwatch sw = new Stopwatch();
            for (int x = 0; x < M; x++) {
                test.getLast();
            }
            double timeInSeconds = sw.elapsedTime();
            Ns.addLast(test.size());
            times.addLast(timeInSeconds);
        }
        printTimingTable(Ns, times, opCounts);

    }

}
