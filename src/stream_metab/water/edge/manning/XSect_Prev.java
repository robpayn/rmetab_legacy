package stream_metab.water.edge.manning;

import neo.motif.AbstractSimpleCalculatorDbl;
import neo.motif.Calculator;
import neo.state.HStateDbl;

public class XSect_Prev extends AbstractSimpleCalculatorDbl {

    HStateDbl xsect = null;
    HStateDbl depth = null;

    public void setDependencies()
    {
        xsect = (HStateDbl) getInitHState("XSECT_CURR");
        depth = (HStateDbl) getInitHState("DEPTH");
    }

    public double initValue()
    {
        Calculator c = (Calculator) xsect.getPrimaryModifier();
        c.calculate();
        return xsect.v;
    }

    public double computeValue()
    {
        return (depth.v > 0) ? xsect.v : 0;
    }

}
