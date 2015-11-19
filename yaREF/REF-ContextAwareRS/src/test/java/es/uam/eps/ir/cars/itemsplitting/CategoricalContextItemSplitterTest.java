/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package es.uam.eps.ir.cars.itemsplitting;

import es.uam.eps.ir.core.context.CategoricalContext;
import es.uam.eps.ir.core.context.ContextContainer;
import es.uam.eps.ir.core.context.ContextDefinition;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import es.uam.eps.ir.core.model.impl.ExplicitPreference;
import es.uam.eps.ir.core.model.impl.GenericExplicitModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;

/**
 *
 * @author pedro
 */
public class CategoricalContextItemSplitterTest extends TestCase {
    final ModelIF<Object, Object, ContextIF> model;
    final ContextDefinition ctxDef;
    
    public CategoricalContextItemSplitterTest(String testName) {
        super(testName);
        ctxDef = new ContextDefinition("ContextVariable");
        ctxDef.addValue("ctx1");
        ctxDef.addValue("ctx2");
        ctxDef.addValue("ctx3");
        ContextContainer ctnPref;
        model = new GenericExplicitModel<Object, Object, ContextIF>(); // equal number of preferences per user is required, for automated tests
        // pref 1: user 1, item 1, rating 1, context 1
        ctnPref = new ContextContainer();
        ctnPref.add(new CategoricalContext(ctxDef,"ctx1"));
        model.addPreference(1, 1, (float)1, ctnPref);

        // pref 2: user 1, item 2, rating 1, context 2
        ctnPref = new ContextContainer();
        ctnPref.add(new CategoricalContext(ctxDef,"ctx2"));
        model.addPreference(1, 2, (float)1, ctnPref);

        // pref 3: user 1, item 3, rating 1, context 3
        ctnPref = new ContextContainer();
        ctnPref.add(new CategoricalContext(ctxDef,"ctx3"));
        model.addPreference(1, 3, (float)1, ctnPref);

        // pref 4: user 1, item 4, rating 1, context 3
        ctnPref = new ContextContainer();
        ctnPref.add(new CategoricalContext(ctxDef,"ctx3"));
        model.addPreference(1, 4, (float)1, ctnPref);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

//    /**
//     * Test of getSplitItemID method, of class CategoricalContextItemSplitter.
//     */
//    public void testGetSplitItemID() {
//        System.out.println("getSplitItemID");
//        Object user = null;
//        Object item = null;
//        Object context = null;
//        CategoricalContextItemSplitter instance = new CategoricalContextItemSplitter();
//        Object expResult = null;
//        Object result = instance.getSplitItemID(user, item, context);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//    /**
//     * Test of splitModel method, of class CategoricalContextItemSplitter.
//     */
//    public void testSplitModel() {
//        System.out.println("splitModel");
//        ModelIF model = null;
//        CategoricalContextItemSplitter instance = new CategoricalContextItemSplitter();
//        ModelIF expResult = null;
//        ModelIF result = instance.splitModel(model);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of getContextSplits method, of class CategoricalContextItemSplitter.
     */
    public void testGetContextSplits() {
        Collection<? extends PreferenceIF<Object, Object, ContextIF>> preferences = model.getPreferencesFromUser(1);
        CategoricalContextItemSplitter instance = new CategoricalContextItemSplitter();
        Map<String, Collection<PreferenceIF<Object, Object, ContextIF>>> expResult = new HashMap<String, Collection<PreferenceIF<Object, Object, ContextIF>>>();
        Collection<PreferenceIF<Object, Object, ContextIF>> prefs = new ArrayList();
        ContextContainer ctnPref;
        
        prefs = new ArrayList();
        ctnPref = new ContextContainer();        
        ctnPref.add(new CategoricalContext(ctxDef,"ctx1"));
        prefs.add(new ExplicitPreference(1, 1, ctnPref, (float)1));
        expResult.put("ctx1", prefs);
        
        prefs = new ArrayList();
        ctnPref = new ContextContainer();        
        ctnPref.add(new CategoricalContext(ctxDef,"ctx2"));
        prefs.add(new ExplicitPreference(1, 2, ctnPref, (float)1));
        expResult.put("ctx2", prefs);

        prefs = new ArrayList();
        ctnPref = new ContextContainer();        
        ctnPref.add(new CategoricalContext(ctxDef,"ctx3"));
        prefs.add(new ExplicitPreference(1, 3, ctnPref, (float)1));
        prefs.add(new ExplicitPreference(1, 4, ctnPref, (float)1));
        expResult.put("ctx3", prefs);
        
        Map<String, Collection<PreferenceIF<Object, Object, ContextIF>>> result = instance.getContextSplits(preferences, ctxDef);
        for (String key: result.keySet()){
            Collection<PreferenceIF<Object, Object, ContextIF>> prefs1=result.get(key);
            Collection<PreferenceIF<Object, Object, ContextIF>> prefs2=expResult.get(key);
            assertEquals(prefs1.size(), prefs2.size());
            assertTrue(prefs1.containsAll(prefs2));
        }        
    }
    
}
