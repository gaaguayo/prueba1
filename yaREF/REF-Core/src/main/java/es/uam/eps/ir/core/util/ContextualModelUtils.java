package es.uam.eps.ir.core.util;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceAggregationFunction;
import es.uam.eps.ir.core.model.PreferenceIF;
import es.uam.eps.ir.core.model.impl.DefaultAggregationFunction;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 *
 * @author pedro
 */
public class ContextualModelUtils<U,I,C extends ContextIF> extends ModelUtils<U,I,C>{
    private static final long serialVersionUID = 1001L;
//    protected ModelIF<U,I,C> model;
    
    protected Map<U,Integer> userIndexMap;    // Maps userKey to userIndex (1..userSize)
    protected Map<I,Integer> itemIndexMap;    // Maps itemKey to itemIndex (1..itemSize)
    protected int ratingCount=0;        // general ratings counter
    protected int aggregatedRatingCount=0;        // general ratings counter
    protected float meanRating;    // overall mean rating
    protected float minRating;     // min. rating in the data
    protected float maxRating;     // max. rating in the data
    protected float[] userMinRating;  // per user min rating
    protected float[] userMaxRating;  // per item max rating
    protected float[] userMeanRating;  // per user mean rating
    protected float[] itemMeanRating;  // per item mean rating
    protected int[] userRatingsCount;  // per user ratings count
    protected int[] itemRatingsCount;  // per item ratings count
    protected int[] userAggregatedRatingsCount;  // per user aggregated ratings count
    protected int[] itemAggregatedRatingsCount;  // per item aggregated ratings count
    protected Date minDate; // minimum rating date
    protected Date maxDate; // maximum rating date
    protected Date[] userMinDate; // per user minimum rating date
    protected Date[] userMaxDate; // per user maximum rating date
    protected boolean indexesComputed=false;  // have means been computed?
    protected boolean meansComputed=false;  // have means been computed?
    protected boolean firstGlobalItem=false;  // have the first item been processed?
    protected boolean firstUserItem=false;  // have the first user's item been processed?
    protected C exampleContext;

 
    protected Map<U, Collection<I>> userItemsMap;
    protected Map<I, Collection<U>> itemUsersMap;
    protected PreferenceAggregationFunction<U,I,C> f;
    
    public ContextualModelUtils(ModelIF<U, I, C> model) {
        super(model);
        indexesComputed=false;
        meansComputed=false;
        userItemsMap = new HashMap<U, Collection<I>>();
        itemUsersMap = new HashMap<I, Collection<U>>();
        f = new DefaultAggregationFunction();
        computeIndexes();       
    }
    
    public final boolean computeIndexes(){
        if (indexesComputed){
            return true;
        }
        userIndexMap=new HashMap();
        itemIndexMap=new HashMap();
        
        int i=0;
        for (U userKey:(Collection<U>)model.getUsers()){
            userIndexMap.put(userKey, i++);
        }

        i=0;
        for (I itemKey:(Collection<I>)model.getItems()){
            itemIndexMap.put(itemKey, i++);
        }
        
        return true;
    }
    
    public C getExampleContext(){ return this.exampleContext;}
    public Collection<I> getItemsRatedBy(U user) {
        if (!userItemsMap.containsKey(user)){
            Collection<I> userItems = new TreeSet<I>();
            Collection<? extends PreferenceIF<U, I, C>> prefs=model.getPreferencesFromUser(user);
            if (prefs != null){
                for(PreferenceIF<U,I,C> pref:prefs){
                    userItems.add(pref.getItem());
                }
            }
            userItemsMap.put(user, userItems);
        }
        return userItemsMap.get(user);
    }

    public Collection<U> getUsersWhomRate(I item) {
        if (!itemUsersMap.containsKey(item)){        
            Collection<U> itemsUsers = new TreeSet<U>();
            Collection<? extends PreferenceIF<U, I, C>> prefs=model.getPreferencesFromItem(item);
            if (prefs != null){
                for(PreferenceIF<U,I,C> pref:prefs){
                    itemsUsers.add(pref.getUser());
                }
            }
            itemUsersMap.put(item, itemsUsers);
        }
        return itemUsersMap.get(item);
    }
    
    public Float getItemMeanRating(I itemKey) {
        if (!meansComputed) this.meansComputation();        
        try {
            int itemIndex=itemIndexMap.get(itemKey);
            return this.itemMeanRating[itemIndex];
        } catch (NullPointerException e){
            return Float.NaN;
        }
        
    }

