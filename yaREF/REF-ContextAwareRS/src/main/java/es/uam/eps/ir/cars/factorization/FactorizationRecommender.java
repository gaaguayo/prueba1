package es.uam.eps.ir.cars.factorization;

import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.cars.bias.AbstractTrainingAlgorithm;
import es.uam.eps.ir.cars.bias.ModelBasedRecommender;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.rec.RecommenderIF;

/**
 *
 * @author Pedro G. Campos, pcampossoto@gmail.com
 */
public class FactorizationRecommender<U,I,C extends ContextIF> extends ModelBasedRecommender<U,I,C> implements RecommenderIF<U,I,C> {

    protected FactorizationRecommender(ModelIF<U,I,C> model){
        super(model);
    }
        
    // Single thread  MF
    public FactorizationRecommender(ModelIF<U,I,C> model, ContextualModelUtils<U,I,C> eModel, int nFeatures, double defaultValue, int iterationSteps, double lrate, double lambda, double lrStaticBias, double lambdaStaticBias, boolean WITH_BIAS, String type){
        super(model);
        this.eModel=eModel;
        //this.algorithm=new MF_ExpectationMaximizationAlgorithm(dataModel, eModel, nFeatures, defaultValue, iterationSteps, lrate, lambda, lambdaStaticBias, WITH_BIAS, minRating, maxRating);
        if (type.compareTo("EM")==0){
                this.algorithm=new MF_ExpectationMaximizationAlgorithm(model, eModel, nFeatures, defaultValue, iterationSteps, lrate, lambda, lrStaticBias, lambdaStaticBias, WITH_BIAS);
        }
        else if (type.compareTo("EM_Implicit")==0){
            this.algorithm=new EM_ImplicitAlgorithm(model, eModel, nFeatures, defaultValue, iterationSteps, lrate, lambda, lrStaticBias, lambdaStaticBias, WITH_BIAS);
        }
    }
    
    // Single thread MF (EM algorithm)
    public FactorizationRecommender(ModelIF<U,I,C> model, ContextualModelUtils<U,I,C> statistics,  int nFeatures, double defaultValue, int iterationSteps, double lrate, double lambda, double lrStaticBias, double lambdaStaticBias, boolean WITH_BIAS){
        this(model, statistics, nFeatures,  defaultValue, iterationSteps, lrate, lambda, lrStaticBias, lambdaStaticBias, WITH_BIAS, "EM");
    }
    
    // User Factors Time Aware MF
    public FactorizationRecommender(ModelIF<U,I,C> model, ContextualModelUtils<U,I,C> statistics,  int nFeatures, double defaultValue, int iterationSteps, double lrate, double lambda, double beta, double lrUserAlphaFactor, double lambdaUserAlphaFactor, double lrUserDayFactor, double lambdaUserDayFactor, boolean useFutureData){
        super(model);
        this.eModel=statistics;
        this.algorithm=new MF_UserFactorsTimeAwareAlgorithm(model, statistics, nFeatures, defaultValue, iterationSteps, lrate, lambda, beta, lrUserAlphaFactor, lambdaUserAlphaFactor, lrUserDayFactor, lambdaUserDayFactor, useFutureData);
    }
    
    
    // Static Bias and User Factors Time Aware MF
    public FactorizationRecommender(ModelIF<U,I,C> model, ContextualModelUtils<U,I,C> statistics,  int nFeatures, double defaultValue, int iterationSteps, double lrate, double lambda, double lrStaticBias, double lambdaStaticBias, double beta, double lrUserAlphaFactor, double lambdaUserAlphaFactor, double lrUserDayFactor, double lambdaUserDayFactor, boolean WITH_BIAS, boolean useFutureData){
        super(model);
        this.eModel=statistics;
        this.algorithm=new MF_StaticBiasAndUserFactorsTimeAwareAlgorithm(model, statistics, nFeatures, defaultValue, iterationSteps, lrate, lambda, lrStaticBias, lambdaStaticBias, beta, lrUserAlphaFactor, lambdaUserAlphaFactor, lrUserDayFactor, lambdaUserDayFactor, WITH_BIAS, useFutureData);
    }
    

