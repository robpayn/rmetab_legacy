package stream_metab.water.edge.culvert;

import neo.motif.*;
import neo.state.*;

/**
 * Controls the effective angle for calculation of wetted geometry
 * (cross-sectional area and hydraulic radius)
 * 
 * @author robert.payn
 */
public class AngleGeom extends AbstractUpdaterDbl {

    /**
     * Effective depth of water in the culvert [Length]
     */
    private HStateDbl depth = null;
    /**
     * Diameter of the circular culvert [Length]
     */
    private HStateDbl diameter = null;

    /**
     * Calculates the effective angle for wetted geometry
     * 
     * @return angle (radians)
     */
    @Override
    protected double computeValue()
    {

        if (depth.v < 0.0)
        {
            return 0.0;
        }
        else
        {
            if (2 * depth.v > diameter.v)
            {
                return 2.0 * (3.141593 - Math.acos(((2 * depth.v) / diameter.v) - 1.0));
            }
            else
            {
                return 2.0 * Math.acos(1.0 - ((2 * depth.v) / diameter.v));
            }
        }

    }

    /**
     * Calculates the effective angle for wetted geometry
     * 
     * @return angle (radians)
     */
    @Override
    protected double initValue()
    {

        return computeValue();

    }

    /**
     * Defines the state dependencies for calculation of effective angle for
     * wetted geometry
     */
    @Override
    protected void setDependencies()
    {

        depth = (HStateDbl) getInitHState("DEPTH");
        diameter = (HStateDbl) getInitHState("DIAMETER");

    }

}
