package es.uam.eps.ir.metrics.list;

import es.uam.eps.ir.metrics.RecommendationIF;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author pedro
 */
public class RecommendationList<U,I> {
    final U user;
    Set<I> relevantItems;
    Set<I> notRelevantItems;
    List<RecommendationIF<I>> recommendations;

    public RecommendationList(U user, Set<I> relevantItems, Set<I> notRelevantItems) {
        this.user = user;
        this.relevantItems = relevantItems;
        this.notRelevantItems = notRelevantItems;
        this.recommendations = new ArrayList<RecommendationIF<I>>();
    }

    public RecommendationList(RecommendationList<U,I> rl) {
        this.user = rl.user;
        this.relevantItems = new TreeSet<I>();
        this.relevantItems.addAll(rl.relevantItems);
        this.notRelevantItems = new TreeSet<I>();
        this.notRelevantItems.addAll(rl.notRelevantItems);
        this.recommendations = new ArrayList<RecommendationIF<I>>();
        this.recommendations.addAll(rl.recommendations);
    }

    public void setRelevantItems(Set<I> rels){
        this.relevantItems = rels;        
    }
    
    public void setNotRelevantItems(Set<I> notRels){
        this.notRelevantItems = notRels;
    }
    
    public void addRecommendation(RecommendationIF<I> recommendation){
        recommendations.add(recommendation);
    }
    
    public void sort(){
        Collections.sort(recommendations);
    }
    
}
