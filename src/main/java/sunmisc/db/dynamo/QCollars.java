package sunmisc.db.dynamo;

import sunmisc.db.agents.Collars;
import sunmisc.db.models.Collar;
import sunmisc.db.models.Pet;

import java.sql.Connection;
import java.util.Objects;
import java.util.function.Function;

public final class QCollars implements Collars {

    private static final String INSERT_COLLAR = """
            INSERT INTO collars (pet_id, description)
            VALUES (?,?)
            """;

    private final Connection connection;

    private final Function<Pet, Collar> mapping;

    public QCollars(Connection connection) {
        this(connection, pet -> new QCollar(pet, connection));
    }


    public QCollars(Connection connection, Function<Pet, Collar> mapping) {
        this.connection = connection;
        this.mapping = mapping;
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

    @Override
    public Collar collar(Pet pet) {
        return mapping.apply(pet);
    }
}
