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

public class CompteComptableTest {

    @Test
    public void validateSettersAndGetters() {
        final PojoClass compteComptablePojo = PojoClassFactory.getPojoClass(CompteComptable.class);

        final Validator validator = ValidatorBuilder.create()
                .with(new SetterTester(), new GetterTester())
                .build();
        validator.validate(compteComptablePojo);
    }

    @Test
    public void getByNumero_returnCompteComptable_listCompteComptableAndNumero(){
        CompteComptable compteComptable1 = new CompteComptable();
        compteComptable1.setLibelle("libelleNumero1");
        compteComptable1.setNumero(1);

        CompteComptable compteComptable2 = new CompteComptable(2,"libelleNumero2");

        CompteComptable compteComptable3 = new CompteComptable();
        compteComptable3.setLibelle("libelleNumero3");
        compteComptable3.setNumero(3);

        CompteComptable compteComptable4 = new CompteComptable();
        compteComptable4.setLibelle("libelleNumero4");
        compteComptable4.setNumero(4);

        List<CompteComptable> list= new ArrayList<>();
        list.add(compteComptable1);
        list.add(compteComptable3);
        list.add(compteComptable2);
        list.add(compteComptable4);
        Assert.assertEquals(compteComptable3,CompteComptable.getByNumero(list,3));


    }
}
