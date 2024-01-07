package sunmisc.db.dynamo;

import sunmisc.db.agents.Owners;
import sunmisc.db.models.Owner;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public final class QOwners implements Owners {
    private static final String INSERT_OWNER = """
            INSERT INTO owners (first_name, last_name, phone)
            VALUES (?,?,?)
            """;
    private static final String SELECT_BY_PHONE = """
            SELECT id,phone FROM owners WHERE phone = ?
            LIMIT 1
            """;
    private static final String SELECT_BY_IDENTIFICATION = """
            SELECT id,phone FROM owners
            WHERE first_name = ? AND last_name = ?
            """;
    private final Connection connection;

    private final Function<Long, Owner> mapping;

    public QOwners(Connection connection) {
        this(connection, id -> new QOwner(id, connection));
    }

    public QOwners(Connection connection, Function<Long, Owner> mapping) {
        this.connection = connection;
        this.mapping = mapping;
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
    public Stream<Owner> owners(Owner.Identification identifier) {
        List<Owner> owners = new LinkedList<>();
        try (var ps = connection.prepareStatement(SELECT_BY_IDENTIFICATION)) {
            ps.setString(1, identifier.firstName());
            ps.setString(2, identifier.lastName());

            try (ResultSet result = ps.executeQuery()) {
                while (result.next()) {
                    long id = result.getLong(1);
                    owners.add(owner(id));
                }
            }
        } catch (SQLException e) {
            return Stream.empty();
        }
        return owners.stream();
    }

    @Override
    public Optional<Owner> owner(String phone) {
        try (var ps = connection.prepareStatement(SELECT_BY_PHONE)) {
            ps.setString(1, phone);

            try (ResultSet result = ps.executeQuery()) {
                if (result.next()) {
                    long id = result.getLong(1);

                    return Optional.of(owner(id));
                }

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Owner owner(long id) {
        return mapping.apply(id);
    }

}
