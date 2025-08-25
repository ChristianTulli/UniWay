package uniway.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uniway.beans.UtenteBean;
import uniway.controller.IscrittoSelezionaCorsoController;
import uniway.controller.LogInController;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//ESEGUIRE IL TEST IN MODALITA' DEMO
public class IscrittoSelezionaCorsoDemoTest {

    @Test
    @DisplayName("registrazione utente iscritto e selezione corso")
    void registrazioneUtenteIscritto() throws IOException {
        LogInController controller = new LogInController();

        final String ui = "ui";
        final String p = "111111";
        final boolean i = true;

        UtenteBean utenteBean= new UtenteBean(ui,p,i);

        boolean maybe = controller.registrazione(utenteBean);

        assertTrue(maybe, "registrazione fallita");

        IscrittoSelezionaCorsoController c = new IscrittoSelezionaCorsoController();

        String corso="Ingegneria Informatica";
        //SELEZIONE FILTRI
        List<String> regioni =c.getRegioni();
        assertNotNull(regioni, "Regioni è null");
        assertTrue(regioni.contains("LAZIO"), "Regione LAZIO non trovata tra " + regioni);

        List<String> province = c.getProvince("LAZIO");
        assertNotNull(province, "Province per LAZIO è null");
        assertTrue(province.contains("ROMA"), "Provincia ROMA non trovata tra " + province);

        List<String> comuni = c.getComuni("ROMA");
        assertNotNull(comuni, "Comuni per provincia ROMA è null");
        assertTrue(comuni.contains("ROMA"), "Comune ROMA non trovato tra " + comuni);

        List<String> atenei = c.getAtenei("ROMA");
        assertNotNull(atenei, "Atenei per comune ROMA è null");
        assertTrue(atenei.contains("Universita' degli studi di Roma Tor Vergata"), "Universita' degli studi di Roma Tor Vergata non trovato tra " + atenei);

        List<String> discipline = c.getDiscipline("Universita' degli studi di Roma Tor Vergata");
        assertNotNull(discipline, "discipline per Universita' degli studi di Roma Tor Vergata è null");
        assertTrue(discipline.contains("Ingegneria industriale e dell'informazione"), "Ingegneria industriale e dell'informazione non trovato tra " + discipline);

        List<String> tipologie = c.getTipologie("Ingegneria industriale e dell'informazione");
        assertNotNull(tipologie, "tipologie per Ingegneria industriale e dell'informazione è null");
        assertTrue(tipologie.contains("Laurea triennale"), "Laurea triennale non trovato tra " + tipologie);

        List<String> corsi = c.getCorsi("Laurea triennale");
        assertNotNull(corsi, "corso per Laurea triennale è null");
        assertTrue(corsi.contains("Ingegneria dell'informazione"), "Ingegneria dell'informazione non trovato tra " + corsi);

        List<String> risultati = c.getRisultati("Ingegneria dell'informazione");
        assertNotNull(risultati, "risultati per Ingegneria dell'informazione è null");
        assertFalse(risultati.isEmpty(), "Nessun risultato per Ingegneria dell'informazione");
        assertTrue(risultati.contains(corso), "Ingegneria Informatica non trovato tra " + risultati);

        List<String> curriculum = c.getCurriculumPerCorso(corso);
        assertNotNull(curriculum, "curriculum per Ingegneria Informatica è null");
        assertTrue(curriculum.contains("Sistemi software e Web"), "Sistemi software e Web non trovato tra " + curriculum);

        c.setCurriculumUtente(utenteBean, "Sistemi software e Web");
        c.setCorsoUtente(utenteBean, corso);

        System.out.println("Corso selezionato: \n Universita' degli studi di Roma Tor Vergata \n Ingegneria dell'informazione \n Sistemi software e Web");
    }
}
