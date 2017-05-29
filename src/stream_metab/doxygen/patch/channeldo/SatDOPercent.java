package stream_metab.doxygen.patch.channeldo;

import neo.motif.*;
import neo.state.*;

/**
 * Controls the percent oxygen saturation in the surface water cell
 * 
 * @author Administrator
 */
public class SatDOPercent extends AbstractUpdaterDbl {

    /**
     * DO concentration in the cell [Mass Length<sup><small>-3</small></sup>]
     */
    private HStateDbl dOConc;
    
    /**
     * DO saturation concentration in the cell [Mass
     * Length<sup><small>-3</small></sup>]
     */
    private HStateDbl satDOConc;

    /**
     * Calculate the percent DO saturation
     * 
     * @return Percent saturation (%)
     */
    @Override
    protected double computeValue()
    {
        return (dOConc.v / satDOConc.v) * 100;
    }

    /**
     * Calculate the initial percent DO saturation
     * 
     * @return Percent saturation (%)
     */
    @Override
    protected double initValue()
    {
        return computeValue();
    }

    /**
     * Define the state dependencies for calculating percent DO saturation
     * 
     * @see DOConc
     * @see SatDOConc
     */
    @Override
    protected void setDependencies()
    {
        dOConc = (HStateDbl) getInitHState(DOConc.class.getSimpleName());
        satDOConc = (HStateDbl) getInitHState(SatDOConc.class.getSimpleName());
    }

}
