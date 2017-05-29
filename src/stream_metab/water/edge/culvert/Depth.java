package stream_metab.water.edge.culvert;

import neo.motif.*;
import neo.state.*;

/**
 * Controls the average depth of water in the culvert
 * <p>
 * Average depth is defined as half the sum of the depth at the deeper end of
 * the culvert (though not to be greater than the culvert size), and the depth
 * at the shallower end of the culvert (though not to be less than the critical
 * depth).
 * </p>
 * 
 * @author robert.payn
 */
public class Depth extends AbstractUpdaterDbl {

    /**
     * Critical depth [Length]
     */
    private HStateDbl depthCrit = null;
    /**
     * Depth in "from" patch [Length]
     */
    private HStateDbl depthFrom = null;
    /**
     * Depth in "to" patch [Length]
     */
    private HStateDbl depthTo = null;
    /**
     * Diameter of the circular culvert [Length]
     */
    private HStateDbl diameter = null;
    /**
     * Switch defining whether culvert is full (1 if full, 0 if not full)
     */
    private HStateInt isFull = null;

    /**
     * <p>
     * Calculates the average depth of water in the culvert
     * </p>
     * <p>
     * Effective depth is defined as half the sum of the depth at the deeper end
     * of the culvert (though not to be greater than the culvert size), and the
     * depth at the shallower end of the culvert (though not to be less than the
     * critical depth).
     * </p>
     * 
     * @return effective depth of water [Length]
     */
    @Override
    protected double computeValue()
    {

        if (isFull.v == 1)
        {
            // Culvert is full
            return diameter.v;
        }
        else
        {
            // Culvert is not full
            if (depthFrom.v > depthTo.v)
            {
                // Depth on "from" side greater than depth on "to" side
                return (Math.min(depthFrom.v, diameter.v) + Math.max(depthTo.v, depthCrit.v)) / 2;
            }
            else
            {
                // Depth on "from" side less than depth on "to" side
                return (Math.max(depthFrom.v, depthCrit.v) + Math.min(depthTo.v, diameter.v)) / 2;
            }
        }

    }

    /**
     * Calculates the initial effective depth of water in the culvert
     * 
     * @return effective depth of water [Length]
     */
    @Override
    protected double initValue()
    {

        return computeValue();

    }

    /**
     * Defines the state dependencies for calculation of effective water depth
     * in the culvert
     */
    @Override
    protected void setDependencies()
    {

        depthCrit = (HStateDbl) getInitHState("DEPTHCRIT");
        depthFrom = (HStateDbl) getInitHState("DEPTHFROM");
        depthTo = (HStateDbl) getInitHState("DEPTHTO");
        diameter = (HStateDbl) getInitHState("DIAMETER");
        isFull = (HStateInt) getInitHState("ISFULL");

    }

}
