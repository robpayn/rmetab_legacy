package stream_metab.water.edge.culvert;

import neo.holon.*;
import neo.motif.*;
import neo.state.*;

/**
 * Controls the hydraulic gradient between the patches on either side of the
 * culvert
 * 
 * @author robert.payn
 */
public class GradientHyd extends AbstractUpdaterDbl {

    /**
     * Head in "from" patch [Length]
     */
    private HStateDbl headFrom = null;
    /**
     * Head in "to" patch [Length]
     */
    private HStateDbl headTo = null;
    /**
     * Distance between patches [Length]
     */
    private HStateDbl length = null;

    /**
     * Calculate the hydraulic gradient between the patches connected by the
     * culvert
     * 
     * @return hydraulic gradient [Length<sub><small>&Delta;head</small></sub>
     *         Length<sub><small>edge</small></sub><sup><small>-1</small></sup>]
     */
    @Override
    public double computeValue()
    {

        return (headFrom.v - headTo.v) / length.v;

    }

    /**
     * Calculate the initial hydraulic gradient between the patches connected by
     * the culvert
     * 
     * @return hydraulic gradient [Length<sub><small>&Delta;head</small></sub>
     *         Length
     *         <sub><small>culvert</small></sub><sup><small>-1</small></sup>]
     */
    @Override
    public double initValue()
    {

        return computeValue();

    }

    /**
     * Define the state dependencies for calculation of hydraulic gradient.
     */
    public void setDependencies()
    {

        headFrom = (HStateDbl) getInitHState(((Edge) myHolon).getFrom(), "HEAD");
        headTo = (HStateDbl) getInitHState(((Edge) myHolon).getTo(), "HEAD");
        length = (HStateDbl) getInitHState("LINKLENGTH");

    }

}
