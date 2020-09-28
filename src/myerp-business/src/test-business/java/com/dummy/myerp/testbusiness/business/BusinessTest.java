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
     * Vérification de la récupération de la liste des comptes comptables.
     * Test passant.
     * - 7 Comptes attendus
     */
    @Test
    public void getListCompteComptableTest() {
        List<CompteComptable> listComptes = comptabiliteManager.getListCompteComptable();
        assertEquals("Le nombre de compte attendu n'est pas correct",7, listComptes.size());
    }

    /**
     * Vérification de la récupération de la liste des journaux comptables.
     * Test passant.
     * - 4 journaux attendus
     */
    @Test
    public void getListJournalComptableTest() {
        List<JournalComptable> listJournaux = comptabiliteManager.getListJournalComptable();
        assertEquals("Le nombre de journaux comptables attendu n'est pas correct", 4, listJournaux.size());
    }

    /**
     * Vérification de la récupération de la liste des écritures comptables.
     * Test passant.
     * - 5 écritures attendues
     */
    @Test
    public void getListEcritureComptableTest() {
        List<EcritureComptable> listEcritures = comptabiliteManager.getListEcritureComptable();
        assertEquals("Le nombre d'écritures comptables n'est pas correct", 5, listEcritures.size());
    }

    /**
     * Vérification de l'insertion d'une écriture comptable
     * Test passant.
     * On créé une écriture, on l'insert dans la BDD.
     * On récupère l'écriture insérée et on vérifie qu'elle correspond à l'écriture d'origine.
     * L'écriture insérée est ensuite supprimée pour ne pas interferré avec d'autres tests.
     */
    @Test
    public void insertEcritureComptableTest() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        Date date = dateFormat.parse("2018/12/19 00:00:00");

        JournalComptable journalComptable1 = new JournalComptable();

        List<JournalComptable> listJournaux = comptabiliteManager.getListJournalComptable();
        for (JournalComptable journal : listJournaux) {
            if (StringUtils.equals(journal.getCode(), "AC")) {
                journalComptable1 = journal;
            }
        }

        CompteComptable compte = comptabiliteManager.getListCompteComptable().get(0);
        LigneEcritureComptable ligneEcriture = new LigneEcritureComptable(compte, "Ligne1", BigDecimal.valueOf(1500), null);
        LigneEcritureComptable ligneEcriture2 = new LigneEcritureComptable(compte, "Ligne2", null, BigDecimal.valueOf(1500));
        List<LigneEcritureComptable> listLigneEcriture = new ArrayList<>();
        listLigneEcriture.add(ligneEcriture);
        listLigneEcriture.add(ligneEcriture2);

        EcritureComptable ecritureComptable = createEcritureComptable(null, journalComptable1, "AC-2018/09999", date, "ecriture insérée", listLigneEcriture);

        try {
            comptabiliteManager.insertEcritureComptable(ecritureComptable);
        } catch (FunctionalException e) {
            fail("L'insertion de l'écriture n'a pu se faire : " + e.getMessage());
        }

        List<EcritureComptable> listEcritures = comptabiliteManager.getListEcritureComptable();
        Iterator it = listEcritures.listIterator();
        boolean testOK = false;
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
                testOK = true;
            }
        }
       assertTrue("Les informations de l'écriture insérée ne correspondent pas à l'écriture d'origine", testOK);
        comptabiliteManager.deleteEcritureComptable(ecritureComptable.getId());
    }

    /**
     * Vérification de l'insertion d'une écriture ne respectant pas les règles de gestion.
     * L'écriture doit être vérifiée avec insertion
     * Doit renvoyer une Fonctional Exception
     *
     * @throws FunctionalException
     */
    @Test (expected = FunctionalException.class)
    public void insertEcritureComptableIncorrectTest() throws FunctionalException {
        EcritureComptable ecritureComptable = new EcritureComptable();

        comptabiliteManager.insertEcritureComptable(ecritureComptable);
    }

