package stream_metab.doxygen.edge.lagrangeb;

import neo.motif.*;
import neo.state.*;
import neo.table.*;
import neo.util.*;

/**
 * Controls the observed DO concentration at the downstream end of the reach (input data)
 * 
 * @author robert.payn
 */
public class DownDOConcObs extends AbstractUpdaterDbl {

    /**
     * Interpolator for DO input data
     */
    private FileInterpolater interpDO;
    
    /**
     * Name of table containing input data
     */
    private HStateStr tableName;

    /**
     * Interpolates the current value from the input data
     * 
     * @return oxygen concentration [Mass Length<sup><small>-3</small></sup>]
     */
    @Override
    protected double computeValue()
    {
        return interpDO.getValue(Scheduler.getCurrentTime());
    }

    /**
     * Interpolates the initial value from the input data
     * 
     * @return oxygen concentration [Mass Length<sup><small>-3</small></sup>]
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
     * Define the state dependencies for getting input data
     */
    @Override
    protected void setDependencies()
    {
        tableName = (HStateStr)getInitHState(Doxygen.Names.TABLE_DO_CONC_DOWN);
    }

}
