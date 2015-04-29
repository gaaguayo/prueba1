package es.uam.eps.ir.split;

import es.uam.eps.ir.core.context.ContinuousTimeContext;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import es.uam.eps.ir.split.baseset.BSCommunity;
import es.uam.eps.ir.split.baseset.BSUser;
import es.uam.eps.ir.split.baseset.BaseSetGeneratorIF;
import es.uam.eps.ir.split.impl.DatasetSplitter_Holdout;
import es.uam.eps.ir.split.ratingorder.ROTime;
import es.uam.eps.ir.split.ratingorder.RatingOrderIF;
import es.uam.eps.ir.split.sizecondition.SCFixed;
import es.uam.eps.ir.split.sizecondition.SizeConditionIF;
import junit.framework.TestCase;

/**
 *
 * @author Pedro G. Campos
 */
public class DatasetSplitter_ExplicitRatingsTest extends TestCase {
    final ModelIF<Object, Object, ContinuousTimeContextIF> model;
    final int modelSize;
    
    public DatasetSplitter_ExplicitRatingsTest(String testName) {
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
    
    public void testHoldoutUserCentricTimeDependentFixedSize(){
        DatasetSplitterIF<Object, Object, ContinuousTimeContextIF> splitter;
        BaseSetGeneratorIF<Object, Object, ContinuousTimeContextIF> base = new BSUser();
        RatingOrderIF<Object,Object,ContinuousTimeContextIF> ratingOrder = new ROTime<Object,Object,ContinuousTimeContextIF>();
        int size = 1;
        SizeConditionIF<Object,Object,ContinuousTimeContextIF> sizeCondition = new SCFixed(size);
        
        splitter = new DatasetSplitter_Holdout(base, ratingOrder, sizeCondition);
        
        SplitIF<Object, Object, ContinuousTimeContextIF>[] splits = splitter.split(model);
        int expectedSplits = 1;
        int actualSplits= splits.length;
        assertEquals(expectedSplits, actualSplits);
        
        SplitIF<Object, Object, ContinuousTimeContextIF> split = splits[0];
        ModelIF<Object, Object, ContinuousTimeContextIF> trainingSet = split.getTrainingSet();
        ModelIF<Object, Object, ContinuousTimeContextIF> testingSet = split.getTestingSet();
                
        assertEquals(trainingSet.getUsers() , testingSet.getUsers());
        for (Object user : trainingSet.getUsers()){
            for (PreferenceIF<Object, Object, ContinuousTimeContextIF> pref : trainingSet.getPreferencesFromUser(user)){
                assertFalse(pref.getItem() == (Object)5);
                assertTrue(pref.getContext().getTimestamp() < 2500);
            }
            for (PreferenceIF<Object, Object, ContinuousTimeContextIF> pref : testingSet.getPreferencesFromUser(user)){
                assertTrue(pref.getItem() == (Object)5);
                assertTrue(pref.getContext().getTimestamp() >= 2500);
            }
        }
    }

    public void testHoldoutCommunityCentricTimeDependentFixedSize(){
        DatasetSplitterIF<Object, Object, ContinuousTimeContextIF> splitter;
        BaseSetGeneratorIF<Object, Object, ContinuousTimeContextIF> base = new BSCommunity();
        RatingOrderIF<Object,Object,ContinuousTimeContextIF> ratingOrder = new ROTime<Object,Object,ContinuousTimeContextIF>();
        int size = 1;
        SizeConditionIF<Object,Object,ContinuousTimeContextIF> sizeCondition = new SCFixed(size);
        
        splitter = new DatasetSplitter_Holdout(base, ratingOrder, sizeCondition);
        
        SplitIF<Object, Object, ContinuousTimeContextIF>[] splits = splitter.split(model);
        int expectedSplits = 1;
        int actualSplits= splits.length;
        assertEquals(expectedSplits, actualSplits);
        
        SplitIF<Object, Object, ContinuousTimeContextIF> split = splits[0];
        ModelIF<Object, Object, ContinuousTimeContextIF> trainingSet = split.getTrainingSet();
        ModelIF<Object, Object, ContinuousTimeContextIF> testingSet = split.getTestingSet();
                
        assertNotSame(trainingSet.getUsers() , testingSet.getUsers());
        for (Object user : trainingSet.getUsers()){
            for (PreferenceIF<Object, Object, ContinuousTimeContextIF> pref : trainingSet.getPreferencesFromUser(user)){
                assertTrue(pref.getContext().getTimestamp() < 3000);
            }
        }
        for (PreferenceIF<Object, Object, ContinuousTimeContextIF> pref : testingSet.getPreferencesFromUser(2)){
            assertTrue(pref.getItem() == (Object)5);
            assertTrue(pref.getContext().getTimestamp() == 3000);
        }
        
    }
}
