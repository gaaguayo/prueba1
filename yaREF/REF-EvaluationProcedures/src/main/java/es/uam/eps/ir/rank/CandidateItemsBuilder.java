package es.uam.eps.ir.rank;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.split.SplitIF;

/**
 *
 * @author pedro
 */
public class CandidateItemsBuilder<U,I,C extends ContextIF> {
    public enum CANDIDATE_ITEMS{
        USER_TEST,
        USER_TEST_USER_MEAN,
        COMMUNITY_TEST,
        COMMUNITY_TRAINING,
        ONE_PLUS_RANDOM,
        ONE_PLUS_RANDOM_CONTEXT
    }
    
    private CANDIDATE_ITEMS candidate_items = CANDIDATE_ITEMS.COMMUNITY_TEST;
    private float threshold = (float)0.0;
    private int nForOnePlusRandom = 10;
    
    public CandidateItemsBuilder(){
    }
    
    public CandidateItemsBuilder<U,I,C> userTest(){
        candidate_items = CANDIDATE_ITEMS.USER_TEST;
        return this;
    }

    public CandidateItemsBuilder<U,I,C> userTest_UserMean(){
        candidate_items = CANDIDATE_ITEMS.USER_TEST_USER_MEAN;
        return this;
    }

    public CandidateItemsBuilder<U,I,C> CommunityTesting(){
        candidate_items = CANDIDATE_ITEMS.COMMUNITY_TEST;
        return this;
    }
    
    public CandidateItemsBuilder<U,I,C> CommunityTraining(){
        candidate_items = CANDIDATE_ITEMS.COMMUNITY_TRAINING;
        return this;
    }
    
    public CandidateItemsBuilder<U,I,C> onePlusRandom(){
        candidate_items = CANDIDATE_ITEMS.ONE_PLUS_RANDOM;
        return this;
    }
    
    public CandidateItemsBuilder<U,I,C> onePlusRandomContext(){
        candidate_items = CANDIDATE_ITEMS.ONE_PLUS_RANDOM_CONTEXT;
        return this;
    }
        
    public CandidateItemsBuilder<U,I,C> onePlusRandom(int N){
        candidate_items = CANDIDATE_ITEMS.ONE_PLUS_RANDOM;
        this.nForOnePlusRandom = N;
        return this;
    }
    
    public CandidateItemsBuilder<U,I,C> onePlusRandomContext(int N){
        candidate_items = CANDIDATE_ITEMS.ONE_PLUS_RANDOM_CONTEXT;
        this.nForOnePlusRandom = N;
        return this;
    }
    
    public CandidateItemsBuilder<U,I,C> threshold(float threshold){
        this.threshold = threshold;
        return this;
    }
    
    public CandidateItemsBuilder<U,I,C> nForOnePlusRandom(int N){
        this.nForOnePlusRandom = N;
        return this;
    }
    
    public CandidateItemsBuilder<U,I,C> set(CANDIDATE_ITEMS candidateItems){
        candidate_items = candidateItems;
        return this;
    }
    
    
    public CandidateItemsIF<U,I,C> buildCandidateItems(SplitIF<U,I,C> split){
        CandidateItemsIF<U,I,C> _candidateItems = null;
        switch (candidate_items){
            case USER_TEST:
                _candidateItems = new CandidateItems_UserTest(split, threshold);
                break;
            case USER_TEST_USER_MEAN:
                _candidateItems = new CandidateItems_UserTest_UserMean(split);
                break;
            case COMMUNITY_TEST:
                _candidateItems = new CandidateItems_CommunityTest(split, threshold);
                break;
            case COMMUNITY_TRAINING:
                _candidateItems = new CandidateItems_CommunityTraining(split, threshold);
                break;
            case ONE_PLUS_RANDOM:
                _candidateItems = new CandidateItems_OnePlusRandom(split, nForOnePlusRandom, threshold);
                break;
            case ONE_PLUS_RANDOM_CONTEXT:
                _candidateItems = new CandidateItems_OnePlusRandom_Context(split, nForOnePlusRandom, threshold);
                break;
        }
        return _candidateItems;
    }
}
