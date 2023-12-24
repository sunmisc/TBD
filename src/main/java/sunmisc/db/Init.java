package sunmisc.db;


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Init {
    private static final String OWNERS_TABLE = """
            CREATE TABLE IF NOT EXISTS `owners` (
                `id` BIGINT AUTO_INCREMENT NOT NULL,
                `first_name` VARCHAR(255) NOT NULL,
                `last_name` VARCHAR(255) NOT NULL,
                `phone` VARCHAR(255) NULL DEFAULT NULL,
                PRIMARY KEY (`id`)
            ) CHARACTER SET utf8 COLLATE utf8_general_ci;
            """;
    private static final String ANIMALS_TABLE = """
            CREATE TABLE IF NOT EXISTS `animals` (
                `id` BIGINT AUTO_INCREMENT NOT NULL,
                `date_of_birth` TIMESTAMP NOT NULL,
                `date_of_death` TIMESTAMP NULL DEFAULT NULL,
            	`type` VARCHAR(255) NOT NULL,
                PRIMARY KEY (`id`)
            ) CHARACTER SET utf8 COLLATE utf8_general_ci;
            """;
    private static final String PETS_TABLE = """
            CREATE TABLE IF NOT EXISTS `pets` (
                `pet_id` BIGINT NOT NULL,
                `owner_id` BIGINT NULL DEFAULT NULL,
                `name` VARCHAR(255) NOT NULL,
                PRIMARY KEY (`pet_id`, `name`),
                CONSTRAINT `own` FOREIGN KEY (`owner_id`) REFERENCES `owners` (`id`)
            	ON UPDATE NO ACTION ON DELETE NO ACTION
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
                     new PooledDatabase().connection()){
            ensureTables(connection);
        }
    }

    private static void
    ensureTables(Connection connection) throws SQLException{
        try (Statement statement = connection.createStatement()) {
            statement.addBatch(OWNERS_TABLE);
            statement.addBatch(ANIMALS_TABLE);
            statement.addBatch(PETS_TABLE);
            statement.addBatch(COLLARS_TABLE);

            statement.executeBatch();
        }
    }

}
