package stream_metab.water.utils;

import java.util.*;

/**
 * Tools for creating tables for the patch.channel behavior
 * 
 * @author robert.payn
 */
public class PatchChannel {

    /**
     * An enumeration for indexing the list of values corresponding
     * to the order of the list of fields
     * 
     * @author robert.payn
     */
    public enum Fields {
        /**
         * Identification field
         */
        ID("ID","TEXT"),
        /**
         * Surface area field
         */
        AS("AS","FLOAT"),
        /**
         * Maximum surface area field
         */
        ASMAX("ASMAX","FLOAT"),
        /**
         * Bank slope field
         */
        BANKSLOPE("BANKSLOPE","FLOAT"),
        /**
         * Change in area with depth field
         */
        DELAS("DELAS","FLOAT"),
        /**
         * Initial head field
         */
        INIHEAD("INIHEAD","FLOAT"),
        /**
         * Length of cell field
         */
        LENGTH("LENGTH","FLOAT"),
        /**
         * Average width field
         */
        WIDTHAVG("WIDTHAVG","FLOAT"),
        /**
         * x coordinate field (horizontal plane)
         */
        XCOORD("XCOORD","FLOAT"),
        /**
         * y coordinate field (horizontal plane)
         */
        YCOORD("YCOORD","FLOAT"),
        /**
         * elevation field of bed (vertical line)
         */
        ZBOT("ZBOT","FLOAT"), 
        /**
         * elevation of bank top (vertical line)
         */
        ZTOP("ZTOP","FLOAT");
        
        /**
         * Name of the field
         */
        private String name;
        /**
         * Type of the field
         */
        private String type;
        
        /**
         * Create a new instance of the enumeration, storing the associated
         * name and type of the field
         * 
         * @param name name of the field
         * @param type type of the field
         */
        private Fields(String name, String type)
        {
            this.name = name;
            this.type = type;
        }
        
        /**
         * Getter for name
         * 
         * @return name
         */
        public String getName()
        {
            return name;
        }
        
        /**
         * Getter for type
         * 
         * @return type
         */
        public String getType()
        {
            return type;
        }
        
    }
    
    /**
     * Key for bed gradient
     */
    public static final String PROP_KEY_GRADIENT = "bedGradient";

    /**
     * Key for the cell count
     */
    public static final String PROP_KEY_CELL_COUNT = "cellCount";
    
    /**
     * Key for bank-full depth
     */
    public static final String PROP_KEY_DEPTH = "depth";

    /**
     * Key for upstream boundary name
     */
    public static final String PROP_KEY_DOWNSTREAM_BEHAVIOR = "downstreamBehavior";
    
    /**
     * Key for the initial depth 
     */
    public static final String PROP_KEY_INIDEPTH = "iniDepth";
    
    /**
     * Key for patch name seed
     */
    public static final String PROP_KEY_PATCH_BEHAVIOR = "patchBehavior";
    
    /**
     * Key for length of reach
     */
    public static final String PROP_KEY_REACH_LENGTH = "reachLength";

    /**
     * Key for resource name
     */
    public static final String PROP_KEY_RESOURCE = "resourceName";
    
    /**
     * Key for upstream boundary name
     */
    public static final String PROP_KEY_UPSTREAM_BEHAVIOR = "upstreamBehavior";
    
    /**
     * Properties containing information about the patch
     */
    private Properties properties;
    
    /**
     * Create a new instance of a patch channel behavior
     * 
     * @param properties properties containing information about the patch
     */
    public PatchChannel(Properties properties)
    {
        this.properties = properties;
    }

    /**
     * Create a new list of values and initialize them
     * 
     * @return an array of strings of the appropriate size for the number of fields
     */
    public String[] getPatchValues()
    {
        String[] values = new String[Fields.values().length];
        for (int i = 1; i < values.length; i++)
        {
            values[i] = properties.getProperty(getBehaviorName() + "." + Fields.values()[i].getName());
        }
        return values;
    }

    /**
     * Gets the behavior name from the properties
     * 
     * @return behavior name
     */
    public String getBehaviorName()
    {
        return properties.getProperty(PROP_KEY_PATCH_BEHAVIOR);
    }

    /**
     * Get the field list with types as a 2-D array.  First dimension is
     * the list of available fields and the second dimension is the type
     * for each field.
     * 
     * @return array of fields and types
     */
    public static String[][] getFieldsAsArray()
    {
        String[][] array = new String[Fields.values().length][2];
        for (int i = 0; i < Fields.values().length; i++)
        {
            array[i][0] = Fields.values()[i].getName();
            array[i][1] = Fields.values()[i].getType();
        }
        return array;
    }

    /**
     * Gets the cell count from the properties
     * 
     * @return cell count
     */
    public int getCellCount()
    {
        return Integer.valueOf(properties.getProperty(PROP_KEY_CELL_COUNT));
    }

    /**
     * Gets the active channel depth from the properties
     * 
     * @return depth
     */
    public double getDepth()
    {
        return Double.valueOf(properties.getProperty(PROP_KEY_DEPTH));
    }

    /**
     * Gets the bed gradient from the properties
     * 
     * @return bed gradient
     */
    public double getGradient()
    {
        return Double.valueOf(properties.getProperty(PROP_KEY_GRADIENT));
    }

    /**
     * Gets the initial depth from the properties
     * 
     * @return initial depth
     */
    public double getInitialDepth()
    {
        return Double.valueOf(properties.getProperty(PROP_KEY_INIDEPTH));
    }

    /**
     * Gets the reach length from the properties
     * 
     * @return reach length
     */
    public double getReachLength()
    {
        return Double.valueOf(properties.getProperty(PROP_KEY_REACH_LENGTH));
    }

    /**
     * Gets the resource name from the properties
     * 
     * @return resource name
     */
    public String getResourceName()
    {
        return properties.getProperty(PROP_KEY_RESOURCE);
    }

}
