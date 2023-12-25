package sunmisc.db.dynamo;

import sunmisc.db.agents.Collars;
import sunmisc.db.models.Pet;

import java.sql.Connection;
import java.util.Objects;

public final class QCollars implements Collars {

    private static final String INSERT_COLLAR = """
            INSERT INTO collars (pet_id, description)
            VALUES (?,?)
            """;

    private final Connection connection;

    public QCollars(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void add(Pet pet, String description) throws Exception {
        Objects.requireNonNull(pet);
        Objects.requireNonNull(description);

        try (var ps = connection.prepareStatement(INSERT_COLLAR)) {
            ps.setLong(1, pet.id());
            ps.setString(2, description);

            ps.execute();
        }
    }
}
