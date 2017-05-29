package stream_metab.water.edge.hdarcy;

import neo.motif.AbstractParameterDbl;
import neo.state.HStateDbl;

/**
 * <p>
 * Title: Restate ecosystem agent simulation system
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * <p>
 * Company: Eco-metrics, Inc.
 * </p>
 * 
 * @author Chris Bennett
 * @version 1.0
 */
public class Thickness extends AbstractParameterDbl {
    private HStateDbl ztop = null;
    private HStateDbl zbot = null;

    public void setDependencies()
    {

        ztop = (HStateDbl) getInitHState("ZTop");
        zbot = (HStateDbl) getInitHState("ZBot");
    }

    public double initValue()
    {

        double v = ztop.v - zbot.v;

        if (v <= 0)
            initError("THICKNESS(" + v + ") <= 0");

        return v;
    }
}
