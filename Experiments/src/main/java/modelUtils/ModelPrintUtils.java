package modelUtils;

import es.uam.eps.ir.cars.model.ContinuousTimeModelReader;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.model.DescriptionIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author Pedro G. Campos
 */
public class ModelPrintUtils {
    private ModelIF model;
    private List userIdxs;
    private List<Integer> userPrefs;
    private int currentLine = 0;
    private int totalLines = 0;
    private int currentUserIdx = 0;
    private int currentUserPrefIdx = 0;
    private List<PreferenceIF> currentUserPrefs;
    private final static Logger logger = Logger.getLogger("ExperimentLog");
    private static int logLine = 1000000;
    
    
    public ModelPrintUtils(ModelIF model, int logLine) {
        this(model);
        ModelPrintUtils.logLine = logLine;
    }
    public ModelPrintUtils(ModelIF model) {
        this.model = model;
        userIdxs = new ArrayList(model.getUsers());
        userPrefs = new ArrayList<Integer>();
        for (Object user : userIdxs){
            int items = model.getPreferencesFromUser(user).size();
            userPrefs.add(items);
            totalLines += items;
        }
        currentUserPrefs = new ArrayList<PreferenceIF>(model.getPreferencesFromUser(userIdxs.get(currentUserIdx)));
    }

    public boolean hasNext(){
        return currentLine < totalLines ? true : false;
    }
    
    public String next(){
        if (currentLine >= totalLines){
            return null;
        }
        
        Object user = userIdxs.get(currentUserIdx);
        Collection _userItems = model.getPreferencesFromUser(user);
        PreferenceIF pref = currentUserPrefs.get(currentUserPrefIdx);
        currentLine++;
        currentUserPrefIdx++;
        if (currentUserPrefIdx == userPrefs.get(currentUserIdx) && currentLine < totalLines){
            currentUserIdx++;
            currentUserPrefIdx = 0;
            currentUserPrefs = new ArrayList<PreferenceIF>(model.getPreferencesFromUser(userIdxs.get(currentUserIdx)));            
        }
        return pref.getUser() + "\t" + pref.getItem() + "\t" + pref.getValue() + "\t" + pref.getContext();
    }
 
    public static String printModel(ModelIF model){
        ModelPrintUtils util = new ModelPrintUtils(model);
        final String newline = System.getProperty("line.separator");
        
        logger.log(Level.INFO, "Initiating generation of data");
        StringBuilder builder = new StringBuilder();
        int n = 0;
        while (util.hasNext()){
            builder.append(util.next()).append(newline);
            if (++n%logLine == 0){
                logger.log(Level.INFO, "Generated {0} preferences", n);
            }
        }
        
        return builder.toString();
    }
    
    public static String printModel2(ModelIF model){
        ModelPrintUtils util = new ModelPrintUtils(model);
        final String newline = System.getProperty("line.separator");
        
        logger.log(Level.INFO, "Initiating generation of data");
        StringBuilder builder = new StringBuilder();
        int n = 0;
        for (Object user : model.getUsers()){
            if (n==0){
                DescriptionIF preference = (DescriptionIF)model.getPreferencesFromUser(user).toArray()[0];
                builder.append(preference.description()).append(newline);
            }
            for (Object preference : model.getPreferencesFromUser(user)){
                builder.append(preference.toString()).append(newline);
                if (++n%logLine == 0){
                    logger.log(Level.INFO, "Generated {0} preferences", n);
                }
            }
        }
        
        return builder.toString();
    }
    
    
    public static void toGz(ModelIF model, String file){
        ModelPrintUtils util = new ModelPrintUtils(model);
        final String newline = System.getProperty("line.separator");
        
        PrintStream ps;
        logger.log(Level.INFO, "Initiating generation of data file: {0}", file);
        try{
            //Opening
            ps=new PrintStream(new GZIPOutputStream(new FileOutputStream(file, false)));
            int n = 0;
            while (util.hasNext()){
                ps.println(util.next());
                if (++n%logLine == 0){
                    logger.log(Level.INFO, "Saved {0} preferences", n);
                }
            }                    
            ps.flush();
            ps.close();
        } catch (Exception e){
            System.err.println("Problem with printing to file: "+e);
            for (StackTraceElement ste:e.getStackTrace()) {
                System.err.println(ste);
            }
        }
    }
    
    public static void getModelfromGz(String file, boolean explicitRatings){
        ContinuousTimeModelReader reader=new ContinuousTimeModelReader<String,String,ContinuousTimeContextIF>();
        ContinuousTimeModelReader<String,String,ContinuousTimeContextIF> theReader=reader;
        theReader.setDelimiter("\t");
        theReader.setUserIndex(0);
        theReader.setItemIndex(1);
        theReader.setTimestampIndex(3);
        theReader.setTimestampFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        theReader.setContainsTitleLine(true);
        if (explicitRatings){
            theReader.setRatingIndex(2);
        }
        
    }
}
