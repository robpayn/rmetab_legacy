package stream_metab.water.patch.channel;

import neo.motif.AbstractUpdaterDbl;
import neo.state.HStateDbl;

/** compute wetted surface area (plan view) with current depth. */
public class WettedArea extends AbstractUpdaterDbl {

    private HStateDbl ovrbnk = null;
    private HStateDbl as = null;
    private HStateDbl asmax = null;
    private HStateDbl delas = null;
    private HStateDbl depth = null;
    private HStateDbl head = null;
    private HStateDbl water = null;

    public void setDependencies()
    {

        ovrbnk = (HStateDbl) getInitHState("ZTOP");
        as = (HStateDbl) getInitHState("AS");
        asmax = (HStateDbl) getInitHState("ASMAX");
        delas = (HStateDbl) getInitHState("DELAS");
        depth = (HStateDbl) getInitHState("DEPTH");
        head = (HStateDbl) getInitHState("HEAD");
        water = (HStateDbl) getInitHState("WATER");
    }

    public double initValue()
    {
        return computeValue();
    }

    /*-------------------------*/
    public double computeValue()
    {
        if (water.v <= 0)
            return 0.;
        else if (head.v > ovrbnk.v)
            return asmax.v;
        else
            return as.v + (delas.v * depth.v);
    }
}
