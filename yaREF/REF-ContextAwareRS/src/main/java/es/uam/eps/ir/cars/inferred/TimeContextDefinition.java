package es.uam.eps.ir.cars.inferred;

import es.uam.eps.ir.core.context.CategoricalContextDefinitionIF;
import es.uam.eps.ir.core.context.ContextDefinition;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pedro G. Campos <pcampossoto@gmail.com>
 * Creation date: 13-nov-2015
 */
public class TimeContextDefinition extends ContextDefinition implements CategoricalContextDefinitionIF,Comparable<ContextDefinition>{
    private CategoricalContextComputerIF computer;
    final static Logger logger = Logger.getLogger("ExperimentLog");
            
    public TimeContextDefinition(String context) {
        super(context);
        for (ContinuousTimeContextComputerBuilder.TimeContext tc : ContinuousTimeContextComputerBuilder.TimeContext.values()){
            if (tc.name().equalsIgnoreCase(context))
                computer =  ContinuousTimeContextComputerBuilder.getContextComputer(tc);
        }
        if (computer == null){
            logger.log(Level.SEVERE, "ERROR: Context {0} not found!", context);
            System.exit(1);
        }
        for (String ctx : Arrays.asList(computer.getAttributeType().split(","))){
            addValue(ctx);
        }        
    }
    
    public String getNominalValue(ContinuousTimeContextIF context){
        return computer.getAttributeNominalValue(context);
    }
}
