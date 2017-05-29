package stream_metab.water.patch.porous;

import neo.motif.AbstractParameterDbl;
import neo.state.HStateDbl;

public class GrossVolume extends AbstractParameterDbl {

    HStateDbl zTop = null;
    HStateDbl zBot = null;
    HStateDbl surfarea = null;

    public void setDependencies()
    {

        zTop = (HStateDbl) getInitHState("ZTOP");
        zBot = (HStateDbl) getInitHState("ZBOT");
        surfarea = (HStateDbl) getInitHState("SURFAREA");
    }

    public double initValue()
    {
        assert surfarea.v > 0 : "surfaraea.v <= 0";

        if (zTop.v <= zBot.v)
            initError("ZTop is <= ZBot");

        return surfarea.v * (zTop.v - zBot.v);
    }

}
