package nl.hva.ict.ads.elections.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CustomConstituencyTest {
    private final int VOTES_S1 = 11;
    private final int VOTES_S2 = 22;
    private final int VOTES_S3 = 33;
    private final int VOTES_T1 = 1;
    private final int VOTES_T2 = 2;
    private final int VOTES_ST = 3;

    private Constituency constituency;
    private Party studentsParty, teachersParty;
    private Candidate student1, student2, student3a, student3b, teacher1, teacher2;
    private Candidate studentTeacher;
    private List<Candidate> studentCandidates;
    private List<Candidate> teacherCandidates;
    private PollingStation pollingStation1, pollingStation2;

    @BeforeEach
    public void setup() {

        this.constituency = new Constituency(0, "HvA");

        this.studentsParty = new Party(101, "Students Party");
        this.teachersParty = new Party(102, "Teachers Party");

        this.student1 = new Candidate("S.", null, "Leader", this.studentsParty);
        this.student2 = new Candidate("S.", null, "Vice-Leader", this.studentsParty);
        this.student3a = new Candidate("A.", null, "Student", this.studentsParty);
        this.student3b = new Candidate("A.", null, "Student", this.studentsParty);
        this.teacher1 = new Candidate("T.", null, "Leader", this.teachersParty);
        this.teacher2 = new Candidate("T.", null, "Vice-Leader", this.teachersParty);
        this.studentTeacher = new Candidate("A.", null, "Student", this.teachersParty);
    }

    @Test
    public void registerWorksProperly() {
        assertTrue(this.constituency.register(1, student1));
        assertFalse(this.constituency.register(2, student1));
        assertFalse(this.constituency.register(1, student3a));
        assertTrue(this.constituency.register(2, student2));
        assertTrue(this.constituency.register(1, teacher1));
    }
}
