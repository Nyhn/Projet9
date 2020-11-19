package com.dummy.myerp.business.impl.manager;

import java.math.BigDecimal;
import java.util.Date;
import com.dummy.myerp.business.impl.AbstractBusinessManager;
import com.dummy.myerp.business.impl.TransactionManager;
import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import com.dummy.myerp.consumer.dao.contrat.DaoProxy;
import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.exception.NotFoundException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.dummy.myerp.technical.exception.FunctionalException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ComptabiliteManagerImplTest {

    private final ComptabiliteManagerImpl comptabiliteManager = new ComptabiliteManagerImpl();

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test de la fonction checkEcritureComptableUnit
     * Vérifie qu'une ecriture comptable vide déclenche une FunctionalEsception
     * @throws Exception FunctionalException
     */
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnit_throwFunctonalException() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        comptabiliteManager.checkEcritureComptableUnit(vEcritureComptable);
    }

    /**
     * Test de la fonction checkEcritureComptableUnit
     * @throws Exception FunctionalException
     */
    @Test
    public void checkEcritureComptableUnit_testPassed() throws Exception {
        EcritureComptable ecritureComptable = new EcritureComptable();
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptable.setDate(new Date());
        ecritureComptable.setReference("AC-2020/00001");
        ecritureComptable.setLibelle("Libelle");
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(42),
                null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(42)));
        comptabiliteManager.checkEcritureComptableUnit(ecritureComptable);
    }

    /**
     * Test de la fonction checkEcritureComptableUnit
     * vérification que la RG2 n'est pas respecté
     * une functionalException est attendu
     * @throws Exception FunctionalException
     */
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG2_NoEquilibre_ThrowFunctionalException() throws Exception {
        EcritureComptable ecritureComptable;
        ecritureComptable = new EcritureComptable();
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptable.setReference("AC-2020/00001");
        ecritureComptable.setDate(new Date());
        ecritureComptable.setLibelle("Libelle");
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(42),
                                                                                 null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                                                                                 null, null,
                                                                                 new BigDecimal(424)));
        comptabiliteManager.checkEcritureComptableUnit(ecritureComptable);
    }

    /**
     * Test de la fonction checkEcritureComptableUnit
     * vérification que la RG2 est respecté
     * @throws Exception FunctionalException
     */
    @Test
    public void checkEcritureComptableUnitRG2_Equilibre_DontThrowException() throws Exception {
        EcritureComptable ecritureComptable;
        ecritureComptable = new EcritureComptable();
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptable.setReference("AC-2020/00001");
        ecritureComptable.setDate(new Date());
        ecritureComptable.setLibelle("Libelle");
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(42),
                null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(42)));
        comptabiliteManager.checkEcritureComptableUnit(ecritureComptable);
    }

    /**
     * Test de la fonction checkEcritureComptableUnit
     * vérification que la RG3 n'est pas respecté (car 2 débit sans crédit)
     * une functionalException est attendu
     * @throws Exception FunctionalException
     */
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG3_WithTwoDebit_throwFunctionalException() throws Exception {
        EcritureComptable ecritureComptable;
        ecritureComptable = new EcritureComptable();
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptable.setReference("AC-2020/00001");
        ecritureComptable.setDate(new Date());
        ecritureComptable.setLibelle("Libelle");
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(42),
                                                                                 null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(42),
                                                                                 null));
        comptabiliteManager.checkEcritureComptableUnit(ecritureComptable);
    }

    /**
     * Test de la fonction checkEcritureComptableUnit
     * vérification que la RG3 n'est pas respecté (car une seule ligne)
     * une functionalException est attendu
     * @throws Exception FunctionalException
     */
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG3_WithOneLine_throwFunctionalException() throws Exception {
        EcritureComptable ecritureComptable;
        ecritureComptable = new EcritureComptable();
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptable.setReference("AC-2020/00001");
        ecritureComptable.setDate(new Date());
        ecritureComptable.setLibelle("Libelle");
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(42),
                null));
        comptabiliteManager.checkEcritureComptableUnit(ecritureComptable);
    }

    /**
     * Test de la fonction checkEcritureComptableUnit
     * vérification que la RG3 n'est pas respecté (car 2 crédit sans débit)
     * une functionalException est attendu
     * @throws Exception FunctionalException
     */
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG3_WithTwoCredit_throwFunctionalException() throws Exception {
        EcritureComptable ecritureComptable;
        ecritureComptable = new EcritureComptable();
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptable.setReference("AC-2020/00001");
        ecritureComptable.setDate(new Date());
        ecritureComptable.setLibelle("Libelle");
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null,null,
                new BigDecimal(42)));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null,null,
                new BigDecimal(42)));
        comptabiliteManager.checkEcritureComptableUnit(ecritureComptable);
    }

    /**
     * Test de la fonction checkEcritureComptableUnit
     * vérification que la RG3 n'est pas respecté (car 3 crédit sans débit)
     * une functionalException est attendu
     * @throws Exception FunctionalException
     */
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG3_WithThreeCredit_throwFunctionalException() throws Exception {
        EcritureComptable ecritureComptable;
        ecritureComptable = new EcritureComptable();
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptable.setReference("AC-2020/00001");
        ecritureComptable.setDate(new Date());
        ecritureComptable.setLibelle("Libelle");
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null,null,
                new BigDecimal(42)));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null,null,
                new BigDecimal(42)));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null,null,
                new BigDecimal(42)));
        comptabiliteManager.checkEcritureComptableUnit(ecritureComptable);
    }

    /**
     * Test de la fonction checkEcritureComptableUnit
     * vérification que la RG3 est respecté (car 3 lignes avec au moins 1 débit et 1 crédit)
     * une functionalException n'est pas attendu
     * @throws Exception FunctionalException
     */
    @Test
    public void checkEcritureComptableUnitRG3AndRG2_WithThreeLine_NoThrowFunctionalException() throws Exception {
        EcritureComptable ecritureComptable;
        ecritureComptable = new EcritureComptable();
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptable.setDate(new Date());
        ecritureComptable.setLibelle("Libelle");
        ecritureComptable.setReference("AC-2020/00001");
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null,null,
                new BigDecimal(42)));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null,null,
                new BigDecimal(42)));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null,new BigDecimal(84),
                null));
        comptabiliteManager.checkEcritureComptableUnit(ecritureComptable);
    }


    /**
     * Test de la fonction checkEcritureComptableUnit
     * vérification que la RG5 n'est pas respecté sur un ensemble de référence (Format non respecté)
     * une functionalException est attendu
     * @param ref mauvaise référence
     */
    @ParameterizedTest(name = "{0} bad référence throw FunctionalException")
    @ValueSource(strings = {"AC-20m0/00001","ACA-2020/00001","AC-200/000X1","AC-2020/000018","AC-2020/0018"})
    public void checkEcritureComptableUnitRG5_BadFormat_ThrowFunctionalException(String ref){

        EcritureComptable ecritureComptable;
        ecritureComptable = new EcritureComptable();
        ecritureComptable.setId(1);
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptable.setDate(new Date());
        ecritureComptable.setReference(ref);
        ecritureComptable.setLibelle("Libelle");
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(42),
                null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(42)));


        Assertions.assertThrows(FunctionalException.class,() -> comptabiliteManager.checkEcritureComptableUnit(ecritureComptable));
    }

    /**
     * Test de la fonction checkEcritureComptableUnit
     * vérification que la RG5 n'est pas respecté (car mauvaise année)
     * une functionalException est attendu
     * @throws Exception FunctionalException
     */
     @Test(expected = FunctionalException.class)
     public void checkEcritureComptableUnitRG5_BadYear_ThrowFunctionalException() throws Exception {
        EcritureComptable ecritureComptable;
        ecritureComptable = new EcritureComptable();
        ecritureComptable.setId(1);
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptable.setDate(new Date());
        ecritureComptable.setReference("AC-2019/00001");
        ecritureComptable.setLibelle("Libelle");
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(42),
                null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(42)));

        comptabiliteManager.checkEcritureComptableUnit(ecritureComptable);
    }

    /**
     * Test de la fonction checkEcritureComptableUnit
     * vérification que la RG5 n'est pas respecté (car mauvais code)
     * une functionalException est attendu
     * @throws Exception FunctionalException
     */
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG5_BadCode_ThrowFunctionalException() throws Exception {
        EcritureComptable ecritureComptable;
        ecritureComptable = new EcritureComptable();
        ecritureComptable.setId(1);
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptable.setDate(new Date());
        ecritureComptable.setReference("AT-2019/00001");
        ecritureComptable.setLibelle("Libelle");
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(42),
                null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(42)));

        comptabiliteManager.checkEcritureComptableUnit(ecritureComptable);
    }


    /**
     * Test de la fonction checkEcritureComptableContext
     * @throws Exception Exception
     */
    @Test
    public void checkEcritureComptableContext_NoReturn_testPassed() throws Exception {

        EcritureComptable ecritureComptable;
        ecritureComptable = new EcritureComptable();
        ecritureComptable.setId(1);
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptable.setDate(new Date());
        ecritureComptable.setReference("AC-2018/00001");
        ecritureComptable.setLibelle("Libellé");
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(42),
                null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(42)));

        DaoProxy daoProxy = Mockito.mock(DaoProxy.class);
        ComptabiliteDao comptabiliteDao = Mockito.mock(ComptabiliteDao.class);

        Mockito.when(daoProxy.getComptabiliteDao()).thenReturn(comptabiliteDao);
        Mockito.when(comptabiliteDao.getEcritureComptableByRef("AC-2018/00001")).thenReturn(ecritureComptable);

        AbstractBusinessManager.configure(null, daoProxy, null);
        comptabiliteManager.checkEcritureComptableContext(ecritureComptable);
    }

    /**
     * Test de la fonction checkEcritureComptableContext
     * vérification que la RG6 n'est pas respecté (car ecritureComptable déjà existante)
     * une functionalException est attendu
     * @throws Exception FunctionalException
     */
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableContextRG6_double_throwFunctionalException() throws Exception {

        EcritureComptable ecritureComptable;
        ecritureComptable = new EcritureComptable();
        ecritureComptable.setId(1);
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptable.setDate(new Date());
        ecritureComptable.setReference("AC-2018/00001");
        ecritureComptable.setLibelle("Libellé");
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(42),
                null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(42)));

        EcritureComptable ecritureComptableDouble;
        ecritureComptableDouble = new EcritureComptable();
        ecritureComptableDouble.setId(2);
        ecritureComptableDouble.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptableDouble.setDate(new Date());
        ecritureComptableDouble.setReference("AC-2018/00001");
        ecritureComptableDouble.setLibelle("Libellé");
        ecritureComptableDouble.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(42),
                null));
        ecritureComptableDouble.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(42)));

        DaoProxy daoProxy = Mockito.mock(DaoProxy.class);
        ComptabiliteDao comptabiliteDao = Mockito.mock(ComptabiliteDao.class);

        Mockito.when(daoProxy.getComptabiliteDao()).thenReturn(comptabiliteDao);
        Mockito.when(comptabiliteDao.getEcritureComptableByRef("AC-2018/00001")).thenReturn(ecritureComptable);

        AbstractBusinessManager.configure(null, daoProxy, null);
        comptabiliteManager.checkEcritureComptableContext(ecritureComptableDouble);
    }

    /**
     * Test de la fonction checkEcritureComptableContext
     * vérification que la RG6 n'est pas respecté (car une nouvelle ecritureComptable est déjà existante)
     * une functionalException est attendu
     * @throws Exception FunctionalException
     */
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableContextRG6_NewEcritureComptableFound_throwFunctionalException() throws Exception {

        EcritureComptable ecritureComptable;
        ecritureComptable = new EcritureComptable();
        ecritureComptable.setId(1);
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptable.setDate(new Date());
        ecritureComptable.setReference("AC-2018/00001");
        ecritureComptable.setLibelle("Libellé");
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(42),
                null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(42)));

        EcritureComptable ecritureComptableDouble;
        ecritureComptableDouble = new EcritureComptable();
        ecritureComptableDouble.setId(2);
        ecritureComptableDouble.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptableDouble.setDate(new Date());
        ecritureComptableDouble.setReference("AC-2018/00002");
        ecritureComptableDouble.setLibelle("Libellé");
        ecritureComptableDouble.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(42),
                null));
        ecritureComptableDouble.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(42)));

        DaoProxy daoProxy = Mockito.mock(DaoProxy.class);
        ComptabiliteDao comptabiliteDao = Mockito.mock(ComptabiliteDao.class);

        Mockito.when(daoProxy.getComptabiliteDao()).thenReturn(comptabiliteDao);
        Mockito.when(comptabiliteDao.getEcritureComptableByRef("AC-2018/00001")).thenReturn(ecritureComptableDouble);

        AbstractBusinessManager.configure(null, daoProxy, null);
        comptabiliteManager.checkEcritureComptableContext(ecritureComptable);
    }

    /**
     * Test de la fonction checkEcritureComptableContext
     * vérification que la RG6 n'est pas respecté (car ecritureComptable n'existe pas)
     * une functionalException est attendu
     * @throws Exception FunctionalException
     */
    @Test
    public void checkEcritureComptableContextRG6_EcritureComptableNotFound_throwFunctionalException() throws Exception {

        EcritureComptable ecritureComptable;
        ecritureComptable = new EcritureComptable();
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptable.setDate(new Date());
        ecritureComptable.setReference("AC-2018/00001");
        ecritureComptable.setLibelle("Libellé");
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(42),
                null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(42)));

        DaoProxy daoProxy = Mockito.mock(DaoProxy.class);
        ComptabiliteDao comptabiliteDao = Mockito.mock(ComptabiliteDao.class);

        Mockito.when(daoProxy.getComptabiliteDao()).thenReturn(comptabiliteDao);
        Mockito.doThrow(NotFoundException.class).when(comptabiliteDao).getEcritureComptableByRef("AC-2018/00001");

        AbstractBusinessManager.configure(null, daoProxy, null);
        comptabiliteManager.checkEcritureComptableContext(ecritureComptable);
    }

    /**
     * Test de la fonction checkEcritureComptable
     * vérification que les régles de gestion unitaire et du contexte sont respecté
     * @throws Exception FunctionalException
     */
    @Test
    public void checkEcritureComptable_NoReturn_NotThrowException() throws Exception {

        EcritureComptable ecritureComptable;
        ecritureComptable = new EcritureComptable();
        ecritureComptable.setId(1);
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptable.setDate(new Date());
        ecritureComptable.setReference("AC-2020/00001");
        ecritureComptable.setLibelle("Libellé");
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(42),
                null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(42)));

        DaoProxy daoProxy = Mockito.mock(DaoProxy.class);
        ComptabiliteDao comptabiliteDao = Mockito.mock(ComptabiliteDao.class);

        Mockito.when(daoProxy.getComptabiliteDao()).thenReturn(comptabiliteDao);
        Mockito.when(comptabiliteDao.getEcritureComptableByRef("AC-2020/00001")).thenReturn(ecritureComptable);

        AbstractBusinessManager.configure(null, daoProxy, null);
        comptabiliteManager.checkEcritureComptable(ecritureComptable);
    }

    /**
     * Test de addReference
     * @throws NotFoundException FunctionalException
     */
    @Test
    public void addReference_NoReturn_EcritureComptable() throws NotFoundException {
        EcritureComptable ecritureComptable = new EcritureComptable();
        ecritureComptable.setDate(new Date());
        ecritureComptable.setId(21);
        ecritureComptable.setLibelle("Ecriture comptable test add reference");
        ecritureComptable.setJournal(new JournalComptable("VE","test"));
        ecritureComptable.getListLigneEcriture().add(
                new LigneEcritureComptable(
                        new CompteComptable(1),null,new BigDecimal(42)
                        ,null
                ));
        ecritureComptable.getListLigneEcriture().add(
                new LigneEcritureComptable(
                        new CompteComptable(2),null,null
                        ,new BigDecimal(42)
                ));

        SequenceEcritureComptable sequenceEcritureComptable = new SequenceEcritureComptable("VE",2020,16);

        DaoProxy daoProxy = Mockito.mock(DaoProxy.class);
        ComptabiliteDao comptabiliteDao = Mockito.mock(ComptabiliteDao.class);
        TransactionManager transactionManager = Mockito.mock(TransactionManager.class);

        Mockito.when(daoProxy.getComptabiliteDao()).thenReturn(comptabiliteDao);
        Mockito.when(comptabiliteDao.getSequenceEcritureComptable("VE",2020)).thenReturn(sequenceEcritureComptable);

        AbstractBusinessManager.configure(null,daoProxy,transactionManager);
        comptabiliteManager.addReference(ecritureComptable);

        Assert.assertEquals(ecritureComptable.toString(),"VE-2020/00017",ecritureComptable.getReference());
    }

    /**
     * Test de addReference
     * Une NotFoundException est attendu
     * @throws NotFoundException FunctionalException
     */
    @Test
    public void addReference_NoReturn_EcritureComptable_ThrowNotFoundException() throws NotFoundException {
        EcritureComptable ecritureComptable = new EcritureComptable();
        ecritureComptable.setDate(new Date());
        ecritureComptable.setId(21);
        ecritureComptable.setLibelle("libelle");
        ecritureComptable.setJournal(new JournalComptable("VE","test"));
        ecritureComptable.getListLigneEcriture().add(
                new LigneEcritureComptable(
                        new CompteComptable(1),null,new BigDecimal(42)
                        ,null
                ));
        ecritureComptable.getListLigneEcriture().add(
                new LigneEcritureComptable(
                        new CompteComptable(2),null,null
                        ,new BigDecimal(42)
                ));


        DaoProxy daoProxy = Mockito.mock(DaoProxy.class);
        ComptabiliteDao comptabiliteDao = Mockito.mock(ComptabiliteDao.class);
        TransactionManager transactionManager = Mockito.mock(TransactionManager.class);

        Mockito.when(daoProxy.getComptabiliteDao()).thenReturn(comptabiliteDao);
        Mockito.doThrow(NotFoundException.class).when(comptabiliteDao).getSequenceEcritureComptable("VE",2020);

        AbstractBusinessManager.configure(null, daoProxy, transactionManager);
        comptabiliteManager.addReference(ecritureComptable);

        Assert.assertEquals(ecritureComptable.toString(),"VE-2020/00001",ecritureComptable.getReference());
    }

}
