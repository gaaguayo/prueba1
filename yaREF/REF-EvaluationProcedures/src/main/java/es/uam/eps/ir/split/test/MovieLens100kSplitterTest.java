package es.uam.eps.ir.split.test;

import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.split.SplitIF;
import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.split.impl.DatasetSplitterBuilder;
import es.uam.eps.ir.split.DatasetSplitterIF;
import java.util.Date;

/**
 *
 * @author pedro
 */
public class MovieLens100kSplitterTest {
    
    public static void test(ModelIF model){
        DatasetSplitterIF splitter;
        
        // Test of user-centric base set, random rating order, proportion size
        splitter = getUserTimeOrderProportionHoldout(0.2);
        testUserTimeOrderProportionHoldout(splitter.split(model));
        
        splitter = getCommunityTimeOrderProportionHoldout(0.2);
        testCommunityTimeOrderProportionHoldout(splitter.split(model));
    }
    
    public static void testUserTimeOrderProportionHoldout(SplitIF splits[]){        
        ModelIF trainingSet = splits[0].getTrainingSet();
        ModelIF testingSet = splits[0].getTestingSet();
        
        ContextualModelUtils trainingUtils = new ContextualModelUtils(trainingSet);
        ContextualModelUtils testingUtils = new ContextualModelUtils(testingSet);
        
        int expectedTrainingUsers = 943;
        int expectedTrainingItems = 1615;
        int expectedTrainingPrefs = 80367;
        Float expectedTrainingMeanRating = new Float(3.5792675);
        Date expectedTrainingMinDate = new Date(874724710000L);
        Date expectedTrainingMaxDate = new Date(893286638000L);
        
        int actualTrainingUsers = trainingSet.getUsers().size();
        int actualTrainingItems = trainingSet.getItems().size();
        int actualTrainingPrefs = trainingUtils.getRatingCount();
        Float actualTrainingMeanRating = trainingUtils.getMeanRating();
        Date actualTrainingMinDate = trainingUtils.getMinDate();
        Date actualTrainingMaxDate = trainingUtils.getMaxDate();
        
        
        int expectedTestingUsers = 943;
        int expectedTestingItems = 1497;
        int expectedTestingPrefs = 19633;
        Float expectedTestingMeanRating = new Float(3.3276117);
        Date expectedTestingMinDate = new Date(874786695000L);
        Date expectedTestingMaxDate = new Date(893286638000L);
        
        int actualTestingUsers = testingSet.getUsers().size();
        int actualTestingItems = testingSet.getItems().size();
        int actualTestingPrefs = testingUtils.getRatingCount();
        Float actualTestingMeanRating = testingUtils.getMeanRating();
        Date actualTestingMinDate = testingUtils.getMinDate();
        Date actualTestingMaxDate = testingUtils.getMaxDate();

        
        
        System.out.println("=============================================================");
        System.out.println("=============================================================");
        System.out.println("MovieLens100k user centric, time dependent holdout split test");
        System.out.println("=============================================================");
        System.out.println("basic statistics:    expected    actual  equals");
        
        System.out.format ("training users       %8d  %8d    %b%n", expectedTrainingUsers, actualTrainingUsers, (expectedTrainingUsers == actualTrainingUsers) );
        System.out.format ("training items       %8d  %8d    %b%n", expectedTrainingItems, actualTrainingItems, (expectedTrainingItems == actualTrainingItems) );
        System.out.format ("training prefs       %8d  %8d    %b%n", expectedTrainingPrefs, actualTrainingPrefs, (expectedTrainingPrefs == actualTrainingPrefs) );
        System.out.format ("training mean rating %1.6f  %1.6f    %b%n", expectedTrainingMeanRating, actualTrainingMeanRating, (expectedTrainingMeanRating.compareTo(actualTrainingMeanRating) == 0) );
        
        System.out.format ("testing users        %8d  %8d    %b%n", expectedTestingUsers, actualTestingUsers, (expectedTestingUsers == actualTestingUsers) );
        System.out.format ("testing items        %8d  %8d    %b%n", expectedTestingItems, actualTestingItems, (expectedTestingItems == actualTestingItems) );
        System.out.format ("testing prefs        %8d  %8d    %b%n", expectedTestingPrefs, actualTestingPrefs, (expectedTestingPrefs == actualTestingPrefs) );
        System.out.format ("testing mean rating  %1.6f  %1.6f    %b%n", expectedTestingMeanRating, actualTestingMeanRating, (expectedTestingMeanRating.compareTo(actualTestingMeanRating) == 0) );
        
        System.out.println("=============================================================");
        System.out.println("Time statistics                                        equals");
        System.out.format ("expec. training min date: %tF %tT %n", expectedTrainingMinDate, expectedTrainingMinDate);
        System.out.format ("actual training min date: %tF %tT %b%n", actualTrainingMinDate, actualTrainingMinDate, (expectedTrainingMinDate.compareTo(actualTrainingMinDate) == 0) );
        System.out.format ("expec. training max date: %tF %tT %n", expectedTrainingMaxDate, expectedTrainingMaxDate);
        System.out.format ("actual training max date: %tF %tT %b%n", actualTrainingMaxDate, actualTrainingMaxDate, (expectedTrainingMaxDate.compareTo(actualTrainingMaxDate) == 0) );
        System.out.format ("expec.  testing min date: %tF %tT %n", expectedTestingMinDate, expectedTestingMinDate);
        System.out.format ("actual  testing min date: %tF %tT %b%n", actualTestingMinDate, actualTestingMinDate, (expectedTestingMinDate.compareTo(actualTestingMinDate) == 0) );
        System.out.format ("expec.  testing max date: %tF %tT %n", expectedTestingMaxDate, expectedTestingMaxDate);
        System.out.format ("actual  testing max date: %tF %tT %b%n", actualTestingMaxDate, actualTestingMaxDate, (expectedTestingMaxDate.compareTo(actualTestingMaxDate) == 0) );
        
    }

