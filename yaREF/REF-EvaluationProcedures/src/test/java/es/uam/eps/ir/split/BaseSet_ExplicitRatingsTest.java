package es.uam.eps.ir.split;

import es.uam.eps.ir.split.baseset.BSCommunity;
import es.uam.eps.ir.split.baseset.BaseSetGeneratorIF;
import es.uam.eps.ir.split.baseset.BSUser;
import es.uam.eps.ir.core.context.ContinuousTimeContext;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import java.util.Collection;
import junit.framework.TestCase;

/**
 *
 * @author Pedro G. Campos
 */
public class BaseSet_ExplicitRatingsTest extends TestCase {
    final ModelIF<Object, Object, ContinuousTimeContextIF> model;
    final int modelSize;
    
    public BaseSet_ExplicitRatingsTest(String testName) {
        super(testName);
        model = new SimpleModel<Object, Object, ContinuousTimeContextIF>();
        model.addPreference(1, 1, (float)5, new ContinuousTimeContext((long)1000));
        model.addPreference(1, 3, (float)3, new ContinuousTimeContext((long)2000));
        model.addPreference(1, 5, (float)2, new ContinuousTimeContext((long)2500));
        model.addPreference(2, 3, (float)5, new ContinuousTimeContext((long)1500));
        model.addPreference(2, 4, (float)3, new ContinuousTimeContext((long)2000));
        model.addPreference(2, 5, (float)2, new ContinuousTimeContext((long)3000));
        modelSize = 6;
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    // TODO add test methods here. The name must begin with 'test'. For example:
    // public void testHello() {}
    
    public void testBaseSet_Community(){
        BaseSetGeneratorIF<Object, Object, ContinuousTimeContextIF> base;
        base = new BSCommunity();
        Collection baseSet = base.getBaseSets(model);
        int expected = 1;
        int result = baseSet.size();
        assertEquals(expected, result);
    }

    public void testBaseSet_User(){
        BaseSetGeneratorIF<Object, Object, ContinuousTimeContextIF> base;
        base = new BSUser();
        Collection baseSet = base.getBaseSets(model);
        int expected = 2;
        int result = baseSet.size();
        assertEquals(expected, result);
    }
}
