package stream_metab.doxygen.edge.lagrangeb;

import neo.motif.*;
import neo.state.*;
import neo.table.*;
import neo.util.*;

/**
 * Controls temperature at the upstream end of the reach (input data)
 * 
 * @author robert.payn
 */
public class UpTemp extends AbstractUpdaterDbl {

    /**
     * Interpolater for upstream temperature
     */
    private FileInterpolater interpTemp;
    
    /**
     * Name of table with upstream temperature data
     */
    private HStateStr tableName;
    
    /**
     * Transport time over the reach
     */
    private HStateDbl tTime;

    /**
     * Interpolate the upstream temperature
     * 
     * @return temperature (&deg;C)
     */
    @Override
    protected double computeValue()
    {
        return interpTemp.getValue(Scheduler.getCurrentTime() - tTime.v);
    }

    /**
     * Interpolate the initial upstream temperature
     * 
     * @return temperature (&deg;C)
     */
    @Override
    protected double initValue()
    {
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
     * Define the state dependencies for interpolating upstream temperature
     */
    @Override
    protected void setDependencies()
    {
        tableName = (HStateStr)getInitHState(Doxygen.Names.TABLE_TEMP_UP);
        tTime = (HStateDbl)getInitHState(Doxygen.Names.TIME_TRANSPORT);
    }

}
