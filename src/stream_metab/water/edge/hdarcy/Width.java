package stream_metab.water.edge.hdarcy;

import neo.holon.Edge;
import neo.holon.Patch;
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
public class Width extends AbstractParameterDbl {
    private HStateDbl iniwidth = null;
    private HStateDbl frsurfarea = null;
    private HStateDbl tosurfarea = null;
    private HStateDbl linklength = null;

    protected void setDependencies()
    {
        Patch frHolon = ((Edge) myHolon).getFrom();
        Patch toHolon = ((Edge) myHolon).getTo();

        frsurfarea = (HStateDbl) getInitHState(frHolon, "surfarea");
        tosurfarea = (HStateDbl) getInitHState(toHolon, "surfarea");
        linklength = (HStateDbl) getInitHState("linklength");
        iniwidth = (HStateDbl) getInitHState("iniwidth");

    }

    protected double initValue()
    {
        if (iniwidth.v != 0.0)
            return iniwidth.v;
        else
            return ((frsurfarea.v + tosurfarea.v) / 2) / linklength.v;
    }

}
