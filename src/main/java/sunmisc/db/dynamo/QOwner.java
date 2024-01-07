package sunmisc.db.dynamo;

import sunmisc.db.models.Owner;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class QOwner implements Owner {
    private static final String SELECT_IDENTIFICATION =
            "SELECT phone FROM owners WHERE id = ? LIMIT 1";
    private static final String SELECT_PHONE_NUMBER =
            "SELECT id FROM owners WHERE id = ? LIMIT 1";
    private static final String UPDATE_PHONE_NUMBER =
            "UPDATE owners SET phone = ? WHERE id = ?";
    private final long id;

    private final Connection connection;


    public QOwner(long id, Connection connection) {
        this.id = id;
        this.connection = connection;
    }


    @Override
    public long id() throws SQLException {
        return id;
    }

    @Override
    public Identification identification() throws Exception {
        try (var ps = connection.prepareStatement(SELECT_IDENTIFICATION)) {

            ps.setLong(1, id());

            try (ResultSet result = ps.executeQuery()) {
                if (result.next()) {
                    String first = result.getString(1);
                    String last = result.getString(2);

                    return new Identification(first, last);
                }
                throw new IllegalStateException("identification is empty");
            }
        }
    }

    @Override
    public String phone() throws SQLException  {
        try (var ps = connection.prepareStatement(SELECT_PHONE_NUMBER)) {

            ps.setLong(1, id());

            try (ResultSet result = ps.executeQuery()) {
                if (result.next())
                    return result.getString(1);
                throw new IllegalStateException("phone is empty");
            }
        }
    }

    @Override
    public void updatePhone(String newPhoneNumber) throws SQLException {
        try (var ps = connection.prepareStatement(UPDATE_PHONE_NUMBER)) {
            ps.setString(1, newPhoneNumber);
            ps.setLong(2, id());

            ps.execute();
        }
    }
}
