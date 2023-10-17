package spotifycharts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class SongSorterTest {
    private SongSorter songSorter;
    private List<Song> fewSongs;
    private List<Song> manySongs;
    private Comparator<Song> rankingScheme = Song::compareByHighestStreamsCountTotal;
    private List<Song> heap;
    private Comparator<Song> heapComparator = Comparator.comparing(Song::getTitle);

    @BeforeEach
    void setup() {
        ChartsCalculator chartsCalculator = new ChartsCalculator(2L);
        this.songSorter = new SongSorter();
        fewSongs = new ArrayList(chartsCalculator.registerStreamedSongs(23));
        manySongs = new ArrayList(chartsCalculator.registerStreamedSongs(250));
    }

    @Test
    void selInsBubSortAndCollectionSortYieldSameOrder() {
        customSortAndCollectionSortResultInSameOrder(songSorter::selInsBubSort);
    }

    @Test
    void quickSortAndCollectionSortYieldSameOrder() {
        customSortAndCollectionSortResultInSameOrder(songSorter::quickSort);
    }

    private void customSortAndCollectionSortResultInSameOrder(BiFunction<List<Song>,Comparator,List<Song>> sorterMethod) {
        List<Song> fewSortedSongs = new ArrayList<>(fewSongs);
        Collections.shuffle(fewSortedSongs);
        List<Song> manySortedSongs = new ArrayList<>(manySongs);
        Collections.shuffle(manySortedSongs);

        sorterMethod.apply(fewSortedSongs, Comparator.comparing(Song::getTitle));
        fewSongs.sort(Comparator.comparing(Song::getTitle));
        String difference = findFirstDifference(fewSongs, fewSortedSongs, Comparator.comparing(Song::getTitle), 3);
        assertNull(difference, difference);

        sorterMethod.apply(manySortedSongs, Comparator.comparing(Song::getArtist));
        manySongs.sort(Comparator.comparing(Song::getArtist));
        difference = findFirstDifference(manySongs, manySortedSongs, Comparator.comparing(Song::getArtist), 3);
        assertNull(difference, difference);

        sorterMethod.apply(fewSortedSongs, rankingScheme);
        fewSongs.sort(rankingScheme);
        difference = findFirstDifference(fewSongs, fewSortedSongs, rankingScheme, 3);
        assertNull(difference, difference);

        sorterMethod.apply(manySortedSongs, rankingScheme);
        manySongs.sort(rankingScheme);
        difference = findFirstDifference(manySongs, manySortedSongs, rankingScheme, 3);
        assertNull(difference, difference);
    }


    @Test
    void topsHeapSortAndCollectionSortYieldSameOrder() {
        List<Song> fewSortedSongs = new ArrayList<>(fewSongs);
        Collections.shuffle(fewSortedSongs);
        List<Song> manySortedSongs = new ArrayList<>(manySongs);
        Collections.shuffle(manySortedSongs);

        songSorter.topsHeapSort(5, fewSortedSongs, Comparator.comparing(Song::getTitle));
        fewSongs.sort(Comparator.comparing(Song::getTitle));
        assertEquals(fewSongs.subList(0,5).stream().map(Song::getTitle).collect(Collectors.toList()),
                fewSortedSongs.subList(0,5).stream().map(Song::getTitle).collect(Collectors.toList()));

        songSorter.topsHeapSort(1, manySortedSongs, rankingScheme);
        manySongs.sort(rankingScheme);
        assertEquals(manySongs.get(0), manySortedSongs.get(0));

        songSorter.topsHeapSort(25, manySortedSongs, rankingScheme);
        assertEquals(manySongs.subList(0,25), manySortedSongs.subList(0,25));
    }

    public static <E> String findFirstDifference(List<E> expected, List<E> actual, Comparator<E> ranker, int displayLength) {
        if (expected.size() != actual.size()) {
            return String.format("Expected list with size=%d, got %d", expected.size(), actual.size());
        }
        for (int i = 0; i < expected.size(); i++) {
            if (ranker.compare(actual.get(i), expected.get(i)) != 0) {
                int subListEnd = Integer.min(i+displayLength, expected.size());
                return String.format("Expected items[%d..%d] = %s,\n   got: %s", i, subListEnd-1,
                        expected.subList(i,subListEnd), actual.subList(i,subListEnd));
            }
        }
        return null;
    }

    static final int MAX_SMALL_HEAP_SIZE = 25;
    @Test
    void swimShouldBuildASmallHeapCorrectly() {

        this.heap = new ArrayList<>(this.manySongs.subList(0,MAX_SMALL_HEAP_SIZE));
        Set<Song> preHeap;

        for (int heapSize = 1; heapSize <= MAX_SMALL_HEAP_SIZE; heapSize++) {
            // capture a copy of the current songs in the heap
            preHeap = Set.copyOf(this.heap.subList(0, heapSize));

            // swim last item in the heap: heap[heapSize-1]
            this.songSorter.heapSwim(this.heap, heapSize, this.heapComparator);

            this.checkZeroBasedHeapCondition(this.heap, heapSize, this.heapComparator);

            // check whether all songs are still in the heap
            assertEquals(preHeap, Set.copyOf(this.heap.subList(0, heapSize)),
                    "The set of songs should not change when changing the order in Swim; Error when heapSize="+heapSize);
        }

        //System.out.println(this.heap);
    }

    @Test
    void sinkShouldSortASmallHeapCorrectly() {

        // first build the heap correctly
        this.swimShouldBuildASmallHeapCorrectly();

        Set<Song> preHeap;

        for (int heapSize = MAX_SMALL_HEAP_SIZE; heapSize > 1; heapSize--) {
            // capture a copy of the current songs in the heap
            preHeap = Set.copyOf(this.heap.subList(0, heapSize));

            // fix last item in the heap: heap[heapSize-1]
            Collections.swap(this.heap, 0, heapSize-1);

            // the remaining heap is now one shorter
            this.songSorter.heapSink(this.heap, heapSize-1, this.heapComparator);
            this.checkZeroBasedHeapCondition(this.heap, heapSize-1, this.heapComparator);

            // check whether all songs are still in the heap
            assertEquals(preHeap, Set.copyOf(this.heap.subList(0, heapSize)),
                    "The set of songs should not change when changing the order in Sink; Error when heapSize="+heapSize);
        }

        //System.out.println(this.heap);
    }

    public <E> void checkZeroBasedHeapCondition(List<E> items, int heapSize, Comparator<E> comparator) {
        for (int i = 1; i < heapSize; i++) {
            int parentIndex = (i-1)/2;
            assertTrue(comparator.compare(items.get(parentIndex),items.get(i)) <= 0,
                    String.format("heap[%d]='%s' should preceed heap[%d]='%s' in zero-based heap of size=%d ",
                            parentIndex, items.get(parentIndex), i, items.get(i), heapSize));
        }
    }
}
