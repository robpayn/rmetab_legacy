package stream_metab.water.edge.vdarcy;

import neo.holon.Edge;
import neo.holon.Patch;
import neo.motif.AbstractParameterDbl;
import neo.state.HStateDbl;
import stream_metab.water.Utility;

public class LinkLength extends AbstractParameterDbl {

    HStateDbl length = null;
    HStateDbl f_zcoord = null;
    HStateDbl t_zcoord = null;

    public void setDependencies()
    {
        Patch frHolon = ((Edge) myHolon).getFrom();
        Patch toHolon = ((Edge) myHolon).getTo();

        length = (HStateDbl) getInitHState("LENGTH");
        f_zcoord = (HStateDbl) getInitHState(frHolon, "ZCOORD");
        t_zcoord = (HStateDbl) getInitHState(toHolon, "ZCOORD");

    }

    public double initValue()
    {
        double v = (length.v == 0) ? Utility.getLinkLengthVert(f_zcoord.v, t_zcoord.v) : length.v;
        if (v <= 0)
            initError("Negative vertical link length; 'from' and 'to' nodes may be reversed.");
        return (v);
    }
}
