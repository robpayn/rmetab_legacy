package stream_metab.water.edge.manningcalib;

import neo.holon.Edge;
import neo.holon.EdgeGenerator;
import neo.motif.*;
import neo.state.HStateDbl;
import neo.state.HStateStr;
import neo.uid.UniqueID;
import neo.uid.UniqueIDMgr;

public class VelocityPredicted extends AbstractUpdaterDbl {
    
    private HStateDbl velocity;

    @Override
    protected double computeValue()
    {
        return velocity.v;
    }

    @Override
    protected double initValue()
    {
        return velocity.v;
    }

    @Override
    protected void setDependencies()
    {
        HStateStr linkID = (HStateStr) getInitHState("VELEDGENAME");
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
        velocity = (HStateDbl) getInitHState(inEdge, "VELOCITY");
    }

}
