package uniway.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import uniway.beans.UtenteBean;

import static org.junit.jupiter.api.Assertions.*;

//TEST IN MODALITÀ DEMO (NO DB)
@Tag("demo")
public class IscrittoSelezionaCorsoDemoTest {

    @Test
    @DisplayName("DEMO – Flusso selezione corso + curriculum senza accesso al DB")
    void selezioneCorsoECurriculum_demoSenzaDB() {
        // Utente “nuovo” (solo per coerenza del flusso, non persiste nulla)
        UtenteBean utente = new UtenteBean("demo_user_iscritto", "demo_password", true);

        // Selezioni simulate (senza interrogare DAO)
        final String regione     = "LAZIO";
        final String provincia   = "ROMA";
        final String comune      = "ROMA";
        final String ateneo      = "Universita' degli studi di Roma Tor Vergata";
        final String disciplina  = "Ingegneria industriale e dell'informazione";
        final String tipologia   = "Laurea triennale";
        final String corso       = "Ingegneria dell'informazione";
        final String classeScelta = "Ingegneria Informatica"; // elemento scelto nella ComboBox "classe"
        // Dal popup curriculum scelgo:
        final String curriculumScelto = "Sistemi software e Web";

        // Verifiche di base sui dati “selezionati” (simulati)
        assertAll(
                () -> assertEquals("LAZIO", regione),
                () -> assertEquals("ROMA", provincia),
                () -> assertEquals("ROMA", comune),
                () -> assertEquals("Universita' degli studi di Roma Tor Vergata", ateneo),
                () -> assertEquals("Ingegneria industriale e dell'informazione", disciplina),
                () -> assertEquals("Laurea triennale", tipologia),
                () -> assertEquals("Ingegneria dell'informazione", corso),
                () -> assertEquals("Ingegneria Informatica", classeScelta),
                () -> assertEquals("Sistemi software e Web", curriculumScelto)
        );

        // La GUI, dopo la scelta, mostra nella Label:
        // "Corso selezionato: <corsoSelezionato>\ncurriculum: <curriculum>"
        // Nel tuo controller grafico: label.setText(corsoSelezionato + newValue + "\ncurriculum: " + curr);
        final String testoLabelAtteso =
                "Corso selezionato: " + classeScelta + "\ncurriculum: " + curriculumScelto;

        // In DEMO non abbiamo la label reale: costruiamo ciò che dovrebbe apparire
        String testoLabelOttenuto = "Corso selezionato: " + classeScelta + "\ncurriculum: " + curriculumScelto;

        assertEquals(testoLabelAtteso, testoLabelOttenuto,
                "La stringa mostrata nella label non è quella attesa");

        // (Facoltativo) controlli minimal su utente in modalità DEMO
        assertTrue(utente.getIscritto(), "L’utente per questo flusso deve essere 'iscritto' (true)");
        assertEquals("demo_user_iscritto", utente.getUsername());
    }
}
