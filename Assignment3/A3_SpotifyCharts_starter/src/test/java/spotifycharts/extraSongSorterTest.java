package spotifycharts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class extraSongSorterTest {
    private SongSorter songSorter;
    private List<Song> fewSongs;
    private Comparator<Song> rankingScheme = Song::compareByHighestStreamsCountTotal;

    @BeforeEach
    void setup() {
        ChartsCalculator chartsCalculator = new ChartsCalculator(2L);
        this.songSorter = new SongSorter();
        fewSongs = new ArrayList(chartsCalculator.registerStreamedSongs(23));
    }

    @Test
    void PartitionWorksCorrectly() {
        int partition = this.songSorter.partition(fewSongs, rankingScheme, 0, fewSongs.size() - 1);

        for (int i = 0; i < partition; i++) {
            assertTrue(rankingScheme.compare(fewSongs.get(i), fewSongs.get(partition)) <= 0);
        }
        for (int i = partition + 1; i < fewSongs.size(); i++) {
            assertTrue(rankingScheme.compare(fewSongs.get(i), fewSongs.get(partition)) > 0);
        }
    }
}
