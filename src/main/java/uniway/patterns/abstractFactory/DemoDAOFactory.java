package uniway.patterns.abstractFactory;

import uniway.persistenza.*;

import java.sql.Connection;
import java.util.Objects;

//CONCRETE FACTORY
public final class DemoDAOFactory implements DAOFactory {

    private final Connection conn;

    public DemoDAOFactory(Connection conn) {
        this.conn = Objects.requireNonNull(conn, "Connection DB nulla (usata per RecensioneDB)");
    }

    @Override
    public UtenteDAO utenteDAO() {
        return new UtenteDemo();
    }

    @Override
    public RecensioneDAO recensioneDAO() {
        return new RecensioneDemo();
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
