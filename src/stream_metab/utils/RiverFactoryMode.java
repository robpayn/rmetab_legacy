package stream_metab.utils;

import java.io.*;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import neo.*;
import org.neosimulation.apps.modelmanager.neov1.*;
import org.neosimulation.apps.modelmanager.neov1.strategy.metaparam.*;
import org.neosimulation.apps.modelmanager.neov1.tools.*;
import org.w3c.dom.Element;

/**
 * Run mode for the river builder
 * 
 * @author robert.payn
 *
 */
public class RiverFactoryMode implements Mode {
    
    /**
     * Report from the model run
     */
    private StreamMetabReport report;
    
    /**
     * Getter for the report
     * 
     * @return report
     */
    public StreamMetabReport getReport()
    {
        return report;
    }

    /**
     * Execute the river builder mode
     */
    @Override
    public void runMode(ModelBase neoModelBase, String workingDir, ConfigElement modeElem) throws Exception
    {
        // Create the model manager
        ConfigElement buildElem = modeElem.getFirstElementByTag("build");
        NEOModelManager modelManager = new NEOModelManager(
                new File(workingDir), 
                buildElem.getElement().getAttribute("dbName"), 
                buildElem.getElement().getAttribute("mainConfigName"), 
                buildElem.getElement().getAttribute("outputConfigName"),
                buildElem.getElement().getAttribute("runEnvironment"),
                neoModelBase);

        // Get the builder properties and execute if enabled
        ConfigElement switchElem = modeElem.getFirstElementByTag("masterswitches");
        if (Boolean.valueOf(switchElem.getTextContentForFirstTag("buildModel")))
        {
            System.out.println("Building the river model.");
            new RiverFactory(
                    "RiverMeta", 
                    buildElem).applyParam(modelManager);
        }
        
        if (Boolean.valueOf(switchElem.getTextContentForFirstTag("getData")))
        {
            System.out.println("Retreiving input data.");
            new DataExtractor(modeElem.getFirstElementByTag("input")).run(modelManager);
        }
        
        if (Boolean.valueOf(switchElem.getTextContentForFirstTag("init")))
        {
            System.out.println("Initializing the river model.");
            new Serializer("serial", modeElem.getFirstElementByTag(
                    "initialization").getElement()).applyParam(modelManager);
        }

        // run the model if enabled
        if (Boolean.valueOf(switchElem.getTextContentForFirstTag("runModel")))
        {
            // run the model
            modelManager.getModelSpawner().runModel();
        }
        
        if (Boolean.valueOf(switchElem.getTextContentForFirstTag("write")))
        {
            Element reportElem = modeElem.getFirstElementByTag("report").getElement();
            String outputDir = reportElem.getAttribute("dir");
            report = new StreamMetabReport(outputDir, modeElem);
            report.extractOutput(modelManager);
            
            Transformer xformer = TransformerFactory.newInstance().newTransformer();
            xformer.transform(
                    new DOMSource(modeElem.getElement().getOwnerDocument()), 
                    new StreamResult(new File(workingDir + File.separator + 
                            outputDir + File.separator + "last.xml")));
        }
        
        if (Boolean.valueOf(switchElem.getTextContentForFirstTag("aggregateOutput")))        
        {
            new TimeSeriesGenerator(modeElem.getFirstElementByTag("aggregator").getElement()).extractOutput(modelManager);
        }
        
    }

}
