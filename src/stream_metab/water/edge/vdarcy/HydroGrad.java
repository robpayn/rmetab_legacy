package stream_metab.water.edge.vdarcy;

import neo.holon.Edge;
import neo.holon.Patch;
import neo.motif.AbstractUpdaterDbl;
import neo.state.HStateDbl;

public class HydroGrad extends AbstractUpdaterDbl {
    HStateDbl length = null;
    HStateDbl f_head = null;
    HStateDbl t_head = null;
    HStateDbl t_bot = null;

    public void setDependencies()
    {
        Patch frHolon = ((Edge) myHolon).getFrom();
        Patch toHolon = ((Edge) myHolon).getTo();

        length = (HStateDbl) getInitHState("LINKLENGTH");
        f_head = (HStateDbl) getInitHState(frHolon, "HEAD");
        t_head = (HStateDbl) getInitHState(toHolon, "HEAD");
        t_bot = (HStateDbl) getInitHState(toHolon, "ZBOT");

    }

    public double initValue()
    {
        return computeValue();
    }

    public double computeValue()
    {
        return (t_head.v > t_bot.v) ? (f_head.v - t_head.v) / length.v : 0.0;
    }

}
