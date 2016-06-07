package utils;

import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import java.util.Locale;

/**
 *
 * @author pedro
 */
public class SummaryPrinter<U,I,C extends ContextIF> {
    public String summary(ModelIF<U,I,C> model, ContextualModelUtils<U,I,C> eModel){
            return summary(model, eModel, "");
    }
    
    public String summary(ModelIF<U,I,C> model, ContextualModelUtils<U,I,C> eModel, String prefix){
        final StringBuilder summary = new StringBuilder();
        final String newline = System.getProperty("line.separator");
        
        // users
        int _size=model.getUsers().size();
        String size=String.format(Locale.US,"%,10d",_size);
        summary.append(prefix).append("Users\t").append(size).append(newline);
        
        // items
        _size=model.getItems().size();
        size=String.format(Locale.US,"%,10d",_size);
        summary.append(prefix).append("Items\t").append(size).append(newline);
        
        if (eModel == null){
            eModel = new ContextualModelUtils(model);
        }
        // ratings
        _size=eModel.getFeedbackRecordsCount();
        size=String.format(Locale.US,"%,10d",_size);
        summary.append(prefix).append("Ratings\t").append(size).append(newline);
        
        // mean rating
        float _value = eModel.getMeanRating();
        String value = String.format(Locale.US,"%,7.4f",_value);
        summary.append(prefix).append("MeanRating\t").append(value).append(newline);

        // min rating
        _value = eModel.getMinRating();
        value = String.format(Locale.US,"%,7.4f",_value);
        summary.append(prefix).append("MinRating\t").append(value).append(newline);

        // max rating
        _value = eModel.getMaxRating();
        value = String.format(Locale.US,"%,7.4f",_value);
        summary.append(prefix).append("MaxRating\t").append(value).append(newline);

        // Context-specific info
        try{
            summary.append(prefix).append("MinDate\t").append(eModel.getMinDate()).append(newline);
            summary.append(prefix).append("MaxDate\t").append(eModel.getMaxDate()).append(newline);
        }
        catch(Exception e){
            // nothing to do
        }
        
        return summary.toString();
    }
}
