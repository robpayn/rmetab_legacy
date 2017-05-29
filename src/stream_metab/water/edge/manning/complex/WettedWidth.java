package stream_metab.water.edge.manning.complex;

import neo.motif.AbstractUpdaterDbl;
import neo.state.HStateDbl;
import neo.table.TabledInterpolater;
import neo.util.ItemNotFoundException;
import neo.util.Param;

public class WettedWidth extends AbstractUpdaterDbl {

    private HStateDbl depth = null;
    private HStateDbl width_a = null;
    private HStateDbl width_b = null;
    private HStateDbl width_n = null;
    private HStateDbl maxwidth = null;
    private HStateDbl maxdepth = null;
    private HStateDbl xarea = null;

    private double[] vect;

    public void setDependencies()
    {
        depth = (HStateDbl) getInitHState("DEPTH");
        maxdepth = (HStateDbl) getInitHState("MAXDEPTH");
        maxwidth = (HStateDbl) getInitHState("MAXWIDTH");
        xarea = (HStateDbl) getInitHState("XSECT_CURR");

        if (!Param.interpGeom)
        {
            width_a = (HStateDbl) getInitHState("WETWID_A");
            width_b = (HStateDbl) getInitHState("WETWID_B");
            width_n = (HStateDbl) getInitHState("WETWID_N");
        }
    }

    public double initValue()
    {
        try
        {
            vect = TabledInterpolater.getVector("DepthToWetWidth", myHolon.getUID());
        }
        catch (ItemNotFoundException e)
        {
            initError("Table 'DepthToWetWidth' not loaded for interpolation of channel width from depth");
        }
        return computeValue();
    }

    public double computeValue()
    {
        double w;

        if (depth.v <= 0)
            return 0;

        double d = Math.min(depth.v, maxdepth.v);

        /**
         * Find the larger of either xarea/depth or the calculated width using
         * the fit equation. Check against xarea/depth is needed incase equation
         * estimate is substantially too small due to occational bad fits in
         * parts of the equation.
         */

        if (!Param.interpGeom)
        {
            w = Math.max(xarea.v / depth.v, Math.pow(10, width_a.v / (1 + width_b.v * Math.pow(d, width_n.v))) - 1);
        }
        else
        {
            w = Math.max(TabledInterpolater.getValue(vect, d / maxdepth.v), xarea.v / depth.v);
        }
        return Math.min(w, maxwidth.v);
    }

}
