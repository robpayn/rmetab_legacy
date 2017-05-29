package stream_metab.water.edge.manning.complex;

import neo.motif.AbstractParameterDbl;
import neo.state.HStateDbl;

public class ChannelZ extends AbstractParameterDbl {

    /*
     * This class may seem unnecessary because it just stores "ZCHAN," but the
     * default (non-complex) version of this motif allow the model to calculate
     * ChannelZ when ZCHAN = 0. Thus, this class makes ZCHAN a required input
     * for the complex version of the motif and disallows input of ZCHAN=0.
     */

    HStateDbl zchan = null;

    public void setDependencies()
    {

        zchan = (HStateDbl) getInitHState("ZCHAN");

    }

    public double initValue()
    {
        return zchan.v;
    }

}
