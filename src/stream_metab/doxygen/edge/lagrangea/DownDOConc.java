package stream_metab.doxygen.edge.lagrangea;

import neo.motif.*;
import neo.state.*;

/**
 * Controls DO concentration at the downstream end of the reach
 * 
 * @author robert.payn
 */
public class DownDOConc extends AbstractUpdaterDbl {

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
     * Calculates the dissolved oxygen concentration at the downstream end of the reach
     * 
     * @return oxygen concentration [Mass Length<sup><small>-3</small></sup>]
     */
    @Override
    protected double computeValue()
    {
        return dOUp.v + prod.v + resp.v + reaer.v;
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
        dOUp = (HStateDbl)getInitHState(UpDOConc.class.getSimpleName());
        prod = (HStateDbl)getInitHState(Prod.class.getSimpleName());
        resp = (HStateDbl)getInitHState(Resp.class.getSimpleName());
        reaer = (HStateDbl)getInitHState(Reaer.class.getSimpleName());
    }

}
