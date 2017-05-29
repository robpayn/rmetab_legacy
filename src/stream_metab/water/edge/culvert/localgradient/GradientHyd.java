package stream_metab.water.edge.culvert.localgradient;

import neo.motif.*;
import neo.state.*;

/**
 * Controls the hydraulic gradient along the culvert.
 * 
 * @author robert.payn
 */
public class GradientHyd extends AbstractUpdaterDbl {

    /**
     * Depth of water in culvert [Length]
     */
    private HStateDbl depth = null;
    /**
     * Depth in "from" patch [Length]
     */
    private HStateDbl depthFrom = null;
    /**
     * Depth in "to" patch [Length]
     */
    private HStateDbl depthTo = null;
    /**
     * Elevation of base of culvert on "from" side of edge
     */
    private HStateDbl elevFrom;
    /**
     * Elevation of base of culvert on "to" side of edge
     */
    private HStateDbl elevTo;
    /**
     * Length of culvert [Length]
     */
    private HStateDbl length = null;

    /**
     * Calculate the hydraulic gradient along the culvert
     * 
     * @return hydraulic gradient [Length<sub><small>&Delta;head</small></sub>
     *         Length
     *         <sub><small>culvert</small></sub><sup><small>-1</small></sup>]
     */
    @Override
    public double computeValue()
    {

        if (depth.v <= 0)
        {
            return 0;
        }
        else
        {
            return ((elevFrom.v + depthFrom.v) - (elevTo.v + depthTo.v)) / length.v;
        }

    }

    /**
     * Calculate the initial hydraulic gradient along the culvert
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

        depth = (HStateDbl) getInitHState("DEPTH");
        depthFrom = (HStateDbl) getInitHState("DEPTHFROM");
        depthTo = (HStateDbl) getInitHState("DEPTHTO");
        elevFrom = (HStateDbl) getInitHState("ELEVFROM");
        elevTo = (HStateDbl) getInitHState("ELEVTO");
        length = (HStateDbl) getInitHState("LENGTH");

    }

}