//    /**
//     *  Vérification de l'ajout d'une référence à une écriture comptable.
//     *  Test passant.
//     *  On vérifie qu'une référence a bien été ajouté à l'écriture
//     *  On vérifie ensuite que l'écriture respecte les règles de gestion.
//     *  L'écriture insérée est ensuite supprimée pour ne pas interferré avec d'autres tests.
//     */
//    @Test
//    public void addReferenceTest() throws ParseException {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
//        Date date = dateFormat.parse("2018/12/19 00:00:00");
//
//        JournalComptable journalComptable1 = new JournalComptable();
//
//        List<JournalComptable> listJournaux = comptabiliteManager.getListJournalComptable();
//        for (JournalComptable journal : listJournaux) {
//            if (StringUtils.equals(journal.getCode(), "AC")) {
//                journalComptable1 = journal;
//            }
//        }
//
//        CompteComptable compte = comptabiliteManager.getListCompteComptable().get(0);
//        LigneEcritureComptable ligneEcriture = new LigneEcritureComptable(compte, "Ligne1", BigDecimal.valueOf(1500), null);
//        LigneEcritureComptable ligneEcriture2 = new LigneEcritureComptable(compte, "Ligne2", null, BigDecimal.valueOf(1500));
//        List<LigneEcritureComptable> listLigneEcriture = new ArrayList<>();
//        listLigneEcriture.add(ligneEcriture);
//        listLigneEcriture.add(ligneEcriture2);
//
//        EcritureComptable ecritureComptable = createEcritureComptable(null, journalComptable1, null, date, "ecriture insérée", listLigneEcriture);
//
//        comptabiliteManager.addReference(ecritureComptable);
//
//        assertNotEquals("La référence n'a pas été insérée", null, ecritureComptable.getReference());
//        try {
//            comptabiliteManager.checkEcritureComptable(ecritureComptable);
//        } catch (FunctionalException e) {
//            fail("L'écriture ne respecte pas les règles de gestion");
//        }
//
//        comptabiliteManager.deleteEcritureComptable(ecritureComptable.getId());
//    }

