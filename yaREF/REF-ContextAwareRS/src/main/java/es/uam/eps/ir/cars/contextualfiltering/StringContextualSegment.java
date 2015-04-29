package es.uam.eps.ir.cars.contextualfiltering;

/**
 *
 * @author pedro
 */
public class StringContextualSegment implements ContextualSegmentIF{
    String value;

    public StringContextualSegment(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        StringContextualSegment o = (StringContextualSegment)obj;
        return value.equals(o.value);
    }
    
    
    
    @Override
    public String toString(){
        return value;
    }
}
