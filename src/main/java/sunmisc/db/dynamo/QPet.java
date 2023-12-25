package sunmisc.db.dynamo;

import sunmisc.db.models.Collar;
import sunmisc.db.models.Live;
import sunmisc.db.models.Owner;
import sunmisc.db.models.Pet;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class QPet implements Pet {
    private static final String SELECT_NAME_BY_PET_ID = """
            SELECT name FROM pets WHERE pet_id = ?
            LIMIT 1
            """;
    private static final String SELECT_OWNER_BY_PET_ID = """
            SELECT owner_id FROM pets WHERE pet_id = ?
            LIMIT 1
            """;
    private final long petId;
    private final Connection connection;


    public QPet(long petId, Connection connection) {
        this.petId = petId;
        this.connection = connection;
    }
    @Override
    public Owner owner() throws Exception {
        try (var ps = connection.prepareStatement(SELECT_OWNER_BY_PET_ID)) {
            ps.setLong(1, id());

            try (ResultSet result = ps.executeQuery()) {
                if (result.next()) {
                    long owner_id = result.getLong(1);
                    return new QOwner(owner_id, connection);
                }
                throw new IllegalStateException("owner is empty");
            }
        }
    }

    @Override
    public String name() throws Exception {
        try (var ps = connection.prepareStatement(SELECT_NAME_BY_PET_ID)) {
            ps.setLong(1, id());

            try (ResultSet result = ps.executeQuery()) {
                if (result.next())
                    return result.getString(1);
                throw new IllegalStateException("name is empty");
            }
        }
    }

    @Override
    public Live live() throws Exception {
        return new QAnimal(connection, id()).live();
    }

    @Override
    public Collar collar() {
        return new QCollar(this, connection);
    }


    @Override
    public long id() throws Exception {
        return petId;
    }

    @Override
    public String type() throws Exception {
        return new QAnimal(connection, id()).type();
    }

    @Override
    public void die() throws Exception {
        Connection conn = connection;

        // transaction block
        conn.setAutoCommit(false);

        try {
            new QAnimal(connection, id()).die();
            new QCollar(this, connection).invalidate();
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw new RuntimeException(e);
        }
    }

}
