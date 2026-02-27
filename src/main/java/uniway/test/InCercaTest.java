package uniway.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uniway.beans.UtenteBean;
import uniway.controller.InCercaTrovaCorsoController;
import uniway.controller.LogInController;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

//ESEGUIRE IL TEST IN MODALITA' FULL

public class InCercaTest {

    @Test
    @DisplayName("Login reale utente in cerca (uc / 111111)")
    void loginUtenteInCerca() throws IOException {
        LogInController controller = new LogInController();

        final String uc = "uc";
        final String p = "111111";

        Optional<UtenteBean> maybe = controller.autenticazione(uc, p);

        assertTrue(maybe.isPresent(), "Autenticazione fallita contro DB reale");
        UtenteBean u = maybe.get();

        assertEquals(uc, u.getUsername(), "Username diverso da quello atteso");
        assertEquals(p, u.getPassword(), "Password differente da quella salvata");
        assertFalse(u.getIscritto(), "L'utente 'uc' deve essere in cerca (non iscritto)");
    }

    @Test
    @DisplayName("Ricerca corsi con filtri")
    void ricercaCorsiFiltriRichiesti() {
        InCercaTrovaCorsoController c = new InCercaTrovaCorsoController();

        // COLONNA 1: TIPOLOGIA ATENEO
        // 1) Tipo ateneo Statale -> Tipologia Universita'
        List<String> statale = c.getTipiAteneo();
        assertNotNull(statale, "getTipiAteneo ha restituito null");
        assertTrue(statale.contains("Statale   "), "Statale non trovata tra " + statale);

        List<String> tipologie = c.getTipologie("Statale   ");
        assertNotNull(tipologie, "getTipologie('Statale') ha restituito null");
        assertTrue(tipologie.contains("Universita'"), "Universita' non trovata tra " + tipologie);
        c.setTipologia("Universita'");


        // COLONNA 2: UBICAZIONE
        // 2) Regione LAZIO -> Provincia ROMA -> Comune ROMA
        List<String> regioni =c.getRegioni();
        assertNotNull(regioni, "Regioni è null");
        assertTrue(regioni.contains("LAZIO"), "Regione LAZIO non trovata tra " + regioni);

        List<String> province = c.getProvince("LAZIO");
        assertNotNull(province, "Province per LAZIO è null");
        assertTrue(province.contains("ROMA"), "Provincia ROMA non trovata tra " + province);

        List<String> comuni = c.getComuni("ROMA");
        assertNotNull(comuni, "Comuni per provincia ROMA è null");
        assertTrue(comuni.contains("ROMA"), "Comune ROMA non trovato tra " + comuni);
        c.setComune("ROMA");

        // COLONNA 3: CARATTERISTICHE CORSO
        // 3) Durata: Laurea triennale
        List<String> durate =c.getDurate();
        assertNotNull(durate, "durate è null");
        assertTrue(durate.contains("Laurea triennale"), "Laurea triennale non trovata tra " + durate);

        List<String> discipline = c.getDiscipline("Laurea triennale");
        assertNotNull(discipline, "Discipline per 'Laurea triennale' è null");
        assertTrue(
                discipline.stream().anyMatch(d -> d.equalsIgnoreCase("Ingegneria industriale e dell'informazione")),
                "Gruppo disciplina 'Ingegneria industriale e dell'informazione' non presente"
        );

        // 4) Gruppo: Ingegneria industriale e dell'informazione -> Classe: Ingegneria dell'informazione
        List<String> classi = c.getClassi("Ingegneria industriale e dell'informazione");
        assertNotNull(classi, "Classi per gruppo 'Ingegneria industriale e dell'informazione' è null");
        assertTrue(
                classi.stream().anyMatch(cl -> cl.equalsIgnoreCase("Ingegneria dell'informazione")),
                "Classe corso 'Ingegneria dell'informazione' non presente"
        );
        c.setClasseCorso("Ingegneria dell'informazione");

        // CERCA
        List<String> risultati = c.getRisultati();
        assertNotNull(risultati, "getRisultati() ha restituito null");
        assertFalse(risultati.isEmpty(), "Nessun risultato trovato con i filtri richiesti");

        // stampiamo i risultati
        for (String r : risultati) {
            assertNotNull(r);
            assertFalse(r.isBlank(), "Trovato un risultato vuoto");
            System.out.println("Risultato: " + r);
        }
    }
}
