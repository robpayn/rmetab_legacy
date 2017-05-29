package stream_metab.water.patch.porous;

import neo.motif.AbstractParameterDbl;
import neo.state.HStateDbl;

public class ZCoord extends AbstractParameterDbl {

    HStateDbl ztop = null;
    HStateDbl zbot = null;

    public void setDependencies()
    {

        ztop = (HStateDbl) getInitHState("ZTOP");
        zbot = (HStateDbl) getInitHState("ZBOT");
    }

    public double initValue()
    {
        return 0.5 * (ztop.v + zbot.v);
    }

}