    public Float getUserMeanRating(U userKey) {
        if (!meansComputed) this.meansComputation();
        try {
            int userIndex=userIndexMap.get(userKey);
            return this.userMeanRating[userIndex];
        } catch (NullPointerException e){
            return Float.NaN;
        }
    }
    
    public Float getMeanRating(){
        if (!meansComputed) this.meansComputation();
        return this.meanRating;
    }

    public Float getMinRating(){
        if (!meansComputed) this.meansComputation();
        return this.minRating;
    }

    public Float getMaxRating(){
        if (!meansComputed) this.meansComputation();
        return this.maxRating;
    }
    
    public Date getMinDate(){
        if (!meansComputed) this.meansComputation();
        return this.minDate;
    }

    public Date getMaxDate(){
        if (!meansComputed) this.meansComputation();
        return this.maxDate;
    }
    
    public Date getMeanUserDate(U user){
        if (!meansComputed) this.meansComputation();
        
        try{
            int userIndex=userIndexMap.get(user);
            Long minUserTime = this.userMinDate[userIndex].getTime();
            Long maxUserTime = this.userMaxDate[userIndex].getTime();
            Long meanUserTime = (minUserTime + maxUserTime) / (long)2;
            return new Date(meanUserTime);
        } catch (NullPointerException e){
            return null;
        }
    }

    public Float getMinRating(U user){
        if (!meansComputed) this.meansComputation();
        try{
            int userIndex=userIndexMap.get(user);
            return this.userMinRating[userIndex];
        } catch (NullPointerException e){
            return null;
        }
    }

    public Float getMaxRating(U userKey){
        if (!meansComputed) this.meansComputation();
        try{
            int userIndex=userIndexMap.get(userKey);
            return this.userMaxRating[userIndex];
        } catch (NullPointerException e){
            return null;
        }
    }

    public Date getMinDate(U user){
        if (!meansComputed) this.meansComputation();
        try{
            int userIndex=userIndexMap.get(user);
            return this.userMinDate[userIndex];
        } catch (NullPointerException e){
            return null;
        }
    }

    public Date getMaxDate(U userKey){
        if (!meansComputed) this.meansComputation();
        try{
            int userIndex=userIndexMap.get(userKey);
            return this.userMaxDate[userIndex];
        } catch (NullPointerException e){
            return null;
        }
    }

    public int getRatingCount() { // all ratings (observations)
        if (!meansComputed) this.meansComputation();
        return ratingCount;
    }


    public int getAggregatedRatingCount() { // unique (user, item) pairs
        return aggregatedRatingCount;
    }
    
    public int getUserRatingCount(U user){
        if (!meansComputed) this.meansComputation();
        try {
            int userIndex=userIndexMap.get(user);
            return this.userRatingsCount[userIndex];
        } catch (NullPointerException e){
            return 0;
        }
    }
    
    public int getUserAggregatedRatingCount(U user){
        if (!meansComputed) this.meansComputation();
        try {
            int userIndex=userIndexMap.get(user);
            return this.userAggregatedRatingsCount[userIndex];
        } catch (NullPointerException e){
            return 0;
        }
    }
    
    public int getItemRatingCount(I item){
        if (!meansComputed) this.meansComputation();
        try {
            int itemIndex=itemIndexMap.get(item);
            return this.itemRatingsCount[itemIndex];
        } catch (NullPointerException e){
            return 0;
        }
    }
    
    public int getItemAggregatedRatingCount(I item){
        if (!meansComputed) this.meansComputation();
        try {
            int itemIndex=itemIndexMap.get(item);
            return this.itemAggregatedRatingsCount[itemIndex];
        } catch (NullPointerException e){
            return 0;
        }
    }

    public int getItemIndex(I itemKey) {
        try{
            return this.itemIndexMap.get(itemKey);
        } catch (NullPointerException e){
            return -1;
        }
    }

