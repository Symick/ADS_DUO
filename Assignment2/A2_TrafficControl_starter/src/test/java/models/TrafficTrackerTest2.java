package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        // Test when violations are empty.
        TrafficTracker emptyTrafficTracker = new TrafficTracker();
        assertEquals(0, emptyTrafficTracker.topViolationsByCar(5).size());

        // Test when violations are smaller than the requested top size.
        assertEquals(3, trafficTracker.topViolationsByCar(5).size());


    }
}
