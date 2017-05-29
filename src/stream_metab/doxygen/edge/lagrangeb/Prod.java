package stream_metab.doxygen.edge.lagrangeb;

import neo.motif.*;
import neo.state.*;
import neo.table.*;
import neo.util.*;

/**
 * Controls the change in oxygen concentration due to photosynthesis over the reach
 * 
 * @author robert.payn
 */
public class Prod extends AbstractUpdaterDbl {

    /**
     * Ratio of oxygen production flux to photosynthetically active radiation flux
     * [Mass Length<sup><small>-2</small></sup> Time<sup><small>-1</small></sup>
     * (Energy Length<sup><small>-2</small></sup> Time<sup><small>-1</small></sup>)<sup><small>-1</small></sup>]
     */
    private HStateDbl ratioProdPAR;
    
    /**
     * Time interval for PAR data
     */
    private HStateDbl timeInt;
    
    /**
     * Average depth of water over the reach
     */
    private HStateDbl depth;
    
    /**
     * Time interpolater for PAR data
     */
    private FileInterpolater interpPAR;
    
    /**
     * Name of table containing PAR data
     */
    private HStateStr tableName;
    
    /**
     * Transport time of the reach
     */
    private HStateDbl transportTime;

    /**
     * Calculate the current change in oxygen concentration due to photosynthesis.\
     * This calculation is most accurate when the time interval of the PAR data
     * is much smaller than the transport time over the reach.
     * 
     * @return change in oxygen concentration 
     * [Mass Length<sup><small>-3</small></sup>]
     */
    @Override
    protected double computeValue()
    {
        double parInteg = 0;
        for (double time = Scheduler.getCurrentTime(); 
                time > Scheduler.getCurrentTime() - transportTime.v + timeInt.v; time -= timeInt.v)
        {
            parInteg +=  interpPAR.getValue(time) + interpPAR.getValue(time - timeInt.v);
        }
        return (ratioProdPAR.v * (parInteg / 2) * timeInt.v) / depth.v;
    }

    /**
     * Create the interpolater for PAR and calculate the initial change in 
     * oxygen concentration due to photosynthesis
     * 
     * @return change in oxygen concentration 
     * [Mass Length<sup><small>-3</small></sup>]
     */
    @Override
    protected double initValue()
    {
        try
        {
            interpPAR = InterpolaterFactory.create(tableName.v);
            return computeValue();
        }
        catch (Throwable t)
        {
            Logger.logError(t.getMessage());
            return 0;
        }
    }

    /**
     * Define the dependencies for calculating the change in oxygen due to photosynthesis
     * 
     * @see Depth
     */
    @Override
    protected void setDependencies()
    {
        ratioProdPAR = (HStateDbl)getInitHState(Doxygen.Names.RATIO_PROD_PAR);
        depth = (HStateDbl)getInitHState(Depth.class.getSimpleName());
        tableName = (HStateStr)getInitHState(Doxygen.Names.TABLE_PAR);
        timeInt = (HStateDbl)getInitHState(Doxygen.Names.INTERVAL_PAR_DATA);
        transportTime = (HStateDbl)getInitHState(Doxygen.Names.TIME_TRANSPORT);
    }

}
