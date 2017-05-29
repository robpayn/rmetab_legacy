package stream_metab.doxygen.edge.lagrangeb;

import neo.motif.*;
import neo.state.*;

/**
 * Controls the change in oxygen concentration due to aerobic respiration
 * over the reach
 * 
 * @author robert.payn
 */
public class Resp extends AbstractUpdaterDbl {

    /**
     * Daily average respiration flux
     * [Mass Length<sup><small>-2</small></sup> Time<sup><small>-1</small></sup>]
     */
    private HStateDbl dailyResp;
    
    /**
     * Depth of water over reach
     * [Length]
     */
    private HStateDbl depth;
    
    /**
     * Transport time over the reach
     * [Time]
     */
    private HStateDbl transportTime;

    /**
     * Calculate the change in oxygen concentration due to respiration over the reach
     * 
     * @return change in oxygen concentration [Mass Length<sup><small>-3</small></sup>]
     */
    @Override
    protected double computeValue()
    {
        return (dailyResp.v * transportTime.v ) / depth.v;
    }

    /**
     * Calculate the initial change in oxygen concentration due to respiration over the reach
     * 
     * @return change in oxygen concentration [Mass Length<sup><small>-3</small></sup>]
     */
    @Override
    protected double initValue()
    {
        return computeValue();
    }

    /**
     * Define state dependencies for calculating oxygen concentration change due
     * to aerobic respiration over the reach
     * 
     * @see Depth
     */
    @Override
    protected void setDependencies()
    {
        dailyResp = (HStateDbl) getInitHState(Doxygen.Names.RESP_DAILY_AVG);
        depth = (HStateDbl) getInitHState(Depth.class.getSimpleName());
        transportTime = (HStateDbl) getInitHState(Doxygen.Names.TIME_TRANSPORT);
    }

}
