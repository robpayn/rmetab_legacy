package stream_metab.water.edge.gsdarcy;

import neo.motif.AbstractRoute;
import neo.state.HStateDbl;

/**
 * Flux between surface compartment and ground compartment. Surface is always
 * "to", ground is always "from" so that upward flow is positive and downward
 * flow is negative
 */

public class Water extends AbstractRoute {

    private HStateDbl wetxsect = null;
    private HStateDbl hydrograd = null;
    private HStateDbl k = null;

    public void setDependencies()
    {
        wetxsect = (HStateDbl) getInitHState("WETXSECT");
        hydrograd = (HStateDbl) getInitHState("HYDROGRAD");
        k = (HStateDbl) getInitHState("K");
    }

    public double initValue()
    {
        return 0.0;
    }

    public double computeValue()
    {
        return k.v * wetxsect.v * hydrograd.v;
    }

}
