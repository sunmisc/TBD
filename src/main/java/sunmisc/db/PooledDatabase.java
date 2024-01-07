package sunmisc.db;

import org.mariadb.jdbc.MariaDbDataSource;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

@SuppressWarnings("forRemoval")
public class PooledDatabase {

    private final String username, password, dbName;

    public PooledDatabase(String username, String password, String dbName) {
        this.username = username;
        this.password = password;
        this.dbName = dbName;
    }
    public PooledDatabase() {
        this("root", "123456", "test");
    }

    public Connection connection() throws SQLException {
        MariaDbDataSource ds = new MariaDbDataSource();

        ds.setPort(3306);
        ds.setUser(username);
        ds.setPassword(password);
        ds.setDatabaseName(dbName);

        return ds.getConnection();
    }
}
