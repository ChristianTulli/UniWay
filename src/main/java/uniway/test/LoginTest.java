package uniway.test;


import org.junit.Test;
import uniway.beans.UtenteBean;
import uniway.controller.LogInController;

import java.util.Optional;

import static org.junit.Assert.*;

public class LoginTest {

    @Test
    public void testLoginPasswordErrata() {
        LogInController controller = new LogInController();
        String username = "ui";//utente esistente
        String passwordCorretta = "111111";
        String passwordErrata = "passwordSbagliata";

        UtenteBean utente = new UtenteBean(username, passwordCorretta, true);

        try {
            controller.registrazione(utente);

            Optional<UtenteBean> result = controller.autenticazione(username, passwordErrata);

            assertFalse("Il login non dovrebbe riuscire con password errata", result.isPresent());

        } catch (Exception e) {
            fail("Eccezione non prevista: " + e.getMessage());
        }
    }

    @Test
    public void testLoginUtenteInCerca() {
        LogInController controller = new LogInController();
        String username = "uc";//utente registrato come in cerca
        String password = "111111";

        UtenteBean utente = new UtenteBean(username, password, false);

        try {
            controller.registrazione(utente);

            Optional<UtenteBean> result = controller.autenticazione(username, password);

            assertTrue("Login fallito per utente in cerca", result.isPresent());
            UtenteBean loggato = result.get();

            assertEquals(username, loggato.getUsername());
            assertEquals(password, loggato.getPassword());
            assertFalse("L'utente non dovrebbe essere iscritto", loggato.getIscritto());

        } catch (Exception e) {
            fail("Eccezione imprevista: " + e.getMessage());
        }
    }

    @Test
    public void testLoginUsernameInesistente() {
        LogInController controller = new LogInController();
        String usernameNonRegistrato = "non_registrato";//username non registrato
        String password = "qualcosa";

        try {
            Optional<UtenteBean> result = controller.autenticazione(usernameNonRegistrato, password);
            assertFalse("Il login non dovrebbe riuscire con un username inesistente", result.isPresent());
        } catch (Exception e) {
            fail("Eccezione non prevista: " + e.getMessage());
        }
    }


}


