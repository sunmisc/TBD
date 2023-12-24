package sunmisc.db.dynamo;

import sunmisc.db.models.Animal;
import sunmisc.db.models.Live;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public class QAnimal implements Animal {
    private static final String SELECT_TYPE =
            "SELECT type FROM animals WHERE id = ?";
    private static final String UPDATE_DEATH_DATE = """
             UPDATE animals SET date_of_death = ?
             WHERE id = ? AND date_of_death IS NULL
             """;

    private final long id;
    private final Connection connection;

    public QAnimal(Connection connection, long id) {
        this.connection = connection;
        this.id = id;
    }

    @Override
    public long id() {
        return id;
    }

    @Override
    public Live live() {
        return new QLive(this, connection);
    }

    @Override
    public String type() throws SQLException {
        try (var ps = connection.prepareStatement(SELECT_TYPE)) {

            ps.setLong(1, id);

            try (ResultSet result = ps.executeQuery()) {
                if (result.next())
                    return result.getString(1);
                throw new IllegalStateException("type is empty");
            }
        }
    }

    @Override
    public void die() throws Exception {
        try (var ps = connection.prepareStatement(UPDATE_DEATH_DATE)) {
            ps.setDate(1, Date.valueOf(LocalDate.now()));
            ps.setLong(2, id());
            if (ps.executeUpdate() == 0) {
                throw new RuntimeException(
                        "failed to die the animal, it may have already been died");
            }
        }
    }


    private record QLive(
            Animal animal, Connection connection
    ) implements Live {
        private static final String SELECT_DATE_OF_BIRTH_BY_ID =
                "SELECT date_of_birth FROM animals WHERE id = ?";
        private static final String SELECT_DATE_OF_DEATH_BY_ID =
                "SELECT date_of_death FROM animals WHERE id = ?";

        @Override
        public LocalDateTime dateOfBirth() {
            try (var ps = connection.prepareStatement(SELECT_DATE_OF_BIRTH_BY_ID)) {
                ps.setLong(1, animal.id());

                try (ResultSet result = ps.executeQuery()) {
                    if (result.next())
                        return result
                                .getTimestamp(1)
                                .toLocalDateTime();
                    throw new IllegalStateException("birth date is empty");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Optional<LocalDateTime> dateOfDeath() {
            try (var ps = connection.prepareStatement(SELECT_DATE_OF_DEATH_BY_ID)) {
                ps.setLong(1, animal.id());

                try (ResultSet result = ps.executeQuery()) {

                    return result.next()
                            ? Optional.of(result.getTimestamp(1).toLocalDateTime())
                            : Optional.empty();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}

