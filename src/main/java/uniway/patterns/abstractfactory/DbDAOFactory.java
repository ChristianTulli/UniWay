package uniway.patterns.abstractfactory;

import uniway.persistenza.*;

import java.sql.Connection;
import java.util.Objects;

//CONCRETE FACTORY
public final class DbDAOFactory implements DAOFactory {

    private final Connection conn;

    public DbDAOFactory(Connection conn) {
        this.conn = Objects.requireNonNull(conn, "Connection DB nulla");
    }

    @Override
    public UtenteDAO utenteDAO() {
        return new UtenteDB(conn);
    }

    @Override
    public RecensioneDAO recensioneDAO() {
        return new RecensioneDB(conn);
    }

    @Override
    public AteneoDAO ateneoDAO() {
        return new AteneoDAO(conn);
    }

    @Override
    public CorsoDAO corsoDAO() {
        return new CorsoDAO(conn);
    }

    @Override
    public InsegnamentoDAO insegnamentoDAO() {
        return new InsegnamentoDAO(conn);
    }
}
