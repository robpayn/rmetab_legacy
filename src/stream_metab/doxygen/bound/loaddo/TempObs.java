package stream_metab.doxygen.bound.loaddo;

import neo.motif.*;
import neo.state.*;
import neo.table.*;
import neo.util.*;

/**
 * Controls the temperature of water in an inflow (interpolated from table)
 * 
 * @author Rob Payn
 */
public class TempObs extends AbstractUpdaterDbl {

    /**
     * Table interpolator for temperature
     */
    private FileInterpolater interpTemp;
    
    /**
     * Name of the temperature table
     */
    private HStateStr tableName;

    /**
     * Calculates the temperature (interpolated from input table)
     * 
     * @return Temperature (&deg;C)
     */
    @Override
    protected double computeValue()
    {
        // Interpolate the current value using the current simulation time
        return interpTemp.getValue(Scheduler.getCurrentTime());
    }

    /**
     * Creates the table interpolator from the provided file name and calculates
     * the initial temperature (interpolated from input table)
     * 
     * @return Temperature (&deg;C)
     */
    @Override
    protected double initValue()
    {
        // Create the interpolator and log an error if exception is thrown
        try
        {
            interpTemp = InterpolaterFactory.create(tableName.v);
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
        tableName = (HStateStr) getInitHState(Doxygen.Names.TABLE_TEMP);
    }

}
