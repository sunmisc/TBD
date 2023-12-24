package sunmisc.db.dynamo;

import sunmisc.db.agents.Animals;
import sunmisc.db.models.Animal;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class QAnimals implements Animals {

    private final Connection connection;

    private static final String INSERT_ANIMAL = """
            INSERT INTO animals (type, date_of_birth)
            VALUES (?,?)
            """;
    private static final String SELECT_ANIMAL_BY_TYPE =
            "SELECT id FROM animals WHERE type = ?";

    public QAnimals(Connection connection) {
        this.connection = connection;
    }


    @Override
    public void add(String type) throws Exception {
        try (var ps = connection.prepareStatement(INSERT_ANIMAL)) {
            ps.setString(1, type);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));

            ps.execute();
        }
    }

    @Override
    public Stream<Animal> animalsByType(String type) {
        List<Animal> list = new LinkedList<>();
        try (var ps = connection.prepareStatement(SELECT_ANIMAL_BY_TYPE)) {
            ps.setString(1, type);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long id = rs.getLong(1);
                    list.add(new QAnimal(connection, id));
                }
            }
            return list.stream();
        } catch (SQLException e) {
            return Stream.empty();
        }
    }
}
