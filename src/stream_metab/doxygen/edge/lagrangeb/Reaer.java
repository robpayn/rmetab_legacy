package stream_metab.doxygen.edge.lagrangeb;

import neo.motif.*;
import neo.state.*;

/**
 * Controls the change in oxygen concentration due to reaeration, or 
 * physical exchange with the atmosphere.
 * 
 * @author robert.payn
 */
public class Reaer extends AbstractUpdaterDbl {

    /**
     * Depth of water [Length]
     */
    private HStateDbl depth;
    
    /**
     * Saturated DO concentration at the upstream end of the reach
     * [Mass Length<sup><small>-3</small></sup>]
     */
    private HStateDbl dOSatUp;
    
    /**
     * Saturated DO concentration at the downstream end of the reach
     * [Mass Length<sup><small>-3</small></sup>]
     */
    private HStateDbl dOSatDown;
    
    /**
     * DO concentration at the upstream end of the reach
     * [Mass Length<sup><small>-3</small></sup>]
     */
    private HStateDbl dOUp;
    
    /**
     * Gas exchange velocity for oxygen
     * [Length Time<sup><small>-1</small></sup>]
     */
    private HStateDbl reaerVel;
    
    /**
     * Transport time of water over the reach
     * [Time]
     */
    private HStateDbl transportTime;

    /**
     * Calculate the change in oxygen due to reaeration
     * 
     * @return change in oxygen concentration [Mass Length<sup><small>-3</small></sup>]
     */
    @Override
    protected double computeValue()
    {
        return (reaerVel.v / depth.v) * transportTime.v * ((dOSatUp.v - dOUp.v + dOSatDown.v) * 0.5);
    }

    /**
     * Calculate the initial change in oxygen due to reaeration
     * 
     * @return change in oxygen concentration [Mass Length<sup><small>-3</small></sup>]
     */
    @Override
    protected double initValue()
    {
        return computeValue();
    }

    /**
     * Define the state dependencies for calculating oxygen concentration change due to reaeration
     */
    @Override
    protected void setDependencies()
    {
        depth = (HStateDbl)getInitHState(Depth.class.getSimpleName());
        dOSatUp = (HStateDbl)getInitHState(UpDOSat.class.getSimpleName());
        dOSatDown = (HStateDbl)getInitHState(DownDOSat.class.getSimpleName());
        dOUp = (HStateDbl)getInitHState(UpDOConc.class.getSimpleName());
        reaerVel = (HStateDbl)getInitHState(VelocityK.class.getSimpleName());
        transportTime = (HStateDbl)getInitHState(Doxygen.Names.TIME_TRANSPORT);
    }

}
