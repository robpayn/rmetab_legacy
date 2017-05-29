package stream_metab.water.edge.vdarcy;

import neo.holon.Edge;
import neo.holon.Patch;
import neo.motif.AbstractParameterDbl;
import neo.state.HStateDbl;

/**
 * area of plane section shared between two compartments. Read from input, or
 * computed if input value is zero.
 */
public class XSectArea extends AbstractParameterDbl {

    private HStateDbl xarea = null;
    private HStateDbl linklen = null;
    private HStateDbl f_surfarea = null;
    private HStateDbl t_surfarea = null;

    public void setDependencies()
    {

        Patch frHolon = ((Edge) myHolon).getFrom();
        Patch toHolon = ((Edge) myHolon).getTo();

        xarea = (HStateDbl) getInitHState("XAREA");
        linklen = (HStateDbl) getInitHState("LINKLENGTH");
        f_surfarea = (HStateDbl) getInitHState(frHolon, "SURFAREA");
        t_surfarea = (HStateDbl) getInitHState(toHolon, "SURFAREA");

    }

    public double initValue()
    {
        double v;
        assert linklen.v > 0 : "LinkLength <= 0";
        assert f_surfarea.v > 0 : "f_surfarea.v <= 0";
        assert t_surfarea.v > 0 : "t_surfarea.v <= 0";

        if (xarea.v == 0)
        {
            // check to be sure f_surfarea and t_surfarea are within 0.1% of
            // each other.
            if (Math.abs(1 - (f_surfarea.v / t_surfarea.v)) > 0.001)
                initError("User must provide xarea of vdarcy link when"
                        + " surface areas of associated nodes are unequal.");
            v = f_surfarea.v;
        }
        else
        {
            v = xarea.v;
        }

        if (v <= 0)
            initError("XSECTAREA(" + v + ") <= 0");

        return (v);
    }
}
