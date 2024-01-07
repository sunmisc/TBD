package sunmisc.db.dynamo;

import sunmisc.db.Transaction;
import sunmisc.db.agents.Animals;
import sunmisc.db.agents.Collars;
import sunmisc.db.agents.Owners;
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
import java.util.function.Function;
import java.util.stream.Stream;

public final class QPets implements Pets {
    private static final String SELECT_PETS =
            "SELECT pet_id FROM pets WHERE owner_id = ?";
    private static final String INSERT_PET = """
            INSERT INTO pets (pet_id, owner_id, name)
            VALUES (?,?,?)
            """;
    private final Connection connection;
    private final Collars collars;
    private final Function<Long, Pet> mapping;

    public QPets(Connection connection) {
        this(connection,
                new QOwners(connection),
                new QAnimals(connection),
                new QCollars(connection));
    }

    public QPets(Connection connection,
                 Owners owners,
                 Animals animals,
                 Collars collars) {
        this(connection, collars,
                id -> new QPet(id, connection, owners, collars, animals));
    }

    public QPets(Connection connection,
                 Collars collars,
                 Function<Long, Pet> mapping) {
        this.connection = connection;
        this.collars = collars;
        this.mapping = mapping;
    }


    @Override
    public void add(Owner owner, Animal animal, String name) throws Exception {
        Objects.requireNonNull(owner);
        Objects.requireNonNull(animal);

        new Transaction(() -> {
            try (var ps = connection.prepareStatement(INSERT_PET)) {
                long pet_id = animal.id();
                ps.setLong(1, pet_id);
                ps.setLong(2, owner.id());
                ps.setString(3, name);

                ps.execute();

                collars.add(pet(pet_id), "empty");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, connection).run();
    }

    @Override
    public Stream<Pet> pets(Owner owner) {
        try (var ps = connection.prepareStatement(SELECT_PETS)) {

            List<Pet> pets = new LinkedList<>();

            ps.setLong(1, owner.id());

            try (ResultSet result = ps.executeQuery()) {
                while (result.next()) {
                    long pet_id = result.getLong(1);
                    pets.add(pet(pet_id));
                }
            }
            return pets.stream();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Pet pet(long id) {
        return mapping.apply(id);
    }
}
