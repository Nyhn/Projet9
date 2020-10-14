package com.dummy.myerp.testbusiness.business;

import com.dummy.myerp.business.impl.manager.ComptabiliteManagerImpl;
import com.dummy.myerp.consumer.dao.impl.db.dao.ComptabiliteDaoImpl;
import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;

public class BusinessTest extends BusinessTestCase {

    private ComptabiliteManagerImpl comptabiliteManager = new ComptabiliteManagerImpl();
    private ComptabiliteDaoImpl comptabiliteDao = ComptabiliteDaoImpl.getInstance();

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
     * Test de la récupération de la liste des comptes comptables.
     */
    @Test
    public void getListCompteComptableTest_listCompteComptable_sizeListEqual7() {
        List<CompteComptable> listCompteComptable = comptabiliteManager.getListCompteComptable();
        assertEquals("Le nombre de compte doit être égale à 7",7, listCompteComptable.size());
    }

    /**
     * Test de la récupération de la liste des journaux comptables.
     */
    @Test
    public void getListJournalComptableTest_listJournalComptable_sizeListEqual4() {
        List<JournalComptable> listJournaux = comptabiliteManager.getListJournalComptable();
        assertEquals("Le nombre de journaux comptables doit être égale à 4", 4, listJournaux.size());
    }

    /**
     * Test de la récupération de la liste des écritures comptables.
     */
    @Test
    public void getListEcritureComptableTest_listEcritureComptable_sizeListEqual5() {
        List<EcritureComptable> listEcritures = comptabiliteManager.getListEcritureComptable();
        assertEquals("Le nombre d'écritures comptables doit être égale à 5", 5, listEcritures.size());
    }

    /**
     * Test d'insertion d'une écriture comptable
     * Création d'un écriture, puis insertion dans la BDD.
     * Récupération de l'écriture insérée et vérification d'égalité avec l'écriture d'origine.
     * Puis suppression de l'écriture insérée
     */
    @Test
    public void insertEcritureComptableTest_NoReturn_checkEcritureComptable() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        Date date = simpleDateFormat.parse("2018/12/19 00:00:00");

        JournalComptable journalComptable = new JournalComptable();

        List<JournalComptable> listJournalComptable = comptabiliteManager.getListJournalComptable();
        for (JournalComptable journal : listJournalComptable) {
            if (StringUtils.equals(journal.getCode(), "AC")) {
                journalComptable = journal;
            }
        }

        CompteComptable compteComptable = comptabiliteManager.getListCompteComptable().get(0);
        LigneEcritureComptable ligneEcritureComptable = new LigneEcritureComptable(compteComptable, "Ligne1", BigDecimal.valueOf(1500), null);
        LigneEcritureComptable ligneEcritureComptable1 = new LigneEcritureComptable(compteComptable, "Ligne2", null, BigDecimal.valueOf(1500));
        List<LigneEcritureComptable> ligneEcritureComptableList = new ArrayList<>();
        ligneEcritureComptableList.add(ligneEcritureComptable);
        ligneEcritureComptableList.add(ligneEcritureComptable1);

        EcritureComptable ecritureComptable = createEcritureComptable(null, journalComptable, "AC-2018/09999", date, "ecriture insérée", ligneEcritureComptableList);

        try {
            comptabiliteManager.insertEcritureComptable(ecritureComptable);
        } catch (FunctionalException e) {
            fail("Erreur de l'insertion : " + e.getMessage());
        }

