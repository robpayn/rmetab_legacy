package stream_metab.water.edge.manning;

import neo.motif.AbstractUpdaterDbl;
import neo.state.HStateDbl;
import neo.util.Param;

/** Hydraulic radius. Exact results depend on Param.WDWBM. */
public class HRadius extends AbstractUpdaterDbl {

    private HStateDbl depth = null;
    private HStateDbl xsectcur = null;
    private HStateDbl wetwth = null;

    public void setDependencies()
    {

        depth = (HStateDbl) getInitHState("DEPTH");
        wetwth = (HStateDbl) getInitHState("WETTEDWIDTH");
        xsectcur = (HStateDbl) getInitHState("XSECT_CURR");

    }

    public double initValue()
    {
        return computeValue();

    }

    public double computeValue()
    {
        if (depth.v <= 0.)
            return 0.;
        double r = (Param.WDWBM) ? depth.v : xsectcur.v / wetwth.v;
        if (r > Utility.maxHydroRad)
        {
            initError("HydRad > MaxHydroRad stated in water.edge.manning.Utility");
            r = Utility.maxHydroRad;
        }
        return r;

    }

}