    public static void testCommunityTimeOrderProportionHoldout(SplitIF splits[]){        
        ModelIF trainingSet = splits[0].getTrainingSet();
        ModelIF testingSet = splits[0].getTestingSet();
        
        ContextualModelUtils trainingUtils = new ContextualModelUtils(trainingSet);
        ContextualModelUtils testingUtils = new ContextualModelUtils(testingSet);
        
        int expectedTrainingUsers = 751;
        int expectedTrainingItems = 1616;
        int expectedTrainingPrefs = 80000;
        Float expectedTrainingMeanRating = new Float(3.517650);
        Date expectedTrainingMinDate = new Date(874724710000L);
        Date expectedTrainingMaxDate = new Date(889237269000L);
        
        int actualTrainingUsers = trainingSet.getUsers().size();
        int actualTrainingItems = trainingSet.getItems().size();
        int actualTrainingPrefs = trainingUtils.getRatingCount();
        Float actualTrainingMeanRating = trainingUtils.getMeanRating();
        Date actualTrainingMinDate = trainingUtils.getMinDate();
        Date actualTrainingMaxDate = trainingUtils.getMaxDate();
        
        
        int expectedTestingUsers = 301;
        int expectedTestingItems = 1448;
        int expectedTestingPrefs = 20000;
        Float expectedTestingMeanRating = new Float(3.578700);
        Date expectedTestingMinDate = new Date(889237269000L);
        Date expectedTestingMaxDate = new Date(893286638000L);
        
        int actualTestingUsers = testingSet.getUsers().size();
        int actualTestingItems = testingSet.getItems().size();
        int actualTestingPrefs = testingUtils.getRatingCount();
        Float actualTestingMeanRating = testingUtils.getMeanRating();
        Date actualTestingMinDate = testingUtils.getMinDate();
        Date actualTestingMaxDate = testingUtils.getMaxDate();

        
        
        System.out.println("==================================================================");
        System.out.println("==================================================================");
        System.out.println("MovieLens100k community centric, time dependent holdout split test");
        System.out.println("==================================================================");
        System.out.println("basic statistics:    expected    actual  equals");
        
        System.out.format ("training users       %8d  %8d    %b%n", expectedTrainingUsers, actualTrainingUsers, (expectedTrainingUsers == actualTrainingUsers) );
        System.out.format ("training items       %8d  %8d    %b%n", expectedTrainingItems, actualTrainingItems, (expectedTrainingItems == actualTrainingItems) );
        System.out.format ("training prefs       %8d  %8d    %b%n", expectedTrainingPrefs, actualTrainingPrefs, (expectedTrainingPrefs == actualTrainingPrefs) );
        System.out.format ("training mean rating %1.6f  %1.6f    %b%n", expectedTrainingMeanRating, actualTrainingMeanRating, (expectedTrainingMeanRating.compareTo(actualTrainingMeanRating) == 0) );
        
        System.out.format ("testing users        %8d  %8d    %b%n", expectedTestingUsers, actualTestingUsers, (expectedTestingUsers == actualTestingUsers) );
        System.out.format ("testing items        %8d  %8d    %b%n", expectedTestingItems, actualTestingItems, (expectedTestingItems == actualTestingItems) );
        System.out.format ("testing prefs        %8d  %8d    %b%n", expectedTestingPrefs, actualTestingPrefs, (expectedTestingPrefs == actualTestingPrefs) );
        System.out.format ("testing mean rating  %1.6f  %1.6f    %b%n", expectedTestingMeanRating, actualTestingMeanRating, (expectedTestingMeanRating.compareTo(actualTestingMeanRating) == 0) );
        
        System.out.println("==================================================================");
        System.out.println("Time statistics                                        equals");
        System.out.format ("expec. training min date: %tF %tT %n", expectedTrainingMinDate, expectedTrainingMinDate);
        System.out.format ("actual training min date: %tF %tT %b%n", actualTrainingMinDate, actualTrainingMinDate, (expectedTrainingMinDate.compareTo(actualTrainingMinDate) == 0) );
        System.out.format ("expec. training max date: %tF %tT %n", expectedTrainingMaxDate, expectedTrainingMaxDate);
        System.out.format ("actual training max date: %tF %tT %b%n", actualTrainingMaxDate, actualTrainingMaxDate, (expectedTrainingMaxDate.compareTo(actualTrainingMaxDate) == 0) );
        System.out.format ("expec.  testing min date: %tF %tT %n", expectedTestingMinDate, expectedTestingMinDate);
        System.out.format ("actual  testing min date: %tF %tT %b%n", actualTestingMinDate, actualTestingMinDate, (expectedTestingMinDate.compareTo(actualTestingMinDate) == 0) );
        System.out.format ("expec.  testing max date: %tF %tT %n", expectedTestingMaxDate, expectedTestingMaxDate);
        System.out.format ("actual  testing max date: %tF %tT %b%n", actualTestingMaxDate, actualTestingMaxDate, (expectedTestingMaxDate.compareTo(actualTestingMaxDate) == 0) );
        
    }
    
    public static DatasetSplitterIF getUserTimeOrderProportionHoldout(double testProportion){
        DatasetSplitterIF splitter = new DatasetSplitterBuilder()
                .userBase()
                .TimeOrder()
                .ProportionSize(testProportion)
                .buildSplitter();
        return splitter;
    }
    
    public static DatasetSplitterIF getCommunityTimeOrderProportionHoldout(double testProportion){
        DatasetSplitterIF splitter = new DatasetSplitterBuilder()
                .communityBase()
                .TimeOrder()
                .ProportionSize(testProportion)
                .buildSplitter();
        return splitter;
    }
    
}
