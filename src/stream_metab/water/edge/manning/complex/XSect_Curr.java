package stream_metab.water.edge.manning.complex;

import neo.motif.AbstractUpdaterDbl;
import neo.motif.SimpleCalculator;
import neo.state.HStateDbl;
import neo.table.TabledInterpolater;
import neo.util.ItemNotFoundException;
import neo.util.Param;

public class XSect_Curr extends AbstractUpdaterDbl {

    private HStateDbl depth = null;
    private HStateDbl xarea_a = null;
    private HStateDbl xarea_b = null;
    private HStateDbl xarea_n = null;
    private HStateDbl maxarea = null;
    private HStateDbl maxxwidth = null;
    private HStateDbl maxdepth = null;

    private SimpleCalculator xsect_prevCalc = null;
    private HStateDbl xsect_prev = null;

    private double[] vect;

    public void setDependencies()
    {

        depth = (HStateDbl) getInitHState("DEPTH");

        if (!Param.interpGeom)
        {
            xarea_a = (HStateDbl) getInitHState("XAREA_A");
            xarea_b = (HStateDbl) getInitHState("XAREA_B");
            xarea_n = (HStateDbl) getInitHState("XAREA_N");
        }

        maxdepth = (HStateDbl) getInitHState("MAXDEPTH");
        // wetted width at the highest water depth from input table
        maxxwidth = (HStateDbl) getInitHState("MAXXWIDTH");
        maxarea = (HStateDbl) getInitHState("MAXXAREA");

        try
        {
            xsect_prev = (HStateDbl) myHolon.getHState("XSECT_PREV");
            xsect_prevCalc = (SimpleCalculator) xsect_prev.getPrimaryModifier();
        }
        catch (Exception e)
        {
            initError("Can't find state for calculator Xsect_Prev");
        }
    }

    public double initValue()
    {
        try
        {
            vect = TabledInterpolater.getVector("DepthToXArea", myHolon.getUID());
        }
        catch (ItemNotFoundException e)
        {
            initError("Table 'DepthToXArea' not available for interpolation of channel cross section area from depth");
        }

        xsect_prevCalc.initialize();
        return xsect_prev.v;
    }

    public double computeValue()
    {

        double d, xtra_d, xarea;

        if (depth.v <= 0)
            return 0;

        xsect_prevCalc.calculate();

        if (depth.v > maxdepth.v)
        {
            d = maxdepth.v;
            xtra_d = depth.v - maxdepth.v;
        }
        else
        {
            d = depth.v;
            xtra_d = 0;
        }

        if (!Param.interpGeom)
            xarea = Math.pow(10, xarea_a.v / ((1 + xarea_b.v * Math.pow(d, xarea_n.v)))) - 1;
        else
            xarea = TabledInterpolater.getValue(vect, d / maxdepth.v);

        // If water depth is higher than the maximum depth, then we need to know
        // the wetted width at the highest depth
        // to compute the additional cross section of water crossing the edge.
        // The previous version used the maximum width.
        // However, now that width may both increase and decrease with depth
        // (i.e., as parameterized in the interpolation table)
        // the maximum width isn't necessarily the width at the highest water
        // depth

        return Math.min(xarea, maxarea.v) + xtra_d * maxxwidth.v;

    }
}
