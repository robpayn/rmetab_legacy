package stream_metab.doxygen.bound.respdo;

import neo.motif.*;
import neo.state.*;

/**
 * Controls the instantaneous oxygen mass flux due to respiration
 * 
 * @author Rob Payn
 */
public class InstResp extends AbstractParameterDbl {

    /**
     * Average daily oxygen mass flux due to respiration [Mass
     * Length<sup><small>-2</small></sup> Time<sup><small>-1</small></sup>]
     */
    private HStateDbl dailyResp;

    /**
     * Calculate the oxygen mass flux due to respiration
     * 
     * @return Mass flux of oxygen [Mass Length<sup><small>-2</small></sup>
     *         Time<sup><small>-1</small></sup>]
     */
    @Override
    protected double initValue()
    {
        // This implementation assumes constant respiration, so the
        // instantaneous rate is identical to the daily average
        return dailyResp.v;
    }

    /**
     * Define the state dependencies for calculating respiration
     */
    @Override
    protected void setDependencies()
    {
        dailyResp = (HStateDbl) getInitHState(Doxygen.Names.DAILY_RESP_RATE);
    }

}
