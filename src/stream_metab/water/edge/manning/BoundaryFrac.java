package stream_metab.water.edge.manning;

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
public class BoundaryFrac extends AbstractParameterDbl {
    HStateDbl frlength = null;
    HStateDbl tolength = null;

    protected void setDependencies()
    {
        frlength = (HStateDbl) getInitHState("FromLen");
        tolength = (HStateDbl) getInitHState("ToLen");
    }

    protected double initValue()
    {
        double v = 0.;
        if (frlength.v == 0 && tolength.v == 0)
            v = 0.5;
        else if (frlength.v > 0 && tolength.v > 0)
            v = frlength.v / (frlength.v + tolength.v);
        else
            initError("FromLength & ToLength improperly defined.");

        return v;
    }
}
