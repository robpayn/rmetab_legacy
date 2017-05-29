package stream_metab.water.edge.gsdarcy;

import neo.holon.Edge;
import neo.holon.Patch;
import neo.motif.AbstractParameterDbl;
import neo.state.HStateDbl;

public class LinkLength extends AbstractParameterDbl {

    HStateDbl length = null;
    HStateDbl surfElev = null;
    HStateDbl grndElev = null;
    HStateDbl elevAdj = null;

    public void setDependencies()
    {
        Patch frHolon = ((Edge) myHolon).getFrom();
        Patch toHolon = ((Edge) myHolon).getTo();

        length = (HStateDbl) getInitHState("LENGTH");
        surfElev = (HStateDbl) getInitHState(toHolon, "ZBOT");
        grndElev = (HStateDbl) getInitHState(frHolon, "ZCOORD");
    }

    public double initValue()
    {

        double v = 0;
        if (length.v < 0 || surfElev.v <= grndElev.v)
        {
            initError("Negative link length; 'from' and 'to' nodes may be reversed.");
        }
        else if (length.v == 0)
        {
            v = surfElev.v - grndElev.v;
        }
        else
        {
            v = length.v;
        }
        return v;
    }

}
