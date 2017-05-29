package stream_metab.doxygen.edge.lagrangea;

import neo.motif.*;
import neo.state.*;
import neo.table.*;
import neo.util.*;

/**
 * Controls the upstream DO concentration at one transport time before the
 * current time.
 * 
 * @author robert.payn
 */
public class UpDOConc extends AbstractUpdaterDbl {

    /**
     * Interpolator table for the DO concentration
     */
    private FileInterpolater interpDO;
    
    /**
     * Name of the table used for DO interpolation
     */
    private HStateStr tableName;
    
    /**
     * Total transport time over the stream reach (min)
     */
    private HStateDbl transportTime;

    /**
     * Interpolate the upstream DO concentration at one travel time before
     * current time
     */
    @Override
    protected double computeValue()
    {
        return interpDO.getValue(Scheduler.getCurrentTime() - transportTime.v);
    }

    /**
     * Create the interpolation table
     */
    @Override
    protected double initValue()
    {
        try
        {
            interpDO = InterpolaterFactory.create(tableName.v);
            return computeValue();
        }
        catch (Throwable t)
        {
            Logger.logError(t.getMessage());
            return 0;
        }
    }

    /**
     * Define state dependencies for interpolating upstream DO concentration
     */
    @Override
    protected void setDependencies()
    {
        tableName = (HStateStr)getInitHState(Doxygen.Names.TABLE_DO_CONC_UP);
        transportTime = (HStateDbl)getInitHState(Doxygen.Names.TIME_TRANSPORT);
    }

}
