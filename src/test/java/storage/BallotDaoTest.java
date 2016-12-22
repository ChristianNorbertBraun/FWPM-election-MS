package storage;

import de.fhws.fiw.fwpm.election.exceptions.DuplicateKeyException;
import de.fhws.fiw.fwpm.election.models.Ballot;
import de.fhws.fiw.fwpm.election.models.FWPMChoice;
import de.fhws.fiw.fwpm.election.models.PeriodConfig;
import de.fhws.fiw.fwpm.election.models.VotesPerFWPM;
import de.fhws.fiw.fwpm.election.network.PeriodConfigClient;
import de.fhws.fiw.fwpm.election.storage.BallotDao;
import de.fhws.fiw.fwpm.election.storage.DaoFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Created by christianbraun on 18/05/16.
 */
public class BallotDaoTest {

	private DaoFactory daoFactory;
	private BallotDao ballotDao;
	private Ballot testBallot;
	private PeriodConfig config;

	@Rule
	public ExpectedException thrown= ExpectedException.none();

	@Before
	public void init() throws IOException {
		PeriodConfigClient client = new PeriodConfigClient();
		client.setBase_uri("http://docker:8080/electionConfig/api");

		config = client.getLatestPeriodConfig();
		testBallot = new Ballot();
		testBallot.setPeriodId(config != null ? config.getPeriodId() : 0);
		testBallot.setStudentNumber("k345" + (int) (Math.random() * 100000));
//		testBallot.setStudentNumber("k31491");
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

		daoFactory = DaoFactory.getInstance();
		ballotDao = daoFactory.createBallotService();
	}

	@Test
	public void createBallotTest() throws SQLException {
		try{
			ballotDao.insert(testBallot);
		}catch(DuplicateKeyException e){
			fail("Unable to create Ballot");
		}
	}

	@Test
	public void readVotesPerFWPMTest() {
		try {
			List<VotesPerFWPM> votes =  ballotDao.countVotesPerFWPM(0);
			for(VotesPerFWPM vote: votes) {
				System.out.println(vote.getName() + " " + vote.getFwpmId() + " " + vote.getVotes());
			}
		} catch (SQLException ex) {
			fail("Unable to read count of votes");
		}
	}

	@Test
	public void readAllBallotsForPeriod() throws SQLException {
		List<Ballot> ballots = ballotDao.readAllBallotsForElectionPeriod(testBallot.getPeriodId());

		assertThat(String.format("Reading all ballots of period %s returned an empty collection",
				testBallot.getPeriodId()),
				not(is(ballots.isEmpty())));
	}

	@Test
	@Ignore
	public void tryUpdateBallotWithWrongChoices() throws SQLException {
		List<Ballot> ballots = ballotDao.readAllBallotsForElectionPeriod(testBallot.getPeriodId());
		if(ballots.size() != 0){
			Ballot ballot = ballots.get(0);
			FWPMChoice choice = new FWPMChoice();
			choice.setFwpmId(3);
			choice.setPriority(1);
			choice.setName("TestName");
			ballot.addChoice(choice);
			thrown.expect(DuplicateKeyException.class);
			ballotDao.update(ballot);
		}
	}

	@Test
	public void tryUpdateBallot() throws SQLException {
		List<Ballot> ballots = ballotDao.readAllBallotsForElectionPeriod(testBallot.getPeriodId());
		if(ballots.size() != 0){
			int index = (int)(Math.random() * ballots.size());
			Ballot ballot = ballots.get(index);
			if(ballot.getChoices().size() != 0) {
				ballot.getChoices().get(0).setName("Now Other Choice");
				ballotDao.update(ballot);
			}

			Ballot updatedBallot = ballotDao.readBallotForStudentAndPeriod(ballot.getStudentNumber(), ballot.getPeriodId());

			assertThat(ballot, is(updatedBallot));
		}
	}

	@Test
	public void tryDeleteBallots() {
		try {
			ballotDao.deleteAllOldBallotsForPeriodId(0);
		} catch (SQLException e) {
			fail("Unable to delete periodIds");
		}
	}
}
