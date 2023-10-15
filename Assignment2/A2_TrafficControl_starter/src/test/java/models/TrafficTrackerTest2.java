package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TrafficTrackerTest2 {
    private final static String VAULT_NAME = "/2023-09";

    TrafficTracker trafficTracker;
    Car volvo1, daf1;
    Violation v1, v2, v3, v4;

    @BeforeEach
    public void setup() {
        volvo1 = new Car("1-TTT-01", 5, Car.CarType.Truck, Car.FuelType.Diesel, LocalDate.of(2009,1,31));
        daf1 = new Car("1-CCC-01", 5, Car.CarType.Coach, Car.FuelType.Diesel, LocalDate.of(2009,1,31));


        //violations
        v1 = new Violation(volvo1, "Amsterdam");
        v1.setOffencesCount(5);
        v2 = new Violation(volvo1, "Rotterdam");
        v2.setOffencesCount(2);
        v3 = new Violation(daf1, "Amsterdam");
        v3.setOffencesCount(4);
        v4 = new Violation(daf1, "Rotterdam");
        v4.setOffencesCount(9);

        trafficTracker = new TrafficTracker();

        trafficTracker.importCarsFromVault(VAULT_NAME + "/cars.txt");

        trafficTracker.importDetectionsFromVault(VAULT_NAME + "/detections");
    }

    @Test

    public void topViolationsByCarCheck() {
        // When violations are empty.
        TrafficTracker emptyTrafficTracker = new TrafficTracker();
        assertEquals(0, emptyTrafficTracker.topViolationsByCar(5).size());

        //adding violations to empty list
        emptyTrafficTracker.getViolations().add(v1);
        emptyTrafficTracker.getViolations().add(v2);
        emptyTrafficTracker.getViolations().add(v3);
        emptyTrafficTracker.getViolations().add(v4);

        //get topViolationList
        List<Violation> top = emptyTrafficTracker.topViolationsByCar(2);

        assertEquals("1-CCC-01/null/13", top.get(0).toString());
        assertEquals("1-TTT-01/null/7", top.get(1).toString());

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

        //adding violations to empty list
        emptyTrafficTracker.getViolations().add(v1);
        emptyTrafficTracker.getViolations().add(v2);
        emptyTrafficTracker.getViolations().add(v3);
        emptyTrafficTracker.getViolations().add(v4);

        //get topViolationList
        List<Violation> top = emptyTrafficTracker.topViolationsByCity(2);

        assertEquals("null/Rotterdam/11", top.get(0).toString());
        assertEquals("null/Amsterdam/9", top.get(1).toString());



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
        //adding violations

        emptyTrafficTracker.getViolations().add(v1);
        emptyTrafficTracker.getViolations().add(v2);
        emptyTrafficTracker.getViolations().add(v3);
        emptyTrafficTracker.getViolations().add(v4);

        assertEquals(630, emptyTrafficTracker.calculateTotalFines());
        assertEquals(186090, trafficTracker.calculateTotalFines());
    }
}
