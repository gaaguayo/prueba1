package es.uam.eps.ir.split;

import es.uam.eps.ir.split.ratingorder.RatingOrderIF;
import es.uam.eps.ir.split.ratingorder.ROTime;
import es.uam.eps.ir.split.ratingorder.RORandom;
import es.uam.eps.ir.split.baseset.BaseSetGeneratorIF;
import es.uam.eps.ir.split.baseset.BSUser;
import es.uam.eps.ir.core.context.ContinuousTimeContext;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import java.util.Collection;
import java.util.List;
import junit.framework.TestCase;

/**
 *
 * @author Pedro G. Campos
 */
public class RatingOrder_ImplicitDataTest extends TestCase {
    final ModelIF<Object, Object, ContinuousTimeContextIF> model;
    final int modelSize;
    final int preferencesSize;
    
    public RatingOrder_ImplicitDataTest(String testName) {
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
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testRatingOrder_time(){
        RatingOrderIF<Object,Object,ContinuousTimeContextIF> ratingOrder = new ROTime<Object,Object,ContinuousTimeContextIF>();
        List<PreferenceIF<Object,Object,ContinuousTimeContextIF>> orderedRatings = ratingOrder.getOrderedRatings(model);
        int expectedSize = preferencesSize;
        int size = orderedRatings.size();
        assertEquals(expectedSize, size);
        for (int i = 0; i < size-1; i++){
            long time = orderedRatings.get(i).getContext().getTimestamp();
            assertTrue(time >= (long)1000 && time <= (long)3000);
            
            long timeAfter = orderedRatings.get(i+1).getContext().getTimestamp();
            assertTrue(time <= timeAfter);
        }
    }

    public void testRatingOrder_Random(){
        RatingOrderIF<Object,Object,ContinuousTimeContextIF> ratingOrder = new RORandom<Object,Object,ContinuousTimeContextIF>();
        List<PreferenceIF<Object,Object,ContinuousTimeContextIF>> orderedRatings = ratingOrder.getOrderedRatings(model);
        int expectedSize = preferencesSize;
        int size = orderedRatings.size();
        assertEquals(expectedSize, size);
        for (int i = 0; i < size-1; i++){
            long time = orderedRatings.get(i).getContext().getTimestamp();
            assertTrue(time >= (long)1000 && time <= (long)3000);
        }
    }
    
    public void testUserCentricRatingOrder_Time(){
        BaseSetGeneratorIF<Object, Object, ContinuousTimeContextIF> base;
        base = new BSUser();
        Collection<ModelIF<Object, Object, ContinuousTimeContextIF>> baseSets = base.getBaseSets(model);
        int expected = model.getUsers().size();
        int result = baseSets.size();
        assertEquals(expected, result);

        for (ModelIF _baseSet:baseSets){
            RatingOrderIF<Object,Object,ContinuousTimeContextIF> ratingOrder = new ROTime<Object,Object,ContinuousTimeContextIF>();
            List<PreferenceIF<Object,Object,ContinuousTimeContextIF>> orderedRatings = ratingOrder.getOrderedRatings(_baseSet);
            int expectedSize = preferencesSize / model.getUsers().size();
            int size = orderedRatings.size();
            assertEquals(expectedSize, size);
            for (int i = 0; i < size-1; i++){
                long time = orderedRatings.get(i).getContext().getTimestamp();
                assertTrue(time >= (long)1000 && time <= (long)3000);

                long timeAfter = orderedRatings.get(i+1).getContext().getTimestamp();
                assertTrue(time <= timeAfter);
            }        
            
        }        
    }

    public void testUserCentricRatingOrder_Random(){
        BaseSetGeneratorIF<Object, Object, ContinuousTimeContextIF> base;
        base = new BSUser();
        Collection<ModelIF<Object, Object, ContinuousTimeContextIF>> baseSets = base.getBaseSets(model);
        int expected = model.getUsers().size();
        int result = baseSets.size();
        assertEquals(expected, result);

        for (ModelIF _baseSet:baseSets){
            RatingOrderIF<Object,Object,ContinuousTimeContextIF> ratingOrder = new RORandom<Object,Object,ContinuousTimeContextIF>();
            List<PreferenceIF<Object,Object,ContinuousTimeContextIF>> orderedRatings = ratingOrder.getOrderedRatings(_baseSet);
            int expectedSize = preferencesSize / model.getUsers().size();
            int size = orderedRatings.size();
            assertEquals(expectedSize, size);
            for (int i = 0; i < size-1; i++){
                long time = orderedRatings.get(i).getContext().getTimestamp();
                assertTrue(time >= (long)1000 && time <= (long)3000);
            }        
            
        }        
    }
}
