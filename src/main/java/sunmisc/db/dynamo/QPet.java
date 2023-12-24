package sunmisc.db.dynamo;

import sunmisc.db.models.Collar;
import sunmisc.db.models.Live;
import sunmisc.db.models.Owner;
import sunmisc.db.models.Pet;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QPet implements Pet {
    private static final String SELECT_ID_BY_NAME =
            "SELECT pet_id FROM pets WHERE name = ? AND owner_id = ?";
    private final String name;

    private final Connection connection;

    private final Owner owner;

    public QPet(String name,
                Connection connection,
                Owner owner) {
        this.name = name;
        this.connection = connection;
        this.owner = owner;
    }

    @Override
    public String name() {
        return name;
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
    public Owner owner() {
        return owner;
    }

    @Override
    public long id() throws Exception {
        try (var ps = connection.prepareStatement(SELECT_ID_BY_NAME)) {
            ps.setString(1, name());
            ps.setLong(2, owner.id());

            try (ResultSet result = ps.executeQuery()) {
                if (result.next())
                    return result.getLong(1);
                throw new IllegalStateException("id is empty");
            }
        }
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
