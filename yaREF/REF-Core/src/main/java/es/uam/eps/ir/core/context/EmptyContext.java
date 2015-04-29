package es.uam.eps.ir.core.context;

/**
 *
 * @author Alejandro
 */
public final class EmptyContext implements ContextIF{
    
    private static EmptyContext singleton = null;

    private EmptyContext(){
        
    }
    
    public static ContextIF getEmptyContext(){
        if (singleton == null){
            singleton = new EmptyContext();
        }
        return singleton;
    }
    
    public int compareTo(ContextIF o) {
        if (o instanceof EmptyContext){
            return 0;
        }
        return Integer.MAX_VALUE;
    }
    
}
