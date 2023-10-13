package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        String topViolationsByCar = trafficTracker.topViolationsByCar(8).toString();
        String expected = "[27-IP-IX/null/241, AUQ-42-V/null/219, IX-TN-99/null/218, 007-JQ-3/null/214, MAS-16-X/null/214, 044-AR-9/null/211, 575-DU-8/null/207, 419-TH-8/null/206]";
        assertEquals(expected, topViolationsByCar);
    }
}
