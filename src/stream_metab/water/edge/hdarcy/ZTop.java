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
public class ZTop extends AbstractParameterDbl {

    HStateDbl ztop = null;
    HStateDbl zbot = null;
    HStateDbl frac = null;
    HStateDbl f_ztop = null;
    HStateDbl t_ztop = null;
    private boolean calculate = false;

    protected void setDependencies()
    {
        Holon frHolon = ((Edge) myHolon).getFrom();
        Holon toHolon = ((Edge) myHolon).getTo();
        ztop = (HStateDbl) getInitHState("ZTop");
        frac = (HStateDbl) getInitHState("BoundaryFrac");
        f_ztop = (HStateDbl) getInitHState(frHolon, "ZTOP");
        t_ztop = (HStateDbl) getInitHState(toHolon, "ZTOP");
        if (isNil())
            calculate = true;

    }

    protected double initValue()
    {
        return (calculate) ? (1 - frac.v) * f_ztop.v + frac.v * t_ztop.v : ztop.v;

    }
}
