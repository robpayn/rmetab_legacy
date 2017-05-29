package stream_metab.water.edge.culvert;

import neo.motif.*;
import neo.state.*;
import neo.util.*;

/**
 * <p>
 * Controls the velocity of water along the culvert
 * </p>
 * <p>
 * This is a simple calculator that is called when the flow of water is
 * calculated
 * </p>
 * 
 * @author robert.payn
 * @see Water
 */
public class Velocity extends AbstractSimpleCalculatorDbl {

    /**
     * Maximum velocity allowed by calculation [Length
     * Time<sup><small>-1</small></sup>]
     */
    private static final double VEL_MAX = 5.0;

    /**
     * Cross-sectional area of water in the culvert
     * [Length<sup><small>2</small></sup>]
     */
    private HStateDbl areaXSect;
    /**
     * Cross-sectional area of water in the culvert from the previous time step
     * [Length<sup><small>2</small></sup>]
     */
    private HStateDbl areaXSectPrev;
    /**
     * Volumetric flow of water through the culvert
     * [Length<sup><small>3</small></sup> Time<sup><small>-1</small></sup>]
     */
    private HStateDbl water;

    /**
     * <p>
     * Calculates the velocity of water in the culvert.
     * </p>
     * <p>
     * Calculation is based on flow and the average cross-sectional area over
     * the current and previous time steps
     * </p>
     * 
     * @return velocity of water [Length Time<sup><small>-1</small></sup>]
     */
    public double computeValue()
    {

        if (areaXSect.v <= 0)
        {
            return 0.0;
        }
        else
        {
            double v = water.v * (2 / (areaXSect.v + areaXSectPrev.v));
            if (Math.abs(v) > VEL_MAX)
            {
                initError("Velocity > MaxVelocity stated in water.edge.culvert.Utility");
                v = VEL_MAX * (v / Math.abs(v));
            }
            return v;
        }

    }

    /**
     * <p>
     * Calculates the initial velocity of water in the culvert.
     * </p>
     * 
     * @return velocity of water [Length Time<sup><small>-1</small></sup>]
     */
    public double initValue()
    {

        return (isNil()) ? 0.0 : getValue();

    }

    /**
     * Acquires references needed for calculation of water velocity in the
     * culvert
     */
    public void setDependencies()
    {

        try
        {
            // Dependencies are not set because this is a simple calculator
            areaXSect = (HStateDbl) myHolon.getHState("AREAXSECT");
            areaXSectPrev = (HStateDbl) myHolon.getHState("AREAXSECTPREV");
            water = (HStateDbl) myHolon.getHState("WATER");
        }
        catch (HStateNotFoundException e)
        {
            Logger.logError(e.getMessage());
        }

    }

}
