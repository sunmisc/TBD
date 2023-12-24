package sunmisc.db.dynamo;

import sunmisc.db.agents.Pets;
import sunmisc.db.models.Animal;
import sunmisc.db.models.Owner;
import sunmisc.db.models.Pet;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class QPets implements Pets {
    private static final String SELECT_PETS =
            "SELECT name FROM pets WHERE owner_id = ?";
    private static final String INSERT_PET = """
            INSERT INTO pets (pet_id, owner_id, name)
            VALUES (?,?,?)
            """;
    private final Connection connection;

    public QPets(Connection connection) {
        this.connection = connection;
    }


    @Override
    public void add(Owner owner, Animal animal, String name) throws Exception {
        Objects.requireNonNull(owner);
        Objects.requireNonNull(animal);

        // transaction block
        connection.setAutoCommit(false);
        try (var ps = connection.prepareStatement(INSERT_PET)) {
            ps.setLong(1, animal.id());
            ps.setLong(2, owner.id());
            ps.setString(3,name);

            ps.execute();

            new QCollars(connection)
                    .add(
                            new QPet(name, connection, owner),
                            "empty");
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Stream<Pet> pets(Owner owner) {
        try (var ps = connection.prepareStatement(SELECT_PETS)) {

            List<Pet> pets = new LinkedList<>();

            ps.setLong(1, owner.id());

            try (ResultSet result = ps.executeQuery()) {
                while (result.next()) {
                    String pet_name = result.getString(1);
                    pets.add(new QPet(pet_name, connection, owner));
                }
            }
            return pets.stream();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
