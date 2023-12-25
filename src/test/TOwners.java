import org.testng.annotations.Test;
import sunmisc.db.PooledDatabase;
import sunmisc.db.agents.Owners;
import sunmisc.db.dynamo.QOwners;
import sunmisc.db.models.Owner;

import java.sql.Connection;

import static org.testng.Assert.assertEquals;

public class TOwners {

    public static final Owner.Identification IDENTIFIER
            = new Owner.Identification("Дмитрий", "Абрамов");
    public static final String PHONE = "7182827323";

    @Test
    public void add() throws Exception {
        PooledDatabase db = new PooledDatabase();
        try (Connection connection = db.connection()) {
            Owners owners = new QOwners(connection);

            owners.add(
                    IDENTIFIER,
                    PHONE
            );
        }
    }
    @Test
    public void findOwner() throws Exception {
        PooledDatabase db = new PooledDatabase();
        try (Connection connection = db.connection()) {
            Owners owners = new QOwners(connection);

            Owner owner = owners.owners(TOwners.IDENTIFIER)
                    .findAny()
                    .orElseThrow();
            assertEquals(owner.identification(), IDENTIFIER);
            assertEquals(owner.phone(), PHONE);
        }
    }
}
