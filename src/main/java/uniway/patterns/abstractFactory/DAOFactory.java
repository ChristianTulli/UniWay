package uniway.patterns.abstractFactory;

import uniway.persistenza.*;

//ABSTRACT FACTORY
public interface DAOFactory {
    UtenteDAO utenteDAO();
    AteneoDAO ateneoDAO();
    CorsoDAO corsoDAO();
    InsegnamentoDAO insegnamentoDAO();
    RecensioneDAO recensioneDAO();
}

