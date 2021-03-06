package com.dummy.myerp.model.bean.comptabilite;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class JournalComptableTest {

    /**
     * Test les getters et les setters de la classe JournalComptable
     */
    @Test
    public void validateSettersAndGetters() {
        final PojoClass JournalComptablePojo = PojoClassFactory.getPojoClass(JournalComptable.class);

        final Validator validator = ValidatorBuilder.create()
                .with(new SetterTester(), new GetterTester())
                .build();
        validator.validate(JournalComptablePojo);
    }


    /**
     * test de la fonction getByCode
     * vérifie que la fonction récupère le bon journal dans une liste par rapport à son journalCode
     */
    @Test
    public void getByCode_returnJournalComptable_listJournalComptableAndCode(){
        JournalComptable journalComptable1 = new JournalComptable();
        journalComptable1.setCode("21");
        journalComptable1.setLibelle("journal comptable 1");

        JournalComptable journalComptable2 = new JournalComptable("67","journal comptable 2");

        JournalComptable journalComptable3 = new JournalComptable();
        journalComptable3.setCode("29");
        journalComptable3.setLibelle("journal comptable 3");

        JournalComptable journalComptable4 = new JournalComptable();
        journalComptable4.setCode("3");
        journalComptable4.setLibelle("journal comptable 4");

        JournalComptable journalComptable5 = new JournalComptable();
        journalComptable5.setCode("51");
        journalComptable5.setLibelle("journal comptable 5");

        List<JournalComptable> list = new ArrayList<>();
        list.add(journalComptable3);
        list.add(journalComptable1);
        list.add(journalComptable5);
        list.add(journalComptable2);
        list.add(journalComptable4);

        Assert.assertEquals(journalComptable4,JournalComptable.getByCode(list,"3"));
    }
}
