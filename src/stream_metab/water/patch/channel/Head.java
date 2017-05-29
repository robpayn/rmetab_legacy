package stream_metab.water.patch.channel;

import neo.motif.AbstractUpdaterDbl;
import neo.state.HStateDbl;

/** Head, in meters above datum. */
public class Head extends AbstractUpdaterDbl {

    private HStateDbl zbot = null;
    private HStateDbl depth = null;
    private HStateDbl h2o = null;

    public void setDependencies()
    {

        depth = (HStateDbl) getInitHState("DEPTH");
        zbot = (HStateDbl) getInitHState("ZBOT");
        h2o = (HStateDbl) getInitHState("WATER");

    }

    public double initValue()
    {
        return computeValue();
    }

    /*-------------------------*/
    public double computeValue()
    {
        return (h2o.v <= 0) ? zbot.v : zbot.v + depth.v;

    }
}
