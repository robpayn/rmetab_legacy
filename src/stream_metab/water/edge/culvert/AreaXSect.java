package stream_metab.water.edge.culvert;

import neo.motif.*;
import neo.state.*;
import neo.util.*;

/**
 * <p>
 * Controls the cross-sectional area of water in the culvert.
 * </p>
 * <p>
 * Calculations based on the depth-area relationship of a culvert with circular
 * cross-section.
 * <p>
 * <p>
 * This updated calls the simple calculator <i>AreaXSectPrev</i>, so anything
 * dependent on the stateval associated with <i>AreaXSectPrev</i> should be
 * declared dependent on this updater.
 * </p>
 * 
 * @author robert.payn
 * @see AreaXSectPrev
 */
public class AreaXSect extends AbstractUpdaterDbl {

    /**
     * Effective angle for calculation of wetted geometry (radians)
     */
    private HStateDbl angleGeom = null;
    /**
     * Calculator for cross-sectional area from the previous time step
     */
    private SimpleCalculator areaXSectPrevCalc;
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
     * Hydraulic radius of the water in the culvert [Length]
     */
    private HStateDbl radiusHyd = null;

    /**
     * Calculate the cross-sectional area of water in the culvert
     * 
     * @return cross-sectional area [Length<sup><small>2</small></sup>]
     */
    @Override
    public double computeValue()
    {

        if (isFull.v == 1)
        {
            // Culvert is full
            setValue(0.7854 * diameter.v * diameter.v);
        }
        else
        {
            // Culvert is not full
            if (depth.v < 0.0)
            {
                setValue(0.0);
            }
            else
            {
                setValue(radiusHyd.v * 0.5 * angleGeom.v * diameter.v);
            }
        }
        areaXSectPrevCalc.calculate();
        return getValue();

    }

    /**
     * Calculate the initial cross-sectional area of water in the culvert
     * 
     * @return cross-sectional area [Length<sup><small>2</small></sup>]
     */
    @Override
    public double initValue()
    {

        double returnValue = computeValue();
        areaXSectPrevCalc.initialize();
        return returnValue;

    }

    /**
     * Define the state dependencies for calculation of cross-sectional area
     */
    public void setDependencies()
    {

        try
        {
            areaXSectPrevCalc = (SimpleCalculator) (myHolon.getHState("AREAXSECTPREV").getPrimaryModifier());
            angleGeom = (HStateDbl) getInitHState("ANGLEGEOM");
            depth = (HStateDbl) getInitHState("DEPTH");
            diameter = (HStateDbl) getInitHState("DIAMETER");
            isFull = (HStateInt) getInitHState("ISFULL");
            radiusHyd = (HStateDbl) getInitHState("RADIUSHYD");
        }
        catch (HStateNotFoundException e)
        {
            Logger.logError(e.getMessage());
        }

    }

}