    // Bias Time Aware MF
    public FactorizationRecommender(ModelIF<U,I,C> model, ContextualModelUtils<U,I,C> statistics,  int nFeatures, double defaultValue, int iterationSteps, double lrate, double lambda, double lrStaticBias, double lambdaStaticBias, double lrBinBias, double lambdaBinBias, double lrAlpha, double lambdaAlpha, double beta, double lrDayBias, double lambdaDayBias, boolean WITH_BIAS, int userBiasBins, int itemBiasBins, boolean useFutureData){
        super(model);
        this.eModel=statistics;
        this.algorithm=new MF_BiasTimeAwareAlgorithm(model, statistics, nFeatures, defaultValue, iterationSteps, lrate, lambda, lrStaticBias, lambdaStaticBias, lrBinBias, lambdaBinBias, lrAlpha, lambdaAlpha, beta, lrDayBias, lambdaDayBias, WITH_BIAS, userBiasBins, itemBiasBins, useFutureData);
    }
    
    // Full Time Aware MF
    public FactorizationRecommender(ModelIF<U,I,C> model, ContextualModelUtils<U,I,C> statistics,  int nFeatures, double defaultValue, int iterationSteps, double lrate, double lambda, double lrStaticBias, double lambdaStaticBias, double lrBinBias, double lambdaBinBias, double lrAlpha, double lambdaAlpha, double beta, double lrDayBias, double lambdaDayBias, double lrUserAlphaFactor, double lambdaUserAlphaFactor, double lrUserDayFactor, double lambdaUserDayFactor, boolean WITH_BIAS, int userBiasBins, int itemBiasBins, boolean useFutureData){
        super(model);
        this.eModel=statistics;
//        this.algorithm=new TimeBiasFactorAlgorithm(model, statistics, nFeatures, defaultValue, iterationSteps, lrate, lambda, lrStaticBias, lambdaStaticBias, lrBinBias, lambdaBinBias, lrAlpha, lambdaAlpha, beta, lrDayBias, lambdaDayBias, lrUserAlphaFactor, lambdaUserAlphaFactor, lrUserDayFactor, lambdaUserDayFactor, WITH_BIAS, userBiasBins, itemBiasBins, useFutureData);
        this.algorithm=new MF_FullTimeAwareAlgorithm(model, statistics, nFeatures, defaultValue, iterationSteps, lrate, lambda, lrStaticBias, lambdaStaticBias, lrBinBias, lambdaBinBias, lrAlpha, lambdaAlpha, beta, lrDayBias, lambdaDayBias, lrUserAlphaFactor, lambdaUserAlphaFactor, lrUserDayFactor, lambdaUserDayFactor, WITH_BIAS, userBiasBins, itemBiasBins, useFutureData);
    }
    
    
    // PARALLEL IMPLEMENTATIONS
    // Parallel MF
    public FactorizationRecommender(ModelIF<U,I,C> model, ContextualModelUtils<U,I,C> eModel, int nFeatures, double defaultValue, int iterationSteps, double lrate, double lambda, double lrStaticBias, double lambdaStaticBias, boolean WITH_BIAS, int maxThreads){
        super(model);
        this.eModel=eModel;
        this.algorithm=(AbstractTrainingAlgorithm)new MF_ParallelExpectationMaximizationAlgorithm(model, eModel, nFeatures, defaultValue, iterationSteps, lrate, lambda, lrStaticBias, lambdaStaticBias, WITH_BIAS, maxThreads);
//        this.algorithm=new ParallelRechtReExpectationMaximizationAlgorithm(model, eModel, nFeatures, defaultValue, iterationSteps, lrate, lambda, lrStaticBias, lambdaStaticBias, WITH_BIAS);
    }
    
