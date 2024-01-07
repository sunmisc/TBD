package sunmisc.db.dynamo;

import sunmisc.db.Transaction;
import sunmisc.db.agents.Animals;
import sunmisc.db.agents.Collars;
import sunmisc.db.agents.Owners;
import sunmisc.db.models.Collar;
import sunmisc.db.models.Live;
import sunmisc.db.models.Owner;
import sunmisc.db.models.Pet;

import java.sql.Connection;
import java.sql.ResultSet;

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

    private final Owners owners;
    private final Collars collars;
    private final Animals animals;


    public QPet(long petId, Connection connection) {

        this(petId, connection,
                new QOwners(connection),
                new QCollars(connection),
                new QAnimals(connection));
    }

    public QPet(long petId, Connection connection,
                Owners owners,
                Collars collars,
                Animals animals) {
        this.petId = petId;
        this.connection = connection;
        this.owners = owners;
        this.collars = collars;
        this.animals = animals;
    }
    @Override
    public long id() throws Exception {
        return petId;
    }

    @Override
    public Owner owner() throws Exception {
        try (var ps = connection.prepareStatement(SELECT_OWNER_BY_PET_ID)) {
            ps.setLong(1, id());

            try (ResultSet result = ps.executeQuery()) {
                if (result.next()) {
                    long owner_id = result.getLong(1);
                    return owners.owner(owner_id);
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
        return animals.animal(id()).live();
    }

    @Override
    public Collar collar() {
        return collars.collar(this);
    }


    @Override
    public String type() throws Exception {
        return animals.animal(id()).type();
    }

    @Override
    public void die() {

        new Transaction(() -> {

            try {
                animals.animal(id()).die();
                collar().invalidate();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, connection).run();
    }

}
