package stream_metab.water.edge.hdarcy;

import neo.holon.Edge;
import neo.holon.Patch;
import neo.motif.AbstractParameterDbl;
import neo.state.HStateDbl;
import stream_metab.water.Utility;

public class LinkLength extends AbstractParameterDbl {

    HStateDbl frlength = null;
    HStateDbl tolength = null;
    HStateDbl f_xcoord = null;
    HStateDbl f_ycoord = null;
    HStateDbl f_zcoord = null;
    HStateDbl t_xcoord = null;
    HStateDbl t_ycoord = null;
    HStateDbl t_zcoord = null;

    public void setDependencies()
    {
        Patch frHolon = ((Edge) myHolon).getFrom();
        Patch toHolon = ((Edge) myHolon).getTo();

        frlength = (HStateDbl) getInitHState("FromLen");
        tolength = (HStateDbl) getInitHState("ToLen");
        f_xcoord = (HStateDbl) getInitHState(frHolon, "XCOORD");
        f_ycoord = (HStateDbl) getInitHState(frHolon, "YCOORD");
        f_zcoord = (HStateDbl) getInitHState(frHolon, "ZCOORD");
        t_xcoord = (HStateDbl) getInitHState(toHolon, "XCOORD");
        t_ycoord = (HStateDbl) getInitHState(toHolon, "YCOORD");
        t_zcoord = (HStateDbl) getInitHState(toHolon, "ZCOORD");

    }

    public double initValue()
    {
        double len = frlength.v + tolength.v;
        double v = (len == 0) ? Utility.getLinkLengthHoriz(f_xcoord.v, f_ycoord.v, f_zcoord.v, t_xcoord.v, t_ycoord.v,
                t_zcoord.v) : len;
        if (v <= 0)
            initError("Linklength (" + v + ") <= 0");

        return v;
    }
}
