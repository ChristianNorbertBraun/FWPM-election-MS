package de.fhws.fiw.fwpm.election.storage;

import de.fhws.fiw.fwpm.election.exceptions.DuplicateKeyException;
import de.fhws.fiw.fwpm.election.models.Ballot;
import de.fhws.fiw.fwpm.election.models.FWPMChoice;
import de.fhws.fiw.fwpm.election.models.VotesPerFWPM;
import de.fhws.fiw.fwpm.election.models.ZoneIds;
import de.fhws.fiw.fwpm.election.storage.tables.BallotTable;
import de.fhws.fiw.fwpm.election.storage.tables.FWPMChoicesTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by christianbraun on 23/05/16.
 */
public class BallotDaoImpl implements BallotDao, Ballot.Fields, FWPMChoice.Fields, ZoneIds {

	private Persistency persistency;

	public BallotDaoImpl(Persistency persistency) {
		this.persistency = persistency;
	}

	@Override
	public void insert(Ballot ballot) throws SQLException {

		try (Connection conn = persistency.getConnection()) {
			conn.setAutoCommit(false);
			try {
				try (PreparedStatement ballotStatement = createBallotStatement(conn, ballot);
					 PreparedStatement fwpmChoiceStatement = createFwpmChoiceStatement(conn, ballot);) {
					ballotStatement.executeUpdate();
					fwpmChoiceStatement.executeBatch();
					conn.commit();
				}

			} catch (SQLException e) {
				e.printStackTrace();
				conn.rollback();
				throw new DuplicateKeyException();
			}
		}

	}

