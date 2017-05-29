package stream_metab.doxygen.edge.lagrangea;

import neo.motif.*;
import neo.state.*;
import stream_metab.doxygen.utils.*;

/**
 * <p>Controls the Schmidt number for oxygen dissolved in water</p>
 * <b>References:</b>
 * <ul style="list-style-type: none">
 *      <li style="margin-bottom: 5px; margin-left: 10px; text-indent:-10px">
 *      Wanninkhof, R.  1992.  Relationship between wind speed and gas exchange 
 *      over the ocean.  Journal of Geophysical Research, 97(C5), 7373-7382.
 *      </li>
 * </ul>
 * 
 * 
 * @author robert.payn
 *
 */
public class SchmidtNumber extends AbstractUpdaterDbl {
    
    /**
     * Temperature at the downstream end of the reach (&deg;C)
     */
    private HStateDbl tempDown;
    
    /**
     * Temperature at the upstream end of the reach (&deg;C)
     */
    private HStateDbl tempUp;

    /**
     * Calculate the Schmidt number for oxygen dissolved in water from Wanninkhof (1992)
     * 
     * @return Schmidt number
     */
    @Override
    protected double computeValue()
    {
        return Calculators.schmidtNumberEmpirical((tempUp.v + tempDown.v) / 2);
    }

    /**
     * Calculate the Schmidt number for oxygen dissolved in water
     * 
     * @return Schmidt number
     */
    @Override
    protected double initValue()
    {
        return computeValue();
    }

    /**
     * Define the state dependencies for estimating the Schmidt number
     */
    @Override
    protected void setDependencies()
    {
        tempDown = (HStateDbl)getInitHState(DownTemp.class.getSimpleName());
        tempUp = (HStateDbl)getInitHState(UpTemp.class.getSimpleName());
    }

}
