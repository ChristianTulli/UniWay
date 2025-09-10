package uniway.patterns.abstractFactory;

import java.sql.Connection;
import java.util.Objects;
import java.util.Properties;

//Selettore di concrete factory in base alle properties
public final class DAOFactories {
    private DAOFactories() {}

    public static DAOFactory from(Properties props, Connection conn) {
        Objects.requireNonNull(props, "props nulle");

        String running = props.getProperty("running.mode", "full"); // full | demo
        if ("demo".equalsIgnoreCase(running)) {
            return new DemoDAOFactory(conn);
        }

        // full
        String persistence = props.getProperty("persistence.mode", "db"); // db | file
        switch (persistence.toLowerCase()) {
            case "file" -> {
                String filePath = props.getProperty("file.path");
                return new FileDAOFactory(filePath, conn);
            }
            case "db" -> {
                return new DbDAOFactory(conn);
            }
            default -> {
                // fallback sicuro
                return new DbDAOFactory(conn);
            }
        }
    }
}
