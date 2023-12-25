import org.testng.annotations.Test;
import sunmisc.db.PooledDatabase;
import sunmisc.db.agents.Owners;
import sunmisc.db.agents.Pets;
import sunmisc.db.dynamo.QCollar;
import sunmisc.db.dynamo.QOwners;
import sunmisc.db.dynamo.QPets;
import sunmisc.db.models.Collar;
import sunmisc.db.models.Owner;
import sunmisc.db.models.Pet;

import java.sql.Connection;

public class TPet {


    @Test
    public void die() throws Exception {
        PooledDatabase db = new PooledDatabase();

        try (Connection connection = db.connection()) {
            Owners owners = new QOwners(connection);
            Owner owner = owners
                    .owners(TOwners.IDENTIFIER)
                    .findAny()
                    .orElseThrow();

            Pets pets = new QPets(connection);

            Pet pet = pets.pets(owner).findAny().orElseThrow();
            pet.die();


            Collar collar = new QCollar(pet, connection);

            try {
                collar.id();
            } catch (Exception ignored) { }


            System.out.println(pet.live().dateOfDeath());

        }
    }
}
