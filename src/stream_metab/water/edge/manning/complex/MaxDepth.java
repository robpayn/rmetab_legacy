package stream_metab.water.edge.manning.complex;

import neo.motif.AbstractParameterDbl;
import neo.state.HStateDbl;

public class MaxDepth extends AbstractParameterDbl {

    HStateDbl channelz = null;
    HStateDbl zmax = null;

    public void setDependencies()
    {

        channelz = (HStateDbl) getInitHState("CHANNELZ");
        zmax = (HStateDbl) getInitHState("ZMAX");

    }

    public double initValue()
    {

        if (zmax.v <= channelz.v)
            initError("Maximum channel elevation (zmax) is <= channel elevation (zchan).");

        return zmax.v - channelz.v;

    }

}
