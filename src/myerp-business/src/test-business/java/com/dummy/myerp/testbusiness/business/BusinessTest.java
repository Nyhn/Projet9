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

    /**
     * Fonction de création d'un écriture comptable
     * @param id id de l'écriture comptable
     * @param journalComptable journalComptable de l'écriture comptable
     * @param reference référence de l'écriture comptable
     * @param date date de l'écriture comptable
     * @param libelle libellé de l'écriture comptable
     * @param listLigneEcriture la liste de ligne d'écriture de l'écriture comptable
     * @return l'écriture comptable
     */
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
     * Test de récupération de liste de compte comptable
     * Puis vérification du nombre de compte comptable obtenu
     */
    @Test
    public void getListCompteComptableTest_checkNbOfCompteComptable() {
        List<CompteComptable> listCompteComptable = comptabiliteManager.getListCompteComptable();
        assertEquals("Le nombre de compte attendu n'est pas égale à 7",7, listCompteComptable.size());
    }

    /**
     * Test de récupération de liste de journal comptable
     * Puis vérification du nombre de journal comptable obtenu
     */
    @Test
    public void getListJournalComptableTest_checkNbOfJournalComptable() {
        List<JournalComptable> listJournalComptable = comptabiliteManager.getListJournalComptable();
        assertEquals("Le nombre de journal comptable attendu n'est pas égales à 4", 4, listJournalComptable.size());
    }

    /**
     * Test de récupération de liste d'écriture comptable
     * Puis vérification du nombre d'écriture comptable obtenu
     */
    @Test
    public void getListEcritureComptableTest_checkNbOfEcritureComptable() {
        List<EcritureComptable> listEcritureComptable = comptabiliteManager.getListEcritureComptable();
        assertEquals("Le nombre d'écritures comptables attendu n'est pas égales à 5",5, listEcritureComptable.size());
    }

    /**
     * Test d'insertion d'une écriture comptable dans la BDD
     * puis vérification de l'insertion
     * L'écriture insérée est ensuite supprimée
     */
    @Test
    public void insertEcritureComptableTest_checkInsertion() throws ParseException {
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
        LigneEcritureComptable ligneEcriture = new LigneEcritureComptable(compteComptable, "Ligne1", BigDecimal.valueOf(1500), null);
        LigneEcritureComptable ligneEcriture2 = new LigneEcritureComptable(compteComptable, "Ligne2", null, BigDecimal.valueOf(1500));
        List<LigneEcritureComptable> ligneEcritureComptables = new ArrayList<>();
        ligneEcritureComptables.add(ligneEcriture);
        ligneEcritureComptables.add(ligneEcriture2);

        EcritureComptable ecritureComptable = createEcritureComptable(null, journalComptable, "AC-2018/09999", date, "ecriture insérée", ligneEcritureComptables);

        try {
            comptabiliteManager.insertEcritureComptable(ecritureComptable);
        } catch (FunctionalException e) {
            fail("L'insertion de l'écriture n'a pu se faire : " + e.getMessage());
        }

        List<EcritureComptable> listEcritureComptable = comptabiliteManager.getListEcritureComptable();
        Iterator iterator = listEcritureComptable.listIterator();
        boolean testOK = false;
        while (iterator.hasNext()) {
            EcritureComptable ecritureInseree = (EcritureComptable) iterator.next();
            if(ecritureInseree.getId() != null &&
                StringUtils.equals(ecritureInseree.getJournal().getCode(), ecritureComptable.getJournal().getCode()) &&
                StringUtils.equals(ecritureInseree.getReference(), ecritureComptable.getReference()) &&
                StringUtils.equals(ecritureInseree.getLibelle(), ecritureComptable.getLibelle()) &&
                ecritureInseree.getDate().compareTo(ecritureComptable.getDate()) == 0 &&
                ecritureInseree.getListLigneEcriture().size() == ecritureComptable.getListLigneEcriture().size() &&
                ecritureInseree.getListLigneEcriture().get(1).getCompteComptable().getNumero().equals(ecritureComptable.getListLigneEcriture().get(1).getCompteComptable().getNumero()) &&
                ecritureInseree.getListLigneEcriture().get(1).getCredit().compareTo(ecritureComptable.getListLigneEcriture().get(1).getCredit()) == 0)
            {
                testOK = true;
            }
        }
       assertTrue("Les informations d'insertion ne correspondent pas à l'écriture récupéré", testOK);
        comptabiliteManager.deleteEcritureComptable(ecritureComptable.getId());
    }

    /**
     * Test d'insertion d'une écriture comptable dans la BDD
     * l'écriture ne respecte pas les règles de gestion
     * Doit renvoyer une Fonctional Exception
     *
     * @throws FunctionalException
     */
    @Test (expected = FunctionalException.class)
    public void insertEcritureComptableIncorrectTest_throwFunctionalException() throws FunctionalException {
        EcritureComptable ecritureComptable = new EcritureComptable();
        comptabiliteManager.insertEcritureComptable(ecritureComptable);
    }

    /**
     *  Test d'ajout d'une référence d'écriture comptable
     *  puis vérification que la référence a bien été ajouté à l'écriture
     *  L'écriture insérée est ensuite supprimée
     */
    @Test
    public void addReferenceTest_checkReference() throws ParseException {
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
        LigneEcritureComptable ligneEcriture = new LigneEcritureComptable(compteComptable, "Ligne 1", BigDecimal.valueOf(200), null);
        LigneEcritureComptable ligneEcriture2 = new LigneEcritureComptable(compteComptable, "Ligne 2", null, BigDecimal.valueOf(200));
        List<LigneEcritureComptable> ligneEcritureComptables = new ArrayList<>();
        ligneEcritureComptables.add(ligneEcriture);
        ligneEcritureComptables.add(ligneEcriture2);

        EcritureComptable ecritureComptable = createEcritureComptable(null, journalComptable, null, date, "insertion d'écriture", ligneEcritureComptables);
        comptabiliteManager.addReference(ecritureComptable);

        assertNotEquals("Erreur d'insertion de la référence", null, ecritureComptable.getReference());
        try {
            comptabiliteManager.checkEcritureComptable(ecritureComptable);
        } catch (FunctionalException e) {
            fail("Les règles de gestion ne sont pas respecté");
        }
        comptabiliteManager.deleteEcritureComptable(ecritureComptable.getId());
    }

    /**
     * Test de mise à jour d'une écriture comptable
     * création d'une écriture, puis modification de cette écriture.
     * Ensuite vérification que la liste des écritures, une écriture comptable existe bien avec les modifications réalisés
     * L'écriture insérée est ensuite supprimée
     */
    @Test
    public void updateEcritureComptableTest_checkModification() throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        Date date = simpleDateFormat.parse("2020/09/18 00:00:00");

        JournalComptable journalComptable1 = new JournalComptable();
        JournalComptable journalComptable2 = new JournalComptable();

        List<JournalComptable> listJournalComptable = comptabiliteManager.getListJournalComptable();
        for (JournalComptable journal : listJournalComptable) {
            if (StringUtils.equals(journal.getCode(), "AC")) {
                journalComptable1 = journal;
            }
            if (StringUtils.equals(journal.getCode(), "BQ")) {
                journalComptable2 = journal;
            }
        }

        CompteComptable compteComptable = comptabiliteManager.getListCompteComptable().get(0);
        LigneEcritureComptable ligneEcriture = new LigneEcritureComptable(compteComptable, "Ligne 1", BigDecimal.valueOf(200), null);
        LigneEcritureComptable ligneEcriture2 = new LigneEcritureComptable(compteComptable, "Ligne 2", null, BigDecimal.valueOf(200));
        List<LigneEcritureComptable> ligneEcritureComptables = new ArrayList<>();
        ligneEcritureComptables.add(ligneEcriture);
        ligneEcritureComptables.add(ligneEcriture2);

        EcritureComptable ecritureComptable = createEcritureComptable(null, journalComptable1, "AC-2020/09999", date, "insertion d'écriture", ligneEcritureComptables);

        comptabiliteManager.insertEcritureComptable(ecritureComptable);

        Date date2 = simpleDateFormat.parse("2020/09/18 00:00:00");
        JournalComptable journal2 = comptabiliteManager.getListJournalComptable().get(1);

        LigneEcritureComptable ligneEcriture3 = new LigneEcritureComptable(compteComptable, "Ligne 3", BigDecimal.valueOf(500), null);
        LigneEcritureComptable ligneEcriture4 = new LigneEcritureComptable(compteComptable, "Lign e4", null, BigDecimal.valueOf(500));
        List<LigneEcritureComptable> ligneEcritureComptables1 = new ArrayList<>();
        ligneEcritureComptables1.add(ligneEcriture3);
        ligneEcritureComptables1.add(ligneEcriture4);

        EcritureComptable ecritureComptableModifiee = createEcritureComptable(ecritureComptable.getId(), journalComptable2, "BQ-2020/10000", date2, "ecriture modifié", ligneEcritureComptables1);
        comptabiliteManager.updateEcritureComptable(ecritureComptableModifiee);

        List<EcritureComptable> listEcritures = comptabiliteManager.getListEcritureComptable();
        Iterator iterator = listEcritures.listIterator();
        boolean testOk = false;

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
                testOk = true;
            }
        }
        assertTrue("Erreur de la mise à jour de l'écriture comptable", testOk);

        comptabiliteManager.deleteEcritureComptable(ecritureComptable.getId());
    }

    /**
     * Test de modification d'une écriture comptable.
     * Ecriture incorrecte avec une liste de ligne d'écriture vide.
     * Doit renvoyer une FunctionalException.
     *
     */
    @Test
    public void updateEcritureComptableTest_withListLigneEmpty_throwFunctionalException() throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        Date date = simpleDateFormat.parse("2018/12/19 00:00:00");

        JournalComptable journalComptable1 = new JournalComptable();

        List<JournalComptable> listJournalComptable = comptabiliteManager.getListJournalComptable();
        for (JournalComptable journal : listJournalComptable) {
            if (StringUtils.equals(journal.getCode(), "AC")) {
                journalComptable1 = journal;
            }
        }

        CompteComptable compteComptable = comptabiliteManager.getListCompteComptable().get(0);
        LigneEcritureComptable ligneEcriture = new LigneEcritureComptable(compteComptable, "Ligne1", BigDecimal.valueOf(1500), null);
        LigneEcritureComptable ligneEcriture2 = new LigneEcritureComptable(compteComptable, "Ligne2", null, BigDecimal.valueOf(1500));
        List<LigneEcritureComptable> ligneEcritureComptableList = new ArrayList<>();
        ligneEcritureComptableList.add(ligneEcriture);
        ligneEcritureComptableList.add(ligneEcriture2);

        EcritureComptable ecritureComptable = createEcritureComptable(null, journalComptable1, "AC-2018/09999", date, "insertion d'écriture", ligneEcritureComptableList);

        comptabiliteManager.insertEcritureComptable(ecritureComptable);
        List<LigneEcritureComptable> ligneEcritureComptableList1 = new ArrayList<>();
        EcritureComptable ecritureInvalide = createEcritureComptable(ecritureComptable.getId(), journalComptable1, "AC-2018/09999", date, "ecriture modifié", ligneEcritureComptableList1);

        try {
            comptabiliteManager.updateEcritureComptable(ecritureInvalide);
            comptabiliteManager.deleteEcritureComptable(ecritureComptable.getId());
            fail("La vérification de l'écriture avec l'update n'a pas été faite correctement");
        } catch (FunctionalException e) {
            comptabiliteManager.deleteEcritureComptable(ecritureComptable.getId());
        }
    }

    /**
     * Test de modification d'une écriture comptable.
     * Ecriture incorrecte avec une référence vide.
     * Doit renvoyer une FunctionalException.
     *
     */
    @Test
    public void updateEcritureComptableTest_withReferenceEmpty_throwFucntionalException() throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        Date date = simpleDateFormat.parse("2020/09/18 00:00:00");

        JournalComptable journalComptable1 = new JournalComptable();

        List<JournalComptable> listJournalComptable = comptabiliteManager.getListJournalComptable();
        for (JournalComptable journal : listJournalComptable) {
            if (StringUtils.equals(journal.getCode(), "AC")) {
                journalComptable1 = journal;
            }
        }

        CompteComptable compteComptable = comptabiliteManager.getListCompteComptable().get(0);
        LigneEcritureComptable ligneEcritureComptable = new LigneEcritureComptable(compteComptable, "Ligne 1", BigDecimal.valueOf(500), null);
        LigneEcritureComptable ligneEcritureComptable1 = new LigneEcritureComptable(compteComptable, "Ligne 2", null, BigDecimal.valueOf(500));
        List<LigneEcritureComptable> ligneEcritureComptableList = new ArrayList<>();
        ligneEcritureComptableList.add(ligneEcritureComptable);
        ligneEcritureComptableList.add(ligneEcritureComptable1);

        EcritureComptable ecritureComptable = createEcritureComptable(null, journalComptable1, "AC-2020/09999", date, "insertion d'écriture", ligneEcritureComptableList);

        comptabiliteManager.insertEcritureComptable(ecritureComptable);
        EcritureComptable ecritureInvalide = createEcritureComptable(ecritureComptable.getId(), journalComptable1, "", date, "insertion d'écriture", ligneEcritureComptableList);

        try {
            comptabiliteManager.updateEcritureComptable(ecritureInvalide);
            comptabiliteManager.deleteEcritureComptable(ecritureComptable.getId());
            fail("La vérification de l'écriture avec l'update n'a pas été faite correctement");
        } catch (FunctionalException e) {
            comptabiliteManager.deleteEcritureComptable(ecritureComptable.getId());
        }
    }

    /**
     * Test de suppression d'une écriture comptable.
     * On créé une écriture comptable,puis on la supprime.
     * On cherche si l'écriture existe toujours parmi la liste des écritures existantes.
     */
    @Test
    public void deleteEcritureComptableTest_checkIfEcritureIsNotExist() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        Date date = dateFormat.parse("2020/09/18 00:00:00");

        JournalComptable journalComptable1 = new JournalComptable();

        List<JournalComptable> listJournaux = comptabiliteManager.getListJournalComptable();
        for (JournalComptable journal : listJournaux) {
            if (StringUtils.equals(journal.getCode(), "AC")) {
                journalComptable1 = journal;
            }
        }

        CompteComptable compte = comptabiliteManager.getListCompteComptable().get(0);
        LigneEcritureComptable ligneEcriture = new LigneEcritureComptable(compte, "Ligne1", BigDecimal.valueOf(100), null);
        LigneEcritureComptable ligneEcriture2 = new LigneEcritureComptable(compte, "Ligne2", null, BigDecimal.valueOf(100));
        List<LigneEcritureComptable> listLigneEcriture = new ArrayList<>();
        listLigneEcriture.add(ligneEcriture);
        listLigneEcriture.add(ligneEcriture2);

        EcritureComptable ecritureComptable = createEcritureComptable(null, journalComptable1, "AC-2020/09999", date, "ecriture insérée", listLigneEcriture);

        comptabiliteManager.insertEcritureComptable(ecritureComptable);

        comptabiliteManager.deleteEcritureComptable(ecritureComptable.getId());

        List<EcritureComptable> listEcritures = comptabiliteManager.getListEcritureComptable();
        for (EcritureComptable ecriture : listEcritures) {
            if (ecriture.getId().equals(ecritureComptable.getId())) {
                fail("L'écriture n'a pas été supprimé correctement");
            }
        }
    }

    /**
     * Test d'insertion de la séquence d'écriture comptable
     * puis suppression de la séquence d'écriture comptable
     */
    @Test
    public void insertSequenceEcritureComptableTest_checkIfInsertIsOK() {
        SequenceEcritureComptable sequenceEcritureComptable = new SequenceEcritureComptable("OD", 2020, 1);
        comptabiliteManager.insertSequenceEcritureComptable(sequenceEcritureComptable);

        try {
            SequenceEcritureComptable sequence = comptabiliteDao.getSequenceEcritureComptable(sequenceEcritureComptable.getJournalCode(), sequenceEcritureComptable.getAnnee());
        } catch (NotFoundException e) {
            fail("La séquence de l'écriture comptable n'a pas été insérée" + e.getMessage());
        }

        comptabiliteDao.deleteSequenceEcritureComptable(sequenceEcritureComptable.getJournalCode(), sequenceEcritureComptable.getAnnee());
    }

    /**
     * Test de modification de la séquence d'écriture comptable.
     * puis suppression de la séquence de la BDD
     */
    @Test
    public void updateSequenceEcritureComptableTest() {
        SequenceEcritureComptable sequenceEcritureComptable = new SequenceEcritureComptable("OD", 2020, 1);
        comptabiliteManager.insertSequenceEcritureComptable(sequenceEcritureComptable);
        SequenceEcritureComptable sequenceEcritureComptableModifiee = new SequenceEcritureComptable("OD", 2020, 50);
        comptabiliteManager.updateSequenceEcritureComptable(sequenceEcritureComptableModifiee);

        try {
            SequenceEcritureComptable sequence = comptabiliteDao.getSequenceEcritureComptable(sequenceEcritureComptableModifiee.getJournalCode(), sequenceEcritureComptableModifiee.getAnnee());
            if(sequence.getDerniereValeur() != 50){
                fail("La séquence n'a été que partiellement modifiée : " + sequence.getDerniereValeur());
            }
        } catch (NotFoundException e) {
            fail("La séquence de l'écriture comptable n'a pas été modifiée correctement" + e.getMessage());
        }

        comptabiliteDao.deleteSequenceEcritureComptable(sequenceEcritureComptableModifiee.getJournalCode(), sequenceEcritureComptableModifiee.getAnnee());
    }

    /**
     * Test de suppression d'une séquence d'écriture comptable.
     * insertion une séquence, puis suppression et recherche en BDD.
     * Doit renvoyer une NotFoundException
     */
    @Test (expected = NotFoundException.class)
    public void deleteSequenceEcritureComptableTest() throws Exception {
        SequenceEcritureComptable sequenceEcritureComptable = new SequenceEcritureComptable("OD", 2020, 1);
        comptabiliteManager.insertSequenceEcritureComptable(sequenceEcritureComptable);

        comptabiliteManager.deleteSequenceEcritureComptable(sequenceEcritureComptable);

        comptabiliteDao.getSequenceEcritureComptable(sequenceEcritureComptable.getJournalCode(), sequenceEcritureComptable.getAnnee());
    }
}