	@Override
	public void update(Ballot ballot) throws SQLException {
		Ballot oldBallot = readBallotForStudentAndPeriod(ballot.getStudentNumber(), ballot.getPeriodId());
		ballot = setUnmutableProperties(oldBallot, ballot);

		try (Connection conn = persistency.getConnection()) {
			try {
				conn.setAutoCommit(false);
				String deleteQuery =
						"DELETE FROM " + BallotTable.Fields.TABLE_NAME +
								" WHERE " + STUDENT_NUMBER + " = ? " +
								"AND " + PERIOD_ID + " = ?";

				try (PreparedStatement prepStatement = conn.prepareStatement(deleteQuery);
					 PreparedStatement ballotStatement = createBallotStatement(conn, ballot);
					 PreparedStatement fwpmChoicesStatement = createFwpmChoiceStatement(conn, ballot)) {
					prepStatement.setString(1, ballot.getStudentNumber());
					prepStatement.setLong(2, ballot.getPeriodId());

					prepStatement.executeUpdate();
					ballotStatement.executeUpdate();
					fwpmChoicesStatement.executeBatch();

					conn.commit();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				conn.rollback();
				throw new DuplicateKeyException();
			}
		}

	}

	@Override
	public List<Ballot> readAllBallotsForElectionPeriod(long periodId) throws SQLException {
		try (Connection conn = persistency.getConnection()) {
			try {
				String readQuery =
						"SELECT * FROM " + BallotTable.Fields.TABLE_NAME +
								" LEFT JOIN " + FWPMChoicesTable.Fields.TABLE_NAME +
								" ON " + BallotTable.Fields.TABLE_NAME + "." + STUDENT_NUMBER + "=" +
								FWPMChoicesTable.Fields.TABLE_NAME + "." + STUDENT_NUMBER +
								" AND " + BallotTable.Fields.TABLE_NAME + "." + PERIOD_ID + "=" +
								FWPMChoicesTable.Fields.TABLE_NAME + "." + PERIOD_ID +
								" WHERE " + BallotTable.Fields.TABLE_NAME + "." + PERIOD_ID + "= ?";

				try (PreparedStatement prepStatement = conn.prepareStatement(readQuery)) {
					prepStatement.setLong(1, periodId);
					try (ResultSet rs = prepStatement.executeQuery()) {
						return readBallotResultSet(rs);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return Collections.emptyList();
	}

	@Override
	public List<VotesPerFWPM> countVotesPerFWPM(long periodId) throws SQLException {
		try (Connection conn = persistency.getConnection()) {
			try {
				String readQuery =
						"SELECT " + NAME + ", " + FWPM_ID + ", " + PERIOD_ID + ", COUNT(*) votes" +
								" FROM " + FWPMChoicesTable.Fields.TABLE_NAME +
								" WHERE " + PERIOD_ID + "= ?" +
								" GROUP BY " + NAME + ", " + FWPM_ID + ", " + PERIOD_ID +
								" ORDER BY votes DESC";

				try (PreparedStatement prepStatement = conn.prepareStatement(readQuery)) {
					prepStatement.setLong(1, periodId);
					try (ResultSet rs = prepStatement.executeQuery()) {
						return readVotesPerFWPMResultSet(rs);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return Collections.emptyList();
	}

	@Override
	public Ballot readBallotForStudentAndPeriod(String studentNumber, long periodId) throws SQLException {
		try (Connection conn = persistency.getConnection()) {
			try {

				String readQuery =
						"SELECT * FROM " + BallotTable.Fields.TABLE_NAME +
								" LEFT JOIN " + FWPMChoicesTable.Fields.TABLE_NAME +
								" ON " + BallotTable.Fields.TABLE_NAME + "." + STUDENT_NUMBER + "=" +
								FWPMChoicesTable.Fields.TABLE_NAME + "." + STUDENT_NUMBER +
								" AND " + BallotTable.Fields.TABLE_NAME + "." + PERIOD_ID + "=" +
								FWPMChoicesTable.Fields.TABLE_NAME + "." + PERIOD_ID +
								" WHERE " + BallotTable.Fields.TABLE_NAME + "." + PERIOD_ID + " = ?" +
								" AND " + BallotTable.Fields.TABLE_NAME + "." + STUDENT_NUMBER + " = ?";

				try (PreparedStatement prepStatement = conn.prepareStatement(readQuery)) {
					prepStatement.setLong(1, periodId);
					prepStatement.setString(2, studentNumber);
					try (ResultSet rs = prepStatement.executeQuery()) {
						List<Ballot> results = readBallotResultSet(rs);
						return results.isEmpty() == false ? results.get(0) : null;
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	@Override
	public void deleteAllOldBallotsForPeriodId(long periodId) throws SQLException {
		try (Connection conn = persistency.getConnection()) {
			try {
				String deleteQuery =
						"DELETE FROM " + BallotTable.Fields.TABLE_NAME +
								" WHERE " + PERIOD_ID + " <= ? ";
				try (PreparedStatement deleteStatement = conn.prepareStatement(deleteQuery)) {
					deleteStatement.setLong(1, periodId);
					deleteStatement.executeUpdate();
				}
			} catch (SQLException e) {
				conn.rollback();
			}
		}
	}

	protected List<VotesPerFWPM> readVotesPerFWPMResultSet(ResultSet rs) throws SQLException {
		List<VotesPerFWPM> votesPerFWPM = new ArrayList<>();

		while (rs.next()) {
			VotesPerFWPM vote = new VotesPerFWPM();
			vote.setPeriodId(rs.getLong(PERIOD_ID));
			vote.setName(rs.getString(NAME));
			vote.setFwpmId(rs.getLong(FWPM_ID));
			vote.setVotes(rs.getInt("votes"));

			votesPerFWPM.add(vote);
		}

		return votesPerFWPM;
	}

	protected List<Ballot> readBallotResultSet(ResultSet rs) throws SQLException {
		HashMap<String, Ballot> ballots = new HashMap<>();

		while (rs.next()) {

			Ballot currentBallot = new Ballot();
			currentBallot.setPeriodId(rs.getLong(BallotTable.Fields.TABLE_NAME + "." + PERIOD_ID));
			currentBallot.setStudentNumber(rs.getString(BallotTable.Fields.TABLE_NAME + "." + STUDENT_NUMBER));
			currentBallot.setLastUpdated(ZonedDateTime.ofInstant(rs.getTimestamp(LAST_UPDATED).toInstant(), TIME_ZONE));
			currentBallot.setCreated(ZonedDateTime.ofInstant(rs.getTimestamp(CREATED).toInstant(), TIME_ZONE));
			currentBallot.setFirstName(rs.getString(FIRST_NAME));
			currentBallot.setLastName(rs.getString(LAST_NAME));
			currentBallot.setCp(rs.getInt(CP));
			currentBallot.setEmail(rs.getString(EMAIL));
			currentBallot.setMajor(rs.getString(MAJOR));
			currentBallot.setSemester(rs.getInt(SEMESTER));
			currentBallot.setFacultyName(rs.getString(FACULTY_NAME));
			currentBallot.setRequiredFwpms(rs.getInt(REQUIRED_FWPMS));
			if (!ballots.containsKey(currentBallot.getStudentNumber())) {
				ballots.put(currentBallot.getStudentNumber(), currentBallot);
			}

			FWPMChoice choice = new FWPMChoice();
			choice.setPriority(rs.getInt(PRIORITY));
			choice.setFwpmId(rs.getLong(FWPM_ID));
			choice.setName(rs.getString(NAME));

			if (!rs.wasNull()) {
				ballots.get(currentBallot.getStudentNumber()).addChoice(choice);
			}
		}
		return new ArrayList<>(ballots.values());
	}


	private PreparedStatement createBallotStatement(Connection conn, Ballot ballot) throws SQLException {
		String ballotQuery = "INSERT INTO " + BallotTable.Fields.TABLE_NAME + "(" +
				PERIOD_ID + ", " +
				STUDENT_NUMBER + ", " +
				LAST_UPDATED + ", " +
				CREATED + ", " +
				FIRST_NAME + ", " +
				LAST_NAME + ", " +
				CP + ", " +
				EMAIL + ", " +
				MAJOR + ", " +
				SEMESTER + ", " +
				FACULTY_NAME + ", " +
				REQUIRED_FWPMS + " )" +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		PreparedStatement ballotStatement = conn.prepareStatement(ballotQuery);

		ballotStatement.setLong(1, ballot.getPeriodId());
		ballotStatement.setString(2, ballot.getStudentNumber());
		ballotStatement.setTimestamp(3, Timestamp.from(ballot.getLastUpdated().toInstant()));
		ballotStatement.setTimestamp(4, Timestamp.from(ballot.getCreated().toInstant()));
		ballotStatement.setString(5, ballot.getFirstName());
		ballotStatement.setString(6, ballot.getLastName());
		ballotStatement.setInt(7, ballot.getCp());
		ballotStatement.setString(8, ballot.getEmail());
		ballotStatement.setString(9, ballot.getMajor());
		ballotStatement.setInt(10, ballot.getSemester());
		ballotStatement.setString(11, ballot.getFacultyName());
		ballotStatement.setInt(12, ballot.getRequiredFwpms());

		return ballotStatement;
	}

	private PreparedStatement createFwpmChoiceStatement(Connection conn, Ballot ballot) throws SQLException {
		String fwpmChoiceQuery = "INSERT INTO " + FWPMChoicesTable.Fields.TABLE_NAME + "(" +
				STUDENT_NUMBER + ", " +
				PERIOD_ID + ", " +
				FWPM_ID + ", " +
				PRIORITY + ", " +
				NAME + " )" +
				"VALUES (?, ?, ?, ?, ?)";


		PreparedStatement fwpmChoiceStatement = conn.prepareStatement(fwpmChoiceQuery);

		for (FWPMChoice choices : ballot.getChoices()) {
			fwpmChoiceStatement.setString(1, ballot.getStudentNumber());
			fwpmChoiceStatement.setLong(2, ballot.getPeriodId());
			fwpmChoiceStatement.setLong(3, choices.getFwpmId());
			fwpmChoiceStatement.setInt(4, choices.getPriority());
			fwpmChoiceStatement.setString(5, choices.getName());
			fwpmChoiceStatement.addBatch();
		}

		return fwpmChoiceStatement;
	}

	private Ballot setUnmutableProperties(Ballot oldBallot, Ballot newBallot) {
		newBallot.setLastUpdated(ZonedDateTime.now(TIME_ZONE));
		newBallot.setCreated(oldBallot.getCreated());
		newBallot.setPeriodId(oldBallot.getPeriodId());
		newBallot.setCp(oldBallot.getCp());
		newBallot.setFirstName(oldBallot.getFirstName());
		newBallot.setLastName(oldBallot.getLastName());
		newBallot.setFacultyName(oldBallot.getFacultyName());
		newBallot.setMajor(oldBallot.getMajor());
		newBallot.setEmail(oldBallot.getEmail());
		newBallot.setSemester(oldBallot.getSemester());

		return newBallot;
	}
}
