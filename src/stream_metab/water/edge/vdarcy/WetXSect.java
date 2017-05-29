package stream_metab.water.edge.vdarcy;

import neo.motif.*;
import neo.state.*;

/**
 * area of plane section shared between two compartments. Read from input, or
 * computed if input value is zero.
 * 
 * @author Rob Payn
 */
public class WetXSect extends AbstractParameterDbl {

    /**
     * Cross-sectional area
     */
    private HStateDbl xsectarea = null;

    /**
     * Define dependencies for the calculation of cross-sectional area
     */
    public void setDependencies()
    {

        xsectarea = (HStateDbl) getInitHState("XSECTAREA");

    }

    /**
     * Calculate the initial value of cross-sectional area
     * 
     * @return cross-sectional area
     */
    public double initValue()
    {

        return xsectarea.v;

    }

}
