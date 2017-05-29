package stream_metab.water.edge.hdarcy;

import neo.holon.Edge;
import neo.holon.Holon;
import neo.motif.AbstractUpdaterDbl;
import neo.state.HStateDbl;

public class HydroGrad extends AbstractUpdaterDbl {

    private HStateDbl linklength = null;
    private HStateDbl f_head = null;
    private HStateDbl t_head = null;

    private Holon frHolon;
    private Holon toHolon;

    /**
     * the parameter indices to be initialized within this method must already
     * be registered in the respective holons.
     */
    public void setDependencies()
    {
        frHolon = ((Edge) myHolon).getFrom();
        toHolon = ((Edge) myHolon).getTo();

        linklength = (HStateDbl) getInitHState("LINKLENGTH");
        f_head = (HStateDbl) getInitHState(frHolon, "HEAD");
        t_head = (HStateDbl) getInitHState(toHolon, "HEAD");
    }

    public double initValue()
    {
        return computeValue();
    }

    /**
     * compute horizontal groundwater flux
     * 
     * @param val
     *            ignored as part of interface method
     * @return flux amount in cubic meters
     */
    public double computeValue()
    {
        return (f_head.v - t_head.v) / linklength.v;
    }
}
