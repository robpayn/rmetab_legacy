package stream_metab.water.patch.porous;

import neo.motif.AbstractUpdaterDbl;
import neo.state.HStateDbl;

/** Water state as 'head' in the compartment. */
public class SoilMoisture extends AbstractUpdaterDbl {

    private HStateDbl porosity = null;
    private HStateDbl h2o = null;
    private HStateDbl maxh2o = null;

    public void setDependencies()
    {

        porosity = (HStateDbl) getInitHState("POROSITY");
        h2o = (HStateDbl) getInitHState("WATER");
        maxh2o = (HStateDbl) getInitHState("MAXH2O");

    }

    public double initValue()
    {
        return computeValue();
    }

    public double computeValue()
    {

        return (h2o.v >= maxh2o.v) ? porosity.v : porosity.v * h2o.v / maxh2o.v;

    }

}
