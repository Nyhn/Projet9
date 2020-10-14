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
     * Vérification de l'initialisation du context Spring
     */
    @Test
    public void testInitSpring() {
        SpringRegistry.init();
        assertNotNull(SpringRegistry.getDaoProxy());
    }

    /**
     * Test de récupération de la liste de compte comptable
     * et vérification que la liste contient 7 élèments
     * et que le premier élèment de la liste est égale au numéro "401" et au libellé "Fournisseurs"
     */
    @Test
    public void getListCompteComptableTest_listCompteComptable_checkFirstCompteComptableAndSizeListEqual7() {
        List<CompteComptable> listCompteComptable = comptabiliteDao.getListCompteComptable();

        assertTrue("La liste des comptes est différent de 7", listCompteComptable.size() == 7 );

        boolean testCheck = false;
        if(listCompteComptable.get(0).getNumero() == 401 && StringUtils.equals(listCompteComptable.get(0).getLibelle(),"Fournisseurs")){
            testCheck = true;
        }
        assertTrue("Le compte de numéro 401 et de libellé Fournisseurs n'existe pas ou n'est pas le premier élèment de la liste", testCheck);
    }

    /**
     * Test de récupération de la liste de journal comptable
     * et vérification que la liste contient 4 élèments
     * et que le premier élèment de la liste est égale au code "AC" et au libellé "Achat"
     */
    @Test
    public void getListJournalComptableTest_listJournalComptable_checkFirstJournalComptableAndSIzeListEqual4() {
        List<JournalComptable> listJournalComptable = comptabiliteDao.getListJournalComptable();
        
        assertTrue("La liste des journaux est différent de 4", listJournalComptable.size() == 4);
        boolean testCheck = false;
        if(StringUtils.equals(listJournalComptable.get(0).getCode(),"AC") && StringUtils.equals(listJournalComptable.get(0).getLibelle(),"Achat"))
        {
            testCheck = true;
        }
        assertTrue("Le journal de code AC et de libellé Achat n'existe pas ou n'est pas le premier élèment de la liste", testCheck);
    }

    /**
     * Test de récupération de la liste des écritures comptables
     * Vérification que la taille de liste est égale à 5
     * et que la première écriture comptable vaut :
     * - id = -5
     * - code journal = BQ
     * - reference = BQ-2016/00005
     * - data = 2016/12/27 00:00:00
     * - libellé = Paiement Facture C110002
     * - 2 lignes d'écriture :
     *      - Code compte comptable ligne écriture 2 = 411
     *      - Crédit ligne écriture 2 = 3000
     *
     */
    @Test
    public void getListEcritureComptableTest_listEcritureComtable_checkFirstEcritureComptableAndSizeListEqual5() throws ParseException {
        List<EcritureComptable> listEcritureComptable = comptabiliteDao.getListEcritureComptable();

        assertTrue("La liste des écritures n'est pas égale à 5", listEcritureComptable.size() == 5);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        Date date = simpleDateFormat.parse("2016/12/27 00:00:00");
        boolean testCheck = false;
        if(listEcritureComptable.get(0).getId() == -5 &&
                StringUtils.equals(listEcritureComptable.get(0).getJournal().getCode(), "BQ") &&
                StringUtils.equals(listEcritureComptable.get(0).getReference(), "BQ-2016/00005") &&
                StringUtils.equals(listEcritureComptable.get(0).getLibelle(), "Paiement Facture C110002") &&
                listEcritureComptable.get(0).getDate().compareTo(date) == 0 &&
                listEcritureComptable.get(0).getListLigneEcriture().size() == 2 &&
                listEcritureComptable.get(0).getListLigneEcriture().get(1).getCompteComptable().getNumero() == 411 &&
                listEcritureComptable.get(0).getListLigneEcriture().get(1).getCredit().compareTo(BigDecimal.valueOf(3000)) == 0)
        {
            testCheck = true;
        }
        assertTrue("L'écriture comptable d'ID -5, de code journal BQ, de référence BQ-2016/00005, de date 2016/12/27 00:00:00 et de libellé Paiement Facture C110002 n'existe pas ou n'est pas le premier élèment de la liste", testCheck);
    }

    /**
     * Test de récupération d'une écriture comptable par son id -4
     * puis vérification des données de l'écriture :
     * - id = -4
     * - code journal = VE
     * - reference = VE-2016/00004
     * - data = 2016/12/28 00:00:00
     * - libellé = TMA Appli Yyy
     * - 3 ligne d'écriture associées à cette écriture
     * - Code compte comptable ligne écriture 2 = 706
     * - Libellé de la ligne écriture 2 = TMA Appli Xxx
     */
    @Test
    public void getEcritureComptableTest_EcritureComptable_checkEcritureComptable() throws ParseException {
        EcritureComptable ecritureComptable = new EcritureComptable();
        try {
            ecritureComptable = comptabiliteDao.getEcritureComptable(-4);
        } catch (NotFoundException e) {
            fail();
        }
        boolean testCheck = false;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        Date dateAttendue = simpleDateFormat.parse("2016/12/28 00:00:00");

        if(ecritureComptable.getId() == -4 &&
            StringUtils.equals(ecritureComptable.getJournal().getCode(), "VE") &&
            StringUtils.equals(ecritureComptable.getReference(), "VE-2016/00004") &&
            StringUtils.equals(ecritureComptable.getLibelle(), "TMA Appli Yyy") &&
            ecritureComptable.getDate().compareTo(dateAttendue) == 0 &&
            ecritureComptable.getListLigneEcriture().size() == 3 &&
            ecritureComptable.getListLigneEcriture().get(1).getCompteComptable().getNumero() == 706 &&
            StringUtils.equals(ecritureComptable.getListLigneEcriture().get(1).getLibelle(), "TMA Appli Xxx"))
        {
            testCheck = true;
        }
        assertTrue("L'écriture comptable d'ID -4, de code journal VE, de référence VE-2016/00004, de date 2016/12/28 00:00:00 et de libellé TMA Appli Yyy n'a pas été récupéré correctement", testCheck);
    }

    /**
     * Test de récupération d'une écriture comptable inexistant par son id.
     * Doit renvoyer une exception NotFoundException
     *
     * @throws NotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void getEcritureComptableTest_EcritureComptable_throwNotFoundException() throws NotFoundException {
        comptabiliteDao.getEcritureComptable(659);
    }

    /**
     * Test de récupération d'une écriture comptable par sa référence VE-2016/00004
     * puis vérification des données de l'écriture comptable :
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
    public void getEcritureComptableByRefTest_EcritureComptable_checkEcritureComptable() throws ParseException {
        EcritureComptable ecritureComptable = new EcritureComptable();
        try {
            ecritureComptable = comptabiliteDao.getEcritureComptableByRef("VE-2016/00004");
        } catch (NotFoundException e) {
            fail();
        }
        boolean testCheck = false;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        Date dateAttendue = simpleDateFormat.parse("2016/12/28 00:00:00");

        if(ecritureComptable.getId() == -4 &&
            StringUtils.equals(ecritureComptable.getJournal().getCode(), "VE") &&
            StringUtils.equals(ecritureComptable.getReference(), "VE-2016/00004") &&
            StringUtils.equals(ecritureComptable.getLibelle(), "TMA Appli Yyy") &&
            ecritureComptable.getDate().compareTo(dateAttendue) == 0 &&
            ecritureComptable.getListLigneEcriture().size() == 3 &&
            ecritureComptable.getListLigneEcriture().get(1).getCompteComptable().getNumero() == 706 &&
            StringUtils.equals(ecritureComptable.getListLigneEcriture().get(1).getLibelle(), "TMA Appli Xxx"))
        {
            testCheck = true;
        }
        assertTrue("L'écriture comptable d'ID -4, de code journal VE, de référence VE-2016/00004, de date 2016/12/28 00:00:00 et de libellé TMA Appli Yyy n'a pas été récupéré correctement", testCheck);
    }

    /**
     * Test de récupération d'une écriture comptable inexistant par sa référence.
     * Doit renvoyer une exception NotFoundException
     *
     * @throws NotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void getEcritureComptableByRefTest_EcritureComptable_throwNotFoundException() throws NotFoundException {
        comptabiliteDao.getEcritureComptableByRef("ZZ-2018/90004");
    }

    /**
     * Test de chargement de la liste des lignes d'écriture dans une écriture.
     * Le test vérifie que la ligne d'écriture présente est remplacé par la liste chargé.
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
    public void loadListLigneEcritureTest_NoReturn_checkListLigneEcritureComptableInEcritureComptable() throws ParseException {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        Date date = simpleDateFormat.parse("2016/12/28 00:00:00");

        JournalComptable journalComptable = new JournalComptable("VE", "Vente");

        CompteComptable compteComptable = new CompteComptable(999);
        LigneEcritureComptable ligneEcritureComptable = new LigneEcritureComptable(compteComptable, "Ligne1", BigDecimal.valueOf(100), null);
        List<LigneEcritureComptable> ligneEcritureComptableList = new ArrayList<>();
        ligneEcritureComptableList.add(ligneEcritureComptable);

        EcritureComptable ecritureComptable = createEcritureComptable(-4, journalComptable, "VE-2016/00004", date,"TMA Appli Yyy", ligneEcritureComptableList );

        comptabiliteDao.loadListLigneEcriture(ecritureComptable);

        boolean testCheck = false;
        if(ecritureComptable.getId() == -4 &&
        StringUtils.equals(ecritureComptable.getJournal().getCode(), "VE") &&
        StringUtils.equals(ecritureComptable.getReference(), "VE-2016/00004") &&
        StringUtils.equals(ecritureComptable.getLibelle(), "TMA Appli Yyy") &&
        ecritureComptable.getDate().compareTo(date) == 0 &&
        ecritureComptable.getListLigneEcriture().size() == 3 &&
        ecritureComptable.getListLigneEcriture().get(0).getCompteComptable().getNumero() != 999 &&
        ecritureComptable.getListLigneEcriture().get(1).getCompteComptable().getNumero() == 706 &&
        StringUtils.equals(ecritureComptable.getListLigneEcriture().get(1).getLibelle(), "TMA Appli Xxx"))
        {
            testCheck = true;
        }
        assertTrue("Le chargement des lignes d'écritures ne s'est pas fait correctement", testCheck);
    }

    /**
     * Test d'insertion d'une écriture comptable
     * puis récupération par l'id pour vérification
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
     * puis suppresion de l'insertion
     */
    @Test
    public void insertEcritureComptableTest_NoReturn_checkbyGetId() throws ParseException {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        Date date = simpleDateFormat.parse("2018/12/19 00:00:00");

        JournalComptable journalComptable = comptabiliteDao.getListJournalComptable().get(0);

        CompteComptable compteComptable = comptabiliteDao.getListCompteComptable().get(0);
        LigneEcritureComptable ligneEcritureComptable = new LigneEcritureComptable(compteComptable, "Ligne1", BigDecimal.valueOf(1500), null);
        LigneEcritureComptable ligneEcritureComptable1 = new LigneEcritureComptable(compteComptable, "Ligne2", null, BigDecimal.valueOf(1500));
        List<LigneEcritureComptable> ligneEcritureComptableList = new ArrayList<>();
        ligneEcritureComptableList.add(ligneEcritureComptable);
        ligneEcritureComptableList.add(ligneEcritureComptable1);

        EcritureComptable ecritureComptable = createEcritureComptable(null, journalComptable, "ZZ-2018/00001", date, "ecriture insérée", ligneEcritureComptableList);
        comptabiliteDao.insertEcritureComptable(ecritureComptable);
        boolean testCheck = false;
        try {
            EcritureComptable ecritureComptableInseree = comptabiliteDao.getEcritureComptable(ecritureComptable.getId());
            if( ecritureComptableInseree.getId().equals(ecritureComptable.getId()) &&
                ecritureComptableInseree.getDate().compareTo(ecritureComptable.getDate()) == 0 &&
                StringUtils.equals(ecritureComptableInseree.getJournal().getCode(), ecritureComptable.getJournal().getCode()) &&
                StringUtils.equals(ecritureComptableInseree.getLibelle(), ecritureComptable.getLibelle()) &&
                StringUtils.equals(ecritureComptableInseree.getReference(), ecritureComptable.getReference()) &&
                ecritureComptableInseree.getListLigneEcriture().size() == ecritureComptable.getListLigneEcriture().size() &&
                StringUtils.equals(ecritureComptableInseree.getListLigneEcriture().get(0).getLibelle(), ecritureComptable.getListLigneEcriture().get(0).getLibelle()) &&
                StringUtils.equals(ecritureComptableInseree.getListLigneEcriture().get(1).getLibelle(), ecritureComptable.getListLigneEcriture().get(1).getLibelle())) {

                testCheck = true;
            }
        } catch (NotFoundException e) {
            fail();
        }
        assertTrue("L'insertion de l'écriture comptable a échoué", testCheck);
        comptabiliteDao.deleteEcritureComptable(ecritureComptable.getId());
    }

    /**
     * Test de mise à jour d'une écriture comptable
     * puis vérification de la mise à jour
     */
    @Test
    public void updateEcritureComptableTest_NoReturn_checkEcritureComptable() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        Date date = simpleDateFormat.parse("2018/12/19 00:00:00");

        JournalComptable journalComptable = comptabiliteDao.getListJournalComptable().get(0);

        CompteComptable compteComptable = comptabiliteDao.getListCompteComptable().get(0);
        LigneEcritureComptable ligneEcritureComptable = new LigneEcritureComptable(compteComptable, "Ligne1", BigDecimal.valueOf(1500), null);
        LigneEcritureComptable ligneEcritureComptable1 = new LigneEcritureComptable(compteComptable, "Ligne2", null, BigDecimal.valueOf(1500));
        List<LigneEcritureComptable> ligneEcritureComptableList = new ArrayList<>();
        ligneEcritureComptableList.add(ligneEcritureComptable);
        ligneEcritureComptableList.add(ligneEcritureComptable1);

        EcritureComptable ecritureComptable = createEcritureComptable(null, journalComptable, "ZZ-2018/00001", date, "ecriture insérée", ligneEcritureComptableList);
        comptabiliteDao.insertEcritureComptable(ecritureComptable);

        Date date1 = simpleDateFormat.parse("2018/12/19 00:00:00");
        JournalComptable journalComptable1 = comptabiliteDao.getListJournalComptable().get(1);

        CompteComptable compteComptable1 = comptabiliteDao.getListCompteComptable().get(0);
        LigneEcritureComptable ligneEcritureComptable2 = new LigneEcritureComptable(compteComptable, "Ligne3", BigDecimal.valueOf(3500), null);
        LigneEcritureComptable ligneEcritureComptable3 = new LigneEcritureComptable(compteComptable, "Ligne4", null, BigDecimal.valueOf(3500));
        List<LigneEcritureComptable> listLigneEcriture2 = new ArrayList<>();
        listLigneEcriture2.add(ligneEcritureComptable2);
        listLigneEcriture2.add(ligneEcritureComptable3);

        EcritureComptable ecritureComptableModifiee = createEcritureComptable(ecritureComptable.getId(), journalComptable1, "YY-2018/00001", date1, "ecriture modifié", listLigneEcriture2);
        comptabiliteDao.updateEcritureComptable(ecritureComptableModifiee);

        boolean testCheck = false;
        try {
            EcritureComptable ecritureComptable1 = comptabiliteDao.getEcritureComptable(ecritureComptable.getId());
            if( ecritureComptable1.getDate().compareTo(ecritureComptableModifiee.getDate()) == 0 &&
                StringUtils.equals(ecritureComptable1.getJournal().getCode(), ecritureComptableModifiee.getJournal().getCode()) &&
                StringUtils.equals(ecritureComptable1.getLibelle(), ecritureComptableModifiee.getLibelle()) &&
                StringUtils.equals(ecritureComptable1.getReference(), ecritureComptableModifiee.getReference()) &&
                ecritureComptable1.getListLigneEcriture().size() == ecritureComptableModifiee.getListLigneEcriture().size() &&
                StringUtils.equals(ecritureComptable1.getListLigneEcriture().get(0).getLibelle(), ecritureComptableModifiee.getListLigneEcriture().get(0).getLibelle()) &&
                StringUtils.equals(ecritureComptable1.getListLigneEcriture().get(1).getLibelle(), ecritureComptableModifiee.getListLigneEcriture().get(1).getLibelle()))
            {
                testCheck = true;
            }
        } catch (NotFoundException e) {
            fail();
        }
        assertTrue("La mise à jour de l'écriture comptable a échoué", testCheck);
        comptabiliteDao.deleteEcritureComptable(ecritureComptableModifiee.getId());
    }

    /**
     * Vérification de la suppression d'une écriture comptable.
     */
    @Test
    public void deleteEcritureComptableTest_NoReturn_checkEcritureComptableNotFound() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        Date date = simpleDateFormat.parse("2018/12/19 00:00:00");

        JournalComptable journalComptable = comptabiliteDao.getListJournalComptable().get(0);

        CompteComptable compteComptable = comptabiliteDao.getListCompteComptable().get(0);
        LigneEcritureComptable ligneEcritureComptable = new LigneEcritureComptable(compteComptable, "Ligne1", BigDecimal.valueOf(1500), null);
        LigneEcritureComptable ligneEcritureComptable1 = new LigneEcritureComptable(compteComptable, "Ligne2", null, BigDecimal.valueOf(1500));
        List<LigneEcritureComptable> ligneEcritureComptableList = new ArrayList<>();
        ligneEcritureComptableList.add(ligneEcritureComptable);
        ligneEcritureComptableList.add(ligneEcritureComptable1);

        EcritureComptable ecritureComptable = createEcritureComptable(null, journalComptable, "ZZ-2018/00001", date, "ecriture insérée", ligneEcritureComptableList);
        comptabiliteDao.insertEcritureComptable(ecritureComptable);

        comptabiliteDao.deleteEcritureComptable(ecritureComptable.getId());
        boolean testCheck = false;
        try {
            comptabiliteDao.getEcritureComptable(ecritureComptable.getId());
        } catch (NotFoundException e) {
            testCheck = true;
        }
        assertTrue("La suppression de l'écriture comptable a échoué", testCheck);
    }

    /**
     * Test de récupération d'une séquence d'écriture comptable.
     * Vérification des données de la séquence :
     * code journal : AC
     * année : 2016
     * dernière valeur : 40
     */
    @Test
    public void getSequenceEcritureComptableTest_SequenceEcritureComptable_withJournalCodeAndAnnee_checkDerniereValeur() throws NotFoundException {
        SequenceEcritureComptable sequenceEcritureComptable = comptabiliteDao.getSequenceEcritureComptable("AC", 2016);

        assertEquals("La séquence d'écriture comptable n'existe pas", sequenceEcritureComptable.getDerniereValeur(), Integer.valueOf(40));
    }

    /**
     * Test de récupération d'une séquence d'écriture comptable inexistante.
     * Doit renvoyer une exception Not Found Exception
     *
     * @throws NotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void getSequenceEcritureComptableTest_SequenceEcritureComptable_throwNotFoundException() throws NotFoundException {
        comptabiliteDao.getSequenceEcritureComptable("UK", 2020);
    }

    /**
     * Test de suppression d'une séquence d'écriture comptable
     * NotFoundException est déclenché en cherchant l'élément supprimé
     *
     * @throws NotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void deleteSequenceEcritureComptableTest_NoReturn_ThrowNotFoundException() throws NotFoundException {
        SequenceEcritureComptable sequenceEcritureComptable = new SequenceEcritureComptable("AC", 2055, 555);
        comptabiliteDao.insertSequenceEcritureComptable(sequenceEcritureComptable);
        comptabiliteDao.deleteSequenceEcritureComptable("AC", 2055);
        comptabiliteDao.getSequenceEcritureComptable("AC", 2055);
    }

    /**
     * Test d'insertion d'une séquence d'écriture comptable
     */
    @Test
    public void insertSequenceEcritureComptableTest_NoReturn() throws NotFoundException {

        SequenceEcritureComptable sequenceEcritureComptable = new SequenceEcritureComptable("AC", 2019, 15);
        comptabiliteDao.insertSequenceEcritureComptable(sequenceEcritureComptable);

        SequenceEcritureComptable sequenceEcritureComptableInseree = comptabiliteDao.getSequenceEcritureComptable("AC", 2019);

        assertEquals("L'insertion de la séquence a échoué", sequenceEcritureComptableInseree.getDerniereValeur(), sequenceEcritureComptable.getDerniereValeur());
        comptabiliteDao.deleteSequenceEcritureComptable("AC", 2019);
    }

    /**
     * Test de mise à jour d'une séquence écriture comptable
     * @throws NotFoundException
     */
    @Test
    public void updateSequenceEcritureComptableTest_NoReturn() throws NotFoundException {
        SequenceEcritureComptable sequenceEcritureComptable = new SequenceEcritureComptable("AC", 2019, 15);
        comptabiliteDao.insertSequenceEcritureComptable(sequenceEcritureComptable);

        SequenceEcritureComptable sequenceEcritureComptable1 = new SequenceEcritureComptable("AC", 2019, 76);
        comptabiliteDao.updateSequenceEcritureComptable(sequenceEcritureComptable1);

        SequenceEcritureComptable sequenceEcritureComptableModifiee = comptabiliteDao.getSequenceEcritureComptable("AC", 2019);
        assertEquals("La mise à jour a échoué", sequenceEcritureComptableModifiee.getDerniereValeur(), sequenceEcritureComptable1.getDerniereValeur());
        comptabiliteDao.deleteSequenceEcritureComptable("AC", 2019);
    }
}