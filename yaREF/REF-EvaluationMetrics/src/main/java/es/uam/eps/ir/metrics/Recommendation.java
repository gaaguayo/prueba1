package es.uam.eps.ir.metrics;

/**
 *
 * @author pedro
 */
public class Recommendation<I> implements RecommendationIF<I> {
    private I item;
    private Float value;
    private boolean personalized;

    public Recommendation(I item, Float value, boolean personalized) {
        this.item = item;
        this.value = value;
        this.personalized = personalized;
    }

    public I getItemID() {
        return item;
    }

    public Float getValue() {
        return value;
    }

    public boolean isPersonalized() {
        return personalized;
    }

    public int compareTo(RecommendationIF<I> t) {
        int res=Float.compare(t.getValue(),this.value);
        Integer aux=new Integer(0);
        if (res==0){
            String otherRecommItemID=null;
            String thisRecommItemID=null;
            if (this.item instanceof Integer){
                otherRecommItemID=Integer.toString((Integer)t.getItemID());
                thisRecommItemID=Integer.toString((Integer)this.item);
            }
            else if(this.item instanceof String){
                otherRecommItemID=(String)t.getItemID();
                thisRecommItemID=(String)this.item;
            }
            
            res=otherRecommItemID.compareTo(thisRecommItemID);
        }
        return res;
    }
    
}
