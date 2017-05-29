package stream_metab.utils;

import java.io.*;

import org.neosimulation.apps.modelmanager.neov1.*;
import org.neosimulation.apps.modelmanager.neov1.tools.*;
import org.neosimulation.apps.simmanager.*;
import org.w3c.dom.Element;

/**
 * Report metaoutput for an execution of the stream metabolism model 
 * as run by the river builder
 * 
 * @author robert.payn
 *
 */
public class StreamMetabReport extends MetaOutputGenerator<NEOModelManager,File> {
    
    /**
     * Conversion factor for model time to days
     */
    public static final double TIME_CONVERTER = 86400;
    
    /**
     * Key for name of output file with air behavior states
     */
    public static final String KEY_AIR_OUTPUT_FILE = "airFile";
    
    /**
     * Key for start time of PAR integral
     */
    public static final String KEY_PAR_START_TIME = "start";
    
    /**
     * Key for stop time of PAR integral
     */
    public static final String KEY_PAR_STOP_TIME = "stop";
    
    /**
     * Directory where report will be written
     */
    private String dir;
    
    /**
     * Properties for the report
     */
    private ConfigElement element;
    
    /**
     * Daily average PAR (typically integrated over a photoperiod)
     */
    private double parAverage;
    
    /**
     * Daily average GPP
     */
    private double gppAverage;
    
    /**
     * Create a new instance with given fields
     * 
     * @param dir directory for output
     * @param element properties of report
     */
    public StreamMetabReport(String dir, ConfigElement element)
    {
        this.dir = dir;
        this.element = element;
    }

    /**
     * Extract the data for the report from the output
     */
    @Override
    public void extractOutput(NEOModelManager modelManager) throws Exception
    {
        OutputFile airFile = modelManager.getOutputManager().getOutputFile(
                element.getFirstElementByTag("build").getFirstElementByTag("NEO")
                .getTextContentForFirstTag(KEY_AIR_OUTPUT_FILE));
        TimeSeries parTimeSeries = airFile.getTimeSeries("air", "instpar");
        Element parElem = element.getFirstElementByTag("report")
                .getFirstElementByTag("parintegrate").getElement();
        parAverage = parTimeSeries.integral(
                "instpar",
                Double.valueOf(parElem.getAttribute(KEY_PAR_START_TIME)),
                Double.valueOf(parElem.getAttribute(KEY_PAR_STOP_TIME)))
                / 86400;
        gppAverage = parAverage * Double.valueOf(
                element.getFirstElementByTag("build").getFirstElementByTag("DO")
                .getTextContentForFirstTag(RiverFactory.PROP_SUBKEY_P_TO_PAR_RATIO));
        writeOutput(modelManager);
    }
    
    /**
     * Getter for daily average PAR
     * 
     * @return daily average PAR
     */
    public double getPARAverage()
    {
        return parAverage;
    }
    
    /**
     * Write the output
     * 
     * @param modelManager model manager with the output location
     * @throws Exception if error in writing
     */
    public void writeOutput(NEOModelManager modelManager) throws Exception
    {
        BufferedWriter writer = modelManager.getOutputManager()
                .createTextWriter(dir + File.separator + "report.html");
        HTMLFactory.openGeneric(writer);
        writer.write("<ul type=none>");
        writer.newLine();
        writer.write("<li>Daily average PAR = " + Double.toString(parAverage * TIME_CONVERTER)
                + " {PAR units} m<sup><small>-2</small></sup> day<sup><small>-1</small></sup></li>");
        writer.newLine();
        writer.write("<li>Daily average GPP = " + Double.toString(gppAverage * TIME_CONVERTER)
                + " g m<sup><small>-2</small></sup> day<sup><small>-1</small></sup></li>");
        writer.newLine();
        double rAverage = Double.valueOf(
                element.getFirstElementByTag("build").getFirstElementByTag("DO")
                .getTextContentForFirstTag(RiverFactory.PROP_SUBKEY_DAILY_RESP));
        writer.write("<li>Daily average R = " + Double.toString(rAverage * TIME_CONVERTER)
                + " g m<sup><small>-2</small></sup> day<sup><small>-1</small></sup></li>");
        HTMLFactory.closeGeneric(writer);
        writer.close();
        
        File file = new File(dir + File.separator + "parAverage.txt");
        writer = new BufferedWriter(new FileWriter(file));
        writer.write(Double.toString(parAverage));
        writer.newLine();
        writer.close();
    }

}
