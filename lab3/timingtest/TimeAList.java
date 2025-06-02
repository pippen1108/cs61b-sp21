package timingtest;
import edu.princeton.cs.algs4.Stopwatch;
import edu.princeton.cs.introcs.In;

/**
 * Created by hug.
 */
public class TimeAList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %18s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.print("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeAListConstruction();
    }

    public static  void timeAListConstruction2() {
        //construct the three alist and use the printTimingTable to print out
        int [] ns = new int[]{1000, 2000, 4000, 8000, 16000, 32000, 64000, 128000};
        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> opCount = new AList<>();

        for (int n : ns) {
            Ns.addLast(n);

            AList<Integer> test = new AList<>();
            int op = 0;
            Stopwatch sw = new Stopwatch();
            while (op < n) {
                test.addLast(0);
                op += 1;
            }
            times.addLast(sw.elapsedTime());

            opCount.addLast(op);
        }
        printTimingTable(Ns, times, opCount);

    }

    public static void timeAListConstruction() {
        int[] tests = new int[]{1000, 2000, 4000, 8000, 16000, 32000, 64000, 128000, 1000000};
        AList<Integer> Ns = new AList<>();
        AList<Integer> opCounts = new AList<>();
        AList<Double> times = new AList<>();

        for (int j : tests) {
            opCounts.addLast(j);
            AList<Integer> test = new AList<>();
            Stopwatch sw = new Stopwatch();
            for (int x = 0; x < j; x++) {
                test.addLast(1);
            }
            double timeInSeconds = sw.elapsedTime();
            Ns.addLast(test.size());
            times.addLast(timeInSeconds);
        }
        printTimingTable(Ns, times, opCounts);

    }
}
