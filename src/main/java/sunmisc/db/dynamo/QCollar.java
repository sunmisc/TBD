package sunmisc.db.dynamo;

import sunmisc.db.models.Collar;
import sunmisc.db.models.Pet;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Objects;

public class QCollar implements Collar {
    private static final String SELECT_DESCRIPTION_BY_ID =
            "SELECT description FROM collars WHERE pet_id = ?";
    private static final String UPDATE_DESCRIPTION =
            "UPDATE collars SET description = ? WHERE pet_id = ?";
    private static final String DELETE =
            "DELETE FROM collars WHERE pet_id = ?";

    private final Connection connection;
    private final Pet pet;

    public QCollar(Pet pet, Connection connection) {
        this.pet = pet;
        this.connection = connection;
    }

    @Override
    public Pet pet() {
        return pet;
    }

    @Override
    public long id() throws Exception {
        return pet().id();
    }

    @Override
    public String description() throws Exception {
        try (var ps = connection.prepareStatement(SELECT_DESCRIPTION_BY_ID)) {
            ps.setLong(1, pet().id());

            try (ResultSet result = ps.executeQuery()) {
                return result.next()
                        ? result.getString(1)
                        : "empty";
            }
        }
    }

    @Override
    public void updateDescription(String description) throws Exception {
        Objects.requireNonNull(description);
        try (var ps = connection.prepareStatement(UPDATE_DESCRIPTION)) {
            ps.setString(1, description);
            ps.setLong(2, pet().id());
            ps.execute();
        }
    }

    @Override
    public void invalidate() throws Exception {
        try (var ps = connection.prepareStatement(DELETE)) {
            ps.setLong(1, pet().id());
            ps.execute();
        }
    }
}
