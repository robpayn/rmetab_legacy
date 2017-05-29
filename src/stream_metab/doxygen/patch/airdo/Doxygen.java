package stream_metab.doxygen.patch.airdo;

import neo.motif.*;
import neo.util.*;

/**
 * Controls the net oxygen change in the air due to diffusion of DO across the
 * air-water interface.
 * 
 * @author Administrator
 */
public class Doxygen extends AbstractHub {
    
    /**
     * Static inner class to track the names of states and motifs used
     * by this behavior
     * 
     * @author robert.payn
     */
    public static class Names {
        
        /**
         * State name for the table with PAR data
         */
        public static final String PAR_TABLE = "PARTABLE";
        
    }

    /**
     * Log a warning and initialize to zero unless a different value is provide
     * in the initialization tables
     * 
     * @return Mass of oxygen [M]
     */
    @Override
    protected double initValue()
    {
        // Check if state has a value
        if (isNil())
        {
            // Log an warning if there is no value and set to 0
            Logger.logWarn(1, "No intitial value provided for state " + getHState().toString() + " in cell "
                    + myHolon.getUID().toString() + ". Initializing to zero.");
            return 0;
        }
        else
        {
            // If state has a value, use it
            return getValue();
        }
    }

    /**
     * Default hub mass balance summation (no additional dependencies)
     */
    @Override
    protected void setDependencies()
    {
    }

}
