package data;

import java.io.File;
import java.util.*;

import org.w3c.dom.Element;

import data.doprobe.DOProbeDBImporter;
import data.envdb.EnvironDatabase;
import data.glencanyondam.GlenCanyonDamFlowImporter;
import data.leesferry.LeesFerryFlowImporter;
import data.simimporter.SimulationImporter;

/**
 * Importer for a metabolism database
 * 
 * @author robpayn
 *
 */
public abstract class DBImporter {
    
    /**
     * Environmental database for imported data
     */
    protected EnvironDatabase environDB;
    
    /**
     * XML element with information for the importer
     */
    protected Element importerElem;
    
    /**
     * Properties for the importer
     */
    protected Properties props;
    
    /**
     * Base key for the properties applicable to this importer
     */
    protected String baseKey;
    
    /**
     * Working directory
     */
    protected File workingDir;

    /**
     * Map of available importers
     */
    public static final HashMap<String,Class<? extends DBImporter>> IMPORTER_MAP = 
        new HashMap<String,Class<? extends DBImporter>>();
    static
    {
        IMPORTER_MAP.put("DOProbe", DOProbeDBImporter.class);
        IMPORTER_MAP.put("GlenCanyonDamFlow", GlenCanyonDamFlowImporter.class);
        IMPORTER_MAP.put("LeesFerryFlow", LeesFerryFlowImporter.class);
        IMPORTER_MAP.put("Simulation", SimulationImporter.class);
    }
    
    /**
     * Import data to the database
     * 
     * @throws Exception if error in the import source
     */
    public abstract void importData() throws Exception;

    /**
     * Initialize the fields common to all DB importers
     * 
     * @param workingDir working directory
     * @param environDB environmental database for import
     * @param importerElem
     *      XML element with information needed to run the importer
     */
    public void initialize(File workingDir, EnvironDatabase environDB, Element importerElem)
    {
        this.workingDir = workingDir;
        this.environDB = environDB;
        this.importerElem = importerElem;
    }

    /**
     * Initialize the fields common to all DB importers
     * 
     * @param workingDir working directory
     * @param environDB environmental database for import
     * @param props properties for the importer
     * @param baseKey base key for the properties applicable to this importer
     */
    public void initialize(File workingDir, EnvironDatabase environDB, Properties props, String baseKey)
    {
        this.workingDir = workingDir;
        this.environDB = environDB;
        this.props = props;
        this.baseKey = baseKey;
    }

    /**
     * Create the importer type designated by the properties and
     * execute it
     * @param workingDir 
     *      working directory
     * @param environDB 
     *      database for imported data
     * @param importerElem
     *      XML element containing information needed to run the importer
     * @throws Exception 
     *      if error in creating the importer
     */
    public static void execute(File workingDir, EnvironDatabase environDB, Element importerElem) 
    throws Exception
    {
        DBImporter importer = IMPORTER_MAP.get(
                importerElem.getAttribute("type")).newInstance();
        importer.initialize(workingDir, environDB, importerElem);
        importer.importData();
    }
    
    /**
     * Create the importer type designated by the properties and
     * execute it
     * @param workingDir working directory
     * @param environDB database for imported data
     * @param props properties defining the import
     * @param baseKey name of the importer in the properties
     * @throws Exception if error in creating the importer
     */
    public static void execute(File workingDir, EnvironDatabase environDB, Properties props, String baseKey) 
    throws Exception
    {
        DBImporter importer = DBImporter.IMPORTER_MAP.get(
                props.getProperty(baseKey + ".Type")).newInstance();
        importer.initialize(workingDir, environDB, props, baseKey);
        importer.importData();
    }
    
    /**
     * Return the key with the base key for this importer
     * 
     * @param propKey key suffix for property
     * @return full key for property
     */
    public String key(String propKey)
    {
        return baseKey + "." + propKey;
    }
    
    /**
     * Return the property associated with the key with the base key for this importer
     * 
     * @param propKey key suffix for property
     * @return property associated with the key suffix
     */
    public String prop(String propKey)
    {
        return props.getProperty(key(propKey));
    }

    /**
     * Return the property associated with the potential base key and property key if exists.
     * Otherwise return the property associated with the default base key and property key.
     * Returns null if neither exist.
     * 
     * @param potentialBaseKey potential specific base key for value
     * @param defaultBaseKey default base key used if specific key does not exist
     * @param propKey key suffix for property
     * @return property key
     */
    public String prop(String potentialBaseKey, String defaultBaseKey, String propKey)
    {
        String potentialValue = prop(potentialBaseKey + "." + propKey);
        if (potentialValue == null)
        {
            return prop(defaultBaseKey + "." + propKey);
        }
        else
        {
            return potentialValue;
        }
    }

}
