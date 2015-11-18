package es.uam.eps.ir.core.util;

/**
 *
 * @author Pedro G.Campos
 */
public class Pair<V1, V2> implements Comparable<Pair<V1,V2>> {
    V1 value1;
    V2 value2;

    public Pair(V1 value1, V2 value2) {
        this.value1 = value1;
        this.value2 = value2;
    }

    public V1 getValue1() {
        return value1;
    }

    public V2 getValue2() {
        return value2;
    }
    
    @Override
    public boolean equals(Object obj){
        if (obj == this){
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Pair<V1,V2> other = (Pair<V1,V2>)obj;
        return value1 == other.value1 && value2 == other.value2;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.value1 != null ? this.value1.hashCode() : 0);
        hash = 89 * hash + (this.value2 != null ? this.value2.hashCode() : 0);
        return hash;
    }

    public int compareTo(Pair<V1, V2> o) {
        int result = 0;

        // Comparing user IDs
        if (result == 0) {
            String otherUserKey = o.getValue1().toString();
            String thisUserKey  = getValue1().toString();
            result = otherUserKey.compareTo(thisUserKey);
        }

        // Comparing item IDs
        if (result == 0) {
            String otherItemKey = o.getValue2().toString();
            String thisItemKey  = getValue2().toString();
            result = otherItemKey.compareTo(thisItemKey);
        }


        return result;
    }
}
