package stream_metab.water.edge.gsdarcy;

import neo.holon.Edge;
import neo.holon.Holon;
import neo.motif.AbstractUpdaterDbl;
import neo.state.HStateDbl;

/**
 * Flux between surface compartment and ground compartment.
 */

public class HydroGrad extends AbstractUpdaterDbl {

    private HStateDbl linklen = null;
    private HStateDbl g_head = null;
    private HStateDbl s_elev = null;
    private HStateDbl s_head = null;

    private Holon gHolon;
    private Holon sHolon;

    private double bedZBot;

    public void setDependencies()
    {

        // surface is always "to", ground is always "from"
        // so that upward flow is positive and downward flow is negative

        gHolon = ((Edge) myHolon).getFrom();
        sHolon = ((Edge) myHolon).getTo();

        s_head = (HStateDbl) getInitHState(sHolon, "HEAD");
        g_head = (HStateDbl) getInitHState(gHolon, "HEAD");
        linklen = (HStateDbl) getInitHState("LINKLENGTH");
        s_elev = (HStateDbl) getInitHState(sHolon, "ZBOT");
    }

    public double initValue()
    {
        return computeValue();

    }

    /*-------------------------*/

    public double computeValue()
    {
        // to match MODFLOW output, linklen is used as streambed thickness
        double bedZBot = s_elev.v - linklen.v;
        return (g_head.v >= bedZBot) ? (g_head.v - s_head.v) / linklen.v : (bedZBot - s_head.v) / linklen.v;
    }

}
