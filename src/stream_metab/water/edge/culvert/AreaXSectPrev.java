package stream_metab.water.edge.culvert;

import neo.motif.*;
import neo.state.*;
import neo.util.*;

/**
 * Controls the cross-sectional area of water in the culvert from the previous
 * time step.
 * <p>
 * This simple calculator is called by <i>AreaXSect</i>, so updaters that are
 * dependent on statevals associated with this simple calculator should be
 * defined as dependent on <i>AreaXSect</i>
 * 
 * @author robert.payn
 */
public class AreaXSectPrev extends AbstractSimpleCalculatorDbl {

    /**
     * Current cross-sectional area [Length<sup><small>2</small></sup>]
     */
    private HStateDbl areaXSect;
    /**
     * Previous cross-sectional area (stored from last time step)
     * [Length<sup><small>2</small></sup>]
     */
    private double areaXSectPrev;

    /**
     * Calculate the cross-sectional area by retrieving value stored from
     * previous time step
     * 
     * @return [Length<sup><small>2</small></sup>]
     */
    @Override
    protected double computeValue()
    {

        double returnVal = areaXSectPrev;
        areaXSectPrev = areaXSect.v;
        return returnVal;

    }

    /**
     * Calculate the initial previous cross-sectional area by using the same
     * value as the initial cross-sectional area.
     */
    @Override
    protected double initValue()
    {

        return computeValue();

    }

    /**
     * Define the state dependencies for calculation of cross-sectional area of
     * water in the culvert
     */
    @Override
    protected void setDependencies()
    {

        try
        {
            areaXSect = (HStateDbl) myHolon.getHState("AREAXSECT");
        }
        catch (HStateNotFoundException e)
        {
            Logger.logError(e.getMessage());
        }

    }

}
