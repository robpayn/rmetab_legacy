package stream_metab.doxygen.edge.lagrangeb;

import neo.motif.*;
import neo.state.*;

/**
 * Controls DO concentration at the downstream end of the reach
 * 
 * @author robert.payn
 */
public class DownDOConc extends AbstractUpdaterDbl {

    /**
     * Depth of water [Length]
     */
    private HStateDbl depth;
    
    /**
     * DO concentration at the upstream end of the reach [Mass Length<sup><small>-3</small></sup>]
     */
    private HStateDbl dOUp;
    
    /**
     * DO concentration increase (should be positive) due to biological production 
     * [Mass Length<sup><small>-3</small></sup>]
     */
    private HStateDbl prod;

    /**
     * DO concentration decrease (should be negative) due to biological respiration
     * [Mass Length<sup><small>-3</small></sup>]
     */
    private HStateDbl resp;
    
    /**
     * Do concentration change (could be positive or negative) due to physical exchange
     * with the atmosphere [Mass Length<sup><small>-3</small></sup>]
     */
    private HStateDbl reaer;

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
     * Calculates the dissolved oxygen concentration at the downstream end of the reach
     * 
     * @return oxygen concentration [Mass Length<sup><small>-3</small></sup>]
     */
    @Override
    protected double computeValue()
    {
        return (dOUp.v + prod.v + resp.v + reaer.v) / 
                (1 + ((reaerVel.v * transportTime.v) / (2 * depth.v)));
    }

    /**
     * Calculates the initial dissolved oxygen concentration at the downstream end of the reach
     * 
     * @return oxygen concentration [Mass Length<sup><small>-3</small></sup>]
     */
    @Override
    protected double initValue()
    {
        return computeValue();
    }

    /**
     * Define the state dependencies for calculating downstream oxygen concentration
     * 
     * @see UpDOConc
     * @see Prod
     * @see Resp
     * @see Reaer
     */
    @Override
    protected void setDependencies()
    {
        depth = (HStateDbl)getInitHState(Depth.class.getSimpleName());
        dOUp = (HStateDbl)getInitHState(UpDOConc.class.getSimpleName());
        prod = (HStateDbl)getInitHState(Prod.class.getSimpleName());
        resp = (HStateDbl)getInitHState(Resp.class.getSimpleName());
        reaer = (HStateDbl)getInitHState(Reaer.class.getSimpleName());
        reaerVel = (HStateDbl)getInitHState(VelocityK.class.getSimpleName());
        transportTime = (HStateDbl)getInitHState(Doxygen.Names.TIME_TRANSPORT);
    }

}
