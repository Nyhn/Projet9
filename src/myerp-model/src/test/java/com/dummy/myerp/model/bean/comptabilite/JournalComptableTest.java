package com.dummy.myerp.model.bean.comptabilite;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class JournalComptableTest {

    @Test
    public void getByCode_returnJournalComptable_listJournalComptableAndCode(){
        JournalComptable journalComptable1 = new JournalComptable();
        journalComptable1.setCode("21");
        journalComptable1.setLibelle("journal comptable 1");

        JournalComptable journalComptable2 = new JournalComptable();
        journalComptable2.setCode("67");
        journalComptable2.setLibelle("journal comptable 2");

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
