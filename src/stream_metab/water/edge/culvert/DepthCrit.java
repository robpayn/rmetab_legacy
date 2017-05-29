package stream_metab.water.edge.culvert;

import neo.motif.*;
import neo.state.*;
import neo.util.*;

/**
 * Controls the critical depth for the current flow condition through the
 * culvert
 * 
 * @author robert.payn
 */
public class DepthCrit extends AbstractUpdaterDbl {

    /**
     * Diameter of the culvert [Length]
     */
    private HStateDbl diameter = null;
    /**
     * Initial flow through the culvert
     */
    private HStateDbl flowInit = null;
    /**
     * Volumetric flow of water through the culvert
     * [Length<sup><small>3</small></sup> Time<sup><small>-1</small></sup>]
     */
    private HStateDbl water = null;

    /**
     * Calculates critical depth
     * 
     * @return critical depth [Length]
     */
    @Override
    public double computeValue()
    {

        return 0.325 * Math.pow(water.v / diameter.v, 0.6666667) + 0.083 * diameter.v;

    }

    /**
     * Calculates initial critical depth
     * 
     * @return critical depth [Length]
     */
    @Override
    protected double initValue()
    {

        return 0.325 * Math.pow(flowInit.v / diameter.v, 0.6666667) + 0.083 * diameter.v;

    }

    /**
     * Define the state dependencies for calculation of critical depth
     */
    @Override
    protected void setDependencies()
    {

        try
        {
            diameter = (HStateDbl) getInitHState("DIAMETER");
            flowInit = (HStateDbl) getInitHState("FLOWINIT");
            // Note that critical depth is based on the flow during the previous
            // time step
            // (necessary to avoid circular dependency)
            water = (HStateDbl) myHolon.getHState("WATER");
        }
        catch (HStateNotFoundException e)
        {
            Logger.logError(e.getMessage());
        }

    }

}
