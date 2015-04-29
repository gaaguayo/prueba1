package es.uam.eps.ir.split;

import es.uam.eps.ir.split.sizecondition.SCFixed;
import es.uam.eps.ir.split.sizecondition.SCTimeProportion;
import es.uam.eps.ir.split.sizecondition.SCProportion;
import es.uam.eps.ir.split.sizecondition.SizeConditionIF;
import es.uam.eps.ir.core.context.ContinuousTimeContext;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import junit.framework.TestCase;

/**
 *
 * @author Pedro G. Campos
 */
public class SizeCondition_ExplicitRatingsTest extends TestCase {
    ModelIF<Object, Object, ContinuousTimeContextIF> model;
    int modelSize;
    
    public SizeCondition_ExplicitRatingsTest(String testName) {
        super(testName);
        model = new SimpleModel<Object, Object, ContinuousTimeContextIF>();
        model.addPreference(1, 1, (float)5, new ContinuousTimeContext((long)1000));
        model.addPreference(1, 3, (float)3, new ContinuousTimeContext((long)2000));
        model.addPreference(1, 5, (float)2, new ContinuousTimeContext((long)2500));
        model.addPreference(2, 3, (float)5, new ContinuousTimeContext((long)2000));
        model.addPreference(2, 4, (float)3, new ContinuousTimeContext((long)1500));
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
    public void testSizeCondition_Fixed(){
        int size = 1;
        int expectedSize = modelSize - size;
        SizeConditionIF<Object,Object,ContinuousTimeContextIF> sizeCondition;
        sizeCondition = new SCFixed(size);
        int actualSize = sizeCondition.getNumberOfRatingsForTraining(model);
        assertEquals(expectedSize, actualSize);
    }

    public void testSizeCondition_Proportion(){
        float proportion = (float)0.3;
        SizeConditionIF<Object,Object,ContinuousTimeContextIF> sizeCondition;
        sizeCondition = new SCProportion(proportion);
        int expectedSize = (int) (modelSize - Math.round(modelSize * proportion));
        int actualSize = sizeCondition.getNumberOfRatingsForTraining(model);
        assertEquals(expectedSize, actualSize);
    }

    public void testSizeCondition_TimeProportion(){
        float proportion = (float)0.3;
        SizeConditionIF<Object,Object,ContinuousTimeContextIF> sizeCondition;
        sizeCondition = new SCTimeProportion(proportion);
        int expectedSize = 4; // test starting time = 2400 
        int actualSize = sizeCondition.getNumberOfRatingsForTraining(model);
        assertEquals(expectedSize, actualSize);
    }
}
