package uniway.test;


import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import uniway.beans.UtenteBean;
import uniway.controller.LogInController;

import java.util.Optional;

import static org.junit.Assert.*;

public class LoginTest {

    //ESEGUIRE TEST IN MODALITA' FULL

    @Test
    @DisplayName("password errata")
    public void testLoginPasswordErrata() {
        LogInController controller = new LogInController();
        String ui = "ui";//utente esistente
        String pc = "111111";
        String pe = "blabla";

        UtenteBean utente = new UtenteBean(ui, pc, true);

        try {
            controller.registrazione(utente);

            Optional<UtenteBean> result = controller.autenticazione(ui, pe);

            assertFalse("Il login non dovrebbe riuscire con password errata", result.isPresent());

        } catch (Exception e) {
            fail("Eccezione non prevista: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("log in utente in cerca")
    public void testLoginUtenteInCerca() {
        LogInController controller = new LogInController();
        final String uc = "uc";//utente registrato come in cerca
        final String p = "111111";

        UtenteBean utente = new UtenteBean(uc, p, false);

        try {
            controller.registrazione(utente);

            Optional<UtenteBean> result = controller.autenticazione(uc, p);

            assertTrue("Login fallito per utente in cerca", result.isPresent());
            UtenteBean loggato = result.get();

            assertEquals(uc, loggato.getUsername());
            assertEquals(p, loggato.getPassword());
            assertFalse("L'utente non dovrebbe essere iscritto", loggato.getIscritto());

        } catch (Exception e) {
            fail("Eccezione imprevista: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Username inesistente")
    public void testLoginUsernameInesistente() {
        LogInController controller = new LogInController();
        final String un = "non_registrato";//username non registrato
        final String p = "blabla";

        try {
            Optional<UtenteBean> result = controller.autenticazione(un, p);
            assertFalse("Il login non dovrebbe riuscire con un username inesistente", result.isPresent());
        } catch (Exception e) {
            fail("Eccezione non prevista: " + e.getMessage());
        }
    }


}


