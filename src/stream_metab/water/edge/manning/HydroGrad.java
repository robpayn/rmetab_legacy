package stream_metab.water.edge.manning;

import neo.holon.Edge;
import neo.holon.Holon;
import neo.motif.AbstractUpdaterDbl;
import neo.state.HStateDbl;

/** hydraulogic gradient */
public class HydroGrad extends AbstractUpdaterDbl {

    private HStateDbl length = null;
    private HStateDbl t_head = null;
    private HStateDbl f_head = null;
    private HStateDbl depth = null;

    private Holon frHolon;
    private Holon toHolon;

    public void setDependencies()
    {
        frHolon = ((Edge) myHolon).getFrom();
        toHolon = ((Edge) myHolon).getTo();

        length = (HStateDbl) getInitHState("LINKLENGTH");
        f_head = (HStateDbl) getInitHState(frHolon, "HEAD");
        t_head = (HStateDbl) getInitHState(toHolon, "HEAD");
        depth = (HStateDbl) getInitHState("DEPTH");

    }

    public double initValue()
    {
        return computeValue();

    }

    public double computeValue()
    {
        if (depth.v <= 0)
            return 0;

        return (f_head.v - t_head.v) / length.v;

    }

}
