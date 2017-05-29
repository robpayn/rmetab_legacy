package stream_metab.water.patch.channel;

import neo.motif.AbstractUpdaterDbl;
import neo.state.HStateDbl;

/** Water depth in meters. accounts for prismatic and parallel bank sides. */
public class Depth extends AbstractUpdaterDbl {

    private HStateDbl as = null;
    private HStateDbl asmax = null;
    private HStateDbl delas = null;
    private HStateDbl h2o = null;
    private HStateDbl ztop = null;
    private HStateDbl zbot = null;

    public void setDependencies()
    {

        as = (HStateDbl) getInitHState("AS");
        asmax = (HStateDbl) getInitHState("ASMAX");
        delas = (HStateDbl) getInitHState("DELAS");
        h2o = (HStateDbl) getInitHState("WATER");
        ztop = (HStateDbl) getInitHState("ZTOP");
        zbot = (HStateDbl) getInitHState("ZBOT");

    }

    public double initValue()
    {
        return computeValue();
    }

    public double computeValue()
    {
        if (h2o.v == 0.)
            return 0.;

        double d = 0.;
        double maxdepth = ztop.v - zbot.v;
        if (delas.v == 0)
            d = h2o.v / as.v;
        else
            d = (Math.sqrt(as.v * as.v + 2 * delas.v * h2o.v) - as.v) / delas.v;

        if (d > maxdepth)
        {
            double vol = (maxdepth * as.v) + (maxdepth * maxdepth * delas.v / 2);
            d = maxdepth + (h2o.v - vol) / asmax.v;
        }
        return d;

    }

}
