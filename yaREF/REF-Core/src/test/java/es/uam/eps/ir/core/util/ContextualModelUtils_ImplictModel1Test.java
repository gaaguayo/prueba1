package es.uam.eps.ir.core.util;


import es.uam.eps.ir.core.context.ContinuousTimeContext;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.impl.GenericImplicitModel;
import java.util.Collection;
import java.util.Date;
import java.util.TreeSet;
import static junit.framework.Assert.assertEquals;
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
public class ContextualModelUtils_ImplictModel1Test extends TestCase {
    final ModelIF<Object, Object, ContinuousTimeContextIF> model;
    
    public ContextualModelUtils_ImplictModel1Test(String testName) {
        super(testName);
        model = new GenericImplicitModel<Object, Object, ContinuousTimeContextIF>(); // equal number of preferences per user is required, for automated tests
        model.addPreference(1, 1, (float)1, new ContinuousTimeContext((long)100000000));
        model.addPreference(1, 1, (float)1, new ContinuousTimeContext((long)200000000));
        model.addPreference(1, 5, (float)1, new ContinuousTimeContext((long)250000000));
        model.addPreference(1, 5, (float)1, new ContinuousTimeContext((long)280000000));
        model.addPreference(2, 3, (float)1, new ContinuousTimeContext((long)150000000));
        model.addPreference(2, 3, (float)1, new ContinuousTimeContext((long)200000000));
        model.addPreference(2, 5, (float)1, new ContinuousTimeContext((long)260000000));
        model.addPreference(2, 5, (float)1, new ContinuousTimeContext((long)300000000));
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
    public void testGetItemsRatedBy1() {
        ContextualModelUtils instance = new ContextualModelUtils(model);
        Collection expResult1 = new TreeSet();
        expResult1.add(1);
        expResult1.add(5);
        Collection result1 = instance.getItemsRatedBy(1);
        assertEquals(expResult1, result1);
    }
    public void testGetItemsRatedBy2() {
        ContextualModelUtils instance = new ContextualModelUtils(model);
        Collection expResult2 = new TreeSet();
        expResult2.add(3);
        expResult2.add(5);
        Collection result2 = instance.getItemsRatedBy(2);
        assertEquals(expResult2, result2);
    }

    /**
     * Test of getUsersWhomRate method, of class ContextualModelUtils.
     */
    public void testGetUsersWhomRate1() {
        ContextualModelUtils instance = new ContextualModelUtils(model);
        Collection expResult1 = new TreeSet();
        expResult1.add(1);
        Collection result1 = instance.getUsersWhomRate(1);
        assertEquals(expResult1, result1);
    }
    public void testGetUsersWhomRate2() {
        ContextualModelUtils instance = new ContextualModelUtils(model);
        Collection expResult2 = new TreeSet();
        expResult2.add(1);
        expResult2.add(2);
        Collection result2 = instance.getUsersWhomRate(5);
        assertEquals(expResult2, result2);
    }

    /**
     * Test of getItemMeanRating method, of class ContextualModelUtils.
     */
    public void testGetItemMeanRating1() {
        ContextualModelUtils instance = new ContextualModelUtils(model);
        Float expResult = (float)2.0;
        Float result = instance.getItemMeanRating(1);
        assertEquals(expResult, result);
    }
    public void testGetItemMeanRating2() {
        ContextualModelUtils instance = new ContextualModelUtils(model);
        Float expResult = (float)2.0;
        Float result = instance.getItemMeanRating(5);
        assertEquals(expResult, result);
    }

    /**
     * Test of getUserMeanRating method, of class ContextualModelUtils.
     */
    public void testGetUserMeanRating1() {
        ContextualModelUtils instance = new ContextualModelUtils(model);
        Float expResult =(float)2.0;
        Float result = instance.getUserMeanRating(1);
        assertEquals(expResult, result);
    }
    public void testGetUserMeanRating2() {
        ContextualModelUtils instance = new ContextualModelUtils(model);
        Float expResult =(float)2.0;
        Float result = instance.getUserMeanRating(2);
        assertEquals(expResult, result);
    }

    /**
     * Test of getMeanRating method, of class ContextualModelUtils.
     */
    public void testGetMeanRating() {
        ContextualModelUtils instance = new ContextualModelUtils(model);
        Float expResult = (float)2.0;
        Float result = instance.getMeanRating();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMinRating method, of class ContextualModelUtils.
     */
    public void testGetMinRating_0args() {
        ContextualModelUtils instance = new ContextualModelUtils(model);
        Float expResult = (float)2.0;
        Float result = instance.getMinRating();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMaxRating method, of class ContextualModelUtils.
     */
    public void testGetMaxRating_0args() {
        ContextualModelUtils instance = new ContextualModelUtils(model);
        Float expResult = (float)2.0;
        Float result = instance.getMaxRating();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMinDate method, of class ContextualModelUtils.
     */
    public void testGetMinDate_0args() {
        ContextualModelUtils instance = new ContextualModelUtils(model);
        Date expResult = new Date((long)100000000);
        Date result = instance.getMinDate();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMaxDate method, of class ContextualModelUtils.
     */
    public void testGetMaxDate_0args() {
        ContextualModelUtils instance = new ContextualModelUtils(model);
        Date expResult = new Date((long)300000000);
        Date result = instance.getMaxDate();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMeanUserDate method, of class ContextualModelUtils.
     */
    public void testGetMeanUserDate1() {
        ContextualModelUtils instance = new ContextualModelUtils(model);
        Date expResult = new Date((long)190000000);
        Date result = instance.getMeanUserDate(1);
        assertEquals(expResult, result);
    }
    public void testGetMeanUserDate2() {
        ContextualModelUtils instance = new ContextualModelUtils(model);
        Date expResult = new Date((long)225000000);
        Date result = instance.getMeanUserDate(2);
        assertEquals(expResult, result);
    }

    /**
     * Test of getMinRating method, of class ContextualModelUtils.
     */
    public void testGetMinRating_GenericType1() {
        ContextualModelUtils instance = new ContextualModelUtils(model);
        Float expResult = (float)2.0;
        Float result = instance.getMinRating(1);
        assertEquals(expResult, result);
    }
    public void testGetMinRating_GenericType2() {
        ContextualModelUtils instance = new ContextualModelUtils(model);
        Float expResult = (float)2.0;
        Float result = instance.getMinRating(2);
        assertEquals(expResult, result);
    }

    /**
     * Test of getMaxRating method, of class ContextualModelUtils.
     */
    public void testGetMaxRating_GenericType() {
        ContextualModelUtils instance = new ContextualModelUtils(model);
        Float expResult = (float)2.0;
        Float result = instance.getMaxRating(1);
        assertEquals(expResult, result);
    }

    /**
     * Test of getMinDate method, of class ContextualModelUtils.
     */
    public void testGetMinDate_GenericType() {
        ContextualModelUtils instance = new ContextualModelUtils(model);
        Date expResult = new Date((long)150000000);
        Date result = instance.getMinDate(2);
        assertEquals(expResult, result);
    }

    /**
     * Test of getMaxDate method, of class ContextualModelUtils.
     */
    public void testGetMaxDate_GenericType() {
        ContextualModelUtils instance = new ContextualModelUtils(model);
        Date expResult = new Date((long)280000000);
        Date result = instance.getMaxDate(1);
        assertEquals(expResult, result);
    }

    /**
     * Test of getFeedbackRecordsCount method, of class ContextualModelUtils.
     */
    public void testGetFeedbackRecordsCount() {
        ContextualModelUtils instance = new ContextualModelUtils(model);
        int expResult = 8;
        int result = instance.getFeedbackRecordsCount();
        assertEquals(expResult, result);
    }

    /**
     * Test of getRatingCount method, of class ContextualModelUtils.
     */
    public void testGetRatingCount() {
        ContextualModelUtils instance = new ContextualModelUtils(model);
        int expResult = 4;
        int result = instance.getRatingCount();
        assertEquals(expResult, result);
    }

    /**
     * Test of getUserFeedbackRecordsCount method, of class ContextualModelUtils.
     */
    public void testGetUserFeedbackRecordsCount1() {
        ContextualModelUtils instance = new ContextualModelUtils(model);
        int expResult = 4;
        int result = instance.getUserFeedbackRecordsCount(1);
        assertEquals(expResult, result);
    }
    public void testGetUserFeedbackRecordsCount2() {
        ContextualModelUtils instance = new ContextualModelUtils(model);
        int expResult = 4;
        int result = instance.getUserFeedbackRecordsCount(2);
        assertEquals(expResult, result);
    }

    /**
     * Test of getUserRatingCount method, of class ContextualModelUtils.
     */
    public void testGetUserRatingCount1() {
        ContextualModelUtils instance = new ContextualModelUtils(model);
        int expResult = 2;
        int result = instance.getUserRatingCount(1);
        assertEquals(expResult, result);
    }
    public void testGetUserRatingCount2() {
        ContextualModelUtils instance = new ContextualModelUtils(model);
        int expResult = 2;
        int result = instance.getUserRatingCount(2);
        assertEquals(expResult, result);
    }

    /**
     * Test of getItemFeedbackRecordsCount method, of class ContextualModelUtils.
     */
    public void testGetItemFeedbackRecordsCount1() {
        ContextualModelUtils instance = new ContextualModelUtils(model);
        int expResult = 2;
        int result = instance.getItemFeedbackRecordsCount(3);
        assertEquals(expResult, result);
    }
    public void testGetItemFeedbackRecordsCount2() {
        ContextualModelUtils instance = new ContextualModelUtils(model);
        int expResult = 4;
        int result = instance.getItemFeedbackRecordsCount(5);
        assertEquals(expResult, result);
    }

    /**
     * Test of getItemRatingCount method, of class ContextualModelUtils.
     */
    public void testGetItemRatingCount1() {
        ContextualModelUtils instance = new ContextualModelUtils(model);
        int expResult = 1;
        int result = instance.getItemRatingCount(3);
        assertEquals(expResult, result);
    }
    public void testGetItemRatingCount2() {
        ContextualModelUtils instance = new ContextualModelUtils(model);
        int expResult = 2;
        int result = instance.getItemRatingCount(5);
        assertEquals(expResult, result);
    }

    /**
     * Test of getItemIndex method, of class ContextualModelUtils.
     */
    public void testGetItemIndex1() {
        ContextualModelUtils instance = new ContextualModelUtils(model);
        int expResult = 0;
        int result = instance.getItemIndex(1);
        assertEquals(expResult, result);
    }
    public void testGetItemIndex2() {
        ContextualModelUtils instance = new ContextualModelUtils(model);
        int expResult = 1;
        int result = instance.getItemIndex(3);
        assertEquals(expResult, result);
    }
    public void testGetItemIndex3() {
        ContextualModelUtils instance = new ContextualModelUtils(model);
        int expResult = 2;
        int result = instance.getItemIndex(5);
        assertEquals(expResult, result);
    }

    /**
     * Test of getUserIndex method, of class ContextualModelUtils.
     */
    public void testGetUserIndex1() {
        ContextualModelUtils instance = new ContextualModelUtils(model);
        int expResult = 0;
        int result = instance.getUserIndex(1);
        assertEquals(expResult, result);
    }
    public void testGetUserIndex2() {
        ContextualModelUtils instance = new ContextualModelUtils(model);
        int expResult = 1;
        int result = instance.getUserIndex(2);
        assertEquals(expResult, result);
    }
}
