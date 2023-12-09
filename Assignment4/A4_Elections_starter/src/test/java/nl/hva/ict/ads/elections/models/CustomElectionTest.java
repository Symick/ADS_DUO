package nl.hva.ict.ads.elections.models;

import nl.hva.ict.ads.utils.PathUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomElectionTest {

    private Election election;
    private Party party;
    private Candidate candidate;
    private Constituency constituency;

    @BeforeEach
    void setup() {
        election = new Election("Test Election");

        party = new Party(1, "Test Party");

        candidate = new Candidate("Test", null, "Candidate");
        party.addOrGetCandidate(candidate);

        election.addParty(party);
        constituency = new Constituency(1, "Test Constituency");
        election.addConstituency(constituency);

        PollingStation pollingStation = new PollingStation("TestId", "Test Polling Station", "Test Zip Code");
        constituency.add(pollingStation);
    }

    @Test
    public void getPartyGivesCorrectParty() throws IOException, XMLStreamException {
        election = Election.importFromDataFolder(PathUtils.getResourcePath("/EML_bestanden_TK2021_HvA_UvA"));

        Party vvd = new Party(1, "VVD");
        Party volt = new Party(17, "VOLT");

        assertEquals(election.getParty(1), vvd);
        assertEquals(election.getParty(17), volt);
    }

    @Test
    void prepareSummaryShouldReturnCorrectSummaryForExistingParty() {
//         Add a registration for the candidate in the constituency.
        constituency.register(1, candidate);

        // TODO: Party is somehow null.

        String expectedSummary = "\nSummary of Party{id=1, name='Test Party'}:\n" +
                "Total number of candidates = 1\n" +
                "Candidates: [Candidate{partyId=1, name='Test Candidate'}]\n" +
                "Total number of registrations = 1\n" +
                "Number of registrations by constituency: {Constituency{id=1, name='Test Constituency'}=1}";

        assertEquals(expectedSummary, election.prepareSummary(1));
    }

    @Test
    void prepareSummaryShouldReturnNotFoundMessageForNonExistingParty() {
        Election election = new Election("Test Election");
        String expectedMessage = "Party with id 1 not found";
        assertEquals(expectedMessage, election.prepareSummary(1));
    }

    @Test
    void prepareSummaryShouldReturnCorrectSummaryForExistingElection() {
        String expectedSummary = "\nElection summary of Test Election:\n\n" +
                "1 Participating parties:\n" +
                "[Party{id=1, name='Test Party'}]\n" +
                "Total number of constituencies = 1\n" +
                "Total number of polling stations = 1\n" +
                "Total number of candidates = 1\n" +
                "Different candidates with duplicate names across different parties are:\n" +
                "[]\n\n" +
                "Overall election results by party percentage:\n" +
                "[]\n" +
                "\nPolling stations in Amsterdam Wibautstraat area with zip codes 1091AA-1091ZZ:\n" +
                "[]\n" +
                "Top 10 election results by party percentage in Amsterdam area with zip codes 1091AA-1091ZZ:\n" +
                "[]\n" +
                "Most representative polling station is:\n" +
                "PollingStation{id='TestId',zipCode='Test Polling Station',name='Test Zip Code'}\n" +
                "[]";

        assertEquals(expectedSummary, election.prepareSummary());
    }

    @Test
    void prepareSummaryShouldReturnEmptySummaryForEmptyElection() {
        Election election = new Election("Test Election");

        String expectedSummary = "\nElection summary of Test Election:\n\n" +
                "0 Participating parties:\n" +
                "[]\n" +
                "Total number of constituencies = 0\n" +
                "Total number of polling stations = 0\n" +
                "Total number of candidates = 0\n" +
                "Different candidates with duplicate names across different parties are:\n" +
                "[]\n\n" +
                "Overall election results by party percentage:\n" +
                "[]\n" +
                "\nPolling stations in Amsterdam Wibautstraat area with zip codes 1091AA-1091ZZ:\n" +
                "[]\n" +
                "Top 10 election results by party percentage in Amsterdam area with zip codes 1091AA-1091ZZ:\n" +
                "[]\n" +
                "Most representative polling station is:\n" +
                "No most representative polling station found.\n";

        assertEquals(expectedSummary, election.prepareSummary());
    }

}
