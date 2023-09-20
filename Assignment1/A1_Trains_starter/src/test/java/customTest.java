
import models.PassengerWagon;
import models.Wagon;
import org.junit.jupiter.api.*;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class customTest {
    Wagon passengerWagon1, passengerWagon2, passengerWagon3, passengerWagon4;

    @BeforeEach
    public void setup() {
        Locale.setDefault(Locale.ENGLISH);
        passengerWagon1 = (Wagon) (Object) new PassengerWagon(8001, 36);
        passengerWagon2 = (Wagon) (Object) new PassengerWagon(8002, 18);
        passengerWagon3 = (Wagon) (Object) new PassengerWagon(8003, 48);
        passengerWagon4 = (Wagon) (Object) new PassengerWagon(8004, 44);
    }
    public static void checkRepresentationInvariant(Wagon wagon) {
        // TODO check the nextWagon and previousWagon representation invariants of wagon
        assertTrue(!wagon.hasNextWagon() || wagon == wagon.getNextWagon().getPreviousWagon(),
                String.format("Wagon %s should be the previous wagon of its next wagon, if any", wagon));
        assertTrue(!wagon.hasPreviousWagon() || wagon == wagon.getPreviousWagon().getNextWagon(),
                String.format("Wagon %s should be the next wagon of its previous wagon, if any", wagon));

        //assertTrue(false);
    }
    @AfterEach
    public void checkRepresentationInvariants() {
        checkRepresentationInvariant(passengerWagon1);
        checkRepresentationInvariant(passengerWagon2);
        checkRepresentationInvariant(passengerWagon3);
        checkRepresentationInvariant(passengerWagon4);
    }

    @Test
    public void T01_WagonReverseShouldNotThrowException() {
        assertDoesNotThrow(() -> {passengerWagon1.reverseSequence();});
    }
}
