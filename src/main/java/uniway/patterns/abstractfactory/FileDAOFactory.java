package uniway.patterns.abstractfactory;

import uniway.persistenza.*;

import java.sql.Connection;
import java.util.Objects;

//CONCRETE FACTORY
public final class FileDAOFactory implements DAOFactory {

    private final String utentiFilePath;
    private final Connection conn; // usato da RecensioneDB

    public FileDAOFactory(String utentiFilePath, Connection conn) {
        this.utentiFilePath = Objects.requireNonNull(utentiFilePath, "file.path utenti nullo");
        this.conn = Objects.requireNonNull(conn, "Connection DB nulla (usata per RecensioneDB)");
    }

    @Override
    public UtenteDAO utenteDAO() {
        return new UtenteFS(utentiFilePath);
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

