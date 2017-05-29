package stream_metab.doxygen.edge.lagrangea;

import neo.motif.*;
import neo.state.*;
import neo.table.*;
import neo.util.*;

/**
 * Controls the temperature at the downstream end of the reach (input data)
 * 
 * @author robert.payn
 */
public class DownTemp extends AbstractUpdaterDbl {

    /**
     * Time interpolater for temperature input data
     */
    private FileInterpolater interpTemp;
    
    /**
     * Name of table containing temperature time series
     */
    private HStateStr tableName;

    /**
     * Interpolate the current temperature at the downstream end of the reach
     * 
     * @return temperature (&deg;C)
     */
    @Override
    protected double computeValue()
    {
        return interpTemp.getValue(Scheduler.getCurrentTime());
    }

    /**
     * Interpolate the initial current temperature at the downstream end of the reach
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
     * Define the state dependencies for calculating temperature
     */
    @Override
    protected void setDependencies()
    {
        tableName = (HStateStr)getInitHState(Doxygen.Names.TABLE_TEMP_DOWN);
    }

}