//    /**
//     * Vérification de la mise à jour d'une écriture comptable.
//     * Test passant.
//     * On insert une écriture, on demande sa modification.
//     * On vérifie ensuite si parmi la liste des écritures, une écriture contient bien les modifications de l'écriture
//     * L'écriture insérée est ensuite supprimée pour ne pas interferré avec d'autres tests.
//     */
//    @Test
//    public void updateEcritureComptableTest() throws Exception {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
//        Date date = dateFormat.parse("2018/12/19 00:00:00");
//
//        JournalComptable journalComptable1 = new JournalComptable();
//        JournalComptable journalComptable2 = new JournalComptable();
//
//        List<JournalComptable> listJournaux = comptabiliteManager.getListJournalComptable();
//        for (JournalComptable journal : listJournaux) {
//            if (StringUtils.equals(journal.getCode(), "AC")) {
//                journalComptable1 = journal;
//            }
//            if (StringUtils.equals(journal.getCode(), "BQ")) {
//                journalComptable2 = journal;
//            }
//        }
//
//        CompteComptable compte = comptabiliteManager.getListCompteComptable().get(0);
//        LigneEcritureComptable ligneEcriture = new LigneEcritureComptable(compte, "Ligne1", BigDecimal.valueOf(1500), null);
//        LigneEcritureComptable ligneEcriture2 = new LigneEcritureComptable(compte, "Ligne2", null, BigDecimal.valueOf(1500));
//        List<LigneEcritureComptable> listLigneEcriture = new ArrayList<>();
//        listLigneEcriture.add(ligneEcriture);
//        listLigneEcriture.add(ligneEcriture2);
//
//        EcritureComptable ecritureComptable = createEcritureComptable(null, journalComptable1, "AC-2018/09999", date, "ecriture insérée", listLigneEcriture);
//
//        comptabiliteManager.insertEcritureComptable(ecritureComptable);
//
//        Date date2 = dateFormat.parse("2018/12/19 00:00:00");
//        JournalComptable journal2 = comptabiliteManager.getListJournalComptable().get(1);
//
//        LigneEcritureComptable ligneEcriture3 = new LigneEcritureComptable(compte, "Ligne3", BigDecimal.valueOf(3500), null);
//        LigneEcritureComptable ligneEcriture4 = new LigneEcritureComptable(compte, "Ligne4", null, BigDecimal.valueOf(3500));
//        List<LigneEcritureComptable> listLigneEcriture2 = new ArrayList<>();
//        listLigneEcriture2.add(ligneEcriture3);
//        listLigneEcriture2.add(ligneEcriture4);
//
//        EcritureComptable ecritureComptableModifiee = createEcritureComptable(ecritureComptable.getId(), journalComptable2, "BQ-2018/00001", date2, "ecriture modifié", listLigneEcriture2);
//        comptabiliteManager.updateEcritureComptable(ecritureComptableModifiee);
//
//        List<EcritureComptable> listEcritures = comptabiliteManager.getListEcritureComptable();
//        Iterator it = listEcritures.listIterator();
//        boolean testOk = false;
//
//        while (it.hasNext()) {
//            EcritureComptable ecriture = (EcritureComptable) it.next();
//            if (ecriture.getId().equals(ecritureComptableModifiee.getId()) &&
//            ecriture.getDate().compareTo(ecritureComptableModifiee.getDate()) == 0 &&
//            StringUtils.equals(ecriture.getJournal().getCode(), ecritureComptableModifiee.getJournal().getCode()) &&
//            StringUtils.equals(ecriture.getLibelle(), ecritureComptableModifiee.getLibelle()) &&
//            StringUtils.equals(ecriture.getReference(), ecritureComptableModifiee.getReference()) &&
//            ecriture.getListLigneEcriture().size() == ecritureComptableModifiee.getListLigneEcriture().size() &&
//            StringUtils.equals(ecriture.getListLigneEcriture().get(0).getLibelle(), ecritureComptableModifiee.getListLigneEcriture().get(0).getLibelle()) &&
//            StringUtils.equals(ecriture.getListLigneEcriture().get(1).getLibelle(), ecritureComptableModifiee.getListLigneEcriture().get(1).getLibelle())) {
//                testOk = true;
//            }
//        }
//        assertTrue("La mise à jour de l'écriture comptable ne s'est pas faite correctement", testOk);
//
//        comptabiliteManager.deleteEcritureComptable(ecritureComptable.getId());
//    }

