package spotifycharts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.EfficiencyTable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EfficiencyTest {
    private final int ITERATIONS = 10;
    private final int MAX_TIME = 20_000; // 20 seconds in milliseconds
    private final int BIGGEST_INPUT_SIZE = 10000;
    private final double NANO_TO_MILLI = 1E-6;

    long started;

    private SongSorter songSorter;
    private Comparator<Song> rankingScheme = Song::compareByHighestStreamsCountTotal;
    //set up table
    private EfficiencyTable<Double> results = new EfficiencyTable<>(5, 23, Double::sum, (a, b) -> a * b);

    private List<Song> toBeSorted;

    @BeforeEach
    void setup() {
        this.songSorter = new SongSorter();
    }

    @Test
    void efficiencyTest() {
        for (int i = 1; i <= ITERATIONS; i++) {
            System.out.println("this is iteration -" + i);
            //set up list



            //keeps track of last duration, so after it takes longer than 20 seconds you can stop executing
            double lastBubSelIntDuration = 0;
            double lastQuickDuration = 0;
            double lastTopHeapDuration = 0;


            //loop over all input sizes and track on which row you are.
            for (int n = 100, row = 0; n < BIGGEST_INPUT_SIZE; n = n * 2, row++) {
                //break out of iteration if all sorting algoritmes take longer than 20 seconds
                if (lastQuickDuration >= MAX_TIME && lastBubSelIntDuration >= MAX_TIME && lastTopHeapDuration >= MAX_TIME) break;


                //setup list with n size
                ChartsCalculator charts = new ChartsCalculator(i);
                 List<Song> songs = charts.registerStreamedSongs(n);
                //add the n column
                results.add(0, row, (double) n);

                //copy list
                toBeSorted = new ArrayList<>(songs);
                //bubble insertion or selection sort
                System.gc();
                if (lastBubSelIntDuration <= MAX_TIME) {
                    started = System.nanoTime();
                    songSorter.selInsBubSort(toBeSorted, rankingScheme);
                    lastBubSelIntDuration = NANO_TO_MILLI * (System.nanoTime() - started);
                    results.add(1, row, lastBubSelIntDuration);
                }


                //quicksort
                toBeSorted.clear();
                toBeSorted = new ArrayList<>(songs);
                System.gc();
                if (lastQuickDuration <= MAX_TIME) {
                    started = System.nanoTime();
                    songSorter.quickSort(toBeSorted, rankingScheme);
                    lastQuickDuration = NANO_TO_MILLI * (System.nanoTime() - started);
                    results.add(2, row, lastQuickDuration);
                }

                //heapsort
                toBeSorted.clear();
                toBeSorted = new ArrayList<>(songs);
                System.gc();
                if (lastTopHeapDuration <= MAX_TIME) {
                    started = System.nanoTime();
                    songSorter.topsHeapSort(100, toBeSorted, rankingScheme);
                    lastTopHeapDuration = NANO_TO_MILLI * (System.nanoTime() - started);
                    results.add(3, row, lastTopHeapDuration);
                }

                toBeSorted = new ArrayList<>(songs);
                System.gc();
                System.out.println(toBeSorted.subList(0,5));
                System.out.println(toBeSorted.size());
                started = System.nanoTime();
                toBeSorted.sort(rankingScheme);
                double duration = NANO_TO_MILLI * (System.nanoTime() - started);
                System.out.printf("duration was %.2f msec \n", duration);
                results.add(4, row, duration);
            }
        }

        results.multiplyAll(1.0/ITERATIONS);

        System.out.printf("\nSummary of %d repeats:\n", ITERATIONS);
        System.out.println("N; T_selInsBub; T_quickSort; T_topsHeapSort");
        System.out.println(results.csv("%.2f"));
    }
}
