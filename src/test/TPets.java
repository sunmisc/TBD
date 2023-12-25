import org.testng.annotations.Test;
import sunmisc.db.PooledDatabase;
import sunmisc.db.agents.Pets;
import sunmisc.db.dynamo.QAnimal;
import sunmisc.db.dynamo.QOwner;
import sunmisc.db.dynamo.QPets;
import sunmisc.db.models.Owner;

import java.sql.Connection;

import static org.testng.Assert.assertTrue;

public class TPets {


    @Test
    public void add() throws Exception {
        PooledDatabase db = new PooledDatabase();
        try (Connection connection = db.connection()) {

            new QOwner(1, connection).add(
                    new QAnimal(connection, 1),
                    "Чоп");
        }
    }
    @Test
    public void findOwner() throws Exception {
        PooledDatabase db = new PooledDatabase();
        try (Connection connection = db.connection()) {
            Owner owner = new QOwner(1, connection);

            Pets pets = new QPets(connection);

            assertTrue(pets.pets(owner).findAny().isPresent());
        }
    }
}
