package stream_metab.water.edge.manning;

import neo.holon.Edge;
import neo.holon.Patch;
import neo.motif.AbstractParameterDbl;
import neo.state.HStateDbl;
import neo.state.HStateStr;
import neo.uid.UniqueID;

public class ChannelZ extends AbstractParameterDbl {

    HStateStr calcz = null;
    HStateDbl zchan = null;
    HStateDbl f_zcoord = null;
    HStateDbl t_zcoord = null;
    HStateDbl frac = null;
    UniqueID id;
    UniqueID f_id;
    UniqueID t_id;

    Patch frHolon = null;
    Patch toHolon = null;

    public void setDependencies()
    {

        frHolon = ((Edge) myHolon).getFrom();
        toHolon = ((Edge) myHolon).getTo();

        calcz = (HStateStr) getInitHState("CALCZ");
        zchan = (HStateDbl) getInitHState("ZCHAN");
        f_zcoord = (HStateDbl) getInitHState(frHolon, "ZBOT");
        t_zcoord = (HStateDbl) getInitHState(toHolon, "ZBOT");
        id = myHolon.getUID();
        f_id = frHolon.getUID();
        t_id = toHolon.getUID();
        frac = (HStateDbl) getInitHState("BOUNDARYFRAC");

    }

    public double initValue()
    {

        double defaultval = (1 - frac.v) * f_zcoord.v + frac.v * t_zcoord.v;
        double v = (calcz.v.substring(0, 1).toUpperCase().equals("N")) ? zchan.v : defaultval;

        if (defaultval > v)
            initError("Channel Z(" + id + ")=" + v + " < IDW average of attached node Z values: " + "From(" + f_id
                    + ")=" + f_zcoord.v + "," + "To(" + t_id + ")=" + t_zcoord.v);
        return v;
    }

}
