package com.dummy.myerp.model.bean.comptabilite;

import java.math.BigDecimal;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.Assert;
import org.junit.Test;


public class EcritureComptableTest {

    @Test
    public void validateSettersAndGetters() {
        final PojoClass EcritureComptablePojo = PojoClassFactory.getPojoClass(EcritureComptable.class);

        final Validator validator = ValidatorBuilder.create()
                .with(new SetterTester(), new GetterTester())
                .build();
        validator.validate(EcritureComptablePojo);
    }

    private LigneEcritureComptable createLigne(Integer pCompteComptableNumero, String pDebit, String pCredit) {
        BigDecimal vDebit = pDebit == null ? null : new BigDecimal(pDebit);
        BigDecimal vCredit = pCredit == null ? null : new BigDecimal(pCredit);
        String vLibelle = ObjectUtils.defaultIfNull(vDebit, BigDecimal.ZERO)
                                     .subtract(ObjectUtils.defaultIfNull(vCredit, BigDecimal.ZERO)).toPlainString();
        LigneEcritureComptable vRetour = new LigneEcritureComptable(new CompteComptable(pCompteComptableNumero),
                                                                    vLibelle,
                                                                    vDebit, vCredit);
        return vRetour;
    }

    @Test
    public void isEquilibree() {
        EcritureComptable vEcriture;
        vEcriture = new EcritureComptable();

        vEcriture.setLibelle("Equilibrée");
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "200.50", null));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "100.50", "33"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "301"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "40", "7"));
        Assert.assertTrue(vEcriture.toString(), vEcriture.isEquilibree());
    }
    @Test
    public void isNotEquilibree() {
        EcritureComptable vEcriture;
        vEcriture = new EcritureComptable();
        vEcriture.getListLigneEcriture().clear();
        vEcriture.setLibelle("Non équilibrée");
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "10", null));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "20", "1"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "30"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "1", "2"));
        Assert.assertFalse(vEcriture.toString(), vEcriture.isEquilibree());
    }

    @Test
    public void getTotalDebit_returnBigDecimal_EcritureComptable(){
        EcritureComptable vEcriture;
        vEcriture = new EcritureComptable();
        vEcriture.getListLigneEcriture().clear();
        vEcriture.setLibelle("check total debit");
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "10", null));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "20", "1"));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "300", null));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "150", "1"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "30"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "1.40", "2"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "179"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "1.0", "271"));
        BigDecimal result = BigDecimal.valueOf(482.4);

        Assert.assertTrue(vEcriture.getTotalDebit().compareTo(result) == 0);
    }

    @Test
    public void getTotalCredit_returnBigDecimal_EcritureComptable(){
        EcritureComptable vEcriture;
        vEcriture = new EcritureComptable();
        vEcriture.getListLigneEcriture().clear();
        vEcriture.setLibelle("check total debit");
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "10", null));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "20", "10.20"));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "300", null));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "150", "21"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "302.9"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "1.40", "2"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "179"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "1.0", "271"));
        BigDecimal result = BigDecimal.valueOf(786.1);

        Assert.assertTrue(vEcriture.getTotalCredit().compareTo(result) == 0);
    }

}
