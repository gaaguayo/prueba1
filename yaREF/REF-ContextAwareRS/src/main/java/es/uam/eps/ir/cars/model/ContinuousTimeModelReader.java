package es.uam.eps.ir.cars.model;

import es.uam.eps.ir.core.context.ContinuousTimeContext;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pedro
 */
public class ContinuousTimeModelReader<U,I,C extends ContinuousTimeContextIF> extends GenericModelReader<U,I,C> implements ModelReaderIF<U,I,C>{
    protected int timestampIndex = -1;
    protected String timestampFormat;
    
    public void setTimestampIndex(int index) {this.timestampIndex = index;}
    public void setTimestampFormat(String format) {this.timestampFormat = format;}
    
    public String getTimestampFormat() {return timestampFormat;}
    public int getTimestampIndex() {return timestampIndex;}

    @Override
    protected C processContextData(String[] values){
        Logger logger = Logger.getLogger("ExperimentLog");
        Date date=null;
        C context = null;
        SimpleDateFormat df=new SimpleDateFormat();
        df.applyPattern(this.timestampFormat);
        
        try{
            if (values[timestampIndex].equalsIgnoreCase("")){ 
                logger.log(Level.WARNING, "Ignoring line {0} (user:{1}, item:{2},ts:{3})", new Object[]{values.toString(), values[userIndex], values[itemIndex], values[timestampIndex]});
            }
            date=df.parse(values[timestampIndex]);
        } catch (java.text.ParseException e){
            // assumes UNIX's epoch as timestamp
            date=new Date(new Long(values[timestampIndex])*(long)1000);
        }
        context = (C)new ContinuousTimeContext(date.getTime());
        
        return context;
    }
}
