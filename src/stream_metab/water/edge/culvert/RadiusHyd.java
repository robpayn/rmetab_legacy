package stream_metab.water.edge.culvert;

import neo.motif.*;
import neo.state.*;

/**
 * <p>
 * Controls the hydraulic radius of the wetted channel.
 * </p>
 * <p>
 * Calculations based culverts with circular cross-sections.
 * </p>
 * 
 * @author robert.payn
 */
public class RadiusHyd extends AbstractUpdaterDbl {

    /**
     * Effective angle for calculation of wetted geometry (radians)
     */
    private HStateDbl angleGeom = null;
    /**
     * Depth of water in culvert [Length]
     */
    private HStateDbl depth = null;
    /**
     * Diameter of the circular culvert [Length]
     */
    private HStateDbl diameter = null;
    /**
     * Switch defining whether culvert is full (1 if full, 0 if not full)
     */
    private HStateInt isFull = null;

    /**
     * Calculate the hydraulic radius of the wetted channel
     * 
     * @return hydraulic radius [Length]
     */
    @Override
    public double computeValue()
    {

        if (isFull.v == 1)
        {
            // Culvert is full
            return 0.25 * diameter.v;
        }
        else
        {
            // Culvert is not full
            if (depth.v < 0.0)
            {
                return 0.0;
            }
            else
            {
                return 0.25 * diameter.v * (1.0 - (Math.sin(angleGeom.v) / angleGeom.v));
            }
        }

    }

    /**
     * Calculate the hydraulic radius of the wetted channel
     * 
     * @return hydraulic radius [Length]
     */
    @Override
    public double initValue()
    {

        return computeValue();

    }

    /**
     * Define the state dependencies for calculation of hydraulic radius
     */
    @Override
    public void setDependencies()
    {

        angleGeom = (HStateDbl) getInitHState("ANGLEGEOM");
        depth = (HStateDbl) getInitHState("DEPTH");
        diameter = (HStateDbl) getInitHState("DIAMETER");
        isFull = (HStateInt) getInitHState("ISFULL");

    }

}
