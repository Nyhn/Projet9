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

    @Test
    public void checkEcritureComptableUnit() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setReference("AC-2020/00001");
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(123),
                                                                                 null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                                                                                 null, null,
                                                                                 new BigDecimal(123)));
        comptabiliteManager.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitViolation() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        comptabiliteManager.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG2_NoEquilibre_ThrowFunctionalException() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setReference("AC-2020/00001");
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(123),
                                                                                 null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                                                                                 null, null,
                                                                                 new BigDecimal(1234)));
        comptabiliteManager.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test
    public void checkEcritureComptableUnitRG2_Equilibre_DontThrowException() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setReference("AC-2020/00001");
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(123)));
        comptabiliteManager.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG3_WithTwoDebit_throwFunctionalException() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setReference("AC-2020/00001");
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(123),
                                                                                 null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(123),
                                                                                 null));
        comptabiliteManager.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG3_WithOneLine_throwFunctionalException() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setReference("AC-2020/00001");
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        comptabiliteManager.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG3_WithTwoCredit_throwFunctionalException() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setReference("AC-2020/00001");
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null,null,
                new BigDecimal(123)));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null,null,
                new BigDecimal(123)));
        comptabiliteManager.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG3_WithThreeCredit_throwFunctionalException() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setReference("AC-2020/00001");
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null,null,
                new BigDecimal(123)));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null,null,
                new BigDecimal(123)));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null,null,
                new BigDecimal(123)));
        comptabiliteManager.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test
    public void checkEcritureComptableUnitRG3AndRG2_WithThreeLine_NoThrowFunctionalException() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.setReference("AC-2020/00001");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null,null,
                new BigDecimal(123)));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null,null,
                new BigDecimal(123)));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null,new BigDecimal(246),
                null));
        comptabiliteManager.checkEcritureComptableUnit(vEcritureComptable);
    }


    @ParameterizedTest(name = "{0} bad référence throw FunctionalException")
    @ValueSource(strings = {"AC-20m0/00001","ACA-2020/00001","AC-200/000X1","AC-2020/000018","AC-2020/0018"})
    public void checkEcritureComptableUnitRG5_BadFormat_ThrowFunctionalException(String ref){
        System.out.println("valeur = "+ref);
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setId(1);
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setReference(ref);
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(123)));


        Assertions.assertThrows(FunctionalException.class,() -> comptabiliteManager.checkEcritureComptableUnit(vEcritureComptable));
    }

     @Test(expected = FunctionalException.class)
     public void checkEcritureComptableUnitRG5_BadYear_ThrowFunctionalException() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setId(1);
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setReference("AC-2019/00001");
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(123)));

        comptabiliteManager.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG5_BadCode_ThrowFunctionalException() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setId(1);
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setReference("AT-2019/00001");
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(123)));

        comptabiliteManager.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test
    public void checkEcritureComptableContext() throws Exception {

        EcritureComptable ecritureComptable;
        ecritureComptable = new EcritureComptable();
        ecritureComptable.setId(1);
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptable.setDate(new Date());
        ecritureComptable.setReference("AC-2018/00001");
        ecritureComptable.setLibelle("Libellé");
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(123)));

        DaoProxy daoProxy = Mockito.mock(DaoProxy.class);
        ComptabiliteDao comptabiliteDao = Mockito.mock(ComptabiliteDao.class);

        Mockito.when(daoProxy.getComptabiliteDao()).thenReturn(comptabiliteDao);
        Mockito.when(comptabiliteDao.getEcritureComptableByRef("AC-2018/00001")).thenReturn(ecritureComptable);

        AbstractBusinessManager.configure(null, daoProxy, null);
        comptabiliteManager.checkEcritureComptableContext(ecritureComptable);
    }

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
                null, new BigDecimal(123),
                null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(123)));

        EcritureComptable ecritureComptableDouble;
        ecritureComptableDouble = new EcritureComptable();
        ecritureComptableDouble.setId(2);
        ecritureComptableDouble.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptableDouble.setDate(new Date());
        ecritureComptableDouble.setReference("AC-2018/00001");
        ecritureComptableDouble.setLibelle("Libellé");
        ecritureComptableDouble.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        ecritureComptableDouble.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(123)));

        DaoProxy daoProxy = Mockito.mock(DaoProxy.class);
        ComptabiliteDao comptabiliteDao = Mockito.mock(ComptabiliteDao.class);

        Mockito.when(daoProxy.getComptabiliteDao()).thenReturn(comptabiliteDao);
        Mockito.when(comptabiliteDao.getEcritureComptableByRef("AC-2018/00001")).thenReturn(ecritureComptable);

        AbstractBusinessManager.configure(null, daoProxy, null);
        comptabiliteManager.checkEcritureComptableContext(ecritureComptableDouble);
    }

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
                null, new BigDecimal(123),
                null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(123)));

        EcritureComptable ecritureComptableDouble;
        ecritureComptableDouble = new EcritureComptable();
        ecritureComptableDouble.setId(2);
        ecritureComptableDouble.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptableDouble.setDate(new Date());
        ecritureComptableDouble.setReference("AC-2018/00002");
        ecritureComptableDouble.setLibelle("Libellé");
        ecritureComptableDouble.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        ecritureComptableDouble.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(123)));

        DaoProxy daoProxy = Mockito.mock(DaoProxy.class);
        ComptabiliteDao comptabiliteDao = Mockito.mock(ComptabiliteDao.class);

        Mockito.when(daoProxy.getComptabiliteDao()).thenReturn(comptabiliteDao);
        Mockito.when(comptabiliteDao.getEcritureComptableByRef("AC-2018/00001")).thenReturn(ecritureComptableDouble);

        AbstractBusinessManager.configure(null, daoProxy, null);
        comptabiliteManager.checkEcritureComptableContext(ecritureComptable);
    }

    @Test
    public void checkEcritureComptableContextRG6_EcritureComptableNotFound_throwFunctionalException() throws Exception {

        EcritureComptable ecritureComptable;
        ecritureComptable = new EcritureComptable();
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptable.setDate(new Date());
        ecritureComptable.setReference("AC-2018/00001");
        ecritureComptable.setLibelle("Libellé");
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(123)));

        DaoProxy daoProxy = Mockito.mock(DaoProxy.class);
        ComptabiliteDao comptabiliteDao = Mockito.mock(ComptabiliteDao.class);

        Mockito.when(daoProxy.getComptabiliteDao()).thenReturn(comptabiliteDao);
        Mockito.doThrow(NotFoundException.class).when(comptabiliteDao).getEcritureComptableByRef("AC-2018/00001");

        AbstractBusinessManager.configure(null, daoProxy, null);
        comptabiliteManager.checkEcritureComptableContext(ecritureComptable);
    }


    @Test
    public void addReference_NoReturn_EcritureComptable() throws NotFoundException {
        EcritureComptable ecritureComptable = new EcritureComptable();
        ecritureComptable.setDate(new Date());
        ecritureComptable.setId(21);
        ecritureComptable.setLibelle("Ecriture comptable test add reference");
        ecritureComptable.setJournal(new JournalComptable("VE","test"));
        ecritureComptable.getListLigneEcriture().add(
                new LigneEcritureComptable(
                        new CompteComptable(1),null,new BigDecimal(2239)
                        ,null
                ));
        ecritureComptable.getListLigneEcriture().add(
                new LigneEcritureComptable(
                        new CompteComptable(2),null,null
                        ,new BigDecimal(2239)
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

    @Test
    public void addReference_NoReturn_EcritureComptable_Throw_NotFoundException() throws NotFoundException {
        EcritureComptable ecritureComptable = new EcritureComptable();
        ecritureComptable.setDate(new Date());
        ecritureComptable.setId(21);
        ecritureComptable.setLibelle("Ecriture comptable test add reference");
        ecritureComptable.setJournal(new JournalComptable("VE","test"));
        ecritureComptable.getListLigneEcriture().add(
                new LigneEcritureComptable(
                        new CompteComptable(1),null,new BigDecimal(2239)
                        ,null
                ));
        ecritureComptable.getListLigneEcriture().add(
                new LigneEcritureComptable(
                        new CompteComptable(2),null,null
                        ,new BigDecimal(2239)
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
