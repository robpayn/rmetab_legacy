package stream_metab.water.edge.manning;

import neo.motif.AbstractSimpleCalculatorDbl;
import neo.state.HStateDbl;

/**
 * velocity for surface water exchange. If depth < min depth (1 cm) set velocity
 * to 0. Note that this is simply a placeholder class so the value can be
 * written to output. <b>The value is computed and stored in the Fluxer
 * class.</b>
 */
public class Velocity extends AbstractSimpleCalculatorDbl {

    HStateDbl xsectpre = null;
    HStateDbl xsectcur = null;
    HStateDbl flow = null;

    public void setDependencies()
    {

        xsectpre = (HStateDbl) getInitHState("XSECT_PREV");
        xsectcur = (HStateDbl) getInitHState("XSECT_CURR");
        flow = (HStateDbl) getInitHState("WATER");
    }

    public double initValue()
    {
        return (isNil()) ? 0.0 : getValue();
    }

    public double computeValue()
    {
        // compute velocity based on last timestep

        double v = (xsectcur.v > 0) ? 2 * (flow.v / (xsectcur.v + xsectpre.v)) : 0;
        if (Math.abs(v) > Utility.maxVelocity)
        {
            initError("Velocity > MaxVelocity stated in water.edge.manning.Utility");
            v = Utility.maxVelocity * v / Math.abs(v);
        }
        return v;
    }

}
