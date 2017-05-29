package stream_metab.water.edge.manning;

import neo.motif.AbstractUpdaterDbl;
import neo.motif.SimpleCalculator;
import neo.state.HStateDbl;
import neo.util.Param;

/**
 * Cross-sectional area of water link channel. Calculated with wetincr (delAs)
 * as twice the slope of one bank. If testing for Wetland Dynamic Water Budget
 * Model (WDWBM) results, multiplies wetted width * depth, otherwise, multiply
 * minimum wetted width (As or btmwth) plus wetinct (delAs) * depth.
 */
public class XSect_Curr extends AbstractUpdaterDbl {

    private HStateDbl depth = null;
    private HStateDbl wetwth = null;
    private HStateDbl btmwth = null;
    private HStateDbl delwth = null;

    private SimpleCalculator xsect_prevCalc = null;
    private HStateDbl xsect_prev = null;

    public void setDependencies()
    {

        btmwth = (HStateDbl) getInitHState("BTMWTH");
        delwth = (HStateDbl) getInitHState("WETTEDINCR");
        depth = (HStateDbl) getInitHState("DEPTH");
        wetwth = (HStateDbl) getInitHState("WettedWidth");

        try
        {
            xsect_prev = (HStateDbl) myHolon.getHState("XSECT_PREV");
            xsect_prevCalc = (SimpleCalculator) xsect_prev.getPrimaryModifier();
        }
        catch (Exception e)
        {
            initError("Can't find state for calculator Xsect_Prev");
        }
    }

    public double initValue()
    {
        xsect_prevCalc.initialize();
        return xsect_prev.v;
    }

    public double computeValue()
    {
        xsect_prevCalc.calculate();
        if (depth.v <= 0)
            return 0.;
        return (Param.WDWBM) ? wetwth.v * depth.v : (btmwth.v + (delwth.v * depth.v) / 2) * depth.v;
    }

}
