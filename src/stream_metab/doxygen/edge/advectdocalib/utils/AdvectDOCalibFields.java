package stream_metab.doxygen.edge.advectdocalib.utils;

import org.neosimulation.apps.modelmanager.neov1.neodatabase.*;
import stream_metab.doxygen.edge.advectdocalib.Doxygen;

/**
 * Fields needed by the DO advection calibration behavior
 * 
 * @author robert.payn
 *
 */
@SuppressWarnings("serial")
public class AdvectDOCalibFields extends FieldMap {

    /**
     * Name of currency
     */
    public static final String NAME_RESOURCE = "doxygen";
    
    /**
     * Name of behavior
     */
    public static final String NAME_BEHAVIOR = "advectdocalib";
    
    /**
     * Name of reach input DO concentration holon
     */
    public static final String REQ_SAT_DO_CONC_IN = Doxygen.Names.SAT_DO_CONC_IN;
    
    /**
     * Name of table with observed DO times series
     */
    public static final String REQ_TABLE_DO_CONC = Doxygen.Names.TABLE_DO_CONC;
    
    /**
     * Tame of table with observed temperatures
     */
    public static final String REQ_TABLE_TEMP = Doxygen.Names.TABLE_TEMP;
    
    /**
     * Name of reach input temperature holon
     */
    public static final String REQ_TEMP_IN = Doxygen.Names.TEMP_IN;
    
    /**
     * List of required fields
     */
    public static final Field[] reqFields = {
        new Field(REQ_SAT_DO_CONC_IN, "TEXT", null, true),
        new Field(REQ_TABLE_DO_CONC, "TEXT", null, true),
        new Field(REQ_TABLE_TEMP, "TEXT", null, true),
        new Field(REQ_TEMP_IN, "TEXT", null, true)
    };
    
    /**
     * getter for list of required fields
     */
    @Override
    public Field[] getRequiredFieldList()
    {
        return reqFields;
    }

    /**
     * getter for resource name
     */
    @Override
    public String getResourceName()
    {
        return NAME_RESOURCE;
    }

    /**
     * getter for behavior name
     */
    @Override
    public String getBehaviorName()
    {
        return NAME_BEHAVIOR;
    }
    
}
