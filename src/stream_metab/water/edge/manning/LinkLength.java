package stream_metab.water.edge.manning;

import neo.holon.Edge;
import neo.holon.Patch;
import neo.motif.AbstractParameterDbl;
import neo.state.HStateDbl;
import stream_metab.water.Utility;

/**
 * compute straight-line distance between connected nodes.
 */
public class LinkLength extends AbstractParameterDbl {

    HStateDbl frlength = null;
    HStateDbl tolength = null;
    HStateDbl f_xcoord = null;
    HStateDbl f_ycoord = null;
    HStateDbl f_zcoord = null;
    HStateDbl t_xcoord = null;
    HStateDbl t_ycoord = null;
    HStateDbl t_zcoord = null;

    Patch frHolon = null;
    Patch toHolon = null;

    public void setDependencies()
    {
        frHolon = ((Edge) myHolon).getFrom();
        toHolon = ((Edge) myHolon).getTo();

        frlength = (HStateDbl) getInitHState("FromLen");
        tolength = (HStateDbl) getInitHState("ToLen");
        f_xcoord = (HStateDbl) getInitHState(frHolon, "XCOORD");
        f_ycoord = (HStateDbl) getInitHState(frHolon, "YCOORD");
        f_zcoord = (HStateDbl) getInitHState(frHolon, "ZBOT");
        t_xcoord = (HStateDbl) getInitHState(toHolon, "XCOORD");
        t_ycoord = (HStateDbl) getInitHState(toHolon, "YCOORD");
        t_zcoord = (HStateDbl) getInitHState(toHolon, "ZBOT");

    }

    public double initValue()
    {
        double v = 0.;
        if (frlength.v == 0 && tolength.v == 0)
            v = Utility.getLinkLengthHoriz(f_xcoord.v, f_ycoord.v, f_zcoord.v, t_xcoord.v, t_ycoord.v, t_zcoord.v);
        else if (frlength.v > 0 && tolength.v > 0)
            v = frlength.v + tolength.v;

        else
            initError(" FromLength & ToLength improperly defined.");

        return v;
    }

}
