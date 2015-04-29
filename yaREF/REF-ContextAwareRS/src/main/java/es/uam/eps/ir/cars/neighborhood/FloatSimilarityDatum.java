package es.uam.eps.ir.cars.neighborhood;

import java.io.Serializable;

/**
 *
 * @author Pedro G. Campos <pcampossoto@gmail.com>
 * @date 2011-jan-04
 */
public class FloatSimilarityDatum implements SimilarityDatumIF, Comparable<FloatSimilarityDatum>, Serializable{
    private float similarity;
    private Object key;

    public FloatSimilarityDatum(float similarity, Object key){
        this.similarity=similarity;
        this.key=key;
    }

    public Object getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public float getSimilarity() {
        return similarity;
    }

    public void setSimilarity(float similarity) {
        this.similarity = similarity;
    }

    public int compareTo(FloatSimilarityDatum t) {
        if ( (t.similarity-this.similarity) > 0) {
            return 1;
        }
        else if ( (t.similarity-this.similarity) < 0) {
            return -1;
        }
        else {
            return ((Comparable)key).compareTo(t.key);
        }
    }
    
    @Override
    public String toString(){
        return "key:" + key +" sim:" + similarity;
    }
}
