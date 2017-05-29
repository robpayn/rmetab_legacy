package stream_metab.water.patch.channel;

import neo.motif.AbstractUpdaterDbl;
import neo.state.HStateDbl;
import neo.util.Param;

/** Water volume when channel is at capacity. */
public class H2OMass extends AbstractUpdaterDbl {

    private HStateDbl watervol = null;

    public void setDependencies()
    {
        watervol = (HStateDbl) getInitHState("WATER");
    }

    public double initValue()
    {
        return computeValue();
    }

    public double computeValue()
    {
        return watervol.v * Param.H2Okgm3;
    }

}
