package sunmisc.db.dynamo;

import sunmisc.db.models.Animal;
import sunmisc.db.models.Owner;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QOwner implements Owner {
    private static final String SELECT_PHONE_NUMBER =
            "SELECT phone FROM owners WHERE first_name = ? AND last_name = ?";
    private static final String SELECT_ID_BY_NAME =
            "SELECT id FROM owners WHERE first_name = ? AND last_name = ?";

    private final Identification origin;

    private final Connection connection;

    public QOwner(Identification origin, Connection connection) {
        this.origin = origin;
        this.connection = connection;
    }

    @Override
    public Identification identification() {
        return origin;
    }

    @Override
    public long id() throws SQLException {
        try (var ps = connection.prepareStatement(SELECT_ID_BY_NAME)) {

            ps.setString(1, origin.firstName());
            ps.setString(2, origin.lastName());

            try (ResultSet result = ps.executeQuery()) {
                if (result.next())
                    return result.getLong(1);
                throw new IllegalStateException("id is empty");
            }
        }
    }


    @Override
    public String phone() throws SQLException  {
        try (var ps = connection.prepareStatement(SELECT_PHONE_NUMBER)) {

            ps.setString(1, origin.firstName());
            ps.setString(2, origin.lastName());

            try (ResultSet result = ps.executeQuery()) {
                if (result.next())
                    return result.getString(1);
                throw new IllegalStateException("phone is empty");
            }
        }
    }

    @Override
    public void add(Animal animal, String name) throws Exception {
        new QPets(connection).add(this, animal, name);
    }
}
