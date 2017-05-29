package stream_metab.water.edge.manningcalib;

import neo.holon.*;
import neo.motif.*;
import neo.state.*;
import neo.uid.*;

public class WaterPredicted extends AbstractUpdaterDbl {
    
    private HStateDbl flow;

    /**
     * perform the (possibly complex) calculation of flux between the associated
     * compartments.
     */
    protected double computeValue()
    {
        return flow.v;
    }

    protected double initValue()
    {
        return flow.v;
    }

    public void setDependencies()
    {
        HStateStr linkID = (HStateStr) getInitHState("EDGENAME");
        UniqueID uid = UniqueIDMgr.cast(linkID.v);
        if (uid == null)
        {
            initError("Specified link id " + linkID.v + " does not exist.");
        }
        Edge inEdge = EdgeGenerator.get(uid);
        Edge h = (Edge)myHolon;
        if (inEdge.getFrom() != h.getFrom())
        {
            initError("Provided Link ID (" + linkID + ") doesn't connect to our node (" + h.getPatch().getUID() + ").");
        }
        flow = (HStateDbl) getInitHState(inEdge, "WATER");
    }

}
