package sunmisc.db.dynamo;

import sunmisc.db.agents.Owners;
import sunmisc.db.models.Owner;

import java.sql.Connection;
import java.util.Objects;

public class QOwners implements Owners {
    private static final String INSERT_OWNER = """
            INSERT INTO owners (first_name, last_name, phone)
            VALUES (?,?,?)
            """;
    private final Connection connection;

    public QOwners(Connection connection) {
        this.connection = connection;
    }


    @Override
    public void add(Owner.Identification identifier,
                    String phone) throws Exception {
        Objects.requireNonNull(identifier);
        Objects.requireNonNull(phone);
        try (var ps = connection.prepareStatement(INSERT_OWNER)) {
            ps.setString(1, identifier.firstName());
            ps.setString(2, identifier.lastName());
            ps.setString(3, phone);

            ps.execute();
        }
    }

    @Override
    public Owner owner(Owner.Identification identifier) {
        Objects.requireNonNull(identifier);
        return new QOwner(identifier, connection);
    }
}
