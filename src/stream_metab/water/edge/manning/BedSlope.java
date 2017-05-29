package stream_metab.water.edge.manning;

import neo.holon.Edge;
import neo.holon.Holon;
import neo.motif.AbstractParameterDbl;
import neo.state.HStateDbl;

public class BedSlope extends AbstractParameterDbl {

    private HStateDbl t_zcoord = null;
    private HStateDbl f_zcoord = null;
    private HStateDbl length = null;

    private Holon frHolon;
    private Holon toHolon;

    public void setDependencies()
    {
        frHolon = ((Edge) myHolon).getFrom();
        toHolon = ((Edge) myHolon).getTo();

        length = (HStateDbl) getInitHState("LINKLENGTH");
        f_zcoord = (HStateDbl) getInitHState(frHolon, "ZBOT");
        t_zcoord = (HStateDbl) getInitHState(toHolon, "ZBOT");

    }

    public double initValue()
    {
        assert length.v > 0 : "LINKLENGTH <= 0";
        return (f_zcoord.v - t_zcoord.v) / length.v;
    }

}
