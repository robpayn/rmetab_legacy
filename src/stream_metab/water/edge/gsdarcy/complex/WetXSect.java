package stream_metab.water.edge.gsdarcy.complex;

import neo.holon.Edge;
import neo.holon.Holon;
import neo.motif.AbstractUpdaterDbl;
import neo.state.HStateDbl;
import neo.table.TabledInterpolater;
import neo.util.ItemNotFoundException;
import neo.util.Param;

/**
 * Compute wetted cross-sectional area between a surface (channel) node and the
 * ground node immediately below. Input data must always code the link with
 * surface as the "to" node and ground as the "from" node so that upward flow is
 * positive and downward flow is negative.
 */
public class WetXSect extends AbstractUpdaterDbl {

    private Holon sHolon;

    private HStateDbl g_head = null;
    private HStateDbl s_head = null;
    private HStateDbl xarea_a = null;
    private HStateDbl xarea_b = null;
    private HStateDbl xarea_n = null;
    private HStateDbl d_max = null;
    private HStateDbl s_zmin = null;
    private HStateDbl asmax = null;

    private double xarea;
    private double d;
    private double dd;
    private double ddd;

    private double[] vect;

    public void setDependencies()
    {

        Holon sHolon = ((Edge) myHolon).getTo();
        Holon gHolon = ((Edge) myHolon).getFrom();

        if (!Param.interpGeom)
        {
            xarea_a = (HStateDbl) getInitHState("XAREA_a");
            xarea_b = (HStateDbl) getInitHState("XAREA_b");
            xarea_n = (HStateDbl) getInitHState("XAREA_n");
        }
        d_max = (HStateDbl) getInitHState("MAXDEPTH");
        s_zmin = (HStateDbl) getInitHState(sHolon, "ZBOT");
        s_head = (HStateDbl) getInitHState(sHolon, "HEAD");
        g_head = (HStateDbl) getInitHState(gHolon, "HEAD");
        asmax = (HStateDbl) getInitHState(gHolon, "SURFAREA");
    }

    public double initValue()
    {
        try
        {
            vect = TabledInterpolater.getVector("DepthToGSWet", myHolon.getUID());
        }
        catch (ItemNotFoundException e)
        {
            initError("Table 'DepthToGSWet' not available for interpolation of wetted area of vertical exchange");
        }

        return computeValue();
    }

    public double computeValue()
    {

        double ctrlHead = Math.max(s_head.v, g_head.v);
        // ctrlHead = Math.min(s_zmax.v, ctrlHead);

        d = Math.max(0.0, ctrlHead - s_zmin.v);

        if (d == 0)
            return 0;
        if (d > d_max.v)
            return asmax.v;

        if (!Param.interpGeom)
        {
            xarea = xarea_a.v / (1 + xarea_b.v * Math.pow(d, xarea_n.v));
        }
        else
        {
            xarea = TabledInterpolater.getValue(vect, d / d_max.v);
        }

        if (xarea < 0)
        {
            initFatal("Wetted area (" + xarea + ") < 0 when depth = " + d + ".");
        }

        // Since GW patches are different sizes than their corresponding SW
        // patches, need
        // to constrain surface area by surface area of GW patch. Is this the
        // best assumption??
        if (xarea > asmax.v)
            return asmax.v;

        return xarea;
    }
}
