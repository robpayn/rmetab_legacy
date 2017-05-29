package stream_metab;

import java.io.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import neo.*;
import stream_metab.utils.*;

/**
 * Executes a single stream metabolism model
 * 
 * @author robpayn
 */
public class StreamMetab extends ModelBase implements Runnable {
    
    /**
     * Map of run modes available for this model
     */
    public static final HashMap<String,Class<? extends Mode>> modeMap;
    static
    {
        modeMap = new HashMap<String,Class<? extends Mode>>();
        modeMap.put("AMMCMC", AMMCMCMode.class);
        modeMap.put("RiverBuilder", RiverFactoryMode.class);
    }

    /**
     * Command line key for the main ini file
     */
    public static final String KEY_MAIN = "ini";

    /**
     * Command line key for manager properties
     */
    public static final String KEY_SIM_ENV = "simenv";

    /**
     * Array of command line arguments
     */
    private String[] args;
    
    /**
     * Map of command line properties
     */
    private HashMap<String,String> cmdMap; 
    
    /**
     * Working directory
     */
    private String workingDir;

    /**
     * Constructs an instance of the model by instantiated
     * a NEO framework.
     * 
     * @param args command line arguments from an entry point
     */
    public StreamMetab(String[] args)
    {
        this.args = args;
        workingDir = System.getProperty("user.dir");
        cmdMap = new HashMap<String, String>();
        for (String arg : args)
        {
            String[] sides = arg.split("=");
            if (sides.length == 2)
            {
                cmdMap.put(sides[0], sides[1]);
            }
            else if (sides.length == 1)
            {
                cmdMap.put(sides[0], "");
            }
        }
    }
    
    /**
     * Entry point for running the model from a command line
     * 
     * @param args command line arguments
     */
    public static void main(String[] args)
    {
        // start timer
        long startTime = System.currentTimeMillis();
        
        // instantiate this class and run processors and model
        StreamMetab streamMetabModel = new StreamMetab(args);
        streamMetabModel.run();
        
        // report overall runtime
        long elapsedTime = System.currentTimeMillis() - startTime;
        long days = elapsedTime / 86400000;
        elapsedTime = elapsedTime - days * 86400000;
        long hours = elapsedTime / 3600000;
        elapsedTime = elapsedTime - hours * 3600000;
        double minutes = elapsedTime / 60000.0;
        System.out.println("Total time: " + Long.toString(days) + " days " + Long.toString(hours) + " hours "
                + Double.toString(minutes) + " minutes");
    }
    
    /**
     * Builds and executes the NEO model
     */
    @Override
    public void run()
    {
        if (!cmdMap.containsKey(KEY_SIM_ENV) && !cmdMap.containsKey(KEY_MAIN))
        {
            System.out.println("Manager properties or model initialization file not specified.  " +
            		"No models executed.");
        }
        else if (cmdMap.containsKey(KEY_MAIN))
        {
            try
            {
                NEO neoFramework = new NEO(this, args);
                neoFramework.buildModel();
                neoFramework.execute();
            }
            catch (RuntimeException e)
            {
                int errorCount = 0;
                File errorCountFile = new File(workingDir + File.separator + "error_count.txt");
                try
                {
                    BufferedReader errorReader = new BufferedReader(new FileReader(errorCountFile));
                    errorCount = 1 + Integer.valueOf(errorReader.readLine());
                    errorReader.close();
                }
                catch (Exception e1)
                {
                    errorCount = 1;
                }
                try
                {
                    BufferedWriter errorWriter = new BufferedWriter(new FileWriter(errorCountFile));
                    errorWriter.write(Integer.toString(errorCount));
                    errorWriter.newLine();
                    errorWriter.close();
                    
                    errorWriter = new BufferedWriter(new FileWriter(new File(workingDir + File.separator + "errors.txt"), true));
                    errorWriter.newLine();
                    errorWriter.write(Integer.toString(errorCount));
                    errorWriter.newLine();
                    e.printStackTrace(new PrintWriter(errorWriter));
                    errorWriter.close();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
        }
        else
        {
            ConfigElement managerElement = null;
            try
            {
                 Document xml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                        workingDir + File.separator + cmdMap.get(KEY_SIM_ENV) + ".xml");
                 managerElement = new ConfigElement(xml.getDocumentElement());
            }
            catch (Exception t)
            {
                System.out.println("Problem loading manager XML file.  Model not executed.");
                t.printStackTrace();
                System.exit(1);
            }
            
            if (managerElement != null)
            {
                try
                {
                    Mode mode = modeMap.get(managerElement.getElement().getAttribute("mode")).newInstance();
                    if (mode != null)
                    {
                        mode.runMode(this, workingDir, managerElement);
                    }
                    else
                    {
                        System.out.println("Unrecognized mode or execution is disabled. No models executed.");
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

}