        List<EcritureComptable> listEcritureComptable = comptabiliteManager.getListEcritureComptable();
        Iterator it = listEcritureComptable.listIterator();
        boolean testCheck = false;
        while (it.hasNext()) {
            EcritureComptable ecritureInseree = (EcritureComptable) it.next();
            if(ecritureInseree.getId() != null &&
                StringUtils.equals(ecritureInseree.getJournal().getCode(), ecritureComptable.getJournal().getCode()) &&
                StringUtils.equals(ecritureInseree.getReference(), ecritureComptable.getReference()) &&
                StringUtils.equals(ecritureInseree.getLibelle(), ecritureComptable.getLibelle()) &&
                ecritureInseree.getDate().compareTo(ecritureComptable.getDate()) == 0 &&
                ecritureInseree.getListLigneEcriture().size() == ecritureComptable.getListLigneEcriture().size() &&
                ecritureInseree.getListLigneEcriture().get(1).getCompteComptable().getNumero().equals(ecritureComptable.getListLigneEcriture().get(1).getCompteComptable().getNumero()) &&
                ecritureInseree.getListLigneEcriture().get(1).getCredit().compareTo(ecritureComptable.getListLigneEcriture().get(1).getCredit()) == 0)
            {
                testCheck = true;
            }
        }
       assertTrue("Les données de l'écriture insérée n'est pas égales à l'écriture d'origine", testCheck);
        comptabiliteManager.deleteEcritureComptable(ecritureComptable.getId());
    }

    /**
     * Test d'insertion d'une écriture ne respectant pas les règles de gestion.
     * vérification de l'écriture avant insertion
     * @throws FunctionalException
     */
    @Test (expected = FunctionalException.class)
    public void insertEcritureComptable_NoReturn_throwFunctionalException() throws FunctionalException {
        EcritureComptable ecritureComptable = new EcritureComptable();
        comptabiliteManager.insertEcritureComptable(ecritureComptable);
    }

    /**
     *  Test d'ajout d'une référence à une écriture comptable.
     *  Puis vérification que la référence a bien été ajouté à l'écriture
     *  Vérification que l'écriture respecte les règles de gestion.
     *  Suppression de l'écriture insérée
     */
    @Test
    public void addReferenceTest_NoReturn_checkAdd() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        Date date = simpleDateFormat.parse("2020/09/18 00:00:00");

        JournalComptable journalComptable = new JournalComptable();

        List<JournalComptable> listJournalComptable = comptabiliteManager.getListJournalComptable();
        for (JournalComptable journal : listJournalComptable) {
            if (StringUtils.equals(journal.getCode(), "AC")) {
                journalComptable = journal;
            }
        }

        CompteComptable compteComptable = comptabiliteManager.getListCompteComptable().get(0);
        LigneEcritureComptable ligneEcritureComptable = new LigneEcritureComptable(compteComptable, "Ligne 1", BigDecimal.valueOf(200), null);
        LigneEcritureComptable ligneEcritureComptable1 = new LigneEcritureComptable(compteComptable, "Ligne 2", null, BigDecimal.valueOf(200));
        List<LigneEcritureComptable> ligneEcritureComptableList = new ArrayList<>();
        ligneEcritureComptableList.add(ligneEcritureComptable);
        ligneEcritureComptableList.add(ligneEcritureComptable1);

        EcritureComptable ecritureComptable = createEcritureComptable(null, journalComptable, null, date, "ecriture insérée", ligneEcritureComptableList);
        comptabiliteManager.addReference(ecritureComptable);

        assertNotEquals("Erreur d'insertion de la référence", null, ecritureComptable.getReference());
        try {
            comptabiliteManager.checkEcritureComptable(ecritureComptable);
        } catch (FunctionalException e) {
            fail("Erreur avec les règles de gestion");
        }
        comptabiliteManager.deleteEcritureComptable(ecritureComptable.getId());
    }

    /**
     * Test de mise à jour d'une écriture comptable.
     * Insertion d'une écriture, puis modification.
     * Vérification de la modification dans la bdd
     * suppression de l'écriture insérée
     */
    @Test
    public void updateEcritureComptableTest_NoReturn_checkEcritureComptable() throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        Date date = simpleDateFormat.parse("2020/09/18 00:00:00");

        JournalComptable journalComptable = new JournalComptable();
        JournalComptable journalComptable1 = new JournalComptable();

        List<JournalComptable> listJournalComptable = comptabiliteManager.getListJournalComptable();
        for (JournalComptable journal : listJournalComptable) {
            if (StringUtils.equals(journal.getCode(), "AC")) {
                journalComptable = journal;
            }
            if (StringUtils.equals(journal.getCode(), "BQ")) {
                journalComptable1 = journal;
            }
        }

        CompteComptable compteComptable = comptabiliteManager.getListCompteComptable().get(0);
        LigneEcritureComptable ligneEcritureComptable = new LigneEcritureComptable(compteComptable, "Ligne 1", BigDecimal.valueOf(200), null);
        LigneEcritureComptable ligneEcritureComptable1 = new LigneEcritureComptable(compteComptable, "Ligne 2", null, BigDecimal.valueOf(200));
        List<LigneEcritureComptable> listLigneEcriture = new ArrayList<>();
        listLigneEcriture.add(ligneEcritureComptable);
        listLigneEcriture.add(ligneEcritureComptable1);

        EcritureComptable ecritureComptable = createEcritureComptable(null, journalComptable, "AC-2020/09999", date, "ecriture insérée", listLigneEcriture);

        comptabiliteManager.insertEcritureComptable(ecritureComptable);

        Date date1 = simpleDateFormat.parse("2020/09/18 00:00:00");
        JournalComptable journalComptable2 = comptabiliteManager.getListJournalComptable().get(1);

        LigneEcritureComptable ligneEcritureComptable2 = new LigneEcritureComptable(compteComptable, "Ligne 3", BigDecimal.valueOf(500), null);
        LigneEcritureComptable ligneEcritureComptable3 = new LigneEcritureComptable(compteComptable, "Ligne 4", null, BigDecimal.valueOf(500));
        List<LigneEcritureComptable> listLigneEcriture2 = new ArrayList<>();
        listLigneEcriture2.add(ligneEcritureComptable2);
        listLigneEcriture2.add(ligneEcritureComptable3);

        EcritureComptable ecritureComptableModifiee = createEcritureComptable(ecritureComptable.getId(), journalComptable1, "BQ-2020/10000", date1, "ecriture modifié", listLigneEcriture2);
        comptabiliteManager.updateEcritureComptable(ecritureComptableModifiee);

        List<EcritureComptable> listEcritureComptable = comptabiliteManager.getListEcritureComptable();
        Iterator iterator = listEcritureComptable.listIterator();
        boolean testCheck = false;

        while (iterator.hasNext()) {
            EcritureComptable ecriture = (EcritureComptable) iterator.next();
            if (ecriture.getId().equals(ecritureComptableModifiee.getId()) &&
            ecriture.getDate().compareTo(ecritureComptableModifiee.getDate()) == 0 &&
            StringUtils.equals(ecriture.getJournal().getCode(), ecritureComptableModifiee.getJournal().getCode()) &&
            StringUtils.equals(ecriture.getLibelle(), ecritureComptableModifiee.getLibelle()) &&
            StringUtils.equals(ecriture.getReference(), ecritureComptableModifiee.getReference()) &&
            ecriture.getListLigneEcriture().size() == ecritureComptableModifiee.getListLigneEcriture().size() &&
            StringUtils.equals(ecriture.getListLigneEcriture().get(0).getLibelle(), ecritureComptableModifiee.getListLigneEcriture().get(0).getLibelle()) &&
            StringUtils.equals(ecriture.getListLigneEcriture().get(1).getLibelle(), ecritureComptableModifiee.getListLigneEcriture().get(1).getLibelle())) {
                testCheck = true;
            }
        }
        assertTrue("Erreur dans la mise à jour de l'écriture comptable", testCheck);
        comptabiliteManager.deleteEcritureComptable(ecritureComptable.getId());
    }

    /**
     * Test de modification d'une écriture comptable.
     * @throws Exception
     */
    @Test
    public void updateEcritureComptableTest_NoReturn_incorrectList() throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        Date date = simpleDateFormat.parse("2018/12/19 00:00:00");

        JournalComptable journalComptable = new JournalComptable();

        List<JournalComptable> listJournalComptable = comptabiliteManager.getListJournalComptable();
        for (JournalComptable journal : listJournalComptable) {
            if (StringUtils.equals(journal.getCode(), "AC")) {
                journalComptable = journal;
            }
        }

        CompteComptable compteComptable = comptabiliteManager.getListCompteComptable().get(0);
        LigneEcritureComptable ligneEcritureComptable = new LigneEcritureComptable(compteComptable, "Ligne1", BigDecimal.valueOf(1500), null);
        LigneEcritureComptable ligneEcritureComptable1 = new LigneEcritureComptable(compteComptable, "Ligne2", null, BigDecimal.valueOf(1500));
        List<LigneEcritureComptable> ligneEcritureComptableList = new ArrayList<>();
        ligneEcritureComptableList.add(ligneEcritureComptable);
        ligneEcritureComptableList.add(ligneEcritureComptable1);

        EcritureComptable ecritureComptable = createEcritureComptable(null, journalComptable, "AC-2018/09999", date, "ecriture insérée test update incorrect liste", ligneEcritureComptableList);

        comptabiliteManager.insertEcritureComptable(ecritureComptable);
        List<LigneEcritureComptable> ligneEcritureComptableList1 = new ArrayList<>();
        EcritureComptable ecritureComptableInvalide = createEcritureComptable(ecritureComptable.getId(), journalComptable, "AC-2018/09999", date, "ecriture modifié test update incorrect liste", ligneEcritureComptableList1);

        try {
            comptabiliteManager.updateEcritureComptable(ecritureComptableInvalide);
            comptabiliteManager.deleteEcritureComptable(ecritureComptable.getId());
            fail("Erreur dans la mise à jour de l'écriture comptable");
        } catch (FunctionalException e) {
            comptabiliteManager.deleteEcritureComptable(ecritureComptable.getId());
        }
    }

    /**
     * Test de modification d'une écriture comptable.
     * @throws Exception
     */
    @Test
    public void updateEcritureComptableTest_NoReturn_IncorrectRef() throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        Date date = simpleDateFormat.parse("2020/09/18 00:00:00");

        JournalComptable journalComptable = new JournalComptable();

        List<JournalComptable> listJournalComptable = comptabiliteManager.getListJournalComptable();
        for (JournalComptable journal : listJournalComptable) {
            if (StringUtils.equals(journal.getCode(), "AC")) {
                journalComptable = journal;
            }
        }

        CompteComptable compteComptable = comptabiliteManager.getListCompteComptable().get(0);
        LigneEcritureComptable ligneEcritureComptable = new LigneEcritureComptable(compteComptable, "Ligne 1", BigDecimal.valueOf(500), null);
        LigneEcritureComptable ligneEcritureComptable1 = new LigneEcritureComptable(compteComptable, "Ligne 2", null, BigDecimal.valueOf(500));
        List<LigneEcritureComptable> ligneEcritureComptableList = new ArrayList<>();
        ligneEcritureComptableList.add(ligneEcritureComptable);
        ligneEcritureComptableList.add(ligneEcritureComptable1);

        EcritureComptable ecritureComptable = createEcritureComptable(null, journalComptable, "AC-2020/09999", date, "ecriture insérée", ligneEcritureComptableList);

        comptabiliteManager.insertEcritureComptable(ecritureComptable);
        EcritureComptable ecritureComptableInvalide = createEcritureComptable(ecritureComptable.getId(), journalComptable, "", date, "ecriture insérée", ligneEcritureComptableList);

        try {
            comptabiliteManager.updateEcritureComptable(ecritureComptableInvalide);
            comptabiliteManager.deleteEcritureComptable(ecritureComptable.getId());
            fail("Erreur dans la mise à jour de l'écriture comptable");
        } catch (FunctionalException e) {
            comptabiliteManager.deleteEcritureComptable(ecritureComptable.getId());
        }
    }

    /**
     * Test de suppression d'une écriture comptable.
     * Création d'une écriture comptable, Puis suppression.
     * Vérification de la suppression.
     */
    @Test
    public void deleteEcritureComptableTest_NoReturn_checkEcritureComtpable() throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        Date date = simpleDateFormat.parse("2018/12/19 00:00:00");

        JournalComptable journalComptable = new JournalComptable();

        List<JournalComptable> listJournalComptable = comptabiliteManager.getListJournalComptable();
        for (JournalComptable journal : listJournalComptable) {
            if (StringUtils.equals(journal.getCode(), "AC")) {
                journalComptable = journal;
            }
        }

        CompteComptable compteComptable = comptabiliteManager.getListCompteComptable().get(0);
        LigneEcritureComptable ligneEcritureComptable = new LigneEcritureComptable(compteComptable, "Ligne1", BigDecimal.valueOf(1500), null);
        LigneEcritureComptable ligneEcritureComptable1 = new LigneEcritureComptable(compteComptable, "Ligne2", null, BigDecimal.valueOf(1500));
        List<LigneEcritureComptable> ligneEcritureComptableList = new ArrayList<>();
        ligneEcritureComptableList.add(ligneEcritureComptable);
        ligneEcritureComptableList.add(ligneEcritureComptable1);

        EcritureComptable ecritureComptable = createEcritureComptable(null, journalComptable, "AC-2018/09999", date, "ecriture insérée", ligneEcritureComptableList);

        comptabiliteManager.insertEcritureComptable(ecritureComptable);

        comptabiliteManager.deleteEcritureComptable(ecritureComptable.getId());

        List<EcritureComptable> listEcritureComptable = comptabiliteManager.getListEcritureComptable();
        for (EcritureComptable ecriture : listEcritureComptable) {
            if (ecriture.getId().equals(ecritureComptable.getId())) {
                fail("Erreur de suppression de l'écriture comptable");
            }
        }
    }

    /**
     * Test d'insertion de la séquence d'écriture comptable.
     */
    @Test
    public void insertSequenceEcritureComptableTest_NoReturn_checkSequenceEcritureComtpable() {
        SequenceEcritureComptable sequenceEcritureComptable = new SequenceEcritureComptable("OD", 2020, 1);
        comptabiliteManager.insertSequenceEcritureComptable(sequenceEcritureComptable);

        try {
            SequenceEcritureComptable sequenceEcritureComptable1 = comptabiliteDao.getSequenceEcritureComptable(sequenceEcritureComptable.getJournalCode(), sequenceEcritureComptable.getAnnee());
        } catch (NotFoundException e) {
            fail("Erreur d'insertion de la séquence d'écriture comptable" + e.getMessage());
        }
        comptabiliteDao.deleteSequenceEcritureComptable(sequenceEcritureComptable.getJournalCode(), sequenceEcritureComptable.getAnnee());
    }

    /**
     * Test de modification de la séquence d'écriture comptable.
     */
    @Test
    public void updateSequenceEcritureComptableTest_NoReturn_checkSequenceEcritureComptable() {
        SequenceEcritureComptable sequenceEcritureComptable = new SequenceEcritureComptable("OD", 2020, 1);
        comptabiliteManager.insertSequenceEcritureComptable(sequenceEcritureComptable);
        SequenceEcritureComptable sequenceEcritureComptableModifiee = new SequenceEcritureComptable("OD", 2020, 50);
        comptabiliteManager.updateSequenceEcritureComptable(sequenceEcritureComptableModifiee);

        try {
            SequenceEcritureComptable sequenceEcritureComptable1 = comptabiliteDao.getSequenceEcritureComptable(sequenceEcritureComptableModifiee.getJournalCode(), sequenceEcritureComptableModifiee.getAnnee());
            if(sequenceEcritureComptable1.getDerniereValeur() != 50){
                fail("Modification partielle de la séquence d'écriture comptable : " + sequenceEcritureComptable1.getDerniereValeur());
            }
        } catch (NotFoundException e) {
            fail("Erreur de modification de la séquence d'écriture comptable : " + e.getMessage());
        }
        comptabiliteDao.deleteSequenceEcritureComptable(sequenceEcritureComptableModifiee.getJournalCode(), sequenceEcritureComptableModifiee.getAnnee());
    }

    /**
     * Test de suppression d'une séquence d'écriture comptable.
     * Insertion d'une séquence, Puis suppression et on la recherche en BDD.
     * @throws Exception
     */
    @Test (expected = NotFoundException.class)
    public void deleteSequenceEcritureComptableTest_NoReturn_checkSequenceEcritureComptable() throws Exception {
        SequenceEcritureComptable sequenceEcritureComptable = new SequenceEcritureComptable("OD", 2020, 1);
        comptabiliteManager.insertSequenceEcritureComptable(sequenceEcritureComptable);
        comptabiliteManager.deleteSequenceEcritureComptable(sequenceEcritureComptable);
        comptabiliteDao.getSequenceEcritureComptable(sequenceEcritureComptable.getJournalCode(), sequenceEcritureComptable.getAnnee());
    }
}