    // Parallel User Factors Time Aware MF
    public FactorizationRecommender(ModelIF<U,I,C> model, ContextualModelUtils<U,I,C> statistics,  int nFeatures, double defaultValue, int iterationSteps, double lrate, double lambda, double beta, double lrUserAlphaFactor, double lambdaUserAlphaFactor, double lrUserDayFactor, double lambdaUserDayFactor, boolean useFutureData, int maxThreads){
        super(model);
        this.eModel=statistics;
        this.algorithm=new MF_ParallelUserFactorsTimeAwareAlgorithm(model, statistics, nFeatures, defaultValue, iterationSteps, lrate, lambda, beta, lrUserAlphaFactor, lambdaUserAlphaFactor, lrUserDayFactor, lambdaUserDayFactor, useFutureData, maxThreads);
    }
    
    // Parallel Static Bias and User Factors Time Aware MF
    public FactorizationRecommender(ModelIF<U,I,C> model, ContextualModelUtils<U,I,C> statistics,  int nFeatures, double defaultValue, int iterationSteps, double lrate, double lambda, double lrStaticBias, double lambdaStaticBias, double beta, double lrUserAlphaFactor, double lambdaUserAlphaFactor, double lrUserDayFactor, double lambdaUserDayFactor, boolean WITH_BIAS, boolean useFutureData, int maxThreads){
        super(model);
        this.eModel=statistics;
        this.algorithm=new MF_ParallelStaticBiasAndUserFactorsTimeAwareAlgorithm(model, statistics, nFeatures, defaultValue, iterationSteps, lrate, lambda, lrStaticBias, lambdaStaticBias, beta, lrUserAlphaFactor, lambdaUserAlphaFactor, lrUserDayFactor, lambdaUserDayFactor, WITH_BIAS, useFutureData, maxThreads);
    }
    
    // Bias Time Aware parallel MF
    public FactorizationRecommender(ModelIF<U,I,C> model, ContextualModelUtils<U,I,C> statistics,  int nFeatures, double defaultValue, int iterationSteps, double lrate, double lambda, double lrStaticBias, double lambdaStaticBias, double lrBinBias, double lambdaBinBias, double lrAlpha, double lambdaAlpha, double beta, double lrDayBias, double lambdaDayBias, boolean WITH_BIAS, int userBiasBins, int itemBiasBins, boolean useFutureData, int maxThreads){
        super(model);
        this.eModel=statistics;
        this.algorithm=new MF_ParallelBiasTimeAwareAlgorithm(model, statistics, nFeatures, defaultValue, iterationSteps, lrate, lambda, lrStaticBias, lambdaStaticBias, lrBinBias, lambdaBinBias, lrAlpha, lambdaAlpha, beta, lrDayBias, lambdaDayBias, WITH_BIAS, userBiasBins, itemBiasBins, useFutureData, maxThreads);
    }
    
    // Full Time Aware parallel MF
    public FactorizationRecommender(ModelIF<U,I,C> model, ContextualModelUtils<U,I,C> statistics,  int nFeatures, double defaultValue, int iterationSteps, double lrate, double lambda, double lrStaticBias, double lambdaStaticBias, double lrBinBias, double lambdaBinBias, double lrAlpha, double lambdaAlpha, double beta, double lrDayBias, double lambdaDayBias, double lrUserAlphaFactor, double lambdaUserAlphaFactor, double lrUserDayFactor, double lambdaUserDayFactor, boolean WITH_BIAS, int userBiasBins, int itemBiasBins, boolean useFutureData, int maxThreads){
        super(model);
        this.eModel=statistics;
        this.algorithm=new MF_ParallelFullTimeAwareAlgorithm(model, statistics, nFeatures, defaultValue, iterationSteps, lrate, lambda, lrStaticBias, lambdaStaticBias, lrBinBias, lambdaBinBias, lrAlpha, lambdaAlpha, beta, lrDayBias, lambdaDayBias, lrUserAlphaFactor, lambdaUserAlphaFactor, lrUserDayFactor, lambdaUserDayFactor, WITH_BIAS, userBiasBins, itemBiasBins, useFutureData, maxThreads);
    }
    

    @Override
    public String toString(){
        return this.algorithm.toString();
    }
}