    public int getUserIndex(U userKey) {
        try{
            return this.userIndexMap.get(userKey);
        } catch (NullPointerException e){
            return -1;
        }
    }    

 
    public synchronized boolean meansComputation(){
        if (meansComputed) {
            return true;
        }
        if (!indexesComputed) {
            this.computeIndexes();
        }
        userMeanRating=new float[userIndexMap.size()];
        itemMeanRating=new float[itemIndexMap.size()];
        userMinRating=new float[userIndexMap.size()];
        userMaxRating=new float[userIndexMap.size()];
        userRatingsCount=new int[userIndexMap.size()];
        itemRatingsCount=new int[itemIndexMap.size()];
        userAggregatedRatingsCount=new int[userIndexMap.size()];
        itemAggregatedRatingsCount=new int[itemIndexMap.size()];
        userMinDate=new Date[userIndexMap.size()];
        userMaxDate=new Date[userIndexMap.size()];       
        
        for (U userKey:(Collection<U>)model.getUsers()){
            int userIndex=userIndexMap.get(userKey);
            int userRatingCount = 0;
            int userAggregatedRatingCount = 0;
            firstUserItem=false;
            for (I itemKey:(Collection<I>)getItemsRatedBy(userKey)){
                int itemIndex=itemIndexMap.get(itemKey);
                Collection<? extends PreferenceIF<U, I, C>> prefs = model.getPreferences(userKey, itemKey);
                PreferenceIF<U,I,C> aggregatedPreference = f.getAggregatedValue(prefs);
                float theRating = aggregatedPreference.getValue();
                processAggregatedRatingValue(userIndex,itemIndex,theRating);
                userAggregatedRatingCount++;
                aggregatedRatingCount++;
                        
                for (PreferenceIF<U,I,C> pref:prefs){
                    processPreference(userIndex, itemIndex, pref);
                    userRatingCount++;
                }
            }
            userMeanRating[userIndex]/=(double)userAggregatedRatingCount;
        }
        // item mean rating computation
        for (I itemKey:(Collection<I>)model.getItems()){
            int itemIndex=itemIndexMap.get(itemKey);
            itemMeanRating[itemIndex]/=(double)itemAggregatedRatingsCount[itemIndex];
            
        }
        meanRating/=(double)aggregatedRatingCount;
        meansComputed=true;
        return true;
    }
    
    private void processAggregatedRatingValue(int userIndex, int itemIndex, float aggregatedRating){
        // global min and max rating
        if (!firstGlobalItem){
            this.minRating = aggregatedRating;
            this.maxRating = aggregatedRating;
        }

        if (aggregatedRating < this.minRating){
            this.minRating = aggregatedRating;
        }
        if (aggregatedRating > this.maxRating){
            this.maxRating = aggregatedRating;
        }
        
        // per user min and max rating
        if (aggregatedRating < userMinRating[userIndex] || userMinRating[userIndex] == 0){
            userMinRating[userIndex] = aggregatedRating;
        }
        if (aggregatedRating > userMaxRating[userIndex] || userMaxRating[userIndex] == 0){
            userMaxRating[userIndex] = aggregatedRating;
        }
        
        userAggregatedRatingsCount[userIndex]++;
        itemAggregatedRatingsCount[itemIndex]++;
    }
    
    private void processPreference(int userIndex, int itemIndex, PreferenceIF<U,I,C> pref){
        processRatingValue(userIndex,itemIndex,pref.getValue());
        
        C context = pref.getContext();
        if (context instanceof ContinuousTimeContextIF){
            processContinuousTimeContext(userIndex,itemIndex,(ContinuousTimeContextIF)context);
        }
        
        if (!firstGlobalItem){
            this.exampleContext = context;
            firstGlobalItem=true;
        }
    }

    private void processRatingValue(int userIndex, int itemIndex, float theRating){
        // Rating statistics
        userMeanRating[userIndex] += theRating;
        itemMeanRating[itemIndex] += theRating;
        meanRating += theRating;
        userRatingsCount[userIndex]++;
        itemRatingsCount[itemIndex]++;
    //                if (theRating>relevantRatingThreshold){
    //                    dim1RelFrequencies[dim1Index]++;
    //                    dim2RelFrequencies[dim2Index]++;
    //                }

        ratingCount++;        
    }

    private void processContinuousTimeContext(int userIndex, int itemIndex, ContinuousTimeContextIF context){
        Date theDate=new Date( context.getTimestamp() );
        
        // Initialization of general values
        if (!firstGlobalItem){
            this.minDate=theDate;
            this.maxDate=theDate;
        }
        
        // Initialization of per-user values
        if (!firstUserItem){
            this.userMinDate[userIndex]=theDate;
            this.userMaxDate[userIndex]=theDate;
            firstUserItem=true;
        }
        
        // Date statistics
        if (theDate.before(this.minDate)){
            this.minDate=theDate;
        }
        if (theDate.after(this.maxDate)){
            this.maxDate=theDate;
        }
        if (theDate.before(this.userMinDate[userIndex])){
            this.userMinDate[userIndex]=theDate;
        }
        if (theDate.after(this.userMaxDate[userIndex])){
            this.userMaxDate[userIndex]=theDate;
        }
    }
    
}