//    /**
//     * Vérification de la modification d'une écriture comptable.
//     * Ecriture incorrecte avec une liste de ligne d'écriture vide.
//     * Doit renvoyer une FunctionalException.
//     *
//     */
//    @Test
//    public void updateEcritureComptableIncorrecteListTest() throws Exception {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
//        Date date = dateFormat.parse("2018/12/19 00:00:00");
//
//        JournalComptable journalComptable1 = new JournalComptable();
//
//        List<JournalComptable> listJournaux = comptabiliteManager.getListJournalComptable();
//        for (JournalComptable journal : listJournaux) {
//            if (StringUtils.equals(journal.getCode(), "AC")) {
//                journalComptable1 = journal;
//            }
//        }
//
//        CompteComptable compte = comptabiliteManager.getListCompteComptable().get(0);
//        LigneEcritureComptable ligneEcriture = new LigneEcritureComptable(compte, "Ligne1", BigDecimal.valueOf(1500), null);
//        LigneEcritureComptable ligneEcriture2 = new LigneEcritureComptable(compte, "Ligne2", null, BigDecimal.valueOf(1500));
//        List<LigneEcritureComptable> listLigneEcriture = new ArrayList<>();
//        listLigneEcriture.add(ligneEcriture);
//        listLigneEcriture.add(ligneEcriture2);
//
//        EcritureComptable ecritureComptable = createEcritureComptable(null, journalComptable1, "AC-2018/09999", date, "ecriture insérée test update incorrect liste", listLigneEcriture);
//
//        comptabiliteManager.insertEcritureComptable(ecritureComptable);
//        List<LigneEcritureComptable> listLigneEcriture2 = new ArrayList<>();
//        EcritureComptable ecritureInvalide = createEcritureComptable(ecritureComptable.getId(), journalComptable1, "AC-2018/09999", date, "ecriture modifié test update incorrect liste", listLigneEcriture2);
//
//        try {
//            comptabiliteManager.updateEcritureComptable(ecritureInvalide);
//            comptabiliteManager.deleteEcritureComptable(ecritureComptable.getId());
//            fail("La vérification de l'écriture avec l'update n'a pas été faite correctement");
//        } catch (FunctionalException e) {
//            comptabiliteManager.deleteEcritureComptable(ecritureComptable.getId());
//        }
//    }
//
//    /**
//     * Vérification de la modification d'une écriture comptable.
//     * Ecriture incorrecte avec une référence vide.
//     * Doit renvoyer une FunctionalException.
//     *
//     */
//    @Test
//    public void updateEcritureComptableIncorrecteRefTest() throws Exception {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
//        Date date = dateFormat.parse("2018/12/19 00:00:00");
//
//        JournalComptable journalComptable1 = new JournalComptable();
//
//        List<JournalComptable> listJournaux = comptabiliteManager.getListJournalComptable();
//        for (JournalComptable journal : listJournaux) {
//            if (StringUtils.equals(journal.getCode(), "AC")) {
//                journalComptable1 = journal;
//            }
//        }
//
//        CompteComptable compte = comptabiliteManager.getListCompteComptable().get(0);
//        LigneEcritureComptable ligneEcriture = new LigneEcritureComptable(compte, "Ligne1", BigDecimal.valueOf(1500), null);
//        LigneEcritureComptable ligneEcriture2 = new LigneEcritureComptable(compte, "Ligne2", null, BigDecimal.valueOf(1500));
//        List<LigneEcritureComptable> listLigneEcriture = new ArrayList<>();
//        listLigneEcriture.add(ligneEcriture);
//        listLigneEcriture.add(ligneEcriture2);
//
//        EcritureComptable ecritureComptable = createEcritureComptable(null, journalComptable1, "AC-2018/09999", date, "ecriture insérée", listLigneEcriture);
//
//        comptabiliteManager.insertEcritureComptable(ecritureComptable);
//        EcritureComptable ecritureInvalide = createEcritureComptable(ecritureComptable.getId(), journalComptable1, "", date, "ecriture insérée", listLigneEcriture);
//
//        try {
//            comptabiliteManager.updateEcritureComptable(ecritureInvalide);
//            comptabiliteManager.deleteEcritureComptable(ecritureComptable.getId());
//            fail("La vérification de l'écriture avec l'update n'a pas été faite correctement");
//        } catch (FunctionalException e) {
//            comptabiliteManager.deleteEcritureComptable(ecritureComptable.getId());
//        }
//    }
//
//    /**
//     * Vérification de la suppression d'une écriture comptable.
//     * Test passant.
//     * On créé une écriture comptable, on la supprime.
//     * On cherche si l'écriture existe toujours parmi la liste des écritures existantes.
//     *
//     */
//    @Test
//    public void deleteEcritureComptableTest() throws Exception {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
//        Date date = dateFormat.parse("2018/12/19 00:00:00");
//
//        JournalComptable journalComptable1 = new JournalComptable();
//
//        List<JournalComptable> listJournaux = comptabiliteManager.getListJournalComptable();
//        for (JournalComptable journal : listJournaux) {
//            if (StringUtils.equals(journal.getCode(), "AC")) {
//                journalComptable1 = journal;
//            }
//        }
//
//        CompteComptable compte = comptabiliteManager.getListCompteComptable().get(0);
//        LigneEcritureComptable ligneEcriture = new LigneEcritureComptable(compte, "Ligne1", BigDecimal.valueOf(1500), null);
//        LigneEcritureComptable ligneEcriture2 = new LigneEcritureComptable(compte, "Ligne2", null, BigDecimal.valueOf(1500));
//        List<LigneEcritureComptable> listLigneEcriture = new ArrayList<>();
//        listLigneEcriture.add(ligneEcriture);
//        listLigneEcriture.add(ligneEcriture2);
//
//        EcritureComptable ecritureComptable = createEcritureComptable(null, journalComptable1, "AC-2018/09999", date, "ecriture insérée", listLigneEcriture);
//
//        comptabiliteManager.insertEcritureComptable(ecritureComptable);
//
//        comptabiliteManager.deleteEcritureComptable(ecritureComptable.getId());
//
//        List<EcritureComptable> listEcritures = comptabiliteManager.getListEcritureComptable();
//        for (EcritureComptable ecriture : listEcritures) {
//            if (ecriture.getId().equals(ecritureComptable.getId())) {
//                fail("L'écriture n'a pas été supprimé correctement");
//            }
//        }
//    }
//
//    /**
//     * Vérification de l'insertion de la séquence d'écriture comptable.
//     * Test passant.
//     */
//    @Test
//    public void insertSequenceEcritureComptableTest() {
//        SequenceEcritureComptable sequenceEcritureComptable = new SequenceEcritureComptable("OD", 2020, 1);
//        comptabiliteManager.insertSequenceEcritureComptable(sequenceEcritureComptable);
//
//        try {
//            SequenceEcritureComptable sequence = comptabiliteDao.getSequenceEcritureComptable(sequenceEcritureComptable.getJournalCode(), sequenceEcritureComptable.getAnnee());
//        } catch (NotFoundException e) {
//            fail("La séquence de l'écriture comptable n'a pas été insérée correctement" + e.getMessage());
//        }
//
//        comptabiliteDao.deleteSequenceEcritureComptable(sequenceEcritureComptable.getJournalCode(), sequenceEcritureComptable.getAnnee());
//    }
//
//    /**
//     * Vérification de la modification de la séquence d'écriture comptable.
//     * Test passant.
//     */
//    @Test
//    public void updateSequenceEcritureComptableTest() {
//        SequenceEcritureComptable sequenceEcritureComptable = new SequenceEcritureComptable("OD", 2020, 1);
//        comptabiliteManager.insertSequenceEcritureComptable(sequenceEcritureComptable);
//        SequenceEcritureComptable sequenceEcritureComptableModifiee = new SequenceEcritureComptable("OD", 2020, 50);
//        comptabiliteManager.updateSequenceEcritureComptable(sequenceEcritureComptableModifiee);
//
//        try {
//            SequenceEcritureComptable sequence = comptabiliteDao.getSequenceEcritureComptable(sequenceEcritureComptableModifiee.getJournalCode(), sequenceEcritureComptableModifiee.getAnnee());
//            if(sequence.getDerniereValeur() != 50){
//                fail("La séquence n'a été que partiellement modifiée : " + sequence.getDerniereValeur());
//            }
//        } catch (NotFoundException e) {
//            fail("La séquence de l'écriture comptable n'a pas été modifiée correctement" + e.getMessage());
//        }
//
//        comptabiliteDao.deleteSequenceEcritureComptable(sequenceEcritureComptableModifiee.getJournalCode(), sequenceEcritureComptableModifiee.getAnnee());
//    }
//
//    /**
//     * Vérification de la suppression d'une séquence d'écriture comptable.
//     * Test passant
//     * On insert une séquence, on la supprime et on la recherche en BDD.
//     * Doit renvoyer une NotFoundException
//     */
//    @Test (expected = NotFoundException.class)
//    public void deleteSequenceEcritureComptableTest() throws Exception {
//        SequenceEcritureComptable sequenceEcritureComptable = new SequenceEcritureComptable("OD", 2020, 1);
//        comptabiliteManager.insertSequenceEcritureComptable(sequenceEcritureComptable);
//
//        comptabiliteManager.deleteSequenceEcritureComptable(sequenceEcritureComptable);
//
//        comptabiliteDao.getSequenceEcritureComptable(sequenceEcritureComptable.getJournalCode(), sequenceEcritureComptable.getAnnee());
//    }
}
