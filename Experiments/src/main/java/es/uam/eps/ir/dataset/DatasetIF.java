package es.uam.eps.ir.dataset;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;

/**
 *
 * @author pedro
 */
public interface DatasetIF<U,I,C extends ContextIF> {
    public ModelIF<U,I,C> getModel();
    public ModelIF<U,I,C> getPredefinedTestModel(String testName);
    public ModelIF<U,I,C> getPredefinedTestModel();
    public String getPath();
    public String getResultsPath();
    public String getDetails();
}
