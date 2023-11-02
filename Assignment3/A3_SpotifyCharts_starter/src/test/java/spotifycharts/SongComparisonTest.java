package spotifycharts;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SongComparisonTest {
    private static Comparator<Song> rankingSchemeTotal, rankingSchemeDutchNational;
    Song songBYC, songKKA, songTS, songJVT, songBB;

    @BeforeAll
    static void setupClass() {
        rankingSchemeTotal = Song::compareByHighestStreamsCountTotal;
        rankingSchemeDutchNational = Song::compareForDutchNationalChart;
    }

    @BeforeEach
    void setup() {
        songBYC = new Song("Beyoncé", "CUFF IT", Song.Language.EN);
        songBYC.setStreamsCountOfCountry(Song.Country.UK,100);
        songBYC.setStreamsCountOfCountry(Song.Country.NL,40);
        songBYC.setStreamsCountOfCountry(Song.Country.BE,20);
        songTS = new Song("Taylor Swift", "Anti-Hero", Song.Language.EN);
        songTS.setStreamsCountOfCountry(Song.Country.UK,100);
        songTS.setStreamsCountOfCountry(Song.Country.DE,60);
        songKKA = new Song("Kris Kross Amsterdam", "Vluchtstrook", Song.Language.NL);
        songKKA.setStreamsCountOfCountry(Song.Country.NL,40);
        songKKA.setStreamsCountOfCountry(Song.Country.BE,30);
        songJVT = new Song("De Jeugd Van Tegenwoordig", "Sterrenstof", Song.Language.NL);
        songJVT.setStreamsCountOfCountry(Song.Country.NL,70);
        songBB = new Song("Bad Bunny", "La Coriente", Song.Language.SP);
    }

    @Test
    void ComparingItselfReturns0() {
        assertEquals(0, rankingSchemeTotal.compare(songBYC, songBYC));
        assertEquals(0, rankingSchemeDutchNational.compare(songBYC, songBYC));
    }

    @Test
    void ComparatorShouldReturnTheSameAsTheNegateOfTheOpposite(){
//    comparator.comparing(song1, song2) == -1* comparator.comparing(song2, song1)
        assertEquals(rankingSchemeTotal.compare(songBYC, songTS), -1 * rankingSchemeTotal.compare(songTS, songBYC));
        assertEquals(rankingSchemeTotal.compare(songJVT, songKKA), -1* rankingSchemeTotal.compare(songKKA, songJVT));
        assertEquals(rankingSchemeDutchNational.compare(songKKA,songBB), -1 * rankingSchemeDutchNational.compare(songBB, songKKA));
        assertEquals(rankingSchemeDutchNational.compare(songKKA, songBYC), -1 * rankingSchemeDutchNational.compare(songBYC, songKKA));
    }

}
