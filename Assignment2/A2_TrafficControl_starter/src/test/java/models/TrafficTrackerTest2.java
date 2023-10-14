package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TrafficTrackerTest2 {
    private final static String VAULT_NAME = "/2023-09";

    TrafficTracker trafficTracker;

    @BeforeEach
    public void setup() {

        trafficTracker = new TrafficTracker();

        trafficTracker.importCarsFromVault(VAULT_NAME + "/cars.txt");

        trafficTracker.importDetectionsFromVault(VAULT_NAME + "/detections");
    }

    @Test

    public void topViolationsByCarCheck() {
        // When violations are empty.
        TrafficTracker emptyTrafficTracker = new TrafficTracker();
        assertEquals(0, emptyTrafficTracker.topViolationsByCar(5).size());

        // When top violations are greater than violations size.
        assertThrows(IndexOutOfBoundsException.class, () -> trafficTracker.topViolationsByCar(100));

        // When top violations are negative.
        assertThrows(IllegalArgumentException.class, () -> trafficTracker.topViolationsByCar(-1));

        assertEquals(5, trafficTracker.topViolationsByCar(5).size());
        assertEquals(0, trafficTracker.topViolationsByCar(0).size());
    }

    @Test
    public void topViolationsByCityCheck() {
        // When violations are empty.
        TrafficTracker emptyTrafficTracker = new TrafficTracker();
        assertEquals(0, emptyTrafficTracker.topViolationsByCity(5).size());

        // When top violations are greater than violations size.
        assertThrows(IndexOutOfBoundsException.class, () -> trafficTracker.topViolationsByCity(100));

        // When top violations are negative.
        assertThrows(IllegalArgumentException.class, () -> trafficTracker.topViolationsByCity(-1));

        assertEquals(5, trafficTracker.topViolationsByCity(5).size());
        assertEquals(0, trafficTracker.topViolationsByCity(0).size());

    }
    @Test
    public void finesAggregateCorrectly() {
        TrafficTracker emptyTrafficTracker = new TrafficTracker();
        //empty trafficTracker should have no violation to fine.
        assertEquals(0,  emptyTrafficTracker.calculateTotalFines());
        assertEquals(186090, trafficTracker.calculateTotalFines());
    }
}
