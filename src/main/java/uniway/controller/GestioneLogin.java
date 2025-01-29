
package uniway.controller;

import uniway.beans.UtenteBean;
import uniway.model.Utente;
import uniway.persistenza.UtenteDAO;
import uniway.persistenza.UtenteDB;
import uniway.persistenza.UtenteFS;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class GestioneLogin {

    //pattern singleton
    private  static GestioneLogin instance;
    private final List<Utente> utenti= new ArrayList<>();
    private static UtenteDAO utenteDAO;
    private static boolean isFullMode;


    private GestioneLogin() {
        Properties properties = new Properties();
        try (FileInputStream input=new FileInputStream("src/main/resources/config.properties")) {
            properties.load(input);

            isFullMode="full".equals(properties.getProperty("running.mode"));

            if(isFullMode) {
                String mode = properties.getProperty("running.mode");
                if ("file".equals(mode)) {
                    utenteDAO = new UtenteFS(properties.getProperty("file.path"));
                } else if ("db".equalsIgnoreCase(mode)) {
                    utenteDAO = new UtenteDB(properties.getProperty("db.url"), properties.getProperty("db.username"), properties.getProperty("db.password"));
                } else {
                    throw new IllegalArgumentException("Invalid persistence mode: " + mode);
                }

                //carica gli utenti salvati sulla lista
                utenti.addAll(utenteDAO.ottieniUtenti());
            }
        }catch (IOException e){
            throw new RuntimeException("Errore nel caricamenteo della configurazione", e);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static GestioneLogin getInstance() {
        if(instance == null) {
            instance = new GestioneLogin();
        }
        return instance;
    }


    //per la registrazione controlliamo se i dati inseriti momentaneamente nella classe bean sono accettabili e istanziamo un oggetto user in caso positivo, confermando la registrazione

    public boolean registrazione(UtenteBean utenteBean) {
        if(utenteBean.getUsername().isEmpty() || utenteBean.getPassword().isEmpty()) {
            return false;
        }else if (utenteBean.getPassword().length() < 6) {
            return false;
        }
            Optional<Utente> existingUser = utenti.stream()
                    .filter(u -> u.getUsername().equals(utenteBean.getUsername()))
                    .findFirst();

            if (existingUser.isPresent()) {
                return false; // Username già esistente
            }

                Utente utente = new Utente(utenteBean.getUsername(), utenteBean.getPassword());
                utenti.add(utente);


            //se siamo in modalita' full salviamo nel file/db
            if(isFullMode) {
                try{
                    utenteDAO.salvaUtente(utente);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            return true;
            }

    public boolean autenticazione(UtenteBean utenteBean) {
        return utenti.stream()
                .anyMatch(utente -> utente.getUsername().equals(utenteBean.getUsername()) && utente.getPassword().equals(utenteBean.getPassword()));

    }
}
