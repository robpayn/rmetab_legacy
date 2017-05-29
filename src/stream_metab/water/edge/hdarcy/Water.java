package stream_metab.water.edge.hdarcy;

import neo.holon.Edge;
import neo.holon.Holon;
import neo.motif.AbstractRoute;
import neo.state.HStateDbl;

/**
 * This is the class loaded by the Monad (FluxPath) to act as its identity
 * through which the fluxing algorithm is called. This class is responsible for
 * managing required dynamic properties, including the one that calculates the
 * flux (see Fluxer). The constructor installs required dynamic property objects
 * (via AbstractMonadIdentity) in the specific update order. In other words,
 * calculation of some properties are sequentially dependent such that to be
 * properly update, the HProperty's update() methods must be called in a
 * specific order.
 */

public class Water extends AbstractRoute {

    private HStateDbl wetxsect = null;
    private HStateDbl k = null;
    private HStateDbl hydroGrad = null;
    private HStateDbl t_fieldCap = null;
    private HStateDbl f_fieldCap = null;
    private HStateDbl t_soilM = null;
    private HStateDbl f_soilM = null;

    /**
     * the parameter indices to be initialized within this method must already
     * be registered in the respective holons.
     */
    public void setDependencies()
    {
        Holon t_Holon = ((Edge) myHolon).getTo();
        Holon f_Holon = ((Edge) myHolon).getFrom();

        wetxsect = (HStateDbl) getInitHState("WETXSECT");
        k = (HStateDbl) getInitHState("K");
        hydroGrad = (HStateDbl) getInitHState("HYDROGRAD");
        t_fieldCap = (HStateDbl) getInitHState(t_Holon, "FIELDCAP");
        f_fieldCap = (HStateDbl) getInitHState(f_Holon, "FIELDCAP");
        t_soilM = (HStateDbl) getInitHState(t_Holon, "SOILMOISTURE");
        f_soilM = (HStateDbl) getInitHState(f_Holon, "SOILMOISTURE");

    }

    public double initValue()
    {
        return 0.0;
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
        if (hydroGrad.v > 0 && f_soilM.v <= f_fieldCap.v)
            return 0.0;
        else if (t_soilM.v <= t_fieldCap.v)
            return 0.0;
        else
            return (k.v * wetxsect.v * hydroGrad.v);
    }
}
