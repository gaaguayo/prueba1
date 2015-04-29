package es.uam.eps.ir.analysis2;

import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.context.RatingPreferenceComparator;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import es.uam.eps.ir.dataset.CommonDatasets;
import es.uam.eps.ir.dataset.DatasetIF;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author pedro
 */
public class UserTimestampDistribution {
    // Dataset
    static CommonDatasets.DATASET dataset_name = CommonDatasets.DATASET.MovieLens1m;
    static DatasetIF<Object, Object, ContinuousTimeContextIF> dataset;
    static ModelIF<Object, Object, ContinuousTimeContextIF> model;
    static final long dayMilliseconds = 1000L * 24L * 60L * 60L;

    public static void main (String args[]){
        dataset = new CommonDatasets(args).getDataset(dataset_name);
        model = dataset.getModel();
        final String file = "/datos/experiments/ItemAnalysis/" + dataset + "/ObservationsPerUserItemDistribution.txt";
        List users = new ArrayList(model.getUsers());
        Collections.sort(users);
        
        for (Object user : users){
            final List<PreferenceIF> userPrefs = new ArrayList<PreferenceIF>(model.getPreferencesFromUser(user));
            Collections.sort(userPrefs, new RatingPreferenceComparator() );
            int userDays = daysLength(userPrefs);
            System.out.println(user + "\t" + userDays + "\t" + entropy(userPrefs, userDays) + "\t" + userPrefs.size());
        }
    }
    
    public static int daysLength(List<PreferenceIF> prefs){
        Long days;
        long first = ((ContinuousTimeContextIF)prefs.get(0).getContext()).getTimestamp();
        long last = ((ContinuousTimeContextIF)prefs.get(prefs.size()-1).getContext()).getTimestamp();
        days = (last - first) / dayMilliseconds;
        return days.intValue();
    }
    
    public static double entropy(List<PreferenceIF> prefs, int daysLength){
        double entropy = 0.0;
        
        int totalPrefs = prefs.size();
        List<Integer> dayPrefsList = new ArrayList<Integer>();
        
        long first = ((ContinuousTimeContextIF)prefs.get(0).getContext()).getTimestamp();
        long last = ((ContinuousTimeContextIF)prefs.get(prefs.size()-1).getContext()).getTimestamp();
        long next = first + dayMilliseconds;
        int dayPrefs = 0;
        
        
        for (int i = 0; i < prefs.size(); i++){
            PreferenceIF pref = prefs.get(i);
            long ts = ((ContinuousTimeContextIF)pref.getContext()).getTimestamp();
            if (ts < next){
                dayPrefs++;
            }
            else{
                dayPrefsList.add(dayPrefs);
                next += dayMilliseconds;
                dayPrefs = 0;
                i--;
            }
        }
        dayPrefsList.add(dayPrefs);
        
        for (int dayPrefsCount : dayPrefsList){
            double prob = (double)dayPrefsCount / (double)totalPrefs;
            if (prob != 0.0){
                entropy -= prob * Math.log(prob);
            }
        }
        
        return entropy;
    }
}
