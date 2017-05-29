package stream_metab.water.bound.looprate;

import neo.holon.Boundary;
import neo.holon.Edge;
import neo.holon.EdgeGenerator;
import neo.motif.AbstractHalfRoute;
import neo.state.HStateDbl;
import neo.state.HStateNotFoundException;
import neo.state.HStateStr;
import neo.uid.UniqueID;
import neo.uid.UniqueIDMgr;
import neo.util.Logger;

/**
 * Loop boundary must be connected to a surface node with only one functional
 * link. The functional link may be either an IN edge or OUT edge.
 */
public class Water extends AbstractHalfRoute {

    /*
     * the loop rating curve requires parameters from the connected holon and
     * all holons connected to it, so the setup is a bit involved
     */
    double rexp = 1.3333;
    double vexp = 2.0;
    HStateDbl friction = null;
    HStateDbl hydgrad = null;
    HStateDbl bedslope = null;
    HStateDbl xarea = null;
    HStateDbl hradius = null;
    HStateDbl ovrdslope = null;

    /** the edge providing the supply to our node */
    private Edge inEdge;
    /** correction if inEdge's "from-to" sense reversed */
    int invertSlope = 1;
    double bedctrl = 0;

    /**
     * determine functional (supply) link and "upstream" node from which to
     * obtain required parameters, and initialize holon parameter indices.
     */
    public void setDependencies()
    {

        // this boundary class will require two parameters: input node ID
        // and input link id. this will allow a check to make sure that
        // these two objects are actually connected before continuing.
        // also, it makes it a lot easier to steal the following values

        HStateStr linkID = (HStateStr) getInitHState("LINKID");
        UniqueID uid = UniqueIDMgr.cast(linkID.v);
        if (uid == null)
            initError("Specified link id " + linkID.v + " does not exist.");
        inEdge = EdgeGenerator.get(uid);
        Boundary h = ((Boundary) myHolon);
        if (inEdge.getPatch() != h.getPatch())
        {
            if (inEdge.getConnected().getPatch() != h.getPatch())
            {
                initError("Provided Link ID (" + linkID + ") doesn't connect to our node (" + h.getPatch().getUID()
                        + ").");
            }
            else
                invertSlope = -1;
        }
        // rexp = (HStateDbl) getInitHState( inEdge, "RADEXP");
        // vexp = (HStateDbl) getInitHState( inEdge, "VELEXP");

        try
        {
            friction = (HStateDbl) inEdge.getHState("C");
            getInitHState(inEdge, "WATER");
        }
        catch (HStateNotFoundException e)
        {
            Logger.logError("Boundary condition cannot find upstream roughness.");
        }
        bedslope = (HStateDbl) getInitHState(inEdge, "BEDSLOPE");
        hradius = (HStateDbl) getInitHState(inEdge, "HRADIUS");
        hydgrad = (HStateDbl) getInitHState(inEdge, "HYDROGRAD");
        xarea = (HStateDbl) getInitHState(inEdge, "XSECT_CURR");
        ovrdslope = (HStateDbl) getInitHState("BEDSLOPE");

    }

    public double initValue()
    {
        if (ovrdslope.v == 0)
        {
            bedctrl = bedslope.v;
        }
        else
        {
            bedctrl = ovrdslope.v;
        }
        return isNil() ? 0.0 : getValue();
    }

    public double computeValue()
    {

        // double gradient = (bedslope.v * invertSlope < 0.01)
        // ? bedslope.v * invertSlope
        // : hydgrad.v * invertSlope;
        double gradient = (bedctrl < 0.01) ? bedctrl : hydgrad.v;
        double v = 0.;
        if (gradient > 0)
            v = -xarea.v * Math.pow((Math.pow(hradius.v, rexp) * gradient / friction.v), (1 / vexp));
        return v;
    }

}
