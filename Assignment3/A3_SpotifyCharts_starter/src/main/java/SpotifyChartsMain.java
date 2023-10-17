import spotifycharts.ChartsCalculator;

public class SpotifyChartsMain {
    public static void main(String[] args) {
        System.out.println("Welcome to the HvA Spotify Charts Calculator\n");

        ChartsCalculator chartsCalculator = new ChartsCalculator(20060423L);
        chartsCalculator.registerStreamedSongs(263);
        chartsCalculator.showResults();
    }
}
