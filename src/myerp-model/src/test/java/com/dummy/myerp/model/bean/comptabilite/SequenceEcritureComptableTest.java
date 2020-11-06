package com.dummy.myerp.model.bean.comptabilite;


import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import org.junit.Assert;
import org.junit.Test;

public class SequenceEcritureComptableTest {
    /**
     * Test les getters et les setters de la classe SequenceEcritureComptable
     */
    @Test
    public void validateSettersAndGetters() {
        final PojoClass SequenceEcritureComptablePojo = PojoClassFactory.getPojoClass(SequenceEcritureComptable.class);

        final Validator validator = ValidatorBuilder.create()
                .with(new SetterTester(), new GetterTester())
                .build();
        validator.validate(SequenceEcritureComptablePojo);
    }

    /**
     * Test le constructeur de la classe SequenceEcritureComptable
     */
    @Test
    public void validateConstrutor() {
        SequenceEcritureComptable sequenceEcritureComptable = new SequenceEcritureComptable("VE", 2020, 13);

        Assert.assertEquals(sequenceEcritureComptable.toString(), sequenceEcritureComptable.getAnnee(), Integer.valueOf(2020));
        Assert.assertEquals(sequenceEcritureComptable.toString(), sequenceEcritureComptable.getDerniereValeur(), Integer.valueOf(13));
        Assert.assertNotEquals(sequenceEcritureComptable.toString(), sequenceEcritureComptable.getJournalCode(), "AC");
    }
}