package es.uam.eps.ir.cars.model;

import es.uam.eps.ir.cars.recommender.OfflineRecommender;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.rec.RecommenderIF;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author pedro
 */
public class TrecFormatRecommenderReader<U,I,C extends ContextIF>{
    private final boolean COMPRESSED;

    public TrecFormatRecommenderReader(boolean COMPRESSED) {
        this.COMPRESSED = COMPRESSED;
    }

    public RecommenderIF<U, I, C> readRecommendations(String file) {
        Logger logger = Logger.getLogger("ExperimentLog");
        
        OfflineRecommender<U,I,C> model=new OfflineRecommender<U,I,C>();

        String fileName=file+".gz";
        BufferedReader reader;
        
        logger.log(Level.INFO, "Reading recommendations data (from {0})", fileName);            
        
        try{
            //Opening
            reader=new BufferedReader(
                    new InputStreamReader(
                    new GZIPInputStream(new FileInputStream(fileName)))
                    );
            
            // reading
            String line;
            while( (line=reader.readLine() ) != null){
                String[] values = line.split("\t");
                U userKey=(U)values[0];
                I itemKey=(I)values[2];
                float rating=Float.parseFloat(values[4]);
//                Date date=new Date(Long.parseLong(values[4]));
//                C context = (C)new ContinuousTimeContext(date.getTime());                
                
                model.addPreference(userKey, itemKey, rating, null);
            }
            
            // Closing
            reader.close();
            
        } catch (java.io.IOException e){
            System.err.println("Problem opening file "+fileName+": "+e);
            for (StackTraceElement ste:e.getStackTrace())
                System.err.println(ste);
        }
        
        return model;
    }    
}
