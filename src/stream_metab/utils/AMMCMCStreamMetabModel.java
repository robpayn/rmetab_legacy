package stream_metab.utils;

import java.io.*;
import java.util.*;
import java.util.Map.*;
import org.apache.commons.math.distribution.*;
import org.apache.commons.math.linear.*;
import org.neosimulation.apps.modelmanager.neov1.*;
import org.neosimulation.apps.simmanager.MetaOutputGenerator;
import org.neosimulation.apps.simmanager.MetaParamApplicator;
import org.neosimulation.apps.simmanager.SimulationConfig;
import org.neosimulation.apps.simmanager.analysis.inverse.ammcmc.*;

/**
 * Implements a stream metabolism model in the context necessary for an
 * Adaptive Metropolis MCMC analysis
 * 
 * @author robpayn
 *
 */
public class AMMCMCStreamMetabModel extends AdaptiveMetMCMCModel<NEOModelManager> {
    
    /**
     * Map of estimated parameters
     */
    private LinkedHashMap<String,AdaptiveMetMCMCUniformMetaParam<NEOModelManager>> parametersEstimated;
    
    /**
     * Map of known parameters
     */
    private LinkedHashMap<String,AdaptiveMetMCMCUniformMetaParam<NEOModelManager>> parametersKnown;

    /**
     * Create a new instance of the model with the specified manager
     * 
     * @param extractor metaoutput generator used to extract output for AMMCMC algorithm
     */
    public AMMCMCStreamMetabModel(MetaOutputGenerator<NEOModelManager, RealMatrix> extractor)
    {
        super(extractor);
        parametersEstimated = new LinkedHashMap<String, AdaptiveMetMCMCUniformMetaParam<NEOModelManager>>();
        parametersKnown = new LinkedHashMap<String, AdaptiveMetMCMCUniformMetaParam<NEOModelManager>>();
    }
    
    /**
     * Add an estimated parameter to the model
     */
    @Override
    public void addEstimatedParameter(MetaParamApplicator<Double, NEOModelManager> param, double initVariance,
            AbstractContinuousDistribution samplingDist)
    {
        parametersEstimated.put(param.getParamID(), 
                new AdaptiveMetMCMCUniformMetaParam<NEOModelManager>(param, initVariance, samplingDist));
    }

    /**
     * Add a known parameter to the model
     */
    @Override
    public void addKnownParameter(MetaParamApplicator<Double, NEOModelManager> param,
            AbstractContinuousDistribution samplingDist)
    {
        parametersKnown.put(param.getParamID(), 
                new AdaptiveMetMCMCUniformMetaParam<NEOModelManager>(param, 0, samplingDist));
    }

    /**
     * Execute a stream metabolism model and return output relevant to the Adaptive Metropolis
     * MCMC analysis
     * @throws Exception if error in execution
     */
    @Override
    public RealMatrix execute() throws Exception
    {
        // create the run configuration
        SimulationConfig<NEOModelManager> runConfig = new SimulationConfig<NEOModelManager>("MCMCiteration");
        for (Entry<String, AdaptiveMetMCMCUniformMetaParam<NEOModelManager>> param: parametersEstimated.entrySet())
        {
            runConfig.addMetaParamApplicator(param.getValue().getParam());
        }
        for (Entry<String, AdaptiveMetMCMCUniformMetaParam<NEOModelManager>> param: parametersKnown.entrySet())
        {
            runConfig.addMetaParamApplicator(param.getValue().getParam());
        }
        
        // spawn the model and run the output extractor 
        modelManager.getModelSpawner().runModelConfigPermute(runConfig);
        extractor.extractOutput(modelManager);
        return getOutput();
    }
    
    /**
     * Get the directory where output should be written
     */
    @Override
    public File getOutputDirectory()
    {
        return modelManager.getOutputManager().getBaseLocation();
    }

    /**
     * Get a map of the Adaptive Metropolis MCMC parameters to estimate
     */
    @Override
    public LinkedHashMap<String, AdaptiveMetMCMCParam> createEstimatedParameterList()
    {
        LinkedHashMap<String, AdaptiveMetMCMCParam> map = new LinkedHashMap<String, AdaptiveMetMCMCParam>();
        for (Entry<String, AdaptiveMetMCMCUniformMetaParam<NEOModelManager>> param: parametersEstimated.entrySet())
        {
            map.put(param.getKey(), param.getValue());
        }
        return map;
    }

    /**
     * Get a map of the known Adaptive Metropolis MCMC parameters
     */
    @Override
    public LinkedHashMap<String, AdaptiveMetMCMCParam> createKnownParameterList()
    {
        LinkedHashMap<String, AdaptiveMetMCMCParam> map = new LinkedHashMap<String, AdaptiveMetMCMCParam>();
        for (Entry<String, AdaptiveMetMCMCUniformMetaParam<NEOModelManager>> param: parametersKnown.entrySet())
        {
            map.put(param.getKey(), param.getValue());
        }
        return map;
    }

}
