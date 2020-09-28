package com.dummy.myerp.testconsumer.consumer;

import com.dummy.myerp.consumer.dao.impl.db.dao.ComptabiliteDaoImpl;
import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.exception.NotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class ConsumerTest extends ConsumerTestCase {

    private ComptabiliteDaoImpl vDao = ComptabiliteDaoImpl.getInstance();

    private EcritureComptable createEcritureComptable(Integer id, JournalComptable journalComptable, String reference, Date date, String libelle, List<LigneEcritureComptable> listLigneEcriture) {
        EcritureComptable ecritureComptable = new EcritureComptable();
        ecritureComptable.setId(id);
        ecritureComptable.setJournal(journalComptable);
        ecritureComptable.setReference(reference);
        ecritureComptable.setDate(date);
        ecritureComptable.setLibelle(libelle);
        ecritureComptable.getListLigneEcriture().clear();
        ecritureComptable.getListLigneEcriture().addAll(listLigneEcriture);

        return ecritureComptable;
    }

    /**
     * Vérification de l'initialisation du context Spring
     */
    @Test
    public void testInitSpring() {
        SpringRegistry.init();
        assertNotNull(SpringRegistry.getDaoProxy());
    }

    /**
     * Vérification récupération liste des comptes comptables.
     * Test passant :
     * - taille de la liste > 0
     * - contient un élément ayant comme numéro "401" et un libellé "Fournisseurs"
     */
    @Test
    public void getListCompteComptableTest() {
        List<CompteComptable> vListComptes = vDao.getListCompteComptable();

        assertTrue("La liste des comptes est vide", vListComptes.size() > 0 );
        Iterator it = vListComptes.listIterator();
        boolean testOk = false;
        while (it.hasNext()) {
            CompteComptable compte = (CompteComptable) it.next();
            if(compte.getNumero() == 401 && StringUtils.equals(compte.getLibelle(),"Fournisseurs")) {
                testOk = true;
                break;
            }
        }
        assertTrue("Le compte de numéro 401 et de libellé Fournisseurs n'a pas été trouvé", testOk);
    }

    /**
     * Vérification récupération liste des journaux comptables.
     * Test passant :
     * - taille de la liste > 0
     * - contient un élément ayant comme numéro "AC"  et un libellé "Achat"
     */
    @Test
    public void getListJournalComptableTest() {
        List<JournalComptable> vListJournaux = vDao.getListJournalComptable();
        
        assertTrue("La liste des journaux est vide", vListJournaux.size() > 0);
        Iterator it = vListJournaux.listIterator();
        boolean testOk = false;
        while (it.hasNext()) {
            JournalComptable journal = (JournalComptable) it.next();
            if(StringUtils.equals(journal.getCode(), "AC") && StringUtils.equals(journal.getLibelle(), "Achat")) {
                testOk = true;
                break;
            }
        }
        assertTrue("Le journal de code AC et de libellé Achat n'a pas été trouvé", testOk);
    }

    /**
     * Vérification récupération liste des écritures comptables.
     * Test passant :
     * - id = -5
     * - code journal = BQ
     * - reference = BQ-2016/00005
     * - data = 2016/12/27 00:00:00
     * - libellé = Paiement Facture C110002
     * - 2 lignes d'écriture associées à cette écriture
     * - Code compte comptable ligne écriture 2 = 411
     * - Crédit ligne écriture 2 = 3000
     *
     */
    @Test
    public void getListEcritureComptableTest() throws ParseException {
        List<EcritureComptable> vListEcritures = vDao.getListEcritureComptable();

        assertTrue("La liste des écritures est vide", vListEcritures.size() > 0);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        Date dateAttendue = dateFormat.parse("2016/12/27 00:00:00");
        Iterator it = vListEcritures.listIterator();
        boolean testOk = false;
        while (it.hasNext()) {
            EcritureComptable ecriture = (EcritureComptable) it.next();
            if(ecriture.getId() == -5 &&
                StringUtils.equals(ecriture.getJournal().getCode(), "BQ") &&
                StringUtils.equals(ecriture.getReference(), "BQ-2016/00005") &&
                StringUtils.equals(ecriture.getLibelle(), "Paiement Facture C110002") &&
                ecriture.getDate().compareTo(dateAttendue) == 0 &&
                ecriture.getListLigneEcriture().size() == 2 &&
                ecriture.getListLigneEcriture().get(1).getCompteComptable().getNumero() == 411 &&
                ecriture.getListLigneEcriture().get(1).getCredit().compareTo(BigDecimal.valueOf(3000)) == 0)
            {
                testOk = true;
                break;
            }
        }
        assertTrue("L'écriture comptabel d'ID -5, de code journal BQ, de référence BQ-2016/00005, de date 2016/12/27 00:00:00 et de libellé Paiement Facture C110002 n'a pas été trouvé", testOk);
    }

    /**
     * Vérification récupération d'une écriture comptable par son id.
     * Test passant.
     * - id = -4
     * - code journal = VE
     * - reference = VE-2016/00004
     * - data = 2016/12/28 00:00:00
     * - libellé = TMA Appli Yyy
     * - 3 ligne d'écriture associées à cette écriture
     * - Code compte comptable ligne écriture 2 = 706
     * - Libellé de la ligne écriture 2 = TMA Appli Xxx
     *
     */
    @Test
    public void getEcritureComptableTest() throws ParseException {
        EcritureComptable ecriture = new EcritureComptable();
        try {
            ecriture = vDao.getEcritureComptable(-4);
        } catch (NotFoundException e) {
            fail();
        }
        boolean testOk = false;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        Date dateAttendue = dateFormat.parse("2016/12/28 00:00:00");

        if(ecriture.getId() == -4 &&
            StringUtils.equals(ecriture.getJournal().getCode(), "VE") &&
            StringUtils.equals(ecriture.getReference(), "VE-2016/00004") &&
            StringUtils.equals(ecriture.getLibelle(), "TMA Appli Yyy") &&
            ecriture.getDate().compareTo(dateAttendue) == 0 &&
            ecriture.getListLigneEcriture().size() == 3 &&
            ecriture.getListLigneEcriture().get(1).getCompteComptable().getNumero() == 706 &&
            StringUtils.equals(ecriture.getListLigneEcriture().get(1).getLibelle(), "TMA Appli Xxx"))
        {
            testOk = true;
        }
        assertTrue("L'écriture comptabel d'ID -4, de code journal VE, de référence VE-2016/00004, de date 2016/12/28 00:00:00 et de libellé TMA Appli Yyy n'a pas été récupéré correctement", testOk);
    }

    /**
     * Vérification récupération d'une écriture comptable qui n'existe pas par son id.
     * Doit renvoyer une exception NotFoundException
     *
     * @throws NotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void getEcritureComptableTestNotFound() throws NotFoundException {
        vDao.getEcritureComptable(99);
    }

    /**
     * Vérification récupération d'une écriture comptable par sa référence
     * Test passant.
     * - id = -4
     * - code journal = VE
     * - reference = VE-2016/00004
     * - data = 2016/12/28 00:00:00
     * - libellé = TMA Appli Yyy
     * - 3 ligne d'écriture associées à cette écriture
     * - Code compte comptable ligne écriture 2 = 706
     * - Libellé de la ligne écriture 2 = TMA Appli Xxx
     *
     */
    @Test
    public void getEcritureComptableByRefTest() throws ParseException {
        EcritureComptable ecriture = new EcritureComptable();
        try {
            ecriture = vDao.getEcritureComptableByRef("VE-2016/00004");
        } catch (NotFoundException e) {
            fail();
        }
        boolean testOk = false;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        Date dateAttendue = dateFormat.parse("2016/12/28 00:00:00");

        if(ecriture.getId() == -4 &&
            StringUtils.equals(ecriture.getJournal().getCode(), "VE") &&
            StringUtils.equals(ecriture.getReference(), "VE-2016/00004") &&
            StringUtils.equals(ecriture.getLibelle(), "TMA Appli Yyy") &&
            ecriture.getDate().compareTo(dateAttendue) == 0 &&
            ecriture.getListLigneEcriture().size() == 3 &&
            ecriture.getListLigneEcriture().get(1).getCompteComptable().getNumero() == 706 &&
            StringUtils.equals(ecriture.getListLigneEcriture().get(1).getLibelle(), "TMA Appli Xxx"))
        {
            testOk = true;
        }
        assertTrue("L'écriture comptabel d'ID -4, de code journal VE, de référence VE-2016/00004, de date 2016/12/28 00:00:00 et de libellé TMA Appli Yyy n'a pas été récupéré correctement", testOk);
    }

    /**
     * Vérification récupération d'une écriture comptable qui n'existe pas par sa référence.
     * Doit renvoyer une exception NotFoundException
     *
     * @throws NotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void getEcritureComptableByRefTestNotFound() throws NotFoundException {
        vDao.getEcritureComptableByRef("ZZ-2018/90004");
    }

    /**
     * Vérification du chargement de la liste des lignes d'écriture dans une écriture.
     * Test passant.
     *
     * Le test vérifie que la ligne d'écriture présente est bien remplacé par la liste chargé.
     * - id = -4
     * - code journal = VE
     * - reference = VE-2016/00004
     * - data = 2016/12/28 00:00:00
     * - libellé = TMA Appli Yyy
     * - 3 ligne d'écriture associées à cette écriture
     * - Code compte comptable ligne écriture 2 = 706
     * - Libellé de la ligne écriture 2 = TMA Appli Xxx
     *
     */
    @Test
    public void loadListLigneEcritureTest() throws ParseException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        Date date = dateFormat.parse("2016/12/28 00:00:00");

        JournalComptable journal = new JournalComptable("VE", "Vente");

        CompteComptable compte = new CompteComptable(999);
        LigneEcritureComptable ligneEcriture = new LigneEcritureComptable(compte, "Ligne1", BigDecimal.valueOf(100), null);
        List<LigneEcritureComptable> listLigneEcriture = new ArrayList<>();
        listLigneEcriture.add(ligneEcriture);

        EcritureComptable ecriture = createEcritureComptable(-4, journal, "VE-2016/00004", date,"TMA Appli Yyy", listLigneEcriture );

        vDao.loadListLigneEcriture(ecriture);

        boolean testOk = false;
        if(ecriture.getId() == -4 &&
        StringUtils.equals(ecriture.getJournal().getCode(), "VE") &&
        StringUtils.equals(ecriture.getReference(), "VE-2016/00004") &&
        StringUtils.equals(ecriture.getLibelle(), "TMA Appli Yyy") &&
        ecriture.getDate().compareTo(date) == 0 &&
        ecriture.getListLigneEcriture().size() == 3 &&
        ecriture.getListLigneEcriture().get(0).getCompteComptable().getNumero() != 999 &&
        ecriture.getListLigneEcriture().get(1).getCompteComptable().getNumero() == 706 &&
        StringUtils.equals(ecriture.getListLigneEcriture().get(1).getLibelle(), "TMA Appli Xxx"))
        {
            testOk = true;
        }
        assertTrue("Le chargement des lignes d'écritures ne s'est pas fait correctement", testOk);
    }

    /**
     * Vérification de l'insertion d'une écriture comptable
     * Test passant
     * - id = non connu
     * - Journal : premier la liste (
     * - reference = ZZ-2018/00001
     * - data = 2018/12/19 00:00:00
     * - libellé = ecriture insérée
     * - 2 ligne d'écriture associées à cette écriture
     * - Compte comptable associé aux écritures : premier de la liste
     * - Libellé première écriture : Ligne1
     * - Libellé seconde écriture : Ligne2
     *
     */
    @Test
    public void insertEcritureComptableTest() throws ParseException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        Date date = dateFormat.parse("2018/12/19 00:00:00");

        JournalComptable journal = vDao.getListJournalComptable().get(0);

        CompteComptable compte = vDao.getListCompteComptable().get(0);
        LigneEcritureComptable ligneEcriture = new LigneEcritureComptable(compte, "Ligne1", BigDecimal.valueOf(1500), null);
        LigneEcritureComptable ligneEcriture2 = new LigneEcritureComptable(compte, "Ligne2", null, BigDecimal.valueOf(1500));
        List<LigneEcritureComptable> listLigneEcriture = new ArrayList<>();
        listLigneEcriture.add(ligneEcriture);
        listLigneEcriture.add(ligneEcriture2);

        EcritureComptable ecritureComptable = createEcritureComptable(null, journal, "ZZ-2018/00001", date, "ecriture insérée", listLigneEcriture);
        vDao.insertEcritureComptable(ecritureComptable);
        boolean testOk = false;
        try {
            EcritureComptable ecritureInseree = vDao.getEcritureComptable(ecritureComptable.getId());
            if( ecritureInseree.getId().equals(ecritureComptable.getId()) &&
                ecritureInseree.getDate().compareTo(ecritureComptable.getDate()) == 0 &&
                StringUtils.equals(ecritureInseree.getJournal().getCode(), ecritureComptable.getJournal().getCode()) &&
                StringUtils.equals(ecritureInseree.getLibelle(), ecritureComptable.getLibelle()) &&
                StringUtils.equals(ecritureInseree.getReference(), ecritureComptable.getReference()) &&
                ecritureInseree.getListLigneEcriture().size() == ecritureComptable.getListLigneEcriture().size() &&
                StringUtils.equals(ecritureInseree.getListLigneEcriture().get(0).getLibelle(), ecritureComptable.getListLigneEcriture().get(0).getLibelle()) &&
                StringUtils.equals(ecritureInseree.getListLigneEcriture().get(1).getLibelle(), ecritureComptable.getListLigneEcriture().get(1).getLibelle())) {

                testOk = true;
            }
        } catch (NotFoundException e) {
            fail();
        }
        assertTrue("L'insertion de l'écriture comptable ne s'est pas faite correctement", testOk);
        vDao.deleteEcritureComptable(ecritureComptable.getId());
    }

    /**
     * Vérification update d'une écriture comptable
     * Test passant.
     *
     * On crée une écriture. On crée une seconde écriture avec le même id.
     * On demande la modification en base en envoyant la seconde écriture.
     * On vérifie que la première écriture est bien modifié avec les éléments de la seconde.
     *
     */
    @Test
    public void updateEcritureComptableTest() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        Date date = dateFormat.parse("2018/12/19 00:00:00");

        JournalComptable journal = vDao.getListJournalComptable().get(0);

        CompteComptable compte = vDao.getListCompteComptable().get(0);
        LigneEcritureComptable ligneEcriture = new LigneEcritureComptable(compte, "Ligne1", BigDecimal.valueOf(1500), null);
        LigneEcritureComptable ligneEcriture2 = new LigneEcritureComptable(compte, "Ligne2", null, BigDecimal.valueOf(1500));
        List<LigneEcritureComptable> listLigneEcriture = new ArrayList<>();
        listLigneEcriture.add(ligneEcriture);
        listLigneEcriture.add(ligneEcriture2);

        EcritureComptable ecritureComptable = createEcritureComptable(null, journal, "ZZ-2018/00001", date, "ecriture insérée", listLigneEcriture);
        vDao.insertEcritureComptable(ecritureComptable);

        Date date2 = dateFormat.parse("2018/12/19 00:00:00");
        JournalComptable journal2 = vDao.getListJournalComptable().get(1);

        CompteComptable compte2 = vDao.getListCompteComptable().get(0);
        LigneEcritureComptable ligneEcriture3 = new LigneEcritureComptable(compte, "Ligne3", BigDecimal.valueOf(3500), null);
        LigneEcritureComptable ligneEcriture4 = new LigneEcritureComptable(compte, "Ligne4", null, BigDecimal.valueOf(3500));
        List<LigneEcritureComptable> listLigneEcriture2 = new ArrayList<>();
        listLigneEcriture2.add(ligneEcriture3);
        listLigneEcriture2.add(ligneEcriture4);

        EcritureComptable ecritureComptableModifiee = createEcritureComptable(ecritureComptable.getId(), journal2, "YY-2018/00001", date2, "ecriture modifié", listLigneEcriture2);
        vDao.updateEcritureComptable(ecritureComptableModifiee);

        boolean testOk = false;
        try {
            EcritureComptable ecriture = vDao.getEcritureComptable(ecritureComptable.getId());
            if( ecriture.getDate().compareTo(ecritureComptableModifiee.getDate()) == 0 &&
                StringUtils.equals(ecriture.getJournal().getCode(), ecritureComptableModifiee.getJournal().getCode()) &&
                StringUtils.equals(ecriture.getLibelle(), ecritureComptableModifiee.getLibelle()) &&
                StringUtils.equals(ecriture.getReference(), ecritureComptableModifiee.getReference()) &&
                ecriture.getListLigneEcriture().size() == ecritureComptableModifiee.getListLigneEcriture().size() &&
                StringUtils.equals(ecriture.getListLigneEcriture().get(0).getLibelle(), ecritureComptableModifiee.getListLigneEcriture().get(0).getLibelle()) &&
                StringUtils.equals(ecriture.getListLigneEcriture().get(1).getLibelle(), ecritureComptableModifiee.getListLigneEcriture().get(1).getLibelle()))
            {
                testOk = true;
            }
        } catch (NotFoundException e) {
            fail();
        }
        assertTrue("La mise à jour de l'écriture comptable ne s'est pas faite correctement", testOk);
        vDao.deleteEcritureComptable(ecritureComptableModifiee.getId());
    }

    /**
     * Vérification de la suppression d'une écriture comptable.
     * Test passant
     *
     */
    @Test
    public void deleteEcritureComptableTest() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        Date date = dateFormat.parse("2018/12/19 00:00:00");

        JournalComptable journal = vDao.getListJournalComptable().get(0);

        CompteComptable compte = vDao.getListCompteComptable().get(0);
        LigneEcritureComptable ligneEcriture = new LigneEcritureComptable(compte, "Ligne1", BigDecimal.valueOf(1500), null);
        LigneEcritureComptable ligneEcriture2 = new LigneEcritureComptable(compte, "Ligne2", null, BigDecimal.valueOf(1500));
        List<LigneEcritureComptable> listLigneEcriture = new ArrayList<>();
        listLigneEcriture.add(ligneEcriture);
        listLigneEcriture.add(ligneEcriture2);

        EcritureComptable ecritureComptable = createEcritureComptable(null, journal, "ZZ-2018/00001", date, "ecriture insérée", listLigneEcriture);
        vDao.insertEcritureComptable(ecritureComptable);

        vDao.deleteEcritureComptable(ecritureComptable.getId());
        boolean testOk = false;
        try {
            vDao.getEcritureComptable(ecritureComptable.getId());
        } catch (NotFoundException e) {
            testOk = true;
        }

        List<LigneEcritureComptable> listLigneEcriture2 = new ArrayList<>();
        EcritureComptable ecritureComptableTest = createEcritureComptable(null, journal, "ZZ-2018/00001", date, "ecriture insérée", listLigneEcriture2);
        vDao.loadListLigneEcriture(ecritureComptableTest);
        if(ecritureComptableTest.getListLigneEcriture().size() == ecritureComptable.getListLigneEcriture().size()) {
            testOk = false;
        }
        assertTrue("La suppression de l'écriture comptable ne s'est pas faite correctement", testOk);
    }

    /**
     * Vérification de la récupération d'une séquence d'écriture comptable.
     * Test passant:
     * code journal : AC
     * année : 2016
     * dernière valeur : 40
     */
    @Test
    public void getSequenceEcritureComptableTest() throws NotFoundException {
        SequenceEcritureComptable seq = vDao.getSequenceEcritureComptable("AC", 2016);

        assertEquals("La séquence d'écriture comptable n'a pas été trouvé", seq.getDerniereValeur(), Integer.valueOf(40));
    }

    /**
     * Vérification de la récupération d'une séquence d'écriture comptable.
     * Test sequence qui n'existe pas.
     * Doit renvoyer une exception Not Found Exception
     *
     * @throws NotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void getSequenceEcritureComptableNotFound() throws NotFoundException {
        vDao.getSequenceEcritureComptable("ZZ", 2020);
    }

    /**
     * Vérification de la suppression d'une séquence d'écriture comptable
     * On insert une séquence, on la supprime puis on vérifie qu'elle n'existe plus
     * On doit récupérer une Not Found Exception en cherchant l'élément supprimé
     *
     * @throws NotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void deleteSequenceEcritureComptableTest() throws NotFoundException {
        SequenceEcritureComptable sequence = new SequenceEcritureComptable("AC", 2055, 555);
        vDao.insertSequenceEcritureComptable(sequence);

        vDao.deleteSequenceEcritureComptable("AC", 2055);
        // On vérifie que la suppression s'est bien réalisée en cherchant l'élément supprimé
        vDao.getSequenceEcritureComptable("AC", 2055);
    }

    /**
     * Vérification de l'insertion d'une séquence d'écriture comptable
     * Test passant.
     *
     */
    @Test
    public void insertSequenceEcritureComptableTest() throws NotFoundException {

        SequenceEcritureComptable sequence = new SequenceEcritureComptable("AC", 2055, 555);
        vDao.insertSequenceEcritureComptable(sequence);

        boolean testOk = false;
        SequenceEcritureComptable sequenceInseree = vDao.getSequenceEcritureComptable("AC", 2055);

        assertEquals("L'insertion de la séquence ne s'est pas fait correctement", sequenceInseree.getDerniereValeur(), sequence.getDerniereValeur());
        vDao.deleteSequenceEcritureComptable("AC", 2055);
    }

    @Test
    public void updateSequenceEcritureComptableTest() throws NotFoundException {
        SequenceEcritureComptable sequence = new SequenceEcritureComptable("AC", 2055, 555);
        vDao.insertSequenceEcritureComptable(sequence);

        SequenceEcritureComptable sequence2 = new SequenceEcritureComptable("AC", 2055, 666);
        vDao.updateSequenceEcritureComptable(sequence2);

        SequenceEcritureComptable sequenceModifiee = vDao.getSequenceEcritureComptable("AC", 2055);
        assertEquals("La mise à jour ne s'est pas fait correctement", sequenceModifiee.getDerniereValeur(), sequence2.getDerniereValeur());
        vDao.deleteSequenceEcritureComptable("AC", 2055);
    }
}