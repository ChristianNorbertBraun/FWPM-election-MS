package de.fhws.fiw.fwpm.election.storage;

import de.fhws.fiw.fwpm.election.models.Ballot;
import de.fhws.fiw.fwpm.election.models.VotesPerFWPM;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by christianbraun on 18/05/16.
 */
public interface BallotDao {
	public void insert(Ballot ballot) throws SQLException;

	public void update(Ballot ballot) throws SQLException;

	public List<Ballot> readAllBallotsForElectionPeriod(long periodId) throws SQLException;

	public List<VotesPerFWPM> countVotesPerFWPM(long periodId) throws SQLException;

	public Ballot readBallotForStudentAndPeriod(String studentNumber, long periodId) throws SQLException;

	public void deleteAllOldBallotsForPeriodId(long periodId) throws SQLException;

}
