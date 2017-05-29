package stream_metab.water.edge.manningcalib;

import neo.motif.*;
import neo.state.HStateStr;
import neo.table.FileInterpolater;
import neo.table.InterpolaterFactory;
import neo.util.Logger;
import neo.util.Scheduler;

public class WaterObserved extends AbstractUpdaterDbl {

    /**
     * Table interpolator for observed flow data
     */
    private FileInterpolater interpFlow;
    
    /**
     * Name of the observed flow data table
     */
    private HStateStr tableName;

    @Override
    protected double computeValue()
    {
        // Interpolate the current value using the current simulation time
        return interpFlow.getValue(Scheduler.getCurrentTime());
    }

    @Override
    protected double initValue()
    {
        // Create the interpolator and log an error if exception is thrown
        try
        {
            interpFlow = InterpolaterFactory.create(tableName.v);
            return computeValue();
        }
        catch (Throwable t)
        {
            Logger.logError(t.getMessage());
            return 0;
        }
    }

    @Override
    protected void setDependencies()
    {
        tableName = (HStateStr)getInitHState("FLOWTABLE");
    }

}
