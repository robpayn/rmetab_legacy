package stream_metab.water.edge.culvert;

import neo.motif.*;
import neo.state.HStateDbl;

/**
 * <p>
 * Controls whether or not the culvert is considered full
 * </p>
 * <ul>
 * <li>1 if full</li>
 * <li>0 if not full</li>
 * </ul>
 * 
 * @author robert.payn
 */
public class IsFull extends AbstractUpdaterInt {

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
     * Calculates whether or not the channel is full
     * 
     * @return 1 if full, 0 if not full
     */
    @Override
    protected long computeValue()
    {

        if (depthFrom.v > diameter.v && depthTo.v > diameter.v)
        {
            // Culvert is full
            return 1;
        }
        else
        {
            // Culvert is not full
            return 0;
        }

    }

    /**
     * Calculates whether or not the channel is initially full
     * 
     * @return 1 if full, 0 if not full
     */
    @Override
    protected long initValue()
    {

        return computeValue();

    }

    /**
     * Defines the state dependencies for determination if the culvert is full
     */
    @Override
    protected void setDependencies()
    {

        depthFrom = (HStateDbl) getInitHState("DEPTHFROM");
        depthTo = (HStateDbl) getInitHState("DEPTHTO");
        diameter = (HStateDbl) getInitHState("DIAMETER");

    }

}
