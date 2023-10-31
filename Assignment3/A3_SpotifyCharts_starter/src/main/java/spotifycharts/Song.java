package spotifycharts;

import java.util.EnumMap;
import java.util.Map;

public class Song {

    public enum Language {
        EN, // English
        NL, // Dutch
        DE, // German
        FR, // French
        SP, // Spanish
        IT, // Italian
    }

    public enum Country {
        UK, // United Kingdom
        NL, // Netherlands
        DE, // Germany
        BE, // Belgium
        FR, // France
        SP, // Spain
        IT  // Italy
    }

    private final String artist;
    private final String title;
    private final Language language;

    // Use a map to store stream information per country. These datastructures ensures quick retrieval
    private Map<Country, Integer> streams;


    /**
     * Constructs a new instance of Song based on given attribute values
     */
    public Song(String artist, String title, Language language) {
        this.artist = artist;
        this.title = title;
        this.language = language;

        this.streams = new EnumMap<>(Country.class) {{

        }};
    }

    /**
     * Sets the given streams count for the given country on this song
     * @param country
     * @param streamsCount
     */
    public void setStreamsCountOfCountry(Country country, int streamsCount) {
        this.streams.put(country, streamsCount);
    }

    /**
     * retrieves the streams count of a given country from this song
     * @param country
     * @return
     */
    public int getStreamsCountOfCountry(Country country) {
        return this.streams.getOrDefault(country,0); // replace by the proper amount
    }
    /**
     * Calculates/retrieves the total of all streams counts across all countries from this song
     * @return
     */
    public int getStreamsCountTotal() {
        //maps the int collection to an integer stream and return the sum of the stream.
        return this.streams.values().stream().mapToInt(Integer::intValue).sum();
    }


    /**
     * compares this song with the other song
     * ordening songs with the highest total number of streams upfront
     * @param other     the other song to compare against
     * @return  negative number, zero or positive number according to Comparator convention
     */
    public int compareByHighestStreamsCountTotal(Song other) {
        return Integer.compare(other.getStreamsCountTotal(), this.getStreamsCountTotal());
    }

    /**
     * compares this song with the other song
     * ordening all Dutch songs upfront and then by decreasing total number of streams
     * @param other     the other song to compare against
     * @return  negative number, zero or positive number according to Comparator conventions
     */
    public int compareForDutchNationalChart(Song other) {
        //first set Dutch songs upfront.
        if (this.language == Language.NL && other.language != Language.NL) {
            return  -1;
        }
        if (this.language != Language.NL && other.language == Language.NL) {
            return 1;
        }

        return this.compareByHighestStreamsCountTotal(other);
    }


    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public Language getLanguage() {
        return language;
    }


    @Override
    public String toString() {
        return String.format("%s/%s{%s}(%d)", this.getArtist(), this.getTitle(), this.getLanguage(), this.getStreamsCountTotal());
    }
}
