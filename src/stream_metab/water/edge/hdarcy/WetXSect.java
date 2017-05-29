package stream_metab.water.edge.hdarcy;

import neo.holon.Edge;
import neo.holon.Holon;
import neo.motif.AbstractUpdaterDbl;
import neo.state.HStateDbl;

/**
 * Cross-sectional area of the water-table between horizontal nodes. Doesn't get
 * computed if link is vertical.
 */

public class WetXSect extends AbstractUpdaterDbl {

    private HStateDbl xsectarea = null;
    // private HStateXXX f_xsect = null;
    private HStateDbl f_head = null;
    private HStateDbl t_head = null;
    private HStateDbl ztop = null;
    private HStateDbl zbot = null;
    private HStateDbl frac = null;
    private HStateDbl thick = null;

    private Holon frHolon;
    private Holon toHolon;

    public void setDependencies()
    {

        frHolon = ((Edge) myHolon).getFrom();
        toHolon = ((Edge) myHolon).getTo();

        xsectarea = (HStateDbl) getInitHState("XSECTAREA");
        f_head = (HStateDbl) getInitHState(frHolon, "HEAD");
        t_head = (HStateDbl) getInitHState(toHolon, "HEAD");
        ztop = (HStateDbl) getInitHState("ZTOP");
        zbot = (HStateDbl) getInitHState("ZBOT");
        frac = (HStateDbl) getInitHState("BoundaryFrac");
        thick = (HStateDbl) getInitHState("Thickness");

    }

    public double initValue()
    {
        return computeValue();
    }

    /*-------------------------*/
    public double computeValue()
    {
        double v;
        double avghead = (1 - frac.v) * f_head.v + frac.v * t_head.v;
        if (avghead >= ztop.v)
            v = xsectarea.v;
        else if (avghead > zbot.v)
            v = ((avghead - zbot.v) / (thick.v)) * xsectarea.v;
        else
            v = 0;

        return v;
    }
}
