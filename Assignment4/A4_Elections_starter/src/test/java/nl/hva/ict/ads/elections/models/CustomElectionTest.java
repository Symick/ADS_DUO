package nl.hva.ict.ads.elections.models;

import nl.hva.ict.ads.utils.PathUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomElectionTest {
    static Election election;



    @BeforeAll
    static void setup() throws IOException, XMLStreamException {
        election = Election.importFromDataFolder(PathUtils.getResourcePath("/EML_bestanden_TK2021_HvA_UvA"));
    }

    @Test
    public void getPartyGivesCorrectParty() {
        Party vvd = new Party(1, "VVD");
        Party volt = new Party(17, "VOLT");

        assertEquals(election.getParty(1), vvd);
        assertEquals(election.getParty(17), volt);
    }
}
