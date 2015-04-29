package es.uam.eps.ir.split.sizecondition;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import java.util.Date;

/**
 *
 * @author Pedro G. Campos
 */
public interface SizeConditionDateBasedIF<U,I,C extends ContextIF> {
    public Date getStartDateForTest(ModelIF<U,I,C> model);
    public Date getEndDateForTest(ModelIF<U,I,C> model);    
}
