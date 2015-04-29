package es.uam.eps.ir.metrics;

import java.util.List;
import java.util.Set;

/**
 * @author pedro
 */
public interface MetricResultsIF<U> {

    /*
     * @return metric result among all users, regardless level
     */
    double getResult();

    /*
     * @return metric result among all users, at specified level
     */
    double getResult(int level);
    
    /*
     * @return metric result from a user, regardless level
     */
    double getResult(U user);

    /*
     * @return metric results from a user, at defined level
     */
    double getResult(U user, int level);


    /*
     * @return the {@ Set} of users evaluated
     */
    Set<U> getUsers();

    List<Integer> getLevels();

    // Print methods
    String formattedValue();
    String formattedName();
    String shortName();
    String columnFormat();
    
}
