package jp.co.canon.cks.eec.fs.rssportal.vftp.controller.checker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ch.qos.logback.core.Context;

public class ContextCheckerTest {
    @Test
    public void test_contexttest_001(){

        ContextChecker.Context context = new ContextChecker.Context("DE");
        String name = context.getName();
        Assertions.assertEquals("DE", name);

        context.add("AAA");
        String val = context.get(0);
        Assertions.assertEquals("AAA", val);

        val = context.get(1);
        Assertions.assertNull(val);
    }

    @Test
    public void test_contextList_001(){
        ContextChecker.ContextList ctxList = ContextChecker.ContextList.parseContextString(null);

        Assertions.assertNull(ctxList);

        ctxList = ContextChecker.ContextList.parseContextString("DE_AAA");
        Assertions.assertNotNull(ctxList);

        ContextChecker.Context[] ctxs = ctxList.getContextByName("DE");
        Assertions.assertEquals(1, ctxs.length);

        ctxList = ContextChecker.ContextList.parseContextString("DE_AAAAAAAAA");
        Assertions.assertNull(ctxList);

        ctxList = ContextChecker.ContextList.parseContextString("PR");
        Assertions.assertNull(ctxList);

        ctxList = ContextChecker.ContextList.parseContextString("PR_CCC");
        Assertions.assertNotNull(ctxList);

        ctxs = ctxList.getContextByName("DE");
        Assertions.assertEquals(0, ctxs.length);

        ctxList = ContextChecker.ContextList.parseContextString("PR_AAAAAAAAA");
        Assertions.assertNull(ctxList);

        ctxList = ContextChecker.ContextList.parseContextString("LO");
        Assertions.assertNull(ctxList);

        ctxList = ContextChecker.ContextList.parseContextString("LO_AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        Assertions.assertNull(ctxList);

        ctxList = ContextChecker.ContextList.parseContextString("LO_ABC");
        Assertions.assertNotNull(ctxList);

        ctxList = ContextChecker.ContextList.parseContextString("P_1");
        Assertions.assertNull(ctxList);

        ctxList = ContextChecker.ContextList.parseContextString("P_1_AAA");
        Assertions.assertNull(ctxList);

        ctxList = ContextChecker.ContextList.parseContextString("P_1_10");
        Assertions.assertNotNull(ctxList);

        ctxList = ContextChecker.ContextList.parseContextString("S_1");
        Assertions.assertNull(ctxList);

        ctxList = ContextChecker.ContextList.parseContextString("S_1_AAA");
        Assertions.assertNull(ctxList);

        ctxList = ContextChecker.ContextList.parseContextString("S_1_10");
        Assertions.assertNotNull(ctxList);

        ctxList = ContextChecker.ContextList.parseContextString("M_1");
        Assertions.assertNull(ctxList);

        ctxList = ContextChecker.ContextList.parseContextString("M_1_AAA");
        Assertions.assertNull(ctxList);

        ctxList = ContextChecker.ContextList.parseContextString("M_1_100");
        Assertions.assertNotNull(ctxList);

        ctxList = ContextChecker.ContextList.parseContextString("G");
        Assertions.assertNull(ctxList);

        ctxList = ContextChecker.ContextList.parseContextString("G_AAAAAAAAAAAAAAAAAA");
        Assertions.assertNull(ctxList);

        ctxList = ContextChecker.ContextList.parseContextString("G_AAAA");
        Assertions.assertNotNull(ctxList);

        ctxList = ContextChecker.ContextList.parseContextString("O");
        Assertions.assertNull(ctxList);

        ctxList = ContextChecker.ContextList.parseContextString("DE");
        Assertions.assertNull(ctxList);
    }

    @Test
    public void test_contextChecker_001(){
        ContextChecker checker = null;

        checker = ContextChecker.fromString(null);
        Assertions.assertNull(checker);

        checker = ContextChecker.fromString("DE_AAA_DE_BBB");
        Assertions.assertNull(checker);

        checker = ContextChecker.fromString("DE_AAA");
        Assertions.assertNotNull(checker);
        String name = checker.getDeviceName();
        Assertions.assertEquals("AAA", name);        
    }

    @Test
    public void test_contextChecker_002(){
        boolean r;

        r = ContextChecker.isValidContextString(null);
        Assertions.assertTrue(r);

        r = ContextChecker.isValidContextString("O");
        Assertions.assertFalse(r);

        r = ContextChecker.isValidContextString("DE_ABC_DE_DEF");
        Assertions.assertFalse(r);

        r = ContextChecker.isValidContextString("DE_ABC");
        Assertions.assertTrue(r);        
    }

    @Test
    public void test_contextChecker_003(){
        String name = null;

        name = ContextChecker.getDeviceNameFromContextString(null);
        Assertions.assertNull(name);

        name = ContextChecker.getDeviceNameFromContextString("O");
        Assertions.assertNull(name);

        name = ContextChecker.getDeviceNameFromContextString("DE_ABC_DE_DEF");
        Assertions.assertNull(name);

        name = ContextChecker.getDeviceNameFromContextString("PR_ABC");
        Assertions.assertNull(name);

        name = ContextChecker.getDeviceNameFromContextString("DE_ABC");
        Assertions.assertEquals("ABC", name);
    }
}