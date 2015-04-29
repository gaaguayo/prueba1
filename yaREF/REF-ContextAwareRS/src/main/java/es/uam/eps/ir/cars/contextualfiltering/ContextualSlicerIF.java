package es.uam.eps.ir.cars.contextualfiltering;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import java.util.Date;
import java.util.List;

/**
 * Implementations of this interface must provide a method able to return
 * ratings corresponding to a particular segment, identified by segmentName
 *
 * @author Pedro G. Campos <pcampossoto@gmail.com>
 * Creation date: 19-feb-2012
 */
public interface ContextualSlicerIF<U,I,C extends ContextIF> {
    /*
     * Returns a {@link ModelIF} containing only those data in fullData
     * corresponding to the {@link ContextualSegment} segment
     */
    ModelIF<U,I,C> getSegmentData(ModelIF<U,I,C> fullData, ContextualSegmentIF segment);
    
    /*
     * Returns a {@List} of the {@ContextualSegmentIF} considered by the slicer
     */
    List<ContextualSegmentIF> getSegments();
    
    /*
     * Returns the {@ContextualSegmentIF} in which context falls
     */
    ContextualSegmentIF getSegment(C context);
    
    /*
     * Returns an instance of {@Date} which falls in the {@ContextualSegment}
     * segment
     */
//    Date getDateInContext(ContextualSegmentIF segment);
}
