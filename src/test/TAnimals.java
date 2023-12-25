import org.testng.annotations.Test;
import sunmisc.db.PooledDatabase;
import sunmisc.db.dynamo.QAnimals;

import java.sql.Connection;

import static org.testng.Assert.assertTrue;

public class TAnimals {


    @Test
    public void add() throws Exception {
        PooledDatabase db = new PooledDatabase();
        try (Connection connection = db.connection()) {
            QAnimals animals = new QAnimals(connection);

            animals.add("dog");
        }
    }
    @Test
    public void findOwner() throws Exception {
        PooledDatabase db = new PooledDatabase();
        try (Connection connection = db.connection()) {
            QAnimals animals = new QAnimals(connection);

            assertTrue(animals.animalsByType("dog").findAny().isPresent());
        }
    }
}
