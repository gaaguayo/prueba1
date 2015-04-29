package es.uam.eps.ir.analysis;

import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.dataset.CommonDatasets;
import es.uam.eps.ir.dataset.DatasetIF;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pedro G. Campos
 */
public class ItemTimeAnalysis {
    // Dataset
    static CommonDatasets.DATASET dataset_name = CommonDatasets.DATASET.LastFM_Time;
    
    public static void main (String args[]){
        final DatasetIF<Object, Object, ContinuousTimeContextIF> dataset = new CommonDatasets(args).getDataset(dataset_name);
        final ModelIF<Object, Object, ContinuousTimeContextIF> model = dataset.getModel();
        final String file = "/datos/experiments/ItemAnalysis/" + dataset + "/ObservationsPerUserItemDistribution.txt";
        computeObservationsDistribution(file, model);
    }
    
    // Computes item distribution (in terms of number of observations) in the dataset
    @SuppressWarnings("CallToThreadDumpStack")
    public static void computeObservationsDistribution(String file, ModelIF<Object, Object, ContinuousTimeContextIF> model){
        try {
            FileOutputStream fos = new FileOutputStream(file);
            PrintStream ps = new PrintStream(fos, true);
            final String sep = "";
            ps.println("user\titem\tminTime\tmaxTime\tobservations");
            ContextualModelUtils<Object, Object, ContinuousTimeContextIF> util = new ContextualModelUtils<Object, Object, ContinuousTimeContextIF>(model);
            for (Object user : model.getUsers()){
                Collection userItems = util.getItemsRatedBy(user);
                for (Object item : userItems){
                    long minTime = Long.MAX_VALUE;
                    long maxTime = Long.MIN_VALUE;
                    Collection<? extends PreferenceIF<Object,Object,ContinuousTimeContextIF>> preferences = model.getPreferences(user, item);
                    for (PreferenceIF<Object,Object,ContinuousTimeContextIF> pref : preferences){
                        long time = pref.getContext().getTimestamp();
                        if (minTime > time){
                            minTime = time;
                        }
                        if (maxTime < time){
                            maxTime = time;
                        }
                    }
                    int observations = preferences.size();
                    ps.println(user + "\t" + item + "\t" + minTime + "\t" + maxTime + "\t" + observations);
                }
            }
            ps.flush();
            fos.flush();
            fos.close();
        } catch (IOException ex) {
            Logger.getLogger(ItemTimeAnalysis.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            System.exit(1);
        }
    }
}
