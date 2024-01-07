package sunmisc.db;

import java.sql.Connection;
import java.sql.SQLException;

public class Transaction implements Runnable {

    private final Runnable runnable;

    private final Connection connection;

    public Transaction(Runnable runnable, Connection connection) {
        this.runnable = runnable;
        this.connection = connection;
    }

    @Override
    public void run() {
        try {
            connection.setAutoCommit(false);

            runnable.run();

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }

    }
}
