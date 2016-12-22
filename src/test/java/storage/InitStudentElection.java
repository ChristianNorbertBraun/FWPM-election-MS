package storage;

import de.fhws.fiw.fwpm.election.exceptions.DuplicateKeyException;
import de.fhws.fiw.fwpm.election.models.Ballot;
import de.fhws.fiw.fwpm.election.models.FWPMChoice;
import de.fhws.fiw.fwpm.election.storage.BallotDao;
import de.fhws.fiw.fwpm.election.storage.DaoFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;

public class InitStudentElection {

	private BallotDao ballotDao;
	private List<Ballot> ballots;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void init() throws IOException {
		ballotDao = DaoFactory.getInstance().createBallotService();
		ballots = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			ballots.add(createBallot(i));
		}
	}

	@Ignore
	@Test
	public void createUsersForIT() throws SQLException {
		try{
			for (Ballot currentBallot : ballots) {
				ballotDao.insert(currentBallot);
			}
		}catch(DuplicateKeyException e){
			fail("Unable to create Ballot");
		}
	}

	private Ballot createBallot(int postfix) {
		Ballot testBallot = new Ballot();
		testBallot.setPeriodId(1);
		testBallot.setFirstName("Firstname " + postfix);
		testBallot.setLastName("LastName " + postfix);
		testBallot.setCp(120 + postfix);
		testBallot.setEmail(postfix + "mail@web.de");
		testBallot.setMajor("BIN");
		testBallot.setSemester(7);
		testBallot.setStudentNumber("k1234" + postfix);
		testBallot.setRequiredFwpms(2);

		FWPMChoice testChoice = new FWPMChoice();
		testChoice.setFwpmId(2);
		testChoice.setPriority(1);
		testChoice.setName("Microservices");

		FWPMChoice otherChoice = new FWPMChoice();
		otherChoice.setFwpmId(1);
		otherChoice.setPriority(2);
		otherChoice.setName("International Communication");

		testBallot.addChoice(testChoice);
		testBallot.addChoice(otherChoice);

		return testBallot;
	}

}
