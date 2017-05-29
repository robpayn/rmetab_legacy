package stream_metab.doxygen.bound.loaddo;

import neo.motif.*;
import neo.state.*;
import neo.table.*;
import neo.util.*;

/**
 * Controls the observed dissolved oxygen concentration at the boundary location
 * (interpolated from an input table)
 * 
 * @author Rob Payn
 */
public class DoConcObs extends AbstractUpdaterDbl {

    /**
     * Table interpolator for DO concentration
     */
    private FileInterpolater interpDOConc;
    
    /**
     * Name of the DO concentration table
     */
    private HStateStr tableName;

    /**
     * Calculates the DO concentration (interpolated from input table)
     * 
     * @return DO concentration [Mass Length<sup><small>-3</small></sup>]
     */
    @Override
    protected double computeValue()
    {
        // Interpolate the current value using the current simulation time
        return interpDOConc.getValue(Scheduler.getCurrentTime());
    }

    /**
     * Creates the table interpolator from the provided file name and calculates
     * the initial do concentration (interpolated from input table)
     * 
     * @return DO concentration [Mass Length<sup><small>-3</small></sup>]
     */
    @Override
    protected double initValue()
    {
        // Create the interpolator and log an error if exception is thrown
        try
        {
            interpDOConc = InterpolaterFactory.create(tableName.v);
            return computeValue();
        }
        catch (Throwable t)
        {
            Logger.logError(t.getMessage());
            return 0;
        }
    }

    /**
     * Defines the state dependencies for calculating DO concentration
     */
    @Override
    protected void setDependencies()
    {
        tableName = (HStateStr)getInitHState(Doxygen.Names.TABLE_DO_CONC);
    }

}
