
import es.uam.eps.ir.core.context.ContinuousTimeContext;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.core.util.SimpleModel;
import java.util.Collection;
import java.util.Date;
import java.util.TreeSet;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import junit.framework.TestCase;

/*
package es.uam.eps.ir.core.util;

import java.util.Collection;
import java.util.Date;
import junit.framework.TestCase;

/**
 *
 * @author pedro
 */
public class ContextualModelUtilsTest extends TestCase {
    final ModelIF<Object, Object, ContinuousTimeContextIF> model;
    final int modelSize;
    final int preferencesSize;
    
    public ContextualModelUtilsTest(String testName) {
        super(testName);
        model = new SimpleModel<Object, Object, ContinuousTimeContextIF>(); // equal number of preferences per user is required, for automated tests
        model.addPreference(1, 1, (float)1, new ContinuousTimeContext((long)1000));
        model.addPreference(1, 1, (float)1, new ContinuousTimeContext((long)2000));
        model.addPreference(1, 5, (float)1, new ContinuousTimeContext((long)2500));
        model.addPreference(1, 5, (float)1, new ContinuousTimeContext((long)2800));
        model.addPreference(2, 3, (float)1, new ContinuousTimeContext((long)1500));
        model.addPreference(2, 3, (float)1, new ContinuousTimeContext((long)2000));
        model.addPreference(2, 5, (float)1, new ContinuousTimeContext((long)2600));
        model.addPreference(2, 5, (float)1, new ContinuousTimeContext((long)3000));
        modelSize = 4;
        preferencesSize = 8;
 }
    /**
     * Test of getExampleContext method, of class ContextualModelUtils.
     */
    public void testGetExampleContext() {
        ContextualModelUtils instance = new ContextualModelUtils(model);
        Object expResult = new ContinuousTimeContext((long)0000);
        Object result = instance.getExampleContext();
        assertEquals(expResult.getClass(), result.getClass());
    }

    /**
     * Test of getItemsRatedBy method, of class ContextualModelUtils.
     */
    public void testGetItemsRatedBy() {
        ContextualModelUtils instance = new ContextualModelUtils(model);
        Collection expResult1 = new TreeSet();
        expResult1.add(1);
        expResult1.add(5);
        Collection result1 = instance.getItemsRatedBy(1);
        assertEquals(expResult1, result1);
        Collection expResult2 = new TreeSet();
        expResult2.add(3);
        expResult2.add(5);
        Collection result2 = instance.getItemsRatedBy(2);
        assertEquals(expResult2, result2);
    }

}
