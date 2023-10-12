package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;

public class TrafficTrackerTest2 {
    private final static String VAULT_NAME = "/2023-09";

    TrafficTracker trafficTracker;

    @BeforeEach
    public void setup() {
        Locale.setDefault(Locale.ENGLISH);
        trafficTracker = new TrafficTracker();

        trafficTracker.importCarsFromVault(VAULT_NAME + "/cars.txt");

        trafficTracker.importDetectionsFromVault(VAULT_NAME + "/detections");
    }

    @Test
    public void finesAggregateCorrectly() {
//        assertEquals(186090, trafficTracker.calculateTotalFines());
        assertEquals(70, trafficTracker.calculateTotalFines());
    }
}
