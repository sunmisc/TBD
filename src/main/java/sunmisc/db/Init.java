package sunmisc.db;


import sunmisc.db.agents.Animals;
import sunmisc.db.agents.Owners;
import sunmisc.db.agents.Pets;
import sunmisc.db.dynamo.*;
import sunmisc.db.models.Animal;
import sunmisc.db.models.Owner;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

final class Init {
    private static final String OWNERS_TABLE = """
            CREATE TABLE IF NOT EXISTS `owners` (
                `id` BIGINT AUTO_INCREMENT NOT NULL,
                `first_name` VARCHAR(32) NOT NULL,
                `last_name` VARCHAR(32) NOT NULL,
                `phone` VARCHAR(32) NOT NULL,
                PRIMARY KEY (`id`, `phone`)
            ) CHARACTER SET utf8 COLLATE utf8_general_ci;
            """;
    private static final String ANIMALS_TABLE = """
            CREATE TABLE IF NOT EXISTS `animals` (
                `id` BIGINT AUTO_INCREMENT NOT NULL,
                `date_of_birth` TIMESTAMP NOT NULL,
            	`type` VARCHAR(32) NOT NULL,
                PRIMARY KEY (`id`)
            ) CHARACTER SET utf8 COLLATE utf8_general_ci;
            """;
    private static final String DEAD_ANIMALS_TABLE = """
            CREATE TABLE IF NOT EXISTS `dead_animals` (
                `animal_id` BIGINT NOT NULL,
                `date_of_death` TIMESTAMP NOT NULL,
                PRIMARY KEY (`animal_id`),
                CONSTRAINT `dan` FOREIGN KEY (`animal_id`) REFERENCES `animals` (`id`)
                ON UPDATE NO ACTION
            	ON DELETE NO ACTION
            ) CHARACTER SET utf8 COLLATE utf8_general_ci;
            """;
    private static final String PETS_TABLE = """
            CREATE TABLE IF NOT EXISTS `pets` (
                `pet_id` BIGINT NOT NULL,
                `owner_id` BIGINT NOT NULL,
                `name` VARCHAR(32) NOT NULL,
                PRIMARY KEY (`pet_id`, `name`),
                CONSTRAINT `own` FOREIGN KEY (`owner_id`) REFERENCES `owners` (`id`),
                CONSTRAINT `an` FOREIGN KEY (`pet_id`) REFERENCES `animals` (`id`)
            	ON UPDATE NO ACTION
            	ON DELETE NO ACTION
            ) CHARACTER SET utf8 COLLATE utf8_general_ci;
            """;
    private static final String COLLARS_TABLE = """
            CREATE TABLE IF NOT EXISTS `collars` (
                `pet_id` BIGINT NOT NULL,
                `description` VARCHAR(255) NULL DEFAULT NULL,
                PRIMARY KEY (`pet_id`),
            	CONSTRAINT `pt` FOREIGN KEY (`pet_id`) REFERENCES `pets` (`pet_id`)
            	ON UPDATE NO ACTION ON DELETE NO ACTION
            ) CHARACTER SET utf8 COLLATE utf8_general_ci;
            """;

    private static final Logger LOGGER =
            Logger.getLogger("Курсовая ;)");


    public static void main(String[] args) throws Exception {

        LOGGER.log(Level.INFO, "make sure tables exist");
        try (Connection connection =
                     new PooledDatabase().connection()) {
            try {
                ensureTables(connection);
                putAllOwners(connection);
                putAllAnimals(connection);
                putAllPets(connection);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static final List<Owner.Identification> OWNERS = Map.of(
            "Александр", "Иванов",
            "Екатерина", "Смирнова",
            "Михаил", "Петров",
            "Ольга", "Соколова",
            "Дмитрий", "Козлов",
            "Анна", "Федорова",
            "Сергей", "Морозов",
            "Наталья", "Волкова",
            "Иван", "Кузнецов",
            "Мария", "Лебедева"
    ).entrySet()
            .stream()
            .map(x -> new Owner.Identification(x.getKey(), x.getValue()))
            .toList();

    private static void
    putAllOwners(Connection connection) {
        Owners owners = new QOwners(connection);
        OWNERS.forEach(id -> {
            try {
                owners.add(id, String.valueOf(Math.abs(id.hashCode())));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
    private static void
    putAllAnimals(Connection connection) throws Exception {
        final String[] types = {
                "Собака",
                "Кошка",
                "Морская свинка",
                "Попугай"
        };
        Animals animals = new QAnimals(connection);
        for (int i = 0; i < 10; ++i) {
            int rand = ThreadLocalRandom
                    .current()
                    .nextInt(0, types.length);
            animals.add(types[rand]);
        }
    }
    private static void
    putAllPets(Connection connection) throws Exception {
        final String[] names = {
                "Бонни",
                "Рекс",
                "Чоп",
                "Бэйли",
                "Джесси",
                "Оливер",
                "Лола",
                "Белка",
                "Луна",
                "Макс"
        };
        Pets pets = new QPets(connection);
        for (int i = 0, n = names.length; i < n; ++i) {
            int rand = ThreadLocalRandom
                    .current()
                    .nextInt(0, n);
            long id = i + 1;
            Animal animal = new QAnimal(connection, id);
            Owner owner = new QOwner(id, connection);

            pets.add(owner, animal, names[rand]);

            if (ThreadLocalRandom.current().nextBoolean())
                pets.pets(owner)
                    .findAny()
                    .ifPresent(x -> {
                        try {
                            x.die();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }
    private static void
    ensureTables(Connection connection) throws SQLException{
        try (Statement statement = connection.createStatement()) {

            statement.addBatch(OWNERS_TABLE);
            statement.addBatch(ANIMALS_TABLE);
            statement.addBatch(DEAD_ANIMALS_TABLE);
            statement.addBatch(PETS_TABLE);
            statement.addBatch(COLLARS_TABLE);

            statement.executeBatch();
        }
    }

}
