package stream_metab.water.edge.culvert;

import stream_metab.water.utils.DepthCalculator;
import stream_metab.water.utils.culvert.*;
import neo.holon.*;
import neo.motif.*;
import neo.state.*;

/**
 * Controls the depth of water in the culvert at the "to" side of the edge.
 * 
 * @author robert.payn
 */
public class DepthTo extends AbstractUpdaterDbl {

    /**
     * Calculator for the depth (will be implemented with high side or low side
     * algorithms)
     */
    private DepthCalculator depthCalc;
    /**
     * Depth of water in patch on "to" side of culvert
     */
    private HStateDbl depthToPatch;
    /**
     * Elevation of base of culvert on "from" side of edge
     */
    private HStateDbl elevFrom;
    /**
     * Elevation of base of culvert on "to" side of edge
     */
    private HStateDbl elevTo;
    /**
     * Hydraulic head in patch on "to" side of culvert
     */
    private HStateDbl headToPatch;

    /**
     * Calculate the depth on the "to" side of the culvert
     * 
     * @return depth of water [Length]
     */
    @Override
    protected double computeValue()
    {

        return depthCalc.calcDepth();

    }

    /**
     * Calculate the initial depth of water on the "to" side of culvert.
     * <p>
     * Creates and assigns the appropriate depth calculator object, based on
     * whether the "to" side of the culvert has a higher or lower elevation than
     * the "from" side.
     * </p>
     */
    @Override
    protected double initValue()
    {

        if (elevTo.v > elevFrom.v)
        {
            // To side has higher elevation
            depthCalc = new DepthCalcHigh(elevTo.v, headToPatch, depthToPatch);
        }
        else
        {
            // To side has lower elevation
            depthCalc = new DepthCalcLow(elevTo.v, headToPatch, depthToPatch);
        }
        return computeValue();

    }

    /**
     * Defines the state dependencies for calculating depth at the "to" end of
     * the culvert
     */
    @Override
    protected void setDependencies()
    {

        depthToPatch = (HStateDbl) getInitHState(((Edge) myHolon).getTo(), "DEPTH");
        elevFrom = (HStateDbl) getInitHState("ELEVFROM");
        elevTo = (HStateDbl) getInitHState("ELEVTO");
        headToPatch = (HStateDbl) getInitHState(((Edge) myHolon).getTo(), "HEAD");

    }

}
