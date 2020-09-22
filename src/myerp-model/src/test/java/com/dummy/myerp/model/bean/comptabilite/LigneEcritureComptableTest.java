package com.dummy.myerp.model.bean.comptabilite;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import org.junit.Test;

public class LigneEcritureComptableTest{
    @Test
    public void validateSettersAndGetters() {
        final PojoClass LigneEcritureComptablePojo = PojoClassFactory.getPojoClass(LigneEcritureComptable.class);

        final Validator validator = ValidatorBuilder.create()
                .with(new SetterTester(), new GetterTester())
                .build();
        validator.validate(LigneEcritureComptablePojo);
    }

}