package stream_metab.water.edge.vdarcy;

import neo.motif.AbstractRoute;
import neo.state.HStateDbl;

public class Water extends AbstractRoute {

    private HStateDbl xsect = null;
    private HStateDbl k = null;
    private HStateDbl hydroGrad = null;

    public void setDependencies()
    {
        xsect = (HStateDbl) getInitHState("XSECTAREA");
        k = (HStateDbl) getInitHState("K");
        hydroGrad = (HStateDbl) getInitHState("HYDROGRAD");
    }

    public double initValue()
    {
        return 0.0;
    }

    public double computeValue()
    {
        return (k.v * xsect.v * hydroGrad.v);
    }
}
