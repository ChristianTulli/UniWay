
package uniway.model;

import uniway.persistenza.UtenteDAO;
import uniway.persistenza.UtenteDB;
import uniway.persistenza.UtenteFS;

import java.io.FileInputStream;
import java.util.Properties;

public class Utente {
    private String username;
    private String password;
    UtenteDAO utenteDAO;
    Properties properties=new Properties();

    public Utente() {
        try (FileInputStream input=new FileInputStream("config.properties")) {
            properties.load(input);
        }catch (Exception e){
            throw new RuntimeException("Errore nel caricamenteo della configurazione ");
        }

        String mode=properties.getProperty("persistence.mode");

        if("file".equals(mode)){
            utenteDAO=new UtenteFS("file.path");
        } else if ("db".equals(mode)) {
            utenteDAO=new UtenteDB("db.url", "db.username", "db.password");
        } else {
            throw new IllegalArgumentException("Invalid persistence mode: " + mode);
        }
    }

    public Utente(String username, String password) {
        this.username = username;
        this.password = password;
    }



    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

