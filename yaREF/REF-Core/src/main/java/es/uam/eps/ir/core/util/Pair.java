package es.uam.eps.ir.core.util;

/**
 *
 * @author Pedro G.Campos
 */
public class Pair<U, I> implements Comparable<Pair<U,I>> {
    U user;
    I item;

    public Pair(U user, I item) {
        this.user = user;
        this.item = item;
    }

    public U getUser() {
        return user;
    }

    public I getItem() {
        return item;
    }
    
    @Override
    public boolean equals(Object obj){
        if (obj == this){
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Pair<U,I> other = (Pair<U,I>)obj;
        return user == other.user && item == other.item;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.user != null ? this.user.hashCode() : 0);
        hash = 89 * hash + (this.item != null ? this.item.hashCode() : 0);
        return hash;
    }

    public int compareTo(Pair<U, I> o) {
        int result = 0;

        // Comparing user IDs
        if (result == 0) {
            String otherUserKey = o.getUser().toString();
            String thisUserKey  = getUser().toString();
            result = otherUserKey.compareTo(thisUserKey);
        }

        // Comparing item IDs
        if (result == 0) {
            String otherItemKey = o.getItem().toString();
            String thisItemKey  = getItem().toString();
            result = otherItemKey.compareTo(thisItemKey);
        }


        return result;
    }
    
    
}
