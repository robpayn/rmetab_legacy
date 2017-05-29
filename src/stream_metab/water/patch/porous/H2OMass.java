package stream_metab.water.patch.porous;

import neo.motif.AbstractUpdaterDbl;
import neo.state.HStateDbl;
import neo.util.Param;
import stream_metab.water.Utility;

/** Mass (kg) of water currently stored. */
public class H2OMass extends AbstractUpdaterDbl {

    private HStateDbl watervol = null;
    HStateDbl inihead = null;
    HStateDbl ztop = null;
    HStateDbl zbot = null;
    HStateDbl porosity = null;
    HStateDbl secstore = null;
    HStateDbl surfarea = null;
    HStateDbl fieldcap = null;

    public void setDependencies()
    {
        watervol = (HStateDbl) getInitHState("WATER");
        inihead = (HStateDbl) getInitHState("INIHEAD");
        ztop = (HStateDbl) getInitHState("ZTOP");
        zbot = (HStateDbl) getInitHState("ZBOT");
        porosity = (HStateDbl) getInitHState("POROSITY");
        secstore = (HStateDbl) getInitHState("SECSTORE");
        surfarea = (HStateDbl) getInitHState("SURFAREA");
        fieldcap = (HStateDbl) getInitHState("FIELDCAP");
    }

    public double initValue()
    {
        double watvol = Utility.groundHeadToVol(inihead.v, ztop.v, zbot.v, porosity.v, secstore.v, surfarea.v,
                fieldcap.v);
        return watvol * Param.H2Okgm3;
    }

    public double computeValue()
    {
        return watervol.v * Param.H2Okgm3;
    }

}
