
package ImplicitToExplicitTransformation;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import java.util.List;

public class AccumulatedFrequency<U,I,C extends ContextIF> implements Comparable<AccumulatedFrequency>,PreferenceIF<U,I,C>{
    
    
    private U user;
    private I item;
    private String value;
    private C context;
    private int frec;
    private Float valor;

    public AccumulatedFrequency(U user,C valorcon, I item, Integer frec) {
        this.user = user;
        this.item = item;
        this.context=valorcon;
//        this.value = valorcon;
        this.frec=frec;
    }
      public int compareTo(AccumulatedFrequency o) 
      {
            if (frec > o.getFrec()) {
                return -1;
            }
            if (frec < o.getFrec()) {
                return 1;
            }
            return 0;
        }  
      
    public U getUser() {
        return user;
    }

    public I getItem() {
        return item;
    }

    public C getContext() {
        return context;
    }
     public int getFrec() {
        return frec;
    }
     public String getValor() {
       return value;
    }
    public Float getValue() {
       return valor;
    }




    
}

