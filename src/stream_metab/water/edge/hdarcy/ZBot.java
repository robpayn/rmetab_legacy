package stream_metab.water.edge.hdarcy;

import neo.holon.Edge;
import neo.holon.Holon;
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
public class ZBot extends AbstractParameterDbl {

    HStateDbl zbot = null;
    HStateDbl frac = null;
    HStateDbl f_zbot = null;
    HStateDbl t_zbot = null;

    private boolean calculate = false;

    protected void setDependencies()
    {
        Holon frHolon = ((Edge) myHolon).getFrom();
        Holon toHolon = ((Edge) myHolon).getTo();
        zbot = (HStateDbl) getInitHState("ZBot");
        frac = (HStateDbl) getInitHState("BoundaryFrac");
        f_zbot = (HStateDbl) getInitHState(frHolon, "ZBot");
        t_zbot = (HStateDbl) getInitHState(toHolon, "ZBot");
        if (isNil())
            calculate = true;

    }

    protected double initValue()
    {
        return (calculate) ? (1 - frac.v) * f_zbot.v + frac.v * t_zbot.v : zbot.v;
    }
}
