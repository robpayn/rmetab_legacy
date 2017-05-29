package stream_metab.water.edge.manning;

import neo.motif.AbstractUpdaterDbl;
import neo.state.HStateDbl;

public class WettedWidth extends AbstractUpdaterDbl {

    private HStateDbl btmwth = null;
    private HStateDbl delwth = null;
    private HStateDbl depth = null;

    private boolean prismic;

    public void setDependencies()
    {

        btmwth = (HStateDbl) getInitHState("BTMWTH");
        delwth = (HStateDbl) getInitHState("WETTEDINCR");
        depth = (HStateDbl) getInitHState("DEPTH");

    }

    public double initValue()
    {
        return computeValue();
    }

    public double computeValue()
    {
        return (depth.v > 0) ? btmwth.v + (depth.v * delwth.v) : 0;
    }

}
