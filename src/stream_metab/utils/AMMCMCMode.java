package stream_metab.utils;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import neo.ModelBase;

import org.apache.commons.math.distribution.AbstractContinuousDistribution;
import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.RealMatrix;
import org.neosimulation.apps.modelmanager.neov1.*;
import org.neosimulation.apps.modelmanager.neov1.strategy.metaoutput.*;
import org.neosimulation.apps.modelmanager.neov1.strategy.metaparam.*;
import org.neosimulation.apps.rinterface.Command;
import org.neosimulation.apps.rinterface.RConsole;
import org.neosimulation.apps.simmanager.analysis.inverse.ammcmc.*;
import org.neosimulation.apps.simmanager.analysis.inverse.likelihood.LikelihoodCalculator;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Simulation mode for adaptive Metropolis Markov Chain Monte Carlo parameter space analysis
 * 
 * @author robert.payn
 *
 */
public class AMMCMCMode implements Mode {

    /**
     * Execute the AMMCMC simulation mode
     */
//    @Override
    public void runMode(ModelBase neoModelBase, String workingDir, ConfigElement modeElem) throws Exception
    {        
        RConsole console = null;
        
        // create the simulation manager
        ConfigElement ammcmcElem = modeElem.getFirstElementByTag("AMMCMC");
        NEOModelManager modelManager = new NEOModelManager(
                new File(workingDir), 
                ammcmcElem.getElement().getAttribute("execPath"),
                ammcmcElem.getElement().getAttribute("dbName"), 
                ammcmcElem.getElement().getAttribute("mainConfigName"), 
                ammcmcElem.getElement().getAttribute("outputConfigName"),
                ammcmcElem.getElement().getAttribute("runEnvironment"),
                neoModelBase);
        
        // get the river manager properties
        String outputDir = ammcmcElem.getTextContentForFirstTag("outputDirectory");

        AdaptiveMetMCMC<NEOModelManager> analysis = null;
        
        if (Boolean.valueOf(ammcmcElem.getElement().getAttribute("active")))
        {
            // Create the analysis
            
            // Set up likelihood calculations
            ConfigElement likelihoodElem = ammcmcElem.getFirstElementByTag("likelihood");

            double minTime = Double.NEGATIVE_INFINITY;
            double maxTime = Double.POSITIVE_INFINITY;
            String prop = likelihoodElem.getElement().getAttribute("timestart");
            if (!prop.equals(""))
            {
                minTime = Double.valueOf(prop);
            }
            prop = likelihoodElem.getElement().getAttribute("timestop");
            if (!prop.equals(""))
            {
                maxTime = Double.valueOf(prop);
            }
            
            TimeSeriesMatrix likelihoodDataExtractor = new TimeSeriesMatrix(minTime, maxTime);
            
            String typeName = likelihoodElem.getElement().getAttribute("type");
            LikelihoodCalculator likelihoodCalculator = 
                    AdaptiveMetMCMC.getLikelihoodCalculator(typeName, likelihoodElem.getElement());
            
            int outColumn = 2;
            NodeList list = likelihoodElem.getElement().getElementsByTagName("predicted");
            for (int i = 0; i < list.getLength(); i++)
            {
                ConfigElement elem = new ConfigElement((Element)list.item(i));
                String name = elem.getElement().getAttribute("name");
                likelihoodDataExtractor.addState(
                        elem.getTextContentForFirstTag("filename"), 
                        elem.getTextContentForFirstTag("holon"), 
                        elem.getTextContentForFirstTag("state"));
                likelihoodCalculator.addPrediction(name, outColumn);
                outColumn++;
            }
            
            int totalObsColumns = 0;
            list = likelihoodElem.getElement().getElementsByTagName("observed");
            boolean runModelForObservations = false;
            LinkedHashMap<String, Integer> obsCols = new LinkedHashMap<String, Integer>();
            Integer fileLength = null;
            for (int i = 0; i < list.getLength(); i++)
            {
                ConfigElement elem = new ConfigElement((Element)list.item(i));
                totalObsColumns++;
                if (elem.getElement().getAttribute("location").equals("output"))
                {
                    runModelForObservations = true;
                    String name = elem.getElement().getAttribute("name");
                    likelihoodDataExtractor.addState(
                            elem.getTextContentForFirstTag("filename"), 
                            elem.getTextContentForFirstTag("holon"), 
                            elem.getTextContentForFirstTag("state"));
                    obsCols.put(name, outColumn);
                    outColumn++;
                }
                else if(elem.getElement().getAttribute("location").equals("file"))
                {
                    BufferedReader reader = new BufferedReader(
                            new FileReader(
                                    new File(workingDir + File.separator + elem.getTextContentForFirstTag("filename"))));
                    String[] headers = reader.readLine().split(",");
                    HashMap<String, Integer> headerMap = new HashMap<String, Integer>();
                    for (int j = 0; j < headers.length; j++)
                    {
                        headerMap.put(headers[j], j);
                    }
                    ArrayList<Double> data = new ArrayList<Double>();
                    int counter = 0;
                    while(reader.ready())
                    {
                        String[] line = reader.readLine().split(",");
                        if (line.length == headerMap.size())
                        {
                            data.add(Double.valueOf(
                                    line[headerMap.get(elem.getElement().getAttribute("name"))]));
                            counter++;
                        }
                    }
                    if (fileLength != null && counter != fileLength)
                    {
                        reader.close();
                        throw new Exception("Mismatch in observed data length.");
                    }
                    else
                    {
                        fileLength = counter;
                    }
                    reader.close();
                }
                else
                {
                    throw new Exception("Invalid observation entry.");
                }
            }
            
            AMMCMCStreamMetabModel model = new AMMCMCStreamMetabModel(likelihoodDataExtractor);
            
            RealMatrix outMatrix = null;
            if (runModelForObservations)
            {
                model.setModelManager(modelManager);
                outMatrix = model.execute();
                if (fileLength != null && fileLength != outMatrix.getRowDimension())
                {
                    throw new Exception("Observation lengths do not match");
                }
                else
                {
                    fileLength = outMatrix.getRowDimension();
                }
            }
            RealMatrix observationsMatrix = MatrixUtils.createRealMatrix(fileLength, totalObsColumns);
            
            int obsColumn = 0;
            for (Entry<String, Integer> entry: obsCols.entrySet())
            {
                observationsMatrix.setColumn(obsColumn, outMatrix.getColumn(entry.getValue()));
                likelihoodCalculator.addObservation(entry.getKey(), obsColumn);
                obsColumn++;
            }
            for (int i = 0; i < list.getLength(); i++)
            {
                ConfigElement elem = new ConfigElement((Element)list.item(i));
                if(elem.getElement().getAttribute("location").equals("file"))
                {
                    BufferedReader reader = new BufferedReader(
                            new FileReader(
                                    new File(workingDir + File.separator + elem.getTextContentForFirstTag("filename"))));
                    String[] headers = reader.readLine().split(",");
                    HashMap<String, Integer> headerMap = new HashMap<String, Integer>();
                    for (int j = 0; j < headers.length; j++)
                    {
                        headerMap.put(headers[j], j);
                    }
                    int row = 0;
                    while(reader.ready())
                    {
                        String[] line = reader.readLine().split(",");
                        if (line.length == headerMap.size())
                        {
                            observationsMatrix.setEntry(
                                    row, 
                                    obsColumn, 
                                    Double.valueOf(line[headerMap.get(elem.getElement().getAttribute("name"))]));
                            row++;
                        }
                    }
                    likelihoodCalculator.addObservation(elem.getElement().getAttribute("name"), obsColumn);
                    reader.close();
                    
                    obsColumn++;
                }
            }
            likelihoodCalculator.setObservationMatrix(observationsMatrix);
            
            HashMap<String,Text> estimatedParamMap = new HashMap<String,Text>();
            
            // Set up estimated parameters
            NodeList parameterList = ammcmcElem.getElement().getElementsByTagName("parameter");
            for (int i = 0; i < parameterList.getLength(); i++)
            {
                ConfigElement parameterElem = new ConfigElement((Element)parameterList.item(i));
                String parameterName = parameterElem.getElement().getAttribute("name");
                String priorName = parameterElem.getElement().getAttribute("prior");
                if (priorName == "")
                {
                    priorName = "UniformDistribution";
                }
                AbstractContinuousDistribution dist = AdaptiveMetMCMCParam.getDistribution(priorName, parameterElem.getElement());
                if (parameterElem.getElement().getAttribute("estimate").isEmpty() ||
                        Boolean.valueOf(parameterElem.getElement().getAttribute("estimate")))
                {
                    model.addEstimatedParameter(
                            new UniformlyAppliedDouble(
                                    parameterName,
                                    parameterElem.getTextContentForFirstTag("behavior"), 
                                    parameterElem.getTextContentForFirstTag("state"), 
                                    Double.valueOf(parameterElem.getTextContentForFirstTag("initialValue"))),
                            Double.valueOf(parameterElem.getTextContentForFirstTag("initialVariance")),
                            dist);
                }
                else
                {
                    model.addKnownParameter(
                            new UniformlyAppliedDouble(
                                    parameterName,
                                    parameterElem.getTextContentForFirstTag("behavior"), 
                                    parameterElem.getTextContentForFirstTag("state"), 
                                    Double.valueOf(parameterElem.getTextContentForFirstTag("initialValue"))),
                            dist);
                }
                
                if (parameterElem.getElement().hasAttribute("configpath"))
                {
                    String[] configPath = parameterElem.getElement().getAttribute("configpath").split(":");
                    ConfigElement element = modeElem;
                    for (String elementString: configPath)
                    {
                        element = element.getFirstElementByTag(elementString);
                    }
                    element = element.getFirstElementByTag(parameterName);
                    estimatedParamMap.put(parameterName, (Text)element.getElement().getFirstChild());
                }
            }
            
            // Create the analysis and launch it
            analysis = new AdaptiveMetMCMC<NEOModelManager>(model, likelihoodCalculator, 
                    ammcmcElem.getElement(), outputDir);
            
            modelManager.launchAnalysis(analysis);
            while (!analysis.isDone())
            {
                Thread.sleep(1000);
            }
            
            // Run model
            int i = 0;
            double[] optValues = analysis.getHistory().getOptParameters();
            modeElem.getElement().setAttribute("mode", "RiverBuilder");
            for (Entry<String,AdaptiveMetMCMCParam> param: model.createEstimatedParameterList().entrySet())
            {
                if (estimatedParamMap.containsKey(param.getKey()))
                {
                    estimatedParamMap.get(param.getKey()).replaceWholeText(Double.toString(optValues[i]));
                }
                i++;
            }
            Transformer xformer = TransformerFactory.newInstance().newTransformer();
            xformer.transform(
                    new DOMSource(modeElem.getElement().getOwnerDocument()), 
                    new StreamResult(new File(workingDir + File.separator + 
                            outputDir + File.separator + "optimum.xml")));
            
            RiverFactoryMode riverFactory = new RiverFactoryMode();
            riverFactory.runMode(neoModelBase, workingDir, modeElem);

            console = new RConsole();
            if (riverFactory.getReport() != null)
            {
                console.assign("parAverage", riverFactory.getReport().getPARAverage());
            }
        }
        
        String workspaceName = ammcmcElem.getTextContentForFirstTag("rWorkspaceName");
        //AdaptiveMetMCMCHistory history;
        if (analysis != null)
        {
            NodeList rVarList = ammcmcElem.getElement().getElementsByTagName("rVariable");
            for (int i = 0; i < rVarList.getLength(); i++)
            {
                Element rVarElem = (Element)rVarList.item(i);
                console.assign(rVarElem.getAttribute("name"), rVarElem.getTextContent());
            }
            analysis.createImage(console, workspaceName);
            //history = analysis.getHistory();
        }
        else
        {
            console = new RConsole();
            String fullPath = modelManager.getOutputManager().getBaseLocation() + File.separator + outputDir + 
                    File.separator + workspaceName;
            Command.executeCommand(console, null, "load", "file = '" + fullPath.replace("\\", "/") + "'");
            //history = new AdaptiveMetMCMCHistory(workingDir + File.separator + 
            //        outputDir + File.separator + "history" + File.separator);
            console.close();
        }
        
        
    }

}
