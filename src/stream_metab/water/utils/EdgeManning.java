package stream_metab.water.utils;

import java.util.*;
/**
 * Tools for constructing tables for the edge.manning behavior
 * 
 * @author robert.payn
 */
public class EdgeManning {
    
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
         * Bank slope field
         */
        BANKSLOPE("BANKSLOPE","FLOAT"),
        /**
         * Width of channel at bed
         */
        BTMWTH("BTMWTH","FLOAT"),
        /**
         * Roughness coefficient
         */
        C("C","FLOAT"),
        /**
         * Flag to calculate elevation of channel from adjacent cells
         */
        CALCZ("CALCZ","TEXT"),
        /**
         * Depth of active channel
         */
        DEPTHACTIVE("DEPTHACTIVE","FLOAT"),
        /**
         * Flag for type of equation to use for routing
         */
        EQTYPE("EQTYPE","LONG"),
        /**
         * Length on from side of edge
         */
        FROMLEN("FROMLEN","FLOAT"),
        /**
         * Initial flow
         */
        INITFLOW("INITFLOW","FLOAT"),
        /**
         * Hydraulic radius exponent for friction equation
         */
        RADEXP("P1","FLOAT"),
        /**
         * Velocity exponent for friction equation
         */
        VELEXP("P2","FLOAT"),
        /**
         * Length on to side of edge
         */
        TOLEN("TOLEN","FLOAT"),
        /**
         * Average velocity of flow
         */
        VELOCITY("VELOCITY","FLOAT"),
        /**
         * Change in width with depth
         */
        WETTEDINCR("WETTEDINCR","FLOAT"),
        /**
         * Average width
         */
        WIDTHAVG("WIDTHAVG","FLOAT"),
        /**
         * Elevation of channel (if not calculated due to CALCZ)
         */
        ZCHAN("ZCHAN","FLOAT");

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
     * Property key for edge behavior
     */
    public static final String PROP_KEY_EDGE_BEHAVIOR = "edgeBehavior";

    /**
     * Property key for resource name
     */
    private static final String PROP_KEY_RESOURCE = "resourceName";
    
    /**
     * Properties containing information about the edge
     */
    private Properties properties;
    
    /**
     * Creates a new instance and store the provided properties for the edge
     * 
     * @param properties properties containing information about the edge
     */
    public EdgeManning(Properties properties)
    {
        this.properties = properties;
    }
    
    /**
     * Create an array for values corresponding to the list of fields,
     * and populate any values that are present in the properties
     * 
     * @return array of values
     */
    public String[] getEdgeValues()
    {
        String[] values = new String[Fields.values().length];
        for (int i = 1; i < values.length; i++)
        {
            values[i] = properties.getProperty(getBehaviorName() + "." + Fields.values()[i].getName());
        }
        return values;
    }

    /**
     * Get the behavior name from the properties
     * 
     * @return behavior name
     */
    public String getBehaviorName()
    {
        return properties.getProperty(PROP_KEY_EDGE_BEHAVIOR);
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
     * Get the resource name from the properties
     * 
     * @return resource name
     */
    public String getResourceName()
    {
        return properties.getProperty(PROP_KEY_RESOURCE);
    }

}
