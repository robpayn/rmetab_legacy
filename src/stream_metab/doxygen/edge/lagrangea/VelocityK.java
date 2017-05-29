package stream_metab.doxygen.edge.lagrangea;

import neo.motif.*;
import neo.state.*;

/**
 * Controls the gas exchange velocity corrected for current Schmidt number
 * <p>
 * <b>References:</b>
 * </p>
 * <ul style="list-style-type: none"> <li
 * style="margin-bottom: 5px; margin-left: 10px; text-indent:-10px"> Jahne, B.,
 * K. O. Munnich, R. Bosinger, A. Dutzi, W. Huber, and P. Libner (1987) On
 * parameters influencing air-water gas exchange. Journal of Geophysical
 * Research 74, 456-464. </li> </ul>
 * 
 * @author robert.payn
 */
public class VelocityK extends AbstractUpdaterDbl {

    /**
     * First-order reaeration rate at a Schmidt number of 600 [Length
     * Time<sup><small>-1</small></sup>]
     */
    private HStateDbl k600;
    
    /**
     * Schmidt number
     */
    private HStateDbl schmidt;
    
    /**
     * Calculate the temperature corrected reaeration rate (Jahne et al. 1987)
     * 
     * @return First-order reaeration rate times depth [Length
     *         Time<sup><small>-1</small></sup>]
     */
    @Override
    protected double computeValue()
    {
        return k600.v * Math.sqrt(600 / schmidt.v);
    }

    /**
     * Calculate the initial temperature corrected reaeration rate
     * 
     * @return First-order reaeration rate times depth [Length
     *         Time<sup><small>-1</small></sup>]
     */
    @Override
    protected double initValue()
    {
        return computeValue();
    }

    /**
     * Define the state dependencies for calculating reaeration rate
     * 
     * @see stream_metab.doxygen.bound.loaddo.TempObs
     */
    @Override
    protected void setDependencies()
    {
        k600 = (HStateDbl)getInitHState(Doxygen.Names.K_600);
        schmidt = (HStateDbl)getInitHState(SchmidtNumber.class.getSimpleName());
    }

}
