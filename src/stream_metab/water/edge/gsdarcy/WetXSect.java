package stream_metab.water.edge.gsdarcy;

import neo.holon.Edge;
import neo.holon.Holon;
import neo.motif.AbstractUpdaterDbl;
import neo.state.HStateDbl;

/**
 * Compute wetted cross-sectional area between a surface (channel) node and the
 * ground node immediately below. Input data must always code the link with
 * surface as the "to" node and ground as the "from" node so that upward flow is
 * positive and downward flow is negative.
 */
public class WetXSect extends AbstractUpdaterDbl {

    private HStateDbl g_head = null;
    private HStateDbl s_head = null;
    private HStateDbl s_zmax = null;
    private HStateDbl s_zmin = null;
    private HStateDbl asmax = null;
    private HStateDbl delas = null;
    private HStateDbl s_wettedarea = null;

    private HStateDbl as = null;

    public void setDependencies()
    {

        Holon sHolon = ((Edge) myHolon).getTo();
        Holon gHolon = ((Edge) myHolon).getFrom();

        as = (HStateDbl) getInitHState(sHolon, "AS");
        asmax = (HStateDbl) getInitHState(sHolon, "ASMAX");
        g_head = (HStateDbl) getInitHState(gHolon, "HEAD");
        s_head = (HStateDbl) getInitHState(sHolon, "HEAD");
        delas = (HStateDbl) getInitHState(sHolon, "DELAS");
        s_zmax = (HStateDbl) getInitHState(sHolon, "ZTOP");
        s_zmin = (HStateDbl) getInitHState(sHolon, "ZBOT");
        s_wettedarea = (HStateDbl) getInitHState(sHolon, "WETTEDAREA");

    }

    public double initValue()
    {
        return computeValue();
    }

    public double computeValue()
    {

        if (s_head.v >= g_head.v)
            return s_wettedarea.v;
        else if (g_head.v > s_zmax.v)
            return asmax.v;
        else
            return as.v + (delas.v * (g_head.v - s_zmin.v));
    }

}
