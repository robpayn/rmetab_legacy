package stream_metab.water.edge.hdarcy;

import neo.motif.AbstractParameterDbl;
import neo.state.HStateDbl;

/**
 * area of plane section shared between two compartments. Read from input, or
 * computed if input value is zero.
 */
public class XSectArea extends AbstractParameterDbl {

    private HStateDbl thickness = null;
    private HStateDbl width = null;

    public void setDependencies()
    {

        thickness = (HStateDbl) getInitHState("THICKNESS");
        width = (HStateDbl) getInitHState("WIDTH");

    }

    public double initValue()
    {
        return thickness.v * width.v;
    }
}
