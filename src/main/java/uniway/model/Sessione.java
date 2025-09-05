package uniway.model;

import uniway.beans.UtenteBean;

public class Sessione {
    private static Sessione instance = null;
    private UtenteBean utente;

    private Sessione(){}

    public static synchronized Sessione getInstance(){
        if(instance == null){
            instance = new Sessione();
        }
        return instance;
    }
    public UtenteBean getCurrentUser() {
        return utente;
    }
    public void setCurrentUser(UtenteBean utente) {
        this.utente = utente;
    }
    public void logout(){
        this.utente = null;
    }

}
