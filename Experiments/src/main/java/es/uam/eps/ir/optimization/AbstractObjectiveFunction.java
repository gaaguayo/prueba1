package es.uam.eps.ir.optimization;

import es.uam.eps.ir.cars.bias.ModelBasedRecommender;
import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import es.uam.eps.ir.split.SplitIF;
import es.uam.eps.ir.metrics.MetricIF;
import es.uam.eps.ir.metrics.error.Metric_RMSE;
import es.uam.eps.ir.metrics.Recommendation;
import es.uam.eps.ir.metrics.RecommendationIF;
import es.uam.eps.ir.optimization.jtem.RealFunctionOfSeveralVariables;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.analysis.MultivariateFunction;
import utils.MailSender;

/**
 *
 * @author pedro
 */
public abstract class AbstractObjectiveFunction<U,I,C extends ContextIF> implements MultivariateFunction, RealFunctionOfSeveralVariables{
    protected SplitIF<U,I,C> split;
    protected ContextualModelUtils<U,I,C> eModel;
    protected int features;
    protected int bins;
    protected int executedIterations = 0;
    private final boolean SEND_EMAIL_REPORT = true;
    private int cores = Runtime.getRuntime().availableProcessors();
    
    private int maxIterations;
    protected double bestValue = Double.POSITIVE_INFINITY;
    protected int bestIteration = 1;
    
    public AbstractObjectiveFunction(SplitIF<U, I, C> split, ContextualModelUtils<U, I, C> eModel, int iterations, int features, int bins) {
        this.split = split;
        this.eModel = eModel;
        this.maxIterations = iterations;
        this.features = features;
        this.bins = bins;
        
    }
            
    public final double value(double[] p){
        for (double v:p){
            if (v <= 0.0) return Double.POSITIVE_INFINITY;
        }
        // required for storing best iteration
        double bestTrialValue = Double.POSITIVE_INFINITY;
        int bestTrialIteration = -1;
        ModelBasedRecommender<U,I,C> recommender = getRecommender(split, eModel, 1, p, features, bins);
        for (int iter = 1; iter <= maxIterations; iter++){
            recommender.trainModel();
            Map<U,List<RecommendationIF<I>>> recommendations = new HashMap<U, List<RecommendationIF<I>>>();
            for (U user:split.getTestingSet().getUsers()){
                List<RecommendationIF<I>> userRecommendations = new ArrayList<RecommendationIF<I>>();
                for (PreferenceIF<U,I,C> pref:split.getTestingSet().getPreferencesFromUser(user)){
                    I item = pref.getItem();
                    C context = pref.getContext();
                    Float prediction = recommender.predict(user, item, context);
                    RecommendationIF<I> recom;
                    if (!prediction.isNaN()){
                        recom = new Recommendation(item, prediction, true);
                        userRecommendations.add(recom);
                    }
                }
                recommendations.put(user, userRecommendations);
            }
            MetricIF<U, I, C> metric = new Metric_RMSE(split);
            metric.computeMetric(recommendations, null, null);
            double result = Double.POSITIVE_INFINITY;
            if ( ((Metric_RMSE)metric).getNumberOfEvaluations() > split.getTestingSet().getUsers().size() ){
                    result = metric.getResults().getResult();
            }
            if (result < bestTrialValue){
                bestTrialValue = result;
                bestTrialIteration = iter;
            }
        }
        if (bestTrialValue < bestValue){
            bestValue = bestTrialValue;
            bestIteration = bestTrialIteration;
        }
        
        StringBuilder msg = new StringBuilder();
        msg.append(new java.util.Date()).append(": Best RMSE (@iter:").append(bestTrialIteration).append(", ").append(++executedIterations).append(" trials exec.");
        msg.append(", [");
        for (double theP:p){
            msg.append(theP).append(",");
        }
        msg.append("])\t").append(bestTrialValue);
        System.out.println(msg);
        
        if (SEND_EMAIL_REPORT && executedIterations % 100 == 0){
            String computerName=null;
            try{
                computerName = InetAddress.getLocalHost().getHostName();
            } catch (java.net.UnknownHostException e){
                computerName="unknown";
            }
            StringBuilder header = new StringBuilder();
            StringBuilder body = new StringBuilder();
            header.append(computerName).append(" completed iteration ").append(executedIterations);
            body.append(header).append("\n\n");
            body.append(msg).append("\n\n");
            body.append("Recommender: ").append(recommender).append("\n\n");
            body.append("Cores: ").append(cores).append("\n\n");
            String[] mail=new String[]{header.toString(), body.toString()};
            MailSender.main(mail);
        }
        return bestValue;
    }

    public final double fullTrainingValue(double[] p){
        for (double v:p){
            if (v <= 0.0) return Double.POSITIVE_INFINITY;
        }
        ModelBasedRecommender<U,I,C> recommender = getRecommender(split, eModel, maxIterations, p, features, bins);
        recommender.trainModel();
        Map<U,List<RecommendationIF<I>>> recommendations = new HashMap<U, List<RecommendationIF<I>>>();
        for (U user:split.getTestingSet().getUsers()){
            List<RecommendationIF<I>> userRecommendations = new ArrayList<RecommendationIF<I>>();
            for (PreferenceIF<U,I,C> pref:split.getTestingSet().getPreferencesFromUser(user)){
                I item = pref.getItem();
                C context = pref.getContext();
                Float prediction = recommender.predict(user, item, context);
                RecommendationIF<I> recom;
                if (!prediction.isNaN()){
                    recom = new Recommendation(item, prediction, true);
                    userRecommendations.add(recom);
                }
            }
            recommendations.put(user, userRecommendations);
        }
        MetricIF<U, I, C> metric = new Metric_RMSE(split);
        metric.computeMetric(recommendations, null, null);
        double result = metric.getResults().getResult();
        StringBuilder msg = new StringBuilder();
        msg.append(new java.util.Date()).append(": Validation RMSE (iter ").append(++executedIterations);
        msg.append(", [");
        for (double theP:p){
            msg.append(theP).append(",");
        }
        msg.append("])\t").append(result);
        System.out.println(msg);
        
        if (SEND_EMAIL_REPORT && executedIterations % 100 == 0){
            String computerName=null;
            try{
                computerName = InetAddress.getLocalHost().getHostName();
            } catch (java.net.UnknownHostException e){
                computerName="unknown";
            }
            StringBuilder header = new StringBuilder();
            StringBuilder body = new StringBuilder();
            header.append(computerName).append(" completed iteration ").append(executedIterations);
            body.append(header).append("\n");
            body.append(msg).append("\n");
            body.append("Recommender: ").append(recommender).append("\n");
            body.append("Cores: ").append(cores).append("\n");
            String[] mail=new String[]{header.toString(), body.toString()};
            MailSender.main(mail);
        }
        return result;
    }
        
    protected abstract ModelBasedRecommender<U,I,C> getRecommender(SplitIF<U,I,C> split, ContextualModelUtils<U,I,C> eModel, int iterations, double[] p, int features, int bins);
    protected abstract double[] getStartPoint();
    
    public int getBestIteration(){
        return this.bestIteration;
    }
    
    // Methods from RealFunctionOfSeveralVariables
    public final double eval(double[] p){
        return value(p);
    }
    
    public final int getNumberOfVariables(){
        return getStartPoint().length;
    }
    
}
